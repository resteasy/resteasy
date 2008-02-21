package org.resteasy.specimpl;

import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseImpl extends Response
{
   private Object entity;
   private int status = HttpResponseCodes.SC_OK;
   private MultivaluedMap<String, Object> metadata = new MultivaluedMapImpl<String, Object>();
   private NewCookie[] newCookies = {};

   public ResponseImpl(Object entity, int status, MultivaluedMap<String, Object> metadata, NewCookie[] newCookies)
   {
      this.entity = entity;
      this.status = status;
      this.metadata = metadata;
      if (newCookies != null) this.newCookies = newCookies;
   }

   public ResponseImpl()
   {
   }

   public Object getEntity()
   {
      return entity;
   }

   public int getStatus()
   {
      return status;
   }

   public MultivaluedMap<String, Object> getMetadata()
   {
      return metadata;
   }

   public NewCookie[] getNewCookies()
   {
      return newCookies;
   }

   public void setEntity(Object entity)
   {
      this.entity = entity;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setMetadata(MultivaluedMap<String, Object> metadata)
   {
      this.metadata = metadata;
   }

   public void setNewCookies(NewCookie[] newCookies)
   {
      this.newCookies = newCookies;
   }
}
