package com.datavision.backend.data;

import com.datavision.backend.client.MLClient;
import com.datavision.backend.data.service.DataService;
import com.datavision.backend.minio.service.MinIOService;
import com.datavision.backend.user.model.User;
import com.datavision.backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataServiceTests {

    @Mock
    private MLClient mlClient;

    @Mock
    private UserService userService;

    @Mock
    private MinIOService minIOService;

    @InjectMocks
    private DataService dataService;

    User user;

    @BeforeEach
    protected void setUp() {
        user = new User();
        user.setUsername("test");
        user.setCurrentFile("test.csv");
    }

    @Test
    public void uploadData() {
        String fileName = "test.csv";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv", "text/csv", "test".getBytes());
        when(minIOService.uploadFile(any())).thenReturn(fileName);
        String uploadedFile = minIOService.uploadFile(file);
        assertNotNull(uploadedFile);

        verify(minIOService, times(1)).uploadFile(any());
    }

    @Test
    public void analyzeData() {
        String result = "test";
        Map<String, String> body = Map.of(
                "file_id", user.getCurrentFile()
        );
        when(mlClient.postForAnalysis(anyString(), anyMap())).thenReturn(result);
        String analysisResult = mlClient.postForAnalysis("/api/data/analyze", body);

        assertNotNull(analysisResult);
        verify(mlClient, times(1)).postForAnalysis(eq("/api/data/analyze"), anyMap());
    }

}
