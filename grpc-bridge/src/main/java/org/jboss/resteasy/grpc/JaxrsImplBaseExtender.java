package org.jboss.resteasy.grpc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.jboss.logging.Logger;

public class JaxrsImplBaseExtender {

   private static Logger logger = Logger.getLogger(JaxrsImplBaseExtender.class);

   private boolean inWildFly = true;
   private String packageName = "";
   private String outerClassName = "";
   private String serviceName = "";
   private String servletName = "";
   private Set<String> imports = new HashSet<String>();

   public static void main(String[] args) {
      if (args == null || (args.length != 3 && args.length != 4)) {
         logger.info("need three or four args:");
         logger.info("  arg[0]: .proto file prefix");
         logger.info("  arg[1]: servlet name");
         logger.info("  arg[2]: package of generated sources");
         logger.info("  arg[3]: in WildFly (optional)");
         return;
      }
      new JaxrsImplBaseExtender(args);
   }

   public JaxrsImplBaseExtender(final String[] args) {
      servletName = args[1];
      if (args != null && args.length == 4) {
         inWildFly = !"false".equals(args[3]);
      }
      parse(args[0], args[2]);
   }

   private void parse(String root, String pkg) {
      File file = new File("./src/main/proto/" + root + ".proto");
      if (!file.exists()) {
         throw new RuntimeException(root + ".proto not found");
      }
      try {
         StringBuilder sbHeader = new StringBuilder();
         StringBuilder sbBody = new StringBuilder();
         Reader reader = new FileReader(file);
         Scanner scanner = new Scanner(reader);
         classHeader(scanner, sbHeader, root);
         String s = scanner.findWithinHorizon("service ", 0);
         while (s != null) {
            serviceName = scanner.next();
            sbHeader.append("import ")
                    .append(packageName).append(".")
                    .append(serviceName).append("Grpc").append(".")
                    .append(serviceName).append("ImplBase;\n");
            service(scanner, sbHeader, sbBody, root);
            s = scanner.findWithinHorizon("service ", 0);
         }
         sbHeader.append("\n");
         nonStaticMethods(sbBody);
         staticMethods(sbBody, root, pkg);
         sbBody.append("}\n");
         writeClass(sbHeader, sbBody);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private void classHeader(Scanner scanner, StringBuilder sb, String fileName) {
      String pkg = null;
      String s = scanner.findWithinHorizon("java_package", 0);
      if (s != null) {
         scanner.findWithinHorizon("\"", 0);
         scanner.useDelimiter("[ \"]");
         pkg = scanner.next();
      } else {
         s = scanner.findWithinHorizon("package", 0);
         if (s != null) {
            scanner.useDelimiter("[ ;]");
            pkg = scanner.next();
         }
      }
      sb.append("package ").append(pkg).append(";\n\n");
      packageName = pkg;
      s = scanner.findWithinHorizon("java_outer_classname", 0);
      if (s != null) {
         s = scanner.findWithinHorizon("=", 0);
         s = scanner.findWithinHorizon("\"", 0);
         outerClassName = scanner.next();
      }
      imports(scanner, sb, fileName);
      scanner.reset();
   }

   private void imports(Scanner scanner, StringBuilder sb, String fileName) {
      sb.append("import com.google.protobuf.ByteString;\n")
        .append("import com.google.protobuf.Descriptors.FieldDescriptor;\n")
        .append("import com.google.protobuf.GeneratedMessageV3;\n")
        .append("import com.google.protobuf.Message;\n")
        .append("import com.google.protobuf.Timestamp;\n")
        .append("import io.grpc.stub.StreamObserver;\n")
        .append("import java.io.ByteArrayInputStream;\n")
        .append("import java.io.ByteArrayOutputStream;\n")
        .append("import java.io.IOException;\n")
        .append("import java.io.InputStream;\n")
        .append("import java.text.DateFormat;\n")
        .append("import java.text.ParseException;\n")
        .append("import java.time.ZonedDateTime;\n")
        .append("import java.time.format.DateTimeFormatter;\n")
        .append("import java.util.ArrayList;\n")
        .append("import java.util.Collection;\n")
        .append("import java.util.concurrent.ExecutorService;\n")
        .append("import java.util.HashMap;\n")
        .append("import java.util.Iterator;\n")
        .append("import java.util.List;\n")
        .append("import java.util.Map;\n")
        .append("import jakarta.enterprise.context.control.RequestContextController;\n")
        .append("import jakarta.ws.rs.RuntimeType;\n")
        .append("import jakarta.ws.rs.core.MediaType;\n")
        .append("import jakarta.servlet.Servlet;\n")
        .append("import jakarta.servlet.ServletConfig;\n")
        .append("import jakarta.servlet.ServletContext;\n")
        .append("import jakarta.servlet.http.Cookie;\n")
        .append("import jakarta.servlet.http.HttpServletRequest;\n")
        .append("import jakarta.servlet.http.HttpServletResponse;\n")
        .append("import org.jboss.resteasy.core.ResteasyContext;\n")
        .append("import org.jboss.resteasy.core.SynchronousDispatcher;\n")
        .append("import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;\n")
//        .append("import org.jboss.resteasy.plugins.grpc.sse.SseEvent;\n")
        .append("import org.jboss.resteasy.grpc.runtime.servlet.AsyncContextImpl;\n")
        .append("import org.jboss.resteasy.grpc.runtime.servlet.AsyncMockServletOutputStream;\n")
        .append("import org.jboss.resteasy.grpc.runtime.servlet.GrpcHttpServletDispatcher;\n")
        .append("import org.jboss.resteasy.grpc.runtime.servlet.HttpServletRequestImpl;\n")
        .append("import org.jboss.resteasy.grpc.runtime.servlet.HttpServletResponseImpl;\n")
        .append("import org.jboss.resteasy.grpc.runtime.servlet.MockServletInputStream;\n")
        .append("import org.jboss.resteasy.grpc.runtime.servlet.MockServletOutputStream;\n")
        .append("import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;\n")
        .append("import org.jboss.resteasy.plugins.providers.sse.SseEventInputImpl;\n")
        .append("import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;\n")
        .append("import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;\n")
        .append("import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;\n")
        .append("import org.jboss.resteasy.spi.Dispatcher;\n")
        .append("import org.jboss.resteasy.spi.ResteasyProviderFactory;\n")
        .append("import org.wildfly.grpc.GrpcService;\n")
        .append("import javax.inject.Inject;\n")
        .append("import jakarta.enterprise.inject.spi.BeanManager;\n")
        .append("import jakarta.enterprise.inject.spi.CDI;\n")
        .append("import jakarta.enterprise.context.RequestScoped;\n")
        .append("import jakarta.enterprise.context.ContextNotActiveException;\n")
        .append("import com.google.protobuf.Any;\n")
        .append("import org.jboss.resteasy.grpc.server.").append(fileName).append("_Server;\n")
        .append("import ").append(packageName).append(".").append(outerClassName).append(".gInteger;\n")
        .append("import ").append(packageName).append(".").append(outerClassName).append(".gHeader;\n")
        .append("import ").append(packageName).append(".").append(outerClassName).append(".gCookie;\n")
        .append("import ").append(packageName).append(".").append(outerClassName).append(".gNewCookie;\n")
        .append("import ").append(packageName).append(".").append(outerClassName).append(".gHeader;\n")
        .append("import ").append(packageName).append(".").append(outerClassName).append(".FormValues;\n")
        .append("import ").append(packageName).append(".").append(outerClassName).append(".GeneralEntityMessage;\n")
        .append("import ").append(packageName).append(".").append(outerClassName).append(".GeneralReturnMessage;\n")
        ;
   }

   private void service(Scanner scanner, StringBuilder sbHeader, StringBuilder sbBody, String root) {
     if (inWildFly) {
        sbBody.append("@GrpcService\n");
     }
      sbBody.append("public class ")
            .append(serviceName)
            .append("GrpcImpl extends ")
            .append(serviceName)
            .append("ImplBase {\n\n")
            .append("   private static ").append(root).append("_proto.gString.Builder builder = ").append(root).append("_proto.gString.newBuilder();\n")
            .append("   private static FieldDescriptor fd = builder.getDescriptorForType().getFields().iterator().next();\n")
            .append("   private HttpServletDispatcher servlet;\n")
            .append("   @Inject private RequestContextController requestContextController;\n")
            ;
      scanner.nextLine();
      scanner.skip("//");
      String path = scanner.next();
      String actualEntityClass = scanner.next();
      String actualReturnClass = scanner.next();
      if ("google.protobuf.Any".equals(actualReturnClass)) {
         actualReturnClass = "Any";
      }
      String httpMethod = scanner.next();
      String syncType = scanner.next();
      String rpc = scanner.findWithinHorizon(" rpc ", 0);
      while (rpc != null) {
         rpc(scanner, root, "/" + path, actualEntityClass, actualReturnClass, httpMethod, syncType, sbHeader, sbBody);
         scanner.nextLine();
         if (!scanner.hasNext("//")) {
            break;
         }
         scanner.skip("//");
         path = scanner.next();
         actualEntityClass = scanner.next();
         actualReturnClass = scanner.next();
         if ("google.protobuf.Any".equals(actualReturnClass)) {
           actualReturnClass = "Any";
         }
         httpMethod = scanner.next();
         syncType = scanner.next();
         rpc = scanner.findWithinHorizon(" rpc ", 0);
      }
   }

   private void rpc(Scanner scanner, String root, String path, String actualEntityClass, String actualReturnClass, String httpMethod, String syncType, StringBuilder sbHeader, StringBuilder sbBody) {
      sbBody.append("\n   @java.lang.Override\n");
      String method = scanner.next();
      scanner.findWithinHorizon("\\(", 0);
      scanner.useDelimiter("\\)");
      String param = getParamType(packageName, outerClassName, scanner.next());
      if (!imports.contains(actualEntityClass) && !"Any".equals(actualEntityClass)) {
         sbHeader.append("import " + packageName + "." + outerClassName + "." + actualEntityClass + ";\n");
         imports.add(actualEntityClass);
      }
      if (!imports.contains(actualReturnClass) && !"Any".equals(actualReturnClass)) {
          sbHeader.append("import " + packageName + "." + outerClassName + "." + actualReturnClass + ";\n");
          imports.add(actualReturnClass);
       }
      scanner.findWithinHorizon("returns", 0);
      scanner.findWithinHorizon("\\(", 0);
      String retn = getReturnType(packageName, outerClassName, scanner.next());
      sbBody.append("   public void ")
            .append(method).append("(")
            .append(param).append(" param, ")
            .append("StreamObserver<").append(retn).append("> responseObserver) {\n");
      rpcBody(scanner, root, path, actualEntityClass, actualReturnClass, httpMethod, syncType, sbBody, retn);
      sbBody.append("   }\n");
      scanner.reset();
   }

   private void rpcBody(Scanner scanner, String root, String path, String actualEntityClass, String actualReturnClass, String method, String syncType, StringBuilder sb, String retn) {
      sb.append("      HttpServletRequest request = null;\n")
        .append("      try {\n")
        .append("         HttpServletResponseImpl response = new HttpServletResponseImpl(\"").append(actualReturnClass).append("\", \"").append(syncType).append("\", ").append(root).append("_Server.getContext(), builder, fd);\n")
        .append("         GeneratedMessageV3 actualParam = param.").append(getGetterMethod(actualEntityClass)).append(";\n")
        .append("         request = getHttpServletRequest(param, actualParam, \"").append(path).append("\", response, ").append("\"").append(method).append("\", \"").append(retn).append("\");\n")
        .append("         HttpServletDispatcher servlet = getServlet();\n")
        .append("         activateRequestContext();\n")
        .append("         servlet.service(\"").append(method).append("\", request, response);\n");
      if ("suspended".equals(syncType)) {
         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();\n")
           .append("         amsos.await();\n")
           .append("         ByteArrayOutputStream baos = amsos.getDelegate();\n")
           .append("         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());\n")
           .append("         Any reply = Any.parseFrom(bais);\n")
           .append("         ").append(retn).append(".Builder grmb = createGeneralReturnMessageBuilder(response);\n")
           .append("         ").append(getSetterMethod(actualReturnClass)).append("(reply);\n")
           .append("         responseObserver.onNext(grmb.build());\n");
      } else if ("completionStage".equals(syncType)) {
         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();\n")
           .append("         amsos.await();\n")
           .append("         ByteArrayOutputStream baos = amsos.getDelegate();\n")
           .append("         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());\n")
           .append("         ").append(actualReturnClass).append(" reply = ").append(actualReturnClass).append(".parseFrom(bais);\n")
           .append("         ").append(retn).append(".Builder grmb = createGeneralReturnMessageBuilder(response);\n")
           .append("         ").append(getSetterMethod(actualReturnClass)).append("(reply);\n")
           .append("         responseObserver.onNext(grmb.build());\n");
      }

      else if ("sse".equals(syncType)) {
//         sb.append("         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();\n")
//           .append("         ByteArrayOutputStream baos = msos.getDelegate();\n")
//           .append("         bais = new ByteArrayInputStream(baos.toByteArray());\n")
//           .append("         bais.mark(0);\n")
//           .append("         while (bais.read() != -1) {\n")
//           .append("            bais.reset();\n")
//           .append("            responseObserver.onNext(transformSseEvent(bais));\n")
//           .append("            bais.mark(0);\n")
//           .append("         }\n")
//           ;
         // temporary
//         sb.append("         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();\n")
//         .append("         ByteArrayOutputStream baos = msos.getDelegate();\n");
//
//         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();\n")
//           .append("         while (true) {\n")
//           .append("            ByteArrayOutputStream baos = amsos.await();\n")
//           .append("            if (amsos.isClosed()) {\n")
//           .append("               break;\n")
//           .append("            }\n")
//           .append("            byte[] bytes = baos.toByteArray();\n")
//           .append("            if (bytes.length == 2 && bytes[0] == 10 && bytes[1] == 10) {\n")
//           .append("               continue;\n")
//           .append("            }\n")
//           .append("            try {\n")
//           .append("               org_jboss_resteasy_plugins_protobuf_sse___SseEvent reply = org_jboss_resteasy_plugins_protobuf_sse___SseEvent.parseFrom(bytes);\n")
//           .append("               responseObserver.onNext(reply);\n")
//           .append("            } catch (Exception e) {\n")
//           .append("               continue;\n")
//           .append("            }\n")
//           .append("         }\n");
      } else {
         sb.append("         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();\n")
           .append("         ByteArrayOutputStream baos = msos.getDelegate();\n")
           .append("         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());\n")
           .append("         ").append(actualReturnClass).append(" reply = ").append(actualReturnClass).append(".parseFrom(bais);\n")
           .append("         ").append(retn).append(".Builder grmb = createGeneralReturnMessageBuilder(response);\n")
           .append("         ").append(getSetterMethod(actualReturnClass)).append("(reply);\n")
           .append("         responseObserver.onNext(grmb.build());\n");
      }
      sb.append("      } catch (Exception e) {\n")
        .append("         responseObserver.onError(e);\n")
        .append("      } finally {\n")
        .append("         responseObserver.onCompleted();\n")
        .append("         if (requestContextController != null) {\n")
        .append("            requestContextController.deactivate();\n")
        .append("         }\n")
        .append("      }\n");
   }

   private void nonStaticMethods(StringBuilder sb) {
      sb.append("\n")
        .append("//=============================  non-static methods =============================\n")
        .append("   private void activateRequestContext() {\n")
        .append("      if (requestContextController == null) {\n")
        .append("         requestContextController = CDI.current().select(RequestContextController.class).get();\n")
        .append("      }\n")
        .append("      requestContextController.activate();\n")
        .append("   }\n")
      ;
   }
   private void staticMethods(StringBuilder sb, String root, String pkg) {
      sb.append("\n")
        .append("//=============================  static methods =============================\n")
        .append("   private HttpServletDispatcher getServlet() throws Exception {\n")
        .append("      if (servlet == null) {\n")
        .append("         synchronized(this) {\n")
        .append("            if (servlet != null) {\n")
        .append("               return servlet;\n")
        .append("            }\n")
        .append("            servlet = (HttpServletDispatcher) GrpcHttpServletDispatcher.getServlet(\"").append(servletName).append("\");\n")
        .append("         }\n")
        .append("      }\n")
        .append("      return servlet;\n")
        .append("   }\n\n")
        ;
      sb.append("   private static Map<String, List<String>> convertHeaders(Map<String, ").append(pkg).append(".").append(root).append("_proto.gHeader> protoHeaders) {\n")
        .append("      Map<String, List<String>> headers = new HashMap<String, List<String>>();\n")
        .append("      for (Map.Entry<String, ").append(pkg).append(".").append(root).append("_proto.gHeader> entry : protoHeaders.entrySet()) {\n")
        .append("         String key = entry.getKey();\n")
        .append("         ").append(pkg).append(".").append(root).append("_proto.gHeader protoHeader = entry.getValue();\n")
        .append("         List<String> values = new ArrayList<String>();\n")
        .append("         for (int i = 0; i < protoHeader.getValuesCount(); i++) {\n")
        .append("            values.add(protoHeader.getValues(i));\n")
        .append("         }\n")
        .append("         headers.put(key, values);\n")
        .append("      }\n")
        .append("      return headers;\n")
        .append("   }\n\n")
        ;
      sb.append("   private static HttpServletRequest getHttpServletRequest(").append(pkg).append(".").append(root).append("_proto.GeneralEntityMessage param, GeneratedMessageV3 actualParam, String path, HttpServletResponse response, String verb, String type) throws Exception {\n")
        .append("      String url = param.getURL() == \"\" ? \"http://localhost:8080\" + path : param.getURL();\n")
        .append("      ByteArrayInputStream bais = new ByteArrayInputStream(actualParam.toByteArray());\n")
        .append("      MockServletInputStream msis = new MockServletInputStream(bais);\n")
        .append("      Map<String, List<String>> headers = convertHeaders(param.getHeadersMap());\n")
        .append("      Cookie[] cookies = convertCookies(param.getCookiesList());\n")
        .append("//      ServletContext servletContext = org.jboss.resteasy.grpc.servlet.GrpcHttpServletDispatcher.getServletContext(\"").append(root).append("Servlet\");\n")
        .append("      ServletContext servletContext = ").append(root).append("_Server.getServletContext();\n")
        .append("      HttpServletRequestImpl request = new HttpServletRequestImpl();\n")
        .append("      request.setServletResponse(response);\n")
        .append("      request.setServletContext(servletContext);\n")
        .append("      request.setUri(url);\n")
        .append("      request.setPath(path);\n")
        .append("      request.setContextPath(servletContext.getContextPath());\n")
        .append("      request.setMethod(verb);\n")
        .append("      request.setInputStream(msis);\n")
        .append("      request.setReturnType(type);\n")
        .append("      request.setHeaders(headers);\n")
        .append("      request.setCookies(cookies);\n")
        .append("      request.setFormParameters(extractFormData(param));\n")
        .append("      ").append(pkg).append(".").append(root).append("_proto.ServletInfo servletInfo = param.getServletInfo();\n")
        .append("      if (servletInfo != null) {\n")
        .append("         if (servletInfo.getCharacterEncoding() != null) {\n")
        .append("            request.setCharacterEncoding(servletInfo.getCharacterEncoding());\n")
        .append("         }\n")
        .append("         if (servletInfo.getClientAddress() != null) {\n")
        .append("            request.setRemoteAddr(servletInfo.getClientAddress());\n")
        .append("         }\n")
        .append("         if (servletInfo.getClientHost() != null) {\n")
        .append("            request.setRemoteHost(servletInfo.getClientHost());\n")
        .append("         }\n")
        .append("         if (servletInfo.getClientPort() != -1) {\n")
        .append("            request.setRemotePort(servletInfo.getClientPort());\n")
        .append("         }\n")
        .append("      }\n")
        .append("      return request;\n")
        .append("   }\n\n")
        ;
      sb.append("   private static jakarta.servlet.http.Cookie[] convertCookies(List<").append(pkg).append(".").append(root).append("_proto.gCookie> cookieList) {\n")
        .append("      jakarta.servlet.http.Cookie[] cookieArray = new jakarta.servlet.http.Cookie[cookieList.size()];\n")
        .append("      int i = 0;\n")
        .append("      for (Iterator<").append(pkg).append(".").append(root).append("_proto.gCookie> it = cookieList.iterator(); it.hasNext(); ) {\n")
        .append("         ").append(pkg).append(".").append(root).append("_proto.gCookie protoCookie = it.next();\n")
        .append("         jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(protoCookie.getName(), protoCookie.getValue());\n")
        .append("         cookie.setVersion(protoCookie.getVersion());\n")
        .append("         cookie.setPath(protoCookie.getPath());\n")
        .append("         cookie.setDomain(protoCookie.getDomain());\n")
//        .append("         cookie.setVersion(protoCookie.getVersion());\n")
        .append("         cookieArray[i++] = cookie;\n")
        .append("      }\n")
        .append("      return cookieArray;\n")
        .append("   }\n\n")
        ;
      sb.append("   private static Map<String, String[]> extractFormData(GeneralEntityMessage param) {\n")
        .append("      if (!param.hasFormField()) {\n")
        .append("         return null;\n")
        .append("      }\n")
        .append("      Map<String, String[]> formParams = new HashMap<String, String[]>();\n")
        .append("      Map<String, FormValues> map = param.getFormField().getFormMapFieldMap();\n")
        .append("      for (Map.Entry<String, FormValues> entry : map.entrySet()) {\n")
        .append("         String[] values = new String[entry.getValue().getFormValuesFieldCount()];\n")
        .append("         for (int i = 0; i < entry.getValue().getFormValuesFieldCount(); i++) {\n")
        .append("            values[i] = entry.getValue().getFormValuesField(i);\n")
        .append("         }\n")
        .append("         formParams.put(entry.getKey(), values);\n")
        .append("      }\n")
        .append("      return formParams;\n")
        .append("   }\n\n")
        ;
      sb.append("   private static GeneralReturnMessage.Builder createGeneralReturnMessageBuilder(HttpServletResponseImpl response) throws ParseException {\n")
        .append("      GeneralReturnMessage.Builder grmBuilder = GeneralReturnMessage.newBuilder();\n")
        .append("      gNewCookie.Builder cookieBuilder = gNewCookie.newBuilder();\n")
        .append("      if (!response.getHeaderNames().isEmpty()) {\n")
        .append("         gHeader.Builder headerBuilder = gHeader.newBuilder();\n")
        .append("         for (String headerName : response.getHeaderNames()) {\n")
        .append("            if (\"Set-Cookie\".equals(headerName)) {\n")
        .append("               Collection<String> cookies = response.getHeaders(\"Set-Cookie\");\n")
        .append("               for (String s : cookies) {\n")
        .append("                  grmBuilder.addCookies(parseNewCookie(cookieBuilder, s));\n")
        .append("                  cookieBuilder.clear();\n")
        .append("               }\n")
        .append("            } else {\n")
        .append("               for (String value : response.getHeaders(headerName)) {\n")
        .append("                   headerBuilder.addValues(value);\n")
        .append("               }\n")
        .append("            grmBuilder.putHeaders(headerName, headerBuilder.build());\n")
        .append("            headerBuilder.clear();\n")
        .append("            }\n")
        .append("         }\n")
        .append("      }\n")
        .append("      if (!response.getCookies().isEmpty()) {\n")
        .append("         for (Cookie cookie : response.getCookies()) {\n")
        .append("            cookieBuilder\n")
        .append("               .setMaxAge(cookie.getMaxAge())\n")
        .append("               .setVersion(cookie.getVersion())\n")
        .append("               ;\n")
        .append("            if (cookie.getComment() != null) {\n")
        .append("               cookieBuilder.setComment(cookie.getComment());\n")
        .append("            }\n")
        .append("            if (cookie.getDomain() != null) {\n")
        .append("               cookieBuilder.setDomain(cookie.getDomain());\n")
        .append("            }\n")
        .append("            if (cookie.getName() != null) {\n")
        .append("               cookieBuilder.setName(cookie.getName());\n")
        .append("            }\n")
        .append("            if (cookie.getPath() != null) {\n")
        .append("               cookieBuilder.setPath(cookie.getPath());\n")
        .append("            }\n")
        .append("            if (cookie.getValue() != null) {\n")
        .append("               cookieBuilder.setValue(cookie.getValue());\n")
        .append("            }\n")
        .append("            if (cookie.getSecure()) {\n")
        .append("               cookieBuilder.setSecure(true);\n")
        .append("            }\n")
        .append("            if (cookie.isHttpOnly()) {\n")
        .append("               cookieBuilder.setHttpOnly(true);\n")
        .append("            }\n")
        .append("            grmBuilder.addCookies(cookieBuilder.build());\n")
        .append("            cookieBuilder.clear();\n")
        .append("         }\n")
        .append("      }\n")
        .append("      grmBuilder.setStatus(gInteger.newBuilder().setValue(response.getStatus()).build());\n")
        .append("      return grmBuilder;\n")
        .append("   }\n\n")
        ;
      sb.append("   private static gNewCookie parseNewCookie(gNewCookie.Builder ncb, String s) throws ParseException {\n")
        .append("      String[] fields = s.split(\";\");\n")
        .append("      for (String field : fields) {\n")
        .append("         String[] subfields = field.split(\"=\");\n")
        .append("         switch (subfields[0].strip()) {\n")
        .append("            case \"Domain\":\n")
        .append("               ncb.setDomain(subfields[1].trim());\n")
        .append("               break;\n")
        .append("            case \"Path\":\n")
        .append("               ncb.setPath(subfields[1].trim());\n")
        .append("               break;\n")
        .append("            case \"Version\":\n")
        .append("               ncb.setVersion(Integer.valueOf(subfields[1].trim()));\n")
        .append("               break;\n")
        .append("            case \"Comment\":\n")
        .append("               ncb.setComment(subfields[1].trim());\n")
        .append("               break;\n")
        .append("            case \"Expires\":\n")
        .append("               DateTimeFormatter formatter = DateTimeFormatter.ofPattern(\"EEE, DD-MMM-yyyy HH:mm:ss zzz\");\n")
        .append("               ZonedDateTime zdt = ZonedDateTime.parse(subfields[1].trim(), formatter);\n")
        .append("               ncb.setExpiry(Timestamp.newBuilder().setSeconds(zdt.toEpochSecond()));\n")
        .append("               break;\n")
        .append("            case \"HttpOnly\":\n")
        .append("               ncb.setHttpOnly(true);\n")
        .append("               break;\n")
        .append("            case \"Max-Age\":\n")
        .append("               ncb.setMaxAge(Integer.valueOf(subfields[1].trim()));\n")
        .append("               break;\n")
        .append("            case \"SameSite\":\n")
        .append("               ncb.setSameSite(gNewCookie.SameSite.valueOf(subfields[1].trim()));\n")
        .append("               break;\n")
        .append("            case \"Secure\":\n")
        .append("               ncb.setSecure(true);\n")
        .append("               break;\n")
        .append("            default:\n")
        .append("               ncb.setName(subfields[0].trim());\n")
        .append("               ncb.setValue(subfields[1].trim());\n")
        .append("         }\n")
        .append("      }\n")
        .append("      return ncb.build();\n")
        .append("   }\n")
        ;
        //        .append("   private static org_jboss_resteasy_plugins_grpc_sse___SseEvent transformSseEvent(ByteArrayInputStream bais) throws IOException {\n")
//        .append("      SseEventInputImpl eventInput = new SseEventInputImpl(null, MediaType.TEXT_PLAIN_TYPE, null, null, bais);\n")
//        .append("      InboundSseEventImpl inboundEvent = (InboundSseEventImpl) eventInput.read();\n")
//        .append("      org_jboss_resteasy_plugins_grpc_sse___SseEvent.Builder builder = org_jboss_resteasy_plugins_grpc_sse___SseEvent.newBuilder();\n")
//        .append("      builder.setComment(inboundEvent.getComment());\n")
//        .append("      builder.setData(ByteString.copyFrom(inboundEvent.getRawData()));\n")
//        .append("      builder.setId(inboundEvent.getId());\n")
//        .append("      builder.setName(inboundEvent.getName());\n")
//        .append("      builder.setReconnectDelay(inboundEvent.getReconnectDelay());\n")
//        .append("      return builder.build();\n")
//        .append("   }\n")
//        ;
   }

   private static String getParamType(String packageName, String outerClassName, String param) {
      return packageName + "." + outerClassName + "." + param;
   }

   private static String getReturnType(String packageName, String outerClassName, String param) {
      int pos = param.indexOf("stream");
      if (pos >= 0) {
         param = param.substring(pos + 6).stripLeading();
      }
      if ("google.protobuf.Any".equals(param)) {
         return "com.google.protobuf.Any";
      }
      return packageName + "." + outerClassName + "." + param;
   }

   private String getGetterMethod(String actualEntityClass) {
      actualEntityClass = actualEntityClass.replaceAll("___", "_");
      StringBuilder sb = new StringBuilder("get");
      sb.append(actualEntityClass.substring(0, 1).toUpperCase());
      for (int i = 1; i < actualEntityClass.length(); ) {
         if ("_".equals(actualEntityClass.substring(i, i + 1))) {
            sb.append(actualEntityClass.substring(i + 1, i + 2).toUpperCase());
            i += 2;
         } else {
            sb.append(actualEntityClass.charAt(i++));
         }
      }
      sb.append("Field()");
      return sb.toString();
   }

   private String getSetterMethod(String actualReturnClass) {
      if ("com.google.protobuf.Any".equals(actualReturnClass) || "Any".equals(actualReturnClass)) {
         return "grmb.setGoogleProtobufAnyField";
      }
     if (actualReturnClass.contains("___")) {
        return "grmb.set" + camelize(actualReturnClass) + "Field";
     }
      return "grmb.set" + actualReturnClass.substring(0, 1).toUpperCase() + actualReturnClass.substring(1) + "Field";
   }

   private static String camelize(String s) {
      boolean sawUnderScore = false;
      StringBuilder sb = new StringBuilder();
      sb.append(Character.toUpperCase(s.charAt(0)));
      for (int i = 1; i < s.length(); i++) {
         if (s.charAt(i) == '_') {
            sawUnderScore = true;
            continue;
         }
         if (sawUnderScore) {
            sb.append(Character.toUpperCase(s.charAt(i)));
            sawUnderScore = false;
         } else {
            sb.append(s.charAt(i));
         }
      }
      return sb.toString();
   }

   private void writeClass(StringBuilder sbHeader, StringBuilder sbBody) throws IOException {
      String path = "";
      for (String s : ("target/generated-sources/protobuf/grpc-java/" + packageName.replace(".", "/")).split("/")) {
         path += s;
         File dir = new File(path);
         if(!dir.exists()){
            dir.mkdir();
         }
         path += "/";
      }
      File file = new File(path + serviceName + "GrpcImpl.java");
      if (file.exists()) {
         return;
      }
      file.createNewFile();
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sbHeader.toString());
      bw.write(sbBody.toString());
      bw.close();
   }
}
