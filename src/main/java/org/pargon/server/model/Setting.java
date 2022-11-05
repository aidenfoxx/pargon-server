package org.pargon.server.model;

import org.pargon.server.model.enums.SettingKey;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
