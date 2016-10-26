package com.easycodebox.upload.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.io.IOUtils;

import com.easycodebox.common.enums.DetailEnums;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.file.FileInfo;
import com.easycodebox.common.file.FileUtils;
import com.easycodebox.common.file.Image;
import com.easycodebox.common.file.ImageTools;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.upload.util.CodeMsgExt;
import com.easycodebox.upload.util.Constants;
import com.easycodebox.upload.util.FileType;
import com.easycodebox.upload.util.UploadUtils;

/**
 * @author WangXiaoJin
 * 
 */
public class UploadServlet extends BaseServlet {

	private static final long serialVersionUID = 2962493472282327250L;

	/**
	 * 如果上传图片需要传入PIC_TYPE参数 ，如 PIC_TYPE=food
	 * 请参考file.xml
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String flag = StringUtils.leftPad(String.valueOf(new Random().nextInt(10000)), 4, '0');
		//文件类型
		FileType type = null;
		//指定文件类型路径
		String filePath = null,
				filePathKey = null,
				responseUrl = null;
		boolean transaction = false;
		CodeMsg error = null;

		LOG.info("flag={0} 开始上传文件", flag);

		// 判斷表单提交是否是正确格式
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);

		if (isMultipart) {
			
			// 构造一个文件上传处理对象
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			InputStream[] streams = null;
			
			try {
				
				List<DiskFileItem> itemList = new ArrayList<DiskFileItem>(8);
				// 解析表单中提交的所有文件内容
				Iterator<DiskFileItem> items = upload.parseRequest(req).iterator();
				while (items.hasNext()) {
					DiskFileItem item = items.next();
					if(item.isFormField()) {
						String fieldName = StringUtils.trim(item.getFieldName());
						if(Constants.responseUrlKey.equalsIgnoreCase(fieldName)) {
							//responseUrl 为跨域上传图片的解决方案
							responseUrl = item.getString();
						} else if(Constants.TRANSACTION_KEY.equalsIgnoreCase(fieldName)) {
							transaction = Boolean.parseBoolean(item.getString());
						} else if(type == null && StringUtils.isNotBlank(fieldName)) {
							type = DetailEnums.parse(FileType.class, fieldName);
							if(type != null) {
								filePathKey = item.getString();
								if(StringUtils.isNotBlank(filePathKey)) {
									filePath = type.getUploadPath(filePathKey);
								}
							}
						}
					}else {
						itemList.add(item);
					}
				}
				
				if(type != null) {
					
					if(StringUtils.isBlank(filePath)) {
						error = CodeMsgExt.NO_PATH.fillArgs(filePathKey);
						LOG.warn("flag={0} : {1}", flag, error.getMsg());
						this.outData(error, responseUrl, resp);
						return ;
					}
					
					if(itemList.size() == 0) {
						error = CodeMsgExt.NO_FILE;
						LOG.warn("flag={0} : {1}", flag, error.getMsg());
						this.outData(error, responseUrl, resp);
						return ;
					}
					
					streams = new InputStream[itemList.size()];
					long[] lengths = new long[itemList.size()];
					for(int i = 0; i < itemList.size(); i++) {
						DiskFileItem item = itemList.get(i);
						streams[i] = item.getInputStream();
						lengths[i] = item.getSize();
					}
					
					if(type == FileType.PIC_TYPE) {
						//上传文件为图片类型
						error = ImageTools.validateImgs(filePath, streams, lengths, Constants.MAX_UPLOAD_FILE, transaction);
						if(!error.isSuc()) {
							LOG.warn("flag={0} : {1}", flag, error.getMsg());
							this.outData(error, responseUrl, resp);
							return ;
						}
						Image[] imgs = (Image[])error.getData();
						imgs = UploadUtils.uploadImg(type, itemList, filePath, imgs, null, transaction);
						
						error = CodeMsgExt.SUC.data(imgs);
						LOG.info("flag={0} : {1}", flag, error.getMsg());
					}else if(type == FileType.MIX_TYPE) {
						//上传文件为混合类型，可以是图片、TXT、ZIP等
						String[] filenames = new String[itemList.size()];
						for(int i = 0; i < itemList.size(); i++) {
							DiskFileItem item = itemList.get(i);
							filenames[i] = FilenameUtils.getName(item.getName());
						}
						error = FileUtils.validate(filePath, streams, filenames, lengths, Constants.MAX_UPLOAD_FILE, transaction);
						if(!error.isSuc()) {
							LOG.warn("flag={0} : {1}", flag, error.getMsg());
							this.outData(error, responseUrl, resp);
							return ;
						}
						FileInfo[] files = (FileInfo[])error.getData();
						files = UploadUtils.uploadFile(type, itemList, filePath, files, null, transaction);
						
						error = CodeMsgExt.SUC.data(files);
						LOG.info("flag={0} : {1}", flag, error.getMsg());
					}else {
						error = CodeMsgExt.PARAM_ERROR_TYPE;
						LOG.warn("flag={0} : {1}", flag, error.getMsg());
					}
					
				}else {
					error = CodeMsgExt.PARAM_ERROR_TYPE;
					LOG.warn("flag={0} : {1}", flag, error.getMsg());
				}
				
			} catch (Exception e) {
				error = CodeMsgExt.FAIL.msg("上传失败");
				LOG.error("flag={0} : {1}", e, flag, error.getMsg());
			} finally {
				if(streams != null) {
					for (InputStream is : streams) {
						IOUtils.closeQuietly(is);
					}
				}
			}
		} else {
			error = CodeMsgExt.FAIL.msg("上传失败");
			LOG.warn("flag={0} : {1}", flag, error.getMsg());
		}
		this.outData(error, responseUrl, resp);
	}
	
}
