package org.jboss.resteasy.test.multipart;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInputImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Base64EncoderMultiPartTest {

    @Test
    public void testEncoder() throws Exception {
        String body = Base64.getEncoder().encodeToString("ABC123".getBytes());
        String input = "URLSTR: file:/Users/billburke/jboss/resteasy-jaxrs/resteasy-jaxrs/src/test/test-data/data.txt\r\n"
                + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n"
                + "Content-Disposition: form-data; name=\"data.txt\"; filename=\"data.txt\"\r\n"
                + "Content-Type: application/octet-stream\r\n"
                + "Content-Transfer-Encoding: base64\r\n"
                + "\r\n"
                + body + "\r\n"
                + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3--";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("boundary", "B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3");
        MediaType contentType = new MediaType("multipart", "form-data", parameters);
        MultipartInputImpl multipart = new MultipartInputImpl(contentType, new ResteasyProviderFactoryImpl());
        multipart.parse(bais);

        for (InputPart part : multipart.getParts()) {
            InputStream inputStream = ((MultipartInputImpl.PartImpl) part).getBody();
            byte[] bytes = inputStream.readAllBytes();
            String base64bytes = Base64.getEncoder().encodeToString(bytes);
            Assertions.assertEquals(body, base64bytes);
        }
    }
}
