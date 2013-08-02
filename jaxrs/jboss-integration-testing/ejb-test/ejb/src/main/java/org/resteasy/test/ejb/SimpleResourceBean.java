package org.resteasy.test.ejb;

import javax.ejb.Stateless;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Stateless
public class SimpleResourceBean implements SimpleResource
{
   public String getBasic()
   {
      System.out.println("getBasic()");
      return "basic";
   }

   public void putBasic(String body)
   {
      System.out.println(body);
   }

   public String getQueryParam(String param)
   {
      return param;
   }

   public String getMatrixParam(String param)
   {
      return param;
   }

   public int getUriParam(int param)
   {
      return param;
   }

}
