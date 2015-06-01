package com.efuture.javaPos.Logic;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;


public class PassModifyBS
{
    public PassModifyBS()
    {
    }

    public boolean checkOldPass(String password)
    {
    	boolean ret = true;
    	
        try
        {
            if (GlobalInfo.posLogin == null)
            {
            	ret = false;
            	
                return false;
            }

            if (((password == null) && (GlobalInfo.posLogin.passwd != null)) ||
                    ((password != null) &&
                        (GlobalInfo.posLogin.passwd == null)))
            {
            	ret = false;
            	
                return false;
            }

            if (ManipulatePrecision.getEncrypt(password)
                                       .equals(GlobalInfo.posLogin.passwd))
            {
                return true;
            }
            else
            {
            	ret = false;
                
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
        finally
        {
        	if (!ret) new MessageBox(Language.apply("原密码输入不正确!"), null, false);
        }
    }

    public boolean checkNewPass(String password)
    {
        if ((password == null) || password.trim().equals(""))
        {
        	new MessageBox(Language.apply("密码不能为空!"), null, false);
        	
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean setNewPassWord(String newpass, String okpass)
    {
        if ((newpass == null) || (okpass == null))
        {
        	new MessageBox(Language.apply("密码不能为空!"), null, false);
        	
            return false;
        }
        else if (!newpass.trim().equals(okpass.trim()))
        {
        	new MessageBox(Language.apply("两次输入密码不一致!"), null, false);
        	
            return false;
        }
        else
        {
            if (new MessageBox(Language.apply("你确定要修改密码吗?"), null, true).verify() == GlobalVar.Key1)
            {
            	if (GlobalInfo.isOnline)
            	{
	            	// 修改后台数据库中用户密码
	                if (NetService.getDefault().setPassWord(newpass))
	                {
	                	// 修改本地数据库中用户密码
	                    AccessBaseDB.getDefault().setOperUserPass(newpass);
	                    
	                    // 
	                    GlobalInfo.posLogin.passwd = ManipulatePrecision.getEncrypt(newpass);
	
	                    new MessageBox(Language.apply("当前用户密码修改成功!"), null, false);
	
	                    return true;
	                }
	                else
	                {
	                    return false;
	                }
            	}
            	else
            	{
            		new MessageBox(Language.apply("密码必须在联网状态下修改!"), null, false);
            		
            		return false;
            	}
            }
            
            return false;
        }
    }
}
