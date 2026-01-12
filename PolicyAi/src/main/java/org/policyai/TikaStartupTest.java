package org.policyai;
import java.io.InputStream;

import org.policyai.serviceimpl.TextExtractorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class TikaStartupTest implements CommandLineRunner{

	@Autowired
	private TextExtractorServiceImpl extractor;
	
	@Override
    public void run(String... args) throws Exception {
		ClassPathResource resource = new ClassPathResource("MohammedRahman_FullStackDeveloperResume.pdf");


        try (InputStream inputStream = resource.getInputStream()) {
            String text = extractor.extractText(inputStream);
            System.out.println("========== EXTRACTED TEXT ==========");
            System.out.println(text);
        }
    }
	
}
