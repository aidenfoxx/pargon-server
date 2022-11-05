package org.pargon.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media_stream_hdr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaStreamHdr {

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "media_stream_id", nullable = false)
  private MediaStream stream;

  @Column(name = "color_space", nullable = false)
  private String colorSpace;

  @Column(name = "color_primaries", nullable = false)
  private String colorPrimaries;

  @Column(name = "color_transfer", nullable = false)
  private String colorTransfer;

  @Column(name = "red_x", nullable = false)
  private String redX;

  @Column(name = "red_y", nullable = false)
  private String redY;

  @Column(name = "green_x", nullable = false)
  private String greenX;

  @Column(name = "green_y", nullable = false)
  private String greenY;

  @Column(name = "blue_x", nullable = false)
  private String blueX;

  @Column(name = "blue_y", nullable = false)
  private String blueY;

  @Column(name = "white_point_x", nullable = false)
  private String whitePointX;

  @Column(name = "white_point_y", nullable = false)
  private String whitePointY;

  @Column(name = "min_luminance", nullable = false)
  private String minLuminance;

  @Column(name = "max_luminance", nullable = false)
  private String maxLuminance;

  @Column(name = "max_content", nullable = false)
  private Integer maxContent;

  @Column(name = "max_average", nullable = false)
  private Integer maxAverage;
}
