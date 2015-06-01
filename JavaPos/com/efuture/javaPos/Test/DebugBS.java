package com.efuture.javaPos.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;

public class DebugBS
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
			
			 if (obj != this && obj instanceof DebugBS)
			 {
				 ((DebugBase)obj).Debug(vccontent,vcObject);
				 
				 return;
			 }
			 
			 if (obj instanceof List)
			 {
				 List objs = (List)obj;
				 
				 for (int i = 0;i < objs.size() ;i++)
				 {
					 String filedname = String.valueOf(i);
					 String fieldtype = objs.get(i).getClass().getName();
					 String fieldvalue = objs.get(i).toString();
						
					 String[] str = new String[]{filedname,fieldvalue,fieldtype};
					 vccontent.add(str);
					 vcObject.add(objs.get(i));
				 }
				 
				 return;
			 }
			 // 判断是否为数组
			 else if (obj.getClass().toString().indexOf("class [L") >= 0)
			 {
				 Object[] objs = (Object[])obj;
				 for (int i = 0;i < objs.length ;i++)
				 {
					 String filedname = String.valueOf(i);
					 String fieldtype = objs[i].getClass().getName();
					 String fieldvalue = objs[i].toString();
						
					 String[] str = new String[]{filedname,fieldvalue,fieldtype};
					 vccontent.add(str);
					 vcObject.add(objs[i]);
				 }
				 
				 return;
			}
			else
			{
				 if (obj.getClass().toString().toLowerCase().indexOf("class java.lang.string") >= 0||
							obj.getClass().toString().toLowerCase().indexOf("class java.lang.double") >= 0 || 
							obj.getClass().toString().toLowerCase().indexOf("class java.lang.float") >= 0 || 
							obj.getClass().toString().toLowerCase().indexOf("class java.lang.int") >= 0 || 
							obj.getClass().toString().toLowerCase().indexOf("class java.lang.long") >= 0 || 
							obj.getClass().toString().toLowerCase().indexOf("class java.lang.char") >= 0 ||
							obj.getClass().toString().toLowerCase().indexOf("class java.lang.boolean") >= 0
							)
						{
							return;
						}
			}
			 /*
			if (obj.getClass().toString().toLowerCase().indexOf("java.util.vector") >= 0)
			{
				 Vector objs = (Vector)obj;
				 for (int i = 0;i < objs.size() ;i++)
				 {
					 String filedname = String.valueOf(i);
					 String fieldtype = objs.get(i).getClass().getName();
					 String fieldvalue = objs.get(i).toString();
						
					 String[] str = new String[]{filedname,fieldvalue,fieldtype};
					 vccontent.add(str);
					 vcObject.add(objs.get(i));
				 }
				 
				 return;
			}
			*/
			 
			Vector vcfields = new Vector();
			
			Class c1 = obj.getClass();

			while(true)
			{
				if (c1.toString().toLowerCase().indexOf("class java.lang.object")>=0)
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
}
