<%@ page import="com.hexacta.enhanced.authentication.Role" %>
<g:each in="${roleInstance?.components?.sort {it.id} }" var="component" status="i">
	<g:hiddenField name="components" value="${component.id}" />
</g:each>
<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>
<table class="table table-striped">
	<thead>
		<tr>
			<g:sortableColumn property="controller" title="${message(code: 'component.name.label', default: 'Controller')}" />
			<th></th>
		</tr>
	</thead>
	<tbody>
	<g:each in="${roleInstance.components?.sort { it.id }}" status="i" var="componentInstance">
		<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
			<td><g:message code="security.${fieldValue(bean: componentInstance, field: "name")}"/></td>
			<td><g:submitToRemote action="removeComponent" id="${componentInstance?.id}" update="componentsTable" value="${message(code:'default.button.delete.label', default: 'Delete')}"/></td>
		</tr>
	</g:each>
	</tbody>
</table>
