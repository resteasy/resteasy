<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
 pageEncoding="ISO-8859-1"%>
<html>
<head>
    <title>Login Page</title>
</head>
<body bgcolor="#CED8F6">
<h2>Central Auth Server</h2>
<br><br>
<%

if (request.getParameter("client_id") != null) {
   String redirect = request.getParameter("redirect_uri");
   if (request.getParameter("login") != null) {
      out.println("<p><font color=\"red\">This is a remote login from</font>:<b>" + redirect + "</b></p>");
   }
   else
   {
      out.println("<p><b>"  + redirect + "</b> <font color=\"red\">is requesting permission to access your data.</font></p>");
   }
}
%>


<form action="<%= request.getAttribute("OAUTH_FORM_ACTION")%>" method=post>
    <p><strong>Please Enter Your User Name: </strong>
    <input type="text" name="j_username" size="25">
    <p><p><strong>Please Enter Your Password: </strong>
    <input type="password" size="15" name="j_password">
    <p><p>
    <input type="submit" value="Submit">
    <input type="reset" value="Reset">
</form>
</body>
</html>