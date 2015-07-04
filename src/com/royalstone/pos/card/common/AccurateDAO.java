package com.royalstone.pos.card.common;

import com.royalstone.pos.common.Accurate;

/**
 * Created by IntelliJ IDEA.
 * User: yaopoqing
 * Date: 2005-5-27
 * Time: 12:41:05
 * To change this template use Options | File Templates.
 */
public interface AccurateDAO {
    public Accurate getAccurate(int cardLevel,int deptID)throws Exception;
}
