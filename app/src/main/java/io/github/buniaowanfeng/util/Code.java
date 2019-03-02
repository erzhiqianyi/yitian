package io.github.buniaowanfeng.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by caofeng on 16-9-8.
 */
public class Code {
    private static final Charset charset = Charset.forName("UTF-8");
    public static byte[] encode(String origin,String scretKey){
        byte[] originByte;
        try {
            originByte = origin.getBytes("utf-8");

            byte[] keyBytes = scretKey.getBytes(charset);
            for(int i=0,size=originByte.length;i<size;i++){
                for(byte keyByte:keyBytes){
                    originByte[i] = (byte) (originByte[i]^keyByte);
                }
            }
            return originByte;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return  null;
        }

    }

    public static String decode(byte[] origin,String scretKey){
        byte[] keyBytes = scretKey.getBytes();
        byte[] dee = origin;
        for(int i=0,size=origin.length;i<size;i++){
            for(byte keyBytes0:keyBytes){
                origin[i] = (byte) (dee[i]^keyBytes0);
            }
        }
        return new String(origin);
    }
}
