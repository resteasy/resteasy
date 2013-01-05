<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
 pageEncoding="ISO-8859-1"%>
<html>
<head>
    <title>Commerce Realm</title>
</head>
<body bgcolor="#CED8F6">
<h1>Commerce Realm</h1>
Welcome <b><%=request.getUserPrincipal().getName()%></b>!.
<h2>Realm Applications</h2>
<p><a href="https://localhost:8443/customer-portal">Customer Portal</a></p>
<p><a href="https://localhost:8443/product-portal">Product Portal</a></p>
<br>
<p><a href="https://localhost:8443/auth-server/j_oauth_logoff">logout</a></p>
</body>
</html>