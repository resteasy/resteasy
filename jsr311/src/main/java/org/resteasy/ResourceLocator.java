package org.resteasy;

import org.resteasy.spi.HttpInputMessage;
import org.resteasy.spi.HttpOutputMessage;
import org.resteasy.spi.ResourceFactory;

import javax.ws.rs.ext.ProviderFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceLocator extends ResourceInvoker implements ResourceFactory {

    public ResourceLocator(String path, ResourceFactory factory, Method method, ProviderFactory providerFactory) {
        super(path, factory, method, providerFactory);
    }

    public Object createResource(HttpInputMessage input, HttpOutputMessage output) {
        Object resource = factory.createResource(input, output);
        populateUriParams(input);
        Object[] args = getArguments(input);
        try {
            return method.invoke(resource, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public Class<?> getScannableClass() {
        return method.getReturnType();
    }


}
