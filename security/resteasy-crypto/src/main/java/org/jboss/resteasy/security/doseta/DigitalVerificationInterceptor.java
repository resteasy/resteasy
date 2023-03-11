package org.jboss.resteasy.security.doseta;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.security.doseta.i18n.LogMessages;
import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.jboss.resteasy.util.InputStreamToByteArray;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class DigitalVerificationInterceptor implements ReaderInterceptor {
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundReadFrom", getClass().getName());
        if (!context.hasProperty(Verifier.class.getName())) {
            return context.proceed();
        }

        //System.out.println("TRACE: found verifier");

        MultivaluedMap<String, String> headers = context.getHeaders();
        List<String> strings = headers.get(DKIMSignature.DKIM_SIGNATURE);
        if (strings == null) {
            throw new UnauthorizedSignatureException(Messages.MESSAGES.thereWasNoSignatureHeader(DKIMSignature.DKIM_SIGNATURE));
        }
        List<DKIMSignature> signatures = new ArrayList<DKIMSignature>();
        for (String headerVal : strings) {
            try {
                signatures.add(new DKIMSignature(headerVal));
            } catch (Exception e) {
                throw new UnauthorizedSignatureException(
                        Messages.MESSAGES.malformedSignatureHeader(DKIMSignature.DKIM_SIGNATURE));
            }
        }

        InputStream old = context.getInputStream();
        try {
            InputStreamToByteArray stream = new InputStreamToByteArray(old);
            context.setInputStream(stream);
            Object rtn = context.proceed();
            byte[] body = stream.toByteArray();
            Verifier verifier = (Verifier) context.getProperty(Verifier.class.getName());

            if (verifier.getRepository() == null) {
                KeyRepository repository = (KeyRepository) context.getProperty(KeyRepository.class.getName());
                if (repository == null) {
                    repository = ResteasyContext.getContextData(KeyRepository.class);
                }
                verifier.setRepository(repository);
            }

            VerificationResults results = verifier.verify(signatures, headers, body);
            if (results.isVerified() == false) {
                throw new UnauthorizedSignatureException(results);
            }
            return rtn;
        } finally {
            context.setInputStream(old);
        }
    }
}
