package com.datavision.backend.client;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
public class MLClient {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://ml-service:8000";

    public String postMultipart(String url, MultipartFile file){
        try{
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("file", new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL + url, request, String.class);
            return response.getBody();
        }catch (IOException e ){
            throw new RuntimeException("Failed to upload multipart file to ML service", e);
        }
    }
    public byte[] postMultipartForImage(String url, MultipartFile file){
        try{
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("file", new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
            ResponseEntity<byte[]> response = restTemplate.postForEntity(BASE_URL + url, request, byte[].class);
            return response.getBody();
        }catch (IOException e ){
            throw new RuntimeException("Failed to upload multipart file to ML service", e);
        }
    }

    public String get(String url, Map<String, String> params){
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + url, String.class, params);
        return response.getBody();
    }
}
