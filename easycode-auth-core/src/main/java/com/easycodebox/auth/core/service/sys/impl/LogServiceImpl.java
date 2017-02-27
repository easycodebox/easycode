package com.easycodebox.auth.core.service.sys.impl;

import com.easycodebox.auth.core.service.sys.LogService;
import com.easycodebox.auth.model.entity.sys.Log;
import com.easycodebox.auth.model.enums.ModuleType;
import com.easycodebox.auth.model.util.R;
import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.idconverter.UserIdConverter;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.jdbc.support.AbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
@Service
public class LogServiceImpl extends AbstractServiceImpl<Log> implements LogService {

	@Autowired
	private UserIdConverter userIdConverter;
	
	@Override
	public List<Log> list(String url, String params, ModuleType moduleType, 
			LogLevel logLevel, String result, String clientIp) {
		return super.list(sql()
				.likeTrim(R.Log.url, url)
				.likeTrim(R.Log.params, params)
				.eq(R.Log.moduleType, moduleType)
				.eq(R.Log.logLevel, logLevel)
				.likeTrim(R.Log.result, result)
				.likeTrim(R.Log.clientIp, clientIp)
				.desc(R.Log.createTime)
				);
	}

	@Override
	public Log load(Long id) {
		Log data = super.get(id);
		if (data != null) {
			data.setCreatorName(userIdConverter.idToRealOrNickname(data.getCreator()));
		}
		return data;
	}

	@Override
	@Transactional
	public Log add(Log log) {
		if(Strings.isBlank(log.getTitle()))
			log.setTitle(null);
		super.save(log);
		return log;
	}
	
	@Override
	public int update(Log log) {
		return super.update(log);
	}

	@Override
	public int removePhy(Long[] ids) {
		return super.deletePhy(ids);
	}

	@Override
	public DataPage<Log> page(String title, String url, String params, ModuleType moduleType, 
			LogLevel logLevel, String result, String clientIp, int pageNo, int pageSize) {
		return super.page(sql()
				.likeTrim(R.Log.title, title)
				.likeTrim(R.Log.url, url)
				.likeTrim(R.Log.params, params)
				.eq(R.Log.moduleType, moduleType)
				.eq(R.Log.logLevel, logLevel)
				.likeTrim(R.Log.result, result)
				.likeTrim(R.Log.clientIp, clientIp)
				.desc(R.Log.createTime)
				.limit(pageNo, pageSize)
				);
	}
	
}
