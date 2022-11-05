package org.pargon.server.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Frame {

  @JsonProperty("color_space")
  private String colorSpace;

  @JsonProperty("color_primaries")
  private String colorPrimaries;

  @JsonProperty("color_transfer")
  private String colorTransfer;

  @JsonProperty("side_data_list")
  private List<SideData> sideData;
}
