package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "shipto")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shiptotype", propOrder = {
        "name",
        "address",
        "city",
        "country"
})
public class ShipTo {
    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private String address;

    @XmlElement(required = true)
    private String city;

    @XmlElement(required = true)
    private String country;

    @XmlTransient
    private Order order;

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the address.
     *
     * @return the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address.
     *
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the city.
     *
     * @return the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the city.
     *
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Get the country.
     *
     * @return the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the country.
     *
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get the order.
     *
     * @return the order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Set the order.
     *
     * @param order The order to set.
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * JAXB Callback method used to reassociate the item with the owning Order.
     *
     * @param unmarshaller the JAXB {@link Unmarshaller}.
     * @param parent       the owning {@link Contact} instance.
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.setOrder((Order) order);
    }
}
