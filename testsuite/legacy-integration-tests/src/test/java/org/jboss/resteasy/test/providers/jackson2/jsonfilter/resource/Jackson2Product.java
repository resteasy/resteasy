package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import com.fasterxml.jackson.annotation.JsonFilter;
/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 *
 */

@JsonFilter(value="nameFilter")
public class Jackson2Product {
    protected String name;

    protected int id;

    public Jackson2Product() {
    }

    public Jackson2Product(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
