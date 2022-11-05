package org.pargon.server.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MediaDto {

  @NotEmpty
  private UUID id;

  @NotEmpty
  private String filename;

  @NotEmpty
  private String path;

  @NotNull
  private Double duration;
}
