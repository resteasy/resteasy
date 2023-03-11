package org.jboss.resteasy.plugins.delegates;

import java.util.Date;

import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.DateUtil;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DateDelegate implements RuntimeDelegate.HeaderDelegate<Date> {
    public static final DateDelegate INSTANCE = new DateDelegate();

    @Override
    public Date fromString(String value) {
        if (value == null)
            throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
        return DateUtil.parseDate(value);
    }

    @Override
    public String toString(Date value) {
        if (value == null)
            throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
        return DateUtil.formatDate(value);
    }
}
