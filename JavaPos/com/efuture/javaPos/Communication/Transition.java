package com.efuture.javaPos.Communication;

import java.lang.reflect.Field;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;


public class Transition
{
    public Transition()
    {
    }

    public static void main(String[] args)
    {
        new Transition();
    }

    //批量将已知数值塞入对象中去
    public static boolean ConvertToObject(Vector vec, String className,
                                          Vector arg)
    {
        Class cl = null;

        try
        {
            cl = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            PosLog.getLog("Tansition").error(e);
            return false;
        }

        for (int i = 0; i < arg.size(); i++)
        {
            try
            {
                Object o = cl.newInstance();

                if (!ConvertToObject(o, (String[]) arg.elementAt(i)))
                {
                    return false;
                }
                else
                {
                    vec.add(o);
                }
            }
            catch (InstantiationException e)
            {
            	e.printStackTrace();
            	PosLog.getLog("Tansition").error(e);
                return false;
            }
            catch (IllegalAccessException e)
            {
            	e.printStackTrace();
            	PosLog.getLog("Tansition").error(e);
                return false;
            }
        }

        return true;
    }

    public static boolean ConvertToObject(Object obj, String[] args)
    {
    	return ConvertToObject(obj, args, "ref");
    }
    
    public static boolean ConvertToObject(Object obj, String[] args,String name)
    {
        String[] ref = null;
        
        try
		{
			ref = (String[]) obj.getClass().getDeclaredField(name).get(obj);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			PosLog.getLog("Tansition").error(e);
			return false;
		}
		
        return ConvertToObject(obj,args,ref);
    }
    
    public static boolean ConvertToObject(Object obj, String[] args,String[] ref)
    {
        Field field1 = null;
        int i = 0;
        
        Class classInst = obj.getClass();

        boolean bRet = false;
        
        try
        {
/*        	
            if (ref.length != args.length)
            {
                return false;
            }
*/
            for (i = 0; i < ref.length; i++)
            {
                if (ref[i].length() <= 0) continue;
                if (args.length <= i) continue;

                field1 = classInst.getDeclaredField(ref[i]);

                if (field1.getType().getName().equals("java.lang.String"))
                {
                    field1.set(obj, CommonMethod.isNull(args[i],"").trim());
                }
                else if (field1.getType().getName().equals("char"))
                {
                    field1.setChar(obj,CommonMethod.isNull(args[i], " ").charAt(0));
                }
                else if (field1.getType().getName().equals("int"))
                {
                    field1.setInt(obj,Integer.parseInt(CommonMethod.isNull(args[i].trim(),"0")));
                }
                else if (field1.getType().getName().equals("double"))
                {
                    field1.setDouble(obj,Double.parseDouble(CommonMethod.isNull(args[i].trim(),"0")));
                }
                else if (field1.getType().getName().equals("float"))
                {
                    field1.setFloat(obj,Float.parseFloat(CommonMethod.isNull(args[i].trim(),"0")));
                }
                else if (field1.getType().getName().equals("long"))
                {
                    field1.setLong(obj,Long.parseLong(CommonMethod.isNull(args[i].trim(),"0")));
                }
            }
            
            bRet = true;
            return true;
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
            PosLog.getLog("Tansition").error(e);
            return false;
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
            PosLog.getLog("Tansition").error(e);
            return false;
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            PosLog.getLog("Tansition").error(e);
            return false;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            PosLog.getLog("Tansition").error(e);
            return false;
        }
        finally
        {
        	if (!bRet)
        	{
        		new MessageBox(Language.apply("转换{0}数据对象\n的{1}成员时发生错误!", new Object[]{classInst.getName(),ref[i]}));
//        		new MessageBox("转换 "+classInst.getName()+" 数据对象\n的 "+ref[i]+" 成员时发生错误!");
        	}
        }
    }

    //在同一表中含有多个信息
    public static String ConvertToXMLWithHead(Vector v)
    {
        String line = ConvertToXML(v);
        line = getHeadXML(line);

        return line;
    }

