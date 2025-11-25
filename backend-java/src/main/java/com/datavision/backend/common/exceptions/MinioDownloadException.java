package com.datavision.backend.common.exceptions;

public class MinioDownloadException extends RuntimeException {
  public MinioDownloadException(String message) {
    super(message);
  }
}
