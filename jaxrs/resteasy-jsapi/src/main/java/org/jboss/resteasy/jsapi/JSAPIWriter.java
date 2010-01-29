package org.jboss.resteasy.jsapi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.jboss.resteasy.util.PathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class JSAPIWriter
{

   private static final long serialVersionUID = -1985015444704126795L;

   private final static Logger logger = LoggerFactory
         .getLogger(JSAPIWriter.class);
   private String restPath;

   public JSAPIWriter(String restPath)
   {
      this.restPath = restPath;
   }

   public void writeJavaScript(String uri, PrintWriter writer,
         List<MethodMetaData> methodMetaDataList) throws IOException
   {
	   if(restPath != null)
		   uri = uri + restPath;
      logger.info("rest path: " + uri);

      writer.println("// start RESTEasy client API");
      copyResource("/resteasy-client.js", writer);
      // writer.println("// start RESTEasy JS framework API");
      // copyResource("/resteasy-jsframework.js", writer);
      writer.println("// start JAX-RS API");
      writer.println("REST.apiURL = '" + uri + "';");
      Set<String> declaringClasses = new HashSet<String>();
      for (MethodMetaData methodMetaData : methodMetaDataList)
      {

         logger.info("Path: " + methodMetaData.getKey());
         logger.info(" Invoker: " + methodMetaData.getResource());
         String declaringClass = methodMetaData.getMethod().getDeclaringClass()
               .getSimpleName();
         if (declaringClasses.add(declaringClass))
         {
            writer.println("var " + declaringClass + " = {};");
         }
         for (String httpMethod : methodMetaData.getHttpMethods())
         {
            print(writer, httpMethod, methodMetaData);
         }
      }
   }


   private void copyResource(String name, PrintWriter writer)
         throws IOException
   {
      Reader reader = new InputStreamReader(getClass()
            .getResourceAsStream(name));
      char[] array = new char[1024];
      int read;
      while ((read = reader.read(array)) >= 0)
      {
         writer.write(array, 0, read);
      }
      reader.close();
   }

   private void print(PrintWriter writer, String httpMethod,
         MethodMetaData methodMetaData)
   {
      String uri = methodMetaData.getUri();
      writer.println("// " + httpMethod + " " + uri);
      writer
            .println(methodMetaData.getFunctionName() + " = function(_params){");
      writer.println(" var params = _params ? _params : {};");
      writer.println(" var request = new REST.Request();");
      writer.println(" request.setMethod('" + httpMethod + "');");
      writer
            .println(" var uri = params.$apiURL ? params.$apiURL : REST.apiURL;");
      if (uri.contains("{"))
      {
         printURIParams(uri, writer);
      } else
      {
         writer.println(" uri += '" + uri + "';");
      }
      printOtherParams(methodMetaData, writer);
      writer.println(" request.setURI(uri);");
      writer.println(" if(params.$username && params.$password)");
      writer
            .println("  request.setCredentials(params.$username, params.$password);");
      writer.println(" if(params.$accepts)");
      writer.println("  request.setAccepts(params.$accepts);");
      if (methodMetaData.getWants() != null)
      {
         writer.println(" else");
         writer.println("  request.setAccepts('" + methodMetaData.getWants()
               + "');");
      }
      writer.println(" if(params.$contentType)");
      writer.println("  request.setContentType(params.$contentType);");
      writer.println(" else");
      writer.println("  request.setContentType('"
            + methodMetaData.getConsumesMIMEType() + "');");
      writer.println(" if(params.$callback){");
      writer.println("  request.execute(params.$callback);");
      writer.println(" }else{");
      writer.println("  var returnValue;");
      writer.println("  request.setAsync(false);");
      writer
            .println("  var callback = function(httpCode, xmlHttpRequest, value){ returnValue = value;};");
      writer.println("  request.execute(callback);");
      writer.println("  return returnValue;");
      writer.println(" }");
      writer.println("};");
   }

   private void printOtherParams(MethodMetaData methodMetaData,
         PrintWriter writer)
   {
      List<MethodParamMetaData> params = methodMetaData.getParameters();
      for (MethodParamMetaData methodParamMetaData : params)
      {
         printParameter(methodParamMetaData, writer);
      }
   }

   private void printParameter(MethodParamMetaData metaData,
         PrintWriter writer)
   {
      switch(metaData.getParamType()){
      case QUERY_PARAMETER:
         print(metaData, writer, "QueryParameter");
         break;
      case HEADER_PARAMETER:
         print(metaData, writer, "Header");
         // FIXME: warn about forbidden headers:
         // http://www.w3.org/TR/XMLHttpRequest/#the-setrequestheader-method
         break;
      case COOKIE_PARAMETER:
         print(metaData, writer, "Cookie");
         break;
      case MATRIX_PARAMETER:
         print(metaData, writer, "MatrixParameter");
         break;
      case FORM_PARAMETER:
         // FIXME: handle this;
         break;
      case ENTITY_PARAMETER:
         // the entity
         writer.println(" if(params.$entity)");
         writer.println("  request.setEntity(params.$entity);");
         break;
      }
   }

   private void print(MethodParamMetaData metaData, PrintWriter writer,
         String type)
   {
      String paramName = metaData.getParamName();
      writer.println(String.format(" if(params.%s)\n  request.add%s('%s', params.%s);", paramName, type, paramName, paramName));
   }
   

   private void printURIParams(String uri, PrintWriter writer)
   {
      String replacedCurlyURI = PathHelper.replaceEnclosedCurlyBraces(uri);
      Matcher matcher = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlyURI);
      int i = 0;
      while (matcher.find())
      {
         if (matcher.start() > i)
         {
            writer.println(" uri += '"
                  + replacedCurlyURI.substring(i, matcher.start()) + "';");
         }
         String name = matcher.group(1);
         writer.println(" uri += REST.encodePathSegment(params." + name + ");");
         i = matcher.end();
      }
      if (i < replacedCurlyURI.length())
         writer.println(" uri += '" + replacedCurlyURI.substring(i) + "';");
   }
}
