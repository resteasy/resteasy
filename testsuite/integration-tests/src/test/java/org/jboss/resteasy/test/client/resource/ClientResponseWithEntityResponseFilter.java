package org.jboss.resteasy.test.client.resource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

public class ClientResponseWithEntityResponseFilter implements ClientResponseFilter {

   private static boolean called;

   public static boolean called() {
      return called;
   }

   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
      replaceEntityStream(responseContext);
      called = true;
   }

   private static List<String> replaceEntityStream(ClientResponseContext ctx) throws IOException {
      List<String> entity = null;
      if (ctx.hasEntity()) {
         InputStream is = ctx.getEntityStream();
         entity = readEntityFromStream(is);
         ByteArrayInputStream bais = new ByteArrayInputStream(linesToBytes(entity));
         ctx.setEntityStream(bais);
      }
      return entity;
   }

   private static List<String> readEntityFromStream(InputStream is) throws IOException {
      String entity;
      List<String> lines = new LinkedList<String>();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      while ((entity = br.readLine()) != null)
         lines.add(entity);
      return lines;
   }

   private static byte[] linesToBytes(List<String> lines) {
      StringBuilder sb = new StringBuilder();
      for (Iterator<String> i = lines.iterator(); i.hasNext();) {
         sb.append(i.next());
         if (i.hasNext())
            sb.append("\n");
      }
      return sb.toString().getBytes();
   }
}