package com.datavision.backend.client;

import com.datavision.backend.common.exceptions.MLClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class MLClient {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://ml-service:8000";


    public String postForAnalysis(String url, Object requestBody) {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL + url, request, String.class);
            return response.getBody();
        }catch(RestClientException e){
            log.error("Failed to communicate with ML Service: {}", e.getMessage());
            throw new MLClientException("Failed to communicate with ML Service");
        }
    }

}
