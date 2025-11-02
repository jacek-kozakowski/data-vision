package com.datavision.backend.data.controller;

import com.datavision.backend.data.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeData( @RequestBody MultipartFile file){
        String result = dataService.analyze_data("/api/data/analyze", file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/correlation")
    public ResponseEntity<String> correlationData(@RequestParam() String target, @RequestBody MultipartFile file){
        String result = dataService.correlation_data(target, file);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/plot")
    public ResponseEntity<byte[]> plotData(@RequestParam(required = false, defaultValue = "scatter") String plotType,@RequestParam String column1, @RequestParam String column2, @RequestBody MultipartFile file){
        byte[] result = dataService.plot_data(plotType, column1, column2, file);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(result);
    }
}
