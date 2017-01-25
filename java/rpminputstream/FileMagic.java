https://github.com/randy-c/snippets
package randyc;

public enum FileMagic {
    XZ {
        @Override
        byte[] getMagic() {
            return new byte[] {(byte) 0xFD, (byte) 0x37, (byte) 0x7A, (byte) 0x58, (byte) 0x5A, (byte) 0x00};
        }
    },
    BZ2 {
        @Override
        byte[] getMagic() {
            return new byte[] {(byte) 0x42, (byte) 0x5A, (byte) 0x68};
        }
        
    },   
    GZIP {
        @Override
        byte[] getMagic() {
            // TODO Auto-generated method stub
            return null;
        }
    },    
    CPIO {
        @Override
        byte[] getMagic() {
            return new byte[] {(byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x01};
        }
    },    
    ZIP {
        @Override
        byte[] getMagic() {
            return new byte[] {(byte) 0x50, (byte) 0x4B};
        }
    },    
    SEVENZ {
        @Override
        byte[] getMagic() {
            // TODO Auto-generated method stub
            return null;
        }
    }, 
    AR {
        @Override
        byte[] getMagic() {
            // TODO Auto-generated method stub
            return null;
        }
    },  
    
    DUMP {
        @Override
        byte[] getMagic() {
            // TODO Auto-generated method stub
            return null;
        }
    },    
    TAR {
        @Override
        byte[] getMagic() {
            return new byte[] {(byte) 0x75, (byte) 0x73, (byte) 0x74, (byte) 0x61, (byte) 0x72};
        }
    },    
    LZH {
        @Override
        byte[] getMagic() {
            return new byte[] {(byte) 0x1F, (byte) 0xA0};
        }
    };
    
    abstract byte[] getMagic();
}
