package com.efuture.javaPos.Payment.Bank;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Payment.PaymentBank;

/**
 * 
 * 京客隆移动
 * 
 */
public class JklCmcc_PaymentBankFunc extends CMCC_PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[4];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "积分认证";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "积分取消";
		func[2] = "[" + PaymentBank.XKQT1 + "]" + "积分扣款";
		func[3] = "[" + PaymentBank.XKQT2 + "]" + "积分冲正";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
		case PaymentBank.XYKXF: // 积分认证
			grpLabelStr[0] = null;
			grpLabelStr[1] = "密  码";
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "消费金额";
			break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
		case PaymentBank.XYKXF: // 消费
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		}

		return true;
	}

	public String sp_inputPhone(String msg, String tips)
	{
		String cardno = "";
		try
		{

			StringBuffer buf = new StringBuffer();
			if (new TextBox().open(msg, "POS移动业务", tips, buf, 0, 0, false, TextBox.AllInput))
				cardno = buf.toString();

			return cardno;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (type == PaymentBank.XYKXF) // 积分密码认证
			{
				/*
				 * String shopperNo = sp_inputPhone("请顾客在小键盘输入移动积分密码",
				 * "注意：密码长度不得大于20位");
				 * 
				 * if (shopperNo == null)
				 * {
				 * XYKSetError("EF", "失败:移动密码键盘配置不正确.");
				 * return false;
				 * }
				 * shopperNo = shopperNo.trim();
				 */
				if (oldauthno.trim().length() > 20)
				{
					XYKSetError("EF", "失败:移动积分密码输入不合法");
					return false;
				}
				bld.cardno = oldauthno.trim();
			}

			// 写入请求数据
			if (!XYKWriteRequest(null, type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			// 调用接口模块
			if (!XYKExecuteModule(null)) { return false; }

			// 读取应答数据
			if (!XYKReadResult()) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("EX", "移动接口充值异常XX:" + ex.getMessage());
			new MessageBox(this.getErrorMsg(), null, false);

			return false;
		}
	}

	public boolean XYKReadResult()
	{
		try
		{
			// 读取应答数据
			if (!PathFile.fileExist("c:\\javapos\\result.txt") || !rtf.loadFile("c:\\javapos\\result.txt", "GBK"))
			{
				XYKSetError("XX", "读取移动接口应答数据失败!");
				return false;
			}
			String line = rtf.nextRecord();
			rtf.close();

			// 解析应答数据
			String[] ret = line.split("\\,");
			if (ret.length <= 0)
			{
				// 返回数据不合法
				XYKSetError("EF", "移动接口应答数据不合法");
				return false;
			}

			// 先检查init_config函数的返回
			if (!ret[0].trim().equals("0"))
			{
				XYKSetError(ret[0].trim(), "移动接口初始化失败\n" + XYKReadRetMsg(ret[0].trim()));
				return false;
			}

			// 再检查相应交易的函数返回
			bld.retcode = ret[1].trim();
			bld.retmsg = XYKReadRetMsg(bld.retcode);

			// 根据交易类型解析返回数据
			switch (Convert.toInt(bld.type))
			{
			case PaymentBank.XYKXF: // 积分认证
				if (!bld.retcode.equals("0"))
				{
					if (ret.length > 5)
						bld.retmsg += "\n" + ret[5].trim();
					return false;
				}
				if (ret.length <= 4)
				{
					bld.retmsg += "\n应答数据个数不正确[" + ret.length + "]";
					return false;
				}

				double kye = ManipulatePrecision.doubleConvert(Convert.toDouble(ret[2]) / 100);// 券余额
				String cardType = ret[3].trim();// 券类别
				String sponsor = ret[4].trim();// 券发行商

				// 京客隆这边和移动电子券商确认， 001,002为不可找零券，
				// 003,004为可找零券，并且无论是否可找零，都向移动电子卷商发送实际的消费金额
				if (kye >= bld.je || !cardType.equals("003"))
					bld.bankinfo = "券类型：" + cardType + "余额：" + kye + "";

				bld.je = (kye > bld.je ? bld.je : kye);
				bld.memo = cardType + "," + sponsor;// 卡类别+卡发行商

				break;
			case PaymentBank.XYKCX: // 取消认证
			case PaymentBank.XKQT1: // 积分扣款
			case PaymentBank.XKQT2: // 积分冲正
				if (!bld.retcode.equals("0"))
					return false;
				else
					break;
			case PaymentBank.XKQT3: // 商品充值
				if (!bld.retcode.equals("0"))
				{
					if (ret.length > 2)
						bld.retmsg += "\n" + ret[2].trim();
					return false;
				}
				if (ret.length > 2)
					bld.tempstr = ret[2].trim();
				if (ret.length > 3)
					bld.memo = ret[3].trim();
				break;
			case PaymentBank.XKQT4: // 找零充值
				if (!bld.retcode.equals("0"))
				{
					if (ret.length > 2)
						bld.retmsg += "\n" + ret[2].trim();
					return false;
				}
				break;
			case PaymentBank.XKQT5: // 充值记账
			case PaymentBank.XKQT6: // 充值冲正
				if (!bld.retcode.equals("0"))
					return false;
				else
					break;
			default:
				XYKSetError("XX", "[" + bld.type + "]交易类型不支持,解析应答数据失败!");
				new MessageBox(this.getErrorMsg());
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("EX", "读取应答异常:" + ex.getMessage());
			new MessageBox(this.getErrorMsg());
			return false;
		}
	}
}
