package org.policyai.controllers;

import java.util.Arrays;
import java.util.List;

import org.policyai.dtos.FileUploadResponseDTO;
import org.policyai.services.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/policy-ai/files")
public class FileProcessingController {
	
	@Autowired
	private FileProcessingService fileProcessingService;
	
	//restrictions for the file
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(new String[] { "pdf", "docx", "doc", "txt" });
	private static final long MAX_FILE_SIZE = 10*1024*1024;
	
	@PostMapping(value="/upload",consumes = "multipart/form-data")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
		try {
			
			//Restricting the user to upload empty files
			if(file.isEmpty()) {
				return ResponseEntity.badRequest()
						.body("Please select a file to upload !");
			}
			
			//files with >10MB size are not allowed into our directory
			if(file.getSize()>MAX_FILE_SIZE) {
				return ResponseEntity.badRequest()
						.body("File size exceeds limit of 10MB");
			}
			
			String originalFileName=file.getOriginalFilename();
			
			//If file is not named or if it is not a valid extenstion, then we are not saving the uploaded file
			if(originalFileName==null||!isValidFileExtension(originalFileName)) {
				return ResponseEntity.badRequest()
						.body("Invalid file types. Allowed types : "+ALLOWED_EXTENSIONS);
			}
			
			FileUploadResponseDTO response=fileProcessingService.storeFile(file);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file"+e.getMessage());
		}
	}
	
	//helper functions
	//function1: I will be getting file extention of the uploaded file in this function
	private String getFileExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf('.');
		if(lastIndexOfDot==-1) {
			return "";
		}
		return fileName.substring(lastIndexOfDot+1);
	}
	
	//function2: I will check whether the file is of type allowed extension or not
	private boolean isValidFileExtension(String fileName) {
		String extension = getFileExtension(fileName);
		return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
	}
	
}
