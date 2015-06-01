package custom.localize.Jwyt;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.LoadSysInfo;

public class Jwyt_LoadSysInfo extends LoadSysInfo
{
	public boolean getNetNewData(Label lbl_message)
	{
		if (super.getNetNewData(lbl_message))
		{
			setLabelHint(lbl_message, "正在下载Mars系统参数......");
			Jwyt_MarsModule.getDefault().getDeviceSecKey();
			return true;
		}
		return false;
	}

	public boolean getConfigTemplate(Label lbl_message)
	{
		super.getConfigTemplate(lbl_message);

		setLabelHint(lbl_message, "正在二维码礼品券打印模版......");
		if (!Jwyt_GiftPrintMode.getDefault().ReadTemplateFile())
		{
			new MessageBox("读取盘点单打印模版文件错误!");
		}
		return true;
	}
}
