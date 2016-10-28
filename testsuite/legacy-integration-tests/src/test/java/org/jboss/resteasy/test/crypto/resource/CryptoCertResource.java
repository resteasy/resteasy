package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.utils.TestApplication;
import javax.ws.rs.Path;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Path("/test/resource")
public class CryptoCertResource {

    public static X509Certificate cert;
    public static PrivateKey privateKey;

    static {
        try {
            privateKey = (PrivateKey) fromString(loadString("privateKey"));
            cert = (X509Certificate) fromString(loadString("cert"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String readString(final InputStream in) throws IOException {
        char[] buffer = new char[10240];
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int wasRead = 0;
        do {
            wasRead = reader.read(buffer, 0, 1024);
            if (wasRead > 0) {
                builder.append(buffer, 0, wasRead);
            }
        }
        while (wasRead > -1);

        return builder.toString();
    }

    private static String loadString(String name) throws IOException {
        String resource = String.format("%s.txt", name);
        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(resource);
        }
        if (stream == null) {
            stream = TestApplication.class.getResourceAsStream(resource);
        }
        if (stream == null) {
            throw new RuntimeException();
        }
        return readString(stream);
    }
    /**
     * Read the object from Base64 string.
     */
    private static Object fromString(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
}
