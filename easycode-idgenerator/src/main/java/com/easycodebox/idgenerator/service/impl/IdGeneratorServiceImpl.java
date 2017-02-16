package com.easycodebox.idgenerator.service.impl;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.idconverter.UserIdConverter;
import com.easycodebox.common.idgenerator.*;
import com.easycodebox.common.lang.DataConvert;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.lang.reflect.Fields;
import com.easycodebox.idgenerator.entity.IdGenerator;
import com.easycodebox.idgenerator.service.IdGeneratorService;
import com.easycodebox.idgenerator.util.R;
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
public class IdGeneratorServiceImpl extends AbstractServiceImpl<IdGenerator> implements IdGeneratorService {

	private Lock lock = new ReentrantLock();
	
	@Resource
	private UserIdConverter userIdConverter;
	
	@Resource
	private AbstractIdGenTypeParser idGenTypeParser;
	
	@Override
	public List<com.easycodebox.idgenerator.entity.IdGenerator> list() {
		return super.list(sql().desc(R.IdGenerator.createTime));
	}

	@Override
	public com.easycodebox.idgenerator.entity.IdGenerator load(String id) {
		com.easycodebox.idgenerator.entity.IdGenerator data = super.get(id);
		if (data != null) {
			data.setCreatorName(userIdConverter.idToRealOrNickname(data.getCreator()));
			data.setModifierName(userIdConverter.idToRealOrNickname(data.getModifier()));
		}
		return data;
	}
	
	@Override
	@Transactional
	public int update(com.easycodebox.idgenerator.entity.IdGenerator idGenerator) {
		if(Strings.isBlank(idGenerator.getMaxVal()))
			idGenerator.setMaxVal(null);
		super.get(sql()
				.eqAst(R.IdGenerator.id, idGenerator.getId())
				.lockMode(LockMode.UPGRADE)
				);
		
		IdGeneratorType idGeneratorType = idGenTypeParser.parsePersistentKey(idGenerator.getId());
		if(idGeneratorType.getIdGenerator() != null) {
			Class<? extends AbstractIdGenerator> type = idGeneratorType.getRawIdGenerator().getClass();
			try {
				for (Constructor<?> constructor : type.getConstructors()) {
					Class<?>[] parameterTypes = constructor.getParameterTypes();
					if (parameterTypes.length == 6) {
						AbstractIdGenerator<?> ge = (AbstractIdGenerator<?>)constructor.newInstance(
								idGenerator.getIncrement(),
								idGenerator.getFetchSize(),
								DataConvert.convertType(idGenerator.getInitialVal(), parameterTypes[2]),
								DataConvert.convertType(idGenerator.getCurrentVal(), parameterTypes[3]),
								DataConvert.convertType(idGenerator.getMaxVal(), parameterTypes[4]),
								idGenerator.getIsCycle()
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
				.updAst(R.IdGenerator.initialVal, idGenerator.getInitialVal())
				.updAst(R.IdGenerator.currentVal, idGenerator.getCurrentVal())
				.updAst(R.IdGenerator.fetchSize, idGenerator.getFetchSize())
				.updAst(R.IdGenerator.increment, idGenerator.getIncrement())
				.updAst(R.IdGenerator.isCycle, idGenerator.getIsCycle())
				.upd(R.IdGenerator.maxVal, idGenerator.getMaxVal())
				.eqAst(R.IdGenerator.id, idGenerator.getId())
				);
	}
	
	@Override
	@Transactional
	public int updateIsCycle(String id, YesNo isCycle) {
		super.get(sql()
				.eqAst(R.IdGenerator.id, id)
				.lockMode(LockMode.UPGRADE)
				);
		
		int count = super.update(sql()
				.updAst(R.IdGenerator.isCycle, isCycle)
				.eqAst(R.IdGenerator.id, id)
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
	public DataPage<com.easycodebox.idgenerator.entity.IdGenerator> page(String id, YesNo isCycle, int pageNo, int pageSize) {
		return super.page(sql()
				.likeTrim(R.IdGenerator.id, id)
				.eq(R.IdGenerator.isCycle, isCycle)
				.desc(R.IdGenerator.createTime)
				.limit(pageNo, pageSize)
				);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public com.easycodebox.idgenerator.entity.IdGenerator incrementAndGet(IdGeneratorType idGeneratorType) {
		com.easycodebox.idgenerator.entity.IdGenerator g = super.get(sql()
						.eq(R.IdGenerator.id, idGeneratorType.getPersistentKey())
						.lockMode(LockMode.UPGRADE));
		if(g == null) {
			lock.lock();
			try {
				g = super.get(sql()
						.eq(R.IdGenerator.id, idGeneratorType.getPersistentKey())
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
						.upd(R.IdGenerator.currentVal, nextBatchStart.toString())
						.eq(R.IdGenerator.id, idGeneratorType.getPersistentKey()));
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
	private com.easycodebox.idgenerator.entity.IdGenerator add(IdGeneratorType type) {
		if(type.getIdGenerator() == null) {
			//没有Generator的type才需要出入数据库
			com.easycodebox.idgenerator.entity.IdGenerator temp = super.get(type.getPersistentKey());
			if(temp == null) {
				AbstractIdGenerator ag = type.getRawIdGenerator();
				//数据库中不存在才插入数据库
				com.easycodebox.idgenerator.entity.IdGenerator g = new com.easycodebox.idgenerator.entity.IdGenerator();
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
