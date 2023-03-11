package org.jboss.resteasy.test.providers.sse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import org.jboss.logging.Logger;

@WebListener
public class ExecutorServletContextListener implements ServletContextListener {

    public static final String TEST_EXECUTOR = "testExecutor";

    private static final Logger logger = Logger.getLogger(ExecutorServletContextListener.class);

    private ExecutorService executors = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        executors = Executors.newCachedThreadPool();
        sce.getServletContext().setAttribute(TEST_EXECUTOR, executors);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Object executors = sce.getServletContext().getAttribute(TEST_EXECUTOR);
        if (executors != null) {
            ExecutorService service = ((ExecutorService) executors);
            service.shutdownNow();
            try {
                if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                    logger.warn("ExecutorService for server sent events isn't termitted in 10 secs");
                }
            } catch (InterruptedException e) {
                //
            }
        }

    }

}
