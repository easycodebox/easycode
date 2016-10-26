package com.easycodebox.upload.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easycodebox.common.enums.DetailEnums;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.upload.util.CodeMsgExt;
import com.easycodebox.upload.util.FileType;
import com.easycodebox.upload.util.UploadUtils;

/**
 * @author WangXiaoJin
 * 
 */
public class DeleteServlet extends BaseServlet {

	private static final long serialVersionUID = -15202551101895656L;
	
	private static final String FILES_KEY = "files";
	/**
	 * 默认是图片类型
	 */
	private static final String FILE_TYPE_KEY = "fileType";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	 * 需要传入files的参数
	 * 需要文件类型参数 fileType=PIC_TYPE
	 * 请参考file.xml
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String flag = StringUtils.leftPad(String.valueOf(new Random().nextInt(10000)), 4, '0');
		//用来删除文件的参数
		String[] files = req.getParameterValues(FILES_KEY);
		String fileType = req.getParameter(FILE_TYPE_KEY);
		FileType type = null;
		
		CodeMsg error = null;
		
		if(StringUtils.isBlank(fileType)) {
			//默认是图片类型
			type = FileType.PIC_TYPE;
		}else {
			type = DetailEnums.parse(FileType.class, fileType);
		}
		
		if(type == null) {
			error = CodeMsgExt.FILE_TYPE_PARAM_ERROR;
			LOG.warn("flag={0} : {1}", flag, error.getMsg());
			this.outData(error, null, resp);
			return;
		}
		
		if(files == null || files.length == 0) {
			error = CodeMsgExt.NO_FILES_PARAM;
		}else {
			List<String> list = new ArrayList<String>();
			for(String file : files) {
				if(StringUtils.isNotBlank(file)) {
					String[] inners = file.split(Symbol.COMMA);
					for(String inner : inners) {
						if(StringUtils.isNotBlank(inner)) {
							list.add(inner.trim());
						}
					}
				}
			}
			if(list.size() == 0) {
				error = CodeMsgExt.NO_FILES_PARAM;
			}else {
				if(UploadUtils.delete(type, list.toArray(new String[]{})))
					error = CodeMsgExt.SUC.msg("删除图片成功");
				else
					error = CodeMsgExt.FAIL.msg("上传图片失败");
			}
		}
		
		LOG.info("flag={0} : {1}", flag, error.getMsg());
		this.outData(error, null, resp);
	}
	
}
