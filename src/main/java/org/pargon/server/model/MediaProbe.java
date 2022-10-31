package org.pargon.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class MediaProbe {

  @JsonProperty("frames")
  private List<FrameData> frames;
}
