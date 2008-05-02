package org.resteasy.test.ejb;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.PathParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
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
