<?xml version="1.0" encoding="UTF-8"?>
<#macro notNull key value>
	<#if value[key]??>
		<#t> ${key}="${value[key]}"<#t>
	</#if>
</#macro>
<#macro status value>
	<#if value.status?? && value.status.className == "CLOSE">
		<#t> status="CLOSE"<#t>
	</#if>
</#macro>
<#macro sort value>
	<#if value.sort?? && value.sort != 0>
		<#t> sort="${value.sort?c}"<#t>
	</#if>
</#macro>
<menu project="${project}">

	<#assign tmp1 = false>
	<#list os as m1>
		<#if m1.isMenu.className == "YES">
	<menu1 id="${m1.id?c}" name="${m1.name}"<@notNull key="url" value=m1 /><@status value=m1 /><@notNull key="icon" value=m1 /><@sort value=m1 /><@notNull key="description" value=m1 />>
		<#assign tmp2 = false>
		<#list m1.children as m2>
			<#if m2.isMenu.className == "YES">
		<menu2 id="${m2.id?c}" name="${m2.name}"<@notNull key="url" value=m2 /><@status value=m2 /><@notNull key="icon" value=m2 /><@sort value=m2 /><@notNull key="description" value=m2 />>
			<#assign tmp3 = false>
			<#list m2.children as m3>
				<#if m3.isMenu.className == "YES">
			<menu3 id="${m3.id?c}" name="${m3.name}"<@notNull key="url" value=m3 /><@status value=m3 /><@notNull key="icon" value=m3 /><@sort value=m3 /><@notNull key="description" value=m3 />>
					<#if m3.children?size gt 0>
				<operations>
						<#list m3.children as o4>
							<#if o4.isMenu.className == "NO">
					<operation id="${o4.id?c}" name="${o4.name}"<@notNull key="url" value=o4 /><@status value=o4 /><@notNull key="icon" value=o4 /><@sort value=o4 /><@notNull key="description" value=o4 />/>
							</#if>
						</#list>
				</operations>
					</#if>
			</menu3>
			
				<#elseif !tmp3>
					<#assign tmp3 = true>
				</#if>
			</#list>
			<#if tmp3>
			<operations>
				<#list m2.children as o3>
					<#if o3.isMenu.className == "NO">
				<operation id="${o3.id?c}" name="${o3.name}"<@notNull key="url" value=o3 /><@status value=o3 /><@notNull key="icon" value=o3 /><@sort value=o3 /><@notNull key="description" value=o3 />/>		
					</#if>
				</#list>
			</operations>
			</#if>
		</menu2>
		
			<#elseif !tmp2>
				<#assign tmp2 = true>
			</#if>
		</#list>
		<#if tmp2>
		<operations>
			<#list m1.children as o2>
				<#if o2.isMenu.className == "NO">
			<operation id="${o2.id?c}" name="${o2.name}"<@notNull key="url" value=o2 /><@status value=o2 /><@notNull key="icon" value=o2 /><@sort value=o2 /><@notNull key="description" value=o2 />/>		
				</#if>
			</#list>
		</operations>
		</#if>
	</menu1>
	
		<#elseif !tmp1>
			<#assign tmp1 = true>
		</#if>
	</#list>
	<#if tmp1>
	<operations>
		<#list os as o1>
			<#if o1.isMenu.className == "NO">
		<operation id="${o1.id?c}" name="${o1.name}"<@notNull key="url" value=o1 /><@status value=o1 /><@notNull key="icon" value=o1 /><@sort value=o1 /><@notNull key="description" value=o1 />/>		
			</#if>
		</#list>
	</operations>
	</#if>
</menu>