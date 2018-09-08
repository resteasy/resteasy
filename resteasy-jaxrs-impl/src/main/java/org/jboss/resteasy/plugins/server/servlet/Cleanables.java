package org.jboss.resteasy.plugins.server.servlet;

import java.util.HashSet;
/**
*
* @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
* @version $Revision: 1.1 $
*
* Copyright Jul 23, 2015
*/
public class Cleanables
{
	private HashSet<Cleanable> cleanables = new HashSet<Cleanable>();

	public HashSet<Cleanable> getCleanables()
	{
		return cleanables;
	}

	public void addCleanable(Cleanable cleanable)
	{
		cleanables.add(cleanable);
	}
}
