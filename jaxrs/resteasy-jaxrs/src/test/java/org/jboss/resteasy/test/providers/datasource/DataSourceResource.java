/**
 *
 */
package org.jboss.resteasy.test.providers.datasource;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import java.io.IOException;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 *         Jun 23, 2008
 */
@Path("/jaf")
public class DataSourceResource
{

   @POST
   @ConsumeMime("image/jpeg")
   @ProduceMime("text/plain")
   public String postDataSource(DataSource datasource)
   {
      return datasource.getContentType();
   }

   @GET
   @Path("/{value}")
   public DataSource getDataSource(@PathParam("value")String value) throws IOException
   {
      DataSource ds = new ByteArrayDataSource(value, "text/plain");
      return ds;
   }
}
