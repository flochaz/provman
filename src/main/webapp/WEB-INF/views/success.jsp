<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Registration Confirmation Page</title>
</head>
<body>
	${success}
	<br/>
	<br/>
	<c:choose>
		<c:when test = "${returnPage == 'product'}">
			Go back to <a href="<c:url value='/product/${farmId}' />">List of All Products</a>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test = "${returnPage == 'pricing'}">
					Go back to <a href="<c:url value='/product/edit/${prodId}' />">List of All Buying Options</a>
				</c:when>
				<c:otherwise>
					Go back to <a href="<c:url value='/' />">List of All Providers</a>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	
</body>

</html>