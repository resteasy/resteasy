package org.jboss.resteasy.test.providers.resource.jaxbNameSpacePrefix;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JaxbNameSpacePrefixItems complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="JaxbNameSpacePrefixItems">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jaxbNameSpacePrefixItem" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/po}JaxbNameSpacePrefixItem" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JaxbNameSpacePrefixItems", propOrder = {
        "jaxbNameSpacePrefixItem"
})
public class JaxbNameSpacePrefixItems {

    @XmlElement(required = true)
    protected List<JaxbNameSpacePrefixItem> jaxbNameSpacePrefixItem;

    /**
     * Gets the value of the jaxbNameSpacePrefixItem property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the jaxbNameSpacePrefixItem property.
     * <p>
     * <p>
     * For example, to add a new jaxbNameSpacePrefixItem, do as follows:
     * <pre>
     *    getJaxbNameSpacePrefixItem().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JaxbNameSpacePrefixItem }
     */
    public List<JaxbNameSpacePrefixItem> getJaxbNameSpacePrefixItem() {
        if (jaxbNameSpacePrefixItem == null) {
            jaxbNameSpacePrefixItem = new ArrayList<JaxbNameSpacePrefixItem>();
        }
        return this.jaxbNameSpacePrefixItem;
    }

}
