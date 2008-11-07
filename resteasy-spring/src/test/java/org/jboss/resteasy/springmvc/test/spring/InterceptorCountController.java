package org.jboss.resteasy.springmvc.test.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InterceptorCountController
{
   @Autowired
   MyTestInterceptor interceptor;

   @RequestMapping(method = RequestMethod.GET, value = "/basic/interceptor-test")
   public void getInterceptorCount(@RequestParam("type") String type,
         HttpServletResponse response) throws IOException
   {
      Integer i = interceptor.getCount(type);
      response.setContentType("text/plain");
      response.getOutputStream().print((i == null) ? -1 : i);
   }

}
