package org.jboss.resteasy.microprofile.client.header;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * 2020-07-10
 */
public class DefaultHeaderFillerFactory implements HeaderFillerFactory {
    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public HeaderFiller createFiller(String value,
                                     String headerName,
                                     boolean required,
                                     Class<?> interfaceClass,
                                     Object clientProxy) {
        return new DefaultHeaderFiller(value, headerName, required, interfaceClass, clientProxy);
    }
}
