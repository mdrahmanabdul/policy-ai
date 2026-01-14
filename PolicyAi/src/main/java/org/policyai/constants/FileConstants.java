package org.policyai.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileConstants {

	private FileConstants() {
        
    }

	//We will be accessing these extensions only for RAG
    public static final List<String> ALLOWED_EXTENSIONS = Collections.unmodifiableList(Arrays.asList("pdf", "docx", "doc", "txt"));

    //We don't want to deal with files whose size is more than 10MB
    public static final long MAX_FILE_SIZE = 10L * 1024 * 1024; 
}
