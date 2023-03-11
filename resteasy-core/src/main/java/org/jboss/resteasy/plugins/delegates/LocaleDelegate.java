package org.jboss.resteasy.plugins.delegates;

import java.util.Locale;

import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.LocaleHelper;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocaleDelegate implements RuntimeDelegate.HeaderDelegate<Locale> {
    public static final LocaleDelegate INSTANCE = new LocaleDelegate();

    public Locale fromString(String value) throws IllegalArgumentException {
        if (value == null)
            throw new IllegalArgumentException(Messages.MESSAGES.localeValueNull());
        return LocaleHelper.extractLocale(value);
    }

    public String toString(Locale value) {
        if (value == null)
            throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
        return LocaleHelper.toLanguageString(value);
    }

}
