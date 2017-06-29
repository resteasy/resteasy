package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Path("/")
public class RangeResource {

    static File file;
    static File smallFile;

    static {
        file = createFile();
        smallFile = createSmallFile();
    }

    @GET
    @Path("file")
    @Produces("text/plain")
    public File getFile() {
        return file;
    }

    @GET
    @Path("deletefile")
    public void deleteFile() throws Exception {
        if (file.exists()) {
            file.delete();
        }
    }

    @GET
    @Path("smallfile")
    @Produces("text/plain")
    public File getSmallFile() {
        return smallFile;
    }

    @GET
    @Path("deletesmallfile")
    public void deleteSmallFile() throws Exception {
        if (smallFile.exists()) {
            smallFile.delete();
        }
    }

    private static File createFile() {
        File file = null;
        try {
            file = File.createTempFile("tmp", "tmp");
            FileOutputStream fos = new FileOutputStream(file);
            for (int i = 0; i < 1000; i++) {
                fos.write("hello".getBytes());
            }
            fos.write("1234".getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private static File createSmallFile() {
        File smallfile = null;
        try {
            smallfile = File.createTempFile("smalltmp", "tmp");
            FileOutputStream fos = new FileOutputStream(smallfile);
            fos.write("123456789".getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return smallfile;
    }

}
