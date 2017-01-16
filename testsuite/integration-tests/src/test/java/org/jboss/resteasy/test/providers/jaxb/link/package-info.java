@XmlJavaTypeAdapters(
        {
                @XmlJavaTypeAdapter(type = Link.class, value = Link.JaxbAdapter.class)
        }) package org.jboss.resteasy.test.providers.jaxb.link;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;