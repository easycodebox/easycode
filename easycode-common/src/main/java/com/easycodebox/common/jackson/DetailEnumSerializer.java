package com.easycodebox.common.jackson;

import com.easycodebox.common.enums.DetailEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DetailEnumSerializer extends JsonSerializer<DetailEnum<?>> {

	@Override
	public void serialize(DetailEnum<?> value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException {
		jgen.writeStartObject();
		jgen.writeObjectField("value", value.getValue());
		jgen.writeStringField("desc", value.getDesc());
		jgen.writeStringField("className", value.getClassName());
		jgen.writeEndObject();
	}

}
