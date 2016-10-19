package com.easycodebox.login.shiro.permission;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;

/**
 * 使用AntPathMatcher来匹配权限，参考{@link org.apache.shiro.util.AntPathMatcher AntPathMatcher}类 <br>
 * 多个权限可以用 <code>dividerToken</code>属性值隔开，<code>dividerToken</code>默认值为<code>,</code>
 * @author WangXiaoJin
 *
 */
public class UrlWildcardPermission implements GlobalPermission {

	private static final long serialVersionUID = -5844939019312129337L;
	
    protected static final String DIVIDER_TOKEN = ",";
    protected static final String NO_PERMITTED_FLAG = ":0";
    
    private PatternMatcher pathMatcher;
    private String dividerToken;
    /**
     * 没有此权限的标记，此标记只有出现在权限字符窜的末尾才有效
     */
    private String noPermittedFlag = NO_PERMITTED_FLAG;

    private String[] parts;
    /**
	 * 标记用户是否拥有此权限
	 */
	private boolean permitted;

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
        if (wildcardString.endsWith(noPermittedFlag)) {
        	wildcardString = wildcardString.substring(0, wildcardString.length() - noPermittedFlag.length());
        	permitted = false;
		} else {
			permitted = true;
		}

        this.parts = wildcardString.split(dividerToken);

        if (this.parts.length == 0) {
            throw new IllegalArgumentException("Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
        }
    }
    
	@Override
	public boolean isPermitted() {
		return permitted;
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
        return StringUtils.join(parts, dividerToken) + (isPermitted() ? Symbol.EMPTY : noPermittedFlag);
    }

    public boolean equals(Object o) {
        if (o instanceof UrlWildcardPermission) {
        	UrlWildcardPermission wp = (UrlWildcardPermission) o;
            return new EqualsBuilder()
                	.append(parts, wp.parts)
                	.append(isPermitted(), wp.isPermitted())
                	.isEquals();
        }
        return false;
    }

    public int hashCode() {
        return new HashCodeBuilder()
    			.append(parts)
    			.append(isPermitted())
    			.hashCode();
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

	public String getNoPermittedFlag() {
		return noPermittedFlag;
	}

	public void setNoPermittedFlag(String noPermittedFlag) {
		this.noPermittedFlag = noPermittedFlag;
	}

}
