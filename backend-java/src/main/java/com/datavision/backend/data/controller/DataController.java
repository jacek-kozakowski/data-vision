package com.datavision.backend.data.controller;

import com.datavision.backend.auth.AuthUtils;
import com.datavision.backend.common.dto.response.ApiResponse;
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

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadMultipartFile(@RequestParam Long projectId, @RequestPart MultipartFile file){
        dataService.uploadData(projectId, file, AuthUtils.getUser());
        return ResponseEntity.ok(new ApiResponse("Data uploaded successfully"));
    }
    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeData(@RequestParam Long projectId, @RequestParam(required = false, defaultValue = "false") boolean useScaled){
        String result = dataService.analyze_data(projectId, AuthUtils.getUser(), useScaled);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/correlation")
    public ResponseEntity<String> correlationData(@RequestParam Long projectId, @RequestParam String target, @RequestParam(required = false, defaultValue = "false") boolean useScaled){
        String result = dataService.correlation_data(projectId, target, AuthUtils.getUser(), useScaled);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/plot")
    public ResponseEntity<byte[]> plotData(@RequestParam Long projectId, @RequestParam(required = false, defaultValue = "scatter") String plotType,@RequestParam String column1, @RequestParam String column2,@RequestParam(required = false, defaultValue = "false") boolean useScaled){
        byte[] result = dataService.plot_data(projectId, plotType, column1, column2, AuthUtils.getUser(), useScaled);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(result);
    }
    @PostMapping("/clean")
    public ResponseEntity<String> cleanScaleData(@RequestParam Long projectId, @RequestParam(required = false, defaultValue = "false") boolean fillNa, @RequestParam(required = false, defaultValue = "mean") String fillMethod, @RequestParam(required = false, defaultValue = "false") boolean scale){
        String result = dataService.clean_scale_data(projectId, fillNa, fillMethod, scale, AuthUtils.getUser());
        return ResponseEntity.ok(result);
    }

}
