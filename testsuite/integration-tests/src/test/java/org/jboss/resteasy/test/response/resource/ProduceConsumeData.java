package org.jboss.resteasy.test.response.resource;

public class ProduceConsumeData {
    public final String data;
    public final String type;

    public ProduceConsumeData(final String data, final String type) {
        this.data = data;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Data{" +
                "data='" + data + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
