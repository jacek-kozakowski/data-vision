package com.datavision.backend.data;

import com.datavision.backend.client.MLClient;
import com.datavision.backend.common.dto.data.*;
import com.datavision.backend.common.dto.data.requests.CleanDataRequest;
import com.datavision.backend.common.dto.data.requests.PlotDataRequest;
import com.datavision.backend.common.dto.project.ProjectDto;
import com.datavision.backend.data.service.DataService;
import com.datavision.backend.minio.service.MinIOService;
import com.datavision.backend.project.service.ProjectService;
import com.datavision.backend.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.datavision.backend.common.dto.data.CleanScaleDataDto;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataServiceTests {

    @Mock
    private MLClient mlClient;

    @Mock
    private MinIOService minIOService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private DataService dataService;

    private User user;

    private ProjectDto project;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test");

        project = new ProjectDto();
        String datasetName = "original_dataset";
        String datasetPath = "path/to/dataset.csv";
        Map<String, String> datasets = new HashMap<>();
        datasets.put(datasetName, datasetPath);
        project.setDatasets(datasets);
    }

    @Test
    public void uploadDataTest() {

        Long projectId = 100L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "some,data".getBytes());
        String expectedMinioPath = "uuid-test.csv";

        when(minIOService.uploadFile(file)).thenReturn(expectedMinioPath);

        dataService.uploadData(projectId, file, user);

        verify(minIOService).uploadFile(file);
        verify(projectService).addDatasetToProject(projectId, "original_dataset", expectedMinioPath, user);
    }

    @Test
    public void analyzeDataTest() {
        Long projectId = 100L;
        String expectedResponse = "response";

        when(projectService.getProjectDtoById(projectId, user)).thenReturn(project);
        when(mlClient.postForAnalysis(eq("/api/data/analyze"), any(AnalyzeDataDto.class))).thenReturn(expectedResponse);

        String response = dataService.analyzeData(projectId, user, false);

        verify(mlClient, times(1)).postForAnalysis(eq("/api/data/analyze"), any(AnalyzeDataDto.class));
        assertEquals(expectedResponse, response);
    }

    @Test
    public void correlationDataTests() {
        Long projectId = 100L;
        String expectedResponse = "response";
        String target = "target";

        when(projectService.getProjectDtoById(projectId, user)).thenReturn(project);
        when(mlClient.postForAnalysis(eq("/api/data/correlation"), any(CorrelationDataDto.class)))
                .thenReturn(expectedResponse);

        String response = dataService.correlationData(projectId, target, user, false);

        verify(mlClient, times(1)).postForAnalysis(eq("/api/data/correlation"), any(CorrelationDataDto.class));
        assertEquals(expectedResponse, response);
    }

    @Test
    public void plotDataTest() {
        Long projectId = 100L;
        Integer plotId = 1;
        String expectedResponse = "{\"plot_file_id\": \"plot-uuid\"}";
        byte[] expectedPlot = new byte[] { 1, 2, 3 };
        String plotType = "scatter";
        String column1 = "column1";
        String column2 = "column2";

        PlotDataRequest plotDataRequest = new PlotDataRequest();
        plotDataRequest.setPlotId(plotId);
        plotDataRequest.setPlotType(plotType);
        plotDataRequest.setColumn1(column1);
        plotDataRequest.setColumn2(column2);
        plotDataRequest.setUseScaled(false);

        when(projectService.getProjectDtoById(projectId, user)).thenReturn(project);
        when(mlClient.postForAnalysis(eq("/api/data/plot"), any(PlotDataDto.class))).thenReturn(expectedResponse);
        when(minIOService.downloadFile("plot-uuid")).thenReturn(expectedPlot);

        byte[] result = dataService.plotData(projectId, plotDataRequest,user);

        verify(mlClient, times(1)).postForAnalysis(eq("/api/data/plot"), any(PlotDataDto.class));
        verify(projectService, times(1)).addPlotToProject(projectId, plotId, "plot-uuid", user);
        verify(minIOService, times(1)).downloadFile("plot-uuid");
        assertEquals(expectedPlot, result);
    }

    @Test
    public void cleanScaleDataTest() {
        Long projectId = 100L;
        boolean fillNa = true;
        String fillMethod = "mean";
        boolean scale = true;

        CleanDataRequest body = new CleanDataRequest();
        body.setFillNa(fillNa);
        body.setFillMethod(fillMethod);
        body.setScale(scale);
        String expectedResponse = "{\"cleaned_file_id\": \"scaled-uuid\"}";

        when(projectService.getProjectDtoById(projectId, user)).thenReturn(project);
        when(mlClient.postForAnalysis(eq("/api/data/clean"), any(CleanScaleDataDto.class)))
                .thenReturn(expectedResponse);

        String result = dataService.cleanScaleData(projectId, body,  user);

        verify(mlClient, times(1)).postForAnalysis(eq("/api/data/clean"), any(CleanScaleDataDto.class));
        verify(projectService, times(1)).addDatasetToProject(projectId, "scaled_dataset", "scaled-uuid", user);
        assertEquals(expectedResponse, result);
    }
}
