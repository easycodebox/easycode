package com.easycodebox.common.sitemesh3;

import org.sitemesh.SiteMeshContext;
import org.sitemesh.content.ContentProperty;
import org.sitemesh.content.tagrules.TagRuleBundle;
import org.sitemesh.content.tagrules.html.ExportTagToContentRule;
import org.sitemesh.tagprocessor.State;
import org.sitemesh.tagprocessor.TagRule;

/**
 * 用于被装饰页面的Js
 * @author WangXiaoJin
 *
 */
public class InnerJsTagRuleBundle implements TagRuleBundle {

	@Override
	public void install(State defaultState, ContentProperty contentProperty, SiteMeshContext siteMeshContext) {
		TagRule tagRule = new ExportTagToContentRule(siteMeshContext, contentProperty.getChild("inner-js"), false);
		defaultState.addRule("inner-js", tagRule);
	}

	@Override
	public void cleanUp(State defaultState, ContentProperty contentProperty, SiteMeshContext siteMeshContext) {
		
	}

}
