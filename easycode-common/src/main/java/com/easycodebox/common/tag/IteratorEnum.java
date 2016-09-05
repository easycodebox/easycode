package com.easycodebox.common.tag;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;


/**
 * @author WangXiaoJin
 * 
 */
public class IteratorEnum extends EnumGroupTag {
	
	private static final long serialVersionUID = 3447660627148511161L;

    protected Object oldStatus;
    protected IteratorStatus status;
    protected IteratorStatus.StatusState statusState;
    
    protected String statusAttr;
    protected Integer step;
    protected String var;
    
    private int currentIndex;
    private List<Enum<?>> enumsList;
	
	@Override
	protected void init() {
		oldStatus = status = null;
		statusState = null;
		statusAttr = var = null;
		currentIndex = 0;
		step = 1;
		enumsList = null;
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		if (statusAttr != null) {
            statusState = new IteratorStatus.StatusState();
            status = new IteratorStatus(statusState);
        }
		
		enumsList = getEnumList();
		
		int length = enumsList == null ? 0 : enumsList.size();
        if(end == null) 
    		this.end = (length == 0 || step < 1) ? -1 : length - 1;
        else 
        	this.end = end > length - 1 ? length - 1 : end;
    	currentIndex = begin - step;
        if (enumsList != null && hasNext()) {
            Object currentValue = next();

            if (var != null && currentValue != null) {
            	pageContext.setAttribute(var, currentValue);
            }

            if (statusAttr != null) {
                statusState.setLast(!hasNext());
                oldStatus = pageContext.getAttribute(statusAttr);
                pageContext.setAttribute(statusAttr, status);
            }

            return EVAL_BODY_INCLUDE;
        } else 
        	return SKIP_BODY;
	}

    @Override
	public int doEndTag() throws JspException {
    	if (status != null) {
            if (oldStatus == null) {
            	pageContext.removeAttribute(statusAttr);
            } else {
            	pageContext.setAttribute(statusAttr, oldStatus);
            }
        }
    	if(var != null)
    		pageContext.removeAttribute(var);
		return super.doEndTag();
	}

    @Override
	public int doAfterBody() throws JspException {
		if (enumsList != null && hasNext()) {
            Object currentValue = next();
            if (var != null && currentValue != null) {
            	pageContext.setAttribute(var, currentValue);
            }

            if (status != null) {
                statusState.next();
                statusState.setLast(!hasNext());
            }
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }
    }

    private boolean hasNext(){
    	if(currentIndex + step <= end)
    		return true;
    	else
    		return false;
    }
    
    private Object next(){
    	return enumsList.get(currentIndex = currentIndex + step);
    }
    
    
    public static class IteratorStatus {
        protected StatusState state;

        public IteratorStatus(StatusState aState) {
            state = aState;
        }

        public int getCount() {
            return state.index + 1;
        }

        public boolean isEven() {
            return ((state.index + 1) % 2) == 0;
        }

        public boolean isFirst() {
            return state.index == 0;
        }

        public int getIndex() {
            return state.index;
        }

        public boolean isLast() {
            return state.last;
        }

        public boolean isOdd() {
            return ((state.index + 1) % 2) != 0;
        }

        public int modulus(int operand) {
            return (state.index + 1) % operand;
        }

        public static class StatusState {
            boolean last = false;
            int index = 0;

            public void setLast(boolean isLast) {
                last = isLast;
            }

            public void next() {
                index++;
            }
        }
    }
    
    
    public void setStatus(String status) {
        this.statusAttr = StringUtils.isBlank(status) ? null : status;
    }

    public void setStep(String step) {
        this.step = StringUtils.isBlank(step) ? 1 : obtainVal(step, Integer.class);
    }
    
    public void setVar(String var) {
    	this.var = obtainVal(var, String.class);
    }
    
}
