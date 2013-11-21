<%@ page import="com.hexacta.enhanced.authentication.Role" %>
<%@ page import="com.hexacta.enhanced.authentication.Component" %>
<%@ page import="com.hexacta.enhanced.authentication.ControllerConfiguration" %>
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

<div class="fieldcontain">
	<label for="permissionController">
		<g:message code="permission.label" default="Permission" />
	</label>
	<g:select name="permissionController" from="${ControllerConfiguration.list().sort { it.id }}"  optionKey="name" optionValue="label" onchange="${remoteFunction(action:'updateMethods', params: '\'selectedController=\' + this.value', update: [success:'methodSelect', failure: 'methodSelect'])}"/>
	<div id="methodSelect" class="inline-div"><g:select name="permissionMethod" from="${ControllerConfiguration.get(1).methods.sort { it.id }}" optionKey="name" optionValue="label"/></div>
	<g:submitToRemote action="addPermission" update="permissionsTable" value="${message(code: 'default.button.addPermission', default: 'Add Permission')}"/>
	<div id="permissionsTable">
		<g:render template="permissionsTable"/>	
	</div>
</div>

<div class="fieldcontain">
	<label for="componentName">
		<g:message code="component.label" default="Component" />
	</label>
	<g:select name="componentName" from="${Component.list()}" valueMessagePrefix="security"/>
	<g:submitToRemote action="addComponent" update="componentsTable" value="${message(code: 'default.button.addComponent', default: 'Add Component')}"/>
	<div id="componentsTable">
		<g:render template="componentsTable"/>	
	</div>
</div>

<div class="fieldcontain">
	<label for="roleName">
		<g:message code="role.label" default="Inherited Roles" />
	</label>
	<g:select name="roleName" from="${roleInstance.findPossibleChildren()}" valueMessagePrefix="security"/>
	<g:submitToRemote action="addRole" update="rolesTable" value="${message(code: 'default.button.addRole', default: 'Add Role')}"/>
	<div id="rolesTable">
		<g:render template="rolesTable"/>	
	</div>
</div>
