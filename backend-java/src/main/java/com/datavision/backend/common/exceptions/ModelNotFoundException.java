package com.datavision.backend.common.exceptions;

public class ModelNotFoundException extends RuntimeException {
  public ModelNotFoundException(String message) {
    super(message);
  }
}
