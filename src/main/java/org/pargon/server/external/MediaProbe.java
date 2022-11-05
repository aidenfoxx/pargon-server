package org.pargon.server.external;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MediaProbe {

  private Format format;

  private List<Frame> frames;

  private List<Stream> streams;
}
