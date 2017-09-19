package com.tjresearch.api.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class ApiGatewayServlet
 */
public class ApiGatewayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ApplicationContext context;
	private ApiGatewayHand apiHand;
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		apiHand = context.getBean(ApiGatewayHand.class);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		apiHand.handle(request,response);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		apiHand.handle(req,resp);
	}
	

}
