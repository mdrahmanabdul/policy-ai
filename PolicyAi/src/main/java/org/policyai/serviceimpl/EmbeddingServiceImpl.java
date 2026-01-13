package org.policyai.serviceimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.langchain4j.model.openai.OpenAiEmbeddingModel;

@Service
public class EmbeddingServiceImpl {

    private final OpenAiEmbeddingModel embeddingModel;

    public EmbeddingServiceImpl(@Value("${openai.api.key}") String apiKey) {
        this.embeddingModel = OpenAiEmbeddingModel.withApiKey(apiKey);
    }

    public float[] embed(String text) {
        return embeddingModel.embed(text).content().vector();
    }
}
