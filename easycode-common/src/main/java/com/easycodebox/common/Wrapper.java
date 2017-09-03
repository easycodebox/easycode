package com.easycodebox.common;

/**
 * @author WangXiaoJin
 */
public abstract class Wrapper<T> {
    
    protected T object;
    
    public Wrapper(T object) {
        this.object = object;
    }
    
    public T getObject() {
        return object;
    }
}
