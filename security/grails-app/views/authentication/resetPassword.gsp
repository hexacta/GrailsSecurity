<html>
	<head>
		<meta name="layout" content="main">
	</head>
	<body>
		<div id="page-body" role="main">
			<h1><g:message code="authentication.authentication"/></h1>
			<g:if test="${flash.message}">
				<g:message code="${flash.message}"/><br/>
			</g:if>
			<h2><g:message code="passwordReset.passwordReset"/></h2>
			<g:form controller="authentication" action="changePassword">
				<g:hiddenField name="login" value="${authenticationUserInstance.login}"/>
				<div class="fieldcontain">
					<label for="newPassword"><g:message code="resetPassword.newPassword" default="New password" /></label>
					<input name="newPassword" value="" type="password"/>
					<g:hasErrors bean="${flash.message}" field="newPassword"><g:renderErrors bean="${flash.message}" field="newPassword"/></g:hasErrors>
				</div>
				<div class="fieldcontain">
					<label for="newPasswordConfirmation"><g:message code="resetPassword.newPasswordConfirmation" default="New password confirmation" /></label>
					<input name="newPasswordConfirmation" value="" type="password"/>
					<g:hasErrors bean="${flash.message}" field="newPasswordConfirmation"><g:renderErrors bean="${flash.message}" field="newPasswordConfirmation"/></g:hasErrors>
				</div>
				<g:actionSubmit action="changePassword" value="${message(code:'resetPassword.submit', default: 'Submit')}" />
			</g:form> 
		</div>
	</body>
</html>