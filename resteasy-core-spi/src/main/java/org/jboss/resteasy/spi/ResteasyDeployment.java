package org.jboss.resteasy.spi;

import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Application;

/**
 * This class is used to configure and initialize the core components of RESTEasy.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
public interface ResteasyDeployment {
    static boolean onServer() {
        return ResteasyProviderFactory.getInstance().isOnServer();
    }

    void start();

    void merge(ResteasyDeployment other);

    void registration();

    void stop();

    boolean isUseContainerFormParams();

    void setUseContainerFormParams(boolean useContainerFormParams);

    List<String> getJndiComponentResources();

    void setJndiComponentResources(List<String> jndiComponentResources);

    String getApplicationClass();

    void setApplicationClass(String applicationClass);

    String getInjectorFactoryClass();

    void setInjectorFactoryClass(String injectorFactoryClass);

    boolean isDeploymentSensitiveFactoryEnabled();

    void setDeploymentSensitiveFactoryEnabled(boolean deploymentSensitiveFactoryEnabled);

    boolean isAsyncJobServiceEnabled();

    void setAsyncJobServiceEnabled(boolean asyncJobServiceEnabled);

    int getAsyncJobServiceMaxJobResults();

    void setAsyncJobServiceMaxJobResults(int asyncJobServiceMaxJobResults);

    long getAsyncJobServiceMaxWait();

    void setAsyncJobServiceMaxWait(long asyncJobServiceMaxWait);

    int getAsyncJobServiceThreadPoolSize();

    void setAsyncJobServiceThreadPoolSize(int asyncJobServiceThreadPoolSize);

    String getAsyncJobServiceBasePath();

    void setAsyncJobServiceBasePath(String asyncJobServiceBasePath);

    Application getApplication();

    void setApplication(Application application);

    boolean isRegisterBuiltin();

    void setRegisterBuiltin(boolean registerBuiltin);

    List<String> getProviderClasses();

    void setProviderClasses(List<String> providerClasses);

    List<Object> getProviders();

    void setProviders(List<Object> providers);

    List<Class> getActualProviderClasses();

    void setActualProviderClasses(List<Class> actualProviderClasses);

    List<Class> getActualResourceClasses();

    void setActualResourceClasses(List<Class> actualResourceClasses);

    boolean isSecurityEnabled();

    void setSecurityEnabled(boolean securityEnabled);

    List<String> getJndiResources();

    void setJndiResources(List<String> jndiResources);

    List<String> getResourceClasses();

    void setResourceClasses(List<String> resourceClasses);

    Map<String, String> getMediaTypeMappings();

    void setMediaTypeMappings(Map<String, String> mediaTypeMappings);

    List<Object> getResources();

    void setResources(List<Object> resources);

    Map<String, String> getLanguageExtensions();

    void setLanguageExtensions(Map<String, String> languageExtensions);

    Registry getRegistry();

    void setRegistry(Registry registry);

    Dispatcher getDispatcher();

    void setDispatcher(Dispatcher dispatcher);

    ResteasyProviderFactory getProviderFactory();

    void setProviderFactory(ResteasyProviderFactory providerFactory);

    void setMediaTypeParamMapping(String paramMapping);

    List<ResourceFactory> getResourceFactories();

    void setResourceFactories(List<ResourceFactory> resourceFactories);

    List<String> getUnwrappedExceptions();

    void setUnwrappedExceptions(List<String> unwrappedExceptions);

    Map<String, String> getConstructedDefaultContextObjects();

    void setConstructedDefaultContextObjects(Map<String, String> constructedDefaultContextObjects);

    Map<Class, Object> getDefaultContextObjects();

    void setDefaultContextObjects(Map<Class, Object> defaultContextObjects);

    List<String> getScannedResourceClasses();

    void setScannedResourceClasses(List<String> scannedResourceClasses);

    List<String> getScannedProviderClasses();

    void setScannedProviderClasses(List<String> scannedProviderClasses);

    List<String> getScannedJndiComponentResources();

    void setScannedJndiComponentResources(List<String> scannedJndiComponentResources);

    Map<String, List<String>> getScannedResourceClassesWithBuilder();

    void setScannedResourceClassesWithBuilder(Map<String, List<String>> scannedResourceClassesWithBuilder);

    boolean isWiderRequestMatching();

    void setWiderRequestMatching(boolean widerRequestMatching);

    boolean isAddCharset();

    void setAddCharset(boolean addCharset);

    InjectorFactory getInjectorFactory();

    void setInjectorFactory(InjectorFactory injectorFactory);

    Object getProperty(String key);

    void setProperty(String key, Object value);

    void setStatisticsEnabled(boolean statisticsEnabled);
}
