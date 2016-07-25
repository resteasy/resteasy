package org.jboss.resteasy.test.providers.map.resource;

public abstract class MapProviderAbstractProvider {
    public long getLength() {
        String name = getClass().getSimpleName().replace("Provider", "");
        long size = "writer".length() + name.length();
        return 2 * size;
    }

    public String getWriterName() {
        String name = getClass().getSimpleName().replace("Provider", "Writer");
        return name;
    }

    public String getReaderName() {
        String name = getClass().getSimpleName().replace("Provider", "Reader");
        return name;
    }

}
