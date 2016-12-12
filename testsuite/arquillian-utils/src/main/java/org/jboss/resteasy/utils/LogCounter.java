package org.jboss.resteasy.utils;

/**
 * Counter for log messages in log file.
 */
public class LogCounter {
    /**
     * Examined log message
     */
    private String message;

    /**
     * Initial count of examined log message
     */
    private int initCount;

    /**
     * Log file is accessed differently if test is run on server, or as client
     */
    private boolean onServer;


    public LogCounter(String message, boolean onServer) {
        this.message = message;
        this.onServer = onServer;
        initCount = TestUtil.getWarningCount(message, onServer);
    }

    /**
     * Get count of examined log message, logged after creation of this LogCounter
     */
    public int count() {
        return TestUtil.getWarningCount(message, onServer) - initCount;
    }
}