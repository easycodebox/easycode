package com.easycodebox.common.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 * 
 */
@Deprecated
public class Command {
	
	private static final Logger log = LoggerFactory.getLogger(Command.class);
	
	private static final Runtime rt = Runtime.getRuntime();

	public static void exec(String command) {
		Process proc = null;
		BufferedReader reader = null;
		try {
			log.info("命令信息===开始===" + command);
			proc = rt.exec(command);
			InputStreamReader isr = new InputStreamReader(proc.getErrorStream());
			reader = new BufferedReader(isr);
			String line = null;
			while((line = reader.readLine()) != null) {
				log.info(line);
			}
			log.info("==========正常信息和错误信息分隔线==========");
			isr = new InputStreamReader(proc.getInputStream());
			reader = new BufferedReader(isr);
			while((line = reader.readLine()) != null) {
				log.info(line);
			}
			int exitVal = proc.waitFor();  
			log.info("Process exitValue: " + exitVal);
		} catch (IOException e) {
			log.error("", e);
		} catch (InterruptedException e) {
			log.error("", e);
		}finally {
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					log.error("", e);
				}
		}
	}
	
}
