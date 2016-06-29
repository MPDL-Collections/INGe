<%@page import="java.util.Enumeration"%>
<%@page import="de.mpg.mpdl.inge.aa.AaServerConfiguration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%
		AaServerConfiguration configuration = new AaServerConfiguration();
	
		if (configuration.getMap().size() == 1)
		{
			response.sendRedirect("login?" + request.getQueryString() + "&target=" + URLEncoder.encode(configuration.getMap().values().iterator().next(), "ISO-8859-1") + "clientLogin");
		} else { %>
			<head>
				<title>Select Login Mechanism</title>
			</head>
			<body style="text-align: center;">
				<h1>Select Login Mechanism</h1>
				<% for (String key : configuration.getMap().keySet()) { %>
					<div><a href="login?<%= request.getQueryString() %>&target=<%= URLEncoder.encode(configuration.getMap().get(key), "ISO-8859-1") %>clientLogin"><%= key %></a></div>
				<% } %>
			</body>
	<% } %>
</html>
