package com.easycodebox.common.mail;

import com.easycodebox.common.validate.Assert;
import freemarker.template.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import javax.annotation.PostConstruct;
import java.io.StringWriter;

/**
 * 处理Freemarker模板
 * @author WangXiaoJin
 */
public class FreemarkerTemplateProcessor extends AbstractTemplateProcessor {

    private FreeMarkerConfigurationFactory freeMarkerConfigurationFactory;

    private Configuration configuration;

    @PostConstruct
    public void init() throws Exception {
        Assert.isFalse(freeMarkerConfigurationFactory == null && configuration == null,
                "freeMarkerConfigurationFactory and configuration are both null.");
        if (configuration == null && freeMarkerConfigurationFactory != null) {
            configuration = freeMarkerConfigurationFactory.createConfiguration();
        }
        if (MapUtils.isNotEmpty(this.globalModel)) {
            configuration.setAllSharedVariables(new SimpleHash(this.globalModel, configuration.getObjectWrapper()));
        }
    }

    @Override
    public String process(String template, Object model) throws ParseTemplateException {
        try (StringWriter writer = new StringWriter()) {
            Template tmpl = configuration.getTemplate(template);
            tmpl.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new ParseTemplateException("Process template \"" + template + "\" error.", e);
        }
    }

    public FreeMarkerConfigurationFactory getFreeMarkerConfigurationFactory() {
        return freeMarkerConfigurationFactory;
    }

    public void setFreeMarkerConfigurationFactory(FreeMarkerConfigurationFactory freeMarkerConfigurationFactory) {
        this.freeMarkerConfigurationFactory = freeMarkerConfigurationFactory;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

}
