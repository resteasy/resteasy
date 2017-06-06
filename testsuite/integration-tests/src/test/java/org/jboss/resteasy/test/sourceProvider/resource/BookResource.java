package org.jboss.resteasy.test.sourceProvider.resource;


import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Path("/")
public class BookResource {
    private static Logger logger = Logger.getLogger(BookResource.class);

    @POST
    @Path("test")
    @Consumes({"application/*+xml"})
    @Produces({"application/*+xml"})
    public Source testSource(Source mySource) {
        String resultXmlStr = null;
        try {
            Transformer transformer= TransformerFactory.newInstance().newTransformer();
            StreamResult xmlOutput=new StreamResult(new StringWriter());
            transformer.transform(mySource,xmlOutput);
            resultXmlStr = xmlOutput.getWriter().toString();
            logger.info(xmlOutput.getWriter().toString());
        } catch (TransformerConfigurationException e) {
            logger.error("Failed to create transformer",e);
        } catch (TransformerException e) {
            logger.error("Failed to transform Source to xml result", e);
        }

        InputStream stream = new ByteArrayInputStream(resultXmlStr.getBytes(StandardCharsets.UTF_8));
        return new StreamSource(stream);
    }
}
