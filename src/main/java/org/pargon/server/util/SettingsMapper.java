package org.pargon.server.util;

import java.util.List;
import java.util.function.Function;

import org.pargon.server.dto.SettingsDto;
import org.pargon.server.dto.SettingsDto.SettingsDtoBuilder;
import org.pargon.server.dto.enums.EncoderPreset;
import org.pargon.server.dto.enums.HardwareDecoder;
import org.pargon.server.dto.enums.HardwareEncoder;
import org.pargon.server.model.Setting;
import org.pargon.server.model.enums.SettingKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SettingsMapper {

  // TODO: Can this parse a Setting?
  private static <R> R parseSettingValue(String value, Function<String, R> parser) {
    try {
      return parser.apply(value);
    } catch (IllegalArgumentException e) {
      log.error("Failed to parse setting", e);
      return null;
    }
  };

  public static SettingsDto toDto(List<Setting> settings) {
    SettingsDtoBuilder settingsDtoBuilder = SettingsDto.builder();

    settings.forEach(setting -> {
      switch (setting.getKey()) {
        case MEDIA_PATH:
         settingsDtoBuilder.mediaPath(setting.getValue());
          break;
      
        case TRANSCODE_PATH:
          settingsDtoBuilder.mediaPath(setting.getValue());
          break;

        case HARDWARE_DECODER:
          settingsDtoBuilder.hardwareDecoder(
            parseSettingValue(setting.getValue(), HardwareDecoder::valueOf)
          );
          break;

        case HARDWARE_ENCODER:
          settingsDtoBuilder.hardwareEncoder(
            parseSettingValue(setting.getValue(), HardwareEncoder::valueOf)
          );
          break;

        case ENCODER_PRESET:
          settingsDtoBuilder.encoderPreset(
            parseSettingValue(setting.getValue(), EncoderPreset::valueOf)
          );
          break;

        case BITRATE:
          settingsDtoBuilder.bitrate(
            parseSettingValue(setting.getValue(), Long::valueOf)
          );
          break;
      }
    });

    return settingsDtoBuilder.build();
  }

  public static List<Setting> toEntities(SettingsDto settingsDto) {
    return List.of(
      Setting
        .builder()
        .key(SettingKey.MEDIA_PATH)
        .value(settingsDto.getMediaPath())
        .build(),
      Setting
        .builder()
        .key(SettingKey.TRANSCODE_PATH)
        .value(settingsDto.getTranscodePath())
        .build(),
      Setting
        .builder()
        .key(SettingKey.HARDWARE_DECODER)
        .value(settingsDto.getHardwareDecoder().toString())
        .build(),
      Setting
        .builder()
        .key(SettingKey.HARDWARE_ENCODER)
        .value(settingsDto.getHardwareEncoder().toString())
        .build(),
      Setting
        .builder()
        .key(SettingKey.ENCODER_PRESET)
        .value(settingsDto.getEncoderPreset().toString())
        .build(),
      Setting
        .builder()
        .key(SettingKey.BITRATE)
        .value(settingsDto.getBitrate().toString())
        .build()
    );
  }
}
