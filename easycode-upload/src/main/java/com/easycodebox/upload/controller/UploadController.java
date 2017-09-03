package com.easycodebox.upload.controller;

import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.file.*;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.web.BaseController;
import com.easycodebox.upload.util.*;
import com.easycodebox.upload.util.FileType;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author WangXiaoJin
 */
@Controller
public class UploadController extends BaseController {
	
	@Autowired
	private UploadHandler uploadHandler;
	@Autowired
	private Jacksons mvnJacksons;
	
	@GetMapping("/show")
	public String show() {
		return "upload";
	}
	
	/**
	 * 如果上传图片需要传入PIC_TYPE参数则请参考file.xml
	 *
	 * @param responseUrl 为跨域上传图片的解决方案
	 * @param transaction 上传多个文件时，是否事务控制。即当一个文件上传失败，所有的文件都上传失败 <br>
	 * 当没用事务时，上传文件失败的错误信息会存放在fileInfo中error属性中，否则错误信息存放于CodeMsg的msg属性。<br>
	 * transaction 默认值: false
	 * @param fileType 文件类型
	 * @param fileKey 文件标识，参考file.xml map中entry标签的key属性
	 */
	@RequestMapping("/upload")
	public void upload(String responseUrl, boolean transaction, FileType fileType, String fileKey,
	                   @RequestPart("files") MultipartFile[] files, HttpServletResponse resp) throws Exception {
		CodeMsg error = null;
		String filePath = null; //指定文件类型路径
		
		if (fileType == null) {
			error = CodeMsgExt.PARAM_BLANK.fillArgs("fileType");
		} else if (fileKey == null) {
			error = CodeMsgExt.PARAM_BLANK.fillArgs("fileKey");
		} else if (files.length == 0) {
			error = CodeMsgExt.NO_FILE;
		} else {
			filePath = fileType.getUploadPath(fileKey);
			if (filePath == null) {
				error = CodeMsgExt.PARAM_ILLEGAL.fillArgs("fileKey", fileKey);
			}
		}
		if (error != null) {
			outData(error, responseUrl, resp);
			return;
		}
		
		InputStream[] streams = null;
		try {
			streams = new InputStream[files.length];
			long[] lengths = new long[files.length];
			for(int i = 0; i < files.length; i++) {
				streams[i] = files[i].getInputStream();
				lengths[i] = files[i].getSize();
			}
			
			if (fileType == FileType.PIC_TYPE) {
				//上传文件为图片类型
				error = ImageTools.validateImgs(filePath, streams, lengths, null, transaction);
				if (!error.isSuc()) {
					outData(error, responseUrl, resp);
					return ;
				}
				Image[] imgs = (Image[])error.getData();
				imgs = uploadHandler.uploadImg(fileType, files, filePath, imgs, null, transaction);
				
				error = CodeMsgExt.SUC.data(imgs);
			} else if(fileType == FileType.MIX_TYPE) {
				//上传文件为混合类型，可以是图片、TXT、ZIP等
				String[] filenames = new String[files.length];
				for(int i = 0; i < files.length; i++) {
					filenames[i] = FilenameUtils.getName(files[i].getOriginalFilename());
				}
				error = Files.validate(filePath, streams, filenames, lengths, null, transaction);
				if (!error.isSuc()) {
					outData(error, responseUrl, resp);
					return ;
				}
				FileInfo[] fileInfos = (FileInfo[])error.getData();
				fileInfos = uploadHandler.uploadFile(fileType, files, filePath, fileInfos, null, transaction);
				
				error = CodeMsgExt.SUC.data(fileInfos);
			}
		} catch (Exception e) {
			error = CodeMsgExt.FAIL.msg("上传失败");
			log.error("上传文件失败", e);
		} finally {
			if(streams != null) {
				for (InputStream is : streams) {
					IOUtils.closeQuietly(is);
				}
			}
		}
		outData(error, responseUrl, resp);
	}
	
	/**
	 * @param files 删除的文件名
	 * @param fileType 文件类型
	 */
	@RequestMapping("/delete")
	public void delete(String[] files, FileType fileType, HttpServletResponse resp) throws Exception {
		CodeMsg error;
		
		if (files.length == 0) {
			error = CodeMsgExt.PARAM_BLANK.fillArgs("files");
			outData(error, null, resp);
			return;
		}
		fileType = fileType == null ? FileType.PIC_TYPE : fileType;
		
		List<String> list = new ArrayList<>();
		for(String file : files) {
			if(Strings.isNotBlank(file)) {
				String[] inners = file.split(Symbol.COMMA);
				for(String inner : inners) {
					if(Strings.isNotBlank(inner)) {
						list.add(inner.trim());
					}
				}
			}
		}
		if(list.size() == 0) {
			error = CodeMsgExt.PARAM_ILLEGAL.fillArgs("files", Arrays.toString(files));
		} else {
			if (uploadHandler.delete(fileType, list.toArray(new String[0])))
				error = CodeMsgExt.SUC.msg("删除图片成功");
			else
				error = CodeMsgExt.FAIL.msg("删除图片失败");
		}
		outData(error, null, resp);
	}
	
	protected void outData(CodeMsg error, String responseUrl, HttpServletResponse resp) throws IOException {
		Assert.notNull(error);
		if(Strings.isBlank(responseUrl))
			Https.outHtml(error, resp);
		else {
			//responseUrl 为跨域上传图片的解决方案
			String backData = "back_data=" + URLEncoder.encode(mvnJacksons.toJson(error), "UTF-8");
			responseUrl = !responseUrl.contains(Symbol.QUESTION) ? responseUrl + Symbol.QUESTION + backData :
					responseUrl.endsWith(Symbol.AND_MARK) ? responseUrl + backData : responseUrl + Symbol.AND_MARK + backData;
			resp.sendRedirect(responseUrl);
		}
	}
	
}
