package com.damnhandy.resteasy.test;

import com.damnhandy.resteasy.annotations.WebResource;
import com.damnhandy.resteasy.annotations.WebResources;

/**
 * 
 * @author Ryan J. McDonough
 * Jan 29, 2007
 *
 */
@WebResources(
		resources={@WebResource(id="foo",value = "/contacts"),
				   @WebResource(id="moo",value = "/contacts/{contactId}")}
				   		
	)
public class DummyResource2 {

}
