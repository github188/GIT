package com.efuture.javaPos.Logic;

import java.util.Vector;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.SecMonitor;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.UI.Design.BuyInfoForm;


public class PersonnelGoBS
{
	Vector actshell = null;
	Control actctrl = null;
	
    public PersonnelGoBS()
    {
    }

    public void setPersonGo()
    {
    	BuyInfoForm personGoForm = new BuyInfoForm();
    	personGoForm.ismustsel = true;
    	personGoForm.isshownodata = false;
    	// 选择离开理由，上传到工作日志
    	personGoForm.open(new String[]{"LK"});
		String code = Language.apply("收银员离开");
		if (personGoForm.selCode.size() > 0)
		{
			code = ((String[])personGoForm.selCode.get(0))[1];
		}

    	// 广告屏锁定
    	if (SecMonitor.secMonitor != null) SecMonitor.secMonitor.monitorLock(true);
    	
        // 隐藏收银界面之上的所有激活窗口
    	if (ConfigClass.SecMonitor_Open.equals("Y") && ConfigClass.DisplayMode.toLowerCase().indexOf("mplayer") >= 0)
    	{
    		
    	}
    	else
    	{
	    	actctrl = Display.getDefault().getFocusControl();
	    	if (actshell == null) actshell = new Vector();
	    	else actshell.removeAllElements();
	    	Shell[] shells = Display.getDefault().getShells();
	    	for (int i=shells.length-1;i>=0;i--)		//0为自身离开窗口的SHELL,不应该加入到窗口集合中,离开回来时不应该恢复自身窗口显示,否则焦点在自身
	    	{
	        	Shell shell = shells[i];
	        	if (shell == GlobalInfo.mainshell) break;
	        	actshell.add(shell);
	        	shell.setVisible(false);
	        }
	    	if (GlobalInfo.saleform != null)
	    	{
	    		GlobalInfo.saleform.setVisible(false);
	    		GlobalInfo.background.setVersionEanble(true);
	    	}
    	}
    				        
        AccessDayDB.getDefault().writeWorkLog(code, StatusType.WORK_LEAVER);

        GlobalInfo.syjStatus.status = StatusType.STATUS_LEAVE;
        AccessLocalDB.getDefault().writeSyjStatus();
        DataService.getDefault().sendSyjStatus();
    }

    public boolean checkComeBack(String password, String curtime)
    {
        try
        {
            if (checkPassWord(password))
            {
                AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员回来,本次离开时间为 ") + curtime, StatusType.WORK_COMEBACK);

                GlobalInfo.syjStatus.status = StatusType.STATUS_LOGIN;
                AccessLocalDB.getDefault().writeSyjStatus();
                DataService.getDefault().sendSyjStatus();

                // 广告屏解锁
                if (SecMonitor.secMonitor != null) SecMonitor.secMonitor.monitorLock(false);
                
                // 恢复收银界面之上的所有激活窗口
                if (actshell != null)
                {
	                for (int i=0;i<actshell.size();i++)
	    			{
	                	Shell s = (Shell)actshell.elementAt(i);
	                	s.setVisible(true);
	                	s.setActive();
	    			}
	                if (actctrl != null && !actctrl.isDisposed()) actctrl.setFocus();
	                if (GlobalInfo.saleform != null) 
	                {
	                	GlobalInfo.background.setVersionEanble(false);
	                	GlobalInfo.saleform.setVisible(true);
	                }
                }
                
                return true;
            }
            else
            {
                new MessageBox(Language.apply("密码输入不正确!"), null, false);

                return false;
            }
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private boolean checkPassWord(String password)
    {
        if (GlobalInfo.posLogin == null)
        {
            return false;
        }

        if (((password == null) && (GlobalInfo.posLogin.passwd != null)) ||
                ((password != null) && (GlobalInfo.posLogin.passwd == null)))
        {
            return false;
        }

        if (ManipulatePrecision.getEncrypt(password)
                                   .equals(GlobalInfo.posLogin.passwd))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void disentangleScreen()
    {
    	OperUserDef staff = DataService.getDefault().personGrant(Language.apply("授权解锁"));

    	if (staff == null)
    	{
    		return ;
    	}

    	if (staff.operrange != 'Y')
    	{
    		new MessageBox(Language.apply("该授权人员无解锁权限!"));

    		return ;
    	}

    	// 检查是否能关闭交易窗口
    	if (GlobalInfo.saleform != null && !GlobalInfo.saleform.closeForm())
    	{
    		if (new MessageBox(Language.apply("你确定要放弃已输入的交易重新登录吗?"), null, true).verify() != GlobalVar.Key1) return;
    		GlobalInfo.saleform.getSaleEvent().initOneSale(SellType.RETAIL_SALE);
    	}
	          
    	// 记录解锁日志
    	AccessDayDB.getDefault().writeWorkLog("["+staff.gh+Language.apply("]员工号授权解锁"), StatusType.WORK_COMEBACK);
 
    	// 销毁收银界面之上的所有激活窗口
    	if (actshell != null)
    	{
    		for (int i=actshell.size()-1;i>=0;i--)
    		{
    			Shell s = (Shell)actshell.elementAt(i);
    			s.dispose();
    			s = null;
    		}
    	}

    	// 回到重新登录窗口
    	CustomLocalize.getDefault().createMenuFuncBS().openSyyDl(null,null);
	}
}
