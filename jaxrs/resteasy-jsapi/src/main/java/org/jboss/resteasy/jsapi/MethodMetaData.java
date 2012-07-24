package org.jboss.resteasy.jsapi;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.jsapi.MethodParamMetaData.MethodParamType;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MethodMetaData
{
	private final static Logger logger = Logger
    .getLogger(MethodMetaData.class);

	private ResourceMethod resource;
	private Method method;
	private Class<?> klass;
	private String wants;
	private String consumesMIMEType;
	private String uri;
	private String functionName;
	private List<MethodParamMetaData> parameters = new ArrayList<MethodParamMetaData>();
	private Collection<String> httpMethods;
	private ServiceRegistry registry;
	private String functionPrefix;
	private boolean wantsForm;

	public MethodMetaData(ServiceRegistry serviceRegistry, ResourceMethod resource)
	{
		this.registry = serviceRegistry;
		this.resource = resource;
		this.method = resource.getMethod();
		this.klass = method.getDeclaringClass();
		Path methodPath = method.getAnnotation(Path.class);
		Path klassPath = klass.getAnnotation(Path.class);
		Produces produces = method.getAnnotation(Produces.class);
		if (produces == null)
			produces = klass.getAnnotation(Produces.class);
		this.wants = getWants(produces);
		Consumes consumes = method.getAnnotation(Consumes.class);
		if (consumes == null)
			consumes = klass.getAnnotation(Consumes.class);
		this.uri = appendURIFragments(registry, klassPath, methodPath);
		if(serviceRegistry.isRoot())
			this.functionPrefix = klass.getSimpleName();
		else
			this.functionPrefix = serviceRegistry.getFunctionPrefix();
		this.functionName = this.functionPrefix + "." + method.getName(); 
		httpMethods = resource.getHttpMethods();

		// we need to add all parameters from parent resource locators until the root
		List<Method> methodsUntilRoot = new ArrayList<Method>();
		methodsUntilRoot.add(method);
		serviceRegistry.collectResourceMethodsUntilRoot(methodsUntilRoot);
		for (Method method : methodsUntilRoot) {
			Annotation[][] allAnnotations = method.getParameterAnnotations();
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < parameterTypes.length; i++)
			{
				processMetaData(parameterTypes[i], allAnnotations[i], true);
			}
		}
		// this must be after we scan the params in case of @Form
		this.consumesMIMEType = getConsumes(consumes);
		if(wantsForm && !"application/x-www-form-urlencoded".equals(consumesMIMEType)){
         logger.warn("Overriding @Consumes annotation in favour of application/x-www-form-urlencoded due to the presence of @FormParam");
			this.consumesMIMEType = "application/x-www-form-urlencoded";
		}
	}

	protected void processMetaData(Class<?> type, Annotation[] annotations,
			boolean useBody)
	{
		QueryParam query;
		HeaderParam header;
		MatrixParam matrix;
		PathParam uriParam;
		CookieParam cookie;
		FormParam formParam;

		// boolean isEncoded = FindAnnotation.findAnnotation(annotations,
		// Encoded.class) != null;

		if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
		{
			addParameter(type, annotations, MethodParamType.QUERY_PARAMETER, query
					.value());
		} else if ((header = FindAnnotation.findAnnotation(annotations,
				HeaderParam.class)) != null)
		{
			addParameter(type, annotations, MethodParamType.HEADER_PARAMETER,
					header.value());
		} else if ((cookie = FindAnnotation.findAnnotation(annotations,
				CookieParam.class)) != null)
		{
			addParameter(type, annotations, MethodParamType.COOKIE_PARAMETER,
					cookie.value());
		} else if ((uriParam = FindAnnotation.findAnnotation(annotations,
				PathParam.class)) != null)
		{
			addParameter(type, annotations, MethodParamType.PATH_PARAMETER,
					uriParam.value());
		} else if ((matrix = FindAnnotation.findAnnotation(annotations,
				MatrixParam.class)) != null)
		{
			addParameter(type, annotations, MethodParamType.MATRIX_PARAMETER,
					matrix.value());
		} else if ((formParam = FindAnnotation.findAnnotation(annotations,
				FormParam.class)) != null)
		{
			addParameter(type, annotations, MethodParamType.FORM_PARAMETER,
					formParam.value());
			this.wantsForm = true;
		} else if (FindAnnotation.findAnnotation(annotations, Form.class) != null)
		{
			walkForm(type);
		} else if ((FindAnnotation.findAnnotation(annotations, Context.class)) != null)
		{
			// righfully ignore
		} else if (useBody)
		{
			addParameter(type, annotations, MethodParamType.ENTITY_PARAMETER, null);
		}
	}

	private void walkForm(Class<?> type)
	{
		for (Field field : type.getDeclaredFields())
		{
			processMetaData(field.getType(), field.getAnnotations(), false);
		}
		for (Method method : type.getDeclaredMethods())
		{
			if (method.getParameterTypes().length != 1
					|| !method.getReturnType().equals(Void.class))
				continue;
			processMetaData(method.getParameterTypes()[0],
					method.getAnnotations(), false);
		}
		if (type.getSuperclass() != null)
			walkForm(type.getSuperclass());
	}

	private void addParameter(Class<?> type, Annotation[] annotations,
			MethodParamType paramType, String value)
	{
		this.parameters.add(new MethodParamMetaData(type, annotations, paramType,
				value));
	}

	private String getWants(Produces produces)
	{
		if (produces == null)
			return null;
		String[] value = produces.value();
		if (value.length == 0)
			return null;
		if (value.length == 1)
			return value[0];
		StringBuffer buf = new StringBuffer();
		for (String mime : produces.value())
		{
			if (buf.length() != 0)
				buf.append(",");
			buf.append(mime);
		}
		return buf.toString();
	}

	private String getConsumes(Consumes consumes)
	{
		if (consumes == null)
			return "text/plain";
		if (consumes.value().length > 0)
			return consumes.value()[0];
		return "text/plain";
	}

	public static String appendURIFragments(String... fragments)
	{
		StringBuilder str = new StringBuilder();
		for (String fragment : fragments)
		{
			if (fragment == null || fragment.length() == 0 || fragment.equals("/"))
				continue;
			if (fragment.startsWith("/"))
				fragment = fragment.substring(1);
			if (fragment.endsWith("/"))
				fragment = fragment.substring(0, fragment.length() - 1);
			str.append('/').append(fragment);
		}
		if (str.length() == 0)
			return "/";
		return str.toString();
	}

	public ResourceMethod getResource()
	{
		return resource;
	}

	public Method getMethod()
	{
		return method;
	}

	public Class<?> getKlass()
	{
		return klass;
	}

	public String getWants()
	{
		return wants;
	}

	public String getConsumesMIMEType()
	{
		return consumesMIMEType;
	}

	public String getUri()
	{
		return uri;
	}

	public String getFunctionName()
	{
		return functionName;
	}

	public List<MethodParamMetaData> getParameters()
	{
		return parameters;
	}

	public Collection<String> getHttpMethods()
	{
		return httpMethods;
	}

	public static String appendURIFragments(ServiceRegistry registry, Path classPath, Path methodPath) {
		return appendURIFragments(registry == null ? null : registry.getUri(), 
				classPath != null ? classPath.value() : null,
				methodPath != null ? methodPath.value() : null);
	}

	public String getFunctionPrefix() {
		return functionPrefix;
	}
}
