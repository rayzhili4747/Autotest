package com.forex.autotest.utility;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.quickfixj.CharsetSupport;
import quickfix.Message;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Encodes a Message object or message string as a byte array to be
 * transmitted on MINA connection.
 */
public class FIXMessageEncoder implements MessageEncoder<Object> {

    private static final Set<Class<?>> TYPES =
            new HashSet<>(Arrays.<Class<?>>asList(Message.class, String.class));
    private final String charsetEncoding;

    public FIXMessageEncoder() {
        charsetEncoding = CharsetSupport.getCharset();
    }

    public static Set<Class<?>> getMessageTypes() {
        return TYPES;
    }

    private byte[] toBytes(String str) throws ProtocolCodecException {
        try {
            return str.getBytes(charsetEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new ProtocolCodecException(e);
        }
    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
            throws ProtocolCodecException {
        // get message bytes
        byte[] bytes;
        if (message instanceof String) {
            bytes = toBytes((String) message);
        } else if (message instanceof Message) {
            bytes = toBytes(message.toString());
        } else {
            throw new ProtocolCodecException("Invalid FIX message object type: "
                    + message.getClass());
        }
        // write bytes to buffer and output it
        IoBuffer buffer = IoBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        out.write(buffer);
    }
}