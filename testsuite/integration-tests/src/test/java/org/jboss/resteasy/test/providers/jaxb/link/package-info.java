@XmlJavaTypeAdapters(
      {
            @XmlJavaTypeAdapter(type = Link.class, value = Link.JaxbAdapter.class)
      }) package org.jboss.resteasy.test.providers.jaxb.link;

import jakarta.ws.rs.core.Link;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
