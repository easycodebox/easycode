package com.easycodebox.common.file;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.file.exception.NonEnlargedException;
import com.easycodebox.common.lang.DecimalUtils;
import com.easycodebox.common.lang.RegularUtils;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 * 
 */
public class ImageTools {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImageTools.class);
	
	
	public static CodeMsg validateImgs(String rules, File... imgs) {
		ImageInputStream[] stream = new ImageInputStream[imgs.length];
		long[] lengths = new long[imgs.length];
		try {
			for(int i = 0; i < imgs.length; i++) {
				stream[i] = ImageIO.createImageInputStream(imgs[i]);
				lengths[i] = imgs[i].length();
			}
		} catch (IOException e) {
			LOG.error("解析图片错误", e);
			return CodeMsg.FAIL.msg("解析图片错误");
		} finally {
			for (ImageInputStream is : stream) {
				IOUtils.closeQuietly(is);
			}
		}
		return validateImgs(rules, stream, lengths, null, false);
	}
	
	/**
	 * 注意：需要手动释放InputStream的资源
	 * @param maxSize	文件的最大值，为null、负值、0时不验证此特性：单位MB
	 * @param transaction	是否一个文件验证失败后，所有的文件都失败
	 * @return
	 */
	public static CodeMsg validateImgs(String rules, InputStream[] imgs, long[] fileSizes, Integer maxSize, boolean transaction) {
		ImageInputStream[] stream = new ImageInputStream[imgs.length];
		try {
			for(int i = 0; i < imgs.length; i++) {
				stream[i] = ImageIO.createImageInputStream(imgs[i]);
			}
		} catch (IOException e) {
			LOG.error("解析图片错误", e);
			return CodeMsg.FAIL.msg("解析图片错误");
		}
		return validateImgs(rules, stream, fileSizes, maxSize, transaction);
	}
	
	/**
	 * 注意：需要手动释放InputStream的资源 <br>
	 * 返回"suc"验证成功，除此之外都为验证失败
	 * wl(900) ==> 宽度小于等于900px
	 * wg(900) ==> 宽度大于等于900px
	 * hl(900) ==> 高度小于等于900px
	 * hg(900) ==> 高度大于等于900px
	 * re(0.5) ==> 比例(ratio)等于0.5（宽/高=0.5），容错比例为0.1
	 * rl(0.5) ==> 比例(ratio)小于等于0.5（宽/高<=0.5）
	 * rg(0.5) ==> 比例(ratio)大于等于0.5（宽/高>=0.5）
	 * sl(8)   ==> 大小(size)小于等于8M
	 * sg(8)   ==> 大小(size)大于等于8M
	 * type(jpg,png)  ==> 文件格式 只能为jpg,png
	 * @param rules 图片的规则 
	 * @param maxSize	文件的最大值，为null、负值、0时不验证此特性：单位MB
	 * @param transaction	是否一个文件验证失败后，所有的文件都失败
	 * @return CodeMsg
	 */
	public static CodeMsg validateImgs(String rules, ImageInputStream[] imgs, long[] fileSizes, Integer maxSize, boolean transaction) {
		Assert.notEmpty(imgs);
		if(StringUtils.isBlank(rules)) {
			return CodeMsg.FAIL.msg("图片规则不能为空");
		}
		rules = RegularUtils.getQueryString(rules);
		Double wl = null, wg = null, hl = null, hg = null, re = null, 
				rl = null, rg = null, sl = null, sg = null;
		String type = null, error = null;
		if(StringUtils.isNotBlank(rules)) {
			wl = FileUtils.processRule(rules, "wl", false, Double.class);
			wg = FileUtils.processRule(rules, "wg", true, Double.class);
			hl = FileUtils.processRule(rules, "hl", false, Double.class);
			hg = FileUtils.processRule(rules, "hg", true, Double.class);
			re = FileUtils.processRule(rules, "re", false, Double.class);
			rl = FileUtils.processRule(rules, "rl", false, Double.class);
			rg = FileUtils.processRule(rules, "rg", true, Double.class);
			sl = FileUtils.processRule(rules, "sl", false, Double.class);
			if (maxSize != null && maxSize > 0 && (sl == null || maxSize < sl)) {
				sl = maxSize.doubleValue();
			}
			sg = FileUtils.processRule(rules, "sg", true, Double.class);
			type = FileUtils.processRule(rules, "type", true, String.class);
		}
		
		Image[] newImgs = new Image[imgs.length];
		for(int i = 0; i < imgs.length; i++) {
			ImageReader reader = null;
			Image im = new Image();
			im.setSize(FileUtils.byte2m(fileSizes[i], 2));
			newImgs[i] = im;
			try {
				
				Iterator<ImageReader> iter = ImageIO.getImageReaders(imgs[i]);
		        if (!iter.hasNext()) {
		        	/************* error **************/
		        	error = "文件类型错误";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
		        }
		        reader = (ImageReader)iter.next();
		        
		        String imgType = reader.getFormatName().toLowerCase();
		        String mimeType = MimeTypes.getMimeTypeByExt(imgType);
		        if(mimeType != null) {
		        	//装换成常用的文件扩展名，例：jpeg ==> jpg
		        	String[] exts = MimeTypes.getExtensions(mimeType);
		        	imgType = exts[0];
		        }else {
		        	LOG.error("未知的图片类型：" + imgType);
		        }
		        im.setType(imgType);
				
		        ImageReadParam param = reader.getDefaultReadParam();
		        reader.setInput(imgs[i], true, true);
		        BufferedImage img = reader.read(0, param);
				
				int w = img.getWidth();
				int h = img.getHeight();
				BigDecimal ratio = new BigDecimal(1.0*w/h).setScale(2, BigDecimal.ROUND_HALF_UP);
				if(wl != null && w > wl) {
					/************* error **************/
					error = "图片宽度不能大于" + DecimalUtils.fmt(wl, 2, true) + "像素";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				}
				if(wg != null && w < wg) {
					/************* error **************/
					error = "图片宽度不能小于" + DecimalUtils.fmt(wg, 2, true) + "像素";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				}
				if(hl != null && h > hl) {
					/************* error **************/
					error = "图片高度不能大于" + DecimalUtils.fmt(hl, 2, true) + "像素";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				} 
				if(hg != null && h < hg) {
					/************* error **************/
					error = "图片高度不能小于" + DecimalUtils.fmt(hg, 2, true) + "像素";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				} 
				if(re != null) {
					BigDecimal reDec = new BigDecimal(re.toString())
							.setScale(2, BigDecimal.ROUND_HALF_UP);
					//图片宽高比例的容错为0.1
					if(ratio.compareTo(reDec.subtract(new BigDecimal("0.1"))) < 0
							|| ratio.compareTo(reDec.add(new BigDecimal("0.1"))) > 0) {
						/************* error **************/
						error = "请确定图片的宽高比例是" + DecimalUtils.fmt(reDec, 2, true);
			        	if (transaction) {
			        		return CodeMsg.FAIL.msg(error);
			        	} else {
			        		im.setError(error);
			        		continue;
			        	}
			        	/************* error **************/
					}
				} 
				if(rl != null && ratio.doubleValue() > rl) {
					/************* error **************/
					error = "图片的宽高比例不能大于" + DecimalUtils.fmt(rl, 2, true);
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				} 
				if(rg != null && ratio.doubleValue() < rg) {
					/************* error **************/
					error = "图片的宽高比例不能小于" + DecimalUtils.fmt(rg, 2, true);
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				}
				if(sl != null && FileUtils.byte2m(fileSizes[i], 6) > sl) {
					/************* error **************/
					error = "图片不能大于" + DecimalUtils.fmt(sl, 2, true) + "M";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				} 
				if(sg != null && FileUtils.byte2m(fileSizes[i], 6) < sg) {
					/************* error **************/
					error = "图片不能小于" + DecimalUtils.fmt(sg, 2, true) + "M";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		im.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				}
				if(StringUtils.isNotBlank(type)) {
					boolean auth = false;
					String[] allowTypes = type.split(Symbol.COMMA);
					for(String t : allowTypes) {
						if(t.equalsIgnoreCase(imgType))
							auth = true;
					}
					if(!auth) {
						/************* error **************/
						error = "图片格式只能为" + type;
			        	if (transaction) {
			        		return CodeMsg.FAIL.msg(error);
			        	} else {
			        		im.setError(error);
			        		continue;
			        	}
			        	/************* error **************/
					}
				}
				
				im.setWidth(w);
				im.setHeight(h);
				
			} catch (IOException e) {
				LOG.error("解析图片错误", e);
				/************* error **************/
				error = "解析图片错误，请上传有效图片。";
	        	if (transaction) {
	        		return CodeMsg.FAIL.msg(error);
	        	} else {
	        		im.setError(error);
	        		continue;
	        	}
	        	/************* error **************/
			} finally {
				if(reader != null)
					reader.dispose();
			}
		}
		return CodeMsg.SUC.data(newImgs);
	}
	
	/**
	 * 等比例缩放图片，不满足指定宽高时用白底填充图片
	 * @param srcImg
	 * @param descImg
	 * @param width
	 * @param height
	 * @param enlarge   是否能够放大图片
	 * @return
	 */
	public static boolean fillScaleImg(String srcImg, String descImg
			, int width, int height, boolean enlarge) {
		if(StringUtils.isBlank(srcImg) 
        		||  StringUtils.isBlank(descImg)) {   
        	LOG.warn("img is balnk in the method resizeImage of ImageTools.Class .");
            return false;
		}
		try {
			BufferedImage img = ImageIO.read(new FileInputStream(srcImg));
			int w = img.getWidth(),
			h = img.getHeight();
			int[] tmp = scaleSize(w, h, width, height, true, enlarge);
			// 生成缩放图片存储空间
			BufferedImage newImg = new BufferedImage(width,
					height, BufferedImage.TYPE_3BYTE_BGR);
			/*AffineTransform tsf = AffineTransform.getScaleInstance(
						(double)tmp[0]/w, (double)tmp[1]/h);
			AffineTransformOp ato = new AffineTransformOp(tsf, null);
			scaleImg = ato.filter(img, null);*/
			java.awt.Image imgObj = img.getScaledInstance(tmp[0], tmp[1], java.awt.Image.SCALE_DEFAULT);
			Graphics2D g = newImg.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			int wv = (width - tmp[0])/2;
			int hv = (height - tmp[1])/2;
			g.drawImage(imgObj, wv, hv,  Color.WHITE, null);
			g.dispose();
			File newFile = new File(descImg);
			if(!newFile.getParentFile().exists())
				newFile.getParentFile().mkdirs();
			ImageIO.write(newImg, FilenameUtils.getExtension(descImg).toLowerCase(), newFile);  
		} catch (FileNotFoundException e) {
			LOG.error("read img error.", e);
			return false;
		} catch (IOException e) {
			LOG.error("read img error.", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 获取等比例的宽高
	 * @param originWidth
	 * @param originHeight
	 * @param width		可以为空,为null就等比例缩放
	 * @param height	可以为空,为null就等比例缩放
	 * @param scale		是否等比缩放标记
	 * @param enlarge   是否能够放大图片
	 * @return
	 */
	public static int[] scaleSize(int originWidth, int originHeight, 
			Integer width, Integer height, boolean scale, boolean enlarge) {
		int[] wh = new int[2];
		if(width == null && height == null) {
			wh[0] = originWidth;
			wh[1] = originHeight;
		}else if(width == null) {
			double scaleH = 1.0*originHeight/height;
			if(!enlarge && scaleH < 1) {
				wh[0] = originWidth;
				wh[1] = originHeight;
			}else {
				wh[0] = (int)(originWidth/scaleH);
				wh[1] = height;
			}
		}else if(height == null) {
			double scaleW = 1.0*originWidth/width;
			if(!enlarge && scaleW < 1) {
				wh[0] = originWidth;
				wh[1] = originHeight;
			}else {
				wh[0] = width;
				wh[1] = (int)(originHeight/scaleW);
			}
		} else if(!scale) {
			if(!enlarge && 
					(originWidth < width || originHeight < height)) {
				wh[0] = originWidth;
				wh[1] = originHeight;
			}else {
				wh[0] = width;
				wh[1] = height;
			}
		}else {
			double scaleW = 1.0*originWidth/width,
				scaleH = 1.0*originHeight/height,
				newScale;
			if(scaleW > scaleH)
				newScale = scaleW;
			else
				newScale = scaleH;
			if(!enlarge && newScale < 1) {
				wh[0] = originWidth;
				wh[1] = originHeight;
			}else {
				wh[0] = (int)(originWidth/newScale);
				wh[1] = (int)(originHeight/newScale);
			}
		}
		return wh;
	}
	
	public static Image getImageInfo(File fileImage) throws Exception {
		Image image = new Image();
		BufferedImage bufferImage;
		bufferImage = ImageIO.read(fileImage);
		image.setHeight(bufferImage.getHeight());
		image.setWidth(bufferImage.getWidth());
		image.setSize(FileUtils.byte2m(fileImage.length(), 2));
		return image;
	}

	public static void cutImage(File image, String outputFile, Rectangle rect)
			throws IOException {
		cutImage(image, outputFile, rect.x, rect.y, rect.width, rect.height);
	}
	
	public static void cutImage(File image, String outputFile, int x, int y, int width, int height)
			throws IOException {
		BufferedImage scrBuffer = ImageIO.read(image);
		BufferedImage desBuffer = scrBuffer.getSubimage(x, y, width, height);
		ImageIO.write(desBuffer, FilenameUtils.getExtension(outputFile).toLowerCase(), new File(outputFile));
	}
	
	public static void mergeImages(String source1, String source2, String des, String type, int x, int y, int width, int height) throws Exception {
		BufferedImage img1 = ImageIO.read(new File(source1));
		BufferedImage img2 = ImageIO.read(new File(source2));
		Graphics g = img1.getGraphics();
		g.drawImage(img2, x, y, width, height, null);
		g.dispose();
		ImageIO.write(img1, type, new File(des));  
	}
	
	
	
	
	
	
	
	
	
	
	/*************************（以下规则已被废弃） *******************************************/
	/**
	 * 
	 * 为每个上传图片生成一个大图的规则
	 */
	public static final String BIG_IMG = "960c760";
	public static final Integer BIG_IMG_WIDTH = 960;
	public static final Integer BIG_IMG_HEIGHT = 760;
	//public static final Integer MOBILE_BIG_IMG_WIDTH = 500;
	//public static final Integer MOBILE_BIG_IMG_HEIGHT = 500;
	
	/**
	 * 
	 * @param imgName	如果resizeImg对象的imgName有值，则依据imgName否则图片名用该参数
	 * @param scale		是够是等比例缩放，如果resizeImg中的宽、高有一个没值就强制等比例缩放
	 * @param enlarge   是否能够放大图片
	 * @param resizeImg	期望缩放的图片信息
	 * @return	Image[] 
	 */
	@Deprecated
	public static Image[] resizeImage(BufferedImage img, String imgName, String baseRealPath, boolean scale, boolean enlarge,
			List<Image> resizeImg) throws Exception {
		if(img == null ||  resizeImg.size() == 0) 
        	throw new IllegalArgumentException("img or resizeImg is balnk in the method resizeImage of ImageTools.Class .");
		
		int w = img.getWidth(),
			h = img.getHeight();
		Image[] returnImgs = new Image[resizeImg.size()];
		for(int i = 0; i < resizeImg.size(); i++) {
			Image t = resizeImg.get(i);
			//如果Image没有初始化imgName则用该函数的imgName参数
			if(StringUtils.isBlank(t.getName()))
				t.setName(imgName);
			Integer width = t.getWidth(),
					height = t.getHeight();
			
			if(!enlarge && !(BIG_IMG_WIDTH.equals(width)
					&& BIG_IMG_HEIGHT.equals(height)
					//|| MOBILE_BIG_IMG_WIDTH.equals(width) 
					//&& MOBILE_BIG_IMG_HEIGHT.equals(height)
					) ) {
				if(width != null
						&& width > w) {
					throw new NonEnlargedException("原图片宽度不能小于" + width);
				}
				if(height != null
						&& height > h) {
					throw new NonEnlargedException("原图片高度不能小于" + height);
				}
			}
			int[] tmp = scaleSize(w, h, width, height, scale, enlarge);
			width = tmp[0];
			height = tmp[1];
			// 生成处理后的图片存储空间
			BufferedImage newBufferedImg = new BufferedImage(width,
					height, BufferedImage.TYPE_3BYTE_BGR);
			/*AffineTransform tsf = AffineTransform.getScaleInstance(
						(double)width/w, (double)height/h);
			// 根据原始图片生成处理后的图片
			AffineTransformOp ato = new AffineTransformOp(tsf, null);
			newImg = ato.filter(img, null);*/
			java.awt.Image imgObj = img.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT);
			Graphics2D g = newBufferedImg.createGraphics();
			g.drawImage(imgObj, 0, 0, Color.WHITE, null);
			g.dispose();
			File newFile = new File(baseRealPath + Symbol.SLASH + t.getPath() + Symbol.SLASH + t.getName());
			if(!newFile.getParentFile().exists())
				newFile.getParentFile().mkdirs();
			ImageIO.write(newBufferedImg, FilenameUtils.getExtension(t.getName()).toLowerCase(), newFile);
			
			//设置图片的宽高
			Image sImg = new Image();
			PropertyUtils.copyProperties(sImg,t);
			//BeanUtils.copyProperties(sImg, t);	此方法会自动把Integer=null值转换成0
			sImg.setWidth(width);
			sImg.setHeight(height);
			returnImgs[i] = sImg;
		}
		return returnImgs;
	}
	
	/**
	 * 
	 * @param imgPath	大图的地址 upload/img/gift/1.png
	 * @param baseRealPath	
	 * @param filePath	upload/img/gift=300c0
	 * @param boundw	裁剪图片宽度
	 * @param boundh	裁剪图片高度
	 * @param cx		裁剪图片的x点
	 * @param cy		裁剪图片的y点
	 * @param cw		裁剪的宽度
	 * @param ch		裁剪高度
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static String cutImageComplex(String imgPath, String baseRealPath, 
			String filePath, int x, int y, int width, int height) throws Exception {
		Object[] urlAndImgs = FileUtils.analyzeUploadImgUrl(filePath, false, false);
		List<Image> smallImgs = urlAndImgs[1] == null ? null : (List<Image>)urlAndImgs[1];
		File tmp = new File(baseRealPath + Symbol.SLASH + imgPath);
		String imgName = FilenameUtils.getName(imgPath);
		//复制tmp原图到release环境
		File bigImg = new File(baseRealPath + Symbol.SLASH + urlAndImgs[0] + Symbol.SLASH + imgName);
		if (!bigImg.getParentFile().exists())
			bigImg.getParentFile().mkdirs();
		tmp.renameTo(bigImg);
		//org.apache.commons.io.FileUtils.copyFile(tmp, bigImg);
		if(smallImgs == null || smallImgs.size() == 0)
			return urlAndImgs[0] + Symbol.SLASH + imgName;
		
		BufferedImage scrBuffer = ImageIO.read(bigImg);
		scrBuffer = scrBuffer.getSubimage(x, y, width, height);
		Image[] imgs = resizeImage(scrBuffer, imgName, baseRealPath, false, true, smallImgs);
		return imgs[0].getPath() + Symbol.SLASH + imgs[0].getName();
	}
	
	/**
	 * 提供测试用generateSmallImg(new File("D:\\back\\imgs"));
	 */
	@SuppressWarnings("unused")
	private static void testGenerateSmallImg(File path) {
		File[] files = path.listFiles();
		String absolutePath = path.getAbsolutePath().replace("D:\\back\\imgs", "D:\\back\\imgs\\g");
		for(int i = 0; i < files.length; i++) {
			File f = files[i];
			if(f.isFile()) {
					try {
						List<Image> resizeImg = new ArrayList<Image>(1);
						Image img = new Image();
						img.setPath("");	//替换当前文件
						img.setWidth(960);
						img.setHeight(760);
						resizeImg.add(img);
						BufferedImage tmpImg = ImageIO.read(files[i]);
						resizeImage(tmpImg, f.getName(), absolutePath, true, false, resizeImg);
						tmpImg.flush();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}else if(!f.getName().equals("mobilePath")) {
				testGenerateSmallImg(f);
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			/*generateSmallImg(new File("D:\\test"));*/
			
			/*List<Image> resizeImg = new ArrayList<Image>(1);
			Image img = new Image();
			img.setImgPath("");	//替换当前文件
			img.setWidth(960);
			img.setHeight(760);
			resizeImg.add(img);
			BufferedImage tmpImg = ImageIO.read(new File("e:/loudong.png"));
			resizeImage(tmpImg, "loudong-cut.jpg", "e:", true, false, resizeImg);*/
		
			/*//System.out.println(getImageType(new File("e:/loudong-back.jpg")));
			
			// get image format in a file
	        File file = new File("e:/war");
	        // create an image input stream from the specified file
	        ImageInputStream iis = ImageIO.createImageInputStream(file);
	        // get all currently registered readers that recognize the image format
	        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
	        if (!iter.hasNext()) {
	            throw new RuntimeException("No readers found!");
	        }
	        // get the first reader
	        ImageReader reader = iter.next();
	        System.out.println("Format: " + reader.getFormatName());
	        // close stream
	        iis.close();*/
			//fillScaleImg("e:/zigentu.jpg", "e:/zigentu-back.jpg", 500, 500, false);
			long cur = System.currentTimeMillis();
			for(int i = 0; i < 10000; i++) {
				validateImgs("wg(420)hg(420)", new File[]{ new File("e:/a.jpg")});
			}
			System.out.println(System.currentTimeMillis() - cur);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
