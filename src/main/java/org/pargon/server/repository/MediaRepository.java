package org.pargon.server.repository;

import java.util.List;
import java.util.UUID;
import org.pargon.server.entity.Media;
import org.springframework.data.repository.CrudRepository;

public interface MediaRepository extends CrudRepository<Media, UUID> {
  List<Media> findAll();
}
