package org.pargon.server.model.enums;

import lombok.Getter;

@Getter
public enum SettingKey {
  MEDIA_PATH("MEDIA_PATH"),
  TRANSCODE_PATH("TRANSCODE_PATH"),
  HARDWARE_DECODER("HARDWARE_DECODER"),
  HARDWARE_ENCODER("HARDWARE_ENCODER"),
  ENCODER_PRESET("ENCODER_PRESET"),
  BITRATE("BITRATE");

  private final String key;

  private SettingKey(String key) {
    this.key = key;
  }
}
