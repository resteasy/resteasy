package org.jboss.resteasy.test.cdi.injection.resource;

import static org.jboss.resteasy.test.cdi.injection.resource.ReverseInjectionResource.NON_CONTEXTUAL;

import java.util.HashMap;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Stateful;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Stateful
@Dependent
public class StatefulDependentScopedEJBwithJaxRsComponents implements StatefulDependentScopedEJBwithJaxRsComponentsInterface {
    private static HashMap<String, HashMap<String, Object>> store = new HashMap<String, HashMap<String, Object>>();
    private static int constructions;
    private static int destructions;

    @Inject
    int secret;

    public static int getConstructions() {
        return constructions;
    }

    public static int getDestructions() {
        return destructions;
    }

    @PostConstruct
    public void postConstruct() {
        constructions++;
        log.info(this + " secret: " + secret);
    }

    @PreDestroy
    public void preDestroy() {
        destructions++;
    }

    @Inject
    private Logger log;
    @Inject
    private CDIInjectionBookResource resource;
    @Inject
    private CDIInjectionBookReader reader;
    @Inject
    private CDIInjectionBookWriter writer;

    @Override
    public void setUp(String key) {
        log.info("entering StatefulDependentScopedEJBwithJaxRsComponents.setUp()");
        HashMap<String, Object> substore = new HashMap<String, Object>();
        substore.put("secret", resource.theSecret());
        substore.put(CDIInjectionBookResource.BOOK_READER, reader);
        substore.put(CDIInjectionBookResource.BOOK_WRITER, writer);
        substore.put("this", this);
        store.put(key, substore);
    }

    /**
     * This is a SFSB. See discussion in ReverseInjectionEJBHolder.test().
     * <p>
     * If NON_CONTEXTUAL.equals(key), then this bean was obtained from JNDI, and
     * it is not a CDI contextual object. It follows that it is not dependency
     * scoped, which means it will not be recreated, with new injections, upon a
     * second invocation.
     * <p>
     * Otherwise, it will be recreated, and CDI will redo the injections.
     */
    @Override
    public boolean test(String key) {
        log.info("entering StatefulDependentScopedEJBwithJaxRsComponents.test(" + key + ")");
        HashMap<String, Object> substore = store.get(key);
        int savedSecret = Integer.class.cast(substore.get("secret"));
        log.info("stored resource secret = resource secret: " + (savedSecret == resource.theSecret()));
        log.info("stored reader = reader:                   " + (substore.get(CDIInjectionBookResource.BOOK_READER) == reader));
        log.info("stored writer = writer:                   " + (substore.get(CDIInjectionBookResource.BOOK_WRITER) == writer));

        boolean result = true;
        result &= reader == substore.get(CDIInjectionBookResource.BOOK_READER); // application scoped
        result &= writer == substore.get(CDIInjectionBookResource.BOOK_WRITER); // application scoped
        if (NON_CONTEXTUAL.equals(key)) {
            result &= resource.theSecret() == savedSecret; // request scope not applicable
        } else {
            result &= resource.theSecret() != savedSecret; // request scoped
        }
        return result;
    }

    @Override
    public Class<?> theClass() {
        return StatefulDependentScopedEJBwithJaxRsComponents.class;
    }

    @Override
    public boolean theSame(ReverseInjectionEJBInterface ejb) {
        if (ejb == null) {
            return false;
        }
        Class<?> c = ejb.theClass();
        if (!StatefulDependentScopedEJBwithJaxRsComponents.class.equals(c)) {
            log.info(ejb + " not instanceof StatefulDependentScopedEJBwithJaxRsComponents: " + c);
            return false;
        }
        log.info(this.secret + " " + ejb.theSecret());
        return this.secret == ejb.theSecret();
    }

    @Override
    public int theSecret() {
        return secret;
    }
}
