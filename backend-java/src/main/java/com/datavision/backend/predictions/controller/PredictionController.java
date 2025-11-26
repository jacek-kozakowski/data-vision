package com.datavision.backend.predictions.controller;

import com.datavision.backend.predictions.service.IPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/predictions")
public class PredictionController {
    private final IPredictionService predictionService;
}
