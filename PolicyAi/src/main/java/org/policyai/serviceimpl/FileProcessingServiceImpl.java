package org.policyai.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

import org.policyai.dtos.FileUploadResponseDTO;
import org.policyai.models.FileMetaData;
import org.policyai.repos.FileMetaDataRepo;
import org.policyai.services.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.util.StringUtils;


@Service
public class FileProcessingServiceImpl implements FileProcessingService{

	private final FileMetaDataRepo fileMetadataRepository;
	private final TextExtractorServiceImpl textExtractionService;
	private final Logger logger = LoggerFactory.getLogger(FileProcessingServiceImpl.class);
	
	public FileProcessingServiceImpl(FileMetaDataRepo fileMetadataRepository,TextExtractorServiceImpl textExtractionService) {
		this.fileMetadataRepository=fileMetadataRepository;
		this.textExtractionService=textExtractionService;
	}
	
	
	@Value("${file.upload.dir:uploads}")
    private String uploadDir;
	
	
	public FileUploadResponseDTO storeFile(MultipartFile file) throws IOException {

	    Path uploadPath = Paths.get(uploadDir);
	    if (!Files.exists(uploadPath)) {
	        Files.createDirectories(uploadPath);
	    }

	    String fileId = UUID.randomUUID().toString();
	    String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
	    String fileExtension = getFileExtension(originalFilename);
	    String storedFileName = fileId + "." + fileExtension;

	    Path targetLocation = uploadPath.resolve(storedFileName);
	    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

	    // Extract the text inside a document using Apache Tika
	    String extractedText;
	    try (var inputStream = Files.newInputStream(targetLocation)) {
	        String noisyText = textExtractionService.extractText(inputStream);
	    	extractedText = cleanText(noisyText);
	    }
	    
	    if (extractedText == null || extractedText.trim().isEmpty()) {
	        throw new RuntimeException("No readable text found in document. Possibly a scanned PDF.");
	    }

	    // Save metadata
	    FileMetaData metadata = new FileMetaData();
	    metadata.setFileId(fileId);
	    metadata.setFileName(originalFilename);
	    metadata.setFileType(fileExtension);
	    metadata.setFileSize(file.getSize());
	    metadata.setFilePath(targetLocation.toString());
	    metadata.setUploadedAt(LocalDateTime.now());
	    metadata.setStatus("PROCESSED");

	    fileMetadataRepository.save(metadata);

	    // 🔹 (Next phase) Chunk + Embed + Store in Vector DB
	    // chunkService.chunk(extractedText);
	    // embeddingService.embed(...);
	    logger.info("File : "+metadata.getFileName()+ " uploaded successfully");
	    return new FileUploadResponseDTO(
	        fileId,
	        originalFilename,
	        fileExtension,
	        file.getSize(),
	        targetLocation.toString(),
	        metadata.getUploadedAt(),
	        "PROCESSED"
	    );
	}

	
	//helper function
		//function1: I will be getting file extention of the uploaded file in this function
		private String getFileExtension(String fileName) {
			int lastIndexOfDot = fileName.lastIndexOf('.');
			if(lastIndexOfDot==-1) {
				return "";
			}
			return fileName.substring(lastIndexOfDot+1);
		}
		
		//function2: We will be cleaning all the noise from the extracted text
		private String cleanText(String text) {
		    text = text.replaceAll("", "-");
		    text = text.replaceAll("\\r?\\n+", "\n");
		    text = text.replaceAll("[ ]{2,}", " ");
		    return text.trim();
		}

}
