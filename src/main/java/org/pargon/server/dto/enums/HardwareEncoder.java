package org.pargon.server.dto.enums;

import lombok.Getter;

@Getter
public enum HardwareEncoder {
  NONE("NONE"),
  NVENC("NVENC"),
  QSV("QSV"),
  VIDEO_TOOLBOX("VIDEO_TOOLBOX");

  private final String value;

  private HardwareEncoder(String value) {
    this.value = value;
  }
}
