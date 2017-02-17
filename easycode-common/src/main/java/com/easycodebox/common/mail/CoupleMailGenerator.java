package com.easycodebox.common.mail;

/**
 * 生产CoupleMail数据
 * @author WangXiaoJin
 */
public interface CoupleMailGenerator<T> {

    /**
     * 判断数据源是否改变过。适用于此场景：在调用{@link #generate}之前验证下数据源是否改变，如果数据源没有改变，
     * 则没有必要去执行{@link #generate}函数
     * @return
     */
    boolean isModified() throws GenerateCoupleMailException;

    /**
     * 生成CoupleMail数据
     * @return
     * @throws GenerateCoupleMailException
     */
    T generate() throws GenerateCoupleMailException;

}
