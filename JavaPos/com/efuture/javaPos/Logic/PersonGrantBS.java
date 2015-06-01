package com.efuture.javaPos.Logic;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.OperUserDef;


// 检查用户信息
public class PersonGrantBS
{
	protected OperUserDef staff = new OperUserDef();
	
    public OperUserDef getGrantStaff(String track1,String track2,String track3)
    {
    	try
    	{
	    	String id = track2;
	    	
	    	if (id.equals(GlobalInfo.posLogin.gh))
	    	{
	    		new MessageBox(Language.apply("不允许收银员进行自身授权"));
	    		return null;
	    	}
	    	
	    	// 查找人员
	        if (!DataService.getDefault().getOperUser(staff, id))
	        {
	            return null;
	        }
	
	    	if (staff.isgrant != 'Y')
	    	{
	    		new MessageBox(Language.apply("该员工卡不能授权"));
	    		
	    		return null;
	    	}
	    	
	        // 检查工号过期
	        String expireDate = staff.maxdate + " 0:0:0";
	        ManipulateDateTime mdt = new ManipulateDateTime();
	        if (mdt.getDisDateTime(mdt.getDateBySlash() + " 0:0:0", expireDate) < 0)
	        {
	            new MessageBox(Language.apply("该工号已过期!"), null, false);
	
	            return null;
	        }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
        
        return staff;
    }

    public boolean checkPasswd(String passwd)
    {
        String pwd = ManipulatePrecision.getEncrypt(passwd);

        if (staff.passwd.equals(pwd))
        {
            return true;
        }

        //
        new MessageBox(Language.apply("密码不正确!"));
        
        return false;
    }
}
