package com.datavision.backend.data.controller;

import com.datavision.backend.common.dto.response.ApiResponse;
import com.datavision.backend.data.service.DataService;
import com.datavision.backend.user.model.User;
import com.datavision.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadMultipartFile(@RequestBody MultipartFile file){
        boolean uploaded = dataService.uploadData(file, getUser());
        if (uploaded){
            return ResponseEntity.ok(new ApiResponse("File uploaded successfully"));
        }else{
            return ResponseEntity.badRequest().body(new ApiResponse("Failed to upload file"));
        }
    }
    @GetMapping("/analyze")
    public ResponseEntity<String> analyzeData(){
        String result = dataService.analyze_data( getUser());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/correlation")
    public ResponseEntity<String> correlationData(@RequestParam() String target){
        String result = dataService.correlation_data(target, getUser());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/plot")
    public ResponseEntity<byte[]> plotData(@RequestParam(required = false, defaultValue = "scatter") String plotType,@RequestParam String column1, @RequestParam String column2){
        byte[] result = dataService.plot_data(plotType, column1, column2, getUser());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(result);
    }

    private User getUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.getUserByUsername(username);
    }
}
