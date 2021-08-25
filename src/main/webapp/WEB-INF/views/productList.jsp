<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Products</title>

    <style>
        tr:first-child{
            font-weight: bold;
            background-color: #C6C9C4;
        }
    </style>

</head>


<body>
<h2>List of Products For Farm #${farmId}</h2>
<table>
    <tr>
        <td>ID</td><td>Name</td><td>Description</td><td>Weight (Kg)</td><td>Price ($)</td><td></td><td></td>
    </tr>
    <c:forEach items="${products}" var="product">
            <tr>
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>${product.description}</td>
                <td><a href="<c:url value='/product/edit/${product.id}' />">select</a></td>
                <td><a href="<c:url value='/product/delete/${farmId}/${product.id}' />">delete</a></td>
            </tr>
    </c:forEach>
</table>
<br/>
<a href="<c:url value='/product/new/${farmId}' />">Add New Product</a>
</body>
</html>