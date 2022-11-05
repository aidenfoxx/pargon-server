package org.pargon.server.dto.enums;

import lombok.Getter;

@Getter
public enum HardwareDecoder {
  NONE("NONE"),
  NVENC("NVENC"),
  QSV("QSV"),
  VIDEO_TOOLBOX("VIDEO_TOOLBOX"),
  V4L2("V4L2");

  private final String value;

  private HardwareDecoder(String value) {
    this.value = value;
  }
}
