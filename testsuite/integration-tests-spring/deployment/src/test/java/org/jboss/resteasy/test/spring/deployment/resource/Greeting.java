package org.jboss.resteasy.test.spring.deployment.resource;

/**
 * User: rsearls
 * Date: 2/20/17
 */
public class Greeting {

   private final long id;
   private final String content;

   public Greeting(final long id, final String content) {
      this.id = id;
      this.content = content;
   }

   public long getId() {
      return id;
   }

   public String getContent() {
      return content;
   }
}
