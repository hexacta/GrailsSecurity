<%@ page import="com.hexacta.enhanced.authentication.Role" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'role.label', default: 'Role')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="navbar" role="navigation">
			<div class="navbar-inner">
				<ul class="nav">
					<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
		</div>
		<div id="show-role" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list role">
			
				<g:if test="${roleInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="role.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${roleInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${roleInstance?.parentRole}">
					<li class="fieldcontain">
						<span id="parentRole-label" class="property-label"><g:message code="role.parentRole.label" default="Parent Role" /></span>
						<span class="property-value" aria-labelledby="parentRole-label"><g:link controller="role" action="show" id="${roleInstance.parentRole.id}">${roleInstance.parentRole.encodeAsHTML()}</g:link></span>
					</li>
				</g:if>

				<g:if test="${roleInstance?.roles}">
					<li class="fieldcontain">
						<span id="roles-label" class="property-label"><g:message code="role.roles.label" default="Roles" /></span>
						<g:each in="${roleInstance.roles}" var="r">
							<span class="property-value" aria-labelledby="roles-label"><g:link controller="role" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					</li>
				</g:if>

				<g:if test="${roleInstance?.permissions}">
					<li class="fieldcontain">
						<span id="permissions-label" class="property-label"><g:message code="role.permissions.label" default="Permissions" /></span>
						<g:each in="${roleInstance.permissions.sort { it.id }}" var="r">
							<span class="property-value" aria-labelledby="permissions-label"><g:message code="controller.${r.controller.label}"/>: <g:message code="method.${r.method.label}"/></span>
						</g:each>
					</li>
				</g:if>
			
				<g:if test="${roleInstance?.components}">
					<li class="fieldcontain">
						<span id="components-label" class="property-label"><g:message code="role.components.label" default="Components" /></span>
						<g:each in="${roleInstance.components.sort { it.id }}" var="r">
							<span class="property-value" aria-labelledby="components-label"><g:message code="security.${r?.encodeAsHTML()}"/></span>
						</g:each>
					</li>
				</g:if>
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${roleInstance?.id}" />
					<g:link class="edit" action="edit" id="${roleInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
