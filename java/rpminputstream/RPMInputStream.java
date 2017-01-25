package randyc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for processing RPM file.
 * Extracts Lead, Signature, and Head section information.  
 * Positions the inputstream at beginning of payload.
 * 
 * http://refspecs.linuxbase.org/LSB_3.1.1/LSB-Core-generic/LSB-Core-generic/pkgformat.html
 */
public class RPMInputStream extends InputStream {
    private static final Logger logger = LoggerFactory.getLogger(RPMInputStream.class);
    
    private List<RPMHeaderIndex> signatureIndexes  = new ArrayList<RPMHeaderIndex>();
    private List<RPMHeaderIndex> headIndexes = new ArrayList<RPMHeaderIndex>();
    private RPMLead lead;
    private RPMHeader headHeader;
    private RPMHeader signatureHeader;
    private int payloadOffset;
    private byte[] payloadMagic;
    private InputStream inputStream;
    private int signatureOffset;
    private int signatureIndexOffset;
    private int signatureStoreOffset;
    private int headOffset;
    private int headIndexOffset;
    private int headStoreOffset;
    
    private byte[] signatureStore;
    private byte[] headStore;
    
    
    private static int PAYLOAD_MAGIC_BUFFER_LENGTH = 6;

    public RPMInputStream(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        init(inputStream, false);
    }
    
    public RPMInputStream(InputStream inputStream, boolean saveStores) throws IOException {
        this.inputStream = inputStream;
        init(inputStream, saveStores);
    }

    private void init(InputStream inputStream, boolean saveStores) throws IOException {
        int offset = 0;

        logger.debug(String.format(" RPM Lead (offset = %s)", offset));
        offset = offset + RPMLead.SIZE;
        lead = processLead(inputStream);
        logger.debug(lead.toReadableString());

        logger.debug(String.format(" RPM Signature (offset = %s)", offset));
        signatureOffset = offset;
        offset = offset + RPMHeader.SIZE;
        signatureHeader = processHeader(inputStream);

        logger.debug(signatureHeader.toReadableString());
        
        signatureIndexes = processIndexes(signatureHeader, inputStream);
        signatureIndexOffset = offset;
        logger.debug(String.format(" RPM Signature Indexes (count = %s, offset = %s", 
                signatureIndexes.size(), 
                offset));
        offset = offset + (RPMHeaderIndex.SIZE * signatureIndexes.size());
        signatureStoreOffset = offset;
        logger.debug("Signature Store at offset " + signatureStoreOffset);
        
        if (saveStores) {
            signatureStore = new byte[signatureHeader.getHsize()];
            inputStream.read(signatureStore);
        } else {
            skipLoop(inputStream, new Long(signatureHeader.getHsize()));
        }
        
        offset = offset + signatureHeader.getHsize();
        
        int pad = offset % 8;
        
        skipLoop(inputStream, new Long(pad));
        offset = offset + pad;
        
        headOffset = offset;
        logger.debug(String.format(" RPM Head (offset = %s)", offset));
        offset = offset + RPMHeader.SIZE;
        headHeader = processHeader(inputStream);
        logger.info(headHeader.toReadableString());
        
        headIndexes = processIndexes(headHeader, inputStream);
        headIndexOffset = offset;
        logger.debug(String.format("RPM Head Indexes (count = %s, offset = %s)", 
                headIndexes.size(), 
                offset));
        offset = offset + (RPMHeaderIndex.SIZE * headIndexes.size());
        headStoreOffset = offset;
        logger.debug("Head store at offset " + headStoreOffset);
        
        if (saveStores) {
            headStore = new byte[headHeader.getHsize()];
            inputStream.read(headStore);
        } else {
            skipLoop(inputStream, headHeader.getHsize());
        }

        offset = offset + headHeader.getHsize();
        
        payloadOffset = offset;
        if (markSupported()) {
            inputStream.mark(PAYLOAD_MAGIC_BUFFER_LENGTH);
            payloadMagic = new byte[PAYLOAD_MAGIC_BUFFER_LENGTH];
            inputStream.read(payloadMagic);
            inputStream.reset();
            logger.debug(String.format("RPM Payload (offset = %s, magic = %s)", offset, ByteUtils.toHex(payloadMagic)));
        }
    }
    
    /**
     * Convenience method to extract the CpioArchiveInputStream from the RPM
     * @param file
     * @return
     * @throws IOException
     * @throws CompressorException
     */
    public static CpioArchiveInputStream getCpioArchiveInputStream(File file) throws IOException, CompressorException {
        RPMInputStream rpmInputStream = getInstance(file);
        CompressorInputStream compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(rpmInputStream);
        return new CpioArchiveInputStream(compressorInputStream);
    }
    
    /**
     * Convenience method to create an instance of RPMInputStream
     * @param file RPM filepath
     * @return
     * @throws IOException
     */
    public static RPMInputStream getInstance(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        return new RPMInputStream(bufferedInputStream);
    }
    
    /**
     * Convenience method to extract the bytes of a file contained within the RPM payload.
     * @param rpmFile
     * @param filePath
     * @return
     * @throws IOException
     * @throws CompressorException
     */
    public static byte[] getFile(File rpmFile, String filePath) throws IOException, CompressorException {
        CpioArchiveInputStream cpioInputStream = RPMInputStream.getCpioArchiveInputStream(rpmFile);
        
        CpioArchiveEntry cpioEntry;
        while ((cpioEntry = cpioInputStream.getNextCPIOEntry()) != null) {
            if (cpioEntry.getName().endsWith(filePath)) {
                return IOUtils.toByteArray(cpioInputStream);
            }
        }

        return null;
    }
    
