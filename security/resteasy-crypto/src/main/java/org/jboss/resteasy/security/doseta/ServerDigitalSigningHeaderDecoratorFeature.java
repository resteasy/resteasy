package org.jboss.resteasy.security.doseta;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;

import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.core.ResteasyContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
public class ServerDigitalSigningHeaderDecoratorFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        Signed signed = resourceInfo.getResourceMethod().getAnnotation(Signed.class);
        if (signed == null) {
            signed = (Signed) resourceInfo.getResourceClass().getAnnotation(Signed.class);
        }
        if (signed == null)
            return;

        configurable.register(new DigitalSigningHeaderDecorator(signed));
    }

    /**
     * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
     * @version $Revision: 1 $
     */
    @Priority(Priorities.HEADER_DECORATOR)
    public static class DigitalSigningHeaderDecorator extends AbstractDigitalSigningHeaderDecorator
            implements ContainerResponseFilter {
        public DigitalSigningHeaderDecorator(final Signed signed) {
            this.signed = signed;
        }

        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
                throws IOException {
            KeyRepository repository = ResteasyContext.getContextData(KeyRepository.class);
            DKIMSignature header = createHeader(repository);
            responseContext.getHeaders().add(DKIMSignature.DKIM_SIGNATURE, header);
        }
    }
}
