package org.pargon.server.exception;

public class ProcessServiceException extends RuntimeException {

  public ProcessServiceException(String message) {
    super(message);
  }

  public ProcessServiceException(String message, Exception e) {
    super(message, e);
  }
}