    //在同一表中含有多个信息
    public static String ConvertToXML(Vector v)
    {
        try
        {
            Object arg = (Object) v.elementAt(0);
            String line = new String();

            for (int i = 0; i < v.size(); i++)
            {
                arg = (Object) v.elementAt(i);
                line += ItemDetail(arg,
                                   (String[]) arg.getClass()
                                                 .getDeclaredField("ref")
                                                 .get(arg));
            }

            line = closeTable(line, arg.getClass().getName(), 1);

            return line;
        }
        catch (Exception er)
        {
            return null;
        }
    }

    public static String buildEmptyXML()
    {
        String line = closeTable("*", "null", 1);
        line = getHeadXML(line);

        return line;
    }

    //	在表中含有单个信息
    public static String ConvertToXML(final Object arg)
    {
    	return ConvertToXML(arg, null);
    }
    
    public static String ConvertToXML(final Object arg, String[][] appendArg)
    {
        try
        {
            String line = ItemDetail(arg,
                                     (String[]) arg.getClass()
                                                   .getDeclaredField("ref")
                                                   .get(arg),
                                                   appendArg);
            line = closeTable(line, arg.getClass().getName(), 1);
            line = getHeadXML(line);

            return line;
        }
        catch (final IllegalArgumentException e)
        {
        	e.printStackTrace();
        	PosLog.getLog("Tansition").error(e);
            return null;
        }
        catch (final SecurityException e)
        {
        	e.printStackTrace();
        	PosLog.getLog("Tansition").error(e);
            return null;
        }
        catch (final IllegalAccessException e)
        {
        	e.printStackTrace();
        	PosLog.getLog("Tansition").error(e);
            return null;
        }
        catch (final NoSuchFieldException e)
        {
        	e.printStackTrace();
        	PosLog.getLog("Tansition").error(e);
            return null;
        }
    }

    //加入XML文件开头
    public static String getHeadXML(String line)
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>" + ManipulateStr.getDelCharInStr(line.replace('&', '_'),(char)0x1B,2)+
               "</root>";
    }

    //为同一表结构，增加表头信息
    public static String closeTable(String line, String tableName, int itemCount)
    {
        return "<table  Name=\"" + tableName + "\"   itemCount=\" " +
               itemCount + "\" >" + line + "</table>";
    }

    public static String SimpleXML(String[] values, String[] arg)
    {
        String line = ItemDetail(values, arg);
        line = closeTable(line, "null", 1);

        return getHeadXML(line);
    }

    public static String ItemDetail(String[] values, String[] arg)
    {
        if (values.length != arg.length)
        {
            return null;
        }

        StringBuffer sbXML = new StringBuffer();
        sbXML.append("<row>");

        for (int i = 0; i < arg.length; i++)
        {
            try
            {
                sbXML.append("<" + arg[i] + ">");
                if (values[i] == null) sbXML.append("");
                else sbXML.append(values[i]);
                sbXML.append("</" + arg[i] + ">");
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            	PosLog.getLog("Tansition").error(e);
                return null;
            }
        }

        sbXML.append("</row>");

        return sbXML.toString();
    }

    //转换单行的信息
    public static String ItemDetail(Object o, String[] arg)
    {
    	return ItemDetail(o, arg, null);
    }
    
    public static String ItemDetail(Object o, String[] arg, String[][] appendArg)
    {
        StringBuffer sbXML = new StringBuffer();
        Field field1 = null;
        sbXML.append("<row>");

        Class classInst = o.getClass();

        //Method[] methodInst=classInst.getDeclaredMethods();
        for (int i = 0; i < arg.length; i++)
        {
            try
            {
                //Object obj=new Object();
                field1 = classInst.getDeclaredField(arg[i]);
                sbXML.append("<" + arg[i] + ">");

                if (field1.get(o) != null && field1.get(o).toString().trim().length()>0)
                {
                    sbXML.append(field1.get(o).toString().trim());
                }
                else
                {
                    sbXML.append(" ");
                }

                sbXML.append("</" + arg[i] + ">");
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            	PosLog.getLog("Tansition").error(e);
                continue;
            }
        }
        
        // 添加附加参数
        if (appendArg != null)
        {
        	String[] a = null;
        	for (int j = 0; j< appendArg.length; j++)
        	{
        		a = appendArg[j];
        		sbXML.append("<" + a[0] + ">");
        		sbXML.append(a[1]);
        		sbXML.append("</" + a[0] + ">");
        	}
        }

        sbXML.append("</row>");

        return sbXML.toString();
    }
}
