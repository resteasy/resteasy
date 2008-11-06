package org.jboss.resteasy.springmvc.test.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestInterceptorCountController
{
   @Autowired
   MyTestInterceptor interceptor;

   @RequestMapping(method = RequestMethod.GET, value = "/basic/interceptor-test")
   public void getInterceptorCount(@RequestParam("type") String type,
         HttpServletResponse response, ModelMap model) throws IOException
   {
      Integer i = interceptor.getCount(type);
      if (i == null)
         i = -1;
      model.addAttribute("value", i);

      response.setContentType("text/plain");
      response.getOutputStream().print(String.valueOf(model.get("value")));
   }

}
