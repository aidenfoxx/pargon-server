package org.pargon.server.service;

import org.pargon.server.exception.InvalidConfigException;
import org.pargon.server.model.Setting;
import org.pargon.server.model.enums.SettingKey;
import org.pargon.server.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranscodeService {

  private static final String HDR_PARAMS_FORMAT =
    "hdr-opt=1:colorprim=%s:transfer=%s:colormatrix=%s" +
    ":master-display=G(%s,%s)B(%s,%s)R(%s,%s)WP(%s,%s)L(%s,%s):max-cll=%s,%s";

    private final SettingsRepository settingsRepository;
  
    @Autowired
    public TranscodeService(SettingsRepository settingsRepository) {
      this.settingsRepository = settingsRepository;
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
