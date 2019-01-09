package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.File;
import java.io.FileWriter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ws.rs.Path;


@Path("/ITest")
public class XOPMultipartProxyResource implements XOPMultipartProxy {

   @Override
   public XOPMultipartProxyGetFileRestResponse getFileXOPMulti(XOPMultipartProxyGetFileRequest request) throws Exception {
      return getResponse(request.getFileName());
   }

   private XOPMultipartProxyGetFileRestResponse getResponse(String content) throws Exception {
      XOPMultipartProxyGetFileRestResponse response = new XOPMultipartProxyGetFileRestResponse();
      File out = File.createTempFile("tmp", ".txt");
      try (FileWriter writer = new FileWriter(out)) {
         writer.write(content);
         DataSource fds = new FileDataSource(out);
         DataHandler handler = new DataHandler(fds);
         response.setData(handler);
      }
      return response;
   }
}
