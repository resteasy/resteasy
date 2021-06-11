package org.jboss.resteasy.test.cdi.stereotype.resource;

public class Dummy
{
    private String name;
    private int age;

    public Dummy(final String name, final int age)
    {
        this.name = name;
        this.age = age;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }
}
