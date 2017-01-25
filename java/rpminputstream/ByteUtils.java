package randyc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Byte[] convenience utility methods
 */
public class ByteUtils {
    private static final Logger logger = LoggerFactory.getLogger(ByteUtils.class);
    
    private ByteUtils() {
    }

    public static short toShort(byte byte1, byte byte2) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(byte1);
        bb.put(byte2);
        return bb.getShort(0);
    }
    
    public static int toInt(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(bytes);
        return bb.getInt(0);
    }
    
    public static String toHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        
        return sb.toString();
    }
    
    public static String toString(byte[] content, int offset, int length) {
        int _start = offset;
        int _end = offset + length;
        return toString(Arrays.copyOfRange(content, _start, _end));
    }
    
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException was thrown.");
        }
        
        return null;
    }
    
    public static byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
        ByteArrayOutputStream outputStream = null;

        try {
            outputStream = new ByteArrayOutputStream();
            
            int start = storeOffset + offset;
            int _count = count - 1;
            for (int i = start; i < content.length; i++) {
                byte value = content[i];
                if (value == 0) { //NULL byte
                    if (_count == 0) {
                        break;
                    } else {
                        _count--;
                        outputStream.write(0);
                    }
                } else {
                    outputStream.write(value);
                }
            }
            
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.flush();
                } catch (IOException e1) {
                    logger.warn("IOException while flushing outputstream.");
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.warn("IOException while closing outputstream.");
                }
            }
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * RPMInputStream must be created with saveStores set to true
     * @param rpmInputStream
     * @param tag
     * @return
     */
    public static String getString(RPMInputStream rpmInputStream, int tag) {
        List<RPMHeaderIndex> list = rpmInputStream.getHeadIndexes();
        for (RPMHeaderIndex index : list) {
            if (index.getTag() == tag) {
                return ByteUtils.toString(index.getValue(rpmInputStream.getHeadStore(), 0));
            }
        }
        
        return null;
    }
}
