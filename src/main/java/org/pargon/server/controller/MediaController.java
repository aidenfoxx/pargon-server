package org.pargon.server.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.pargon.server.dto.MediaDto;
import org.pargon.server.exception.MediaNotFoundException;
import org.pargon.server.model.Media;
import org.pargon.server.repository.MediaRepository;
import org.pargon.server.service.PlaylistService;
import org.pargon.server.service.TranscodeService;
import org.pargon.server.util.MediaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/media")
public class MediaController {

  private final MediaRepository mediaRepository;

  private final PlaylistService playlistService;

  private final TranscodeService transcodeService;

  @Autowired
  public MediaController(
    MediaRepository mediaRepository,
    PlaylistService playlistService,
    TranscodeService transcodeService
  ) {
    this.mediaRepository = mediaRepository;
    this.playlistService = playlistService;
    this.transcodeService = transcodeService;
  }

  @GetMapping
  private List<MediaDto> getMedia() {
    return mediaRepository.findAll().stream().map(MediaMapper::toDto).toList();
  }

  // TODO: Create subtitle repository with all sub-files and accept subtitleId as a param
  @GetMapping(value = "/{mediaId}.mpd", produces = "application/dash+xml")
  private String getPlaylist(
    @PathVariable UUID mediaId,
    @RequestParam(
      required = false,
      defaultValue = "0"
    ) Integer audioStreamIndex,
    @RequestParam(
      required = false,
      defaultValue = "0"
    ) Integer videoStreamIndex,
    @RequestParam(
      required = false,
      defaultValue = "-1"
    ) Integer subtitleStreamIndex,
    @RequestParam(required = false, defaultValue = "false") Boolean encodeHevc
  ) {
    Media media = mediaRepository.findById(mediaId).orElse(null);

    if (media == null) {
      throw new MediaNotFoundException();
    }

    return playlistService.generatePlaylist(
      media,
      audioStreamIndex,
      videoStreamIndex,
      subtitleStreamIndex,
      encodeHevc
    );
  }

  @GetMapping(value = "/{mediaId}.mp4", produces = "video/mp4")
  private StreamingResponseBody getTranscode(
    @PathVariable UUID mediaId,
    @RequestParam Integer segment,
    @RequestParam Double segmentDuration,
    @RequestParam(
      required = false,
      defaultValue = "0"
    ) Integer audioStreamIndex,
    @RequestParam(
      required = false,
      defaultValue = "0"
    ) Integer videoStreamIndex,
    @RequestParam(
      required = false,
      defaultValue = "-1"
    ) Integer subtitleStreamIndex,
    @RequestParam(required = false, defaultValue = "false") Boolean encodeHevc
  ) throws IOException, InterruptedException {
    Media media = mediaRepository.findById(mediaId).orElse(null);

    if (media == null) {
      throw new MediaNotFoundException();
    }

    InputStream mediaStream = transcodeService.transcodeMedia(
      media,
      segment,
      segmentDuration,
      audioStreamIndex,
      videoStreamIndex,
      subtitleStreamIndex,
      encodeHevc
    );

    return outputStream -> {
      mediaStream.transferTo(outputStream);
    };
  }
}
