package com.easycodebox.common.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.activation.FileTypeMap;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * 修改自javax.activation.MimetypesFileTypeMap类 <p>
 * This class extends FileTypeMap and provides data typing of files
 * via their file extension. It uses the <code>.mime.types</code> format. <p>
 *
 * <b>MIME types file search order:</b><p>
 * The MimetypesFileTypeMap looks in various places in the user's
 * system for MIME types file entries. When requests are made
 * to search for MIME types in the MimetypesFileTypeMap, it searches  
 * MIME types files in the following order:
 * <p>
 * <ol>
 * <li> Programmatically added entries to the MimetypesFileTypeMap instance.
 * <li> The file <code>.mime.types</code> in the user's home directory.
 * <li> The file &lt;<i>java.home</i>&gt;<code>/lib/mime.types</code>.
 * <li> The file or resources named <code>META-INF/mime.types</code>.
 * <li> The file or resource named <code>META-INF/mimetypes.default</code>
 * </ol>
 * <p>
 * <b>MIME types file format:</b><p>
 * <code>
 * # comments begin with a '#'<br>
 * # the format is &lt;mime type> &lt;space separated file extensions><br>
 * # for example:<br>
 * text/plain    txt text TXT<br>
 * # this would map file.txt, file.text, and file.TXT to<br>
 * # the mime type "text/plain"<br>
 * 
 * <b>type/subtype ext1 ext2 ... or type=type/subtype desc="description of type" exts=ext1,ext2,...</b>
 * </code>
 * @author WangXiaoJin
 * 
 */
public class MimetypesFileTypeMap extends FileTypeMap {

	private static final Logger LOG = LoggerFactory.getLogger(MimetypesFileTypeMap.class);

	private static MimeTypeFile defDB = null;
	private MimeTypeFile[] DB;
	private static final int PROG = 0; // programmatically added entries

	private static String DEFAUL_TTYPE = "application/octet-stream";

	/**
	 * The default constructor.
	 */
	private MimetypesFileTypeMap() {
		List<MimeTypeFile> dbv = new ArrayList<>(5);
		MimeTypeFile mf = null;
		dbv.add(null);

		LOG.info("MimetypesFileTypeMap: load HOME");
		try {
			String user_home = System.getProperty("user.home");

			if (user_home != null) {
				String path = user_home + File.separator + ".mime.types";
				mf = loadFile(path);
				if (mf != null)
					dbv.add(mf);
			}
		} catch (SecurityException ex) {
		}

		LOG.info("MimetypesFileTypeMap: load SYS");
		try {
			// check system's home
			String system_mimetypes = System.getProperty("java.home")
					+ File.separator + "lib" + File.separator + "mime.types";
			mf = loadFile(system_mimetypes);
			if (mf != null)
				dbv.add(mf);
		} catch (SecurityException ex) {
		}

		LOG.info("MimetypesFileTypeMap: load JAR");
		// load from the app's jar file
		loadAllResources(dbv, "META-INF/mime.types");

		LOG.info("MimetypesFileTypeMap: load DEF");
		synchronized (MimetypesFileTypeMap.class) {
			// see if another instance has created this yet.
			if (defDB == null)
				defDB = loadResource("/META-INF/mimetypes.default");
		}

		if (defDB != null)
			dbv.add(defDB);

		DB = dbv.toArray(new MimeTypeFile[0]);
	}

	/**
	 * 获取单例对象
	 * @return
	 */
	public static MimetypesFileTypeMap getInstance() {
		return MimetypesFileTypeMapSingleton.INSTANCE;
	}
	
	private static final class MimetypesFileTypeMapSingleton {
		
		static final MimetypesFileTypeMap INSTANCE = new MimetypesFileTypeMap();
		
	}
	
