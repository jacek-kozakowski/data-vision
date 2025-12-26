package com.datavision.backend.common.exceptions;

public class PipelineNotFoundException extends RuntimeException {
  public PipelineNotFoundException(String message) {
    super(message);
  }
}
