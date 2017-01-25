//https://github.com/randy-c/snippets
package randyc;

import java.util.Arrays;

/** RPM IndexRecord
 * 
struct rpmhdrindex {
    int tag; 4 bytes
    int type; 4 bytes
    int offset; 4 bytes
    int count; 4 bytes
    } ;
 */
public class RPMHeaderIndex {
    private int tag;
    private int type;
    private int offset;
    private int count;
    private int nextIndex;
    
    public static int SIZE = 16;
    
    public RPMHeaderIndex(byte[] contents) {
        this(contents, 0);
    }
    
    public RPMHeaderIndex(byte[] contents, int offset) {
        this.tag = ByteUtils.toInt(Arrays.copyOfRange(contents, offset, offset+4));
        this.type = ByteUtils.toInt(Arrays.copyOfRange(contents, offset+4, offset+8));
        this.offset = ByteUtils.toInt(Arrays.copyOfRange(contents, offset+8, offset+12));
        this.count = ByteUtils.toInt(Arrays.copyOfRange(contents, offset+12, offset+16));
        this.nextIndex = offset+16;
    }
    
    public byte[] getValue(byte[] content) {
        return getValue(content, 0);
    }
    
    public byte[] getValue(byte[] content, int offsetHeaderStore) {
        RPMHeaderType headerType = RPMHeaderType.getType(this.type);
        return headerType.getBytes(content, this.offset, this.count, offsetHeaderStore);
    }
    
    public int getNextIndex() {
        return nextIndex;
    }
    
    public int getTag() {
        return tag;
    }

    public int getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }
    
    public String toReadableString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tag: " + Integer.toString(this.getTag()) + "\n");
        sb.append("type: " + Integer.toString(this.getType()) + "\n");
        sb.append("offset: " + Integer.toString(this.getOffset()) + "\n");
        sb.append("count: " + Integer.toString(this.getCount()) + "\n");
        sb.append("nextIndex: " + Integer.toString(this.nextIndex));
        return sb.toString();
    }
    
    public enum RPMHeaderType {
        NULL {
            @Override
            public int getValue() {
                return 0;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                return null;
            }
        },
        CHAR {
            @Override
            public int getValue() {
                return 1;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                int start = storeOffset + offset;
                int end = storeOffset + offset + count;
                byte[] retVal = Arrays.copyOfRange(content, start, end);
                return retVal;
            }
        },
        INT8 {
            @Override
            public int getValue() {
                return 2;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                int start = storeOffset + offset;
                int end = storeOffset + offset + count;
                byte[] retVal = Arrays.copyOfRange(content, start, end);
                return retVal;
            }
        },
        INT16 {
            @Override
            public int getValue() {
                return 3;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                int start = storeOffset + offset;
                int end = storeOffset + offset + (count * 2);
                byte[] retVal = Arrays.copyOfRange(content, start, end);
                return retVal;
            }
        },
        INT32 {
            @Override
            public int getValue() {
                return 4;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                int start = storeOffset + offset;
                int end = storeOffset + offset + (count * 4);
                byte[] retVal = Arrays.copyOfRange(content, start, end);
                return retVal;
            }
        },
        INT64 {
            @Override
            public int getValue() {
                return 5;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                // TODO Auto-generated method stub
                return null;
            }
        },
        STRING {
            @Override
            public int getValue() {
                return 6;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                return ByteUtils.getBytes(content, offset, count, storeOffset);
            }
        },
        BIN {
            @Override
            public int getValue() {
                return 7;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                // TODO Auto-generated method stub
                return null;
            }
        },
        STRING_ARRAY {
            @Override
            public int getValue() {
                return 8;
            }
            
            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                return ByteUtils.getBytes(content, offset, count, storeOffset);
            }
        },
        I18N_STRING {
            @Override
            public int getValue() {
                return 9;
            }
            
            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                return ByteUtils.getBytes(content, offset, count, storeOffset);
            }
        },
        UNKNOWN {
            @Override
            public int getValue() {
                return -1;
            }

            @Override
            public byte[] getBytes(byte[] content, int offset, int count, int storeOffset) {
                return null;
            }
        };
        
        public static RPMHeaderType getType(int value) {
            for (RPMHeaderType type : RPMHeaderType.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            
            return UNKNOWN;
        }
        
        abstract public int getValue();
        
        abstract public byte[] getBytes(byte[] content, int offset, int count, int storeOffset);
    };
}
