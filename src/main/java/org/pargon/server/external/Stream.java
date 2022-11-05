package org.pargon.server.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Stream {
  
  private Integer index;

  @JsonProperty("codec_type")
  private String codecType;

  @JsonProperty("codec_long_name")
  private String codecName;

  @JsonProperty("channel_layout")
  private String channelLayout;
}
