<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<spring:url value="resources/css/triplea.css" var="tripleaCSS" />
<spring:url value="resources/js/triplea.js" var="tripleaJS"></spring:url>
<spring:url value="resources/css/bootstrap.min.css" var="bootstrapCSS"></spring:url>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
<link href="${bootstrapCSS}" rel="stylesheet" />
<link href="${tripleaCSS}" rel="stylesheet" />
<script src="${tripleaJS}"></script>
<title>AmazonApiAssingment</title>
</head>
<body>
	<div class="app-body">
		<div class="row">Search from Amazon:</div>
		<form id="searchForm">
			<div class="row">
				<div class='text-box col-lg-6 col col-lg-offset-3'>
					<input name="keyword" id="keyword" type="text"
						class="form-control " />
				</div>
			</div>
			<div class="row">
				<input type="submit" class="btn btn-large btn-success" value="Search" id="searchButton" />
			</div>
		</form>
	</div>
</body>
</html>