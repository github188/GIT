package com.efuture.javaPos.Struct;


//保存快捷键
public class ShortcutKeyDef
{
	private int shortcutKey = 0;
	private String keyString = null;
	
	public void setShortcutKey(int shortcutKey)
	{
		this.shortcutKey = shortcutKey;
	}
	
	public int getShortcutKey()
	{
		return shortcutKey;
	}
	
	public void setKeyString(String keyString)
	{
		this.keyString = keyString;
	}
	
	public String getKeyString()
	{
		return keyString;
	}
}
