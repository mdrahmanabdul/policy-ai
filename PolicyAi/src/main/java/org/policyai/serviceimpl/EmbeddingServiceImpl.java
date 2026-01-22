package org.policyai.serviceimpl;

import org.policyai.services.EmbeddingService;
import org.springframework.stereotype.Service;

import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final OllamaEmbeddingModel embeddingModel;

    public EmbeddingServiceImpl() {
        this.embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text")
                .build();
    }

    @Override
    public float[] embed(String text) {
        return embeddingModel.embed(text).content().vector();
    }
}
