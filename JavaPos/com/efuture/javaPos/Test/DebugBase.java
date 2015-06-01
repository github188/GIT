package com.efuture.javaPos.Test;

import java.lang.reflect.Field;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;

public class DebugBase 
{
	public void Debug(Vector vccontent,Vector vcObject)
	{
		Debug(this,vccontent,vcObject);
	}
	
	public void Debug(Object obj,Vector vccontent,Vector vcObject)
	{
		try
		{	
			if (obj == null) return;
			
			 if (obj != this && obj instanceof DebugBase)
			 {
				 ((DebugBase)obj).Debug(vccontent,vcObject);
				 
				 return;
			 }
			 
			if (obj.getClass().toString().toLowerCase().indexOf("java.lang.string") >= 0 ||
				obj.getClass().toString().toLowerCase().indexOf("double") >= 0 || 
				obj.getClass().toString().toLowerCase().indexOf("float") >= 0 || 
				obj.getClass().toString().toLowerCase().indexOf("int") >= 0 || 
				obj.getClass().toString().toLowerCase().indexOf("long") >= 0 || 
				obj.getClass().toString().toLowerCase().indexOf("char") >= 0
				)
			{
				return;
			}
			
			Vector vcfields = new Vector();
			
			Class c1 = obj.getClass();

			while(true)
			{
				if (c1.getName().toString().toLowerCase().indexOf("object")>=0)
				{
					break;
				}
				
				Field[] fields = c1.getDeclaredFields();
				
				for (int i = 0;i < fields.length;i++)
				{
					vcfields.add(fields[i]);
				}
				
				c1 = c1.getSuperclass();
			}
			
			for (int i = 0;i < vcfields.size();i++)
			{
				Field field = (Field)vcfields.get(i);
				
				String filedname = field.getName().toString();
				String fieldtype = field.getType().toString();
				String fieldvalue = "";
				
				try
				{
					vcObject.add(field.get(obj));
					
					Object obj3 = field.get(obj);
					fieldvalue = obj3 == null?"":obj3.toString();
				}
				catch(Exception ex)
				{
					vcObject.add(null);
					
					fieldvalue = "";
				}
				
				String[] str = new String[]{filedname,fieldvalue,fieldtype};
				vccontent.add(str);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(ex.getMessage());
		}
	}
	
	/*
	protected String ddd = "asdf";
	

	public void Debug()
	{
		Debug(this);
	}
	
	public void Debug(Object obj)
	{
		try
		{	
			if (obj == null) return;
			
			if (obj.getClass().toString().toLowerCase().indexOf("java.lang.string") >= 0 ||
				obj.getClass().toString().toLowerCase().indexOf("double") >= 0 || 
				obj.getClass().toString().toLowerCase().indexOf("float") >= 0 || 
				obj.getClass().toString().toLowerCase().indexOf("int") >= 0 || 
				obj.getClass().toString().toLowerCase().indexOf("long") >= 0 || 
				obj.getClass().toString().toLowerCase().indexOf("char") >= 0
				)
			{
				//new TextShowForm().open(obj.toString());
				return;
			}
			
			Vector vcfields = new Vector();
			
			Class c1 = obj.getClass();

			while(true)
			{
				if (c1.getName().toString().toLowerCase().indexOf("object")>=0)
				{
					break;
				}
				
				Field[] fields = c1.getDeclaredFields();
				
				for (int i = 0;i < fields.length;i++)
				{
					vcfields.add(fields[i]);
				}
				
				c1 = c1.getSuperclass();
			}
			
			Vector v1 = new Vector();
			Vector v = new Vector();
			
			for (int i = 0;i < vcfields.size();i++)
			{
				Field field = (Field)vcfields.get(i);
				
				String filedname = field.getName().toString();
				String fieldtype = field.getType().toString();
				String fieldvalue = "";
				
				try
				{
					v1.add(field.get(obj));
					
					Object obj3 = field.get(obj);
					fieldvalue = obj3 == null?"":obj3.toString();
				}
				catch(Exception ex)
				{
					v1.add(null);
					
					fieldvalue = "";
				}
				
				String[] str = new String[]{filedname,fieldvalue,fieldtype};
				v.add(str);
			}
	
			int choice = 0;
			
			while(true)
			{
		        //String[] title = { "名称", "值","类型"};
		        //int[] width = { 100, 400 ,400};
		        //choice = new MutiSelectForm().open("监控"+obj.toString(), title, width, v);
		        
				TextShowForm tsf = new TextShowForm();
				choice = tsf.open(v);
				
		        if (choice < 0) break;
		        
		        Object obj2 = v1.get(choice);
		        
		        if (obj2 instanceof DebugBase)
		        {
		        	((DebugBase)obj2).Debug();
		        }
		        else
		        {
		        	Debug(v1.get(choice));
		        }
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(ex.getMessage());
		}
	}
	*/
}
