package org.pargon.server.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {

  @Id
  @Column(unique = true)
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String path;

  @Column(name = "hdr10_params")
  private String hdr10Params;

  @Column(name = "hdr10_plus_data")
  private String hdr10PlusData;
}
