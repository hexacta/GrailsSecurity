<%@ page import="com.hexacta.enhanced.authentication.Role" %>
<%@ page import="com.hexacta.enhanced.authentication.AuthenticationUtils" %>
<g:each in="${roleInstance?.permissions?.sort {it.id} }" var="permission" status="i">
	<g:hiddenField name="permissions" value="${permission.id}" />
</g:each>
<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>
<table class="table table-striped">
	<thead>
		<tr>
			<g:sortableColumn property="controller" title="${message(code: 'permission.controller.label', default: 'Controller')}" />
			<g:sortableColumn property="method" title="${message(code: 'permission.method.label', default: 'Method')}" />
			<th></th>
		</tr>
	</thead>
	<tbody>
	<g:each in="${roleInstance.permissions?.sort { it.id }}" status="i" var="permissionInstance">
		<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
			<td>${message(code: AuthenticationUtils.I18N_PREFFIX + 'controller.' + permissionInstance.controller.label)}</td>
			<td>${message(code: AuthenticationUtils.I18N_PREFFIX + 'method.' + permissionInstance.method.label)}</td>
			<td><g:submitToRemote action="removePermission" id="${permissionInstance?.id}" update="permissionsTable" value="${message(code:'default.button.delete.label', default: 'Delete')}"/></td>
		</tr>
	</g:each>
	</tbody>
</table>
