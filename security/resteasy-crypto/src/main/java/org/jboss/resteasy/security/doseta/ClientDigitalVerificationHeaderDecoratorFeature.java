package org.jboss.resteasy.security.doseta;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.security.doseta.Verifications;
import org.jboss.resteasy.annotations.security.doseta.Verify;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ConstrainedTo(RuntimeType.CLIENT)
public class ClientDigitalVerificationHeaderDecoratorFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        Verify verify = resourceInfo.getResourceMethod().getAnnotation(Verify.class);
        Verifications verifications = resourceInfo.getResourceClass().getAnnotation(Verifications.class);

        if (verify != null || verifications != null) {
            configurable.register(new DigitalVerificationHeaderDecorator(verify, verifications));
        }

    }

    @Priority(Priorities.HEADER_DECORATOR)
    public static class DigitalVerificationHeaderDecorator extends AbstractDigitalVerificationHeaderDecorator
            implements ClientResponseFilter {
        public DigitalVerificationHeaderDecorator(final Verify verify, final Verifications verifications) {
            this.verify = verify;
            this.verifications = verifications;
        }

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            requestContext.setProperty(Verifier.class.getName(), create());
        }

    }
}
