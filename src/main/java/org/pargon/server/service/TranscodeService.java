package org.pargon.server.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import org.pargon.server.exception.InvalidConfigException;
import org.pargon.server.model.Media;
import org.pargon.server.model.Setting;
import org.pargon.server.model.enums.SettingKey;
import org.pargon.server.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// https://gist.github.com/samson-sham/7cb3a404a7aaaff62ec0ebbe08fb84e1
@Service
public class TranscodeService {

  private static final String FFMPEG_TRANSCODE_EXEC =
    "%s -hide_banner -loglevel quiet -y -vsync 2 -ss %f -t %f -i \"%s\"" +
    " -preset veryfast -map_chapters -1 -map_metadata -1 -map 0:v:0" +
    " -codec:v libx264 -b:v 8000k -f mp4" +
    " -movflags empty_moov+default_base_moof+skip_sidx+skip_trailer+frag_custom" +
    " -fragment_index %d pipe:1";

  private static final String FFMPEG_HDR_PARAMS =
    "hdr-opt=1:colorprim=%s:transfer=%s:colormatrix=%s" +
    ":master-display=G(%s,%s)B(%s,%s)R(%s,%s)WP(%s,%s)L(%s,%s):max-cll=%s,%s";

  private final SettingsRepository settingsRepository;

  private final String ffmpegPath;

  @Autowired
  public TranscodeService(
    SettingsRepository settingsRepository,
    @Value("${pargon.ffmpeg-path}") String ffmpegPath
  ) {
    this.settingsRepository = settingsRepository;
    this.ffmpegPath = ffmpegPath;
  }

  public InputStream transcodeMedia(
    Media media,
    Integer segment,
    Double segmentDuration,
    Integer audioStreamIndex,
    Integer videoStreamIndex,
    Integer subtitleStreamIndex,
    Boolean encodeHvec
  ) throws IOException, InterruptedException {
    Setting transcodePathSetting = settingsRepository
      .findById(SettingKey.TRANSCODE_PATH)
      .orElse(null);

    if (transcodePathSetting == null) {
      throw new InvalidConfigException("Transcode path not defined");
    }

    Object[] transcodeParams = new Object[] {
      media.getId(),
      segment,
      segmentDuration,
      audioStreamIndex,
      videoStreamIndex,
      subtitleStreamIndex,
      encodeHvec,
    };
    File outputFile = new File(
      transcodePathSetting.getValue(),
      String.format("%s.mp4", Math.abs(Arrays.hashCode(transcodeParams)))
    );

    if (outputFile.exists()) {
      return new FileInputStream(outputFile);
    }

    double startTime = segment * segmentDuration;

    var test = String.format(
      Locale.US,
      FFMPEG_TRANSCODE_EXEC,
      ffmpegPath,
      startTime,
      segmentDuration,
      media.getPath(),
      segment + 1
      //outputFile.getAbsolutePath()
    );

    return Runtime
      .getRuntime()
      .exec(
        String.format(
          Locale.US,
          FFMPEG_TRANSCODE_EXEC,
          ffmpegPath,
          startTime,
          segmentDuration,
          media.getPath(),
          segment + 1
          //outputFile.getAbsolutePath()
        )
      ).getInputStream();
  }

  public void clearTranscodes() {
    Setting transcodePathSetting = settingsRepository
      .findById(SettingKey.MEDIA_PATH)
      .orElse(null);

    if (transcodePathSetting == null) {
      throw new InvalidConfigException("Transcode path not defined");
    }

    // TODO: Clear path
  }
}
