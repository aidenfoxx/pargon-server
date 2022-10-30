package org.pargon.server.repository;

import java.util.List;
import org.pargon.server.entity.Setting;
import org.pargon.server.entity.SettingKey;
import org.springframework.data.repository.CrudRepository;

// TODO: Use ListCrudRepository with spring 3.0
// https://spring.io/blog/2022/02/22/announcing-listcrudrepository-friends-for-spring-data-3-0
public interface SettingsRepository
  extends CrudRepository<Setting, SettingKey> {
  List<Setting> findAll();
}
