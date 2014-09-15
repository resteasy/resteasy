package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.WriterException;
import org.w3c.dom.Document;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Provider that reads and writes org.w3c.dom.Document
 *
 * @author <a href="sduskis@gmail.com>Solomon Duskis</a>
 * @version $Revision: $
 */
@Provider
@Produces({"text/*+xml", "application/*+xml"})
@Consumes({"text/*+xml", "application/*+xml"})
public class DocumentProvider extends AbstractEntityProvider<Document>
{
   private static final Logger logger = Logger.getLogger(DocumentProvider.class);
   
   private final TransformerFactory transformerFactory;
   private final DocumentBuilderFactory documentBuilder;
   private boolean expandEntityReferences = false;

   public DocumentProvider(@Context ResteasyConfiguration config)
   {
      this.documentBuilder = DocumentBuilderFactory.newInstance();
      this.transformerFactory = TransformerFactory.newInstance();
      try
      {
         String s = config.getParameter(ResteasyContextParameters.RESTEASY_EXPAND_ENTITY_REFERENCES);
         expandEntityReferences = (s == null ? false : Boolean.parseBoolean(s));
      }
      catch (Exception e)
      {
         logger.debug("Unable to retrieve config: expandEntityReferences defaults to false");
      }
   }

   public boolean isReadable(Class<?> clazz, Type type,
                             Annotation[] annotation, MediaType mediaType)
   {
      return Document.class.isAssignableFrom(clazz);
   }

   public Document readFrom(Class<Document> clazz, Type type,
                            Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, String> headers, InputStream input)
           throws IOException, WebApplicationException
   {
      try
      {
         documentBuilder.setExpandEntityReferences(expandEntityReferences);
         return documentBuilder.newDocumentBuilder().parse(input);
      }
      catch (Exception e)
      {
         throw new ReaderException(e);
      }
   }

   public boolean isWriteable(Class<?> clazz, Type type,
                              Annotation[] annotation, MediaType mediaType)
   {
      return Document.class.isAssignableFrom(clazz);
   }

   public void writeTo(Document document, Class<?> clazz, Type type,
                       Annotation[] annotation, MediaType mediaType,
                       MultivaluedMap<String, Object> headers, OutputStream output)
           throws IOException, WebApplicationException
   {
      try
      {
         DOMSource source = new DOMSource(document);
         StreamResult result = new StreamResult(output);
         transformerFactory.newTransformer().transform(source, result);
      }
      catch (TransformerException te)
      {
         throw new WriterException(te);
      }
   }
}
