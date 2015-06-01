package custom.localize.Gzbh;

import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;

public class Gzbh_AccessBaseDB extends AccessBaseDB
{
	public boolean getCustomer(CustomerDef cust, String track)
	{
		track=track.split(",")[0];//格式为：2轨,3轨
		
		// 本地解析会员卡磁道
		String lsTrack = track.trim();
		String lsCardNo = "";
		String cardtype = "";
		// 首先判断是否联名卡的情况
		if (lsTrack.length() >= 16)
		{
			// 截取前16
			lsCardNo = lsTrack.substring(0, 16);

			// 5，6两位是30的情况-联名卡普卡
			if (lsCardNo.substring(4, 6).equals("30"))
			{
				cust.code = lsCardNo;
				cust.type = "04";
				setCustInfo(cust);
				return true;
			}
			// 5，6两位是35的情况-联名卡金卡			
			else if (lsCardNo.substring(4, 6).equals("35"))
			{
				cust.code = lsCardNo;
				cust.type = "05";
				setCustInfo(cust);
				return true;
			}
			// 判断是会员卡的情况
			else if (lsTrack.length() >= 18)
			{
				// 从第10位起截取9位
				lsCardNo = lsTrack.substring(9, 18);

				int intCardNo = Integer.parseInt(lsCardNo);

				// 根据卡号段取得卡类型
				String sqlstr = "SELECT CODE FROM CUSTOMERTYPE WHERE  VALUE4 <= " + intCardNo + " AND " + intCardNo + " <= VALUE5";

				Object obj = GlobalInfo.localDB.selectOneData(sqlstr);

				if (obj != null)
				{
					cardtype = String.valueOf(obj);
					cust.code = lsCardNo;
					cust.type = cardtype;
					setCustInfo(cust);
					return true;
				}

				GlobalInfo.localDB.resultSetClose();

				return false;
			}
		}

		return false;
	}

	public void setCustInfo(CustomerDef cust)
	{
		cust.name = ""; // 持卡人姓名
		cust.status = "Y"; // 卡状态
		cust.iszk = 'N'; // 是否打折
		cust.isjf = 'Y'; // 是否积分
	}

}
