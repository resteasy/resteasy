package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "respond_test")
public class StringCharsetRespond {
    protected String word;

    public StringCharsetRespond() {

    }

    public StringCharsetRespond(final String _word) {
        this.word = _word;
    }

    @XmlElement(name = "word")
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
