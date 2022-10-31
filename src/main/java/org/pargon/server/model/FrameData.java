package org.pargon.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class FrameData {

  @JsonProperty("color_space")
  private String colorSpace;

  @JsonProperty("color_primaries")
  private String colorPrimaries;

  @JsonProperty("color_transfer")
  private String colorTransfer;

  @JsonProperty("side_data_list")
  private List<SideData> sideData;
}
