<%@ page import="com.hexacta.enhanced.authentication.Role" %>
<%@ page import="com.hexacta.enhanced.authentication.Component" %>
<%@ page import="com.hexacta.enhanced.authentication.ControllerConfiguration" %>
<%@ page import="com.hexacta.enhanced.authentication.AuthenticationUtils" %>
<div class="fieldcontain ${hasErrors(bean: roleInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="role.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${roleInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: role, field: 'allowDeletion', 'error')} ">
	<label for="allowDeletion">
		<g:message code="role.allowDeletion.label" default="Allow Deletion" />
	</label>
	<g:checkBox name="allowDeletion" value="${roleInstance?.allowDeletion}" />
</div>

<div class="fieldcontain ${hasErrors(bean: roleInstance, field: 'roles', 'error')} ">
	<label for="roles">
		<g:message code="role.roles.label" default="Roles" />
	</label>
	<g:select name="roles" from="${Role.findAllByNameNotEqual(roleInstance.name)}" multiple="multiple" optionKey="id" size="5" value="${roleInstance?.roles*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain">
	<label for="permissionController">
		<g:message code="permission.label" default="Permission" />
	</label>
	<g:select name="permissionController" from="${ControllerConfiguration.list().sort { it.id }}"  optionKey="name" optionValue="label" valueMessagePrefix="${AuthenticationUtils.I18N_PREFFIX}controller" onchange="${remoteFunction(action:'updateMethods', params: '\'selectedController=\' + this.value', update: [success:'methodSelect', failure: 'methodSelect'])}"/>
	<div id="methodSelect" class="inline-div"><g:select name="permissionMethod" from="${ControllerConfiguration.get(1).methods.sort { it.id }}" optionKey="name" optionValue="label" valueMessagePrefix="${AuthenticationUtils.I18N_PREFFIX}method"/></div>
	<g:submitToRemote action="addPermission" update="permissionsTable" value="${message(code: 'default.button.addPermission', default: 'Add Permission')}"/>
	<div id="permissionsTable">
		<g:render template="permissionsTable"/>	
	</div>
</div>

<div class="fieldcontain">
	<label for="controller">
		<g:message code="component.label" default="Component" />
	</label>
	<g:select name="componentName" from="${Component.list()}" valueMessagePrefix="security"/>
	<g:submitToRemote action="addComponent" update="componentsTable" value="${message(code: 'default.button.addComponent', default: 'Add Component')}"/>
	<div id="componentsTable">
		<g:render template="componentsTable"/>	
	</div>
</div>
