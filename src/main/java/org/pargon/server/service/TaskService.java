package org.pargon.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskService {

  private final MediaService mediaService;

  private final TranscodeService transcodeService;

  @Autowired
  public TaskService(
    MediaService mediaService,
    TranscodeService transcodeService
  ) {
    this.mediaService = mediaService;
    this.transcodeService = transcodeService;
  }

  @Scheduled(fixedDelayString = "${pargon.process.scan-media-delay:300000}")
  private void scanMedia() {
    log.info("Scanning media for updates");
    mediaService.scanMedia();
  }

  @Scheduled(
    fixedDelayString = "${pargon.process.clear-transcodes-delay:1800000}"
  )
  private void clearTranscodes() {
    log.info("Clearing transcode directory");
    transcodeService.clearTranscodes();
  }
}
