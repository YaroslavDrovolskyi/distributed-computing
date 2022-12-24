<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
<head>
<meta charset="UTF-8">
<title>Deleted <%=(String)request.getAttribute("item")%></title>
</head>
<body>


<div class="container mt-5">
<h2 class="text-center">Delete <%=(String)request.getAttribute("item")%></h2>
<%
if(request.getAttribute("isSuccess") != null){
	boolean isSuccess = (Boolean) request.getAttribute("isSuccess");
	if(isSuccess){
%>
	<div class="alert alert-success">
              Successfully deleted <%=(String)request.getAttribute("item")%>!
	</div>	
<% 
	}
	else{
%>
	<div class="alert alert-danger">
              Error: <%=(String)request.getAttribute("item")%> does not exist
    </div>
<%	
	}	
}	
%> 


<div class="d-flex justify-content-center flex-nowrap mb-5">
  <a href="<%=request.getContextPath()%>/library" class="btn btn-primary btn-lg" role="button" aria-pressed="true">
  See library
  </a>
</div>

</body>
</html>