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
import org.jboss.resteasy.grpc.runtime.servlet.HttpServletRequestImpl;

public class ServiceGrpcExtender {

   private static final Logger logger = Logger.getLogger(ServiceGrpcExtender.class);
   private static final String LS = System.lineSeparator();
   private static final String SSE_EVENT_CLASSNAME = "org_jboss_resteasy_grpc_sse_runtime___SseEvent";

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
      new ServiceGrpcExtender(args);
   }

   public ServiceGrpcExtender(final String[] args) {
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
                    .append(serviceName).append("ImplBase;" + LS);
            service(scanner, sbHeader, sbBody, root);
            s = scanner.findWithinHorizon("service ", 0);
         }
         sbHeader.append("" + LS);
         nonStaticMethods(sbBody);
         staticMethods(sbBody, root, pkg);
         sbBody.append("}" + LS);
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
      sb.append("package ").append(pkg).append(";" + LS + LS);
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
      sb.append("import com.google.protobuf.Descriptors.FieldDescriptor;" + LS)
        .append("import com.google.protobuf.GeneratedMessageV3;" + LS)
        .append("import com.google.protobuf.Timestamp;" + LS)
        .append("import io.grpc.stub.StreamObserver;" + LS)
        .append("import java.io.ByteArrayInputStream;" + LS)
        .append("import java.io.ByteArrayOutputStream;" + LS)
        .append("import java.text.ParseException;" + LS)
        .append("import java.time.ZonedDateTime;" + LS)
        .append("import java.time.format.DateTimeFormatter;" + LS)
        .append("import java.util.ArrayList;" + LS)
        .append("import java.util.Collection;" + LS)
        .append("import java.util.HashMap;" + LS)
        .append("import java.util.Iterator;" + LS)
        .append("import java.util.List;" + LS)
        .append("import java.util.Map;" + LS)
        .append("import jakarta.enterprise.context.control.RequestContextController;" + LS)
        .append("import jakarta.servlet.ServletContext;" + LS)
        .append("import jakarta.servlet.http.Cookie;" + LS)
        .append("import jakarta.servlet.http.HttpServletRequest;" + LS)
        .append("import jakarta.servlet.http.HttpServletResponse;" + LS)
        .append("import org.jboss.resteasy.grpc.runtime.servlet.AsyncMockServletOutputStream;" + LS)
        .append("import org.jboss.resteasy.grpc.runtime.servlet.GrpcHttpServletDispatcher;" + LS)
        .append("import org.jboss.resteasy.grpc.runtime.servlet.HttpServletRequestImpl;" + LS)
        .append("import org.jboss.resteasy.grpc.runtime.servlet.HttpServletResponseImpl;" + LS)
        .append("import org.jboss.resteasy.grpc.runtime.servlet.MockServletInputStream;" + LS)
        .append("import org.jboss.resteasy.grpc.runtime.servlet.MockServletOutputStream;" + LS)
        .append("import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;" + LS)
        .append("import org.wildfly.grpc.GrpcService;" + LS)
        .append("import jakarta.inject.Inject;" + LS)
        .append("import jakarta.enterprise.inject.spi.CDI;" + LS)
        .append("import com.google.protobuf.Any;" + LS)
        .append("import org.jboss.resteasy.grpc.server.").append(fileName).append("_Server;" + LS)
        .append("import ").append(packageName).append(".").append(outerClassName).append(".gInteger;" + LS)
        .append("import ").append(packageName).append(".").append(outerClassName).append(".gNewCookie;" + LS)
        .append("import ").append(packageName).append(".").append(outerClassName).append(".gHeader;" + LS)
        .append("import ").append(packageName).append(".").append(outerClassName).append(".FormValues;" + LS)
        .append("import ").append(packageName).append(".").append(outerClassName).append(".GeneralEntityMessage;" + LS)
        .append("import ").append(packageName).append(".").append(outerClassName).append(".GeneralReturnMessage;" + LS)
        ;
   }

   private void service(Scanner scanner, StringBuilder sbHeader, StringBuilder sbBody, String root) {
     if (inWildFly) {
        sbBody.append("@GrpcService" + LS);
     }
      sbBody.append("public class ")
            .append(serviceName)
            .append("GrpcImpl extends ")
            .append(serviceName)
            .append("ImplBase {" + LS + LS)
            .append("   private static ").append(root).append("_proto.gString.Builder builder = ").append(root).append("_proto.gString.newBuilder();" + LS)
            .append("   private static FieldDescriptor fd = builder.getDescriptorForType().getFields().iterator().next();" + LS)
            .append("   private HttpServletDispatcher servlet;" + LS)
            .append("   @Inject private RequestContextController requestContextController;" + LS)
            ;
      scanner.nextLine();
      scanner.skip("//");
      String path = scanner.next();
      String actualEntityClass = scanner.next();
      String actualReturnClass = scanner.next();
      String httpMethod = scanner.next();
      if (HttpServletRequestImpl.LOCATOR.equals(httpMethod)) {
         actualEntityClass = "google.protobuf.Any";
      }
      if ("google.protobuf.Any".equals(actualReturnClass)) {
         actualReturnClass = "Any";
      }
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
         if (HttpServletRequestImpl.LOCATOR.equals(httpMethod)) {
            actualEntityClass = "google.protobuf.Any";
         }
         if ("google.protobuf.Any".equals(actualReturnClass)) {
            actualReturnClass = "Any";
         }
         httpMethod = scanner.next();
         syncType = scanner.next();
         rpc = scanner.findWithinHorizon(" rpc ", 0);
      }
   }

   private void rpc(Scanner scanner, String root, String path, String actualEntityClass, String actualReturnClass, String httpMethod, String syncType, StringBuilder sbHeader, StringBuilder sbBody) {
      sbBody.append(LS + "   @java.lang.Override" + LS);
      String method = scanner.next();
      scanner.findWithinHorizon("\\(", 0);
      scanner.useDelimiter("\\)");
      String param = getParamType(packageName, outerClassName, scanner.next());
      if (!imports.contains(actualEntityClass)) {
         if (!"Any".equals(actualEntityClass)
               && !"google.protobuf.Any".equals(actualEntityClass)
               && !"gInteger".equals(actualEntityClass)
               && !"gEmpty".equals(actualEntityClass)) {
            sbHeader.append("import " + packageName + "." + outerClassName + "." + actualEntityClass + ";" + LS);
            imports.add(actualEntityClass);
         }
      }
      if (!imports.contains(actualReturnClass)) {
         if (!"Any".equals(actualReturnClass)
               && !"google.protobuf.Any".equals(actualReturnClass)
               && !"gInteger".equals(actualReturnClass)
               && !"gEmpty".equals(actualReturnClass)) {
            sbHeader.append("import " + packageName + "." + outerClassName + "." + actualReturnClass + ";" + LS);
            imports.add(actualReturnClass);
         }
      }
      scanner.findWithinHorizon("returns", 0);
      scanner.findWithinHorizon("\\(", 0);
      String retn = getReturnType(packageName, outerClassName, scanner.next());
      sbBody.append("   public void ")
            .append(method).append("(")
            .append(param).append(" param, ")
            .append("StreamObserver<").append(retn).append("> responseObserver) {" + LS);
      rpcBody(scanner, root, path, actualEntityClass, actualReturnClass, httpMethod, syncType, sbBody, retn);
      sbBody.append("   }" + LS);
      scanner.reset();
   }

   private void rpcBody(Scanner scanner, String root, String path, String actualEntityClass, String actualReturnClass, String method, String syncType, StringBuilder sb, String retn) {
      sb.append("      HttpServletRequest request = null;" + LS)
        .append("      try {" + LS)
        .append("         HttpServletResponseImpl response = new HttpServletResponseImpl(\"").append(actualReturnClass).append("\", \"").append(syncType).append("\", ").append(root).append("_Server.getContext(), builder, fd);" + LS)
        .append("         GeneratedMessageV3 actualParam = param.").append(getGetterMethod(actualEntityClass)).append(";" + LS)
        .append("         request = getHttpServletRequest(param, actualParam, \"").append(path).append("\", response, ").append("\"").append(method).append("\", \"").append(actualReturnClass).append("\");" + LS)
        .append("         HttpServletDispatcher servlet = getServlet();" + LS)
        .append("         activateRequestContext();" + LS)
        .append("         servlet.service(request.getMethod(), request, response);" + LS);
      if ("suspended".equals(syncType)) {
         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();" + LS)
           .append("         amsos.await();" + LS)
           .append("         ByteArrayOutputStream baos = amsos.getDelegate();" + LS)
           .append("         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());" + LS)
           .append("         Any reply = Any.parseFrom(bais);" + LS)
           .append("         ").append(retn).append(".Builder grmb = createGeneralReturnMessageBuilder(response);" + LS)
           .append("         ").append(getSetterMethod(actualReturnClass)).append("(reply);" + LS)
           .append("         responseObserver.onNext(grmb.build());" + LS);
      } else if ("completionStage".equals(syncType)) {
         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();" + LS)
           .append("         ByteArrayOutputStream baos = amsos.await();" + LS)
           .append("         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());" + LS)
           .append("         ").append(actualReturnClass).append(" reply = ").append(actualReturnClass).append(".parseFrom(bais);" + LS)
           .append("         ").append(retn).append(".Builder grmb = createGeneralReturnMessageBuilder(response);" + LS)
           .append("         ").append(getSetterMethod(actualReturnClass)).append("(reply);" + LS)
           .append("         responseObserver.onNext(grmb.build());" + LS);
      } else if ("sse".equals(syncType)) {
         sb.append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) response.getOutputStream();" + LS)
           .append("         while (true) {" + LS)
           .append("            if (amsos.isClosed()) {" + LS)
           .append("               break;" + LS)
           .append("            }" + LS)
           .append("            ByteArrayOutputStream baos = amsos.await();" + LS)
           .append("            if (amsos.isClosed()) {" + LS)
           .append("               break;" + LS)
           .append("            }" + LS)
           .append("            byte[] bytes = baos.toByteArray();" + LS)
           .append("            if (bytes.length == 2 && bytes[0] == 10 && bytes[1] == 10) {" + LS)
           .append("               continue;" + LS)
           .append("            }" + LS)
           .append("            try {" + LS)
           .append("               ").append(SSE_EVENT_CLASSNAME).append(" sseEvent = ").append(SSE_EVENT_CLASSNAME).append(".parseFrom(bytes);" + LS)
           .append("               responseObserver.onNext(sseEvent);" + LS)
           .append("            } catch (Exception e) {" + LS)
           .append("               continue;" + LS)
           .append("            }" + LS)
           .append("         }" + LS);
      } else {
         sb.append("         MockServletOutputStream msos = (MockServletOutputStream) response.getOutputStream();" + LS)
           .append("         ByteArrayOutputStream baos = msos.getDelegate();" + LS)
           .append("         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());" + LS)
           .append("         ").append(actualReturnClass).append(" reply = ").append(actualReturnClass).append(".parseFrom(bais);" + LS)
           .append("         ").append(retn).append(".Builder grmb = createGeneralReturnMessageBuilder(response);" + LS)
           .append("         ").append(getSetterMethod(actualReturnClass)).append("(reply);" + LS)
           .append("         responseObserver.onNext(grmb.build());" + LS);
      }
      sb.append("      } catch (Exception e) {" + LS)
        .append("         responseObserver.onError(e);" + LS)
        .append("      } finally {" + LS)
        .append("         responseObserver.onCompleted();" + LS)
        .append("         if (requestContextController != null) {" + LS)
        .append("            requestContextController.deactivate();" + LS)
        .append("         }" + LS)
        .append("      }" + LS);
   }

   private void nonStaticMethods(StringBuilder sb) {
      sb.append("" + LS)
        .append("//=============================  non-static methods =============================" + LS)
        .append("   private void activateRequestContext() {" + LS)
        .append("      if (requestContextController == null) {" + LS)
        .append("         requestContextController = CDI.current().select(RequestContextController.class).get();" + LS)
        .append("      }" + LS)
        .append("      requestContextController.activate();" + LS)
        .append("   }" + LS)
      ;
   }
   private void staticMethods(StringBuilder sb, String root, String pkg) {
      sb.append("" + LS)
        .append("//=============================  static methods =============================" + LS)
        .append("   private HttpServletDispatcher getServlet() throws Exception {" + LS)
        .append("      if (servlet == null) {" + LS)
        .append("         synchronized(this) {" + LS)
        .append("            if (servlet != null) {" + LS)
        .append("               return servlet;" + LS)
        .append("            }" + LS)
        .append("            servlet = (HttpServletDispatcher) GrpcHttpServletDispatcher.getServlet(\"").append(servletName).append("\");" + LS)
        .append("         }" + LS)
        .append("      }" + LS)
        .append("      return servlet;" + LS)
        .append("   }" + LS + LS)
        ;
      sb.append("   private static Map<String, List<String>> convertHeaders(Map<String, ").append(pkg).append(".").append(root).append("_proto.gHeader> protoHeaders) {" + LS)
        .append("      Map<String, List<String>> headers = new HashMap<String, List<String>>();" + LS)
        .append("      for (Map.Entry<String, ").append(pkg).append(".").append(root).append("_proto.gHeader> entry : protoHeaders.entrySet()) {" + LS)
        .append("         String key = entry.getKey();" + LS)
        .append("         ").append(pkg).append(".").append(root).append("_proto.gHeader protoHeader = entry.getValue();" + LS)
        .append("         List<String> values = new ArrayList<String>();" + LS)
        .append("         for (int i = 0; i < protoHeader.getValuesCount(); i++) {" + LS)
        .append("            values.add(protoHeader.getValues(i));" + LS)
        .append("         }" + LS)
        .append("         headers.put(key, values);" + LS)
        .append("      }" + LS)
        .append("      return headers;" + LS)
        .append("   }" + LS + LS)
        ;
      sb.append("   private static HttpServletRequest getHttpServletRequest(").append(pkg).append(".").append(root).append("_proto.GeneralEntityMessage param, GeneratedMessageV3 actualParam, String path, HttpServletResponse response, String verb, String type) throws Exception {" + LS)
        .append("      String url = \"\".equals(param.getURL()) ? \"http://localhost:8080\" + path : param.getURL();" + LS)
        .append("      ByteArrayInputStream bais = new ByteArrayInputStream(actualParam.toByteArray());" + LS)
        .append("      MockServletInputStream msis = new MockServletInputStream(bais);" + LS)
        .append("      Map<String, List<String>> headers = convertHeaders(param.getHeadersMap());" + LS)
        .append("      Cookie[] cookies = convertCookies(param.getCookiesList());" + LS)
        .append("      String httpMethod = param.getHttpMethod();" + LS)
        .append("//      ServletContext servletContext = org.jboss.resteasy.grpc.servlet.GrpcHttpServletDispatcher.getServletContext(\"").append(root).append("Servlet\");" + LS)
        .append("      ServletContext servletContext = ").append(root).append("_Server.getServletContext();" + LS)
        .append("      HttpServletRequestImpl request = new HttpServletRequestImpl();" + LS)
        .append("      request.setServletResponse(response);" + LS)
        .append("      request.setServletContext(servletContext);" + LS)
        .append("      request.setUri(url);" + LS)
        .append("      request.setPath(path);" + LS)
        .append("      request.setContextPath(servletContext.getContextPath());" + LS)
        .append("      request.setMethod(httpMethod != null && !\"\".equals(httpMethod) ? httpMethod : verb);" + LS)
        .append("      request.setInputStream(msis);" + LS)
        .append("      request.setReturnType(type);" + LS)
        .append("      request.setHeaders(headers);" + LS)
        .append("      request.setCookies(cookies);" + LS)
        .append("      request.setFormParameters(extractFormData(param));" + LS)
        .append("      ").append(pkg).append(".").append(root).append("_proto.ServletInfo servletInfo = param.getServletInfo();" + LS)
        .append("      if (servletInfo != null) {" + LS)
        .append("         if (servletInfo.getCharacterEncoding() != null) {" + LS)
        .append("            request.setCharacterEncoding(servletInfo.getCharacterEncoding());" + LS)
        .append("         }" + LS)
        .append("         if (servletInfo.getClientAddress() != null) {" + LS)
        .append("            request.setRemoteAddr(servletInfo.getClientAddress());" + LS)
        .append("         }" + LS)
        .append("         if (servletInfo.getClientHost() != null) {" + LS)
        .append("            request.setRemoteHost(servletInfo.getClientHost());" + LS)
        .append("         }" + LS)
        .append("         if (servletInfo.getClientPort() != -1) {" + LS)
        .append("            request.setRemotePort(servletInfo.getClientPort());" + LS)
        .append("         }" + LS)
        .append("      }" + LS)
        .append("      return request;" + LS)
        .append("   }" + LS + LS)
        ;
      sb.append("   private static jakarta.servlet.http.Cookie[] convertCookies(List<").append(pkg).append(".").append(root).append("_proto.gCookie> cookieList) {" + LS)
        .append("      jakarta.servlet.http.Cookie[] cookieArray = new jakarta.servlet.http.Cookie[cookieList.size()];" + LS)
        .append("      int i = 0;" + LS)
        .append("      for (Iterator<").append(pkg).append(".").append(root).append("_proto.gCookie> it = cookieList.iterator(); it.hasNext(); ) {" + LS)
        .append("         ").append(pkg).append(".").append(root).append("_proto.gCookie protoCookie = it.next();" + LS)
        .append("         jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(protoCookie.getName(), protoCookie.getValue());" + LS)
        .append("         cookie.setVersion(protoCookie.getVersion());" + LS)
        .append("         cookie.setPath(protoCookie.getPath());" + LS)
        .append("         cookie.setDomain(protoCookie.getDomain());" + LS)
        .append("         cookieArray[i++] = cookie;" + LS)
        .append("      }" + LS)
        .append("      return cookieArray;" + LS)
        .append("   }" + LS + LS)
        ;
      sb.append("   private static Map<String, String[]> extractFormData(GeneralEntityMessage param) {" + LS)
        .append("      if (!param.hasFormField()) {" + LS)
        .append("         return null;" + LS)
        .append("      }" + LS)
        .append("      Map<String, String[]> formParams = new HashMap<String, String[]>();" + LS)
        .append("      Map<String, FormValues> map = param.getFormField().getFormMapFieldMap();" + LS)
        .append("      for (Map.Entry<String, FormValues> entry : map.entrySet()) {" + LS)
        .append("         String[] values = new String[entry.getValue().getFormValuesFieldCount()];" + LS)
        .append("         for (int i = 0; i < entry.getValue().getFormValuesFieldCount(); i++) {" + LS)
        .append("            values[i] = entry.getValue().getFormValuesField(i);" + LS)
        .append("         }" + LS)
        .append("         formParams.put(entry.getKey(), values);" + LS)
        .append("      }" + LS)
        .append("      return formParams;" + LS)
        .append("   }" + LS + LS)
        ;
      sb.append("   private static GeneralReturnMessage.Builder createGeneralReturnMessageBuilder(HttpServletResponseImpl response) throws ParseException {" + LS)
        .append("      GeneralReturnMessage.Builder grmBuilder = GeneralReturnMessage.newBuilder();" + LS)
        .append("      gNewCookie.Builder cookieBuilder = gNewCookie.newBuilder();" + LS)
        .append("      if (!response.getHeaderNames().isEmpty()) {" + LS)
        .append("         gHeader.Builder headerBuilder = gHeader.newBuilder();" + LS)
        .append("         for (String headerName : response.getHeaderNames()) {" + LS)
        .append("            if (\"Set-Cookie\".equals(headerName)) {" + LS)
        .append("               Collection<String> cookies = response.getHeaders(\"Set-Cookie\");" + LS)
        .append("               for (String s : cookies) {" + LS)
        .append("                  grmBuilder.addCookies(parseNewCookie(cookieBuilder, s));" + LS)
        .append("                  cookieBuilder.clear();" + LS)
        .append("               }" + LS)
        .append("            } else {" + LS)
        .append("               for (String value : response.getHeaders(headerName)) {" + LS)
        .append("                   headerBuilder.addValues(value);" + LS)
        .append("               }" + LS)
        .append("            grmBuilder.putHeaders(headerName, headerBuilder.build());" + LS)
        .append("            headerBuilder.clear();" + LS)
        .append("            }" + LS)
        .append("         }" + LS)
        .append("      }" + LS)
        .append("      if (!response.getCookies().isEmpty()) {" + LS)
        .append("         for (Cookie cookie : response.getCookies()) {" + LS)
        .append("            cookieBuilder" + LS)
        .append("               .setMaxAge(cookie.getMaxAge())" + LS)
        .append("               .setVersion(cookie.getVersion())" + LS)
        .append("               ;" + LS)
        .append("            if (cookie.getComment() != null) {" + LS)
        .append("               cookieBuilder.setComment(cookie.getComment());" + LS)
        .append("            }" + LS)
        .append("            if (cookie.getDomain() != null) {" + LS)
        .append("               cookieBuilder.setDomain(cookie.getDomain());" + LS)
        .append("            }" + LS)
        .append("            if (cookie.getName() != null) {" + LS)
        .append("               cookieBuilder.setName(cookie.getName());" + LS)
        .append("            }" + LS)
        .append("            if (cookie.getPath() != null) {" + LS)
        .append("               cookieBuilder.setPath(cookie.getPath());" + LS)
        .append("            }" + LS)
        .append("            if (cookie.getValue() != null) {" + LS)
        .append("               cookieBuilder.setValue(cookie.getValue());" + LS)
        .append("            }" + LS)
        .append("            if (cookie.getSecure()) {" + LS)
        .append("               cookieBuilder.setSecure(true);" + LS)
        .append("            }" + LS)
        .append("            if (cookie.isHttpOnly()) {" + LS)
        .append("               cookieBuilder.setHttpOnly(true);" + LS)
        .append("            }" + LS)
        .append("            grmBuilder.addCookies(cookieBuilder.build());" + LS)
        .append("            cookieBuilder.clear();" + LS)
        .append("         }" + LS)
        .append("      }" + LS)
        .append("      grmBuilder.setStatus(gInteger.newBuilder().setValue(response.getStatus()).build());" + LS)
        .append("      return grmBuilder;" + LS)
        .append("   }" + LS + LS)
        ;
      sb.append("   private static gNewCookie parseNewCookie(gNewCookie.Builder ncb, String s) throws ParseException {" + LS)
        .append("      String[] fields = s.split(\";\");" + LS)
        .append("      for (String field : fields) {" + LS)
        .append("         String[] subfields = field.split(\"=\");" + LS)
        .append("         switch (subfields[0].strip()) {" + LS)
        .append("            case \"Domain\":" + LS)
        .append("               ncb.setDomain(subfields[1].trim());" + LS)
        .append("               break;" + LS)
        .append("            case \"Path\":" + LS)
        .append("               ncb.setPath(subfields[1].trim());" + LS)
        .append("               break;" + LS)
        .append("            case \"Version\":" + LS)
        .append("               ncb.setVersion(Integer.valueOf(subfields[1].trim()));" + LS)
        .append("               break;" + LS)
        .append("            case \"Comment\":" + LS)
        .append("               ncb.setComment(subfields[1].trim());" + LS)
        .append("               break;" + LS)
        .append("            case \"Expires\":" + LS)
        .append("               DateTimeFormatter formatter = DateTimeFormatter.ofPattern(\"EEE, DD-MMM-yyyy HH:mm:ss zzz\");" + LS)
        .append("               ZonedDateTime zdt = ZonedDateTime.parse(subfields[1].trim(), formatter);" + LS)
        .append("               ncb.setExpiry(Timestamp.newBuilder().setSeconds(zdt.toEpochSecond()));" + LS)
        .append("               break;" + LS)
        .append("            case \"HttpOnly\":" + LS)
        .append("               ncb.setHttpOnly(true);" + LS)
        .append("               break;" + LS)
        .append("            case \"Max-Age\":" + LS)
        .append("               ncb.setMaxAge(Integer.valueOf(subfields[1].trim()));" + LS)
        .append("               break;" + LS)
        .append("            case \"SameSite\":" + LS)
        .append("               ncb.setSameSite(gNewCookie.SameSite.valueOf(subfields[1].trim()));" + LS)
        .append("               break;" + LS)
        .append("            case \"Secure\":" + LS)
        .append("               ncb.setSecure(true);" + LS)
        .append("               break;" + LS)
        .append("            default:" + LS)
        .append("               ncb.setName(subfields[0].trim());" + LS)
        .append("               ncb.setValue(subfields[1].trim());" + LS)
        .append("         }" + LS)
        .append("      }" + LS)
        .append("      return ncb.build();" + LS)
        .append("   }" + LS)
        ;
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
         String c = actualEntityClass.substring(i, i + 1);
         if ("_".equals(c) || ".".equals(c)) {
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
     if (actualReturnClass.contains("___") || actualReturnClass.contains("_INNER_")) {
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
            if (s.substring(i).startsWith("INNER_")) {
               sb.append("INNER");
               i += "INNER".length();
            } else {
               sb.append(Character.toUpperCase(s.charAt(i)));
               sawUnderScore = false;
            }
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
