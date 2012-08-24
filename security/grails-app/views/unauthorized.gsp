<!doctype html>
<html>
	<head>
		<title>Unauthorized</title>
		<meta name="layout" content="main">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
	</head>
	<body>
		<g:message code="default.unauthorized.message"/><br/>
		<a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>	
	</body>
</html>