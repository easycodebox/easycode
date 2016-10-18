package com.easycodebox.login.shiro.permission;

import java.io.Serializable;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.DomainPermission;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;

import com.easycodebox.common.lang.StringUtils;

/**
 * 使用AntPathMatcher来匹配权限，参考{@link org.apache.shiro.util.AntPathMatcher AntPathMatcher}类 <br>
 * 多个权限可以用 <code>dividerToken</code>属性值隔开，<code>dividerToken</code>默认值为<code>,</code>
 * @author WangXiaoJin
 *
 */
public class UrlWildcardPermission implements Permission, Serializable {

	private static final long serialVersionUID = -5844939019312129337L;
	
    protected static final String DIVIDER_TOKEN = ",";
    
    private PatternMatcher pathMatcher;
    private String dividerToken;

    private String[] parts;

    /**
     * Default no-arg constructor for subclasses only - end-user developers instantiating Permission instances must
     * provide a wildcard string at a minimum, since Permission instances are immutable once instantiated.
     * <p/>
     * Note that the WildcardPermission class is very robust and typically subclasses are not necessary unless you
     * wish to create type-safe Permission objects that would be used in your application, such as perhaps a
     * {@code UserPermission}, {@code SystemPermission}, {@code PrinterPermission}, etc.  If you want such type-safe
     * permission usage, consider subclassing the {@link DomainPermission DomainPermission} class for your needs.
     */
    protected UrlWildcardPermission() {
    	
    }

    public UrlWildcardPermission(String wildcardString) {
    	this(wildcardString, null);
    }
    
    public UrlWildcardPermission(String wildcardString, String dividerToken) {
    	this(wildcardString, dividerToken, null);
    }
    
    public UrlWildcardPermission(String wildcardString, String dividerToken, PatternMatcher pathMatcher) {
		this.dividerToken = dividerToken == null ? DIVIDER_TOKEN : dividerToken;
    	this.pathMatcher = pathMatcher == null ? new AntPathMatcher() : pathMatcher;
    	setParts(wildcardString);
    }

    protected void setParts(String wildcardString) {
        wildcardString = StringUtils.trimToNull(wildcardString);

        if (wildcardString == null) {
            throw new IllegalArgumentException("Wildcard string cannot be null or empty. Make sure permission strings are properly formatted.");
        }

        this.parts = wildcardString.split(dividerToken);

        if (this.parts.length == 0) {
            throw new IllegalArgumentException("Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
        }
    }

    public boolean implies(Permission p) {
        // By default only supports comparisons with other WildcardPermissions
        if (!(p instanceof UrlWildcardPermission)) {
            return false;
        }

        UrlWildcardPermission wp = (UrlWildcardPermission) p;

        String[] otherParts = wp.getParts();

        for (String otherPart : otherParts) {
        	boolean matched = false;
        	for (String part : getParts()) {
        		if (pathMatcher.matches(part, otherPart)) {
        			matched = true;
        			break;
				}
			}
        	if (!matched) {
				return false;
			}
        }
        return true;
    }

    public String toString() {
        return StringUtils.join(parts, dividerToken);
    }

    public boolean equals(Object o) {
        if (o instanceof UrlWildcardPermission) {
        	UrlWildcardPermission wp = (UrlWildcardPermission) o;
            return parts.equals(wp.parts);
        }
        return false;
    }

    public int hashCode() {
        return parts.hashCode();
    }

	public String[] getParts() {
		return parts;
	}

	public String getDividerToken() {
		return dividerToken;
	}

	public void setDividerToken(String dividerToken) {
		this.dividerToken = dividerToken;
	}

	public PatternMatcher getPathMatcher() {
		return pathMatcher;
	}

	public void setPathMatcher(PatternMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}
    
}
