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
import org.pargon.server.entity.Media;
import org.pargon.server.entity.Setting;
import org.pargon.server.entity.SettingKey;
import org.pargon.server.exception.MediaServiceException;
import org.pargon.server.model.FrameData;
import org.pargon.server.model.MediaInfo;
import org.pargon.server.model.SideData;
import org.pargon.server.repository.MediaRepository;
import org.pargon.server.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

  private static final String FFPROBE_COMMAND =
    "%s -hide_banner -loglevel warning -select_streams v -print_format json" +
    " -read_intervals \"%%+#1\" -show_entries" +
    " \"frame=color_space,color_primaries,color_transfer,side_data_list\"" +
    " -i %s";

  private static final String HDR_PARAMS_FORMAT =
    "hdr-opt=1:colorprim=%s:transfer=%s:colormatrix=%s" +
    ":master-display=G(%s,%s)B(%s,%s)R(%s,%s)WP(%s,%s)L(%s,%s):max-cll=%s,%s";

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
        if (foundMediaPaths.contains(media.getPath())) {
          if (updateCurrentMedia) {
            Media nextMedia = parseMediaFile(media.getPath());
            nextMedia.setId(media.getId());
            parsedMedia.add(nextMedia);
          }
        } else {
          staleMedia.add(media);
        }
      });

    // Parse found media
    foundMediaPaths.forEach(path -> {
      if (currentMedia.get(path) == null) {
        parsedMedia.add(parseMediaFile(path));
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

  private Media parseMediaFile(String path) {
    MediaInfo mediaInfo;

    try {
      mediaInfo =
        objectMapper.readValue(
          Runtime
            .getRuntime()
            .exec(String.format(FFPROBE_COMMAND, ffprobePath, path))
            .getInputStream(),
          MediaInfo.class
        );
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return Media
      .builder()
      .path(path)
      .hdr10Params(generateHdr10Params(mediaInfo))
      .build();
  }

  private String generateHdr10Params(MediaInfo mediaInfo) {
    FrameData frameData = mediaInfo.getFrames().get(0);

    if (frameData == null) {
      return null;
    }

    SideData masteringData = frameData
      .getSideData()
      .stream()
      .filter(sideData ->
        sideData.getSideDataType() == "Mastering display metadata"
      )
      .findFirst()
      .orElse(null);
    SideData lightLevelData = frameData
      .getSideData()
      .stream()
      .filter(sideData ->
        sideData.getSideDataType() == "Content light level metadata"
      )
      .findFirst()
      .orElse(null);

    if (masteringData == null || lightLevelData == null) {
      return null;
    }

    return String.format(
      HDR_PARAMS_FORMAT,
      frameData.getColorPrimaries(),
      frameData.getColorTransfer(),
      frameData.getColorSpace(),
      masteringData.getGreenX(),
      masteringData.getGreenY(),
      masteringData.getBlueX(),
      masteringData.getBlueY(),
      masteringData.getRedX(),
      masteringData.getRedY(),
      masteringData.getWhitePointX(),
      masteringData.getWhitePointY(),
      masteringData.getMinLuminance(),
      masteringData.getMaxLuminance(),
      lightLevelData.getMaxContent(),
      lightLevelData.getMaxAverage()
    );
  }
}
