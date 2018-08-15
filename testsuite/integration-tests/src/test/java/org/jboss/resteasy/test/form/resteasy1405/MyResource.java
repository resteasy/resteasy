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

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

@Path("/")
public class MyResource
{
   private static Logger log = org.jboss.logging.Logger.getLogger(MyResource.class);

   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_PLAIN)
   @Path("/field")
   @POST
   public Response byField(@MultipartForm ByFieldForm form)
   {
      log.info("Entered byField");

      try
      {
         log.info("Name: " + form.getName());

         InputData input = parse(form.getData());
         log.info("Items: " + input.getItems() + " (" + form.getData().getMediaType() + ')');

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
      log.info("Entered bySetter");

      try
      {
         log.info("Name: " + form.getName());

         InputData input = parse(form.getData());
         log.info("Items: " + input.getItems() + " (" + form.getData().getMediaType() + ')');

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
