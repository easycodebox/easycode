package com.easycodebox.common.jackson;

import com.easycodebox.common.error.CodeMsg;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CodeMsgSerializer extends JsonSerializer<CodeMsg> {

	@Override
	public void serialize(CodeMsg value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException {
		jgen.writeStartObject();
		jgen.writeStringField("code", value.getCode());
		jgen.writeStringField("msg", value.getMsg());
		jgen.writeObjectField("data", value.getData());
		jgen.writeEndObject();
	}

}
