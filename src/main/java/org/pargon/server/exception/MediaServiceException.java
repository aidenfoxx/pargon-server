package org.pargon.server.exception;

public class MediaServiceException extends RuntimeException {

  public MediaServiceException(String message) {
    super(message);
  }

  public MediaServiceException(String message, Exception e) {
    super(message, e);
  }
}
