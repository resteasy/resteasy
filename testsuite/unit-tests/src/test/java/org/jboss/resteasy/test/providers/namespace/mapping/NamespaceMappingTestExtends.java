package org.jboss.resteasy.test.providers.namespace.mapping;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NamespaceMappingTestExtends complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="NamespaceMappingTestExtends">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/a/TestCanonical}NamespaceMappingTestBase">
 *       &lt;sequence>
 *         &lt;element name="someMoreEl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="element2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamespaceMappingTestExtends", propOrder = {
        "someMoreEl",
        "element2"
})
public class NamespaceMappingTestExtends
        extends NamespaceMappingTestBase {

    @XmlElement(required = true)
    protected String someMoreEl;
    @XmlElement(required = true)
    protected String element2;

    /**
     * Gets the value of the someMoreEl property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSomeMoreEl() {
        return someMoreEl;
    }

    /**
     * Sets the value of the someMoreEl property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSomeMoreEl(String value) {
        this.someMoreEl = value;
    }

    /**
     * Gets the value of the element2 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getElement2() {
        return element2;
    }

    /**
     * Sets the value of the element2 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setElement2(String value) {
        this.element2 = value;
    }

}
