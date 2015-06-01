package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.DownloadData;
import com.efuture.javaPos.Communication.UpdateBaseInfo;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class CleanUpLocalDataBaseBS 
{
	ProgressBox pb = null;
	
	public CleanUpLocalDataBaseBS()
	{
		
	}
	
	public void executeCleanUp()
	{
		try
		{
			if (new MessageBox(Language.apply("你确定要整理本地数据库吗？"),null,true).verify() != GlobalVar.Key1) return;
			
            if (!executeCleanUpLocal()) return;
            
            if (!getBaseTableCount()) return;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	private boolean executeCleanUpLocal()
	{
		ResultSet rs = null;
		
		try
		{
			pb = new ProgressBox();
			
			pb.setText(Language.apply("开始整理Local数据库,请稍等..."));

			// SQLite数据库不执行
			if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite")) return true;
			
			//整理Local 的 DERBY数据库
            rs =  GlobalInfo.localDB.selectData("select TABLENAME from SYS.SYSTABLES where TABLETYPE = 'T'");
            
            if (rs != null)
            {
            	while(rs.next())
            	{
                    GlobalInfo.localDB.setSql("call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('APP','" + rs.getString(1) + "',0)");
                    GlobalInfo.localDB.executeSql();
            	}
            }
            
            return true;
		}
		catch (Exception ex)
		{
			pb.close();
			pb = null;
			new MessageBox(Language.apply("整理Local报错:") + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
		}
	}
	
	private boolean getBaseTableCount()
	{
		ResultSet rs = null;
		Vector tablename = null;
		int num = 0;
		
		try
		{
			pb.setText(Language.apply("开始获得BASE库资料表个数,请稍等..."));
			
			if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
			{
				rs = GlobalInfo.baseDB.selectData("select tbl_name from sqlite_master where type = 'table' and tbl_name not like 'sqlite%'");
			}
			else
			{
				rs = GlobalInfo.baseDB.selectData("select TABLENAME from SYS.SYSTABLES where TABLETYPE = 'T'");
			}
			
			if (rs == null)
			{
				pb.close();
				new MessageBox(Language.apply("查询Base表信息rs为null"), null, false);
				return true;
			}
			
			tablename = new Vector();
			
			while (rs.next())
			{
				tablename.add(new String[]{rs.getString(1),"0"});
				
				num = num + 1;
			}
			GlobalInfo.baseDB.resultSetClose();
			
			if (num < 1)
			{
				pb.close();
				new MessageBox(Language.apply("Base数据库中无表"), null, false);
				return true;
			}
			
			pb.setText(Language.apply("共{0}个表,开始获得表记录数,请稍等...", new Object[]{" " + num + " "}));
			
			for (int i = 0;i < tablename.size();i++)
			{
				String[] s = (String[])tablename.elementAt(i);
				
				pb.setText(Language.apply("正在计算{0}表({1}/{2})记录数,请稍等...", new Object[]{" " + s[0] + " ", (i+1) + "",num + ""}));
//				pb.setText("正在计算 " + s[0] + " 表("+(i+1)+"/"+num+")记录数,请稍等...");
				while (Display.getDefault().readAndDispatch());

				System.out.println(s[0]);
								
				GlobalInfo.baseDB.setSql("select count(*) from " + s[0]);
				rs = GlobalInfo.baseDB.selectData();
				rs.next();
				
				s[1] = rs.getString(1);
				
				System.out.println(s[0]+":"+s[1]);
				
				GlobalInfo.baseDB.resultSetClose();
			}
			
			pb.close();
			pb = null;
			
			//
    		String[] title = {Language.apply("资料表"),Language.apply("记录数")};
    		int[]    width = {350,150};
			new MutiSelectForm().open(Language.apply("查看BASE库各基础资料表记录数"), title, width, tablename,false);

			return true;
		}
		catch (Exception ex)
		{
			if (pb != null) pb.close();
			new MessageBox(Language.apply("获取Base表总数异常:") + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			pb = null;
			GlobalInfo.baseDB.resultSetClose();
		}
	}
	
	public void reloadBaseDB()
	{
    	StringBuffer buf = new StringBuffer();
    	
    	buf.append(Language.apply("当前基础资料BASE库的版本是: ") + DownloadData.readLocalDBDate() + "\n\n");
    	buf.append(Language.apply("1 - 重新下载基础资料库\n"));
    	buf.append(Language.apply("2 - 下载资料库增量数据\n"));
    	int key = new MessageBox(buf.toString()).verify();
    	if (key != GlobalVar.Key1 && key != GlobalVar.Key2) return;
    	if (key == GlobalVar.Key1)
    	{
    		// 删除已下载标记和增量标记
    		DownloadData.deleteDBDate();
    		UpdateBaseInfo.deleteUpdateInfoDate();
    		
    		if (ConfigClass.LocalDBType.equals("Derby"))
    		{
        		new MessageBox(Language.apply("本地数据库是Derby,将在重启款机后下载基础数据库!"));
    		}
    		else
    		{
    	        // 关闭本地数据库
    	        if (GlobalInfo.baseDB != null)
    	        {
    	            GlobalInfo.baseDB.Close();
    	            GlobalInfo.baseDB = null;
    	        }

		        ProgressBox pb = new ProgressBox();
		        pb.setText(Language.apply("正在下载基础资料BASE库，请等待....."));
    	        
    	        // 下载本地库
    	    	DownloadData.downloadBaseDB(pb.getLabel());
    	    	
    	    	// 重新连接Base库
    	    	LoadSysInfo.getDefault().loadLocalDB(pb.getLabel(),1);
    	    	
    	    	pb.close();
    	    	pb = null;
    		}
    	}
    	else
    	{
    		String[] title = {Language.apply("资料表"),Language.apply("描述"),Language.apply("增量序号")};
    		int[]    width = {160,260,100};
    		String[][] s = UpdateBaseInfo.displayUpdateInfoSeqno();
    		Vector contents = new Vector();
    		for (int i = 0;s != null && i<s.length; i++)
    		{
    			contents.add(s[i]);
    		}
			int choice = new MutiSelectForm().open(Language.apply("'回车键'开始下载增量数据,'退出键'放弃下载返回"), title, width, contents,false);
			if (choice >= 0)
			{
				long oldnum = GlobalInfo.sysPara.num_down;
				if (GlobalInfo.sysPara.num_down > 0)
				{
					key = new MessageBox(Language.apply("你想要分批每次下载{0}个增量数据吗?\n\n1 - 分批下载 / 2 - 全部下载",new Object[]{" " + GlobalInfo.sysPara.num_down + " "}),null,false).verify();
					if (key == GlobalVar.Key2) GlobalInfo.sysPara.num_down = 0;
				}
				
		        if (UpdateBaseInfo.updatBaseIsRunning())
		        {
		        	new MessageBox(Language.apply("后台进程正在下载增量数据,请稍后进行手工下载"));
		        }
		        else
		        {
			        ProgressBox pb = new ProgressBox();
			        pb.setText(Language.apply("正在下载资料库增量信息，请等待....."));
			        
		        	UpdateBaseInfo.downloadBaseInfo(false);
					
					pb.close();
					pb = null;
		        }
		        
		        GlobalInfo.sysPara.num_down = oldnum;
			}
    	}
	}
}
