package org.pargon.server.util;

import org.pargon.server.dto.MediaDto;
import org.pargon.server.model.Media;

public class MediaMapper {
  
  public static MediaDto toDto(Media media) {
    return MediaDto
      .builder()
      .id(media.getId())
      .filename(media.getPath().replaceAll("^.*[\\\\/]", ""))
      .path(media.getPath())
      .duration(media.getDuration())
      .build();
  }
}
