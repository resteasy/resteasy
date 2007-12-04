package org.resteasy;

import org.resteasy.spi.HttpInput;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ParameterExtractor {
    Object extract(HttpInput request);
}
