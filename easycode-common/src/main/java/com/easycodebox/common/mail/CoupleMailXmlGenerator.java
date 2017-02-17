package com.easycodebox.common.mail;

import com.easycodebox.common.validate.Assert;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.util.*;

/**
 * @author WangXiaoJin
 */
public class CoupleMailXmlGenerator implements ResourceLoaderAware, CoupleMailGenerator<Map<String, CoupleMail>> {

    private static final String ENTITY = "entity";
    private static final String KEY = "key";
    private static final String SUBJECT = "subject";
    private static final String CONTENT = "content";

    private ResourceLoader resourceLoader;

    private String location;

    private volatile long lastModified;

    public CoupleMailXmlGenerator(String location) {
        Assert.notBlank(location);
        this.location = location;
    }

    @Override
    public boolean isModified() throws GenerateCoupleMailException {
        try {
            Resource resource = resourceLoader.getResource(location);
            long resLastModified = resource.lastModified();
            if (resLastModified != lastModified) {
                lastModified = resLastModified;
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new GenerateCoupleMailException("Execute isModified error.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, CoupleMail> generate() throws GenerateCoupleMailException {
        SAXReader reader = new SAXReader();
        try (InputStream inputStream = resourceLoader.getResource(location).getInputStream()) {
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            List<Element> entities = root.elements(ENTITY);
            Map<String, CoupleMail> data = new HashMap<>();
            for (Element entity : entities) {
                String key = entity.attributeValue(KEY),
                        subject = entity.attributeValue(SUBJECT),
                        content = entity.attributeValue(CONTENT);
                if (key == null) {
                    key = entity.elementTextTrim(KEY);
                }
                if (subject == null) {
                    subject = entity.elementTextTrim(SUBJECT);
                }
                if (content == null) {
                    content = entity.elementTextTrim(CONTENT);
                }
                if (key == null || subject == null || content == null) continue;
                data.put(key, new CoupleMail(subject, content));
            }
            return data;
        } catch (Exception e) {
            throw new GenerateCoupleMailException("Generate CoupleMail error.", e);
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
