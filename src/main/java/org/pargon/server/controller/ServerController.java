package org.pargon.server.controller;

import org.pargon.server.dto.SettingsDto;
import org.pargon.server.repository.SettingsRepository;
import org.pargon.server.util.SettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server")
public class ServerController {

  private final SettingsRepository settingsRepository;

  @Autowired
  public ServerController(SettingsRepository settingsRepository) {
    this.settingsRepository = settingsRepository;
  }

  @GetMapping("/settings")
  public SettingsDto settings() {
    return SettingsMapper.toDto(settingsRepository.findAll());
  }

  @PatchMapping("/settings")
  public void updateSettings(@RequestBody SettingsDto settingsDto) {
    SettingsMapper
      .toEntities(settingsDto)
      .forEach(
        setting -> {
          settingsRepository.save(setting);
        }
      );
  }
}
