package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import jakarta.enterprise.inject.spi.CDI;

public class RestClientExtension implements Extension {

    private Set<RestClientData> proxyTypes = new LinkedHashSet<>();

    private Set<Throwable> errors = new LinkedHashSet<>();

    public void registerRestClient(@Observes
                                   @WithAnnotations(RegisterRestClient.class) ProcessAnnotatedType<?> type) {
        Class<?> javaClass = type.getAnnotatedType().getJavaClass();
        if (javaClass.isInterface()) {
            RegisterRestClient annotation = type.getAnnotatedType().getAnnotation(RegisterRestClient.class);
            Optional<String> maybeUri = extractBaseUri(annotation);
            Optional<String> maybeConfigKey = extractConfigKey(annotation);

            proxyTypes.add(new RestClientData(javaClass, maybeUri, maybeConfigKey));
            type.veto();
        } else {
            errors.add(new IllegalArgumentException("Rest client needs to be an interface " + javaClass));
        }
    }

    private Optional<String> extractBaseUri(RegisterRestClient annotation) {
        String baseUri = annotation.baseUri();
        return Optional.ofNullable("".equals(baseUri) ? null : baseUri);
    }

    private Optional<String> extractConfigKey(RegisterRestClient annotation) {
        String configKey = annotation.configKey();
        return Optional.ofNullable("".equals(configKey) ? null : configKey);
    }

    public void createProxy(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
        for (RestClientData clientData : proxyTypes) {
            afterBeanDiscovery.addBean(new RestClientDelegateBean(clientData.javaClass, beanManager, clientData.baseUri, clientData.configKey));
        }
    }

    public void reportErrors(@Observes AfterDeploymentValidation afterDeploymentValidation) {
        for (Throwable error : errors) {
            afterDeploymentValidation.addDeploymentProblem(error);
        }
    }

    /**
     *
     * @deprecated this method is not supported and will eventually be deleted
     * @return {@code true} if CDI is believed to be activated, otherwise {@code false}
     */
    @Deprecated
    public static boolean isCDIActive() {
        try {
            return CDI.current().getBeanManager() != null;
        } catch (IllegalStateException ise) {
            // This happens when a CDIProvider is not available.
            return false;
        }
    }

    /**
     * This method currently does nothing.
     *
     * @deprecated this method is not supported and will eventually be deleted
     */
    @Deprecated
    public static void clearBeanManager() {
        // nothing to do
    }

    private static class RestClientData {
        private final Class<?> javaClass;
        private final Optional<String> baseUri;
        private final Optional<String> configKey;

        private RestClientData(final Class<?> javaClass, final Optional<String> baseUri, final Optional<String> configKey) {
            this.javaClass = javaClass;
            this.baseUri = baseUri;
            this.configKey = configKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RestClientData that = (RestClientData) o;
            return javaClass.equals(that.javaClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(javaClass);
        }
    }

    /**
     * Lifted from CdiConstructorInjector in resteasy-cdi
     * @deprecated this method is not supported and will eventually be deleted
     */
    @Deprecated
    public static Object construct(Class<?> clazz){
        BeanManager manager;
        try {
            manager = CDI.current().getBeanManager();
        } catch (IllegalStateException ignore) {
            return null;
        }
        if (manager != null) {
            Set<Bean<?>> beans = manager.getBeans(clazz);
            if (beans.isEmpty()) {
                return null;
            }

            if (beans.size() > 1) {
                Set<Bean<?>> modifiableBeans = new HashSet<>();
                modifiableBeans.addAll(beans);
                // Ambiguous dependency may occur if a resource has subclasses
                // Therefore we remove those beans
                for (Iterator<Bean<?>> iterator = modifiableBeans.iterator(); iterator.hasNext();){
                    Bean<?> bean = iterator.next();
                    if (!bean.getBeanClass().equals(clazz) && !bean.isAlternative()){
                        // remove Beans that have clazz in their type closure but not as a base class
                        iterator.remove();
                    }
                }
                beans = modifiableBeans;
            }
            Bean<?> bean = manager.resolve(beans);
            if (bean == null) {
                return null;
            }
            CreationalContext<?> context = manager.createCreationalContext(bean);
            if (context == null) {
                return null;
            }
            return manager.getReference(bean, clazz, context);
        }else {
            // CDI is not active.
            return null;
        }
    }
}
