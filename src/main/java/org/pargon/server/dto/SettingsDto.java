package org.pargon.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.pargon.server.dto.settings.EncoderPreset;
import org.pargon.server.dto.settings.HardwareDecoder;
import org.pargon.server.dto.settings.HardwareEncoder;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDto {

  public String mediaPath;
  public String transcodePath;
  public HardwareDecoder hardwareDecoder;
  public HardwareEncoder hardwareEncoder;
  public EncoderPreset encoderPreset;
  public Long bitrate;
  public Boolean encodeHvec;
}
