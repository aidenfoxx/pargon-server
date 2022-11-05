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
import org.pargon.server.exception.InvalidConfigException;
import org.pargon.server.exception.InvalidMediaException;
import org.pargon.server.external.Frame;
import org.pargon.server.external.MediaProbe;
import org.pargon.server.external.SideData;
import org.pargon.server.external.Stream;
import org.pargon.server.model.Media;
import org.pargon.server.model.MediaStream;
import org.pargon.server.model.MediaStreamHdr;
import org.pargon.server.model.Setting;
import org.pargon.server.model.enums.CodecType;
import org.pargon.server.model.enums.SettingKey;
import org.pargon.server.repository.MediaRepository;
import org.pargon.server.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MediaService {

  private final List<String> MEDIA_EXTENSIONS = List.of(
    "mp4",
    "mkv",
    "mov",
    "wmv",
    "avi"
  );

  private static final String FFPROBE_STREAMS_EXEC =
    "%s -hide_banner -loglevel quiet -print_format json -show_streams " +
    " -show_format \"%s\"";

  private static final String FFPROBE_HDR_EXEC =
    "%s -hide_banner -loglevel quiet -print_format json -select_streams v:%d" +
    " -read_intervals \"%%+#1\" -show_frames \"%s\"";

  private final MediaRepository mediaRepository;

  private final SettingsRepository settingsRepository;

  private final ObjectMapper objectMapper;

  private final String ffprobePath;

  @Autowired
  public MediaService(
    MediaRepository mediaRepository,
    SettingsRepository settingsRepository,
    ObjectMapper objectMapper,
    @Value("${pargon.ffprobe-path}") String ffprobePath
  ) {
    this.mediaRepository = mediaRepository;
    this.settingsRepository = settingsRepository;
    this.objectMapper = objectMapper;
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
      throw new InvalidConfigException("Media path not defined");
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
    for (Media media : currentMedia.values()) {
      if (!foundMediaPaths.contains(media.getPath())) {
        staleMedia.add(media);
        return;
      }

      if (updateCurrentMedia) {
        try {
          Media updatedMedia = parseMedia(media.getPath());
          updatedMedia.setId(media.getId());
          parsedMedia.add(updatedMedia);
        } catch (IOException | InvalidMediaException e) {
          log.error(
            String.format("Failed to parse file `%s`", media.getPath()),
            e
          );
        }
      }
    }

    // Parse found media
    for (String path : foundMediaPaths) {
      if (currentMedia.get(path) == null) {
        try {
          parsedMedia.add(parseMedia(path));
        } catch (IOException | InvalidMediaException  e) {
          log.error(String.format("Failed to parse file `%s`", path), e);
        }
      }
    }

    mediaRepository.saveAll(parsedMedia);
    mediaRepository.deleteAll(staleMedia);
  }

  private boolean filterByExtension(String path) {
    return MEDIA_EXTENSIONS
      .stream()
      .anyMatch(extension ->
        path.toString().toLowerCase().endsWith(String.format(".%s", extension))
      );
  }

  private Media parseMedia(String path) throws IOException {
    MediaProbe mediaProbe = objectMapper.readValue(
      Runtime
        .getRuntime()
        .exec(String.format(FFPROBE_STREAMS_EXEC, ffprobePath, path))
        .getInputStream(),
      MediaProbe.class
    );

    if (
      mediaProbe.getStreams() == null ||
      mediaProbe.getFormat() == null ||
      mediaProbe.getFormat().getDuration() == null
    ) {
      throw new InvalidMediaException("Invalid metadata");
    }

    Media media = Media
      .builder()
      .path(path)
      .duration(mediaProbe.getFormat().getDuration())
      .streams(new ArrayList<>())
      .build();

    for (Stream stream : mediaProbe.getStreams()) {
      MediaStream mediaStream = parseMediaStream(path, stream);
      mediaStream.setMedia(media);
      media.getStreams().add(mediaStream);
    }

    return media;
  }

  private MediaStream parseMediaStream(String path, Stream stream)
    throws IOException {
    CodecType codecType = parseCodecType(stream.getCodecType());

    if (
      stream.getIndex() == null ||
      codecType == CodecType.AUDIO &&
      stream.getChannelLayout() == null
    ) {
      throw new InvalidMediaException("Invalid media stream");
    }

    MediaStream mediaStream = MediaStream
      .builder()
      .index(stream.getIndex())
      .codecType(codecType)
      .codecName(stream.getCodecName())
      .channelLayout(
        codecType == CodecType.AUDIO ? stream.getChannelLayout() : null
      )
      .build();

    if (codecType == CodecType.VIDEO) {
      MediaStreamHdr mediaStreamHdr = parseMediaStreamHdr(
        path,
        stream.getIndex()
      );
      mediaStreamHdr.setStream(mediaStream);
      mediaStream.setHdr(mediaStreamHdr);
    }

    return mediaStream;
  }

  private MediaStreamHdr parseMediaStreamHdr(String path, Integer streamIndex)
    throws IOException {
    MediaProbe mediaProbe = objectMapper.readValue(
      Runtime
        .getRuntime()
        .exec(String.format(FFPROBE_HDR_EXEC, ffprobePath, streamIndex, path))
        .getInputStream(),
      MediaProbe.class
    );

    if (
      mediaProbe.getFrames() == null ||
      mediaProbe.getFrames().size() == 0 ||
      mediaProbe.getFrames().get(0).getSideData() == null
    ) {
      throw new InvalidMediaException(
        String.format("Invalid frame data for stream `%d`", streamIndex)
      );
    }

    Frame frameData = mediaProbe.getFrames().get(0);
    SideData masteringData = null;
    SideData lightLevelData = null;

    for (SideData sideData : frameData.getSideData()) {
      switch (sideData.getSideDataType()) {
        case "Mastering display metadata":
          masteringData = sideData;
          break;
        case "Content light level metadata":
          lightLevelData = sideData;
          break;
      }
    }

    if (masteringData == null || lightLevelData == null) {
      return null;
    }

    return MediaStreamHdr
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

  private CodecType parseCodecType(String codecType) {
    switch (codecType) {
      case "audio":
        return CodecType.AUDIO;
      case "video":
        return CodecType.VIDEO;
      case "subtitle":
        return CodecType.SUBTITLE;
      default:
        throw new InvalidMediaException("Invalid media codec");
    }
  }
}
