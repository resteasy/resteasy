package org.jboss.resteasy.resteasy1236;

import java.util.Date;

public class YamlProviderObject {

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


    @Override
    public String toString() {
        return "YamlProviderObject[" + nested + "," + date + "," + nested + "]";
    }

}
