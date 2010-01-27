package org.jboss.resteasy.jsapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.auth.oauth.OAuthServlet;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocator;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.registry.RootSegment;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.PathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class JSAPIServlet extends HttpServlet {

	private final static Logger logger = LoggerFactory.getLogger(OAuthServlet.class);
	private ResourceMethodRegistry registry;
	private String restPath;

	@Override
	public void init(ServletConfig config)
    throws ServletException {
		super.init(config);
		logger.info("Loading JSAPI Servlet");
		ServletContext servletContext = config.getServletContext();
	    registry = (ResourceMethodRegistry) servletContext.getAttribute(Registry.class.getName());
		restPath = servletContext.getInitParameter("resteasy.servlet.mapping.prefix");
		logger.debug("JSAPIServlet loaded");
	}
	
	@Override
	protected void service(HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException,
	IOException{
		String pathInfo = req.getPathInfo();
		String uri = req.getRequestURL().toString();
		uri = uri.substring(0, uri.length() - req.getServletPath().length());
		uri += restPath;
		logger.info("rest path: "+uri);
		logger.debug("Serving "+pathInfo);
		logger.debug("Query "+req.getQueryString());
		PrintWriter writer = resp.getWriter();
		RootSegment rootSegment = registry.getRoot();
		writer.println("// start RESTEasy client API");
		copyResource("/resteasy-client.js", writer);
//		writer.println("// start RESTEasy JS framework API");
//		copyResource("/resteasy-jsframework.js", writer);
		writer.println("// start JAX-RS API");
		writer.println("REST.apiURL = '"+uri+"';");
		Set<String> declaringClasses = new HashSet<String>();
		Map<String, List<ResourceInvoker>> bounded = rootSegment.getBounded();
		for(String key : bounded.keySet()){
			logger.info("Path: "+key);
			for(ResourceInvoker invoker : bounded.get(key)){
				logger.info(" Invoker: "+invoker);
				if(invoker instanceof ResourceMethod){
					ResourceMethod method = (ResourceMethod)invoker;
					String declaringClass = method.getMethod().getDeclaringClass().getSimpleName();
					if(declaringClasses.add(declaringClass)){
						writer.println("var "+declaringClass+" = {};");
					}
					for(String httpMethod : method.getHttpMethods()){
						printMethod(httpMethod, method, writer);
					}
				}else if(invoker instanceof ResourceLocator){
					ResourceLocator locator = (ResourceLocator)invoker;
					// FIXME: todo
				}
			}
		}
	}

	private void copyResource(String name, PrintWriter writer) throws IOException{
		Reader reader = new InputStreamReader(getClass().getResourceAsStream(name));
		char[] array = new char[1024];
		int read;
		while((read = reader.read(array)) >= 0){
			writer.write(array, 0, read);
		}
		reader.close();
	}

	private void printMethod(String httpMethod, ResourceMethod resource, PrintWriter writer) {
		Method method = resource.getMethod();
		Class<?> klass = method.getDeclaringClass();
		Path methodPath = method.getAnnotation(Path.class);
		Path klassPath = klass.getAnnotation(Path.class);
		Produces produces = method.getAnnotation(Produces.class);
		if(produces == null)
			produces = klass.getAnnotation(Produces.class);
		String wants = getWants(produces);
		Consumes consumes = method.getAnnotation(Consumes.class);
		if(consumes == null)
			consumes = klass.getAnnotation(Consumes.class);
		String consumesMIMEType = getConsumes(consumes);
		String uri = appendURIFragments(klassPath.value(), methodPath != null ? methodPath.value() : null);
		writer.println("// "+httpMethod+" "+uri);
		String functionName = klass.getSimpleName()+"."+method.getName();
		writer.println(functionName + " = function(_params){");
		writer.println(" var params = _params ? _params : {};");
		writer.println(" var request = new REST.Request();");
		writer.println(" request.setMethod('"+httpMethod+"');");
		writer.println(" var uri = params.$apiURL ? params.$apiURL : REST.apiURL;");
		if(uri.contains("{")){
			printURIParams(uri, writer);
		}else{
			writer.println(" uri += '"+uri+"';");
		}
		printOtherParams(method, writer);
		writer.println(" request.setURI(uri);");
		writer.println(" if(params.$username && params.$password)");
		writer.println("  request.setCredentials(params.$username, params.$password);");
		writer.println(" if(params.$accepts)");
		writer.println("  request.setAccepts(params.$accepts);");
		if(wants != null){
			writer.println(" else");
			writer.println("  request.setAccepts('"+wants+"');");
		}
		writer.println(" if(params.$contentType)");
		writer.println("  request.setContentType(params.$contentType);");
		writer.println(" else");
		writer.println("  request.setContentType('"+consumesMIMEType+"');");
		writer.println(" if(params.$callback){");
		writer.println("  request.execute(params.$callback);");
		writer.println(" }else{");
		writer.println("  var returnValue;");
		writer.println("  request.setAsync(false);");
		writer.println("  var callback = function(httpCode, xmlHttpRequest, value){ returnValue = value;};");
		writer.println("  request.execute(callback);");
		writer.println("  return returnValue;");
		writer.println(" }");
		writer.println("};");
	}

	private String getWants(Produces produces) {
		if(produces == null)
			return null;
		String[] value = produces.value();
		if(value.length == 0)
			return null;
		if(value.length == 1)
			return value[0];
		StringBuffer buf = new StringBuffer();
		for(String mime : produces.value()){
			if(buf.length() != 0)
				buf.append(",");
			buf.append(mime);
		}
		return buf.toString();
	}

	private String getConsumes(Consumes consumes) {
		if(consumes == null)
			return "text/plain";
		if(consumes.value().length > 0)
			return consumes.value()[0];
		return "text/plain";
	}

	private void printOtherParams(Method method, PrintWriter writer) {
		Annotation[][] allAnnotations = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();

		for(int i=0;i<parameterTypes.length;i++){
			printParameter(parameterTypes[i], allAnnotations[i],true, writer);
		}
	}

	private void printParameter(Class<?> type, Annotation[] annotations, boolean allowsEntity, PrintWriter writer) {
		  QueryParam query;
	      HeaderParam header;
	      MatrixParam matrix;
	      PathParam uriParam;
	      CookieParam cookie;
	      FormParam formParam;

	      boolean isEncoded = FindAnnotation.findAnnotation(annotations,
	              Encoded.class) != null;

	      if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
	      {
	    	  writer.println(" if(params."+query.value()+")");
	    	  writer.println("  request.addQueryParameter('"+query.value()+"', params."+query.value()+");");
	      }
	      else if ((header = FindAnnotation.findAnnotation(annotations,
	              HeaderParam.class)) != null)
	      {
	    	  writer.println(" if(params."+header.value()+")");
	    	  writer.println("  request.addHeader('"+header.value()+"', params."+header.value()+");");
	    	  // FIXME: warn about forbidden headers: http://www.w3.org/TR/XMLHttpRequest/#the-setrequestheader-method
	      }
	      else if ((cookie = FindAnnotation.findAnnotation(annotations,
	              CookieParam.class)) != null)
	      {
	    	  writer.println(" if(params."+cookie.value()+")");
	    	  writer.println("  request.addCookie('"+cookie.value()+"', params."+cookie.value()+");");
	      }
	      else if ((uriParam = FindAnnotation.findAnnotation(annotations,
	              PathParam.class)) != null)
	      {
	    	  // we did that already
	      }
	      else if ((matrix = FindAnnotation.findAnnotation(annotations,
	              MatrixParam.class)) != null)
	      {
	    	  writer.println(" if(params."+matrix.value()+")");
	    	  writer.println("  request.addMatrixParameter('"+matrix.value()+"', params."+matrix.value()+");");
	      }
	      else if ((formParam = FindAnnotation.findAnnotation(annotations,
	              FormParam.class)) != null)
	      {
	    	  // FIXME: handle this
	      }
	      else if ((/* form = */FindAnnotation.findAnnotation(annotations,
	              Form.class)) != null)
	      {
	    	  walkForm(type, writer);
	      }
	      else if ((FindAnnotation.findAnnotation(annotations,
	              Context.class)) != null)
	      {
	    	  // righfully ignore
	      }else if(allowsEntity){
	    	  // the entity
	    	  writer.println(" if(params.$entity)");
	    	  writer.println("  request.setEntity(params.$entity);");
	      }
	}

	private void walkForm(Class<?> type, PrintWriter writer) {
  	  for(Field field : type.getDeclaredFields()){
		  printParameter(field.getType(), field.getAnnotations(), false, writer);
	  }
	  for(Method method : type.getDeclaredMethods()){
		  if(method.getParameterTypes().length != 1 || !method.getReturnType().equals(Void.class))
			  continue;
		  printParameter(method.getParameterTypes()[0], method.getAnnotations(), false, writer);
	  }
	  if(type.getSuperclass() != null)
		  walkForm(type.getSuperclass(), writer);
	}

	private void printURIParams(String uri, PrintWriter writer) {
	      String replacedCurlyURI = PathHelper.replaceEnclosedCurlyBraces(uri);
	      Matcher matcher = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlyURI);
	      int i=0;
	      while(matcher.find()){
	    	  if(matcher.start() > i){
	    		  writer.println(" uri += '"+replacedCurlyURI.substring(i, matcher.start())+"';");
	    	  }
	    	  String name = matcher.group(1);
	    	  writer.println(" uri += REST.encodePathSegment(params."+name+");");
	    	  i = matcher.end();
	      }
	      if(i < replacedCurlyURI.length())
    		  writer.println(" uri += '"+replacedCurlyURI.substring(i)+"';");
	}

	private String appendURIFragments(String... fragments) {
		StringBuilder str = new StringBuilder();
		for(String fragment : fragments){
			if(fragment == null || fragment.length() == 0 || fragment.equals("/"))
				continue;
			if(fragment.startsWith("/"))
				fragment = fragment.substring(1);
			if(fragment.endsWith("/"))
				fragment = fragment.substring(0, fragment.length()-1);
			str.append('/').append(fragment);
		}
		if(str.length() == 0)
			return "/";
		return str.toString();
	}

}
