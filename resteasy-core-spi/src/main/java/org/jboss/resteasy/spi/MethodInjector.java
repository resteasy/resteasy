package org.jboss.resteasy.spi;

/**
 * Will invoke a method in the context of an HTTP request. Does all the parameter injection for you.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MethodInjector {
    /**
     * Invoke on a method in the context of an HTTP request. Does all JAX-RS parameter injection.
     *
     * @param request  http request
     * @param response http response
     * @param target   target object
     * @return returned object or CompletionStage<Object> if arguments need async
     * @throws Failure if application failure occurred
     */
    Object invoke(HttpRequest request, HttpResponse response, Object target) throws Failure, ApplicationException;

    /**
     * Create the arguments that would be used to invoke the method in the context of an HTTP request.
     *
     * @param request  http request
     * @param response http response
     * @return array of arguments or CompletionStage<Object[]> if args need to be constructed async
     * @throws Failure if application failure occurred
     */
    Object injectArguments(HttpRequest request, HttpResponse response) throws Failure;

    ValueInjector[] getParams();

    boolean expectsBody();
}
