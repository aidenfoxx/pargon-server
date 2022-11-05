package org.pargon.server.dto.enums;

import lombok.Getter;

@Getter
public enum EncoderPreset {
  ULTRAFAST("ULTRAFAST"),
  SUPERFAST("SUPERFAST"),
  VERYFAST("VERYFAST"),
  FASTER("FASTER"),
  FAST("FAST"),
  MEDIUM("MEDIUM"),
  SLOW("SLOW"),
  SLOWER("SLOWER"),
  VERYSLOW("VERYSLOW");

  private final String value;

  private EncoderPreset(String value) {
    this.value = value;
  }
}
