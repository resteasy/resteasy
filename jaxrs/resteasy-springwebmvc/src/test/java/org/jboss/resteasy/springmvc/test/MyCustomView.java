package org.jboss.resteasy.springmvc.test;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

public class MyCustomView implements View {

	public String getContentType() {
		return null;
	}

	public void render(Map model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("application/custom");
		response.getOutputStream().print("Hi, I'm custom!");
	}

}
