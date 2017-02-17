package com.easycodebox.common.mail;

import org.apache.commons.collections.MapUtils;

import com.easycodebox.common.lang.Strings;

/**
 * 默认模板处理器，内部实现使用{@link Strings#format(String, Object...)}，
 * 使用详情请参考此方法
 * @author WangXiaoJin
 */
public class StringTemplateProcessor extends AbstractTemplateProcessor {

    /**
     * 当占位符没有找到对应的数据时则使用此默认值
     */
    private String defaultVal;

    /**
     * 替换模板内的占位符格式有以下三种:
     * <ul>
     *     <li>template：<i>{}走路掉{}里了</i>  model：<i>["小明", "坑"]</i> &nbsp; --> <i><b>小明</b>走路掉<b>坑</b>里了</i></li>
     *     <li>template：<i>{0}走路掉{1}里了</i>  model：<i>["小东", "缸"]</i> &nbsp; --> <i><b>小东</b>走路掉<b>缸</b>里了</i></li>
     *     <li>template：<i>{name}走路掉{thing}里了</i>  model：<i>{"name": "小李", "thing": "池塘"}</i> &nbsp; --> <i><b>小李</b>走路掉<b>池塘</b>里了</i></li>
     * </ul>
     * @param template 模板内容
     * @param model 替换数据 - 可以是POJO对象或者Map类型或者数组
     * @return
     */
    @Override
    public String process(String template, Object model) throws ParseTemplateException {
        int mode = Strings.EMPTY_INDEX_MODEL | Strings.NUM_INDEX_MODEL | Strings.KEY_VALUE_MODEL;
        try {
            String roughTmpl;
            if (model != null && model.getClass().isArray()
                    && !model.getClass().getComponentType().isPrimitive()) {
                roughTmpl = Strings.formatMix(template, mode, defaultVal, (Object[]) model);
            } else {
                roughTmpl = Strings.formatMix(template, mode, defaultVal, model);
            }
            if (MapUtils.isNotEmpty(this.globalModel)) {
                roughTmpl = Strings.formatMix(roughTmpl, mode, defaultVal, this.globalModel);
            }
            return roughTmpl;
        } catch (Exception e) {
            throw new ParseTemplateException("Parse template error.", e);
        }
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal;
    }
}
