package org.resteasy.mock;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Assert {
    public static void notNull(Object obj, String msg) {
        if (obj == null) throw new RuntimeException(msg);
    }
}
