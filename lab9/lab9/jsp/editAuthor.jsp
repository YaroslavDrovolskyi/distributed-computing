<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
<html>
<head>
<meta charset="UTF-8">
<title>Edit author</title>
</head>
<body>

	<div class="container mt-5">
		<h2 class="text-center">Edit author</h2>


		<c:if test="${not empty requestScope.isSuccess}">
			<c:choose>
				<c:when test="${requestScope.isSuccess}">
					<div class="alert alert-success">Successfully edited!</div>
				</c:when>
				<c:otherwise>
					<div class="alert alert-danger">${requestScope.errorMessage}
					</div>
				</c:otherwise>
			</c:choose>
		</c:if>


		<form action="${pageContext.request.contextPath}/library/editItem" method="POST" id="editAuthorForm" role="form">
			<div class="form-group col-xs-5">
				<p class="mb-1 mt-3">ID:</p>
				<input type="number" id="authorId" name="authorId" required readonly
					value="${requestScope.author.id}" class="form-control" />

				<p class="mb-1 mt-3">Name:</p>
				<input type="text" name="authorName" id="authorName"
					class="form-control" required placeholder="Author name"
					pattern="[\s|\S]*[\w]+[\s|\S]*" value="${requestScope.author.name}" />
			</div>
			<button type="submit" class="btn btn-info" name="submitEditItem" value="editAuthor">Edit</button>
		</form>

		<div class="d-flex justify-content-center flex-nowrap mb-1">
			<a href="${pageContext.request.contextPath}/library"
				class="btn btn-primary btn-md" role="button" aria-pressed="true">
				See library </a>
		</div>
	</div>

</body>
</html>