package org.pargon.server.repository;

import java.util.List;

import org.pargon.server.model.Setting;
import org.pargon.server.model.enums.SettingKey;
import org.springframework.data.repository.ListCrudRepository;

public interface SettingsRepository
  extends ListCrudRepository<Setting, SettingKey> {
  List<Setting> findByKeyIn(SettingKey[] keys);
}
