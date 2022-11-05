package org.pargon.server.model;

import org.pargon.server.model.enums.CodecType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media_stream")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaStream {
  
  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "media_id", nullable = false)
  private Media media;

  @OneToOne(mappedBy = "stream", cascade = CascadeType.ALL)
  private MediaStreamHdr hdr;

  // NOTE: Added quotes for keyword "index"
  @Column(name = "\"index\"", nullable = false)
  private Integer index;

  @Column(name = "codec_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private CodecType codecType;

  @Column(name = "codec_name", nullable = false)
  private String codecName;

  @Column(name = "channel_layout")
  private String channelLayout;
}
