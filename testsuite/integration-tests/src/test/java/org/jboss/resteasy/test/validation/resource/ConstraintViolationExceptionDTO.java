package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ConstraintViolationExceptionDTO
{
    @NotNull
    private String id;

    @Size(min = 1)
    private List<String> names;

    public String getId()
    {
        return id;
    }

    public List<String> getNames()
    {
        if (names == null)
        {
            return new ArrayList<>();
        }
        else
        {
            return names;
        }
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setNames(List<String> names)
    {
        this.names = names;
    }
}
