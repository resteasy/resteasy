package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.annotations.Form;

import java.util.List;
import java.util.Map;

public class CollectionsFormPerson {
    @Form(prefix = "telephoneNumbers")
    public List<CollectionsFormTelephoneNumber> telephoneNumbers;
    @Form(prefix = "address")
    public Map<String, CollectionsFormAddress> adresses;
}
