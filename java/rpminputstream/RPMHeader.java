//https://github.com/randy-c/snippets
package randyc;

import java.util.Arrays;

/**
 * Header of the RPM
 * 
 * Starts at offset 96
 
 struct rpmheader {
    unsigned char magic[4];
    unsigned char reserved[4];
    int nindex;
    int hsize;
    } ;
 
 MAGIC = EDABEEDB
 */
public class RPMHeader {
    public static int RPMTAG_NAME = 1000;
    public static int RPMTAG_VERSION = 1001;
    public static int RPMTAG_RELEASE = 1002;
    public static int RPMTAG_SUMMARY = 1004;
    public static int RPMTAG_DESCRIPTION = 1005;
    public static int RPMTAG_SIZE = 1009;
    public static int RPMTAG_DISTRIBUTION = 1010;
    public static int RPMTAG_VENDOR = 1011;
    public static int RPMTAG_LICENSE = 1014;
    public static int RPMTAG_PACKAGER = 1015;
    public static int RPMTAG_GROUP = 1016;
    public static int RPMTAG_URL = 1020;
    public static int RPMTAG_OS = 1021;
    public static int RPMTAG_ARCH = 1022;
    public static int RPMTAG_SOURCERPM = 1044;
    public static int RPMTAG_ARCHIVESIZE = 1046;
    public static int RPMTAG_RPMVERSION = 1064;
    public static int RPMTAG_COOKIE = 1094;
    public static int RPMTAG_DISTURL = 1123;
    public static int RPMTAG_PAYLOADFORMAT = 1124;
    public static int RPMTAG_PAYLOADCOMPRESSOR = 1125;
    public static int RPMTAG_PAYLOADFLAGS = 1126;
    
    private byte[] magic; // 4 bytes
    private byte[] reserved; // 4bytes
    private int nindex; // 4 bytes
    private int hsize; // 4 bytes;
    private int nextIndex;
    
    public static byte[] MAGIC = new byte[] {(byte) 0x8E, (byte) 0xAD, (byte) 0xE8, (byte) 0x01};
    
    public static int SIZE = 16;
    
    public RPMHeader(byte[] contents) {
        this(contents, 0);
    }
    
    public RPMHeader(byte[] contents, int offset) {
        int _offset = offset;
        magic = Arrays.copyOfRange(contents, _offset, _offset = _offset+4);
        if (!Arrays.equals(magic, MAGIC)) {
            throw new RPMInvalidHeaderException("found: " + ByteUtils.toHex(magic));
        }
        reserved = Arrays.copyOfRange(contents, _offset, _offset = _offset+4);
        nindex = ByteUtils.toInt(Arrays.copyOfRange(contents, _offset, _offset = _offset+4));
        hsize = ByteUtils.toInt(Arrays.copyOfRange(contents, _offset, _offset = _offset+4));
        nextIndex = _offset;
    }
    
    public int getNextIndex() {
        return nextIndex;
    }
    
    public byte[] getMagic() {
        return magic;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public int getNindex() {
        return nindex;
    }

    public int getHsize() {
        return hsize;
    }
    
    public String toReadableString() {
        StringBuffer sb = new StringBuffer();
        sb.append("magic: " + ByteUtils.toHex(this.getMagic()) + "\n");
        sb.append("reserved: " + ByteUtils.toHex(this.getReserved()) + "\n");
        sb.append("nindex: " + Integer.toString(this.getNindex()) + "\n");
        sb.append("hsize: " + Integer.toString(this.getHsize()) + "\n");
        return sb.toString();
    }
}
