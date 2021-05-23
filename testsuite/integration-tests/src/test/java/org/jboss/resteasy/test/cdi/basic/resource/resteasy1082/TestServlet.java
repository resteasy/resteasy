package org.jboss.resteasy.test.cdi.basic.resource.resteasy1082;

import java.io.IOException;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
