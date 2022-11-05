package org.pargon.server.model.enums;

import lombok.Getter;

@Getter
public enum CodecType {
  AUDIO("AUDIO"),
  VIDEO("VIDEO"),
  SUBTITLE("SUBTITLE");

  private final String type;

  private CodecType(String type) {
    this.type = type;
  }
}
