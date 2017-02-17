package com.easycodebox.common.mail;

import java.util.Map;

/**
 * 模板处理器
 * @author WangXiaoJin
 */
public interface TemplateProcessor {

    /**
     * 处理模板
     * @param template 模板
     * @param model 数据
     * @return
     * @throws ParseTemplateException
     */
    String process(String template, Object model) throws ParseTemplateException;

    /**
     * 设置全局model数据，作为{@link #process} model参数的替补
     * @param globalModel
     */
    void setGlobalModel(Map globalModel);

}
