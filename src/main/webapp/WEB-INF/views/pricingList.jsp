<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%--
  Created by IntelliJ IDEA.
  User: dooreaga
  Date: 18/08/2021
  Time: 13:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Pricings</title>

    <style>
        tr:first-child{
            font-weight: bold;
            background-color: #C6C9C4;
        }
    </style>
</head>
<body>

<table>
    <tr>
        <td>Name</td>
        <td>Description</td>
    </tr>

    <tr>
        <td>${product.name}</td>
        <td>${product.description}</td>
    </tr>
</table>

<table>
    <tr>
        <td>Weight</td>
        <td>Price</td>
    </tr>

    <c:forEach items="${product.productPricings}" var="pricing">
        <tr>
            <td>${pricing.weight} kg.</td>
            <td>$${pricing.price}</td>
            <td><a href="<c:url value='/pricing/edit/${pricing.id}' />">edit</a></td>
            <td><a href="<c:url value='/pricing/delete/${product.id}/${pricing.id}' />">delete</a></td>
        </tr>
    </c:forEach>

    <tr>
        <td colspan="2">
            <a href="<c:url value='/pricing/new/${product.id}' />">Create New Buying Option</a>
        </td>
    </tr>
</table>

</body>
</html>
