package org.jboss.resteasy.plugins.server.reactor.netty;

import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * @deprecated use the new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
public class NettyUtil {
    public static boolean isIoThread() {
        return Thread.currentThread() instanceof FastThreadLocalThread;
    }
}
