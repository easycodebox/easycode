package com.easycodebox.spring.boot;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 环境变量{@link #propName}（默认值：env）的值注册进 Active Profiles 中
 * @author WangXiaoJin
 */
public class ActiveProfileEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    public static final String DEFAULT_PROP_NAME = "env";
    
    private int order = ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    
    private String propName = DEFAULT_PROP_NAME;
    
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Assert.notNull(propName);
        String val = environment.getProperty(propName);
        if (StringUtils.hasText(val)) {
            List<String> list = Arrays.asList(StringUtils.commaDelimitedListToStringArray(
                    environment.resolvePlaceholders(val)));
            Collections.reverse(list);
            for (String item : list) {
                if (!environment.acceptsProfiles(item)) {
                    environment.addActiveProfile(item);
                    log.info("Added active profile : {}", item);
                } else {
                    log.info("Active profile '{}' has already existed.", item);
                }
            }
        }
    }
    
    @Override
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public String getPropName() {
        return propName;
    }
    
    public void setPropName(String propName) {
        this.propName = propName;
    }
}
