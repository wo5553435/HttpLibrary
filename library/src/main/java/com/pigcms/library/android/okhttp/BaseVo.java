package com.pigcms.library.android.okhttp;

import java.io.Serializable;

/**
 * Created by win7 on 2017-04-17.
 */

public class BaseVo implements Serializable {
    private static final long serialVersionUID = -2579386368620862298L;

    public  Object getUnstablekey(Object key){//这个方法是在你不确定这个属性类或者属性到底是不是预想值的情况下使用
        if(key instanceof String) return null;
        return "";
    }
}