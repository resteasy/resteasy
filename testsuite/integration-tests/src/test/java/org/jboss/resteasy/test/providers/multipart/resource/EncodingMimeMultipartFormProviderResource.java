package org.jboss.resteasy.test.providers.multipart.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import jakarta.mail.internet.ContentDisposition;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/encoding-mime")
public class EncodingMimeMultipartFormProviderResource {

    private static Logger logger = Logger.getLogger(EncodingMimeMultipartFormProviderResource.class);
    /**
     * Non-ASCII file name
     */
    public static final String FILENAME_NON_ASCII = "Döner 1 + 2.png";

    @POST
    @Path("file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadFile(MultipartFormDataInput multipart) throws Exception {
        Map<String, List<InputPart>> uploadForm = multipart.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file_upload");
        logger.infov("Number of parts {0}", inputParts.size());
        assertTrue(inputParts.size() == 1);
        InputPart inputPart = inputParts.get(0);
        String dispositionHeader = inputPart.getHeaders().getFirst("Content-Disposition");
        logger.infov("Content-Disposition: {0}", dispositionHeader);
        ContentDisposition cd = new ContentDisposition(dispositionHeader);
        String filenameFromHeader = cd.getParameter("filename");
        logger.infov("Got filename {0}", filenameFromHeader);
        assertEquals(FILENAME_NON_ASCII, filenameFromHeader, "Filename must match");
    }
}
