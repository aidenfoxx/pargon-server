package org.pargon.server.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.pargon.server.dto.SettingsDto;
import org.pargon.server.dto.settings.EncoderPreset;
import org.pargon.server.dto.settings.HardwareDecoder;
import org.pargon.server.dto.settings.HardwareEncoder;
import org.pargon.server.entity.Setting;
import org.pargon.server.entity.SettingKey;

public class SettingsMapper {

  public static SettingsDto toDto(List<Setting> settings) {
    Map<SettingKey, String> mappedSettings = settings
      .stream()
      .collect(Collectors.toMap(Setting::getKey, Setting::getValue));

    return SettingsDto
      .builder()
      .mediaPath(mappedSettings.get(SettingKey.MEDIA_PATH))
      .transcodePath(mappedSettings.get(SettingKey.TRANSCODE_PATH))
      .hardwareDecoder(
        HardwareDecoder.valueOf(mappedSettings.get(SettingKey.HARDWARE_DECODER))
      )
      .hardwareEncoder(
        HardwareEncoder.valueOf(mappedSettings.get(SettingKey.HARDWARE_ENCODER))
      )
      .encoderPreset(
        EncoderPreset.valueOf(mappedSettings.get(SettingKey.ENCODER_PRESET))
      )
      .bitrate(Long.valueOf(mappedSettings.get(SettingKey.BITRATE)))
      .build();
  }

  public static List<Setting> toEntities(SettingsDto settingsDto) {
    return List.of(
      Setting
        .builder()
        .key(SettingKey.MEDIA_PATH)
        .value(settingsDto.mediaPath)
        .build(),
      Setting
        .builder()
        .key(SettingKey.TRANSCODE_PATH)
        .value(settingsDto.transcodePath)
        .build(),
      Setting
        .builder()
        .key(SettingKey.HARDWARE_DECODER)
        .value(settingsDto.hardwareDecoder.toString())
        .build(),
      Setting
        .builder()
        .key(SettingKey.HARDWARE_ENCODER)
        .value(settingsDto.hardwareEncoder.toString())
        .build(),
      Setting
        .builder()
        .key(SettingKey.ENCODER_PRESET)
        .value(settingsDto.encoderPreset.toString())
        .build(),
      Setting
        .builder()
        .key(SettingKey.BITRATE)
        .value(settingsDto.bitrate.toString())
        .build()
    );
  }
}
