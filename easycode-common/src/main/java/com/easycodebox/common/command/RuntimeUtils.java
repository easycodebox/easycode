package com.easycodebox.common.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 * 
 */
public abstract class RuntimeUtils {
    // 
	private static final Logger LOG = LoggerFactory.getLogger(RuntimeUtils.class);
	
    /**
     * 
     */
    public static int exec(String cmd) throws Exception {
        return exec(cmd, null, null);
    }
    
    public static int exec(String cmd, StreamHandler handler) throws Exception {
    	return exec(cmd, handler, handler);
    }
    
    public static int exec(String cmd, StreamHandler out, StreamHandler err) throws Exception {
    	Process proc = Runtime.getRuntime().exec(cmd);
    	
        StreamConsumer outConsumer = null;
        if(out != null) {
            outConsumer = new StreamConsumer(proc.getInputStream(), out);
            outConsumer.start();
        }
        
        StreamConsumer errConsumer = null;
        if(err != null) {
            errConsumer = new StreamConsumer(proc.getErrorStream(), err);
            errConsumer.start();
        }
        
        if(outConsumer != null) {
            outConsumer.join();
        }
        if(errConsumer != null) {
            errConsumer.join();
        }
        
        int r = proc.waitFor();
        destroyQuietly(proc);
        return r;
    }
    
    /**
	 * 
	 */
	public static void destroyQuietly(Process process) {
		// Precondition checking
		if(process == null) {
			return;
		}
		
		//
		try {
			IOUtils.closeQuietly(process.getInputStream());
			IOUtils.closeQuietly(process.getErrorStream());
			process.destroy();
		} catch(Exception e) {
			LOG.warn("failed to destroy process", e);
		}
	}
    
    /**
     * Helper classes
     */
    private static class StreamConsumer extends Thread {
        //
        private InputStream is;
        private StreamHandler handler;
        
        /**
         * 
         */
        public StreamConsumer(InputStream is, StreamHandler handler) {
            this.is = is;
            this.handler = handler;
        }
        
        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is, handler.getEncoding()));
                while(true) {
                    String line = reader.readLine();
                    if(line == null) {
                        break;
                    } else {
                        handler.onReadLine(line);
                    }
                }
            } catch(Exception e) {
            	LOG.error("unhandled exception in stream consumer", e);
            }
        }
    }

    /**
     * 
     */
    public interface StreamHandler {
    	
        String getEncoding();
        
        void onReadLine(String line) throws IOException;
    }
    
    /**
     * 
     */
    public static void main(String args[]) {
        try {
			StreamHandler out = new StreamHandler() {

				public String getEncoding() {
					return "ISO-2022-JP";
				}

				public void onReadLine(String line) throws IOException {
					System.out.println(line);
				}

			};

			StreamHandler err = new StreamHandler() {

				public String getEncoding() {
					return "ISO-2022-JP";
				}

				public void onReadLine(String line) throws IOException {
					System.err.println(line);
				}

			};

			System.out.println(RuntimeUtils.exec("java -version", out, err));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
