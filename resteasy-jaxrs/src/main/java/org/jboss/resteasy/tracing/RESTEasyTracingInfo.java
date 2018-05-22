package org.jboss.resteasy.tracing;

import java.util.ArrayList;
import java.util.List;

public class RESTEasyTracingInfo {
    private final List<RESTEasyTracingMessage> messageList = new ArrayList<RESTEasyTracingMessage>();

    public void addMessage(RESTEasyTracingMessage message) {
        messageList.add(message);
    }

    public static String formatDuration(final long duration) {
        if (duration == 0) {
            return " ----";
        } else {
            return String.format("%5.2f", (duration / 1000000.0));
        }
    }

    public static String formatDuration(final long fromTimestamp, final long toTimestamp) {
        return formatDuration(toTimestamp - fromTimestamp);
    }

    public static String formatPercent(final long value, final long top) {
        if (value == 0) {
            return "  ----";
        } else {
            return String.format("%6.2f", 100.0 * value / top);
        }
    }

    public String[] getMessages() {
        // Format: EventCategory [duration / sinceRequestTime | duration/requestTime % ]
        // e.g.:   RI [ 3.88 / 8.93 ms | 1.37 %] message text

        final long fromTimestamp = messageList.get(0).getTimestamp() - messageList.get(0).getDuration();
        final long toTimestamp = messageList.get(messageList.size() - 1).getTimestamp();

        final String[] messages = new String[messageList.size()];

        for (int i = 0; i < messages.length; i++) {
            final RESTEasyTracingMessage message = messageList.get(i);
            final StringBuilder text = new StringBuilder();
            // event
            text.append(String.format("%-11s ", message.getEvent().category()));
            // duration
            text.append('[')
                    .append(formatDuration(message.getDuration()))
                    .append(" / ")
                    .append(formatDuration(fromTimestamp, message.getTimestamp()))
                    .append(" ms |")
                    .append(formatPercent(message.getDuration(), toTimestamp - fromTimestamp))
                    .append(" %] ");
            // text
            text.append(message.toString());
            messages[i] = text.toString();
        }
        return messages;
    }


}
