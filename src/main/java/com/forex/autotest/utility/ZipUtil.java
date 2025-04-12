package com.forex.autotest.utility;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipUtil
{
    public static byte[] compress(byte[] data) {
        byte[] output;
        Deflater compress = new Deflater(Deflater.BEST_COMPRESSION, true);

        compress.reset();
        compress.setInput(data);
        compress.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compress.finished()) {
                int i = compress.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try
            {
                bos.close();
            }catch (Exception e){}
        }
        compress.end();
        return output;
    }

    public static String uncompress(byte[] temp)
    {
        try {
            Inflater decompresser = new Inflater(true);
            decompresser.setInput(temp, 0, temp.length);
            StringBuilder sb = new StringBuilder();
            byte[] result = new byte[1024];
            while (!decompresser.finished()) {
                int resultLength = decompresser.inflate(result);
                sb.append(new String(result, 0, resultLength, "UTF-8"));
            }
            decompresser.end();
            return sb.toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



}
