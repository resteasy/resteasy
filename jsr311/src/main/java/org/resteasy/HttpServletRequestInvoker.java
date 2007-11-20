package org.resteasy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpServletRequestInvoker {
    public void invoke(HttpServletRequest request, HttpServletResponse response);
    public Pattern getPattern();

}
