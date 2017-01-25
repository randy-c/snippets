//https://github.com/randy-c/snippets
package randyc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import randyc.RPMHeaderIndex;

public class RPMTest {
    
    public static byte[] TESTRPM_XZ_MAGIC = new byte[] {(byte) 0xFD, 0x37, 0x7A, 0x58, 0x5A, 0x00};
    public static byte[] TESTRPM_JAR_MAGIC = new byte[] {(byte) 0x50, 0x4B, 0x03, 0x04};
    public static byte[] TESTRPM_GZIP_MAGIC = new byte[] {(byte) 0x1F, (byte) 0x8B, (byte) 0x08, 0x00, 0x00, 0x00};

    public static final String TESTRPM = "src/test/resources/testrpm.rpm";
    
    @Test
    /**
     * Test to demonstrate RPMInputStream used to read RPM meta data (Header indexes) from within Lead, Store, and Head 
     * The RPMInputStream by default will not store the RPM meta Data.  This will use the offsets saved to perform
     * a second read of the RPM to retrieve meta data.
     */
    public void testReadRPMHeaderIndexes() throws Exception {
        File file = new File(TESTRPM);
        RPMInputStream rpmInputStream = null;
        FileInputStream fileInputStream = null;
        byte[] value;
        
        try {
            rpmInputStream = new RPMInputStream(new BufferedInputStream(new FileInputStream(file)));
            fileInputStream = new FileInputStream(file);
            
            RPMLead lead = rpmInputStream.getLead();
            Assert.assertEquals(ByteUtils.toString(lead.getName()).trim(), "testpackagename");
            
            byte[] rpmData = new byte[rpmInputStream.getPayloadOffset()];
            fileInputStream.read(rpmData);
            
            System.out.println("== Signature Indexes ==");
            List<RPMHeaderIndex> signatureIndexes = rpmInputStream.getSignatureIndexes();
            for (RPMHeaderIndex index : signatureIndexes) {
                value = index.getValue(rpmData, rpmInputStream.getSignatureStoreOffset());
                System.out.println(index.toReadableString());
                String hex = (value == null) ? "" : ByteUtils.toHex(value);
                String string = (value == null) ? "" : ByteUtils.toString(value);
                System.out.println("value(hex): " + hex);
                System.out.println("value(string): " + string);
                System.out.println("\n");
            }
            
            System.out.println("== Head Indexes ==");
            List<RPMHeaderIndex> headindexes = rpmInputStream.getHeadIndexes();
            for (RPMHeaderIndex index : headindexes) {
                value = index.getValue(rpmData, rpmInputStream.getHeadStoreOffset());
                System.out.println(index.toReadableString());
                String hex = (value == null) ? "" : ByteUtils.toHex(value);
                String string = (value == null) ? "" : ByteUtils.toString(value);
                System.out.println("value(hex): " + hex);
                System.out.println("value(string): " + string);
                System.out.println("\n");
                
                // spot check a few tags.
                if (index.getTag() == 1000) {
                    Assert.assertEquals(string, "testfile");
                } else if (index.getTag() == 1001) {
                    Assert.assertEquals(string, "2.0.0");
                } else if (index.getTag() == 1005) {
                    Assert.assertEquals(string, "This project test bundle");
                } else if (index.getTag() == 1014) {
                    Assert.assertEquals(string, "My vendor");
                }
            }
            
        } finally {
            if (rpmInputStream != null) {
                rpmInputStream.close();
            }
            
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }
    
    @Test
    /**
     * Test to demonstrate RPMInputStream used to read RPM meta data (Header indexes) from within Lead, Store, and Head
     * This instructs RPMInputStream to store the head and signature stores within the RPMInputStream instance.
     * then use those stored stores to read the RPM meta data. 
     * 
     * Memory footprint is larger because the meta data is stored in RPMInputStream instance BUT only one read of the 
     * RPM is required.
     */
    public void testReadRPMHeaderIndexesStored() throws Exception {
        File file = new File("src/test/resources/testrpm.rpm");
        RPMInputStream rpmInputStream = null;
        byte[] value;
        
        try {
            rpmInputStream = new RPMInputStream(new BufferedInputStream(new FileInputStream(file)), true);
            
            RPMLead lead = rpmInputStream.getLead();
            Assert.assertEquals(ByteUtils.toString(lead.getName()).trim(), "testpackagename");
            
            System.out.println("== Signature Indexes ==");
            List<RPMHeaderIndex> signatureIndexes = rpmInputStream.getSignatureIndexes();
            for (RPMHeaderIndex index : signatureIndexes) {
                value = index.getValue(rpmInputStream.getSignatureStore());
                System.out.println(index.toReadableString());
                String hex = (value == null) ? "" : ByteUtils.toHex(value);
                String string = (value == null) ? "" : ByteUtils.toString(value);
                System.out.println("value(hex): " + hex);
                System.out.println("value(string): " + string);
                System.out.println("\n");
            }
            
            System.out.println("== Head Indexes ==");
            List<RPMHeaderIndex> headindexes = rpmInputStream.getHeadIndexes();
            for (RPMHeaderIndex index : headindexes) {
                value = index.getValue(rpmInputStream.getHeadStore());
                System.out.println(index.toReadableString());
                String hex = (value == null) ? "" : ByteUtils.toHex(value);
                String string = (value == null) ? "" : ByteUtils.toString(value);
                System.out.println("value(hex): " + hex);
                System.out.println("value(string): " + string);
                System.out.println("\n");
                
                // spot check a few tags.
                if (index.getTag() == 1000) {
                    Assert.assertEquals(string, "testfile");
                } else if (index.getTag() == 1001) {
                    Assert.assertEquals(string, "2.0.0");
                } else if (index.getTag() == 1005) {
                    Assert.assertEquals(string, "This project test bundle");
                } else if (index.getTag() == 1014) {
                    Assert.assertEquals(string, "My Vendor");
                }
            }
            
        } finally {
            if (rpmInputStream != null) {
                rpmInputStream.close();
            }
        }
    }
    
    @Test
    /**
     * Test to demonstrate RPMInputStream to read the payload.
     * @throws Exception
     */
    public void testRPMfile() throws Exception {
        File file = new File("src/test/resources/testrpm.rpm");
        CompressorInputStream compressorInputStream = null;
        CpioArchiveInputStream cpioInputStream = null;
        ZipArchiveInputStream zipInputStream = null;
        RPMInputStream rpmInputStream = null;
        
        try {
            rpmInputStream = new RPMInputStream(new BufferedInputStream(new FileInputStream(file)));
            Assert.assertTrue(Arrays.equals(rpmInputStream.getPayloadMagic(), TESTRPM_GZIP_MAGIC));
            
            //Uncompress the payload
            compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(rpmInputStream);
            
            //Read the CPIO archive.
            cpioInputStream = new CpioArchiveInputStream(compressorInputStream);
            CpioArchiveEntry cpioEntry = cpioInputStream.getNextCPIOEntry(); 
            Assert.assertTrue(cpioEntry.getName().equals("./testfile.jar");
            
            //Read the jar inside the archive and find project.properties.
            zipInputStream = new ZipArchiveInputStream(cpioInputStream);
            ZipArchiveEntry zipEntry;
            while((zipEntry = zipInputStream.getNextZipEntry()) != null) {
                if (zipEntry.getName().equals("project.properties")) {
                    break;
                }
            }
            
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            IOUtils.copy((InputStream) zipInputStream, (OutputStream) byteStream);
            String fileContents = byteStream.toString();
            System.out.println(" == project.properties == ");
            System.out.println(fileContents);
            
            Assert.assertTrue(fileContents.contains("testplugin"));
            Assert.assertTrue(fileContents.contains("Test Plugin"));
            Assert.assertTrue(fileContents.contains("Test Plugin"));
            Assert.assertTrue(fileContents.contains("Test only please."));
            
        } finally {
            if (rpmInputStream != null) {
                rpmInputStream.close();
            }
            
            if (zipInputStream != null) {
                zipInputStream.close();
            }
            if (cpioInputStream != null) {
                cpioInputStream.close();
            }
            
            if (compressorInputStream != null) {
                compressorInputStream.close();
            }

        }
    }
    
    @Test
    /**
     * Test to demonstrate using the a RPMInputStream convienience method to access CPIO archive.
     * @throws Exception
     */
    public void extractCPIOArchive() throws Exception {
        File file = new File("src/test/resources/new_testrpm.rpm");
        CpioArchiveInputStream cpioInputStream = RPMInputStream.getCpioArchiveInputStream(file);
        
        CpioArchiveEntry cpioEntry;
        while ((cpioEntry = cpioInputStream.getNextCPIOEntry()) != null) {
            if (cpioEntry.getName().contains("project.properties")) {
                break;
            }
        }
        
        Properties props = new Properties();
        props.load(cpioInputStream);
        
        for (Entry<Object, Object> entry : props.entrySet() ) {
            System.out.println(String.format("%s => %s", entry.getKey(), entry.getValue()));
        }
        
        Assert.assertTrue(props.containsKey("Name"));
        Assert.assertTrue(props.containsKey("DisplayName"));
        Assert.assertTrue(props.containsKey("Description"));
        Assert.assertTrue(props.containsKey("Version"));
        Assert.assertTrue(props.containsKey("Summary"));
    }
    
    @Test
    public void extractFile() throws Exception {
        File file = new File("src/test/resources/new_testrpm.rpm");
        byte[] bytes = RPMInputStream.getFile(file, "project.properties-1.2.4");
        Assert.assertTrue(bytes.length > 0);
    }
}
