package custom.localize.Gzbh;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;


public class Gzbh_DataService extends DataService
{
	public int getGoodsDef(GoodsDef goodsDef, int searchFlag, String barcode, String gz, String proTime, String yhsj,String djlb)
	{
		int result = -1;
		result = super.getGoodsDef(goodsDef, searchFlag, barcode, gz, proTime, yhsj,djlb);
		
		// 播放声音
		playGoodsSound(result);
		
		return result;
	}

	public void playGoodsSound(int rtnValue)
	{
		//声音文件路径
		String audioPath = "";

		if (rtnValue == 0)
		{
			audioPath = "success.wav";
		}
		else
		{
			audioPath = "fail.wav";
		}

		try
		{
			CommonMethod.waitForExec("miniplayer.exe " + audioPath);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendSaleDataToMemberDB(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, boolean again)
	{
		Vector retValue = new Vector();

		if (again)
		{
			saleHead.str1 = "N";
		}
		else
		{
			saleHead.str1 = "Y";
		}

		int result = NetService.getDefault().sendExtendSaleData(saleHead, saleGoods, salePayment, retValue);

		if (retValue.size() > 0)
		{
			String memo = retValue.elementAt(0).toString();
			
			if (memo != null && memo.trim().length() > 0)
			{
				String[] jf = memo.split(";");
				
				if (jf == null || jf.length != 3)
				{
					saleHead.str5 = "积分稍后计算";
					new MessageBox("积分返回格式错误\n" + jf.length + "\n" + jf.toString());
				}
				else
				{
					// 本次积分;可兑奖积分;积分累计
					saleHead.bcjf = Float.parseFloat(jf[0]);
					saleHead.ljjf = Float.parseFloat(jf[1]);
					saleHead.str5 = jf[2];
				}
				updateSendSaleData(saleHead);
			}
//			if (jf.length > 0 && jf[0].trim().length() > 0)
//			{
//				saleHead.bcjf = Float.parseFloat(jf[0]);
//			}
//			if (jf.length > 1 && jf[1].trim().length() > 0)
//			{
//				saleHead.ljjf = Float.parseFloat(jf[1]);
//			}
//			if (jf.length > 2 && jf[2].trim().length() > 0)
//			{
//				saleHead.str5 = jf[2];
//			}
		}

		if (result != 0 && result != 2)
		{
			// 记录小票未发送到CRM任务
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVTOEXTEND, GlobalInfo.balanceDate + "," + saleHead.fphm);

			//
			new MessageBox("上传小票到会员服务器失败\n请去会员中心查询!");
		}
	}

	// 更新本地库SALEHEAD
	public void updateSendSaleData(SaleHeadDef saleHead)
	{
		if (saleHead.hykh != null && saleHead.hykh.length() > 0)
		{
			String line = "";
			line = "update SALEHEAD set str5 = '" + saleHead.str5 + "',bcjf = " + saleHead.bcjf + ",ljjf = " + saleHead.ljjf + " where syjh = '"
					+ ConfigClass.CashRegisterCode + "' and  fphm = " + saleHead.fphm;
			GlobalInfo.dayDB.executeSql(line);
		}
	}
	
	// 根据商品查找超市促销规则单号
	public boolean findSuperMarketPopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String cardno)
	{
		if (GlobalInfo.isOnline)
		{
			Gzbh_NetService netservice = ((Gzbh_NetService)NetService.getDefault());
			boolean suc = netservice.findSuperMarketPopBillNo(ruleDef, code, gz, catid, ppcode, spec, time, yhtime, cardno,
																NetService.getDefault().getMemCardHttp(CmdDef.GETSMPOPBILLNO), CmdDef.GETSMPOPBILLNO);
			if (suc)
			{
				if (ruleDef.djbh.length() > 0) return true;
				else return false;
			}
			return suc;
		}
		return true;
	}

	// 根据规则单号查询超市促销规则
	public boolean findSuperMarketPopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef ruleDef)
	{
		if (GlobalInfo.isOnline)
		{
			Gzbh_NetService netservice = ((Gzbh_NetService)NetService.getDefault());
			boolean suc = netservice.findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef, NetService.getDefault()
																											.getMemCardHttp(CmdDef.GETSMPOPRULE),
															CmdDef.GETSMPOPRULE);
			return suc;
		}
		return true;
	}
}
