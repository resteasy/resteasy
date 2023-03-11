package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class MapJaxb {
    @XmlAnyElement
    List<JAXBElement<Entry>> value = new ArrayList<JAXBElement<Entry>>();

    @XmlTransient
    private String entryName;
    @XmlTransient
    private String keyAttributeName;
    @XmlTransient
    private String namespace;

    public MapJaxb() {
    }

    public MapJaxb(final String entryName, final String keyAttributeName, final String namespace) {
        this.entryName = entryName;
        this.namespace = namespace;
        this.keyAttributeName = keyAttributeName;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Entry {
        @XmlAnyElement
        Object value;

        @XmlAnyAttribute
        Map<QName, Object> attribute = new HashMap<QName, Object>();

        @XmlTransient
        private String key;

        @XmlTransient
        private String keyAttributeName;

        public Entry() {
        }

        public Entry(final String keyAttributeName, final String key, final Object value) {
            this.value = value;
            this.keyAttributeName = keyAttributeName;
            setKey(key);
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getKey() {
            if (key != null) {
                return key;
            }
            key = (String) attribute.values().iterator().next();
            return key;
        }

        public void setKey(String keyValue) {
            this.key = keyValue;
            attribute.clear();

            QName name = new QName(keyAttributeName);
            attribute.put(name, keyValue);
        }
    }

    public void addEntry(String key, Object val) {
        Entry entry = new Entry(keyAttributeName, key, val);
        JAXBElement<Entry> element = new JAXBElement<Entry>(new QName(namespace, entryName), Entry.class, entry);
        value.add(element);
    }

    public List<JAXBElement<Entry>> getValue() {
        return value;
    }
}
