package org.jboss.resteasy.springmvc.test.spring;

import org.jboss.resteasy.springmvc.test.jaxb.BasicJaxbObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class MyTestJaxbController
{
   public MyTestJaxbController()
   {

   }

   @RequestMapping(value = "/basic/spring/object/json", method = { RequestMethod.GET })
   public String testJaxbJson(ModelMap model)
   {
      updateModel(model);
      return "jsonView";
   }

   @RequestMapping(value = "/basic/spring/object/xml", method = { RequestMethod.GET })
   public String testJaxbXml(ModelMap model)
   {
      updateModel(model);
      return "xmlView";
   }

   private void updateModel(ModelMap model)
   {
      model.put("jaxbObject",
            new BasicJaxbObject("springSomething", new Date()));
   }

}
