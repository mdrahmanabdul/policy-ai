package org.policyai.controllers;

import org.policyai.constants.FileConstants;
import org.policyai.dtos.FileUploadResponseDTO;
import org.policyai.services.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	
	
	@PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
		try {
			
			//Restricting the user to upload empty files
			if(file.isEmpty()) {
				return ResponseEntity.badRequest()
						.body("Please select a file to upload !");
			}
			
			//files with >10MB size are not allowed into our directory
			if(file.getSize()>FileConstants.MAX_FILE_SIZE) {
				return ResponseEntity.badRequest()
						.body("File size exceeds limit of 10MB");
			}
			
			String originalFileName=file.getOriginalFilename();
			
			//If file is not named or if it is not a valid extenstion, then we are not saving the uploaded file
			if(originalFileName==null||!fileProcessingService.isValidFileExtension(originalFileName)) {
				return ResponseEntity.badRequest()
						.body("Invalid file types. Allowed types : "+FileConstants.ALLOWED_EXTENSIONS);
			}
			
			FileUploadResponseDTO response=fileProcessingService.storeFile(file);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file "+e.getMessage());
		}
	}
	
	
}
