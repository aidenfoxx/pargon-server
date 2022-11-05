package org.pargon.server.dto;

import org.pargon.server.dto.enums.EncoderPreset;
import org.pargon.server.dto.enums.HardwareDecoder;
import org.pargon.server.dto.enums.HardwareEncoder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SettingsDto {

  @NotEmpty
  private String mediaPath;

  @NotEmpty
  private String transcodePath;

  @NotNull
  private HardwareDecoder hardwareDecoder;

  @NotNull
  private HardwareEncoder hardwareEncoder;

  @NotNull
  private EncoderPreset encoderPreset;

  @NotNull
  private Long bitrate;

  @NotNull
  private Boolean encodeHvec;
}
