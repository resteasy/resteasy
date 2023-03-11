package org.jboss.resteasy.test.response.resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

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
        java.nio.file.Path file = null;
        try {
            file = Files.createTempFile("tmp", "tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                for (int i = 0; i < 1000; i++) {
                    writer.write("hello");
                }
                writer.write("1234");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file.toFile();
    }

    private static File createSmallFile() {
        java.nio.file.Path smallfile = null;
        try {
            smallfile = Files.createTempFile("smalltmp", "tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(smallfile)) {
                writer.write("123456789");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return smallfile.toFile();
    }

}
