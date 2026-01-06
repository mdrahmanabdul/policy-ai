package org.policyai.dtos;

import java.time.LocalDateTime;

public class FileUploadResponseDTO {

	private String fileId;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String filePath;
    private LocalDateTime uploadedAt;
    private String status;
    
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}
	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public FileUploadResponseDTO(String fileId, String fileName, String fileType, long fileSize, String filePath,
			LocalDateTime uploadedAt, String status) {
		super();
		this.fileId = fileId;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.filePath = filePath;
		this.uploadedAt = uploadedAt;
		this.status = status;
	}
	
	public FileUploadResponseDTO() {
		super();
	}
    
    
}
