package com.datavision.backend.data.service;

import com.datavision.backend.client.MLClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {
    private final MLClient mlClient;

    public String analyze_data(String url, MultipartFile file){
        return mlClient.postMultipart(url, file);
    }
    public String correlation_data(String target, MultipartFile file){

        return mlClient.postMultipart("/api/data/correlation?target="+target, file);
    }
    public byte[] plot_data(String plotType, String column1, String column2, MultipartFile file){
        return mlClient.postMultipartForImage("/api/data/plot?plot_type="+plotType+"&column1="+column1+"&column2="+column2, file);
    }
}
