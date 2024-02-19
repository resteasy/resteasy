package org.jboss.resteasy.test.injection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.StringParameterInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.injection.resource.StringParameterInjectorGenericType;
import org.jboss.resteasy.test.injection.resource.StringParameterInjectorInjected;
import org.jboss.resteasy.test.injection.resource.StringParameterInjectorType;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Injection tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for org.jboss.resteasy.annotations.Decorator class.
 * @tpSince RESTEasy 3.0.16
 */
public class StringParameterInjectorTest {

    private static final String MY_SPECIAL_STRING = "MySpecialString";

    /**
     * @tpTestDetails Unmarshaller test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldInjectForAnnotationConfiguredUnmarshaller() throws Exception {
        ResteasyContext.pushContext(StringParameterInjectorInjected.class,
                new StringParameterInjectorInjected(MY_SPECIAL_STRING));

        Field declaredField = StringParameterInjectorType.class.getDeclaredField("name");
        StringParameterInjector injector = new StringParameterInjector(String.class, String.class, "name",
                StringParameterInjectorType.class, null, declaredField,
                declaredField.getAnnotations(), ResteasyProviderFactory.newInstance());

        assertSame(MY_SPECIAL_STRING, injector.extractValue("ignored"), "Ignored annotation missing");
    }

    /**
     * @tpTestDetails Instantiation test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void instantiation() throws Exception {
        final Type type = StringParameterInjectorGenericType.class.getDeclaredMethod("returnSomething").getGenericReturnType();
        final StringParameterInjector injector = new StringParameterInjector(
                List.class, type, "ignored", String.class, null, null,
                new Annotation[0], ResteasyProviderFactory.newInstance());
        final Object result = injector.extractValue("");
        assertNotNull(result, "Injector should not return null");
    }

}
