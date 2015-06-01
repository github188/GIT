package custom.localize.Hbht;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;

import custom.localize.Bcrm.Bcrm_LoadSysInfo;

public class Hbht_LoadSysInfo extends Bcrm_LoadSysInfo
{
	public boolean getConfigTemplate(Label lbl_message)
	{
		setLabelHint(lbl_message, Language.apply("正在读取提货单打印模版......"));

		if (!Hbht_LadingBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取提货单打印模版文件错误!"));
		}

		return super.getConfigTemplate(lbl_message);
	}
}
