<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
<html>
<head>
<meta charset="UTF-8">
<title>Create author</title>
</head>
<body>




<div class="container mt-5">
<h2 class="text-center">Create author</h2>
<%
if(request.getAttribute("isSuccess") != null){
	boolean isSuccess = (Boolean) request.getAttribute("isSuccess");
	if(isSuccess){
%>
	<div class="alert alert-success">
              Successfully created!
	</div>	
<% 
	}
	else{
%>
	<div class="alert alert-danger">
              <%= (String) request.getAttribute("errorMessage")%>
    </div>
<%	
	}	
}	
%> 
  
  <form action="<%=request.getContextPath()%>/library/createAuthor" method="POST" id="createAuthorForm" role="form" >
  <!-- createBookAction is name of parameter, createBookId is value of parameter -->
    <input type="hidden" id="createAuthorAction" name="createAuthorAction" value="createBookId"/>
    <div class="form-group col-xs-5">
        <p class="mb-1 mt-3">ID:</p>
        <input type="number" id="authorId" name="authorId" required="true" min="0" max="100000000" step="1" value="0"
        class="form-control">
        <p class="mb-1 mt-3">
        Name:</p>
        <input type="text" name="authorName" id="authorName" class="form-control" required="true" 
                 placeholder="Author name" pattern="[\s|\S]*[\w]+[\s|\S]*"/> <!-- [[\s]*[\S]+[\s]*]+ -->                    
    </div>
    <button type="submit" class="btn btn-info" value="Create">Create</button>
  </form>
  
  	<div class="d-flex justify-content-center flex-nowrap mb-1">
	  	<a href="<%=request.getContextPath()%>/library" class="btn btn-primary btn-md" role="button" aria-pressed="true">
	  	See library
	  	</a>
	</div>
</div> 

</body>
</html>