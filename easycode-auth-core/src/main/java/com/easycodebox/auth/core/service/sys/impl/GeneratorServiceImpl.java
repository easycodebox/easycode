package com.easycodebox.auth.core.service.sys.impl;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.service.sys.GeneratorService;
import com.easycodebox.auth.model.entity.sys.Generator;
import com.easycodebox.auth.model.util.R;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.idgenerator.*;
import com.easycodebox.common.lang.DataConvert;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.lang.reflect.Fields;
import com.easycodebox.jdbc.LockMode;
import com.easycodebox.jdbc.support.AbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author WangXiaoJin
 *
 */
@Transactional
@Service("generatorService")
public class GeneratorServiceImpl extends AbstractServiceImpl<Generator> implements GeneratorService {

	private Lock lock = new ReentrantLock();
	
	@Resource
	private UserIdConverter userIdConverter;
	
	@Resource
	private AbstractIdGenTypeParser idGenTypeParser;
	
	@Override
	public List<Generator> list() {
		return super.list(sql().desc(R.Generator.createTime));
	}

	@Override
	public Generator load(String id) {
		Generator data = super.get(id);
		if (data != null) {
			data.setCreatorName(userIdConverter.idToRealOrNickname(data.getCreator()));
			data.setModifierName(userIdConverter.idToRealOrNickname(data.getModifier()));
		}
		return data;
	}
	
	@Override
	@Transactional
	public int update(Generator generator) {
		if(Strings.isBlank(generator.getMaxVal()))
			generator.setMaxVal(null);
		super.get(sql()
				.eqAst(R.Generator.id, generator.getId())
				.lockMode(LockMode.UPGRADE)
				);
		
		IdGeneratorType idGeneratorType = idGenTypeParser.parsePersistentKey(generator.getId());
		if(idGeneratorType.getIdGenerator() != null) {
			Class<? extends AbstractIdGenerator> type = idGeneratorType.getRawIdGenerator().getClass();
			try {
				for (Constructor<?> constructor : type.getConstructors()) {
					Class<?>[] parameterTypes = constructor.getParameterTypes();
					if (parameterTypes.length == 6) {
						AbstractIdGenerator<?> ge = (AbstractIdGenerator<?>)constructor.newInstance(
								generator.getIncrement(),
								generator.getFetchSize(),
								DataConvert.convertType(generator.getInitialVal(), parameterTypes[2]),
								DataConvert.convertType(generator.getCurrentVal(), parameterTypes[3]),
								DataConvert.convertType(generator.getMaxVal(), parameterTypes[4]),
								generator.getIsCycle()
						);
						idGeneratorType.setIdGenerator(ge);
						break;
					}
				}
			} catch (Exception e) {
				throw new BaseException("Instance AbstractIdGenerator error.", e);
			}
		}
		
		return super.update(sql()
				.updAst(R.Generator.initialVal, generator.getInitialVal())
				.updAst(R.Generator.currentVal, generator.getCurrentVal())
				.updAst(R.Generator.fetchSize, generator.getFetchSize())
				.updAst(R.Generator.increment, generator.getIncrement())
				.updAst(R.Generator.isCycle, generator.getIsCycle())
				.upd(R.Generator.maxVal, generator.getMaxVal())
				.eqAst(R.Generator.id, generator.getId())
				);
	}
	
	@Override
	@Transactional
	public int updateIsCycle(String id, YesNo isCycle) {
		super.get(sql()
				.eqAst(R.Generator.id, id)
				.lockMode(LockMode.UPGRADE)
				);
		
		int count = super.update(sql()
				.updAst(R.Generator.isCycle, isCycle)
				.eqAst(R.Generator.id, id)
		);
		IdGeneratorType idGeneratorType = idGenTypeParser.parsePersistentKey(id);
		if(idGeneratorType.getIdGenerator() != null) {
			try {
				Fields.writeField(idGeneratorType.getIdGenerator(), "isCycle", isCycle, true);
			} catch (Exception e) {
				throw new BaseException("Write isCycle field error.", e);
			}
		}
		return count;
	}

	@Override
	public DataPage<Generator> page(String id, YesNo isCycle, int pageNo, int pageSize) {
		return super.page(sql()
				.likeTrim(R.Generator.id, id)
				.eq(R.Generator.isCycle, isCycle)
				.desc(R.Generator.createTime)
				.limit(pageNo, pageSize)
				);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Generator incrementAndGet(IdGeneratorType idGeneratorType) {
		Generator g = super.get(sql()
						.eq(R.Generator.id, idGeneratorType.getPersistentKey())
						.lockMode(LockMode.UPGRADE));
		if(g == null) {
			lock.lock();
			try {
				g = super.get(sql()
						.eq(R.Generator.id, idGeneratorType.getPersistentKey())
						.lockMode(LockMode.UPGRADE));
				g = g == null ? this.add(idGeneratorType) : g;
			}finally {
				lock.unlock();
			}
		}
		AbstractIdGenerator ag = idGeneratorType.getIdGenerator();
		boolean updateDbCurrentVal = ag == null || ag.getGenNum() >= ag.getFetchSize();
		if(ag == null) {
	    	ag = idGeneratorType.getRawIdGenerator();
			idGeneratorType.setIdGenerator(ag);
		}
		if(updateDbCurrentVal) {
			Object nextBatchStart = ag.nextStepVal(g != null ? g.getCurrentVal() : null);
			//nextBatchStart 返回下批数据的起始值。返回null则不更新数据库的CurrentVal，例：生成uuid只依据自己的规则，所以返回null
			if(nextBatchStart != null) {
				this.update(sql()
						.upd(R.Generator.currentVal, nextBatchStart.toString())
						.eq(R.Generator.id, idGeneratorType.getPersistentKey()));
			}
		}
		return g;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void incrementGenerator(IdGeneratorType idGeneratorType) {
		incrementAndGet(idGeneratorType);
	}

	@Transactional
	@SuppressWarnings("rawtypes")
	private Generator add(IdGeneratorType type) {
		if(type.getIdGenerator() == null) {
			//没有Generator的type才需要出入数据库
			Generator temp = super.get(type.getPersistentKey());
			if(temp == null) {
				AbstractIdGenerator ag = type.getRawIdGenerator();
				//数据库中不存在才插入数据库
				Generator g = new Generator();
				g.setId(type.getPersistentKey());
		    	g.setInitialVal(ag.getInitialVal().toString());
		    	g.setCurrentVal(ag.getInitialVal().toString());
		    	g.setMaxVal(ag.getMaxVal() == null ? null : ag.getMaxVal().toString());
		    	g.setFetchSize(ag.getFetchSize());
		    	g.setIncrement(ag.getIncrement());
		    	g.setIsCycle(ag.getIsCycle());
		    	this.save(g);
		    	return g;
			}
		}
		return null;
	}
	
}
