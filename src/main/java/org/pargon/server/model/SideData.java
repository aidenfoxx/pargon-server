package org.pargon.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SideData {

  @JsonProperty("side_data_type")
  private String sideDataType;

  @JsonProperty("red_x")
  private String redX;

  @JsonProperty("red_y")
  private String redY;

  @JsonProperty("green_x")
  private String greenX;

  @JsonProperty("green_y")
  private String greenY;

  @JsonProperty("blue_x")
  private String blueX;

  @JsonProperty("blue_y")
  private String blueY;

  @JsonProperty("white_point_x")
  private String whitePointX;

  @JsonProperty("white_point_y")
  private String whitePointY;

  @JsonProperty("min_luminance")
  private String minLuminance;

  @JsonProperty("max_luminance")
  private String maxLuminance;

  @JsonProperty("max_content")
  private Integer maxContent;

  @JsonProperty("max_average")
  private Integer maxAverage;
}
