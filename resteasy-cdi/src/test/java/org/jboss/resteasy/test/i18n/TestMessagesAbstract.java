package org.jboss.resteasy.test.i18n;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 25, 2015
 */
@SuppressWarnings(value = "unchecked")
public abstract class TestMessagesAbstract extends TestMessagesParent {
    private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
    protected static final Annotation testAnnotation = new Annotation() {
        public Class<? extends Annotation> annotationType() {
            return null;
        };
    };

    protected static final Bean<String> testBean = new Bean<String>() {
        public String create(CreationalContext<String> creationalContext) {
            return null;
        }

        public void destroy(String instance, CreationalContext<String> creationalContext) {
        }

        public Set<Type> getTypes() {
            return null;
        }

        public Set<Annotation> getQualifiers() {
            return null;
        }

        public Class<? extends Annotation> getScope() {
            return null;
        }

        public String getName() {
            return null;
        }

        public Set<Class<? extends Annotation>> getStereotypes() {
            return null;
        }

        public Class<?> getBeanClass() {
            return null;
        }

        public boolean isAlternative() {
            return false;
        }

        public Set<InjectionPoint> getInjectionPoints() {
            return null;
        }
    };

    protected static final Set beanSet = new HashSet<Bean<String>>();

    protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);
    protected static final String BASE3 = BASE.substring(0, 3);

    static {
        beanSet.add(testBean);
    }

    @Test
    public void testLocale() throws Exception {
        Locale locale = getLocale();
        String filename = "org/jboss/resteasy/cdi/i18n/Messages.i18n_" + locale.toString() + ".properties";
        if (!before(locale, filename)) {
            LOG.info(getClass() + ": " + filename + " not found.");
            return;
        }

        Assertions.assertEquals(getExpected(BASE + "00", "annotatedTypeNull"), Messages.MESSAGES.annotatedTypeNull());
        Assertions.assertEquals(getExpected(BASE + "05", "beanDoesNotHaveScopeDefined", getClass(), testAnnotation),
                Messages.MESSAGES.beanDoesNotHaveScopeDefined(getClass(), testAnnotation));
        Assertions.assertEquals(getExpected(BASE + "20", "beansFound", getClass().getGenericSuperclass(), beanSet),
                Messages.MESSAGES.beansFound(getClass().getGenericSuperclass(), beanSet));
        Assertions.assertEquals(
                getExpected(BASE3 + "630", "usingInterfaceForLookup", getClass().getGenericSuperclass(), getClass()),
                Messages.MESSAGES.usingInterfaceForLookup(getClass().getGenericSuperclass(), getClass()));
    }

    @Override
    protected int getExpectedNumberOfMethods() {
        return Messages.class.getDeclaredMethods().length;
    }

    protected abstract Locale getLocale();
}
