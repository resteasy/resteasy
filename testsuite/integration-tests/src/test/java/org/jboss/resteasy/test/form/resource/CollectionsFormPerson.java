package org.jboss.resteasy.test.form.resource;

import java.util.List;
import java.util.Map;

import org.jboss.resteasy.annotations.Form;

public class CollectionsFormPerson {
    @Form(prefix = "telephoneNumbers")
    public List<CollectionsFormTelephoneNumber> telephoneNumbers;
    @Form(prefix = "address")
    public Map<String, CollectionsFormAddress> adresses;
}
