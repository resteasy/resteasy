package org.jboss.resteasy.cdi.decorators;

import java.util.ArrayList;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 14, 2012
 */
public class VisitList
{
   static final public String REQUEST_FILTER_DECORATOR_ENTER = "requestFilterDecoratorEnter";
   static final public String REQUEST_FILTER_DECORATOR_LEAVE = "requestFilterDecoratorLeave";
   static final public String RESPONSE_FILTER_DECORATOR_ENTER = "responseFilterDecoratorEnter";
   static final public String RESPONSE_FILTER_DECORATOR_LEAVE = "responseFilterDecoratorLeave";

   static final public String READER_INTERCEPTOR_DECORATOR_ENTER = "readerInterceptorDecoratorEnter";
   static final public String READER_INTERCEPTOR_DECORATOR_LEAVE = "readerInterceptorDecoratorLeave";
   static final public String READER_INTERCEPTOR_ENTER = "readerInterceptorEnter";
   static final public String READER_INTERCEPTOR_LEAVE = "readerInterceptorLeave";
   static final public String READER_DECORATOR_ENTER = "readerDecoratorEnter";
   static final public String READER_DECORATOR_LEAVE = "readerDecoratorLeave";
   
   static final public String WRITER_INTERCEPTOR_DECORATOR_ENTER = "writerInterceptorDecoratorEnter";
   static final public String WRITER_INTERCEPTOR_DECORATOR_LEAVE = "writerInterceptorDecoratorLeave";
   static final public String WRITER_INTERCEPTOR_ENTER = "writerInterceptorEnter";
   static final public String WRITER_INTERCEPTOR_LEAVE = "writerInterceptorLeave";
   static final public String WRITER_DECORATOR_ENTER = "writerDecoratorEnter";
   static final public String WRITER_DECORATOR_LEAVE = "writerDecoratorLeave";
   
   static final public String RESOURCE_INTERCEPTOR_ENTER = "resourceInterceptorEnter";
   static final public String RESOURCE_INTERCEPTOR_LEAVE = "resourceInterceptorLeave";
   static final public String RESOURCE_DECORATOR_ENTER = "resourceDecoratorEnter";
   static final public String RESOURCE_DECORATOR_LEAVE = "resourceDecoratorLeave";
   static final public String RESOURCE_ENTER = "resourceEnter";
   static final public String RESOURCE_LEAVE = "resourceLeave";
   
   static private ArrayList<String> visitList = new ArrayList<String>();
   
   static public void add(String o)
   {
      visitList.add(o);
   }
   
   static public ArrayList<String> getList()
   {
      return new ArrayList<String>(visitList);
   }
}

