<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.*"%>
<%@ page import="ua.drovolskyi.dc.lab9.library.*"%>

<!DOCTYPE html>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<html>
<head>
<meta charset="UTF-8">
<title>Library</title>
</head>
<body>


	<h1 class="text-center">Library</h1>


	<h2 class="text-center">Authors</h2>
	<div class="container">
		<c:choose>
			<c:when test="${not empty authorsList}">
				<table class="table table-striped">
					<thead>
						<tr>
							<td>ID</td>
							<td>Name</td>
							<td></td>
							<td></td>
						</tr>
					</thead>
					<c:forEach var="author" items="${authorsList}">
						<tr>
							<td>${author.id}</td>
							<td>${author.name}</td>
							
							
							<td>
								<form action="${pageContext.request.contextPath}/library/editItem"
									method="POST" id="requestForEditItemForm" role="form">
									<input type="hidden" id="authorId" name="authorId" value="${author.id}"/>
									<button type="submit" name="requestEditItem" value="editAuthor"
										class="btn bg-warning text-white">
										<i class="fa fa-pencil"></i>
									</button>
								</form>
							</td>
							<td>
								<form action="${pageContext.request.contextPath}/library/deleteItem"
									method="POST" id="requestForDeleteItemForm" role="form">
									<input type="hidden" id="authorId" name="authorId" value="${author.id}"/>
									<button type="submit" name="requestDeleteItem" value="deleteAuthor"
										class="btn bg-danger text-white">
										<i class="fa fa-trash"></i>
									</button>
								</form>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				<br>
				</br>
				<div class="alert alert-info">No authors</div>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="d-flex justify-content-center flex-nowrap mb-5">
		<a href="<%=request.getContextPath()%>/library/createAuthor"
			class="btn btn-primary btn-lg" role="button" aria-pressed="true">
			Create author </a>
	</div>


	<h2 class="text-center">Books</h2>
	<div class="container">
		<c:choose>
			<c:when test="${not empty booksList}">
				<table class="table table-striped">
					<thead>
						<tr>
							<td>ISBN</td>
							<td>Title</td>
							<td>Publishing year</td>
							<td>Number of pages</td>
							<td>Author ID</td>
							<td></td>
							<td></td>
						</tr>
					</thead>
					<c:forEach var="book" items="${booksList}">
						<tr>
							<td>${book.ISBN}</td>
							<td>${book.title}</td>
							<td>${book.year}</td>
							<td>${book.numberPages}</td>
							<td>${book.author.id}</td>


							<td>
								<form action="${pageContext.request.contextPath}/library/editItem"
									method="POST" id="requestForEditItemForm" role="form">
									<input type="hidden" id="bookISBN" name="bookISBN" value="${book.ISBN}"/>
									<button type="submit" name="requestEditItem" value="editBook"
										class="btn bg-warning text-white">
										<i class="fa fa-pencil"></i>
									</button>
								</form>
							</td>
							<td>
								<form action="${pageContext.request.contextPath}/library/deleteItem"
									method="POST" id="requestForDeleteItemForm" role="form">
									<input type="hidden" id="bookISBN" name="bookISBN" value="${book.ISBN}"/>
									<button type="submit" name="requestDeleteItem" value="deleteBook"
										class="btn bg-danger text-white">
										<i class="fa fa-trash"></i>
									</button>
								</form>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				<br>
				</br>
				<div class="alert alert-info">No books</div>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="d-flex justify-content-center flex-nowrap mb-5">
		<a href="<%=request.getContextPath()%>/library/createBook"
			class="btn btn-primary btn-lg" role="button" aria-pressed="true">
			Create book </a>
	</div>


</body>
</html>