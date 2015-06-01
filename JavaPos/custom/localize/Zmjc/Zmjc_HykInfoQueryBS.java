package custom.localize.Zmjc;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Zmjc_HykInfoQueryBS extends HykInfoQueryBS
{

	/*public int getMemberInputMode()
	{
		return TextBox.MsrKeyInput;//.MsrInput;
	}*/
	
	public CustomerDef findMemberCard(String track2)
	{
		CustomerDef cust = null;
		try
		{
			cust = super.findMemberCard(track2);
			if(cust!=null)
			{
				if(cust.valstr2!=null && cust.valstr2.equalsIgnoreCase("03"))
				{
					PosLog.getLog(this.getClass().getSimpleName()).info(Language.apply("此卡需要激活：") + String.valueOf(cust.valstr1));
					//valstr2='03'表示要激活
					//valstr1表示激活理由
					if(new MessageBox(String.valueOf(cust.valstr1) + "Language.apply(\n\n是否马上需要激活？)", null, true).verify() != GlobalVar.Key1) 
					{
						PosLog.getLog(this.getClass().getSimpleName()).info(Language.apply("会员卡刷卡失败：此卡未激活（款员放弃）"));
						new MessageBox(Language.apply("会员卡刷卡失败：此卡未激活"));
						return null;
					}
					
					int count=0;
					reInput:
					while(true)
					{
						//录入身份证号、电话号码、证件类型
						String cid=null;
						String phone=null;
						String cidType=null;
						PersoninfoForm f = new PersoninfoForm();
						if(!f.getIsSave()) 
						{
							PosLog.getLog(this.getClass().getSimpleName()).info("会员卡刷卡失败：此卡未激活（款员取消）");
							f = null;
							new MessageBox(Language.apply("\n会员卡刷卡失败：此卡没有进行激活\n\n"));
							return null;
						}
						cidType = f.getCType().trim();
						cid = f.getCID().trim().toUpperCase();
						phone = f.getPhoneNo().trim();
						f = null;
						PosLog.getLog(this.getClass().getSimpleName()).info("开始激活 CID=[" + cid + "],phone=[" + phone + "],cidType=[" + cidType + "].");
						//激活会员卡
						int iRet = ((Zmjc_NetService) NetService.getDefault()).hykJH(cust.code, cid, phone, cidType);
						if (iRet != 0)
						{
							count++;
							PosLog.getLog(this.getClass().getSimpleName()).info("激活失败（第" + count + "次）");
							//return null;
							continue reInput;//激活失败时，继续输入证件信息
						}
						break;
					}
					
					PosLog.getLog(this.getClass().getSimpleName()).info("激活成功，开始获取最新卡信息");
					//激活成功后，再次查找会员卡，获取最新卡信息
					return findMemberCard(track2);
				}
				else
				{
					//PosLog.getLog(this.getClass().getSimpleName()).info("此卡不需要激活");
				}
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}		
		return cust;
		
	}
	
	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		double jcjfl = 0;
		double jczkl = 0;
		String cardType = "";
		if(cust.valstr1!=null)
		{
			String[] arr = cust.valstr1.split("\\|");
			jcjfl = Convert.toDouble(arr[0]);
			if(arr.length>1)
			{
				jczkl = Convert.toDouble(arr[1]);
			}
			if(arr.length>2)
			{
				cardType = arr[2].trim();
			}
		}
		String status="无效";
		if(cust.status!=null && cust.status.length()>0 && cust.status.charAt(0)=='Y') 
			status="有效";
		info.append(Language.apply("卡    号: ") + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("持 卡 人: ") + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("卡 状 态: ") + Convert.appendStringSize("", status, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("卡 积 分: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
		info.append(Language.apply("卡 类 别: ") + Convert.appendStringSize("", cardType, 1, 16, 16, 0) + "\n");
		info.append(Language.apply("基础积分率: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(jcjfl), 1, 16, 16, 0) + "\n");
		info.append(Language.apply("基础折扣率: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(jczkl), 1, 16, 16, 0) + "\n");
		if (isLczcFunc(cust))
		{
			info.append(Language.apply("零钞转存: ") + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
			info.append(Language.apply("零钞余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
			info.append(Language.apply("零钞上限: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		}
	}
}
