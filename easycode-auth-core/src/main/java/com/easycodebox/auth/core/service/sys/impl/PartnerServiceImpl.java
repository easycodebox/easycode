package com.easycodebox.auth.core.service.sys.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.enums.ModuleType;
import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.pojo.sys.Partner;
import com.easycodebox.auth.core.service.sys.PartnerService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.util.Constants;
import com.easycodebox.auth.core.util.R;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.auth.core.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.generator.Generators;
import com.easycodebox.common.jdbc.support.AbstractService;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
@Service("partnerService")
public class PartnerServiceImpl extends AbstractService<Partner> implements PartnerService {

	@Resource
	private UserIdConverter userIdConverter;
	
	@Override
	public List<Partner> list() {
		return super.list(sql()
				.desc(R.Partner.sort)
				.desc(R.Partner.createTime)
				);
	}

	@Override
	@Cacheable(Constants.CN.PARTNER)
	public Partner load(String id) {
		Partner data = super.get(sql()
				.eqAst(R.Partner.id, id)
				.ne(R.Partner.status, CloseStatus.DELETE)
				);
		if (data != null) {
			data.setCreatorName(userIdConverter.id2RealOrNickname(data.getCreator()));
			data.setModifierName(userIdConverter.id2RealOrNickname(data.getModifier()));
		}
		return data;
	}

	@Override
	@Transactional
	@Log(title = "添加合作商", moduleType = ModuleType.SYS)
	public Partner add(Partner partner) {
		
		Assert.isFalse(this.existName(partner.getName(), partner.getId()),
				CodeMsgExt.FAIL.msg("合作商名{0}已被占用", partner.getName()));
		
		partner.setPartnerKey((String)Generators.getGeneratorNextVal(GeneratorEnum.KEY));
		if(partner.getStatus() == null)
			partner.setStatus(CloseStatus.OPEN);
		if(partner.getSort() == null)
			partner.setSort(0);
		super.save(partner);
		return partner;
	}
	
	@Override
	@Log(title = "修改合作商", moduleType = ModuleType.SYS)
	@CacheEvict(cacheNames=Constants.CN.PARTNER, key="#partner.id")
	public int update(Partner partner) {
		Assert.isFalse(this.existName(partner.getName(), partner.getId()),
				CodeMsgExt.FAIL.msg("合作商名{0}已被占用", partner.getName()));
		
		if(partner.getStatus() != null) {
			LOG.info("The update method can not update status property.");
		}
		
		return super.update(sql()
				.updateNeed(R.Partner.name, partner.getName())
				.updateNeed(R.Partner.website, partner.getWebsite())
				//.update(R.Partner.status, partner.getStatus())
				.updateNeed(R.Partner.sort, partner.getSort())
				.updateNeed(R.Partner.contract, partner.getContract())
				.updateNeed(R.Partner.remark, partner.getRemark())
				.eqAst(R.Partner.id, partner.getId()));
	}

	@Override
	@Log(title = "逻辑删除合作商", moduleType = ModuleType.SYS)
	@CacheEvict(cacheNames=Constants.CN.PARTNER, keyGenerator=Constants.MULTI_KEY_GENERATOR)
	public int remove(String[] ids) {
		return super.updateStatus(ids, CloseStatus.DELETE);
	}
	
	@Override
	@Log(title = "物理删除合作商", moduleType = ModuleType.SYS)
	@CacheEvict(cacheNames=Constants.CN.PARTNER, keyGenerator=Constants.MULTI_KEY_GENERATOR)
	public int removePhy(String[] ids) {
		return super.delete(ids);
	}

	@Override
	public DataPage<Partner> page(String name, String partnerKey, 
			String website, CloseStatus status, int pageNo, int pageSize) {
		return super.page(sql()
				.like(R.Partner.name, name)
				.like(R.Partner.partnerKey, partnerKey)
				.like(R.Partner.website, website)
				.eq(R.Partner.status, status)
				.ne(R.Partner.status, CloseStatus.DELETE)
				.desc(R.Partner.sort)
				.desc(R.Partner.createTime)
				.limit(pageNo, pageSize));
	}
	
	@Override
	public boolean existName(String name, String excludeId) {
		return this.exist(sql()
				.eqAst(R.Partner.name, name)
				.ne(R.Partner.status, CloseStatus.DELETE)
				.ne(R.Partner.id, excludeId)
				);
	}
	
	@Override
	@Log(title = "开启关闭合作商", moduleType = ModuleType.SYS)
	@CacheEvict(cacheNames=Constants.CN.PARTNER, keyGenerator=Constants.MULTI_KEY_GENERATOR)
	public int openClose(String[] ids, CloseStatus status) {
		return super.updateStatus(ids, status);
	}
	
}
