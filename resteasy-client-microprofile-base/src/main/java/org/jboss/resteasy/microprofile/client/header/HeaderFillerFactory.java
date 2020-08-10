package org.jboss.resteasy.microprofile.client.header;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * 2020-07-10
 */
public interface HeaderFillerFactory {
    /**
     * If multiple {@link HeaderFillerFactory}'s are defined, the one with the highest priority is used
     * @return the priority
     */
    int getPriority();


    /**
     * Creates {@link HeaderFiller}, will be called once for each method with
     * {@link org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam} annotation
     * @param value value of the annotation
     * @param headerName name of the header to generate
     * @param required if true, a failure of header computation will fail the rest client invocation
     * @param interfaceClass JAX-RS interface class
     * @param clientProxy proxy object
     * @return an object that can generate the header value
     */
    HeaderFiller createFiller(String value,
                              String headerName,
                              boolean required,
                              Class<?> interfaceClass,
                              Object clientProxy);
}
