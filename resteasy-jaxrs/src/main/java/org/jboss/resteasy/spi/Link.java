package org.jboss.resteasy.spi;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Implementation of <a href="http://tools.ietf.org/html/draft-nottingham-http-link-header-06">Link Headers v6</a>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Link
{
   protected String title;
   protected String relationship;
   protected String href;
   protected String type;
   protected MultivaluedMap<String, String> extensions = new MultivaluedMapImpl<String, String>();

   public Link()
   {
   }

   public Link(String title, String relationship, String href, String type, MultivaluedMap<String, String> extensions)
   {
      this.relationship = relationship;
      this.href = href;
      this.type = type;
      this.title = title;
      if (extensions != null) this.extensions = extensions;
   }

   public String getRelationship()
   {
      return relationship;
   }

   public void setRelationship(String relationship)
   {
      this.relationship = relationship;
   }

   public String getHref()
   {
      return href;
   }

   public void setHref(String href)
   {
      this.href = href;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public MultivaluedMap<String, String> getExtensions()
   {
      return extensions;
   }

   public ClientRequest request()
   {
      return new ClientRequest(href);
   }

   public ClientRequest request(ClientExecutor executor)
   {
      return new ClientRequest(href, executor);
   }
}
