package org.pargon.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TranscodeException extends RuntimeException {

  public TranscodeException(String message) {
    super(message);
  }
}
