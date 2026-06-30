/**
 * RESTEasy RxJava 2 Integration module.
 * <p>
 * Integrates RESTEasy with RxJava 2 for reactive programming support.
 * </p>
 *
 * @since 6.2.17
 */
module org.jboss.resteasy.rxjava {
    // Jakarta EE APIs
    requires jakarta.ws.rs;

    // Third-party dependencies
    requires io.reactivex.rxjava2;
    requires org.jboss.logging;
    requires static org.jboss.logging.annotations;

    // RESTEasy modules
    requires org.jboss.resteasy.client;
    requires org.jboss.resteasy.core;

    // Exports
    exports org.jboss.resteasy.rxjava2;

    // Opens
    opens org.jboss.resteasy.rxjava2.propagation to org.jboss.resteasy.core;

    // Provides
    provides jakarta.ws.rs.client.RxInvokerProvider with
            org.jboss.resteasy.rxjava2.SingleRxInvokerProvider,
            org.jboss.resteasy.rxjava2.ObservableRxInvokerProvider,
            org.jboss.resteasy.rxjava2.FlowableRxInvokerProvider;
}
