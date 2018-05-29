package org.jboss.resteasy.tracing;

public interface RESTEasyTracingEvent {
    /**
     * Name of event, should be unique.
     *
     * @return event name.
     */
    public String name();

    /**
     * Category of event, more events share same category.
     * Is used to format response HTTP header.
     *
     * @return event category.
     */
    public String category();

    /**
     * Level of event.
     * Is used to check if the event is logged according to application/request settings.
     *
     * @return event trace level.
     */
    public RESTEasyTracingLevel level();

    /**
     * Message format. Use {@link String#format(String, Object...)} format.
     * Can be null. In that case message arguments are separated by space.
     *
     * @return message format
     */
    public String messageFormat();

}
