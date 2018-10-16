package org.jboss.resteasy.test.spring.deployment.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: rsearls
 * Date: 2/20/17
 */
@RestController
public class GreetingController {

   @RequestMapping("/greeting")
   public String greeting() {
      return "World";
   }
}
