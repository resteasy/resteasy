package org.jboss.resteasy.plugins.providers.html.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 25, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages {
    Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
    int BASE = 6000;

    @Message(id = BASE + 0, value = "No dispatcher found for path '%s'")
    String noDispatcherFound(String path);
}
