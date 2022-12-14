package org.jboss.resteasy.test.form.resource;

import java.util.List;
import java.util.Map;

import org.jboss.resteasy.annotations.Form;

public class NestedCollectionsFormPerson {
    @Form(prefix = "telephoneNumbers")
    public List<NestedCollectionsFormTelephoneNumber> telephoneNumbers;
    @Form(prefix = "address")
    public Map<String, NestedCollectionsFormAddress> adresses;
}
