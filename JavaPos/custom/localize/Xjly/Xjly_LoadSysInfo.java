package custom.localize.Xjly;

import org.eclipse.swt.widgets.Label;

import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;

public class Xjly_LoadSysInfo extends LoadSysInfo
{
	public boolean startLoadInfo(Label lbl_message)
	{
		boolean b = super.startLoadInfo(lbl_message);
            String s[] = GlobalInfo.syjDef.priv.split(",");
			if (s.length > 1) {
				if (s[1].substring(0, 1).equals("Y")) {
					setLabelHint(lbl_message, "正在初始化评价器设备......");
					if (!DeviceName.deviceEvaluation.equals("")) {
						device.Evaluation.PJ06 c = new device.Evaluation.PJ06();
						c.open();
					}
				}
			}
		return b;
	}
}
