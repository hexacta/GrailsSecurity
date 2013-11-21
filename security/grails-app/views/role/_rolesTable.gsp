<%@ page import="com.hexacta.enhanced.authentication.Role" %>
<g:each in="${roleInstance?.roles?.sort {it.id} }" var="role" status="i">
	<g:hiddenField name="roles" value="${role.id}" />
</g:each>
<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>
<table class="table table-striped">
	<thead>
		<tr>
			<g:sortableColumn property="controller" title="${message(code: 'role.name.label', default: 'Role')}" />
			<th></th>
		</tr>
	</thead>
	<tbody>
	<g:each in="${roleInstance.roles?.sort { it.id }}" status="i" var="childRoleInstance">
		<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
			<td><g:message code="security.${fieldValue(bean: childRoleInstance, field: "name")}"/></td>
			<td><g:submitToRemote action="removeRole" id="${childRoleInstance?.id}" update="rolesTable" value="${message(code:'default.button.delete.label', default: 'Delete')}"/></td>
		</tr>
	</g:each>
	</tbody>
</table>
