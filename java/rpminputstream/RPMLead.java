package randyc;

import java.util.Arrays;

/**
 * Header of an RPM.
 * 
 * Starts at offset 0
 * 
struct rpmlead {
    unsigned char magic[4];
    unsigned char major, minor;
    short type;
    short archnum;
    char name[66];
    short osnum;
    short signature_type;
    char reserved[16];
} ;

MAGIC = EDABEEDB
 */
public class RPMLead {
    byte[] magic; //4 bytes
    byte major;
    byte minor;
    short type;
    short archnum;
    byte[] name; //66 bytes NUL terminimated
    short osnum;
    short signature_type;
    byte[] reserved; //16 bytes
    
    public static byte[] MAGIC = new byte[] {(byte) 0xED, (byte) 0xAB, (byte) 0xEE, (byte) 0xDB};
    
    public static int SIZE = 96;
    
    public RPMLead(byte[] content) {
        magic = Arrays.copyOfRange(content, 0, 4);
        if (!Arrays.equals(magic, MAGIC)) {
            throw new RPMInvalidLeadException();
        }
        major = content[4];
        minor = content[5];
        type = ByteUtils.toShort(content[6], content[7]);
        archnum = ByteUtils.toShort(content[8], content[9]);
        name = Arrays.copyOfRange(content, 10, 76);
        osnum = ByteUtils.toShort(content[76], content[77]);
        signature_type = ByteUtils.toShort(content[78], content[79]);
        reserved = Arrays.copyOfRange(content, 80, 96);
    }
    
    public int getNextIndex() {
        return 96;
    }
    
    public byte[] getMagic() {
        return magic;
    }

    public byte getMajor() {
        return major;
    }

    public byte getMinor() {
        return minor;
    }

    public short getType() {
        return type;
    }

    public short getArchnum() {
        return archnum;
    }

    public byte[] getName() {
        return name;
    }

    public short getOsnum() {
        return osnum;
    }

    public short getSignature_type() {
        return signature_type;
    }

    public byte[] getReserved() {
        return reserved;
    }
    
    public String toReadableString() {
        StringBuffer sb = new StringBuffer();
        sb.append("magic: " + ByteUtils.toHex(this.getMagic()) + "\n");
        sb.append("name: " + ByteUtils.toString(this.getName())+ "\n");
        sb.append("osnum: " + this.getOsnum()+ "\n");
        sb.append("archnum: " + Short.toString(this.getArchnum())+ "\n");
        sb.append("signature_type: " + Short.toString(this.getSignature_type())+ "\n");
        return sb.toString();
    }
}
