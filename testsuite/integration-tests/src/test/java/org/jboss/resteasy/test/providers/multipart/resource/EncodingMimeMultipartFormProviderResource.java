package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.mail.internet.ContentDisposition;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

@Path("/encoding-mime")
public class EncodingMimeMultipartFormProviderResource {

    private static Logger logger = Logger.getLogger(EncodingMimeMultipartFormProviderResource.class);
    /**
     * Non-ASCII file name
     */
    public static final String FILENAME_NON_ASCII = "Döner.png";

    @POST
    @Path("file")
    @Consumes( MediaType.MULTIPART_FORM_DATA )
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
       assertEquals("Filename must match", FILENAME_NON_ASCII, filenameFromHeader); 
    }
}