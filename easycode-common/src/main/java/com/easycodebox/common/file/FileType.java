package com.easycodebox.common.file;

import com.easycodebox.common.enums.DetailEnum;

/**
 * 请使用MimeTypes类
 * @author WangXiaoJin
 * 
 */
@Deprecated
public enum FileType implements DetailEnum<String> {

	/** 
     * JPEG(JPG). 
     */  
	JPEG("FFD8FF", "jpg"),
    PNG("89504E47", "png"),
    GIF("47494638", "gif"),
    TIFF("49492A00", "tiff"),
    /** 
     * Windows Bitmap. 
     */  
    BMP("424D", "bmp"),  
    /** 
     * CAD. 
     */  
    DWG("41433130", "dwg"),  
    /** 
     * Adobe Photoshop. 
     */  
    PSD("38425053", "psd"),  
    /** 
     * Rich Text Format. 
     */  
    RTF("7B5C727466", "rtf"),  
    XML("3C3F786D6C", "xml"),  
    HTML("68746D6C3E", "html"),  
    /** 
     * Email [thorough only]. 
     */  
    EML("44656C69766572792D646174653A", "eml"),  
    /** 
     * Outlook Express. 
     */  
    DBX("CFAD12FEC5FD746F", "dbx"),  
    /** 
     * Outlook (pst). 
     */  
    PST("2142444E", "pst"),  
    /** 
     * MS Word/Excel. 
     */  
    XLS_DOC("D0CF11E0", "xls_doc"),  
    /** 
     * MS Access. 
     */  
    MDB("5374616E64617264204A", "mdb"),  
    /** 
     * WordPerfect. 
     */  
    WPD("FF575043", "wpd"),  
    /** 
     * Postscript. 
     */  
    EPS("252150532D41646F6265", "eps"),  
    /** 
     * Adobe Acrobat. 
     */  
    PDF("255044462D312E", "pdf"),  
    /** 
     * Quicken. 
     */  
    QDF("AC9EBD8F", "qdf"),  
      
    /** 
     * Windows Password. 
     */  
    PWL("E3828596", "pwl"),  
    ZIP("504B0304", "zip"),  
    /** 
     * RAR Archive. 
     */  
    RAR("52617221", "rar"),  
    /** 
     * Wave. 
     */  
    WAV("57415645", "wav"),  
    AVI("41564920", "avi"),  
    /** 
     * Real Audio. 
     */  
    RAM("2E7261FD", "ram"),  
    /** 
     * Real Media. 
     */  
    RM("2E524D46", "rm"),  
    /** 
     * MPEG (mpg). 
     */  
    MPG("000001BA", "mpg"),  
    /** 
     * Quicktime. 
     */  
    MOV("6D6F6F76", "mov"),  
    /** 
     * Windows Media. 
     */  
    ASF("3026B2758E66CF11", "asf"),  
    /** 
     * MIDI. 
     */  
    MID("4D546864", "mid");
	
	private String value;
	private String desc;
   
    FileType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public String getClassName() {
		return this.name();
	}
	
}
