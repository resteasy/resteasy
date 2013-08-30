import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ChatClient
{
   public static void main(String[] args) throws Exception
   {
      String name = args[0];

      System.out.println();
      System.out.println();
      System.out.println();
      System.out.println();

      final Client client = new ResteasyClientBuilder()
                          .connectionPoolSize(3)
                          .build();
      WebTarget target = client.target("http://localhost:8080/services/chat");

      target.request().async().get(new InvocationCallback<Response>()
      {
         @Override
         public void completed(Response response)
         {
            Link next = response.getLink("next");
            String message = response.readEntity(String.class);
            System.out.println();
            System.out.print(message);// + "\r");
            System.out.println();
            System.out.print("> ");
            client.target(next).request().async().get(this);
         }

         @Override
         public void failed(Throwable throwable)
         {
            System.err.println("FAILURE!");
         }
      });


      while (true)
      {
         System.out.print("> ");
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         String message = br.readLine();
         target.request().post(Entity.text(name + ": " + message));
      }


   }
}
