package org.jboss.resteasy.links.impl;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.links.ELProvider;
import org.jboss.resteasy.links.LinkELProvider;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.ResourceFacade;
import org.jboss.resteasy.links.i18n.LogMessages;
import org.jboss.resteasy.links.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;

import javax.annotation.security.RolesAllowed;
import javax.el.ELContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RESTUtils {

	public static <T> T addDiscovery(T entity, UriInfo uriInfo, ResourceMethodRegistry registry) {
		// find the field to inject first
		Field injectionField = findInjectionField(entity);
		if(injectionField == null)
			return entity;
		List<Method> methods = getServiceMethods(registry);
		injectionField.setAccessible(true);
		RESTServiceDiscovery ret = null;
		try {
			ret = (RESTServiceDiscovery)injectionField.get(entity);
		} catch (Exception e) {
			LogMessages.LOGGER.error(Messages.MESSAGES.failedToAccessLinks(entity), e);
		}
		injectionField.setAccessible(false);
		if (ret == null) ret = new RESTServiceDiscovery();
		for(Method m : methods){
			processLinkResources(m, entity, uriInfo, ret);
		}
		// do not inject an empty service
		if(ret.isEmpty())
			return entity;
		// now inject
		injectionField.setAccessible(true);
		try {
			injectionField.set(entity, ret);
		} catch (Exception e) {
		   LogMessages.LOGGER.error(Messages.MESSAGES.failedToInjectLinks(entity), e);
		}
		injectionField.setAccessible(false);
		return entity;
	}

	private static Field findInjectionField(Object entity) {
		Class<?> klass = entity.getClass();
		do{
			for(Field f : klass.getDeclaredFields()){
				if(f.getType().equals(RESTServiceDiscovery.class))
					return f;
			}
			klass = klass.getSuperclass();
		}while(klass != null);
		return null;
	}

	public static List<Method> getServiceMethods(ResourceMethodRegistry registry){
		ArrayList<Method> results = new ArrayList<Method>();
		for (Entry<String, List<ResourceInvoker>> entry : registry.getBounded().entrySet())
		{
			List<ResourceInvoker> invokers = entry.getValue();
			for (ResourceInvoker invoker : invokers)
			{
				if (invoker instanceof ResourceMethodInvoker)
				{
					ResourceMethodInvoker resourceMethod = (ResourceMethodInvoker)invoker;
					Method method = resourceMethod.getMethod();
					results.add(method);
				} else
				{
					// TODO: fix this?
				}
			}
		}
		return results;
	}
	

	/**
	 * A drop-in replacement for Method.findAnnotation(Class<T extends Annotation>) that also
	 * searches through the inheritance hierarchy.
	 * 
	 * @param m The method to search from.
	 * @param annotation The annotation.
	 * @return The first annotation or null.
	 */
	private static <T extends Annotation> T findAnnotation(Method m, Class<T> annotation) {
		Method method = findMethod(m, annotation);
		if (method != null) {
			return method.getAnnotation(annotation);			
		}
		return null;
	}
	
	/**
	 * A drop-in replacement for Method.isAnnotationPresent(Class<T extends Annotation>) that also
	 * searches through the inheritance hierarchy.
	 * 
	 * @param m The method to search from.
	 * @param annotation The annotation.
	 * @return True if any method in the hierarchy has the annotation.
	 */
	private static <T extends Annotation> boolean hasAnnotation(Method m, Class<T> annotation) {
		Method method = findMethod(m, annotation);
		return method != null;
	}
	
	/**
	 * Returns the first method that contains the specified annotation or null, if there
	 * is none. This method first searches through the inheritance hierarchy and then looks at 
	 * interfaces.
	 * 
	 * @param m The method to search for and to start the search from.
	 * @param annotation The annotation.
	 * @return The method containing the annotation or null.
	 */
	private static <T extends Annotation> Method findMethod(Method m, Class<T> annotation) {
		Class<?> c = m.getDeclaringClass();
		while (c != null) {
			try {
				Method method = c.getDeclaredMethod(m.getName(), m.getParameterTypes());
				T result = method.getAnnotation(annotation);
				if (result != null) return method;
			} catch (NoSuchMethodException e) { 
				// method not contained
			}
			c = c.getSuperclass();
		}
		Class<?>[] interfaces = m.getDeclaringClass().getInterfaces();
		for (Class<?> inter: interfaces) {
			try {
				Method method = inter.getDeclaredMethod(m.getName(), m.getParameterTypes());
				T result = method.getAnnotation(annotation);
				if (result != null) return method;
			} catch (NoSuchMethodException e) { 
				// method not contained
			}
		}
		return null;
	}


	private static void processLinkResources(Method m, Object entity, UriInfo uriInfo,
			RESTServiceDiscovery ret) {
		// find a single service
		LinkResource service = findAnnotation(m, LinkResource.class);
		if(service != null)
			processLinkResource(m, entity, uriInfo, ret, service);
		// find a multi-service
		LinkResources services = findAnnotation(m, LinkResources.class);
		if(services != null)
			for(LinkResource service2 : services.value())
				processLinkResource(m, entity, uriInfo, ret, service2);
	}

	private static void processLinkResource(Method m, Object entity, UriInfo uriInfo,
			RESTServiceDiscovery ret, LinkResource service) {
		String rel = service.rel();
		// if we have uri templates, we need a compatible instance
		Class<?> type = getServiceType(service, m);
		if(type.isInstance(entity)){
			if(checkConstraint(service, entity, m))
				addInstanceService(m, entity, uriInfo, ret, service, rel);
		}else if(entity instanceof ResourceFacade<?> && ((ResourceFacade<?>)entity).facadeFor() == type){
			if(checkConstraint(service, type, m))
				addService(m, (ResourceFacade<?>) entity, uriInfo, ret, service, rel);
		}
	}

	private static Class<?> getServiceType(LinkResource service, Method m) {
		Class<?> type = service.value();
		if(type != Void.class)
			return type;
		// are we looking at the return type or the body type?
		type = findBodyType(m);
		if(type == null){
			// our next best bet is the return type
			type = m.getReturnType();
		}
		if(Void.TYPE == type)
		   throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessResourceType());
		if(Collection.class.isAssignableFrom(type))
		   throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessCollectionType());
		if(Response.class.isAssignableFrom(type))
		   throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessType());
		return type;
	}

	private static Class<?> findBodyType(Method m) {
		Annotation[][] annotations = m.getParameterAnnotations();
		Class<?>[] types = m.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			// if there's no JAXRS annotation nor @Form, it's a body right?
			if(FindAnnotation.findJaxRSAnnotations(annotations[i]).length == 0
					&& FindAnnotation.findAnnotation(annotations[i], Form.class) == null)
				return types[i];
		}
		return null;
	}

	private static boolean checkConstraint(LinkResource service, Object object, Method m) {
		String constraint = service.constraint();
		if(constraint == null || constraint.length() == 0)
			return checkEJBConstraint(m);
		Boolean ret = evaluateELBoolean(m, getELContext(m, object), object, constraint);
		return ret != null && ret.booleanValue();
	}

	private static boolean checkEJBConstraint(Method m) {
		// Use dynamic class loading here since if the EJB annotation class is not present
		// it cannot be on the method, so we don't have to check for it
		try {
			Class.forName("javax.annotation.security.RolesAllowed");
		} catch (ClassNotFoundException e) {
			// class not here, therefore not on method either
			return true;
		}
		// From now on we can use this class since it's there. I (Stef Epardaud) don't think we need to 
		// remove the reference here and use reflection.
		RolesAllowed rolesAllowed = findAnnotation(m, RolesAllowed.class);
		if(rolesAllowed == null)
			return true;
		SecurityContext context = ResteasyProviderFactory.getContextData(SecurityContext.class);
		for(String role : rolesAllowed.value())
			if(context.isUserInRole(role))
				return true;
		return false;
	}

	private static void addService(Method m, ResourceFacade<?> entity, UriInfo uriInfo,
			RESTServiceDiscovery ret, LinkResource service, String rel) {
		Map<String, ? extends Object> pathParameters = entity.pathParameters();
		// do we need any path parameters?
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(m.getDeclaringClass());
		if(hasAnnotation(m, Path.class))
			uriBuilder.path(findMethod(m, Path.class));
		URI uri;
		List<String> paramNames = ((ResteasyUriBuilder)uriBuilder).getPathParamNamesInDeclarationOrder();
		if(paramNames.isEmpty())
			uri = uriBuilder.build();
		else if(pathParameters.size() >= paramNames.size())
			uri = uriBuilder.buildFromMap(pathParameters);
		else
			// just bail out since we don't have enough parameters, that must be an instance service
			return;
		if(rel.length() == 0){
			if (hasAnnotation(m, GET.class))
				rel = "list";
			else if (hasAnnotation(m, POST.class))
				rel = "add";
		}
		ret.addLink(uri, rel);
	}

	private static void addInstanceService(Method m, Object entity,
			UriInfo uriInfo, RESTServiceDiscovery ret, LinkResource service,
			String rel) {
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(m.getDeclaringClass());
		if(hasAnnotation(m, Path.class))
			uriBuilder.path(findMethod(m, Path.class));
		URI uri = buildURI(uriBuilder, service, entity, m);

		if (rel.length() == 0) {
			if (hasAnnotation(m, GET.class)){
				Class<?> type = m.getReturnType();
				if(Collection.class.isAssignableFrom(type))
					rel = "list";
				else
					rel = "self";
			}else if (hasAnnotation(m, PUT.class))
				rel = "update";
			else if (hasAnnotation(m, POST.class))
				rel = "add";
			else if (hasAnnotation(m, DELETE.class))
				rel = "remove";
		}
		ret.addLink(uri, rel);
	}

	private static URI buildURI(UriBuilder uriBuilder, LinkResource service,
			Object entity, Method m) {
		// see if we need parameters
		String[] uriTemplates = service.pathParameters();
		if (uriTemplates.length > 0) {
			Object[] values = new Object[uriTemplates.length];
			for (int i = 0; i < values.length; i++)
				values[i] = evaluateEL(m, getELContext(m, entity), entity, uriTemplates[i]);
			return uriBuilder.build(values);
		} 
		// do we need any path parameters?
		List<String> paramNames = ((ResteasyUriBuilder)uriBuilder).getPathParamNamesInDeclarationOrder();
		if(paramNames.isEmpty())
			return uriBuilder.build();
		// try to find the IDs
		List<Object> params = findURIParamsFromResource(entity);
		if(params.size() == paramNames.size())
			return uriBuilder.build(params.toArray());
		// if we have too many, ignore the last ones
		if(params.size() > paramNames.size())
			return uriBuilder.build(params.subList(0, paramNames.size()).toArray());
		throw new ServiceDiscoveryException(m, Messages.MESSAGES.notEnoughtUriParameters(paramNames.size(), params.size()));
	}

	private static List<Object> findURIParamsFromResource(Object entity) {
		List<Object> ids = new ArrayList<Object>();
		do{
			List<Object> theseIDs = BeanUtils.findIDs(entity);
			ids.addAll(0, theseIDs);
		}while((entity = BeanUtils.findParentResource(entity)) != null);
		return ids;
	}

	private static LinkELProvider findLinkELProvider(Method m){
		if(hasAnnotation(m, LinkELProvider.class))
			return findAnnotation(m, LinkELProvider.class);
		Class<?> c = m.getDeclaringClass();
		if(c.isAnnotationPresent(LinkELProvider.class))
			return c.getAnnotation(LinkELProvider.class);
		Package p = c.getPackage();
		if(p != null && p.isAnnotationPresent(LinkELProvider.class))
			return p.getAnnotation(LinkELProvider.class);
		return null;
	}

	private static ELProvider getELProvider(Method m){
		LinkELProvider linkElProvider = findLinkELProvider(m);
		if(linkElProvider == null)
			return null;
		Class<? extends ELProvider> elProviderClass = linkElProvider.value();
		try{
			return elProviderClass.newInstance();
		}catch(Exception x){
		   LogMessages.LOGGER.error(Messages.MESSAGES.couldNotInstantiateELProviderClass(elProviderClass.getName()), x);
		   throw new ServiceDiscoveryException(m, Messages.MESSAGES.failedToInstantiateELProvider(elProviderClass.getName()), x);
		}
	}

	private static ELContext getELContext(Method m, Object base){
		ELContext ours = EL.createELContext(base);
		ELProvider elProvider = getELProvider(m);
		if(elProvider != null)
			return elProvider.getContext(ours);
		return ours;
	}

	public static Map<String, ? extends Object> derivePathParameters(UriInfo uriInfo){
		MultivaluedMap<String, String> pathParameters = uriInfo.getPathParameters();
		Map<String, String> ret = new HashMap<String,String>();
		for(Entry<String, List<String>> entry:  pathParameters.entrySet()){
			ret.put(entry.getKey(), entry.getValue().get(0));
		}
		return ret;
	}

	public static Object evaluateEL(Method m, ELContext context, Object base, String expression) {
		try{
			return EL.EXPRESSION_FACTORY.createValueExpression(context, expression,
					Object.class).getValue(context);
		}catch(Exception x){
		   throw new ServiceDiscoveryException(m, Messages.MESSAGES.failedToEvaluateELExpression(expression), x);
		}
	}

	public static Boolean evaluateELBoolean(Method m, ELContext context, Object base, String expression) {
		try{
			return (Boolean) EL.EXPRESSION_FACTORY.createValueExpression(context, expression,
					Boolean.class).getValue(context);
		}catch(Exception x){
		   throw new ServiceDiscoveryException(m, Messages.MESSAGES.failedToEvaluateELExpression(expression), x);

		}
	}

}
