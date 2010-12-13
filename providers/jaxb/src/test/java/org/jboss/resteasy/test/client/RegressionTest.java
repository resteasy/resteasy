package org.jboss.resteasy.test.client;

import junit.framework.Assert;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegressionTest extends BaseResourceTest
{
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(MessageResource.class);
   }

   @XmlRootElement
   public static class MessageTFM
   {
      private BigDecimal msgId;
      private Date createdDate;
      private String destinationId;
      private BigDecimal msgComp;
      private BigDecimal numLocTfmsProvided;
      private String sourceId;
      private String versionMajor;
      private String versionMinor;


      public BigDecimal getMsgId()
      {
         return msgId;
      }

      public void setMsgId(BigDecimal msgId)
      {
         this.msgId = msgId;
      }

      public Date getCreatedDate()
      {
         return createdDate;
      }

      public void setCreatedDate(Date createdDate)
      {
         this.createdDate = createdDate;
      }

      public String getDestinationId()
      {
         return destinationId;
      }

      public void setDestinationId(String destinationId)
      {
         this.destinationId = destinationId;
      }

      public BigDecimal getMsgComp()
      {
         return msgComp;
      }

      public void setMsgComp(BigDecimal msgComp)
      {
         this.msgComp = msgComp;
      }

      public BigDecimal getNumLocTfmsProvided()
      {
         return numLocTfmsProvided;
      }

      public void setNumLocTfmsProvided(BigDecimal numLocTfmsProvided)
      {
         this.numLocTfmsProvided = numLocTfmsProvided;
      }

      public String getSourceId()
      {
         return sourceId;
      }

      public void setSourceId(String sourceId)
      {
         this.sourceId = sourceId;
      }

      public String getVersionMajor()
      {
         return versionMajor;
      }

      public void setVersionMajor(String versionMajor)
      {
         this.versionMajor = versionMajor;
      }

      public String getVersionMinor()
      {
         return versionMinor;
      }

      public void setVersionMinor(String versionMinor)
      {
         this.versionMinor = versionMinor;
      }
   }

   @Path("/messages/TFM")
   public interface IMessageTFMResource
   {

      /*
      @GET
      @Produces("application/xml")
      @Path("{id}")
      public MessageTFM getMessage(@PathParam("id") BigDecimal id);*/

      @POST
      @Consumes("application/xml")
      public Response saveMessage(MessageTFM msg);

      /*
      @PUT
      @Produces("application/xml")
      @Consumes("application/xml")
      @Path("/{id}")
      public Response updateMessage(@PathParam("id") int id, MessageTFM msg);

      @DELETE
      @Consumes("application/xml")
      @Produces("application/xml")
      @Path("/{id}")
      public Response deleteMessage(@PathParam("id") int id);

      @POST
      @Produces("application/xml")
      @Consumes("application/xml")
      @Path("/matching")
      public List<MessageTFM> getMatchingMessages(MessageTFM msg); */

   }

   public static class MessageResource implements IMessageTFMResource
   {
      @Override
      public Response saveMessage(MessageTFM msg)
      {
         System.out.println("saveMessage");
         return Response.created(URI.create("/foo/bar")).build();
      }
   }

   @Test
   public void testClient() throws Exception
   {
      MessageTFM m = new MessageTFM();
      m.setMsgId(new BigDecimal(42));
      m.setCreatedDate(new Date());
      m.setDestinationId("ABCD1234");
      m.setMsgComp(new BigDecimal(2));
      m.setNumLocTfmsProvided(new BigDecimal(14));
      m.setSourceId("WXYZ6789");
      m.setVersionMajor("4");
      m.setVersionMinor("1");
      IMessageTFMResource client = ProxyFactory.create(
              IMessageTFMResource.class,
              generateBaseUrl());
      Response r = client.saveMessage(m);
      Assert.assertEquals(r.getStatus(), 201);
   }
}
