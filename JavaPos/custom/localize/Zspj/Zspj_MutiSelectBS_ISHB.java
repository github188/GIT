package custom.localize.Zspj;

import java.util.Vector;

import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.MutiSelectBS;
public class Zspj_MutiSelectBS_ISHB extends MutiSelectBS
{

	public boolean enterBS(Zspj_MutiSelectEvent_ISHB event, Zspj_MutiSelectForm_ISHB form, int i, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		return false;
	}

	public boolean validationBS(Zspj_MutiSelectEvent_ISHB event, Zspj_MutiSelectForm_ISHB form, int funcid, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
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

	public boolean payBS(Zspj_MutiSelectEvent_ISHB event, Zspj_MutiSelectForm_ISHB form, int funcid, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		return false;
	}

	public boolean exitBS(Zspj_MutiSelectEvent_ISHB event, Zspj_MutiSelectForm_ISHB form, int funcid, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		return false;
	}

	public void initBS(Zspj_MutiSelectEvent_ISHB event, Zspj_MutiSelectForm_ISHB form, int funcID, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		
	}

}
