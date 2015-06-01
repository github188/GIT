package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;

public class HzxlNBWQ2_PaymentBankFunc extends HzxlNBWQ1_PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[4] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "分期付款";
		func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易日结";
		func[7] = "[" + PaymentBank.XKQT2 + "]" + "分期付款撤销";
		
		return func;
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String strtypecode = "";
			String strje = "";
			String strseqno = "";
			String strsyjh = "";
			String strsyyh = "";

			String stroldterm = "";

			String strauto = "";
			String strolddate = "";

			String strtermtype = "";

			String strcust = "";

			String strtrack2 = "";
			String strtrack3 = "";

			switch (type)
			{
				case PaymentBank.XYKXF: //消费
					strtypecode = "01";

					break;

				case PaymentBank.XYKCX: //消费撤销
					strtypecode = "02";

					break;

				case PaymentBank.XYKTH: //隔日退货   
					strtypecode = "09";

					break;
				case PaymentBank.XYKYE: //余额查询    
					strtypecode = "03";

					break;

				case PaymentBank.XYKCD: //签购单重打
					strtypecode = "12";

					break;

				case PaymentBank.XKQT1: //分期付款
					strtypecode = "19";

					break;
					
				case PaymentBank.XYKJZ: //交易结账
					strtypecode = "14";

					break;
				case PaymentBank.XKQT2: //分期付款撤销
					strtypecode = "25";

					break;
				default:
					return false;
			}

			// 交易金额
			strje = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12);

			// 流水号
			// 签购单重打
			if (strtypecode.equals("12"))
			{
				if (oldseqno != null)
				{
					strseqno = Convert.increaseChar(oldseqno, 6);
				}
				else
				{
					strseqno = "000000";
				}
			}
			else
			{
				if (oldseqno != null)
				{
					strseqno = Convert.increaseChar(oldseqno, 6);
				}
				else
				{
					strseqno = Convert.increaseChar("", 6);
				}
			}

			// 收银机号
			strsyjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, 10);
			//strsyjh = Convert.increaseChar("", 10);

			// 收银员号
			strsyyh = Convert.increaseChar(GlobalInfo.posLogin.gh, 10);

			// 原终端号
			// 隔日退货
			if (strtypecode.equals("09"))
			{
				if (oldauthno != null)
				{
					stroldterm = Convert.increaseChar(oldauthno, 15);
				}
				else
				{
					stroldterm = Convert.increaseChar("", 15);
				}

				strauto = Convert.increaseChar("", 6);
			}
			else
			{
				if (oldauthno != null)
				{
					strauto = Convert.increaseChar(oldauthno, 6);
				}
				else
				{
					strauto = Convert.increaseChar("", 6);
				}

				stroldterm = Convert.increaseChar("", 15);
			}

			// 原交易日期
			if (olddate != null)
			{
				strolddate = Convert.increaseChar(olddate, 8);
			}
			else
			{
				strolddate = Convert.increaseChar("", 8);
			}

			// 设备类型
			strtermtype = "H";

			// 自定义信息
			String date = "";
			String time = "";
			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKTH || type == PaymentBank.XYKCX)
			{
				ManipulateDateTime mdt = new ManipulateDateTime();
				date = mdt.getDateByEmpty();
				time = mdt.getTimeByEmpty();
			}
			strcust = Convert.increaseChar(date + time, 76);

			// 二磁道
			if (track2 != null)
			{
				strtrack2 = Convert.increaseChar(track2, 37);
			}
			else
			{
				strtrack2 = Convert.increaseChar("", 37);
			}

			// 三磁道
			if (track3 != null)
			{
				strtrack3 = Convert.increaseChar(track3, 104);
			}
			else
			{
				strtrack3 = Convert.increaseChar("", 104);
			}

			// 传入串
			String cmd = strtypecode + strje + strseqno + strsyjh + strsyyh + stroldterm + strauto + strolddate + strtermtype + strcust + strtrack2
					+ strtrack3;

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile("C:\\JavaPos\\request.txt");

				if (pw != null)
				{
					pw.println(cmd);
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}
	
	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败,文件result.txt不存在!");
				new MessageBox("读取金卡工程应答数据失败\n请联系信息部确定当前交易是否成功!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line.length() <= 0) { return false; }
			
			if (line.indexOf(",") < 0) { return false; }
			
			line = line.split(",")[1];
			
			line = "**" + line;

			bld.retcode = Convert.newSubString(line, 2, 8).trim();
			
			//new MessageBox(line+"\n"+bld.retcode);

			if (!bld.retcode.equals("000000"))
			{
				bld.retmsg = Convert.newSubString(line, 8, 48).trim();
				return true;
			}

			bld.retmsg = Convert.newSubString(line, 8, 48).trim();

			int type = Integer.parseInt(bld.type.trim());

	        // 消费，消费撤销，重打签购单
	        if (type == PaymentBank.XYKJZ || type == PaymentBank.XYKYE)
	        {
	        	return true;
	        }
	        	
			if (Convert.newSubString(line, 48, 54).length() > 0)
			{
				bld.trace = Long.parseLong(Convert.newSubString(line, 48, 54).trim());
			}

			bld.cardno = Convert.newSubString(line, 66, 85).trim();

			bld.bankinfo = Convert.newSubString(line, 89, 91) + XYKReadBankName(Convert.newSubString(line, 89, 91).trim());

			errmsg = bld.retmsg;

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());

			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();

//					if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
//					{
//						PathFile.deletePath("C:\\JavaPos\\request.txt");
//					}
//
//					if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
//					{
//						PathFile.deletePath("C:\\JavaPos\\result.txt");
//					}

				}
				catch (IOException e)
				{
					new MessageBox("result.txt 关闭失败\n重试后如果仍难失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean checkBankOperType(int operType, SaleBS saleBS, PaymentBank payObj)
	{
		boolean ok = true;
		
		//正常情况下无法使用，暂时屏蔽
		/**
		if (saleBS != null)
		{
			if (
			// 销售交易或者扣回时,只允许选择0(消费)
			// 退货交易且非扣回时,只允许选择1(撤销),2(退货)
			((SellType.ISSALE(saleBS.saletype) || saleBS.isRefundStatus()) && operType != PaymentBank.XYKXF && operType != PaymentBank.XKQT1)
					|| ((SellType.ISBACK(saleBS.saletype) && !saleBS.isRefundStatus()) && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH) && operType != PaymentBank.XKQT2)
			{
				ok = false;
			}
		}
		else
		{
			if (
			// 删除付款时只允许选择1(撤销),2(退货)
			// 交易红冲时只允许选择1(撤销)
			// 后台退货时只允许选择1(撤销),2(退货)
			// 非小票交易不允许选择0(消费)
			(payObj != null && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH)
					|| (payObj != null && SellType.ISHC(payObj.salehead.djlb) && operType != PaymentBank.XYKCX)
					|| (payObj != null && SellType.ISBACK(payObj.salehead.djlb) && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH)
					|| (operType == PaymentBank.XYKXF && !salebyself))
			{
				ok = false;
			}
		}
		if (!ok)
		{
			new MessageBox("不允许进行该银联操作,请重新选择");
			return false;
		}
		*/
		return true;
	}
}
