package org.policyai.serviceimpl;

import java.util.List;
import java.util.Map;

import org.policyai.services.ChromaVectorStoreService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChromaVectorStoreServiceImpl implements ChromaVectorStoreService{

	private RestTemplate restTemplate = new RestTemplate();
	private final String CHROMA_URL = "http://localhost:8000";

	    public void storeEmbeddings(String collectionName,
	                                List<String> ids,
	                                List<float[]> embeddings,
	                                List<String> documents,
	                                List<Map<String, Object>> metadatas) {

	        String url = CHROMA_URL + "/api/v1/collections/" + collectionName + "/add";

	        Map<String, Object> payload = Map.of(
	                "ids", ids,
	                "embeddings", embeddings,
	                "documents", documents,
	                "metadatas", metadatas
	        );

	        restTemplate.postForObject(url, payload, String.class);
	    }
}
