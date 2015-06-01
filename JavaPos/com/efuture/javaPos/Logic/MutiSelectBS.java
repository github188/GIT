package com.efuture.javaPos.Logic;

import java.util.Vector;

import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.UI.MutiSelectEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class MutiSelectBS
{

	public boolean enterBS(MutiSelectEvent event, MutiSelectForm form, int i, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		return false;
	}

	public boolean validationBS(MutiSelectEvent event, MutiSelectForm form, int funcid, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		//修改缴款单号
		if (funcid == 907)
		{
			// 刷面值卡
			TextBox txt = new TextBox();
			StringBuffer buff = new StringBuffer();
			if (!txt.open("请输入修改后的缴款单号", "请输入修改后的缴款单号", "请输入修改后的缴款单号", buff, 0, 0, false,TextBox.IntegerInput)) { return false; }
			
		}
		return false;
	}

	public boolean payBS(MutiSelectEvent event, MutiSelectForm form, int funcid, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		return false;
	}

	public boolean exitBS(MutiSelectEvent event, MutiSelectForm form, int funcid, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		return false;
	}

	public void initBS(MutiSelectEvent event, MutiSelectForm form, int funcID, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		
	}

}
