package device.RdPlugins;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_RdPlugins;

 // 永泰会员接口封装（JNI)
public class EontimeCard_RdPlugin implements Interface_RdPlugins
{
	static
	{
		System.loadLibrary("EontimeCard");
	}

	private String errcode;
	private String errmsg;
	private String result;

	public native int load();

	// 1 - 终端主操作界面显示
	public native String memberFunc(String tenantID, String casherID); // 终端主界面

	// 2 - 检查会员状态是否有效
	public native String checkMember(String casherID, String track, String receiptNumber) throws Exception;

	// 3 - 会员购物消费
	public native String memberSales(String tenantID, String casherID, String track, String receiptNumber, String isPayment, String tootalAmount, String amount, String bonus, String payType);

	// 4 - 会员购物消费撤销
	public native String unMemberSales(String tenantID, String casherID, String track, String receiptNumber, String unTraceNumber, String unBatchNumber, String unDate, String unReceiptNumber);

	// 5 - 会员购物消费退货
	public native String memberReturn(String tenantID, String casherID, String track, String receiptNumber, String unTraceNumber, String unBatchNumber, String unDate, String unReceiptNumber, String isPayment, String totalAmount, String returnAmout, String returnBonus);

	// 6 - 会员购物退货撤销
	public native String unMemberReturn(String tenantID, String casherID, String track, String receiptNumber, String unTraceNumber, String unBatchNumber, String unDate, String unReceiptNumber);

	// 7 - 会员交易达成
	public native String transComplete(String tenantID, String cahserID, String traceNumber);

	// 8 - 会员功能操作
	public native String cardFunc(String tenantID, String CasherID, String track, String funcCode);

	// 9 - 终端签到操作，弹出签到结果提示窗
	public native String checkIn(String tenantID, String CasherID);
	
	// 10 - 终端签退/结算操作，此函数弹出签退提示窗口
	public native String checkOut(String tenantID, String CasherID);
	
	// 11 - 会员查询，弹出窗口显示会员信息
	public native String memberQuery(String tenantID, String casherID, String track);

	// 12 - 储值卡购物消费时调用此方法
	public native String cardSales(String tenantID, String casherID, String track, String receiptNumber, String amount);
	
	// 13 - 储值卡购物消费撤销时调用此方法
	public native String unCardSales(String tenantID, String casherID, String track, String receiptNumber, String unTraceNumber, String unBatchNumber, String unDate, String unReceiptNumber);

	// 14 - 无论当日与隔日均调用此方法进行退货操作
	public native String cardReturn(String tenantID, String casherID, String track, String receiptNumber, String unTraceNumber, String unBatchNumber, String unDate, String unReceiptNumber, String returnAmount);

	// 15 - 无论当日与隔日均调用此方法进行退货操作
	public native String getMemberByQrCode(String qrCode);

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
					if (p.length != 3)
					{
						new MessageBox("传入 checkMember 函数参数个数异常！");
						return false;
					}
					result = checkMember(p[0], p[1], p[2]);
					break;
				case 3:
					if (p.length != 9)
					{
						new MessageBox("传入 memberSales 函数参数个数异常！");
						return false;
					}
					result = memberSales(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8]);
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
					if (p.length != 12)
					{
						new MessageBox("传入 memberReturn 函数参数个数异常！");
						return false;
					}
					result = memberReturn(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9], p[10], p[11]);
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
				case 9:
					if (p.length != 2)
					{
						new MessageBox("传入 CheckIn 函数参数个数异常！");
						return false;
					}
					result = checkIn(p[0], p[1]);
					break;
				case 10:
					if (p.length != 2)
					{
						new MessageBox("传入 CheckOut 函数参数个数异常！");
						return false;
					}
					result =checkOut(p[0], p[1]);
					break;
				case 11:
					if (p.length != 3)
					{
						new MessageBox("传入 MemberQuery 函数参数个数异常！");
						return false;
					}
					result =memberQuery(p[0], p[1], p[2]);
					break;
				case 12:
					if (p.length != 5)
					{
						new MessageBox("传入 cardSales 函数参数个数异常！");
						return false;
					}
					result =cardSales(p[0], p[1], p[2], p[3], p[4]);
					break;
				case 13:
					if (p.length != 8)
					{
						new MessageBox("传入 unCardSales 函数参数个数异常！");
						return false;
					}
					result =unCardSales(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
					break;
				case 14:
					if (p.length != 9)
					{
						new MessageBox("传入 cardReturn 函数参数个数异常！");
						return false;
					}
					result =cardReturn(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],p[8]);
					break;
					
				case 15:
					if (p.length != 1)
					{
						new MessageBox("传入 cardReturn 函数参数个数异常！");
						return false;
					}
					result =getMemberByQrCode(p[0]);
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
