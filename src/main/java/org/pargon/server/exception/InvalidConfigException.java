package org.pargon.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class InvalidConfigException extends RuntimeException {

  public InvalidConfigException(String message) {
    super(message);
  }

  public InvalidConfigException(String message, Exception e) {
    super(message, e);
  }
}
