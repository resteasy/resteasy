package org.jboss.resteasy.tracing;

import org.jboss.resteasy.spi.HttpRequestPreprocessor;

public enum RESTEasyServerTracingEvent implements RESTEasyTracingEvent {

     /**
     * Request processing started.
     */
    // TODO: implement this event
    START(RESTEasyTracingLevel.SUMMARY, "START", null),
    /**
     * All HTTP request headers.
     */
    // TODO: implement this event
    START_HEADERS(RESTEasyTracingLevel.VERBOSE, "START", null),
    /**
     * {@link HttpRequestPreprocessor} invoked.
     */
    PRE_MATCH(RESTEasyTracingLevel.TRACE, "PRE-MATCH", "Filter by %s"),
    /**
     * {@link HttpRequestPreprocessor} invocation summary.
     */
    PRE_MATCH_SUMMARY(RESTEasyTracingLevel.SUMMARY, "PRE-MATCH", "PreMatchRequest summary: %s filters"),
    /**
     * Matching path pattern.
     */
    // TODO: implement this event
    MATCH_PATH_FIND(RESTEasyTracingLevel.TRACE, "MATCH", "Matching path [%s]"),
    /**
     * Path pattern not matched.
     */
    // TODO: implement this event
    MATCH_PATH_NOT_MATCHED(RESTEasyTracingLevel.VERBOSE, "MATCH", "Pattern [%s] is NOT matched"),
    /**
     * Path pattern matched/selected.
     */
    // TODO: implement this event
    MATCH_PATH_SELECTED(RESTEasyTracingLevel.TRACE, "MATCH", "Pattern [%s] IS selected"),
    /**
     * Path pattern skipped as higher-priority pattern has been selected already.
     */
    // TODO: implement this event
    MATCH_PATH_SKIPPED(RESTEasyTracingLevel.VERBOSE, "MATCH", "Pattern [%s] is skipped"),
    /**
     * Matched sub-resource locator method.
     */
    // TODO: implement this event
    MATCH_LOCATOR(RESTEasyTracingLevel.TRACE, "MATCH", "Matched locator : %s"),
    /**
     * Matched resource method.
     */
    // TODO: implement this event
    MATCH_RESOURCE_METHOD(RESTEasyTracingLevel.TRACE, "MATCH", "Matched method  : %s"),
    /**
     * Matched runtime resource.
     */
    // TODO: implement this event
    MATCH_RUNTIME_RESOURCE(RESTEasyTracingLevel.TRACE, "MATCH",
            "Matched resource: template=[%s] regexp=[%s] matches=[%s] from=[%s]"),
    /**
     * Matched resource instance.
     */
    MATCH_RESOURCE(RESTEasyTracingLevel.TRACE, "MATCH", "Resource instance: %s"),
    /**
     * Matching summary.
     */
    // TODO: implement this event
    MATCH_SUMMARY(RESTEasyTracingLevel.SUMMARY, "MATCH", "RequestMatching summary"),
    /**
     * {@link ContainerRequestFilter} invoked.
     */
    REQUEST_FILTER(RESTEasyTracingLevel.TRACE, "REQ-FILTER", "Filter by %s"),
    /**
     * {@link ContainerRequestFilter} invocation summary.
     */
    REQUEST_FILTER_SUMMARY(RESTEasyTracingLevel.SUMMARY, "REQ-FILTER", "Request summary: %s filters"),
    /**
     * Resource method invoked.
     */
    METHOD_INVOKE(RESTEasyTracingLevel.SUMMARY, "INVOKE", "Resource %s method=[%s]"),
    /**
     * Resource method invocation results to JAX-RS {@link Response}.
     */
    DISPATCH_RESPONSE(RESTEasyTracingLevel.TRACE, "INVOKE", "Response: %s"),
    /**
     * {@link ContainerResponseFilter} invoked.
     */
    RESPONSE_FILTER(RESTEasyTracingLevel.TRACE, "RESP-FILTER", "Filter by %s"),
    /**
     * {@link ContainerResponseFilter} invocation summary.
     */
    RESPONSE_FILTER_SUMMARY(RESTEasyTracingLevel.SUMMARY, "RESP-FILTER", "Response summary: %s filters"),
    /**
     * Request processing finished.
     */
    // TODO: implement this event
    FINISHED(RESTEasyTracingLevel.SUMMARY, "FINISHED", "Response status: %s"),
    /**
     * {@link ExceptionMapper} invoked.
     */
    // TODO: implement this event
    EXCEPTION_MAPPING(RESTEasyTracingLevel.SUMMARY, "EXCEPTION", "Exception mapper %s maps %s ('%s') to <%s>");

    private final RESTEasyTracingLevel level;
    private final String category;
    private final String messageFormat;

    private RESTEasyServerTracingEvent(RESTEasyTracingLevel level, String category, String messageFormat) {
        this.level = level;
        this.category = category;
        this.messageFormat = messageFormat;
    }

    @Override
    public String category() {
        return category;
    }

    @Override
    public RESTEasyTracingLevel level() {
        return level;
    }

    @Override
    public String messageFormat() {
        return messageFormat;
    }
}
