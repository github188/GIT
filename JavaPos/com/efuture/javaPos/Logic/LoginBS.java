package com.efuture.javaPos.Logic;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PosTimeDef;

//检查用户信息
public class LoginBS
{
	public boolean getLoginStaff(String id)
	{
		GlobalInfo.posLogin = null;

		OperUserDef staff = new OperUserDef();

		// 查找收银员
		if (!DataService.getDefault().getOperUser(staff, id)) { return false; }

		// 检查不允许登录
		if (staff.islogin != 'Y')
		{
			new MessageBox(Language.apply("该工号不允许登录系统!"), null, false);

			return false;
		}


		// 检查工号过期
		String expireDate = staff.maxdate + " 0:0:0";
		ManipulateDateTime mdt = new ManipulateDateTime();

		if (mdt.getDisDateTime(mdt.getDateBySlash() + " 0:0:0", expireDate) < 0)
		{
			new MessageBox(Language.apply("该工号已过期!"), null, false);

			return false;
		}

		
		//
		GlobalInfo.posLogin = staff;

		return true;
	}

	public boolean checkPasswd(String passwd)
	{
		String pwd = ManipulatePrecision.getEncrypt(passwd);

		if (GlobalInfo.posLogin != null)
		{
			if (GlobalInfo.posLogin.passwd.equals(pwd)) { return true; }

			new MessageBox(Language.apply("密码不正确!"));
		}
		return false;
	}

	public boolean setPosTime(int cl)
	{
		PosTimeDef postime = (PosTimeDef) GlobalInfo.posTime.elementAt(cl);

		// 检查收银员当天上次登录班次是否匹配
		Object objbc = GlobalInfo.dayDB.selectOneData("select bc from SALESUMMARY where syyh = '" + GlobalInfo.posLogin.gh + "'");
		if (objbc != null && !objbc.toString().equals(String.valueOf(postime.code)))
		{
			if (new MessageBox(Language.apply("本次登录班次与收银员今天前次登录班次不匹配\n\n你确定要选择该班次吗？"), null, true).verify() != GlobalVar.Key1) { return false; }
		}

		// 设置新班次
		GlobalInfo.syjStatus.bc = postime.code;
		GlobalInfo.statusBar.setPosTime(postime.name);

		// 检查选择的班次和当前时间是否匹配
		String curtime = ManipulateDateTime.getCurrentTime().substring(0, 5);
		if ((postime.btime.compareTo(postime.etime) > 0 && curtime.compareTo(postime.btime) < 0 && curtime.compareTo(postime.etime) > 0) || (postime.btime.compareTo(postime.etime) <= 0 && (curtime.compareTo(postime.btime) < 0 || curtime.compareTo(postime.etime) > 0)))
		{
			if (new MessageBox(Language.apply("当前系统时间和班次不匹配\n\n你确定要选择该班次吗？"), null, true).verify() != GlobalVar.Key1) { return false; }
		}

		return true;
	}

	// 登录成功处理
	public boolean loginDone()
	{
		//检查是否在其他款机已登录
		if (GlobalInfo.sysPara.checknetlogin == 'Y' && !DataService.getDefault().getCheckLogin(ConfigClass.CashRegisterCode, GlobalInfo.posLogin.gh)) { return false; }

		
		// 设置当前登录状态
		GlobalInfo.syjStatus.syyh = GlobalInfo.posLogin.gh;
		GlobalInfo.syjStatus.status = StatusType.STATUS_LOGIN;

		// 发送状态
		AccessLocalDB.getDefault().writeSyjStatus();
		DataService.getDefault().sendSyjStatus();

		// 记录日志
		if (GlobalInfo.isOnline)
		{
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员登录成功"), StatusType.WORK_LOGIN);
		}
		else
		{
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员脱网登录,请检查"), StatusType.WORK_LOGIN);
		}

		// 非维护员登录后自动开钱箱
		if (GlobalInfo.sysPara.autoopendrawer == 'Y' && GlobalInfo.posLogin.type != '3')
		{
			CashBox.getDefault().openCashBox();
		}

		return true;
	}

	// 增加注销用户的处理
	public boolean logoutDone()
	{
		// 设置无收银员,开机状态
		GlobalInfo.syjStatus.status = StatusType.STATUS_START;
		GlobalInfo.syjStatus.syyh = "";
		GlobalInfo.posLogin = null;

		AccessLocalDB.getDefault().writeSyjStatus();
		DataService.getDefault().sendSyjStatus();

		return true;
	}

	public boolean isExit()
	{
		return true;
	}
}
