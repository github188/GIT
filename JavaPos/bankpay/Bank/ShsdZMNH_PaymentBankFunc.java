package bankpay.Bank;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

/**
 * 中免（农行,上海沪友科技）
 * @author Administrator
 *
 */
public class ShsdZMNH_PaymentBankFunc extends ShsdCsf_PaymentBankFunc {
	
	public String getbankfunc()
	{
		return "C:\\SAND\\";
	}
	
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (type != PaymentBank.XYKQD && type != PaymentBank.XYKCD && type != PaymentBank.XYKJZ && type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH && type != PaymentBank.XYKYE)
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);
				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist(getbankfunc() + "request.txt"))
			{
				PathFile.deletePath(getbankfunc() + "request.txt");

				if (PathFile.fileExist(getbankfunc() + "request.txt"))
				{
					errmsg = "交易请求文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(getbankfunc() + "result.txt"))
			{
				PathFile.deletePath(getbankfunc() + "result.txt");

				if (PathFile.fileExist(getbankfunc() + "result.txt"))
				{
					errmsg = "交易应答文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			// 选择卡类型
			// 17 SMART卡
			// 18 巍康卡
			// 19 畅购卡
			// 20 OK积点卡
			// 21 OK会员卡
			// 签到的结算不传卡类型

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, cardtype)) { return false; }

			// 调用接口模块
			if (PathFile.fileExist(getbankfunc() + "javaposbank.exe"))
			{
				CommonMethod.waitForExec(getbankfunc() + "javaposbank.exe SHSD1");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 " + getbankfunc() + "javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 " + getbankfunc() + "javaposbank.exe");
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult()) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

			// 打印签购单
			if (XYKNeedPrintDoc())
			{
				//XYKPrintDoc();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}
	
	public void XYKPrintDoc()
	{
		
	}
	
	protected String getBankClassConfig(String attr)
	{
		if (attr!=null && attr.equalsIgnoreCase("REQCHECKDATETIME")) return "N";//不验证日期格式
		return super.getBankClassConfig(attr);
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector cardtype)
	{
		StringBuffer sbstr = null;

		try
		{
			sbstr = new StringBuffer();
			String cardtypecode = "";

			if (type == PaymentBank.XYKCD)
			{
				sbstr.append("1" + ",");
				sbstr.append("B0" + ",");
				sbstr.append("  " + ",");

				if (cardtype != null && cardtype.size() > 0 && cardtype.elementAt(0).toString().trim().length() >= 2)
				{
					cardtypecode = cardtype.elementAt(0).toString();
					sbstr.append(cardtypecode.substring(cardtypecode.length() - 2) + ",");
				}
				sbstr.append(Convert.increaseChar(GlobalInfo.syjDef.syjh, '0', 6) + ",");
				sbstr.append(Convert.increaseLong(GlobalInfo.syjStatus.fphm, 6));
			}
			else
			{
				// 组织请求数据
				// 操作类型,交易类型,卡类型,收银机编号,操作员,金额,收银流水号,原交易流水号,预留字段
				sbstr.append("0" + ",");
				sbstr.append("A0" + ",");
				if (type == PaymentBank.XYKXF)
					sbstr.append("30" + ",");
				else if (type == PaymentBank.XYKCX)
					sbstr.append("40" + ",");
				else if (type == PaymentBank.XYKTH)
					sbstr.append("50" + ",");
				else if (type == PaymentBank.XYKQD)
					sbstr.append("91" + ",");
				else if (type == PaymentBank.XYKJZ)
					sbstr.append("92" + ",");
				else if (type == PaymentBank.XYKYE)
					sbstr.append("80" + ",");//wangyong add by 2013.9.12 for ZMSY
				else
				{
					throw new Exception("无效的交易类型!");
				}

				if (cardtype != null && cardtype.size() > 0 && cardtype.elementAt(0).toString().trim().length() >= 2)
				{
					cardtypecode = cardtype.elementAt(0).toString();
					sbstr.append(cardtypecode.substring(cardtypecode.length() - 2) + ",");
				}
				else
				{
					sbstr.append("  " + ",");
				}

				sbstr.append(Convert.increaseChar(GlobalInfo.syjDef.syjh, '0', 6) + ",");
				sbstr.append(Convert.increaseChar(GlobalInfo.posLogin.gh, '0', 6) + ",");
				sbstr.append(Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12) + ",");
				sbstr.append(Convert.increaseLong(GlobalInfo.syjStatus.fphm, 6) + ",");
				// 撤销
				if (type == PaymentBank.XYKCX)
				{
					sbstr.append(Convert.increaseCharForward(oldseqno, '0', 6) + ",");
				}
				else
				{
					sbstr.append("000000" + ",");
				}

				// 退货
				if (cardtypecode.length() >= 2 && cardtypecode.substring(cardtypecode.length() - 2).equals("05"))
				{
					if (type == PaymentBank.XYKTH)
					{
						sbstr.append(Convert.increaseChar(oldseqno, 6) + ",");
						sbstr.append(Convert.increaseChar(oldauthno, 15));
					}
					else
					{
						sbstr.append("000000");
						sbstr.append("," + track2);
					}
				}
				else
				{
					if (type == PaymentBank.XYKTH)
					{
						if(oldseqno=="") oldseqno="000000000000";
						sbstr.append(Convert.increaseChar(oldseqno, 12));//6
						olddate = (olddate + "    ").substring(0, 4);
						sbstr.append(Convert.increaseChar(olddate, 4));//10
					}
				}
			}

			// 写入请求数据
			if (!rtf.writeFile(getbankfunc() + "request.txt", sbstr.toString()))
			{
				new MessageBox("写入金卡工程请求数据失败!", null, false);

				return false;
			}
			return true;

		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (sbstr != null)
			{
				sbstr.delete(0, sbstr.length());
				sbstr = null;
			}
		}
	}
}
