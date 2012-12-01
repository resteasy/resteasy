package org.jboss.resteasy.skeleton.key.model.data;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Realm implements Serializable
{
   private static final long serialVersionUID = 1L;

   protected String id;
   protected String name;
   protected long tokenLifespan = 3600 * 24; // one day
   protected long accessCodeLifespan = 300; // 5 minutes
   protected boolean directAccessTokenAllowed;
   protected boolean enabled;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public boolean isEnabled()
   {
      return enabled;
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   public boolean isDirectAccessTokenAllowed()
   {
      return directAccessTokenAllowed;
   }

   public void setDirectAccessTokenAllowed(boolean directAccessTokenAllowed)
   {
      this.directAccessTokenAllowed = directAccessTokenAllowed;
   }

   public long getTokenLifespan()
   {
      return tokenLifespan;
   }

   public void setTokenLifespan(long tokenLifespan)
   {
      this.tokenLifespan = tokenLifespan;
   }

   public long getAccessCodeLifespan()
   {
      return accessCodeLifespan;
   }

   public void setAccessCodeLifespan(long accessCodeLifespan)
   {
      this.accessCodeLifespan = accessCodeLifespan;
   }
}
