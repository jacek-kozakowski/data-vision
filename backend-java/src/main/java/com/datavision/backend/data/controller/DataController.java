package com.datavision.backend.data.controller;

import com.datavision.backend.auth.AuthUtilsService;
import com.datavision.backend.common.dto.data.requests.CleanDataRequest;
import com.datavision.backend.common.dto.data.requests.PlotDataRequest;
import com.datavision.backend.common.dto.response.ApiResponse;
import com.datavision.backend.data.service.DataService;
import jakarta.validation.Valid;
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
    private final AuthUtilsService authUtils;

    @PostMapping("/{projectId}/upload")
    public ResponseEntity<ApiResponse> uploadMultipartFile(@PathVariable Long projectId, @RequestPart MultipartFile file){
        dataService.uploadData(projectId, file, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(new ApiResponse("Data uploaded successfully"));
    }
    @GetMapping("/{projectId}/analyze")
    public ResponseEntity<Object> analyzeData(@PathVariable Long projectId, @RequestParam(required = false, defaultValue = "false") boolean useScaled){
        String result = dataService.analyzeData(projectId, authUtils.getAuthenticatedUser(), useScaled);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{projectId}/correlation")
    public ResponseEntity<Object> correlationData(@PathVariable Long projectId, @RequestParam String target, @RequestParam(required = false, defaultValue = "false") boolean useScaled){
        String result = dataService.correlationData(projectId, target, authUtils.getAuthenticatedUser(), useScaled);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/{projectId}/plot")
    public ResponseEntity<byte[]> plotData(@PathVariable Long projectId, @RequestBody @Valid PlotDataRequest request){
        byte[] result = dataService.plotData(projectId, request, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(result);
    }
    @PostMapping("/{projectId}/clean")
    public ResponseEntity<Object> cleanScaleData(@PathVariable Long projectId, @RequestBody  CleanDataRequest request){
        String result = dataService.cleanScaleData(projectId,request, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(result);
    }

}
