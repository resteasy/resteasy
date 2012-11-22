/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.resteasy.cdi.util;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;


/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@Singleton
@CounterBinding
@ApplicationScoped
public class Counter
{
   public static final int INITIAL_VALUE = 17;
   private static AtomicInteger counter = new AtomicInteger(INITIAL_VALUE);
   
   public int getNext()
   {
      System.out.println("In Counter: counter: " + counter);
      return counter.getAndIncrement();
   }
   
   public void reset()
   {
      counter.set(INITIAL_VALUE);
   }
}

