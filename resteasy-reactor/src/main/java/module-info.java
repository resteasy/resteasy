/**
 * RESTEasy Reactor Integration module.
 * <p>
 * Integrates RESTEasy with Project Reactor for reactive programming support.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.reactor {

    // Third-party dependencies
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;
    requires org.reactivestreams;
    requires reactor.core;

    // RESTEasy modules
    requires org.jboss.resteasy.core;
    requires org.jboss.resteasy.client;

    // Exports
    exports org.jboss.resteasy.reactor;

    // Provides
    provides org.jboss.resteasy.spi.AsyncResponseProvider with
            org.jboss.resteasy.reactor.MonoProvider;
    provides org.jboss.resteasy.spi.AsyncStreamProvider with
            org.jboss.resteasy.reactor.FluxProvider;
    provides jakarta.ws.rs.client.RxInvokerProvider with
            org.jboss.resteasy.reactor.MonoRxInvokerProvider,
            org.jboss.resteasy.reactor.FluxRxInvokerProvider;
}
