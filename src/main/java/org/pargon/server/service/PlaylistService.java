package org.pargon.server.service;

import java.util.Locale;
import org.pargon.server.model.Media;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {

  private static final String PLAYLIST_TEMPLATE =
    "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
    "<MPD xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    "  xmlns=\"urn:mpeg:dash:schema:mpd:2011\"\n" +
    "  xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
    "  xsi:schemaLocation=\"urn:mpeg:DASH:schema:MPD:2011 http://standards.iso.org/ittf/PubliclyAvailableStandards/MPEG-DASH_schema_files/DASH-MPD.xsd\"\n" +
    "  profiles=\"urn:mpeg:dash:profile:isoff-live:2011\"\n" +
    "  type=\"static\"\n" +
    "  mediaPresentationDuration=\"PT%fS\"\n" +
    "  maxSegmentDuration=\"PT%fS\"\n" +
    "  minBufferTime=\"PT%fS\">\n" +
    "  <Period start=\"PT0.0S\">\n" +
    "    <AdaptationSet startWithSAP=\"1\" segmentAlignment=\"true\">\n" +
    "      <Representation mimeType=\"video/mp4\" bandwidth=\"8000000\" width=\"3840\" height=\"1600\" sar=\"1:1\">\n" +
    "        <SegmentTemplate startNumber=\"0\" duration=\"%d\" media=\"" +
    "/media/%s.mp4?" +
    "segment=$Number$&amp;" +
    "segmentDuration=%f&amp;" +
    "audioStreamIndex=%d&amp;" +
    "videoStreamIndex=%d&amp;" +
    "subtitleStreamIndex=%d&amp;" +
    "encodeHevc=%b\"/>\n" +
    "      </Representation>\n" +
    "    </AdaptationSet>\n" +
    "  </Period>\n" +
    "</MPD>\n";

  private final Double PLAYLIST_SEGMENT_DURATION = 3.0;

  public String generatePlaylist(
    Media media,
    Integer audioStreamIndex,
    Integer videoStreamIndex,
    Integer subtitleStreamIndex,
    Boolean encodeHvec
  ) {
    Double duration = media.getDuration();

    return String.format(
      Locale.US,
      PLAYLIST_TEMPLATE,
      duration,
      PLAYLIST_SEGMENT_DURATION,
      PLAYLIST_SEGMENT_DURATION * 3,
      // TODO: Just make this int
      PLAYLIST_SEGMENT_DURATION.longValue(),
      media.getId(),
      PLAYLIST_SEGMENT_DURATION,
      audioStreamIndex,
      videoStreamIndex,
      subtitleStreamIndex,
      encodeHvec
    );
  }
}
