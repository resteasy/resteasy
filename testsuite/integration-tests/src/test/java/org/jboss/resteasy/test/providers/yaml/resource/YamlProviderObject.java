package org.jboss.resteasy.test.providers.yaml.resource;

import java.util.Date;
import java.util.Map;

public class YamlProviderObject {

    private Map<String, YamlProviderNestedObject> data;

    private String someText;

    private Date date;

    private YamlProviderNestedObject nested = new YamlProviderNestedObject();

    public YamlProviderNestedObject getNested() {
        return nested;
    }

    public void setNested(YamlProviderNestedObject nested) {
        this.nested = nested;
    }

    public String getSomeText() {
        return someText;
    }

    public void setSomeText(String someText) {
        this.someText = someText;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, YamlProviderNestedObject> getData() {
       return data;
    }

    public void setData(Map<String, YamlProviderNestedObject> data) {
       this.data = data;
    }

    @Override
    public String toString() {
       final StringBuilder sb = new StringBuilder("MyObject{");
       sb.append("someText='").append(someText).append('\'');
       sb.append(", date=").append(date);
       sb.append(", nested=").append(nested);
       sb.append(", data=").append(data);
       sb.append('}');
       return sb.toString();
    }

}
