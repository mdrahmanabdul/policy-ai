package org.policyai.services;

import java.util.List;
import java.util.Map;

public interface ChromaVectorStoreService {

	public void storeEmbeddings(String collectionName,
            List<String> ids,
            List<float[]> embeddings,
            List<String> documents,
            List<Map<String, Object>> metadatas);
	
	
}
