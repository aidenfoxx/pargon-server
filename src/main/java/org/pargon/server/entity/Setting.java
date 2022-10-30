package org.pargon.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setting {

  @Id
  @Column(unique = true, nullable = false)
  @Enumerated(EnumType.STRING)
  private SettingKey key;

  @Column
  private String value;
}
