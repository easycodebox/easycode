package com.easycodebox.common.net;


import com.easycodebox.common.enums.entity.PhoneType;
import com.easycodebox.common.lang.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgentParser {
	private String userAgentString;
	private String browserName;
	private String browserVersion;
	private String browserOperatingSystem;
	private PhoneType smartPhoneType;
	private List<UserAgentDetails> parsedBrowsers = new ArrayList<UserAgentDetails>();

    /*private static Pattern pattern = Pattern.compile(
            "([^/\\s]*)(/([^\\s]*))?(\\s*\\[[a-zA-Z][a-zA-Z]\\])?" +
            "\\s*(\\((([^()]|(\\([^()]*\\)))*)\\))?\\s*");*/
	
	private static String AGENT_MODEL = "\\s*([\\s:\\w\\.-]+)([/]|\\s*)([\\w\\.-]*)\\s*(\\[[a-zA-Z][a-zA-Z]\\])?\\s*(\\(((([^\\(\\)]+|(\\([^\\(\\)]+\\)));?)+)\\))?\\s?";
	
	private static String IE_MODEL = "MSIE\\s*(.*?);";
	
	private static Pattern PATTERN = Pattern.compile(AGENT_MODEL);
	
	
	/**
	 * Parses the incoming user agent string into useful data about
	 * the browser and its operating system.
	 *
	 * @param userAgentString the user agent header from the browser.
	 */
	/*
	public UserAgentParser(String userAgentString) {
        this.userAgentString = userAgentString;
        Matcher matcher = pattern.matcher(userAgentString);
        
        this.setSmartPhoneType(userAgentString);
        
        while (matcher.find()) {
           
           // for(int i=0; i< matcher.groupCount(); i++) {
           //     System.err.println(i +": " + matcher.group(i));
           // }
            
            String nextBrowserName = matcher.group(1);
            String nextBrowserVersion = matcher.group(3);
            String nextBrowserComments = null;
            if (matcher.groupCount() >= 6) {
                nextBrowserComments = matcher.group(6);
            }
            parsedBrowsers.add(new UserAgentDetails(nextBrowserName,
                    nextBrowserVersion, nextBrowserComments));

        }

        if (parsedBrowsers.size() > 0) {
            processBrowserDetails();
        } else {
            throw new UserAgentParseException("Unable to parse user agent string: " + userAgentString);
        }

    }
   */
	public UserAgentParser(String userAgentString) {
		
		this.userAgentString = userAgentString;
		
		this.setSmartPhoneType(userAgentString);
		
		UserAgentDetails d = UserAgentParser.parseUserAgent(userAgentString);
		
		if (d != null) {
			browserName = d.getBrowserName();
			
			browserVersion = d.getBrowserVersion();
			
			String temp = browserName.trim().toLowerCase();
			
			if (temp.equals("docomo") || temp.equals("softbank") || temp.equals("au")) {
				browserOperatingSystem = d.getBrowserComments();
			} else {
				browserOperatingSystem = extractOperatingSystem(d.getBrowserComments());
			}
		} else {
			//throw new UserAgentParseException("Unable to parse user agent string: " + userAgentString);
			this.browserName = "";
			
			this.browserOperatingSystem = "";
			
			this.browserVersion = "";
		}
		
	}
	
	
	public static UserAgentDetails parseUserAgent(String userAgentString) {
		
		Matcher matcher = PATTERN.matcher(userAgentString);
		
		String brower_name;
		String brower_version;
		String brower_compents;
		if (matcher.find()) {
			String buffer = matcher.group(1).trim().toLowerCase();
			
			brower_name = matcher.group(1);
			brower_version = matcher.group(3);
			brower_compents = matcher.group(6);
			
			// not mozilla opera
			if (buffer.equals("opera")) {
				brower_name = matcher.group(1);
				
				brower_version = matcher.group(3);
				
				brower_compents = matcher.group(6);
				
				return new UserAgentDetails(brower_name, brower_version, brower_compents);
				
			}//docomo
			else if (buffer.equals("docomo")) {
				brower_name = matcher.group(1);
				
				brower_version = matcher.group(3);
				
				if (matcher.find()) {
					brower_compents = matcher.group(1) + matcher.group(3);
				}
				
				return new UserAgentDetails(brower_name, brower_version, brower_compents);
				
			}//SoftBank
			else if (buffer.equals("j-phone") || buffer.equals("vodafone") || buffer.equals("softbank")) {
				brower_name = "SoftBank";
				
				brower_version = matcher.group(3);
				
				if (matcher.find()) {
					brower_compents = matcher.group(1);
				}
				
				return new UserAgentDetails(brower_name, brower_version, brower_compents);
			}// old AU
			else if (buffer.equals("up.browser")) {
				brower_name = "AU";
				
				buffer = matcher.group(3);
				
				if (buffer != null && !buffer.trim().equals("")) {
					String[] temp = buffer.split("-");
					
					brower_compents = temp[1];
					
					brower_version = temp[0];
				}
				
				return new UserAgentDetails(brower_name, brower_version, brower_compents);
			}//NEW AU
			else if (buffer.startsWith("kddi")) {
				String[] temp = buffer.split("-");
				
				brower_name = "AU";
				
				brower_compents = temp[1];
				
				brower_version = matcher.group(3);
				
				return new UserAgentDetails(brower_name, brower_version, brower_compents);
				
			}
			// is Mozilla
			else if (buffer.equals("mozilla")) {
				buffer = matcher.group(6);
				
				if (buffer == null || buffer.trim().equals(Symbol.EMPTY)) {
					// not system info
					return null;
				} else {
					brower_compents = buffer;
					
					brower_name = matcher.group(1);
					
					brower_version = matcher.group(3);
					
					Pattern p = Pattern.compile(IE_MODEL);
					
					Matcher m = p.matcher(buffer);
					
					//IE
					if (m.find()) {
						brower_name = "IE";
						
						brower_version = m.group(1);
						
						return new UserAgentDetails(brower_name, brower_version, brower_compents);
						
					}//not IE
					else {
						if (matcher.find()) {
							buffer = matcher.group(1);
							
							brower_name = matcher.group(1);
							
							brower_version = matcher.group(3);
							
							if (buffer != null && !buffer.trim().equals("")) {
								//(chrome,Safari)
								if (buffer.trim().toLowerCase().equals("applewebkit")) {
									if (matcher.find()) {
										buffer = matcher.group(1);
										
										if (buffer != null && !buffer.trim().equals("")) {
											//chrome
											if (buffer.trim().toLowerCase().equals("chrome")) {
												brower_name = matcher.group(1);
												
												brower_version = matcher.group(3);
												
												return new UserAgentDetails(brower_name, brower_version, brower_compents);
											}
										}
									}
									
								}//opera
								else if (buffer.trim().toLowerCase().startsWith("opera")) {
									String[] temp = buffer.split(" ");
									
									brower_name = temp[0];
									
									brower_version = temp[1];
									
									return new UserAgentDetails(brower_name, brower_version, brower_compents);
								}
								
								//other
								while (matcher.find()) {
									brower_name = matcher.group(1);
									
									brower_version = matcher.group(3);
								}
								
								return new UserAgentDetails(brower_name, brower_version, brower_compents);
							}
						}
					}
				}
			}
			return new UserAgentDetails(brower_name, brower_version, brower_compents);
		}
		return null;
	}
	
	private void setSmartPhoneType(String userAgentString) {
		if (userAgentString.contains("iPad")) {
			this.smartPhoneType = PhoneType.IPAD;
		} else if (userAgentString.contains("Android")) {
			this.smartPhoneType = PhoneType.ANDROID;
		} else if (userAgentString.contains("iPhone")) {
			this.smartPhoneType = PhoneType.IPHONE;
		} else {
			this.smartPhoneType = PhoneType.OTHER;
		}
	}
	
	/**
	 * Wraps the process of extracting browser name, version, and
	 * operating sytem.
	 */
	@SuppressWarnings("unused")
	private void processBrowserDetails() {
		
		String[] browserNameAndVersion = extractBrowserNameAndVersion();
		browserName = browserNameAndVersion[0];
		browserVersion = browserNameAndVersion[1];
		
		browserOperatingSystem = extractOperatingSystem(parsedBrowsers.get(0).getBrowserComments());
		
	}
	
	/**
	 * Iterates through all component browser details to try and find the
	 * canonical browser name and version.
	 *
	 * @return a string array with browser name in element 0 and browser
	 * version in element 1. Null can be present in either or both.
	 */
	private String[] extractBrowserNameAndVersion() {
		
		String[] knownBrowsers = new String[]{
				"firefox", "netscape", "chrome", "safari", "camino", "mosaic", "opera",
				"galeon"
		};
		
		for (UserAgentDetails nextBrowser : parsedBrowsers) {
			for (String nextKnown : knownBrowsers) {
				if (nextBrowser.getBrowserName().toLowerCase().startsWith(nextKnown)) {
					return new String[]{nextBrowser.getBrowserName(), nextBrowser.getBrowserVersion()};
				}
				// TODO might need special case here for Opera's dodgy version
			}
			
		}
		UserAgentDetails firstAgent = parsedBrowsers.get(0);
		if (firstAgent.getBrowserName().toLowerCase().startsWith("mozilla")) {
			
			if (firstAgent.getBrowserComments() != null) {
				String[] comments = firstAgent.getBrowserComments().split(";");
				if (comments.length > 2 && comments[0].toLowerCase().startsWith("compatible")) {
					String realBrowserWithVersion = comments[1].trim();
					int firstSpace = realBrowserWithVersion.indexOf(' ');
					int firstSlash = realBrowserWithVersion.indexOf('/');
					if ((firstSlash > -1 && firstSpace > -1) ||
							(firstSlash > -1 && firstSpace == -1)) {
						// we have slash and space, or just a slash,
						// so let's choose slash for the split
						return new String[]{
								realBrowserWithVersion.substring(0, firstSlash),
								realBrowserWithVersion.substring(firstSlash + 1)
						};
					} else if (firstSpace > -1) {
						return new String[]{
								realBrowserWithVersion.substring(0, firstSpace),
								realBrowserWithVersion.substring(firstSpace + 1)
						};
					} else { // out of ideas for version, or no version supplied
						return new String[]{realBrowserWithVersion, null};
					}
				}
			}
			
			// Looks like a *real* Mozilla :-)
			if (new Float(firstAgent.getBrowserVersion()) < 5.0) {
				return new String[]{"Netscape", firstAgent.getBrowserVersion()};
			} else {
				// TODO: get version from comment string
				return new String[]{"Mozilla",
						firstAgent.getBrowserComments().split(";")[0].trim()};
			}
		} else {
			return new String[]{
					firstAgent.getBrowserName(), firstAgent.getBrowserVersion()
			};
		}
		
	}
	
	/**
	 * Extracts the operating system from the browser comments.
	 *
	 * @param comments the comment string afer the browser version
	 * @return a string representing the operating system
	 */
	private String extractOperatingSystem(String comments) {
		
		if (comments == null) {
			return null;
		}
		
		String[] knownOS = new String[]{"win", "linux", "mac", "freebsd", "netbsd",
				"openbsd", "sunos", "amiga", "beos", "irix", "os/2", "warp", "iphone", "willcom", "ipad", "ipod"};
		List<String> osDetails = new ArrayList<>();
		String[] parts = comments.split(";");
		for (String comment : parts) {
			String lowerComment = comment.toLowerCase().trim();
			for (String os : knownOS) {
				if (lowerComment.startsWith(os)) {
					osDetails.add(comment.trim());
				}
			}
			
		}
		switch (osDetails.size()) {
			case 0: {
				return comments;
			}
			case 1: {
				return osDetails.get(0);
			}
			default: {
				return osDetails.get(0); // need to parse more stuff here
			}
		}
		
	}
	
	/**
	 * Returns the name of the browser.
	 *
	 * @return the name of the browser
	 */
	public String getBrowserName() {
		return browserName;
	}
	
	/**
	 * Returns the version of the browser.
	 *
	 * @return the version of the browser
	 */
	public String getBrowserVersion() {
		return browserVersion;
	}
	
	/**
	 * Returns the operating system the browser is running on.
	 *
	 * @return the operating system the browser is running on.
	 */
	public String getBrowserOperatingSystem() {
		return browserOperatingSystem;
	}
	
	public PhoneType getSmartPhoneType() {
		return smartPhoneType;
	}
	
	public String getUserAgentString() {
		return userAgentString;
	}
	
	public void setUserAgentString(String userAgentString) {
		this.userAgentString = userAgentString;
	}
	
	public static String FF10 = "Mozilla/5.0 (Windows NT 5.1; rv:10.0) Gecko/20100101 Firefox/10.0";
	
	public static String IE8 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)";
	
	public static String CHROME = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11";
	
	public static String OPERA1 = "Opera/9.27 (Windows NT 5.2; U; zh-cn) ";
	
	public static String OPERA2 = "Mozilla/5.0 (Macintosh; PPC Mac OS X; U; en) Opera 8.0  ";
	
	public static String SAFARI1 = "Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Version/3.1 Safari/525.13 ";
	
	public static String SAFARI2 = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";
	
	public static String DOCOMO = "DoCoMo/2.0 N02B(c500;TB;W24H16;ser353151031393402;icc8981100020717929442F)";
	
	public static String SOFTBANK1 = "SoftBank/1.0/941P/PJP10/SN358861030569837 Browser/NetFront/3.4 Profile/MIDP-2.0 Configuration/CLDC-1.1";
	
	public static String SOFRBANK2 = "J-PHONE/4.0/J-SH51/SN12345678901 SH/0001a Profile/MIDP-1.0 Configuration/CLDC-1.0  ";
	
	public static String SOFRBANK3 = "Vodafone/1.0/V904SH/SHJ001/SN123456789012345 Browser/VF-NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1 ";
	
	public static String AU1 = "KDDI-SH3G UP.Browser/6.2_7.2.7.1.K.5.176 (GUI) MMP/2.0";
	
	public static String AU2 = "UP.Browser/3.04-SN12 UP.Link/3.4.4  ";
	
	public static String NETFRONT = "Mozilla/3.0 (WILLCOM;KYOCERA/WX03K/2;1.0.2.7.000000/1/C256) NetFront/3.4";
	
	public static String NETSCAPE = "Mozilla/5.0 (Windows; U; Win 9x 4.90; SG; rv:1.9.2.4) Gecko/20101104 Netscape/9.1.0285";
	
	public static String CAMINO = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en; rv:1.9.2.14pre) Gecko/20101212 Camino/2.1a1pre (like Firefox/3.6.14pre)";
	
	public static String GALENON = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.8) Gecko/20090327 Galeon/2.0.7";
	
	public static void main(String[] args) {
		UserAgentParser p = new UserAgentParser("Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3");
		
		System.out.println(p.getBrowserName());
		
		System.out.println(p.getBrowserVersion());
		
		System.out.println(p.getBrowserOperatingSystem());
	}
	
}
