package org.jboss.resteasy.resteasy1223;

import java.util.Date;
import java.util.Map;

public class MyObject {

    private String someText;

    private Date date;

    private MyNestedObject nested = new MyNestedObject();

    private Map<String, MyNestedObject> data;

    public MyNestedObject getNested() {
        return nested;
    }

    public void setNested(MyNestedObject nested) {
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

    public Map<String, MyNestedObject> getData() {
        return data;
    }

    public void setData(Map<String, MyNestedObject> data) {
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
