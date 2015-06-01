package com.efuture.javaPos.Communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.Ftp;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;


public class UpdateBaseInfo
{
	static boolean alreadyreadinfo = false;
	static long olddownnum = -1;
	static String[][] updatetableinfo = new String[][]
	{
		{ "CUSTOMER","会员主档资料","0","CustomerDef" },
		{ "CUSTOMERTYPE","会员类别资料","0","CustomerTypeDef" },
		{ "CUSTOMERZKL","会员折扣资料","0","CustomerVipZklDef" },
		{ "GOODS","商品主档资料","0","GoodsDef" },
		{ "GOODSAMOUNT","商品批量资料","0","GoodsAmountDef" },
		{ "GOODSPOP","商品优惠资料","0","GoodsPopDef" },
		{ "GOODSUNITS","商品单位资料","0","GoodsUnitsDef" },
		{ "GOODSBARCODE","商品条码资料","0","GoodsBarcodeDef" },
		{ "OPERUSER","人员主档资料","0","OperUserDef" },
		{ "CMPOPTITLE","促销档期资料","0","CmPopTitleDef" },
		{ "CMPOPRULE","促销规则资料","0","CmPopRuleDef" },
		{ "CMPOPLADER","促销阶梯资料","0","CmPopRuleLadderDef" },
		{ "CMPOPMKTLIST","促销门店资料","0","CmPopMktListDef" },
		{ "CMPOPGOODS","促销商品范围","0","CmPopGoodsDef" },
		{ "CMPOPGIFTS","促销赠品范围","0","CmPopGiftsDef" },
		{ "PAYRULE","付款方式规则","0","PayRuleDef" },
		{ "GOODSFRAME","商品例外表","0","GoodsFrameDef" }
	};
	final static int idxtablename = 0;
	final static int idxtableinfo = 1;
	final static int idxtableseq  = 2;
	final static int idxtableclass  = 3;
	
	public static boolean CurrUsing = false;
	public static String CurrUsingMsg = "";
    private static boolean runupdate = false;
	private static String CurrDate = new ManipulateDateTime().getDateBySign();
	
    final int add = 1;
    final int update = 2;
    final int del = 3;
    final int remove = 4;
    
    int curTableIndex = -1;
    String curTableName = null;
    DocumentBuilderFactory dbf = null;
    DocumentBuilder db = null;
    Document doc = null;
	
    public UpdateBaseInfo(String name)
    {
        try
        {
            BufferedReader br = null;

            if (ConfigClass.ServerOS.equalsIgnoreCase("Windows"))
            {
            	br = CommonMethod.readFileGBK(name);
            }
            else
            {
            	br = CommonMethod.readFile(name);
            }
            
            String line = null;
            String line1 = "";

            try
            {
                while ((line = br.readLine()) != null)
                {
                    line1 += line.replace('\'', ' ');
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                PosLog.getLog(getClass()).error(e);
            }
            finally
            {
                if (br != null)
                {
                    br.close();
                }
            }

            //System.out.println(name);
            dbf = DocumentBuilderFactory.newInstance();
            db  = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(line1)));
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
            PosLog.getLog(getClass()).error(e);
        }
        catch (SAXException e)
        {
            e.printStackTrace();
            PosLog.getLog(getClass()).error(e);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            PosLog.getLog(getClass()).error(e);
        }