	/**
	 * Load from the named resource.
	 */
	private MimeTypeFile loadResource(String name) {
		try(InputStream clis = this.getClass().getResourceAsStream(name)) {
			if (clis != null) {
				MimeTypeFile mf = new MimeTypeFile(clis);
				LOG.info("MimetypesFileTypeMap: successfully loaded mime types file: " + name);
				return mf;
			} else {
				LOG.warn("MimetypesFileTypeMap: not loading mime types file: " + name);
			}
		} catch (IOException e) {
			LOG.error("MimetypesFileTypeMap: load file ({0}) failed.", e, name);
		}
		return null;
	}

	private URL[] getResources(final ClassLoader cl, final String name) {
		return AccessController.doPrivileged(new PrivilegedAction<URL[]>() {
			public URL[] run() {
				URL[] ret = null;
				try {
					List<URL> v = new ArrayList<>();
					Enumeration<URL> e = cl == null ? ClassLoader.getSystemResources(name) : cl.getResources(name);
					while (e != null && e.hasMoreElements()) {
						URL url = e.nextElement();
						if (url != null)
							v.add(url);
					}
					if (v.size() > 0) {
						ret = new URL[v.size()];
						ret = v.toArray(ret);
					}
				} catch (IOException ioex) {
				} catch (SecurityException ex) {
				}
				return ret;
			}
		});
	}

	/**
	 * Load all of the named resource.
	 */
	private void loadAllResources(List<MimeTypeFile> v, String name) {
		boolean anyLoaded = false;
		URL[] urls = getResources(ClassUtils.getClassLoader(), name);
		if (urls != null) {
			LOG.info("MimetypesFileTypeMap: getResources");
			for (int i = 0; i < urls.length; i++) {
				URL url = urls[i];
				LOG.info("MimetypesFileTypeMap: URL " + url);
				try(InputStream clis = url.openStream()) {
					if (clis != null) {
						v.add(new MimeTypeFile(clis));
						anyLoaded = true;
						LOG.info("MimetypesFileTypeMap: successfully loaded mime types from URL: " + url);
					} else {
						LOG.warn("MimetypesFileTypeMap: not loading mime types from URL: " + url);
					}
				} catch (IOException e) {
					LOG.info("MimetypesFileTypeMap: can't load " + name, e);
				}
			}
		}

		// if failed to load anything, fall back to old technique, just in case
		if (!anyLoaded) {
			LOG.info("MimetypesFileTypeMap: !anyLoaded");
			MimeTypeFile mf = loadResource(Symbol.SLASH + name);
			if (mf != null)
				v.add(mf);
		}
	}

	/**
	 * Load the named file.
	 */
	private MimeTypeFile loadFile(String name) {
		MimeTypeFile mtf = null;

		try {
			mtf = new MimeTypeFile(name);
		} catch (IOException e) {
			
		}
		return mtf;
	}

	/**
	 * Construct a MimetypesFileTypeMap with programmatic entries added from the
	 * named file.
	 *
	 * @param mimeTypeFileName
	 *            the file name
	 */
	public MimetypesFileTypeMap(String mimeTypeFileName) throws IOException {
		this();
		DB[PROG] = new MimeTypeFile(mimeTypeFileName);
	}

	/**
	 * Construct a MimetypesFileTypeMap with programmatic entries added from the
	 * InputStream.
	 *
	 * @param is
	 *            the input stream to read from
	 */
	public MimetypesFileTypeMap(InputStream is) {
		this();
		try {
			DB[PROG] = new MimeTypeFile(is);
		} catch (IOException ex) {
			// XXX - really should throw it
		}
	}

	/**
	 * Prepend the MIME type values to the registry.
	 *
	 * @param mime_types
	 *            A .mime.types formatted string of entries.
	 */
	public synchronized void addMimeTypes(String mime_types) {
		// check to see if we have created the registry
		if (DB[PROG] == null)
			DB[PROG] = new MimeTypeFile(); // make one

		DB[PROG].appendToRegistry(mime_types);
	}

	/**
	 * Return the MIME type of the file object. The implementation in this class
	 * calls <code>getContentType(f.getName())</code>.
	 *
	 * @param f
	 *            the file
	 * @return the file's MIME type
	 */
	public String getContentType(File f) {
		return this.getContentType(f.getName());
	}

