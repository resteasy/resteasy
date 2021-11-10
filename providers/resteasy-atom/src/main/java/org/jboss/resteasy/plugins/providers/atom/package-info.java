@XmlSchema(namespace = "http://www.w3.org/2005/Atom",
//        attributeFormDefault = XmlNsForm.QUALIFIED,
      elementFormDefault = XmlNsForm.QUALIFIED
)
@XmlJavaTypeAdapters(
      {
            @XmlJavaTypeAdapter(type = URI.class, value = UriAdapter.class),
            @XmlJavaTypeAdapter(type = MediaType.class, value = MediaTypeAdapter.class)
      }) package org.jboss.resteasy.plugins.providers.atom;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.net.URI;
