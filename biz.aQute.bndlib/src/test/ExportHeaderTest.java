package test;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import junit.framework.*;
import aQute.lib.osgi.*;

public class ExportHeaderTest extends TestCase {
    

    /**
     * If you import a range then the maven guys can have the silly -SNAPSHOT in the
     * version. This tests if ranges are correcly cleaned up.
     * @throws Exception
     */
    public void testImportHeaderWithMessedUpRange() throws Exception {
        Builder builder = new Builder();
        Jar bin = new Jar( new File("bin") );
        builder.setClasspath( new Jar[]{bin});
        Properties p = new Properties();
        p.setProperty("Private-Package", "test.packageinfo.ref");
        p.setProperty("Import-Package", "test.packageinfo;version=\"[1.1.1-SNAPSHOT,1.1.1-SNAPSHOT]");
        builder.setProperties(p);
        Jar jar = builder.build();
        Manifest manifest = jar.getManifest();
        
        String imph = manifest.getMainAttributes().getValue("Import-Package");
        assertEquals("test.packageinfo;version=\"[1.1.1.SNAPSHOT,1.1.1.SNAPSHOT]\"", imph);   
    }
    
    public void testPickupExportVersion() throws Exception {
        Builder builder = new Builder();
        Jar bin = new Jar( new File("bin") );
        builder.setClasspath( new Jar[]{bin});
        Properties p = new Properties();
        p.setProperty("Private-Package", "test.packageinfo.ref");
        builder.setProperties(p);
        Jar jar = builder.build();
        Manifest manifest = jar.getManifest();
        
        String imph = manifest.getMainAttributes().getValue("Import-Package");
        assertEquals("test.packageinfo;version=\"[1.0,2)\"", imph);     
    }
    public void testExportVersionWithPackageInfo() throws Exception {
        Builder builder = new Builder();
        Jar bin = new Jar( new File("bin") );
        builder.setClasspath( new Jar[]{bin});
        Properties p = new Properties();
        p.setProperty("Export-Package", "test.packageinfo");
        builder.setProperties(p);

        Jar jar = builder.build();
        Manifest manifest = jar.getManifest();
        
        String exph = manifest.getMainAttributes().getValue("Export-Package");
        assertEquals("test.packageinfo;version=\"1.0.0.SNAPSHOT\"", exph);
    }
}