	/**
	 * Return the MIME type based on the specified file name. The MIME type
	 * entries are searched as described above under <i>MIME types file search
	 * order</i>. If no entry is found, the type "application/octet-stream" is
	 * returned.
	 *
	 * @param filename
	 *            the file name
	 * @return the file's MIME type
	 */
	public String getContentType(String filename) {
		int dot_pos = filename.lastIndexOf("."); // period index

		if (dot_pos < 0)
			return DEFAUL_TTYPE;

		return getContentTypeByExt(filename.substring(dot_pos + 1));
	}
	
	/**
	 * 通过文件扩展名获取ContentType
	 */
	public synchronized String getContentTypeByExt(String extension) {
		if(StringUtils.isBlank(extension))
			return DEFAUL_TTYPE;

		for (MimeTypeFile mt : DB) {
			if (mt == null)
				continue;
			String result = mt.getMimeType(extension);
			if (result != null)
				return result;
		}
		return DEFAUL_TTYPE;
	}
	
	/**
	 * 根据文件的mimeType或者可用的文件扩展名
	 * @param mimeType
	 * @return
	 */
	public synchronized String[] getExtensions(String mimeType) {
		for (MimeTypeFile mt : DB) {
			if (mt == null)
				continue;
			String[] result = mt.getExts(mimeType);
			if (result != null)
				return result;
		}
		return null;
	}

	public class MimeTypeFile {
		
		private ConcurrentMap<String, String> extToType = new ConcurrentHashMap<>();
		private ConcurrentMap<String, String[]> typeToExts = new ConcurrentHashMap<>();

		public MimeTypeFile(String filename) throws IOException {
			try(FileReader fr = new FileReader(new File(filename))) {
				parse(new BufferedReader(fr));
			}
		}

		public MimeTypeFile(InputStream is) throws IOException {
			parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
		}

		/**
		 * Creates an empty DB.
		 */
		public MimeTypeFile() {
			
		}

		/**
		 * get the MimeTypeEntry based on the file extension
		 */
		public String getMimeType(String file_ext) {
			return extToType.get(file_ext.toLowerCase());
		}
		
		/**
		 * 根据mimeType获取文件扩展名
		 * @param mimeType
		 * @return
		 */
		public String[] getExts(String mimeType) {
			return typeToExts.get(mimeType.toLowerCase());
		}

		/**
		 * Appends string of entries to the types registry, must be valid
		 * .mime.types format. A mime.types entry is one of two forms:
		 *
		 * type/subtype ext1 ext2 ... or type=type/subtype
		 * desc="description of type" exts=ext1,ext2,...
		 *
		 * Example: # this is a test audio/basic au text/plain txt text
		 * type=application/postscript exts=ps,eps
		 */
		public void appendToRegistry(String mime_types) {
			try {
				parse(new BufferedReader(new StringReader(mime_types)));
			} catch (IOException ex) {
				// can't happen
			}
		}

		/**
		 * Parse a stream of mime.types entries.
		 */
		private void parse(BufferedReader buf_reader) throws IOException {
			String line = null, prev = null;
			while ((line = buf_reader.readLine()) != null) {
				if (prev == null)
					prev = line;
				else
					prev += line;
				int end = prev.length();
				if (prev.length() > 0 && prev.charAt(end - 1) == '\\') {
					prev = prev.substring(0, end - 1);
					continue;
				}
				this.parseEntry(prev);
				prev = null;
			}
			if (prev != null)
				this.parseEntry(prev);
		}

