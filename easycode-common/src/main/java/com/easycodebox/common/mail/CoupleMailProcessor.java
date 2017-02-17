package com.easycodebox.common.mail;

/**
 * CoupleMail处理器
 * @author WangXiaoJin
 */
public interface CoupleMailProcessor {

    /**
     * 处理CoupleMail相关的逻辑，返回处理后的CoupleMail
     * @return
     * @throws Exception
     */
    CoupleMail process() throws Exception;

}
