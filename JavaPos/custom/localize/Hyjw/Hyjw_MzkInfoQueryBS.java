package custom.localize.Hyjw;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;

import custom.localize.Hyjw.Hyjw_MzkModule.RetInfoDef;

public class Hyjw_MzkInfoQueryBS extends MzkInfoQueryBS
{
	public void QueryMzkInfo()
	{

		ProgressBox box = new ProgressBox();
		box.setText("请将操作转向终端设备....");

		RetInfoDef retinfo = Hyjw_MzkModule.getDefault().cardQuery(false);

		box.close();
		box = null;

		mzkDisplayInfo(retinfo);

	}

	protected void mzkDisplayInfo(RetInfoDef retinfo)
	{
		StringBuffer info = new StringBuffer();

		// 组织提示信息
		info.append(Language.apply("卡    号: ") + Convert.appendStringSize("", retinfo.tradeCardno, 1, 20, 20, 0) + "\n");
		info.append(Language.apply("储值余额: ") + Convert.appendStringSize("", retinfo.ye + "", 1, 20, 20, 0) + "\n");
		info.append(Language.apply("积分余额:") + Convert.appendStringSize("", retinfo.scoreYe + "", 1, 20, 20, 0) + "\n");
		info.append(Language.apply("红包余额:") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(retinfo.elecbagYe), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("积返余额:") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(retinfo.scoreRebateYe), 1, 20, 20, 0) + "\n");

		// 弹出显示
		new MessageBox(info.toString());
	}

}
