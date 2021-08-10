<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>List of Providers</title>

	<style>
		tr:first-child{
			font-weight: bold;
			background-color: #C6C9C4;
		}
	</style>

</head>


<body>
	<h2>List of Providers</h2>	
	<table>
		<tr>
			<td>NAME</td><td>Entering Date</td><td>Nationality</td><td>CODE</td><td></td>
		</tr>
		<c:forEach items="${providers}" var="provider">
			<tr>
			<td>${provider.name}</td>
			<td>${provider.enteringDate}</td>
			<td>${provider.nationality}</td>
			<td><a href="<c:url value='/edit-${provider.code}-provider' />">${provider.code}</a></td>
			<td><a href="<c:url value='/delete-${provider.code}-provider' />">delete</a></td>
			</tr>
		</c:forEach>
	</table>
	<br/>
	<a href="<c:url value='/new' />">Add New Provider</a>
</body>
</html>