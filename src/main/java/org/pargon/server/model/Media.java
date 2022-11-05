package org.pargon.server.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {

  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String path;

  @Column(nullable = false)
  private Double duration;

  @OneToMany(mappedBy = "media", cascade = CascadeType.ALL)
  private List<MediaStream> streams;
}
