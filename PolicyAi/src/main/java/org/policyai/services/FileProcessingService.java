package org.policyai.services;

import java.io.IOException;

import org.policyai.dtos.FileUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileProcessingService {

	public FileUploadResponseDTO storeFile(MultipartFile file) throws IOException;
	public String getFileExtension(String fileName);
	public String cleanText(String text);
	public boolean isValidFileExtension(String fileName);
}
