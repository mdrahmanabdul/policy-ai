package org.policyai.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;

@Service
public class LangchainChunkingServiceImpl {
    private static final int CHUNK_SIZE = 500;
    private static final int CHUNK_OVERLAP = 100;
    
    public List<String> chunk(String text) {
        Document document = Document.from(text);
        DocumentSplitter splitter = DocumentSplitters.recursive(
                CHUNK_SIZE,
                CHUNK_OVERLAP
        );
        
        return splitter.split(document)
                .stream()
                .map(chunk -> chunk.text())
                .toList();
    }
}