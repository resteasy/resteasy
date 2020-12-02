package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.messagebody.AsyncBufferedMessageBodyWriter;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.WriterException;
import org.w3c.dom.Document;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import javax.xml.XMLConstants;
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
 * Provider that reads and writes org.w3c.dom.Document.
 *
 * @author <a href="sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: $
 */
@Provider
@Produces({"text/xml", "text/*+xml", "application/xml", "application/*+xml"})
@Consumes({"text/xml", "text/*+xml", "application/xml", "application/*+xml"})
public class DocumentProvider extends AbstractEntityProvider<Document> implements AsyncBufferedMessageBodyWriter<Document>
{
   private TransformerFactory transformerFactory;
   private DocumentBuilderFactory documentBuilder;
   private boolean expandEntityReferences = false;
   private boolean enableSecureProcessingFeature = true;
   private boolean disableDTDs = true;
   private volatile boolean init;

   public DocumentProvider()
   {
      // nullary constructor so that GraalVM's native-image is able to process this file
      this(ResteasyContext.getContextData(ResteasyConfiguration.class));
   }

   private void lazyInit() {
      if (!init) {
         synchronized (this) {
            if (!init) {
               this.documentBuilder = DocumentBuilderFactory.newInstance();
               this.transformerFactory = TransformerFactory.newInstance();
               this.init = true;
            }
         }
      }

   }

   public DocumentProvider(final @Context ResteasyConfiguration config)
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : DocumentProvider", getClass().getName());
      try
      {
         String s = config.getParameter(ResteasyContextParameters.RESTEASY_EXPAND_ENTITY_REFERENCES);
         expandEntityReferences = (s == null ? false : Boolean.parseBoolean(s));
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.unableToRetrieveConfigExpand();
      }
      try
      {
         String s = config.getParameter(ResteasyContextParameters.RESTEASY_SECURE_PROCESSING_FEATURE);
         enableSecureProcessingFeature = (s == null ? true : Boolean.parseBoolean(s));
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.unableToRetrieveConfigSecure();
      }
      try
      {
         String s = config.getParameter(ResteasyContextParameters.RESTEASY_DISABLE_DTDS);
         disableDTDs = (s == null ? true : Boolean.parseBoolean(s));
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.unableToRetrieveConfigDTDs();
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
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      lazyInit();
      try
      {
         documentBuilder.setExpandEntityReferences(expandEntityReferences);
         documentBuilder.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, enableSecureProcessingFeature);
         documentBuilder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disableDTDs);
         documentBuilder.setFeature("http://xml.org/sax/features/external-general-entities", expandEntityReferences);
         documentBuilder.setFeature("http://xml.org/sax/features/external-parameter-entities", expandEntityReferences);
         if (expandEntityReferences) {
            try {
//               documentBuilder.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "all");
               // backward compatibility for jdk 1.6
               // we can't directly use XMLConstants.ACCESS_EXTERNAL_DTD here
               // because it doesn't exist in jdk 1.6
               documentBuilder.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "all");
            } catch (IllegalArgumentException e) {
               //jaxp 1.5 feature not supported
            }
         }
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
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      lazyInit();
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
