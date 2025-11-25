package com.datavision.backend.common.exceptions;

public class DatasetNotFoundException extends RuntimeException {
  public DatasetNotFoundException(String message) {
    super(message);
  }
}
