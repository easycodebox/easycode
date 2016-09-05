package com.easycodebox.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.CodeMsg.CodeMsgUtils;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.log.logback.LocateLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class TestClass extends LocateLogger {
	
	@Before
	public void beforeTest() {
		
	}

	@Test
    public void testCase() {
    	
    }
	
	public static void main(String[] args) throws JsonProcessingException, IOException {
		
		List<UserInfo> list = new ArrayList<UserInfo>();
		UserInfo user = new UserInfo();
		user.setRealname("realname1");
		list.add(user);
		user.setRealname("realname2");
		list.add(user);
		user.setRealname("realname3");
		list.add(user);
		
		String jsonString = Jacksons.COMMUNICATE.writeValueAsString(CodeMsg.NONE.codeMsg("11", (String)null).data(list));
		System.out.println(jsonString);
		
		System.out.println(CodeMsgUtils.json2Bean(jsonString, new TypeReference<List<UserInfo>>() {}));
		System.out.println(CodeMsgUtils.json2Bean(jsonString, new TypeReference<List<UserInfo>>() {}).getData().getClass());
		
		JsonNode jsonNode = Jacksons.COMMUNICATE.readTree(jsonString);
		String code = jsonNode.get("code").asText();
		String msg = jsonNode.get("msg").asText();
		
		JsonNode data = jsonNode.get("data");
		
		System.out.println(code);
		System.out.println(msg);
		System.out.println(data);
		
		//TypeFactory.collectionType
		System.out.println(Jacksons.COMMUNICATE.readValue(
				Jacksons.COMMUNICATE.treeAsTokens(data), 
				new TypeReference<List<UserInfo>>() {}).toString());
		
	}
    
}
