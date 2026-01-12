package org.policyai.serviceimpl;

import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
public class TextExtractorServiceImpl {

	private final Tika tika = new Tika();
	
	public String extractText(InputStream inputStream) {
		try {
			return tika.parseToString(inputStream);
		} catch (Exception e) {
			throw new RuntimeException("Failed to extract text : "+e.getMessage());
		}
	}
}
