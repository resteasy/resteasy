package org.jboss.resteasy.test.providers.jaxb.regression;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
 public class TestBean
 {
    private String name;

    public String getName()
    {
       return name;
    }

    public void setName(String name)
    {
       this.name = name;
    }

 }