package com.efuture.javaPos.Struct;

import java.io.Serializable;
import java.util.Hashtable;


public class SaleCustDef implements Cloneable, Serializable
{

	private static final long serialVersionUID = 1L;
	
	private Hashtable saleCust = null;
	
	public SaleCustDef()
	{
		saleCust = new Hashtable();
	}
	
	public Hashtable getHashtable()
	{
		return saleCust;
	}
	
	public ParaNodeDef custItem(Object key)
	{
		try
		{
			if (key != null && saleCust != null && saleCust.containsKey(key))
			{
				return (ParaNodeDef)saleCust.get(key);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return new ParaNodeDef();
	}
	
	public Object custGetItemValue(Object key)
	{
		try
		{
			if (key != null && saleCust != null && saleCust.containsKey(key))
			{
				ParaNodeDef node = (ParaNodeDef)saleCust.get(key);
				if (node!=null) return node.value;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	
	public void custAdd(Object key, Object value)
	{
		if (key == null) return;
		
		if (saleCust == null) saleCust = new Hashtable();
		
		if (saleCust.containsKey(key))
		{
			saleCust.remove(key);
		}
		saleCust.put(key, value);
	}
	
	/**
	 * 
	 * @param key
	 * @param value 为ParaNodeDef对象
	 */
	public void custAddNode(Object key, Object value)
	{
		if (key == null) return;
		
		ParaNodeDef node = new ParaNodeDef();
		node.code = String.valueOf(key);
		node.value = String.valueOf(value);
		
		custAdd(node.code, node);
	}
	
	public void custRemove(Object key)
	{
		if (key == null) return;
		
		if (saleCust == null) return;
		
		if (saleCust.containsKey(key))
		{
			saleCust.remove(key);
		}		
	}
	
	public void custClear()
	{
		if (saleCust == null) return;
		
		saleCust.clear();
	}
	
	public int custCount()
	{
		if (saleCust == null) return 0;
		
		return saleCust.size();		
	}
	
	public boolean containsKey(Object key)
	{
		try
		{
			if (key != null && saleCust != null && saleCust.containsKey(key))
			{
				return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
	
	public Object clone()
	{
		try 
		{
			return super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			e.printStackTrace();
			return this;
		}
	}
}
