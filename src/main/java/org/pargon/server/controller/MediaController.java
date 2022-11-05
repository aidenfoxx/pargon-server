package org.pargon.server.controller;

import java.util.List;
import java.util.UUID;
import org.pargon.server.dto.MediaDto;
import org.pargon.server.repository.MediaRepository;
import org.pargon.server.service.PlaylistService;
import org.pargon.server.util.MediaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media")
public class MediaController {

  private final MediaRepository mediaRepository;

  private final PlaylistService playlistService;

  @Autowired
  public MediaController(
    MediaRepository mediaRepository,
    PlaylistService playlistService
  ) {
    this.mediaRepository = mediaRepository;
    this.playlistService = playlistService;
  }

  @GetMapping
  private List<MediaDto> getMedia() {
    return mediaRepository.findAll().stream().map(MediaMapper::toDto).toList();
  }

  @GetMapping(value = "/playlist/{mediaId}.m3u8", produces = "application/x-mpegURL")
  private String getMediaPlaylist(
    @PathVariable("mediaId") UUID mediaId,
    @RequestParam(required = false, defaultValue = "0") Integer audioStreamIndex,
    @RequestParam(required = false, defaultValue = "0") Integer videoStreamIndex,
    @RequestParam(required = false, defaultValue = "-1") Integer subtitleStreamIndex,
    @RequestParam(required = false, defaultValue = "-1") Integer subtitleFileIndex,
    @RequestParam(required = false, defaultValue = "false") Boolean encodeHevc
  ) {
    return playlistService.generatePlaylist(
      mediaId,
      audioStreamIndex,
      videoStreamIndex,
      subtitleStreamIndex,
      subtitleFileIndex,
      encodeHevc
    );
  }
}
