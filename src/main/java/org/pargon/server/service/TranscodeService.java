package org.pargon.server.service;

import org.springframework.stereotype.Service;

@Service
public class TranscodeService {

  private static final String HDR_PARAMS_FORMAT =
    "hdr-opt=1:colorprim=%s:transfer=%s:colormatrix=%s" +
    ":master-display=G(%s,%s)B(%s,%s)R(%s,%s)WP(%s,%s)L(%s,%s):max-cll=%s,%s";
}
