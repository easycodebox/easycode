package com.easycodebox.common.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;

import org.springframework.jdbc.support.JdbcUtils;

import com.easycodebox.common.net.Https;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TestCitySql {
	
	private static Connection connection;
	private static ResultSet resultSet = null;
	
	private static String driverClass = "com.mysql.jdbc.Driver";
	private static String jdbcUrl = "jdbc:mysql://192.168.1.9:3306/botest?useUnicode=true&amp;characterEncoding=UTF8&amp;autoReconnect=true";
	private static String user = "";
	private static String password = "";
	
	@SuppressWarnings("unused")
	private static JSONObject obtainJsonObject() {
		StringBuilder sb = new StringBuilder();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(new File("d:\\js.txt"));
			br = new BufferedReader (fr);
			String s = null;
			while ((s = br.readLine() )!=null) {
				sb.append(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return JSONObject.fromObject(sb.toString());
	}
	
	public static JSONObject ori = JSONObject.fromObject("用此地址获取==http://wuliu.taobao.com/user/output_address.do?range=county");
	
	@SuppressWarnings("rawtypes")
	public static void recursion(int index, String pId) throws Exception {
		PreparedStatement statement = null;
		index++;
		try {
			String sql = null;
			if(index == 1) {
				sql = "insert into l_province(id, state, regionId, name, pinyin, orderNum, creator, createTime, modifier, modifyTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			}else if(index == 2) {
				sql = "insert into l_city(id, state, provinceId, name, pinyin, orderNum, creator, createTime, modifier, modifyTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			}else if(index == 3) {
				sql = "insert into l_district(id, state, cityId, name, pinyin, orderNum, creator, createTime, modifier, modifyTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			}else
				return;
			statement = connection.prepareStatement(sql);
			
			Iterator it = ori.keys();
			while(it.hasNext()) {
				String id = it.next().toString();
				JSONArray ja = ori.getJSONArray(id);
				if(ja.get(1).equals(pId)) {
					
					recursion(index, id);
					
					statement.setInt(1, Integer.parseInt(id));
					statement.setInt(2, 2);
					statement.setInt(3, Integer.parseInt(pId));
					statement.setString(4, ja.get(0).toString());
					statement.setString(5, ja.get(2).toString());
					statement.setInt(6, 1);
					statement.setString(7, "0");
					statement.setDate(8, new Date(System.currentTimeMillis()));
					statement.setString(9, "0");
					statement.setDate(10, new Date(System.currentTimeMillis()));
					statement.execute();
				}
			}
		} finally {
			JdbcUtils.closeStatement(statement);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		String url = "http://lsp.wuliu.taobao.com/locationservice/addr/output_address_town.do?l3=";
		
		PreparedStatement statement = null;
        
		try {
			Class.forName(driverClass).newInstance();
			connection = java.sql.DriverManager.getConnection(jdbcUrl, user, password);
			connection.setAutoCommit(false);
			//插入省市区
			//recursion(0, "1");
			
			//插入街道
			String sql = "select id from l_district";
			statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				int id = rs.getInt("id");
            	String info = Https.Request.get(url + id).replace("callback(", "");
            	info = info.substring(0, info.length()-2);
            	JSONObject jo = JSONObject.fromObject(info);
            	System.out.println(jo);
            	if(!jo.isNullObject()) {
            		JSONObject res = jo.optJSONObject("result");
            		if(res != null && !res.isNullObject()) {
            			String tmpSql = "insert into l_range(id, state, districtId, name, pinyin, orderNum, creator, createTime, modifier, modifyTime) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            			statement = connection.prepareStatement(tmpSql);
            			Iterator it = res.keys();
            			while(it.hasNext()) {
            				String ranId = it.next().toString();
            				JSONArray ja = res.getJSONArray(ranId);
            				if(ja.get(1).equals(id + "")) {
            					statement.setInt(1, Integer.parseInt(ranId));
            					statement.setInt(2, 2);
            					statement.setInt(3, id);
            					statement.setString(4, ja.get(0).toString());
            					statement.setString(5, ja.get(2).toString());
            					statement.setInt(6, 1);
            					statement.setString(7, "0");
            					statement.setDate(8, new Date(System.currentTimeMillis()));
            					statement.setString(9, "0");
            					statement.setDate(10, new Date(System.currentTimeMillis()));
            					statement.addBatch();
            				}
            				statement.executeBatch();
            			}
            		}
            	}
			}
			
			connection.commit(); 
		}finally {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeConnection(connection);
			JdbcUtils.closeStatement(statement);
		}
		
	}

}
