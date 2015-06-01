package com.efuture.javaPos.Struct;

import java.io.Serializable;

/**
 * 
 * @author wy
 *
 */
public class ParaNodeDef implements Serializable
{

	private static final long serialVersionUID = 0L;
    public static String[] ref = { "code", "name", "value", "memo" };
    
    public String code;
    public String name;
    public String value;
    public String memo;
    
}
