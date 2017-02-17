package com.easycodebox.common.mail;

import java.util.Map;

/**
 * @author WangXiaoJin
 */
public abstract class AbstractTemplateProcessor implements TemplateProcessor {

    protected Map globalModel;

    @Override
    public void setGlobalModel(Map globalModel) {
        this.globalModel = globalModel;
    }

}
