<%@ page import="com.hexacta.enhanced.authentication.AuthenticationUser" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'authenticationUser.label', default: 'AuthenticationUser')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="navbar" role="navigation">
			<div class="navbar-inner">
				<ul class="nav">
					<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
		</div>
		<div id="list-authenticationUser" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table class="table table-striped">
				<thead>
					<tr>
						<g:sortableColumn property="login" title="${message(code: 'authenticationUser.login.label', default: 'Login')}" />
						<g:sortableColumn property="firstName" title="${message(code: 'authenticationUser.firstName.label', default: 'First Name')}" />
						<g:sortableColumn property="lastName" title="${message(code: 'authenticationUser.lastName.label', default: 'Last Name')}" />
						<g:sortableColumn property="email" title="${message(code: 'authenticationUser.email.label', default: 'Email')}" />
						<g:sortableColumn property="role" title="${message(code: 'authenticationUser.role.label', default: 'Role')}" />
						<th><g:message code="authenticationUser.passwordReset.label" default="Password Reset"/></th>
					</tr>
				</thead>
				<tbody>
				<g:each in="${authenticationUserInstanceList}" status="i" var="authenticationUserInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${authenticationUserInstance.id}">${fieldValue(bean: authenticationUserInstance, field: "login")}</g:link></td>
						<td>${fieldValue(bean: authenticationUserInstance, field: "firstName")}</td>
						<td>${fieldValue(bean: authenticationUserInstance, field: "lastName")}</td>
						<td>${fieldValue(bean: authenticationUserInstance, field: "email")}</td>
						<td>${fieldValue(bean: authenticationUserInstance, field: "role")}</td>
						<td><g:link controller="authenticationUser" action="resetPassword" id="${authenticationUserInstance.id}"><g:message code="authenticationUser.passwordReset.label" default="Password Reset"/></g:link></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${authenticationUserInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
