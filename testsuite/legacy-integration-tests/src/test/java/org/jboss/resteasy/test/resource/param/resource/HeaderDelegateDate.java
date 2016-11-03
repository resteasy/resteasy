package org.jboss.resteasy.test.resource.param.resource;

import java.util.Date;

public class HeaderDelegateDate extends Date {
    private static final long serialVersionUID = 1L;

    public HeaderDelegateDate(final long date) {
        super(date);
    }
}
