<%@ page import="test.Item" %>



<div class="fieldcontain ${hasErrors(bean: itemInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="item.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" maxlength="50" required="" value="${itemInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: itemInstance, field: 'price', 'error')} required">
	<label for="price">
		<g:message code="item.price.label" default="Price" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="price" value="${fieldValue(bean: itemInstance, field: 'price')}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: itemInstance, field: 'quantity', 'error')} required">
	<label for="quantity">
		<g:message code="item.quantity.label" default="Quantity" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="quantity" type="number" min="0" value="${itemInstance.quantity}" required=""/>
</div>

