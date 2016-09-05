[#assign e=JspTaglibs["/ext-tags"]]

[#macro permited value]
	[#if !value?? || !(operation_key[value])?? || operation_key[value]]
		[#nested]
	[/#if]
[/#macro]