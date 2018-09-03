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


    /**
     * Container qualifier when arquillian starts multiple instance, null otherwise.
     */
    private String containerQualifier;


    public LogCounter(String message, boolean onServer, String containerQualifier) {
        this.message = message;
        this.onServer = onServer;
        this.containerQualifier = containerQualifier;
        this.initCount = TestUtil.getWarningCount(message, onServer, containerQualifier);
    }

    public LogCounter(String message, boolean onServer) {
        this(message, onServer, null);
    }

    /**
     * Get count of examined log message, logged after creation of this LogCounter
     */
    public int count() {
        return TestUtil.getWarningCount(message, onServer, containerQualifier) - initCount;
    }
}