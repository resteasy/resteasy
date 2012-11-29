package org.jboss.resteasy.skeleton.key.model.representations;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AccessTokenRequest extends UserAuth
{
   protected String code;

   public String getCode()
   {
      return code;
   }

   public void setCode(String code)
   {
      this.code = code;
   }
}
