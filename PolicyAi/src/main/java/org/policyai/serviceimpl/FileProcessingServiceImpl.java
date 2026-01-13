package org.policyai.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.policyai.dtos.FileUploadResponseDTO;
import org.policyai.models.FileMetaData;
import org.policyai.repos.FileMetaDataRepo;
import org.policyai.services.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.util.StringUtils;


@Service
public class FileProcessingServiceImpl implements FileProcessingService{

	private final FileMetaDataRepo fileMetadataRepository;
	private final TextExtractorServiceImpl textExtractionService;
	private final LangchainChunkingServiceImpl langchainChunkingService;
	private final EmbeddingServiceImpl embeddingService;
	private final ChromaVectorStoreServiceImpl chromaServiceImpl;
	private final Logger logger = LoggerFactory.getLogger(FileProcessingServiceImpl.class);
	
	public FileProcessingServiceImpl(FileMetaDataRepo fileMetadataRepository,TextExtractorServiceImpl textExtractionService,
			LangchainChunkingServiceImpl langchainChunkingService,EmbeddingServiceImpl embeddingService,
			ChromaVectorStoreServiceImpl chromaServiceImpl) {
		this.fileMetadataRepository=fileMetadataRepository;
		this.textExtractionService=textExtractionService;
		this.langchainChunkingService=langchainChunkingService;
		this.embeddingService=embeddingService;
		this.chromaServiceImpl=chromaServiceImpl;
	}
	
	
	@Value("${file.upload.dir:uploads}")
    private String uploadDir;
	
	
	public FileUploadResponseDTO storeFile(MultipartFile file) throws IOException {

		//We are getting the path where our uploadDir is
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
	    
	    //if it was an image pdf where the text can't be scanned then we will return this exception
	    if (extractedText == null || extractedText.trim().isEmpty()) {
	        throw new RuntimeException("No readable text found in document. Possibly a scanned PDF.");
	    }
	    //We are making chunks of the text we have extracted using langchain
	    List<String> chunks = langchainChunkingService.chunk(extractedText);
	    List<String> ids = new ArrayList<>();
	    List<float[]> embeddings = new ArrayList<>();
	    List<String> documents = new ArrayList<>();
	    List<Map<String, Object>> metadatas = new ArrayList<>();

	    for (String chunk : chunks) {
	        String id = UUID.randomUUID().toString();
	        float[] vector = embeddingService.embed(chunk);

	        ids.add(id);
	        embeddings.add(vector);
	        documents.add(chunk);
	        metadatas.add(Map.of(
	                "documentId", fileId,
	                "source", originalFilename
	        ));
	    }

	    // Store in Chroma
	    chromaServiceImpl.storeEmbeddings(
	            "policy-documents",
	            ids,
	            embeddings,
	            documents,
	            metadatas
	    );
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

	// 
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
