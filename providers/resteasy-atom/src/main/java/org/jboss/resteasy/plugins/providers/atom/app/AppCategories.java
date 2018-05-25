/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.jboss.resteasy.plugins.providers.atom.app;

import org.jboss.resteasy.plugins.providers.atom.Category;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * The app:categories element can contain a "fixed" attribute, with a value of
 * either "yes" or "no", indicating whether the list of categories is a fixed or
 * an open set. The absence of the "fixed" attribute is equivalent to the
 * presence of a "fixed" attribute with a value of "no".
 * 
 * Alternatively, the app:categories element MAY contain an "href" attribute,
 * whose value MUST be an IRI reference identifying a Category Document. If the
 * "href" attribute is provided, the app: categories element MUST be empty and
 * MUST NOT have the "fixed" or "scheme" attributes.
 * 
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "categories")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "categories", propOrder = {
    "category", "href"
})
public class AppCategories extends AppCommonAttributes {

    private static final long serialVersionUID = 7978145545675525082L;
    @XmlElements({
        @XmlElement(name = "category", namespace = "http://www.w3.org/2005/Atom", type = Category.class)
    })
    protected List<Category> category;
    @XmlJavaTypeAdapter( BooleanAdapter.class )
    @XmlAttribute
    protected Boolean fixed;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String scheme;
    @XmlSchemaType(name = "anyURI")
    protected String href;

    public AppCategories() {}
    
    public AppCategories(List<Category> category, Boolean fixed, String scheme,
            String href) {
        super();
        this.category = category;
        this.fixed = fixed;
        this.scheme = scheme;
        this.href = href;
    }

    /**
     * Gets the value of the category property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the category property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Category }
     * @return list of categories
     * 
     */
    public List<Category> getCategory() {
        if (category == null) {
            category = new ArrayList<Category>();
        }
        return this.category;
    }

    /**
     * Gets the value of the fixed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFixed() {
        return fixed;
    }

    /**
     * Sets the value of the fixed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFixed(Boolean value) {
        this.fixed = value;
    }
    
    /**
     * Gets the value of the scheme property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the value of the scheme property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setScheme(String value) {
        this.scheme = value;
    }
    
    /**
     * Gets the value of the href property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setHref(String value) {
        this.href = value;
    }

}
