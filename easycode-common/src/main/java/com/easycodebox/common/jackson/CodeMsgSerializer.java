package com.easycodebox.common.jackson;

import java.io.IOException;

import com.easycodebox.common.error.CodeMsg;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CodeMsgSerializer extends JsonSerializer<CodeMsg> {

	@Override
	public void serialize(CodeMsg value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeStringField("code", value.getCode());
		jgen.writeStringField("msg", value.getMsg());
		jgen.writeObjectField("data", value.getData());
		jgen.writeEndObject();
	}

}
