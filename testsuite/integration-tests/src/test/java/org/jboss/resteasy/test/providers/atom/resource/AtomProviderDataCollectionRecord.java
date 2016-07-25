package org.jboss.resteasy.test.providers.atom.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for AtomProviderDataCollectionRecord complex type.
 * <p>
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * <p>
 * <pre>
 * &lt;complexType name=&quot;AtomProviderDataCollectionRecord&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;timestamp&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}dateTime&quot;/&gt;
 *         &lt;element name=&quot;collectedData&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AtomProviderDataCollectionRecord", propOrder = {"timestamp", "collectedData"})
public class AtomProviderDataCollectionRecord {

    @XmlElement(required = true)
    protected XMLGregorianCalendar timestamp;
    @XmlElement(required = true)
    protected String collectedData;

    /**
     * Gets the value of the timestamp property.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the collectedData property.
     *
     * @return possible object is {@link String }
     */
    public String getCollectedData() {
        return collectedData;
    }

    /**
     * Sets the value of the collectedData property.
     *
     * @param value allowed object is {@link String }
     */
    public void setCollectedData(String value) {
        this.collectedData = value;
    }

}
