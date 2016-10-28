package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement(name = "item")
public class JaxbMarshallingSoakItem {
    String description;
    int price;
    String requestID;
    String dummy1;
    String dummy2;
    String dummy3;
    String dummy4;
    String dummy5;
    String dummy6;
    String dummy7;
    String dummy8;
    Map<String, String> harness;

    public Map<String, String> getHarness() {
        return this.harness;
    }

    public void setHarness(Map<String, String> harness) {
        this.harness = harness;
    }

    public String getDummy1() {
        return dummy1;
    }

    public void setDummy1(String dummy1) {
        this.dummy1 = dummy1;
    }

    public String getDummy2() {
        return dummy2;
    }

    public void setDummy2(String dummy2) {
        this.dummy2 = dummy2;
    }

    public String getDummy3() {
        return dummy3;
    }

    public void setDummy3(String dummy3) {
        this.dummy3 = dummy3;
    }

    public String getDummy4() {
        return dummy4;
    }

    public void setDummy4(String dummy4) {
        this.dummy4 = dummy4;
    }

    public String getDummy5() {
        return dummy5;
    }

    public void setDummy5(String dummy5) {
        this.dummy5 = dummy5;
    }

    public String getDummy6() {
        return dummy6;
    }

    public void setDummy6(String dummy6) {
        this.dummy6 = dummy6;
    }

    public String getDummy7() {
        return dummy7;
    }

    public void setDummy7(String dummy7) {
        this.dummy7 = dummy7;
    }

    public String getDummy8() {
        return dummy8;
    }

    public void setDummy8(String dummy8) {
        this.dummy8 = dummy8;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestID() {
        return this.requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Price " + this.price);
        buffer.append(" Description " + this.description);
        buffer.append(" RequestID " + this.requestID);
        buffer.append(" Dummy1 " + this.dummy1);
        buffer.append(" Dummy2 " + this.dummy2);
        buffer.append(" Dummy3 " + this.dummy3);
        buffer.append(" Dummy4 " + this.dummy4);
        buffer.append(" Dummy5 " + this.dummy5);
        buffer.append(" Dummy6 " + this.dummy6);
        buffer.append(" Dummy7 " + this.dummy7);
        buffer.append(" Dummy8 " + this.dummy8);
        buffer.append(" HASHNAP " + this.harness.toString());

        return buffer.toString();
    }
}
