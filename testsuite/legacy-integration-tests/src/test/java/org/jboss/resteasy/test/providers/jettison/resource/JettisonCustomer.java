package org.jboss.resteasy.test.providers.jettison.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JettisonCustomer implements Serializable {

    private static final long serialVersionUID = -6949084426046457758L;

    @XmlAttribute(name = "id", required = true)
    protected Long id = null;

    @XmlAttribute(required = true)
    protected String surname = "";

    protected String firstName = "";

    @XmlAttribute(required = true)
    protected String customerNumber = "NnVn-001";

    protected Date since = null;

    @XmlTransient
    protected int activeYears;

    protected String details;

    @XmlTransient
    protected Date generated = null;

    @XmlAttribute
    protected Date updated = null;

    public JettisonCustomer() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public int getActiveYears() {
        final GregorianCalendar now = new GregorianCalendar();
        final GregorianCalendar sinceCal = new GregorianCalendar();

        Date temp = since;
        if (temp == null) {
            temp = new Date();
        }

        sinceCal.setTime(temp);
        activeYears = now.get(Calendar.YEAR) - sinceCal.get(Calendar.YEAR);

        return activeYears;
    }

    public String getSinceAsString(int style, Locale locale) {
        Date temp = since;
        if (temp == null) {
            temp = new Date();
        }

        final DateFormat df = DateFormat.getDateInstance(style, locale);
        return df.format(temp);
    }

    public void setSince(String since, int style, Locale locale) {
        final DateFormat df = DateFormat.getDateInstance(style, locale);

        try {
            this.since = df.parse(since);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }

    public void setActiveYears(int activeYears) {
        this.activeYears = activeYears;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getGenerated() {
        return generated;
    }

    public void setGenerated(Date generated) {
        this.generated = generated;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "JettisonCustomer{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", firstName='" + firstName + '\'' +
                ", customerNumber='" + customerNumber + '\'' +
                ", since=" + getSinceAsString(DateFormat.MEDIUM, Locale.ENGLISH) +
                ", activeYears=" + getActiveYears() +
                ", details='" + details + '\'' +
                ", generated=" + generated +
                ", updated=" + updated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JettisonCustomer that = (JettisonCustomer) o;

        if (surname != null ? !surname.equals(that.surname) : that.surname != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        return since != null ? since.equals(that.since) : that.since == null;

    }

    @Override
    public int hashCode() {
        int result = surname != null ? surname.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (since != null ? since.hashCode() : 0);
        return result;
    }
}
