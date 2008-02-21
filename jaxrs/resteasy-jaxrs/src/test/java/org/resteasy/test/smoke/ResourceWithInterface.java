package org.resteasy.test.smoke;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceWithInterface implements SimpleClient
{
   public String getWild()
   {
      return "Wild";
   }

   public String getBasic()
   {
      System.out.println("getBasic()");
      return "basic";
   }

   public void putBasic(String body)
   {
      System.out.println(body);
   }

   public String getQueryParam(@QueryParam("param")String param)
   {
      return param;
   }

   public String getMatrixParam(@MatrixParam("param")String param)
   {
      return param;
   }

   public int getUriParam(@PathParam("param")int param)
   {
      return param;
   }


}