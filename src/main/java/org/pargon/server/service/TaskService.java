package org.pargon.server.service;

import lombok.extern.slf4j.Slf4j;
import org.pargon.server.entity.Setting;
import org.pargon.server.entity.SettingKey;
import org.pargon.server.exception.ProcessServiceException;
import org.pargon.server.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskService {

  private final MediaService mediaService;

  private final SettingsRepository settingsRepository;

  @Autowired
  public TaskService(
    MediaService mediaService,
    SettingsRepository settingsRepository
  ) {
    this.mediaService = mediaService;
    this.settingsRepository = settingsRepository;
  }

  @Scheduled(fixedDelayString = "${pargon.process.scan-media-delay:300000}")
  private void scanMedia() {
    log.info("Scanning media for updates");
    this.mediaService.scanMedia();
  }

  @Scheduled(
    fixedDelayString = "${pargon.process.clear-transcodes-delay:1800000}"
  )
  private void clearTranscodes() {
    log.info("Clearing transcode directory");

    Setting transcodePathSetting = settingsRepository
      .findById(SettingKey.MEDIA_PATH)
      .orElse(null);

    if (transcodePathSetting == null) {
      throw new ProcessServiceException("Transcode path not defined");
    }

    // TODO: Clear path
  }
}
