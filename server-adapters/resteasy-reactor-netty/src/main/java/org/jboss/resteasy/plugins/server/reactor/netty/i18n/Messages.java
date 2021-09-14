package org.jboss.resteasy.plugins.server.reactor.netty.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Sep 1, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
    Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
    int BASE = 22500;

    @Message(id = BASE, value = "Already committed")
    String alreadyCommitted();

    @Message(id = BASE + 5, value = "Already suspended")
    String alreadySuspended();

    @Message(id = BASE + 10, value = "Response write aborted abruptly")
    String responseWriteAborted();
}