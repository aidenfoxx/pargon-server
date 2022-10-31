package org.pargon.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.pargon.server.entity.HdrMeta;
import org.pargon.server.entity.Media;
import org.pargon.server.entity.Setting;
import org.pargon.server.entity.SettingKey;
import org.pargon.server.exception.MediaServiceException;
import org.pargon.server.model.FrameData;
import org.pargon.server.model.MediaProbe;
import org.pargon.server.model.SideData;
import org.pargon.server.repository.MediaRepository;
import org.pargon.server.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MediaService {

  private static final String FFPROBE_COMMAND =
    "%s -hide_banner -loglevel quiet -print_format json -select_streams v:0" +
    " -read_intervals \"%%+#1\" -show_entries frame \"%s\"";

  private final MediaRepository mediaRepository;

  private final SettingsRepository settingsRepository;

  private final ObjectMapper objectMapper;

  private final List<String> mediaExtensions;

  private final String ffprobePath;

  @Autowired
  public MediaService(
    MediaRepository mediaRepository,
    SettingsRepository settingsRepository,
    ObjectMapper objectMapper,
    @Value("${pargon.media-extensions}") List<String> mediaExtensions,
    @Value("${pargon.ffprobe-path}") String ffprobePath
  ) {
    this.mediaRepository = mediaRepository;
    this.settingsRepository = settingsRepository;
    this.objectMapper = objectMapper;
    this.mediaExtensions = mediaExtensions;
    this.ffprobePath = ffprobePath;
  }

  public void scanMedia() {
    scanMedia(false);
  }

  public void scanMedia(Boolean updateCurrentMedia) {
    Setting mediaPathSetting = settingsRepository
      .findById(SettingKey.MEDIA_PATH)
      .orElse(null);

    if (mediaPathSetting == null) {
      throw new MediaServiceException("Media path not defined");
    }

    List<String> foundMediaPaths;

    try {
      foundMediaPaths =
        Files
          .walk(Paths.get(mediaPathSetting.getValue()))
          .map(Path::toString)
          .filter(this::filterByExtension)
          .toList();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    Map<String, Media> currentMedia = mediaRepository
      .findAll()
      .stream()
      .collect(Collectors.toMap(Media::getPath, Function.identity()));
    List<Media> parsedMedia = new ArrayList<>();
    List<Media> staleMedia = new ArrayList<>();

    // Update exising media
    currentMedia
      .values()
      .forEach(media -> {
        if (!foundMediaPaths.contains(media.getPath())) {
          staleMedia.add(media);
          return;
        }

        if (updateCurrentMedia) {
          try {
            Media nextMedia = parseMediaFile(media.getPath());
            nextMedia.setId(media.getId());
            parsedMedia.add(nextMedia);
          } catch (IOException e) {
            log.error(
              String.format("Failed to parse file `%s`", media.getPath()),
              e
            );
          }
        }
      });

    // Parse found media
    foundMediaPaths.forEach(path -> {
      if (currentMedia.get(path) == null) {
        try {
          parsedMedia.add(parseMediaFile(path));
        } catch (IOException e) {
          log.error(String.format("Failed to parse file `%s`", path), e);
        }
      }
    });

    mediaRepository.saveAll(parsedMedia);
    mediaRepository.deleteAll(staleMedia);
  }

  private boolean filterByExtension(String path) {
    return mediaExtensions
      .stream()
      .anyMatch(extension ->
        path.toString().endsWith(String.format(".%s", extension))
      );
  }

  private Media parseMediaFile(String path) throws IOException {
    MediaProbe mediaProbe = objectMapper.readValue(
      Runtime
        .getRuntime()
        .exec(String.format(FFPROBE_COMMAND, ffprobePath, path))
        .getInputStream(),
      MediaProbe.class
    );

    return Media
      .builder()
      .path(path)
      .hdrMeta(generateHdrMeta(mediaProbe))
      .build();
  }

  private HdrMeta generateHdrMeta(MediaProbe mediaProbe) {
    FrameData frameData = mediaProbe.getFrames().get(0);

    if (frameData == null) {
      return null;
    }

    SideData masteringData = frameData
      .getSideData()
      .stream()
      .filter(sideData ->
        sideData.getSideDataType().equals("Mastering display metadata")
      )
      .findFirst()
      .orElse(null);
    SideData lightLevelData = frameData
      .getSideData()
      .stream()
      .filter(sideData ->
        sideData.getSideDataType().equals("Content light level metadata")
      )
      .findFirst()
      .orElse(null);

    if (masteringData == null || lightLevelData == null) {
      return null;
    }

    return HdrMeta
      .builder()
      .colorSpace(frameData.getColorSpace())
      .colorPrimaries(frameData.getColorPrimaries())
      .colorTransfer(frameData.getColorTransfer())
      .redX(masteringData.getRedX())
      .redY(masteringData.getRedY())
      .greenX(masteringData.getGreenX())
      .greenY(masteringData.getGreenY())
      .blueX(masteringData.getBlueX())
      .blueY(masteringData.getBlueY())
      .whitePointX(masteringData.getWhitePointX())
      .whitePointY(masteringData.getWhitePointY())
      .minLuminance(masteringData.getMinLuminance())
      .maxLuminance(masteringData.getMaxLuminance())
      .maxContent(lightLevelData.getMaxContent())
      .maxAverage(lightLevelData.getMaxAverage())
      .build();
  }
}
