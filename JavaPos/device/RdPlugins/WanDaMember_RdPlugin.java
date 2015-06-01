package device.RdPlugins;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_RdPlugins;

public class WanDaMember_RdPlugin implements Interface_RdPlugins
{
	static
	{
		System.loadLibrary("WanDaMember");
	}

	private String errcode;
	private String errmsg;
	private String result;

	public native int load();

	public native String memberFunc(String tenantID, String casherID); // 终端主界面

	// 检查会员状态是否有效
	public native String checkMember(String tenantID, String casherID, String track, String receiptNumber) throws Exception;

	// 会员购物消费
	public native String memberSales(String tenantID, String casherID, String track, String receiptNumber, String isPayment, String amount);

	// 会员购物消费撤销
	public native String unMemberSales(String tenantID, String casherID, String track, String receiptNumber, String unTraceNumber, String unBatchNumber, String unDate, String unReceiptNumber);

	// 会员购物消费退货
	public native String memberReturn(String tenantID, String casherID, String track, String receiptNumber, String unTraceNumber, String unBatchNumber, String unDate, String unReceiptNumber, String returnAmout);

	// 会员购物退货撤销
	public native String unMemberReturn(String tenantID, String casherID, String track, String receiptNumber, String unTraceNumber, String unBatchNumber, String unDate, String unReceiptNumber);

	// 会员交易达成
	public native String transComplete(String tenantID, String cahserID, String traceNumber);

	// 会员功能操作
	public native String cardFunc(String tenantID, String CasherID, String track, String funcCode);

	public native String CheckIn(String tenantID, String CasherID);
	
	public native String CheckOut(String tenantID, String CasherID);
	
	public native String MemberInfo(String tenantID, String casherID, String track);
	
	public native String BonusAlter(String tenantID, String casherID, String track, String receiptNumber, String CashAmount, String BounsAlter, String ReCashAmount, String ReBonusAlter,String transType);
	
	public native int release();

	private void clearMsg()
	{
		errcode = "";
		errmsg = "";
		result = "";
	}

	public boolean loadPlugins()
	{
		return load() == 0 ? true : false;
	}

	public boolean exec(int code, String param)
	{
		clearMsg();
		try
		{
			String[] p = param.split(",");
			switch (code)
			{
				case 1:
					if (p.length != 2)
					{
						errmsg = "传入 memberFunc 函数参数的个数有问题！！！";
						return false;
					}
					result = memberFunc(p[0], p[1]);
					break;
				case 2:
					if (p.length != 4)
					{
						new MessageBox("传入 checkMember 函数参数个数异常！");
						return false;
					}
					result = checkMember(p[0], p[1], p[2], p[3]);
					break;
				case 3:
					if (p.length != 6)
					{
						new MessageBox("传入 memberSales 函数参数个数异常！");
						return false;
					}
					result = memberSales(p[0], p[1], p[2], p[3], p[4], p[5]);
					break;
				case 4:
					if (p.length != 8)
					{
						new MessageBox("传入 unMemberSales 函数参数个数异常！");
						return false;
					}
					result = unMemberSales(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
					break;
				case 5:
					if (p.length != 9)
					{
						new MessageBox("传入 memberReturn 函数参数个数异常！");
						return false;
					}
					result = memberReturn(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8]);
					break;
				case 6:
					if (p.length != 8)
					{
						new MessageBox("传入 unMemberReturn 函数参数个数异常！");
						return false;
					}
					result = unMemberReturn(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
					break;
				case 7:
					if (p.length != 3)
					{
						new MessageBox("传入 transComplete 函数参数个数异常！");
						return false;
					}
					result = transComplete(p[0], p[1], p[2]);
					break;
				case 8:
					if (p.length != 4)
					{
						new MessageBox("传入 cardFunc 函数参数个数异常！");
						return false;
					}
					result = cardFunc(p[0], p[1], p[2], p[3]);
					break;
				case 10:
					if (p.length != 2)
					{
						new MessageBox("传入 CheckIn 函数参数个数异常！");
						return false;
					}
					result = CheckIn(p[0], p[1]);
					break;
				case 11:
					if (p.length != 2)
					{
						new MessageBox("传入 CheckOut 函数参数个数异常！");
						return false;
					}
					result =CheckOut(p[0], p[1]);
					break;
				case 12:
					if (p.length != 3)
					{
						new MessageBox("传入 MemberInfo 函数参数个数异常！");
						return false;
					}
					result =MemberInfo(p[0], p[1], p[2]);
					break;
				case 13:
					if (p.length != 9)
					{
						new MessageBox("传入 BonusAlter 函数参数个数异常！");
						return false;
					}
					result =BonusAlter(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],p[8]);
					break;
			}

			if (result == null)
			{
				errcode = "XX";
				errmsg = "功能模块调用失败";
			}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean exec(String param)
	{
		return false;
	}

	public String getErrorCode()
	{
		return errcode;
	}

	public String getErrorMsg()
	{
		if (errmsg == null)
			errmsg = "未知错误";

		return errmsg;
	}

	public boolean releasePlugins()
	{
		return release() == 0 ? true : false;
	}

	public Object getObject()
	{
		return this.result;
	}
}