        // 确定表名及数组索引
        curTableIndex = parseName(name);
    }

    public static void main(String[] arg)
    {
        UpdateBaseInfo info = new UpdateBaseInfo("../goods_0001.xml");
        Vector v = info.parseMethod();

        if (v != null)
        {
            for (int i = 0; i < v.size(); i++)
            {
                System.out.println((String) v.elementAt(i));
            }
        }
    }

    //  下载及时Base地数据库更新文件
    public static void downloadBaseInfo(boolean isthread)
    {
    	try
    	{
    		if (runupdate) return;
        	if (ConfigClass.FtpIP == null || ConfigClass.FtpIP.trim().length() <= 0) return;
    		runupdate = true;
    		
    		// 检查是否需要按日期进行下载更新
    		int dayidx = 0;
			String[] daylist = null;
			Vector dayvec = null;
			int dayvecidx = -1;
    		String dayuptfile = ConfigClass.LocalDBPath+"\\BaseDateSet.ini";
    		if (PathFile.fileExist(dayuptfile))
    		{
    			dayvec = CommonMethod.readFileByVector(dayuptfile);
				for (int i=0;dayvec != null &&i<dayvec.size();i++)
				{
					String[] s = (String[])dayvec.elementAt(i);
					if (s.length > 1 && s[0].equalsIgnoreCase("DAYLIST") && s[1] != null && s[1].trim().length() > 0)
					{
						dayvecidx = i;
						daylist = s[1].split(",");
						if (daylist.length <= 0) daylist = null;
						break;
					}
				}
    		}
    		
    		while(true)
    		{
    			if (daylist != null)
    			{
	            	if (dayidx >= daylist.length)
	            	{
	            		CurrDate = new ManipulateDateTime().getDateBySign();
	            		GlobalInfo.sysPara.num_down = olddownnum;
	            		break;
	            	}
	            	
	            	// 下载这天全部的更新XML
    				if (olddownnum < 0) olddownnum = GlobalInfo.sysPara.num_down;
    				GlobalInfo.sysPara.num_down = 0;		
    				CurrDate = daylist[dayidx].replaceAll("/","-");
    				 
    				// 检查日期是否有效
    				if (!ManipulateDateTime.checkDate(CurrDate))
    				{
    	            	dayidx++;
    	            	continue;
    				}
    			}
    				
	    		// 读取已下载序号
	    		readUpdateSeqno();
	    	
	    		boolean issuc = true;
	    		
	            //下载文件
	    		issuc = downloadFtp(isthread);
	            
	            //解析文件
	            if(!parseFiles(isthread)) issuc = false;
	            
	            // 没有日期列表只执行一次
	            if (daylist != null && issuc)
	            {
	            	// 已下载完一天
	            	StringBuffer sb = new StringBuffer();
					for (int i=dayidx+1;i<daylist.length;i++) sb.append(daylist[i]+",");
	            	((String[])dayvec.elementAt(dayvecidx))[1] = sb.toString(); 
	            	CommonMethod.writeFileByVector(dayuptfile, dayvec);
	            	
	            	// 标记日终库的时间
	            	PrintWriter pw = CommonMethod.writeFileUTF(ConfigClass.LocalDBPath + "BaseDate.ini");
	            	pw.println(CurrDate + " " + ManipulateDateTime.getCurrentTime());
	            	pw.close();
	            	
	            	// 继续下载下一天
	            	dayidx++;
	            }
	            else break;
    		}
        }
        catch (Exception er)
        {
            er.printStackTrace();
            PosLog.getLog("UpdateBaseInfo").error(er);
        }
        finally
        {
        	runupdate = false;
        }
    }

    public static boolean downloadFtp(boolean isthread)
    {
        //当天日期
        String date = CurrDate.replaceAll("-","");
        Ftp ftp = null;

        try
        {
        	GlobalInfo.statusBar.asyncSetHelpMessage(Language.apply("正在连接FTP服务器..."),isthread);
        	
        	ftp = new Ftp();
        	
            // 连接FTP
            if (!ftp.connect(ConfigClass.FtpIP, ConfigClass.FtpPort,ConfigClass.FtpUser, ConfigClass.FtpPwd))
            {
                return false;
            }
        	
        	// 定义了门店号且没明确指定下载路径，则缺省分门店目录进行下载
	    	String mkt = "";
        	if (ConfigClass.Market != null && ConfigClass.Market.trim().length() > 0 && ConfigClass.FtpPath.trim().length() <= 0)
        	{
        		mkt = "/" + ConfigClass.Market + "/";
        	}
        	
	        //修改ftp路径  是否需要 FTPPath + date 为目录？
	        if (!ftp.changeWorkPath(ConfigClass.FtpPath + mkt + CurrDate))
	        {
	            return false;
	        }
	
	        //用于保存需下载的文件
	        int startxmlindex = 0;
	        Vector v = new Vector();
	        String fileName = null;
	
	        GlobalInfo.statusBar.asyncSetHelpMessage(Language.apply("正在检查{0}增量数据...", new Object[]{CurrDate}),isthread);
	        
	        // 循环检查更新文件
	        while (true)
	        {
	            if (GlobalInfo.sysPara.num_down > 0 && v.size() >= GlobalInfo.sysPara.num_down)
	            {
	                break;
	            }
	
	            // 按序号检查是否有更新文件
	            int idxseq = Convert.toInt(updatetableinfo[startxmlindex][idxtableseq]);
	            fileName = updatetableinfo[startxmlindex][idxtablename] + "_" + Convert.increaseInt(idxseq + 1, 4) + ".xml";
	            fileName = fileName.toLowerCase();
                
	        	GlobalInfo.statusBar.asyncSetHelpMessage(Language.apply("正在下载{0}增量文件 ", new Object[]{CurrDate}) + fileName,isthread);
	        	
	            if (ftp.exist(fileName) && ftp.getFile(fileName, "BaseDD_" + fileName,
	                        ConfigClass.LocalDBPath + "Invoice//" + date, 
	                        "."))
	            {
	            	v.add(fileName);
	            	
                    idxseq++;
                    updatetableinfo[startxmlindex][idxtableseq] = String.valueOf(idxseq);
                    
                    continue;
	            }
	            
	            // 切换下载另一基础资料表
                startxmlindex++;
	            if (startxmlindex >= updatetableinfo.length) break;
	        }
	        
	        // 标记更新文件已下载的序号
	        PrintWriter pw = null;
	        try
	        {
	            pw = CommonMethod.writeFile(ConfigClass.LocalDBPath + "Invoice//" +
	                                        date +
	                                        "//BaseUpdateInfo.ini");
	
	            if (pw != null)
	            {
	            	pw.println("");
	            	for (int i=0;i<updatetableinfo.length;i++)
	            	{
	            		pw.println(updatetableinfo[i][idxtablename] + "_" + updatetableinfo[i][idxtableseq]);
	            	}
	            }
	        }
	        catch (Exception er)
	        {
	            er.printStackTrace();
	            
	            return false;
	        }
	        finally
	        {
	            pw.close();
	        }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	
        	return false;
        }
        finally
        {
        	if (ftp != null) ftp.close();
        	
        	GlobalInfo.statusBar.asyncSetHelpMessage(CurrDate + Language.apply("增量数据下载完成"),isthread);
        }
        
        return true;
    }

    public static void waitUpdateBase()
    {
    	if (CurrUsing)
    	{
    		ProgressBox pb = null;
    		int n = 0;
    		
    		try
    		{
    			pb = new ProgressBox();
    			
		    	while (CurrUsing)
		    	{
		    		try
		    		{
		    			pb.setText(CurrUsingMsg);
		    			while (Display.getCurrent().readAndDispatch());
		    			
		    			Thread.sleep(3000);
					}
		    		catch (Exception e) 
		    		{
						e.printStackTrace();
					}
		    		finally
		    		{
		    			n++;
		    			if (n >= 10)
		    			{
		    				if (new MessageBox(Language.apply("更新动作已经执行了30秒还未完成，不继续等待吗？\n\n 1-是 / 2-否")).verify() == GlobalVar.Key1)
		    				{
		    					CurrUsing = false;
		    					break;
		    				}
		    				else
		    				{
		    					n = 0;
		    				}
		    			}
		    		}
		    	}
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
    		finally
    		{
    			if (pb != null) pb.close();
    		}
    	}
    }
    
    public static boolean parseFiles(boolean isthread)
    {
    	boolean issuc = true;
/*    	采用第二线程方式下载更新，不影响主用户操作交互
        Display.getDefault().syncExec(new Runnable()
            {
                public void run()
                {
*/                
                	// 开始更新数据库
                	CurrUsing = true;
                	
                    try
                    {
                        String date = CurrDate.replaceAll("-", "");

                        File file = new File(ConfigClass.LocalDBPath +
                                             "Invoice//" +
                                             date);

                        if (file.isDirectory())
                        {
                            String[] files = file.list();

                            for (int i = 0; i < files.length; i++)
                            {
                                if (GlobalInfo.sysPara.num_down > 0 && i > GlobalInfo.sysPara.num_down)
                                {
                                    break;
                                }

                                if (files[i].indexOf("BaseDD_") >= 0)
                                {
                                	// 显示状态提示
                                	CurrUsingMsg = Language.apply("正在更新{0}{1}文件,请等待...",new Object[]{CurrDate + " ",files[i]});
                                    GlobalInfo.statusBar.asyncSetHelpMessage(CurrUsingMsg,isthread);
                                	
                                    try
                                    {
                                    	// 构建更新数据库SQL
                                        UpdateBaseInfo info = new UpdateBaseInfo(ConfigClass.LocalDBPath +
                                                                                 "Invoice//" +
                                                                                 date +
                                                                                 "/" +
                                                                                 files[i]);
                                        Vector sql = info.parseMethod();
                                        
                                        // 删除文件
                                        File det = new File(ConfigClass.LocalDBPath +
                                                            "Invoice//" +
                                                            date +
                                                            "//" + files[i]);
                                        det.delete();

                                        // 更新本机数据库
                                        for (int j = 0; j < sql.size(); j++)
                                        {
                                            try
                                            {
                                                GlobalInfo.baseDB.executeSql((String) sql.elementAt(j));
                                            }
                                            catch (Exception er)
                                            {
                                                er.printStackTrace();
                                                
                                                issuc = false;
                                            }
                                        }
                                    }
                                    catch (Exception er)
                                    {
                                        er.printStackTrace();
                                        
                                        issuc = false;
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception er)
                    {
                        er.printStackTrace();
                        
                        issuc = false;
                    }
                    
                    // 更新完成
                	CurrUsing = false;
                	
                    // 恢复状态提示
                	GlobalInfo.statusBar.asyncSetHelpMessage(CurrDate + Language.apply("本地数据库更新完毕"),isthread);
                	
                	return issuc;
/*                	
                }
            });
*/            
    }

    public static void deleteUpdateInfoDate()
    {
		String date = CurrDate.replaceAll("-","");
		
        File file = new File(ConfigClass.LocalDBPath + "Invoice//" + date + "//BaseUpdateInfo.ini");
    	file.delete();
    }
    
    public static void readUpdateSeqno()
    {
    	// 从配置文件中读取需要更新表
    	if (!alreadyreadinfo)
    	{
    		alreadyreadinfo = true;
    		String file = ConfigClass.LocalDBPath + "BaseUpdateTable.ini";
    		if (PathFile.fileExist(file))
    		{
	    		Vector v = CommonMethod.readFileByVector(file);
	    		if (v != null)
	    		{
	    			updatetableinfo = new String[v.size()][4];
		    		for (int i=0;i<v.size();i++)
		    		{
		    			String[] s = (String[])v.elementAt(i);
	    				updatetableinfo[i][idxtablename] = (s.length>0)?s[0].trim():"";
	    				updatetableinfo[i][idxtableinfo] = (s.length>2)?s[2].trim():((s.length>1)?s[1].trim():"");
	    				updatetableinfo[i][idxtableseq]  = "0";
	    				updatetableinfo[i][idxtableclass]= (s.length>1)?s[1].trim():"";
		    		}
	    		}
    		}
    	}
    	
        try
        {
    		String date = CurrDate.replaceAll("-","");
    		
            //读取下载文档信息
            File file = new File(ConfigClass.LocalDBPath + "Invoice//" +
                                 date + "//BaseUpdateInfo.ini");

            //如果文档不存在，全部下载
            if (!file.exists())
            {
            	for (int i=0;i<updatetableinfo.length;i++)
            	{
            		updatetableinfo[i][idxtableseq] = "0";
            	}           	
            }
            else
            {
                BufferedReader br;
                br = CommonMethod.readFile(ConfigClass.LocalDBPath +
                                           "Invoice//" + date +
                                           "//BaseUpdateInfo.ini");

                String name = null;
                
                try
                {
                    while ((name = br.readLine()) != null)
                    {
                        if (name.trim().length() <= 0)
                        {
                            continue;
                        }

                        // 读取序号到内存中
                    	name = name.toLowerCase();
                        for (int i=0;i<updatetableinfo.length;i++)
                        {
                        	if (name.startsWith(updatetableinfo[i][idxtablename].toLowerCase()+"_"))
                        	{
                        		updatetableinfo[i][idxtableseq] = String.valueOf(Convert.toInt(name.split("_")[1]));
                        		break;
                        	}
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if (br != null)
                        {
                            br.close();
                        }
                    }
                    catch (IOException e)
                    {
                        
                        e.printStackTrace();
                    }
                }
            }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
    }
    
    public static boolean updatBaseIsRunning()
    {
    	return runupdate;
    }
    
    public static String[][] displayUpdateInfoSeqno()
    {
    	readUpdateSeqno();
    	return updatetableinfo;
    }
    
    private int parseName(String name)
    {
    	int lastSecIndex = name.lastIndexOf("_", name.lastIndexOf("_") - 1 );//倒数第二个_号索引位置
    	name = name.substring(lastSecIndex + 1,name.lastIndexOf("_"));//当目录名含有_时,此时截取串会有问题 wangyong add by 2013.4.12
    	
    	//name = name.substring(name.indexOf("_") + 1,name.lastIndexOf("_")); bak old
    	
    	
    	for (int i=0;i<updatetableinfo.length;i++)
    	{
    		if (name.equalsIgnoreCase(updatetableinfo[i][idxtablename]))
    		{
    			curTableName = updatetableinfo[i][idxtablename];
    			curTableIndex = i;
    			return i;
    		}
    	}
    	
    	return -1;
    }

    public String XMLtoTableInfo(Element el, int sign)
    {
        Object obj = null;
        Vector v = new Vector();
        String[] args = null;
        String[] cols = null;
        
        if (curTableIndex < 0 || curTableIndex >= updatetableinfo.length) return null;
        else
        {
        	try
        	{
	        	Class cl = null;
	        	String classname = updatetableinfo[curTableIndex][idxtableclass];
	        	if (classname.indexOf(".") >= 0) cl = Class.forName(classname);
	        	else cl = Class.forName("com.efuture.javaPos.Struct."+classname);
	        	obj = cl.newInstance();
	        	
	        	String[] ref = null;
	        	try
	        	{
	        		Field fld = obj.getClass().getDeclaredField("refLocal");
	        		ref = (String[])fld.get(obj);
	        	}
	        	catch(Exception ex)
	        	{
	        		Field fld = obj.getClass().getDeclaredField("ref");
	        		ref = (String[])fld.get(obj);
	        	}
                args = getElement(el,ref,v);
                cols = new String[v.size()];
                v.toArray(cols);
                if (!Transition.ConvertToObject(obj,args,cols)) return null;
        	}
        	catch(Exception ex)
        	{
        		ex.printStackTrace();
        		return null;
        	}
        }

        //
        String line = null;
        if ((sign == remove) && curTableName.equals("goodspop"))
        {
            removeConvert(obj);
        }
        else if ((sign == add) || (sign == update))
        {
        	// 检查本地表结构字段匹配项
        	String[] s = GlobalInfo.baseDB.getTableColumns(curTableName);
        	for (int i=0;i<v.size();i++)
        	{
        		String name = (String) v.elementAt(i);
        		int j;
        		for (j=0;j<s.length;j++)
        		{
        			if (name.equalsIgnoreCase(s[j])) break;
        		}
        		if (j >= s.length)
        		{
        			v.remove(i);		// 去掉表结构不存在的字段
        			i--;
        		}
        	}
  
        	//
            if (exist(obj))
            {
                line = updateConvert(obj, v);
            }
            else
            {
                line = addConvert(obj,v);
            }
        }
        else if (exist(obj))
        {
            line = delConvert(obj);
        }

        return line;
    }

    public boolean exist(Object obj)
    {
        Field field1 = null;
        String condition = "";
        Class classInst = obj.getClass();

        try
        {
            String[] ref = (String[]) obj.getClass().getDeclaredField("key")
                                         .get(obj);

            for (int i = 0; i < ref.length; i++)
            {
                if (ref[i].length() <= 0)
                {
                    continue;
                }

                field1 = classInst.getDeclaredField(ref[i]);

                if (field1.getType().getName().equals("java.lang.String"))
                {
                    condition = condition + " AND " + ref[i] + " = '" +
                                field1.get(obj) + "'";
                }
                else if (field1.getType().getName().equals("char"))
                {
                    condition = condition + " AND " + ref[i] + " = '" +
                                field1.getChar(obj) + "'";
                }
                else if (field1.getType().getName().equals("int"))
                {
                    condition = condition + " AND " + ref[i] + " = " +
                                field1.getInt(obj);
                }
                else if (field1.getType().getName().equals("double"))
                {
                    condition = condition + " AND " + ref[i] + " = " +
                                field1.getDouble(obj);
                }
                else if (field1.getType().getName().equals("float"))
                {
                    condition = condition + " AND " + ref[i] + " = " +
                                field1.getFloat(obj);
                }
                else if (field1.getType().getName().equals("long"))
                {
                    condition = condition + " AND " + ref[i] + " = " +
                                field1.getLong(obj);
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            
            e.printStackTrace();
        }

        boolean exist = false;

        if (condition.length() > 0)
        {
            final String line = "select count(*) from " + curTableName +
                                " where " +
                                condition.substring(condition.indexOf("AND") +
                                                    3);

            Object obj1 = GlobalInfo.baseDB.selectOneData(line);

            if (Integer.parseInt(String.valueOf(obj1)) > 0)
            {
                exist = true;
            }
            else
            {
                exist = false;
            }
        }

        return exist;
    }

    public String delConvert(Object obj)
    {
        Field field1 = null;

        Class classInst = obj.getClass();
        String[] ref;

        String condition = "";

        try
        {
            ref = (String[]) obj.getClass().getDeclaredField("key").get(obj);

            for (int i = 0; i < ref.length; i++)
            {
                if (ref[i].length() <= 0)
                {
                    continue;
                }

                field1 = classInst.getDeclaredField(ref[i]);

                if (field1.getType().getName().equals("java.lang.String"))
                {
                    condition = condition + " AND " + ref[i] + " = '" +
                                field1.get(obj) + "'";
                }
                else if (field1.getType().getName().equals("char"))
                {
                    condition = condition + " AND " + ref[i] + " = '" +
                                field1.getChar(obj) + "'";
                }
                else if (field1.getType().getName().equals("int"))
                {
                    condition = condition + " AND " + ref[i] + " = " +
                                field1.getInt(obj);
                }
                else if (field1.getType().getName().equals("double"))
                {
                    condition = condition + " AND " + ref[i] + " = " +
                                field1.getDouble(obj);
                }
                else if (field1.getType().getName().equals("float"))
                {
                    condition = condition + " AND " + ref[i] + " = " +
                                field1.getFloat(obj);
                }
                else if (field1.getType().getName().equals("long"))
                {
                    condition = condition + " AND " + ref[i] + " = " +
                                field1.getLong(obj);
                }
            }
        }
        catch (Exception e)
        {
            
            e.printStackTrace();

            return null;
        }

        if (condition.length() > 0)
        {
            return "delete from " + curTableName + " where " +
                   condition.substring(condition.indexOf("AND") + 3);
        }

        return null;
    }

    public String removeConvert(Object obj)
    {
        Field field1 = null;

        Class classInst = obj.getClass();
        String ref;

        String condition = "";

        try
        {
            ref    = "djbh";
            field1 = classInst.getDeclaredField(ref);

            if (field1.getType().getName().equals("java.lang.String"))
            {
                if ((field1 != null) &&
                        (((String) field1.get(obj)).trim().length() > 0))
                {
                    condition = condition + " AND " + ref + " = '" +
                                field1.get(obj) + "'";
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }

        if (condition.length() > 0)
        {
            return "delete from " + curTableName + " where " +
                   condition.substring(condition.indexOf("AND") + 3);
        }

        return null;
    }

    public String addConvert(Object obj, Vector v)
    {
        Field field1 = null;

        Class classInst = obj.getClass();

        String para = "";
        String value = "";

        try
        {
            for (int i = 0; i < v.size(); i++)
            {
                String name = (String) v.elementAt(i);

                if (name.length() <= 0)
                {
                    continue;
                }

                field1 = classInst.getDeclaredField(name);

                if (field1.getType().getName().equals("java.lang.String"))
                {
                    if (field1.get(obj) != null)
                    {
                        para  = para + "," + name;
                        value = value + "," + "'" + field1.get(obj) + "'";
                    }
                }
                else if (field1.getType().getName().equals("char"))
                {
                    para  = para + "," + name;
                    value = value + "," + "'" + field1.getChar(obj) + "'";
                }
                else if (field1.getType().getName().equals("int"))
                {
                    para  = para + "," + name;
                    value = value + "," + field1.getInt(obj);
                }
                else if (field1.getType().getName().equals("double"))
                {
                    para  = para + "," + name;
                    value = value + "," + field1.getDouble(obj);
                }
                else if (field1.getType().getName().equals("float"))
                {
                    para  = para + "," + name;
                    value = value + "," + field1.getFloat(obj);
                }
                else if (field1.getType().getName().equals("long"))
                {
                    para  = para + "," + name;
                    value = value + "," + field1.getLong(obj);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }

        if ((para.length() > 0) && (value.length() > 0))
        {
            return "insert into " + curTableName + "(" + para.substring(1) +
                   ") values (" + value.substring(1) + ")";
        }
        else
        {
            return null;
        }
    }
    
    public String updateConvert(Object obj, Vector v)
    {
        Field field1 = null;

        Class classInst = obj.getClass();

        String updatecom = "";

        try
        {
            for (int i = 0; i < v.size(); i++)
            {
                String name = (String) v.elementAt(i);

                if (name.length() <= 0)
                {
                    continue;
                }

                field1 = classInst.getDeclaredField(name);

                if (field1.getType().getName().equals("java.lang.String"))
                {
                    updatecom = updatecom + ", " + name + " = '" +
                                field1.get(obj) + "'";
                }
                else if (field1.getType().getName().equals("char"))
                {
                    updatecom = updatecom + ", " + name + " = '" +
                                field1.getChar(obj) + "'";
                }
                else if (field1.getType().getName().equals("int"))
                {
                    updatecom = updatecom + ", " + name + " = " +
                                field1.getInt(obj);
                }
                else if (field1.getType().getName().equals("double"))
                {
                    updatecom = updatecom + ", " + name + " = " +
                                field1.getDouble(obj);
                }
                else if (field1.getType().getName().equals("float"))
                {
                    updatecom = updatecom + ", " + name + " = " +
                                field1.getFloat(obj);
                }
                else if (field1.getType().getName().equals("long"))
                {
                    updatecom = updatecom + ", " + name + " = " +
                                field1.getLong(obj);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return null;
        }

        if (updatecom.length() > 0)
        {
            String condition = delConvert(obj);

            if (condition != null)
            {
                condition = condition.substring(condition.indexOf("where"));

                return "update " + curTableName + " set " +
                       updatecom.substring(1) + " " + condition;
            }
        }

        return null;
    }

    public String[] getElement(Element el, String[] arg, Vector v)
    {
    	Vector r = new Vector();

        for (int i = 0; i < arg.length; i++)
        {
        	NodeList nl = el.getElementsByTagName(arg[i]);
        	if (nl != null && nl.item(0) != null)
        	{
        		Text t = (Text) nl.item(0).getFirstChild();
            
	            if (t != null)
	            {
	            	r.add(t.getNodeValue());
	                v.add(arg[i]);
	            }
	            else
	            {
	            	r.add("");
	            	v.add(arg[i]);
	            }
        	}
        }

        String[] row = new String[r.size()];
        r.toArray(row);
        
        return row;
    }

    public Vector parseMethod()
    {
        Vector v = new Vector();
        NodeList nList = doc.getElementsByTagName("TABLE");

        NodeList nll = ((Element) nList.item(0)).getElementsByTagName("ROW");
        String line;

        for (int j = 0; j < nll.getLength(); j++)
        {
            line = "";

            Element t = (Element) nll.item(j);

            if (t.getAttribute("type").equals("U"))
            {
                line = XMLtoTableInfo(t, update);
            }
            else if (t.getAttribute("type").equals("A"))
            {
                line = XMLtoTableInfo(t, add);
            }
            else if (t.getAttribute("type").equals("D"))
            {
                line = XMLtoTableInfo(t, del);
            }
            else if (t.getAttribute("type").equals("X"))
            {
                line = XMLtoTableInfo(t, remove);
            }
            else
            {
                continue;
            }

            if ((line == null) || (line.length() <= 0))
            {
                continue;
            }
            else
            {
                v.add(line);
            }
        }

        return v;
    }
}
