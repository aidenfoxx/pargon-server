package org.pargon.server.service;

import java.util.UUID;
import org.pargon.server.exception.MediaNotFoundException;
import org.pargon.server.model.Media;
import org.pargon.server.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {

  private static final String PLAYLIST_TEMPLATE =
    "#EXTM3U\n" +
    "#EXT-X-VERSION:3\n" +
    "#EXT-X-TARGETDURATION:%d\n" +
    "#EXT-X-MEDIA-SEQUENCE:0\n" +
    "%s" +
    "#EXT-X-ENDLIST";

  private static final String PLAYLIST_RECORD_TEMPLATE =
    "#EXTINF:%d\n" +
    "/transcode/%s.ts?" +
    "segment=%i&" +
    "segmentDuration=%f&" +
    "audioStreamIndex=%i&" +
    "videoStreamIndex=%i&" +
    "subtitleStreamIndex=%i&" +
    "subtitleFileIndex=%i&" +
    "encodeHevc=%b\n";

  private final Double PLAYLIST_SEGMENT_LENGTH = 3.0;

  private final MediaRepository mediaRepository;

  @Autowired
  public PlaylistService(MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
  }

  public String generatePlaylist(
    UUID mediaId,
    Integer audioStreamIndex,
    Integer videoStreamIndex,
    Integer subtitleStreamIndex,
    Integer subtitleFileIndex,
    Boolean encodeHvec
  ) {
    Media media = mediaRepository.findById(mediaId).orElse(null);

    if (media == null) {
      throw new MediaNotFoundException();
    }

    Double duration = media.getDuration();
    Integer segmentCount = (int) Math.ceil(duration / PLAYLIST_SEGMENT_LENGTH);
    String playlistSegments = "";

    for (int i = 0; i < segmentCount; i++) {
      playlistSegments +=
        String.format(
          PLAYLIST_RECORD_TEMPLATE,
          PLAYLIST_SEGMENT_LENGTH,
          i,
          PLAYLIST_SEGMENT_LENGTH,
          audioStreamIndex,
          videoStreamIndex,
          subtitleStreamIndex,
          subtitleFileIndex,
          encodeHvec
        );
    }

    return String.format(PLAYLIST_TEMPLATE, duration, playlistSegments);
  }
}
