package org.jboss.resteasy.springmvc.test.spring;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class MyTestInterceptor implements HandlerInterceptor
{
   private Map<String, Integer> hitCount;

   public MyTestInterceptor()
   {
      super();
      this.hitCount = new HashMap<String, Integer>();
      hitCount.put("afterCompletion", 0);
      hitCount.put("postHandle", 0);
      hitCount.put("preHandle", 0);

   }

   public void afterCompletion(HttpServletRequest request,
         HttpServletResponse response, Object handler, Exception ex)
         throws Exception
   {
      increment("afterCompletion");
   }

   public void postHandle(HttpServletRequest request,
         HttpServletResponse response, Object handler, ModelAndView modelAndView)
         throws Exception
   {
      increment("postHandle");
   }

   public boolean preHandle(HttpServletRequest request,
         HttpServletResponse response, Object handler) throws Exception
   {
      increment("preHandle");
      return true;
   }

   private void increment(String key)
   {
      this.hitCount.put(key, getCount(key) + 1);
   }

   public Integer getCount(String type)
   {
      return hitCount.get(type);
   }

}
