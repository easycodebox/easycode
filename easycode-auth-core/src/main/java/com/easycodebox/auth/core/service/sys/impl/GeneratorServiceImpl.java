package com.easycodebox.auth.core.service.sys.impl;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.service.sys.GeneratorService;
import com.easycodebox.auth.model.entity.sys.Generator;
import com.easycodebox.auth.model.util.R;
import com.easycodebox.auth.model.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.generator.AbstractGenerator;
import com.easycodebox.common.generator.GeneratorType;
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
	
	@Override
	public List<Generator> list() {
		return super.list(sql().desc(R.Generator.createTime));
	}

	@Override
	public Generator load(GeneratorEnum generatorType) {
		Generator data = super.get(generatorType);
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
				.eqAst(R.Generator.generatorType, generator.getGeneratorType())
				.lockMode(LockMode.UPGRADE)
				);
		
		if(generator.getGeneratorType().getGenerator() != null) {
			Class<? extends AbstractGenerator> type = generator.getGeneratorType().getRawGenerator().getClass();
			try {
				for (Constructor<?> constructor : type.getConstructors()) {
					Class<?>[] parameterTypes = constructor.getParameterTypes();
					if (parameterTypes.length == 6) {
						AbstractGenerator<?> ge = (AbstractGenerator<?>)constructor.newInstance(
								generator.getIncrement(),
								generator.getFetchSize(),
								DataConvert.convertType(generator.getInitialVal(), parameterTypes[2]),
								DataConvert.convertType(generator.getCurrentVal(), parameterTypes[3]),
								DataConvert.convertType(generator.getMaxVal(), parameterTypes[4]),
								generator.getIsCycle()
						);
						generator.getGeneratorType().setGenerator(ge);
						break;
					}
				}
			} catch (Exception e) {
				throw new BaseException("Instance AbstractGenerator error.", e);
			}
		}
		
		return super.update(sql()
				.updAst(R.Generator.initialVal, generator.getInitialVal())
				.updAst(R.Generator.currentVal, generator.getCurrentVal())
				.updAst(R.Generator.fetchSize, generator.getFetchSize())
				.updAst(R.Generator.increment, generator.getIncrement())
				.updAst(R.Generator.isCycle, generator.getIsCycle())
				.upd(R.Generator.maxVal, generator.getMaxVal())
				.eqAst(R.Generator.generatorType, generator.getGeneratorType())
				);
	}
	
	@Override
	@Transactional
	public int updateIsCycle(GeneratorEnum generatorType, YesNo isCycle) {
		super.get(sql()
				.eqAst(R.Generator.generatorType, generatorType)
				.lockMode(LockMode.UPGRADE)
				);
		
		if(generatorType.getGenerator() != null) {
			try {
				Fields.writeField(generatorType.getGenerator(), "isCycle", isCycle, true);
			} catch (Exception e) {
				throw new BaseException("Write isCycle field error.", e);
			}
		}
		
		return super.update(sql()
				.updAst(R.Generator.isCycle, isCycle)
				.eqAst(R.Generator.generatorType, generatorType)
				);
	}

	@Override
	public DataPage<Generator> page(GeneratorEnum generatorType, 
			YesNo isCycle, int pageNo, int pageSize) {
		return super.page(sql()
				.eq(R.Generator.generatorType, generatorType)
				.eq(R.Generator.isCycle, isCycle)
				.desc(R.Generator.createTime)
				.limit(pageNo, pageSize)
				);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Generator incrementAndGet(GeneratorEnum generatorType) {
		Generator g = super.get(sql()
						.eq(R.Generator.generatorType, generatorType)
						.lockMode(LockMode.UPGRADE));
		if(g == null) {
			lock.lock();
			try {
				g = super.get(sql()
						.eq(R.Generator.generatorType, generatorType)
						.lockMode(LockMode.UPGRADE));
				g = g == null ? this.add(generatorType) : g;
			}finally {
				lock.unlock();
			}
		}
		AbstractGenerator ag = generatorType.getGenerator();
		boolean updateDbCurrentVal = ag == null || ag.getGenNum() >= ag.getFetchSize();
		if(ag == null) {
	    	ag = generatorType.getRawGenerator();
	    	generatorType.setGenerator(ag);
		}
		if(updateDbCurrentVal) {
			Object nextBatchStart = ag.nextStepVal(g != null ? g.getCurrentVal() : null);
			//nextBatchStart 返回下批数据的起始值。返回null则不更新数据库的CurrentVal，例：生成uuid只依据自己的规则，所以返回null
			if(nextBatchStart != null) {
				this.update(sql()
						.upd(R.Generator.currentVal, nextBatchStart.toString())
						.eq(R.Generator.generatorType, generatorType));
			}
		}
		return g;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void incrementGenerator(GeneratorType generatorType) {
		incrementAndGet((GeneratorEnum)generatorType);
	}

	@Override
	@Transactional
	public int batchAdd() throws Exception {
		GeneratorEnum[] types = GeneratorEnum.values();
		int num = 0;
		for(GeneratorEnum type : types) {
			if(this.add(type) != null) num++;
		}
		return num;
	}
	
	@Transactional
	@SuppressWarnings("rawtypes")
	private Generator add(GeneratorEnum type) {
		if(type.getGenerator() == null) {
			//没有Generator的type才需要出入数据库
			Generator temp = super.get(type);
			if(temp == null) {
				AbstractGenerator ag = type.getRawGenerator();
				//数据库中不存在才插入数据库
				Generator g = new Generator();
				g.setGeneratorType(type);
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