		/**
		 * Parse single mime.types entry.
		 */
		private void parseEntry(String line) {
			String mime_type = null;
			String file_ext = null;
			List<String> exts = new ArrayList<>(4);
			line = line.trim();

			if (line.length() == 0) // empty line...
				return; // BAIL!

			// check to see if this is a comment line?
			if (line.charAt(0) == '#')
				return; // then we are done!

			// is it a new format line or old format?
			if (line.indexOf('=') > 0) {
				// new format
				LineTokenizer lt = new LineTokenizer(line);
				while (lt.hasMoreTokens()) {
					String name = lt.nextToken();
					String value = null;
					if (lt.hasMoreTokens() && lt.nextToken().equals("=")
							&& lt.hasMoreTokens())
						value = lt.nextToken();
					if (value == null) {
						LOG.info("Bad .mime.types entry: " + line);
						return;
					}
					if (name.equals("type")) {
						mime_type = value;
						exts.clear();
					} else if (name.equals("exts")) {
						StringTokenizer st = new StringTokenizer(value, ",");
						while (st.hasMoreTokens()) {
							file_ext = st.nextToken();
							extToType.put(file_ext, mime_type);
							exts.add(file_ext);
						}
						typeToExts.put(mime_type, exts.toArray(new String[0]));
					}
				}
			} else {
				// old format
				// count the tokens
				StringTokenizer strtok = new StringTokenizer(line);
				int num_tok = strtok.countTokens();

				if (num_tok == 0) // empty line
					return;

				mime_type = strtok.nextToken(); // get the MIME type
				exts.clear();
				
				while (strtok.hasMoreTokens()) {
					file_ext = strtok.nextToken();
					extToType.put(file_ext, mime_type);
					exts.add(file_ext);
				}
				typeToExts.put(mime_type, exts.toArray(new String[0]));
			}
		}

	}

	class LineTokenizer {
		private int currentPosition;
		private int maxPosition;
		private String str;
		private Vector<String> stack = new Vector<>();
		private static final String singles = "="; // single character tokens

		/**
		 * Constructs a tokenizer for the specified string.
		 * <p>
		 *
		 * @param str
		 *            a string to be parsed.
		 */
		public LineTokenizer(String str) {
			currentPosition = 0;
			this.str = str;
			maxPosition = str.length();
		}

		/**
		 * Skips white space.
		 */
		private void skipWhiteSpace() {
			while ((currentPosition < maxPosition)
					&& Character.isWhitespace(str.charAt(currentPosition))) {
				currentPosition++;
			}
		}

		/**
		 * Tests if there are more tokens available from this tokenizer's
		 * string.
		 *
		 * @return <code>true</code> if there are more tokens available from
		 *         this tokenizer's string; <code>false</code> otherwise.
		 */
		public boolean hasMoreTokens() {
			if (stack.size() > 0)
				return true;
			skipWhiteSpace();
			return (currentPosition < maxPosition);
		}

		/**
		 * Returns the next token from this tokenizer.
		 *
		 * @return the next token from this tokenizer.
		 * @exception NoSuchElementException
		 *                if there are no more tokens in this tokenizer's
		 *                string.
		 */
		public String nextToken() {
			int size = stack.size();
			if (size > 0) {
				String t = (String) stack.elementAt(size - 1);
				stack.removeElementAt(size - 1);
				return t;
			}
			skipWhiteSpace();

			if (currentPosition >= maxPosition) {
				throw new NoSuchElementException();
			}

			int start = currentPosition;
			char c = str.charAt(start);
			if (c == '"') {
				currentPosition++;
				boolean filter = false;
				while (currentPosition < maxPosition) {
					c = str.charAt(currentPosition++);
					if (c == '\\') {
						currentPosition++;
						filter = true;
					} else if (c == '"') {
						String s;

						if (filter) {
							StringBuilder sb = new StringBuilder();
							for (int i = start + 1; i < currentPosition - 1; i++) {
								c = str.charAt(i);
								if (c != '\\')
									sb.append(c);
							}
							s = sb.toString();
						} else
							s = str.substring(start + 1, currentPosition - 1);
						return s;
					}
				}
			} else if (singles.indexOf(c) >= 0) {
				currentPosition++;
			} else {
				while ((currentPosition < maxPosition)
						&& singles.indexOf(str.charAt(currentPosition)) < 0
						&& !Character.isWhitespace(str.charAt(currentPosition))) {
					currentPosition++;
				}
			}
			return str.substring(start, currentPosition);
		}

		public void pushToken(String token) {
			stack.addElement(token);
		}
	}

}

