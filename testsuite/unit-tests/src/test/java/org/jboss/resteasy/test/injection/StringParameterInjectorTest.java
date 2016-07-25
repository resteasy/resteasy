package org.jboss.resteasy.test.injection;

import org.jboss.resteasy.core.StringParameterInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.injection.resource.StringParameterInjectorGenericType;
import org.jboss.resteasy.test.injection.resource.StringParameterInjectorInjected;
import org.jboss.resteasy.test.injection.resource.StringParameterInjectorType;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

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
        ResteasyProviderFactory.pushContext(StringParameterInjectorInjected.class, new StringParameterInjectorInjected(MY_SPECIAL_STRING));

        Field declaredField = StringParameterInjectorType.class.getDeclaredField("name");
        StringParameterInjector injector = new StringParameterInjector(String.class, String.class, "name",
                StringParameterInjectorType.class, null, declaredField,
                declaredField.getAnnotations(), new ResteasyProviderFactory());

        assertSame("Ignored annotation missing", MY_SPECIAL_STRING, injector.extractValue("ignored"));
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
                new Annotation[0], new ResteasyProviderFactory());
        final Object result = injector.extractValue("");
        assertNotNull("Injector should not return null", result);
    }



}