    public static Map<String, byte[]> getFiles(CpioArchiveInputStream cpioInputStream, String[] filePaths) 
            throws IOException, CompressorException {
        Map<String, byte[]> retVal = new HashMap<String, byte[]>();
        CpioArchiveEntry cpioEntry;
        String key;
        while ((cpioEntry = cpioInputStream.getNextCPIOEntry()) != null) {
            key = isThere(filePaths, cpioEntry.getName());
            if (null != key) {
                retVal.put(key, IOUtils.toByteArray(cpioInputStream));
            }
            
            if (retVal.size() == filePaths.length) {
                break;
            }
        }
        
        return retVal;
    }
    
    public static Map<String, byte[]> getFiles(File rpmFile, String[] filePaths) 
            throws IOException, CompressorException {
        CpioArchiveInputStream cpioInputStream = RPMInputStream.getCpioArchiveInputStream(rpmFile);
        return getFiles(cpioInputStream, filePaths);
    }
    
    private static String isThere(String[] filePaths, String path) {
        for (String filePath : filePaths) {
            if (path.endsWith(filePath)) {
                return filePath;
            }
        }
        
        return null;
    }
    
    /**
     * Convenience method to get the value of RPMTAG_NAME (value = 1000) from RPMInputStream instance
     * @param rpmInputStream
     * @return
     */
    public static String getTagName(RPMInputStream rpmInputStream) {
        return ByteUtils.getString(rpmInputStream, RPMHeader.RPMTAG_NAME);
    }
    
    /**
     * Convenience method to get the value of RPMTAG_ARCH (value = 1022) from RPMInputStream instance.
     * @param rpmInputStream
     * @return
     */
    public static String getArch(RPMInputStream rpmInputStream) {
        return ByteUtils.getString(rpmInputStream, RPMHeader.RPMTAG_ARCH);
    }
    
    /**
     * Convenience method to get the value of RPMTAG_VERSION (value = 1001) from RPMInputStream instance.
     * @param rpmInputStream
     * @return
     */
    public static String getVersion(RPMInputStream rpmInputStream) {
        return ByteUtils.getString(rpmInputStream, RPMHeader.RPMTAG_VERSION);
    }
    
    /**
     * Convenience method to get the value of RPMTAG_RELEASE (value = 1002) from RPMInputStream instance.
     * @param rpmInputStream
     * @return
     */
    public static String getRelease(RPMInputStream rpmInputStream) {
        return ByteUtils.getString(rpmInputStream, RPMHeader.RPMTAG_RELEASE);
    }
    
    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
    
    @Override
    public int available() throws IOException {
        return inputStream.available();
    }
    
    @Override
    public void mark(int readlimit) {
        inputStream.mark(readlimit);
    }
    
    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }
    
    @Override
    public void reset() throws IOException {
        inputStream.reset();
    }
    
    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    private static RPMLead processLead(InputStream fileInputStream) throws IOException {
        byte[] leadContent = new byte[RPMLead.SIZE];
        fileInputStream.read(leadContent);
        return new RPMLead(leadContent);
    }
    
    private static RPMHeader processHeader(InputStream fileInputStream) throws IOException {
        byte[] content = new byte[RPMHeader.SIZE];
        fileInputStream.read(content);
        return new RPMHeader(content);
    }
    
    private static List<RPMHeaderIndex> processIndexes(RPMHeader header, InputStream fileInputStream) throws IOException {
        List<RPMHeaderIndex> retVal = new ArrayList<RPMHeaderIndex>();
        
        for (int i = 0; i < header.getNindex(); i++) {
            byte[] indexContent = new byte[RPMHeaderIndex.SIZE];
            fileInputStream.read(indexContent);
            RPMHeaderIndex index = new RPMHeaderIndex(indexContent);
            retVal.add(index);
        }
        
        return retVal;
    }
    
    /*
     * Depending on implementation InputStream.skip() may skip less than requested.
     */
    private static void skipLoop(InputStream inputStream, long skip) throws IOException {
        long n = skip;
        while(n > 0) {
            long n1 = inputStream.skip(n);
            if( n1 > 0 ) {
                n -= n1;
            } else if( n1 == 0 ) {
                if( inputStream.read() == -1)  // EOF
                    break;
                else 
                    n--;
            } else {
                throw new IOException("InputStream.skip() return negative value");
            }
        }
    }
    
    public List<RPMHeaderIndex> getSignatureIndexes() {
        return signatureIndexes;
    }

    public List<RPMHeaderIndex> getHeadIndexes() {
        return headIndexes;
    }

    public RPMLead getLead() {
        return lead;
    }

    public RPMHeader getHeadHeader() {
        return headHeader;
    }

    public RPMHeader getSignatureHeader() {
        return signatureHeader;
    }

    public int getPayloadOffset() {
        return payloadOffset;
    }
    
    public byte[] getPayloadMagic() {
        return payloadMagic;
    }
    
    public int getSignatureOffset() {
        return signatureOffset;
    }

    public int getSignatureStoreOffset() {
        return signatureStoreOffset;
    }

    public int getHeadOffset() {
        return headOffset;
    }

    public int getHeadStoreOffset() {
        return headStoreOffset;
    }
    
    public int getSignatureIndexOffset() {
        return signatureIndexOffset;
    }

    public int getHeadIndexOffset() {
        return headIndexOffset;
    }
    
    public byte[] getSignatureStore() {
        return signatureStore;
    }

    public byte[] getHeadStore() {
        return headStore;
    }
}
