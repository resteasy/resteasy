package org.jboss.resteasy.jsapi;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class ServiceRegistry
{
	private final static Logger logger = Logger
       .getLogger(ServiceRegistry.class);

	private static final long serialVersionUID = -1985015444704126795L;

	private ResourceMethodRegistry registry;

	private ResteasyProviderFactory providerFactory;

	private ServiceRegistry parent;

	private ArrayList<MethodMetaData> methods;

	private ArrayList<ServiceRegistry> locators;

	private ResourceLocatorInvoker locator;

	private String uri;

	private String functionPrefix;

	public ServiceRegistry(ServiceRegistry parent, ResourceMethodRegistry registry, 
			ResteasyProviderFactory providerFactory, ResourceLocatorInvoker locator)
	{
		this.parent = parent;
		this.registry = registry;
		this.providerFactory = providerFactory;
		this.locator = locator;
		if(locator != null){
			Method method = locator.getMethod();
			Path methodPath = method.getAnnotation(Path.class);
			Class<?> declaringClass = method.getDeclaringClass();
			Path classPath = declaringClass.getAnnotation(Path.class);
			this.uri = MethodMetaData.appendURIFragments(parent, classPath, methodPath);
			if(parent.isRoot())
				this.functionPrefix = declaringClass.getSimpleName() + "." + method.getName();
			else
				this.functionPrefix = parent.getFunctionPrefix() + "." + method.getName();
		}
		scanRegistry();
	}
	
	private void scanRegistry() {
		methods = new ArrayList<MethodMetaData>();
		locators = new ArrayList<ServiceRegistry>();
		for (Entry<String, List<ResourceInvoker>> entry : registry.getBounded().entrySet())
		{
			List<ResourceInvoker> invokers = entry.getValue();
			for (ResourceInvoker invoker : invokers)
			{
				if (invoker instanceof ResourceMethodInvoker)
				{
					methods.add(new MethodMetaData(this, (ResourceMethodInvoker) invoker));
				} else if(invoker instanceof ResourceLocatorInvoker)
				{
					ResourceLocatorInvoker locator = (ResourceLocatorInvoker) invoker;
					Method method = locator.getMethod();
					Class<?> locatorType = method.getReturnType();
					Class<?>[] locatorResourceTypes = GetRestful.getSubResourceClasses(locatorType);
					for (Class<?> locatorResourceType : locatorResourceTypes)
					{
					   if (locatorResourceType == null)
					   {
					      // FIXME: we could generate an error for the client, which would be more informative than
					      // just logging this
					      if(logger.isWarnEnabled()){
					         logger.warn("Impossible to generate JSAPI for subresource returned by method "+
					               method.getDeclaringClass().getName()+"."+method.getName()+
					               " since return type is not a static JAXRS resource type");
					      }
					      // skip this
					      continue;
					   }
					   ResourceMethodRegistry locatorRegistry = new ResourceMethodRegistry(providerFactory);
					   locatorRegistry.addResourceFactory(null, null, locatorResourceType);
					   locators.add(new ServiceRegistry(this, locatorRegistry, providerFactory, locator));
					}
				}
			}
		}
	}


	public List<MethodMetaData> getMethodMetaData()
	{
		return methods;
	}

	public List<ServiceRegistry> getLocators() {
		return locators;
	}

	public String getUri() {
		return uri;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public String getFunctionPrefix() {
		return functionPrefix;
	}
	
	public void collectResourceMethodsUntilRoot(List<Method> methods){
		if(isRoot())
			return;
		methods.add(locator.getMethod());
		parent.collectResourceMethodsUntilRoot(methods);
	}

}
