package com.forex.autotest.utility;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

/**
 * Provides the FIX codecs to MINA.
 */
public class FIXProtocolCodecFactory extends DemuxingProtocolCodecFactory {
    public static final String FILTER_NAME = "FIXCodec";

    public FIXProtocolCodecFactory() {
        addMessageDecoder(FIXMessageDecoder.class);
        addMessageEncoder(FIXMessageEncoder.getMessageTypes(), FIXMessageEncoder.class);
    }
}