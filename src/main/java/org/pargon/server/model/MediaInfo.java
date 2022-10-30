package org.pargon.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class MediaInfo {

  @JsonProperty("frames")
  private List<FrameData> frames;
}
