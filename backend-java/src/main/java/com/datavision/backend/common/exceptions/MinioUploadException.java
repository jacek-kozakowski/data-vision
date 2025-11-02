package com.datavision.backend.common.exceptions;

public class MinioUploadException extends RuntimeException {
  public MinioUploadException(String message) {
    super(message);
  }
}
