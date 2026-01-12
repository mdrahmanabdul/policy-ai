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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.util.StringUtils;


@Service
public class FileProcessingServiceImpl implements FileProcessingService{

	@Autowired
	private FileMetaDataRepo fileMetadataRepository;
	
	
	@Value("${file.upload.dir:uploads}")
    private String uploadDir;
	
	
	public FileUploadResponseDTO storeFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Creating a uniqe and clean file name
        String fileId = UUID.randomUUID().toString();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String storedFileName = fileId + "." + fileExtension;

        // Copy file to upload directory
        Path targetLocation = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Save metadata to database
        FileMetaData metadata = new FileMetaData();
        metadata.setFileId(fileId);
        metadata.setFileName(originalFilename);
        metadata.setFileType(fileExtension);
        metadata.setFileSize(file.getSize());
        metadata.setFilePath(targetLocation.toString());
        metadata.setUploadedAt(LocalDateTime.now());
        metadata.setStatus("UPLOADED");

        fileMetadataRepository.save(metadata);

        // Return response
        return new FileUploadResponseDTO(
            fileId,
            originalFilename,
            fileExtension,
            file.getSize(),
            targetLocation.toString(),
            metadata.getUploadedAt(),
            "UPLOADED"
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
		
		

}
