<%--
  Created by IntelliJ IDEA.
  User: dooreaga
  Date: 18/08/2021
  Time: 13:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>Pricing</title>

    <style>
        tr:first-child{
            font-weight: bold;
            background-color: #C6C9C4;
        }
    </style>
</head>
<body>

<form:form method="POST" modelAttribute="pricing">
    <form:input type="hidden" path="id" id="id"/>
    <form:input type="hidden" path="product.id" value="${product.id}"/>
    <form:input type="hidden" path="product.farmId" value="${product.farmId}"/>
    <table>
        <tr>
            <td>Product Name</td>
            <td>Product Description</td>
        </tr>

        <tr>
            <td>${product.name}</td>
            <td>${product.description}</td>
        </tr>

        <tr>
            <td><label for="price">Price: </label> </td>
            <td><form:input path="price" id="price"/></td>
            <td><form:errors path="price" cssClass="error"/></td>
        </tr>

        <tr>
            <td><label for="weight">Weight: </label> </td>
            <td><form:input path="weight" id="weight"/></td>
            <td><form:errors path="weight" cssClass="error"/></td>
        </tr>

        <tr>
            <td colspan="3">
                <c:choose>
                    <c:when test = "${edit}">
                        <input type="submit" value="Update"/>
                    </c:when>
                    <c:otherwise>
                        <input type="submit" value="Create"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>

    </table>
</form:form>

</body>
</html>
