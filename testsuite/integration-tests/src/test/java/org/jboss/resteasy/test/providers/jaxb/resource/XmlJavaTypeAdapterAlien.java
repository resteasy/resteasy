package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(XmlJavaTypeAdapterAlienAdapter.class)
public class XmlJavaTypeAdapterAlien {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof XmlJavaTypeAdapterAlien)) {
            return false;
        }
        return name.equals(XmlJavaTypeAdapterAlien.class.cast(o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
