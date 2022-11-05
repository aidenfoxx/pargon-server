package org.pargon.server.repository;

import java.util.UUID;

import org.pargon.server.model.Media;
import org.springframework.data.repository.ListCrudRepository;

public interface MediaRepository extends ListCrudRepository<Media, UUID> {}
