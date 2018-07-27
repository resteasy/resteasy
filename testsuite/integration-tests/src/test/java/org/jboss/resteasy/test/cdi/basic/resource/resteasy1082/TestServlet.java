package org.jboss.resteasy.test.cdi.basic.resource.resteasy1082;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet({"/"})
public class TestServlet extends HttpServlet {
   @Inject
   FooResource foo;

   public TestServlet() {
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.setContentType("text/plain");
      resp.getWriter().write(this.foo.getAll().toString());
   }
}

