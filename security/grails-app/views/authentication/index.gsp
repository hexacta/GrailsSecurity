<html>
	<head>
		<meta name="layout" content="main">
	</head>
	<body>
		<div id="page-body" role="main">
			<h1><g:message code="authentication.authentication"/></h1>
			<security:ifLoggedIn>
				<g:message code="authentication.loggedInAs"/> <security:user/>
				<h2><g:message code="authentication.logOut"/></h2>
				<security:form authAction="logout" success="[controller:'authentication', action:'index']" error="[controller:'authentication', action:'index']">
					<g:actionSubmit value="Log out"/> 
				</security:form>
			</security:ifLoggedIn>
			<security:ifNotLoggedIn>
				<g:if test="${flash.authenticationFailure}">
					<g:message code="authentication.failure"/> <g:message code="authentication.failure.${flash.authenticationFailure.result}"/><br/>
				</g:if>
				<p><g:message code="authentication.notLoggedIn"/></p>
				<h2><g:message code="authentication.logIn"/></h2>
				<security:form authAction="login" success="[controller:'authentication', action:'mainPage']" error="[controller:'authentication', action:'index']">
					<div class="fieldcontain">
						<label for="login"><g:message code="authenticationUser.login.label" default="Username" /></label>
						<g:textField name="login" value="${flash.loginForm?.login?.encodeAsHTML()}"/>
						<g:hasErrors bean="${flash.loginFormErrors}" field="login"><g:renderErrors bean="${flash.loginFormErrors}" as="list" field="login"/></g:hasErrors>
					</div>
					<div class="fieldcontain">
						<label for="password"><g:message code="authenticationUser.password.label" default="Password" /></label>
						<input name="password" value="" type="password"/>
						<g:hasErrors bean="${flash.loginFormErrors}" field="password"><g:renderErrors bean="${flash.loginFormErrors}" as="list" field="password"/></g:hasErrors>
					</div>
					<g:actionSubmit value="Log in"/> 
				</security:form>
			</security:ifNotLoggedIn>
		</div>
	</body>
</html>