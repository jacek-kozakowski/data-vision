package com.datavision.backend.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class MLClient {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://ml-service:8000";


    public String postForAnalysis(String url, Object requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL + url, request, String.class);
        return response.getBody();
    }

    public byte[] postForImage(String url, Object requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<byte[]> response = restTemplate.postForEntity(BASE_URL + url, request, byte[].class);
        return response.getBody();
    }



    public String get(String url, Map<String, String> params){
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + url, String.class, params);
        return response.getBody();
    }
}
