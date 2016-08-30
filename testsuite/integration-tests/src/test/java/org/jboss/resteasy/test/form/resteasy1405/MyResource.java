package org.jboss.resteasy.test.form.resteasy1405;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

@Path("/")
public class MyResource
{
   private static final Log LOG = LogFactory.getLog(MyResource.class);

   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_PLAIN)
   @Path("/field")
   @POST
   public Response byField(@MultipartForm ByFieldForm form)
   {
      LOG.info("Entered byField");

      try
      {
         LOG.info("Name: " + form.getName());

         InputData input = parse(form.getData());
         LOG.info("Items: " + input.getItems() + " (" + form.getData().getMediaType() + ')');

         OutputData output = new OutputData().withName(form.getName()).withContentType(form.getData().getMediaType())
               .withItems(input.getItems());

         return Response.ok().entity(output).build();
      }
      catch (IOException | JAXBException e)
      {
         return Response.serverError().entity(e.getMessage()).build();
      }
   }

   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_PLAIN)
   @Path("/setter")
   @POST
   public Response bySetter(@MultipartForm BySetterForm form)
   {
      LOG.info("Entered bySetter");

      try
      {
         LOG.info("Name: " + form.getName());

         InputData input = parse(form.getData());
         LOG.info("Items: " + input.getItems() + " (" + form.getData().getMediaType() + ')');

         OutputData output = new OutputData().withName(form.getName()).withContentType(form.getData().getMediaType())
               .withItems(input.getItems());

         return Response.ok().entity(output).build();
      }
      catch (IOException | JAXBException e)
      {
         return Response.serverError().entity(e.getMessage()).build();
      }
   }

   private InputData parse(InputPart part) throws JAXBException, IOException
   {
      JAXBContext jaxbc = JAXBContext.newInstance(InputData.class);
      Unmarshaller unmarshaller = jaxbc.createUnmarshaller();

      try (InputStream stream = part.getBody(InputStream.class, null))
      {
         StreamSource source = new StreamSource(stream);
         return unmarshaller.unmarshal(source, InputData.class).getValue();
      }
      finally
      {
         if (unmarshaller instanceof Closeable)
         {
            ((Closeable) unmarshaller).close();
         }
      }
   }
}