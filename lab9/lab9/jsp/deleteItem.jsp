<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
<head>
<meta charset="UTF-8">
<title>Deleted <%=(String) request.getAttribute("item")%></title>
</head>
<body>
	<div class="container mt-5">
		<h2 class="text-center">Delete ${requestScope.item}</h2>
		<c:if test="${not empty requestScope.isSuccess}">
			<c:choose>
				<c:when test="${requestScope.isSuccess}">
					<div class="alert alert-success">Successfully deleted
						${requestScope.item}</div>
				</c:when>
				<c:otherwise>
					<div class="alert alert-danger">Selected
						${requestScope.item} does not exist</div>
				</c:otherwise>
			</c:choose>
		</c:if>
		
		<div class="d-flex justify-content-center flex-nowrap mb-5">
			<a href="${pageContext.request.contextPath}/library"
				class="btn btn-primary btn-lg" role="button" aria-pressed="true">
				See library </a>
		</div>
	</div>
</body>
</html>