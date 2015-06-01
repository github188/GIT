package com.efuture.javaPos.Payment.Bank;

import java.io.File;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;

import device.ICCard.KTL512V;
import device.ICCard.NT512;

public class CMCC_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[9];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "积分认证";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "积分取消";
		func[2] = "[" + PaymentBank.XKQT1 + "]" + "积分扣款";
		func[3] = "[" + PaymentBank.XKQT2 + "]" + "积分冲正";
		func[4] = "[" + PaymentBank.XKQT3 + "]" + "商品充值";
		func[5] = "[" + PaymentBank.XKQT4 + "]" + "找零充值";
		func[6] = "[" + PaymentBank.XKQT5 + "]" + "充值记账";
		func[7] = "[" + PaymentBank.XKQT6 + "]" + "充值冲正";
		func[8] = "[" + PaymentBank.XYKQD + "]" + "移动初始化";

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
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "消费金额";
			break;
		default:
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = null;
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
			grpTextStr[3] = "*按回车后开始积分密码认证*";
			grpTextStr[4] = null;
			break;
		default:
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		}

		return true;
	}

	public boolean XYKCheckRetCode()
	{
		// 根据返回值置返回标志
		if (bld.retcode.trim().equals("0"))
		{
			bld.retbz = 'Y';
			if (bld.retmsg == null || bld.retmsg.trim().equals(""))
				bld.retmsg = "移动积分接口调用成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';

			return false;
		}
	}

	public String XYKReadRetMsg(String retcode)
	{
		String msg = "未知错误";
		try
		{
			int ret = Convert.toInt(retcode);
			switch (ret)
			{
			case 0:
				msg = "成功";
				break;
			case 1:
				msg = "小票号或柜员编号超长";
				break;
			case 2:
				msg = "网络错误";
				break;
			case 3:
				msg = "信息发送失败";
				break;
			case 4:
				msg = "信息接收失败";
				break;
			case 5:
				msg = "前置系统处理失败";
				break;
			case 6:
				msg = "充值失败";
				break;
			case 9:
				msg = "服务器不可用";
				break;
			default:
				break;
			}
			if (ret != 0)
				msg = "[" + retcode + "]" + msg;

			return msg;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return msg + "|" + ex.getMessage();
		}
	}

	public String sp_inputPhone(String msg)
	{
		return sp_inputPhone(msg, "");
	}

	public String sp_inputPhone(String msg, String tips)
	{
		ProgressBox pb = null;
		String cardno = null;
		try
		{
			pb = new ProgressBox();
			pb.setText(msg);

			// 开发模式调试
			if (ConfigClass.isDeveloperMode())
			{
				StringBuffer buf = new StringBuffer();
				if (new TextBox().open(msg, "手机号码", "仅供程序开发调试使用！！！", buf, 0, 0, false, TextBox.AllInput))
				{
					cardno = buf.toString();
				}
				else
					new MessageBox("移动密码键盘配置不正确,请联系电脑部!", null, false);
			}
			else
			{
				if (ConfigClass.CustomItem4 != null && ConfigClass.CustomItem4.toString().split("\\,")[0].toString().equals("0"))
				{
					StringBuffer buf = new StringBuffer();
					if (new TextBox().open(msg, "POS移动业务", tips, buf, 0, 0, false, TextBox.AllInput))
						cardno = buf.toString();
				}
				else if (ConfigClass.CustomItem4 != null && ConfigClass.CustomItem4.toString().split("\\,")[0].toString().equals("1"))
				{
					cardno = new KTL512V().findCard();
				}
				else if (ConfigClass.CustomItem4 != null && ConfigClass.CustomItem4.toString().split("\\,")[0].toString().equals("2"))
				{
					cardno = new NT512().findCard();
				}
				else
				{
					if (ICCard.getDefault() != null)
						cardno = ICCard.getDefault().findCard();
					else
						new MessageBox("移动密码键盘配置不正确,请联系电脑部!", null, false);
				}
			}

			return cardno;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (type == PaymentBank.XYKXF) // 积分密码认证
			{
				String shopperNo = null;

				if (ConfigClass.CustomItem4 != null && ConfigClass.CustomItem4.toString().split("\\,")[0].toString().equals("0"))
					shopperNo = sp_inputPhone("请顾客在小键盘输入移动积分密码", "注意：密码长度不得小于18位");
				else
					shopperNo = sp_inputPhone("请顾客在小键盘输入移动积分密码...");

				if (shopperNo == null)
				{
					XYKSetError("EF", "失败:移动密码键盘配置不正确.");
					return false;
				}
				shopperNo = shopperNo.trim();
				if (shopperNo.length() != 18)
				{
					XYKSetError("EF", "失败:移动积分密码输入不合法.");
					return false;
				}
				bld.cardno = shopperNo;
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

	protected boolean XYKExecuteModule(String cmdline)
	{
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText("正在执行" + getFuncItemDesc(bld.type, true) + "业务处理,请等待...");

			// 调用接口模块
			if (PathFile.fileExist("c:\\javapos\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("c:\\javapos\\javaposbank.exe DFHQ");
			}
			else
			{
				pb.close();
				pb = null;
				XYKSetError("XX", "找不到移动积分调用模块 javaposbank.exe");
				new MessageBox(this.getErrorMsg());
				return false;
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
		return false;
	}

	protected boolean XYKWriteRequest(StringBuffer arg, int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\javapos\\request.txt"))
			{
				PathFile.deletePath("c:\\javapos\\request.txt");

				if (PathFile.fileExist("c:\\javapos\\request.txt"))
				{
					XYKSetError("XX", "交易请求文件request.txt无法删除,请重试");
					new MessageBox(this.getErrorMsg());
					return false;
				}
			}

			if (PathFile.fileExist("c:\\javapos\\result.txt"))
			{
				PathFile.deletePath("c:\\javapos\\result.txt");

				if (PathFile.fileExist("c:\\javapos\\result.txt"))
				{
					XYKSetError("XX", "交易请求文件result.txt无法删除,请重试");
					new MessageBox(this.getErrorMsg());
					return false;
				}
			}

			// 生成请求数据
			// configPara=前置系统IP地址,前置系统得端口号,SOCKET的超时(以秒为单位),冲正超时时间,集团编号,门店编号
			StringBuffer reqstr = new StringBuffer();
			String initstr = ConfigClass.CustomItem3.trim() + "," + GlobalInfo.syjDef.syjh;
			switch (type)
			{
			case PaymentBank.XYKQD: // 初始化
				reqstr.append("1," + initstr);
				break;
			case PaymentBank.XYKXF: // 积分认证
				reqstr.append("7," + initstr);
				reqstr.append("," + bld.fphm + "," + bld.syyh + "," + bld.cardno);
				break;
			case PaymentBank.XYKCX: // 取消认证
				reqstr.append("8," + initstr);
				reqstr.append("," + track1 + "," + track2 + "," + track3);
				bld.cardno = track3;
				break;
			case PaymentBank.XKQT1: // 积分扣款
				reqstr.append("9," + initstr);
				reqstr.append("," + track1 + "," + track2 + "," + track3);
				reqstr.append("," + String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)));
				bld.cardno = track3;
				break;
			case PaymentBank.XKQT2: // 积分冲正
				reqstr.append("10," + initstr);
				reqstr.append("," + track1 + "," + track2);
				break;
			case PaymentBank.XKQT3: // 商品充值
				reqstr.append("3," + initstr);
				reqstr.append("," + track1 + "," + track2);
				reqstr.append("," + memo.elementAt(0) + "," + memo.elementAt(1));
				reqstr.append("," + memo.elementAt(2) + "," + memo.elementAt(3));
				break;
			case PaymentBank.XKQT4: // 找零充值
				reqstr.append("4," + initstr);
				reqstr.append("," + track1 + "," + track2);
				reqstr.append("," + memo.elementAt(0));
				break;
			case PaymentBank.XKQT5: // 充值记账
				reqstr.append("5," + initstr);
				reqstr.append("," + track1 + "," + track2);
				reqstr.append("," + memo.elementAt(0) + "," + memo.elementAt(1) + "," + memo.elementAt(2));
				break;
			case PaymentBank.XKQT6: // 充值冲正
				reqstr.append("6," + initstr);
				reqstr.append("," + track1 + "," + track2);
				reqstr.append("," + memo.elementAt(0) + "," + memo.elementAt(1) + "," + memo.elementAt(2));
				break;
			default:
				XYKSetError("XX", "[" + type + "]交易类型不支持,写入请求数据失败!");
				new MessageBox(this.getErrorMsg());
				return false;
			}

			// 写入数据
			if (!rtf.writeFile("c:\\javapos\\request.txt", reqstr.toString(), "GBK"))
			{
				XYKSetError("XX", "写入交易请求数据失败!");
				new MessageBox(this.getErrorMsg());
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("写入交易请求数据异常!\n\n" + ex.getMessage(), null, false);

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
				XYKSetError(ret[0].trim(), "移动接口初始化失败|" + XYKReadRetMsg(ret[0].trim()));
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
						bld.retmsg += "|" + ret[5].trim();
					return false;
				}
				if (ret.length <= 4)
				{
					bld.retmsg += "|应答数据个数不正确[" + ret.length + "]";
					return false;
				}

				double kye = ManipulatePrecision.doubleConvert(Convert.toDouble(ret[2]) / 100);// 券余额
				String cardType = ret[3].trim();// 券类别
				String sponsor = ret[4].trim();// 券发行商
				String shopperNo = bld.cardno;// 积分密码
				if (!cardType.equals("003"))
				{
					// 不可找零券
					bld.je = kye;
					bld.memo = cardType + "," + sponsor;// 卡类别+卡发行商
				}
				else
				{
					// 可找零券
					double maxje = (kye > bld.je ? bld.je : kye);
					StringBuffer sbje = new StringBuffer();
					sbje.append(maxje);
					String help = "[" + shopperNo + "]积分券余额为 " + ManipulatePrecision.doubleToString(kye) + " 元\n本次最多消费 " + ManipulatePrecision.doubleToString(maxje) + " 元";
					while (true)
					{
						if (new TextBox().open("积分密码认证成功,请输入要消费的金额", "消费金额", help, sbje, 0.01, maxje, true, TextBox.DoubleInput))
						{
							bld.je = Convert.toDouble(sbje.toString().trim());
							bld.memo = cardType + "," + sponsor;// 卡类别+卡发行商
							break;
						}
					}
				}
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
						bld.retmsg += "|" + ret[2].trim();
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
						bld.retmsg += "|" + ret[2].trim();
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

	// 取消
	public boolean sp_cancelShopper(long fphm, String syyh, String shopperNo)
	{
		int sendCancelNum = 0;
		while (true)
		{
			// 发送取消认证
			if (this.callBankFunc(PaymentBank.XYKCX, 0, String.valueOf(fphm), syyh, shopperNo, null, null, null, null))
				break;
			new MessageBox("取消积分认证时失败,将再次重试\n\n" + bld.retmsg);

			// 发送3次及以上仍失败时,提示是否强制退出界面
			sendCancelNum++;
			if (sendCancelNum >= 3 && new MessageBox("[" + shopperNo + "]积分券\n\n取消积分认证已发送 " + sendCancelNum + " 次均失败\n\n是否不再发送取消命令,该券将在第二天自动解锁？", null, true).verify() == GlobalVar.Key1)
			{
				break;
			}
		}
		return true;
	}

	// 提交
	public boolean sp_commitShopper(long fphm, String syyh, String shopperNo, double paymoney)
	{
		// 记录冲正文件
		String file = ConfigClass.LocalDBPath + "/YDCZ_JF_" + fphm + ".dat";
		if (!PathFile.fileExist(file))
		{
			Vector data = new Vector();
			data.add(String.valueOf(fphm));
			data.add(syyh);
			if (!CommonMethod.writeObjectToFile(file, data))
			{
				new MessageBox("写入积分支付冲正文件失败");
				return false;
			}
		}

		// 发起记账
		if (this.callBankFunc(PaymentBank.XKQT1, paymoney, String.valueOf(fphm), syyh, shopperNo, null, null, null, null))
			return true;
		else
		{
			new MessageBox("提交积分扣款时失败,稍后将自动冲正\n\n" + bld.retmsg);
			return false;
		}
	}

	// 冲正
	public boolean sp_reversalShopper(long fphm, String syyh)
	{
		int sendCancelNum = 0;
		while (true)
		{
			// 发送取消认证
			if (this.callBankFunc(PaymentBank.XKQT2, 0, String.valueOf(fphm), syyh, null, null, null, null, null))
				return true;
			new MessageBox("冲正积分券时失败,将再次重试\n\n" + bld.retmsg);

			// 发送3次及以上仍失败时,提示是否强制退出界面
			sendCancelNum++;
			if (sendCancelNum >= 3 && new MessageBox("[" + fphm + "]小票的积分券冲正 " + sendCancelNum + " 次均失败\n\n是否不再发送冲正命令,稍后重新尝试冲正？", null, true).verify() == GlobalVar.Key1)
			{
				break;
			}
		}
		return false;
	}

	public boolean sp_questGoods(long fphm, String syyh, int offlinenum, String offlinegoods, int onlinenum, String onlinegoods, StringBuffer passwd, StringBuffer memo)
	{
		Vector data = new Vector();
		data.add(String.valueOf(offlinenum));
		data.add(offlinegoods);
		data.add(String.valueOf(onlinenum));
		data.add(onlinegoods);

		boolean ret = this.callBankFunc(PaymentBank.XKQT3, 0, String.valueOf(fphm), syyh, null, null, null, null, data);
		if (ret)
		{
			// 生成离线充值密码文件,以便小票打印
			if (offlinenum > 0 && bld.tempstr != null && bld.tempstr.trim().length() > 0)
			{
				if (passwd != null)
					passwd.append(bld.tempstr);
				if (memo != null)
					memo.append(bld.memo);

				data.removeAllElements();
				data.add(bld.tempstr); // 密码
				data.add(bld.memo); // 描述
				if (!CommonMethod.writeObjectToFile(ConfigClass.LocalDBPath + "/YDLXCZ_" + fphm + ".dat", data))
				{
					new MessageBox("离线充值密码数据写盘失败!");
					return false;
				}
			}
		}
		else
		{
			new MessageBox("申请商品充值时失败\n\n" + bld.retmsg);
		}
		return ret;
	}

	public boolean sp_questChange(long fphm, String syyh, String chggoods)
	{
		Vector data = new Vector();
		data.add(chggoods);

		if (this.callBankFunc(PaymentBank.XKQT4, 0, String.valueOf(fphm), syyh, null, null, null, null, data))
			return true;
		else
		{
			new MessageBox("申请找零充值时失败\n\n" + bld.retmsg);
			return false;
		}
	}

	public boolean sp_commitGoods(long fphm, String syyh, int questgoods, int questchange)
	{
		Vector data = new Vector();
		data.add("1");
		data.add(String.valueOf(questgoods));
		data.add(String.valueOf(questchange));
		data.add(String.valueOf(fphm));
		data.add(String.valueOf(syyh));

		// 记录冲正文件
		String file = ConfigClass.LocalDBPath + "/YDCZ_CZ_" + fphm + ".dat";
		if (!CommonMethod.writeObjectToFile(file, data))
		{
			new MessageBox("写入移动充值冲正文件失败");
			return false;
		}

		// 发起记账
		boolean ret = this.callBankFunc(PaymentBank.XKQT5, 0, String.valueOf(fphm), syyh, null, null, null, null, data);
		if (ret)
			return ret;
		else
		{
			new MessageBox("充值记账时失败,稍后将尝试冲正\n\n" + bld.retmsg);
		}

		/*
		 * 只要失败都发起冲正
		 * // 无需发起充值冲正,删除冲正文件
		 * if (bld.retcode.equals("6") || bld.retcode.equals("9"))
		 * {
		 * sp_deleAccountCz(file);
		 * new MessageBox("提交充值记账时失败\n\n"+bld.retmsg);
		 * return false;
		 * }
		 */
		// 发起冲正
		int sendCancelNum = 0;
		while (true)
		{
			// 如果冲正成功则表示记账失败
			if (sp_reversalGoods(fphm, syyh, "1", questgoods, questchange))
			{
				sp_deleAccountCz(file);
				new MessageBox("充值记账已成功冲正\n\n请重新确认交易,尝试重新充值");
				return false;
			}

			// 冲正3次失败则认为成功先收钱进来再允许顾客去补充值
			sendCancelNum++;
			if (sendCancelNum >= 3)
				break;
		}

		// 待小票存盘后清除冲正文件
		new MessageBox("充值记账冲正3次均失败,但无法确定后台是否已充值\n\n先成交交易,若未充值请顾客凭小票至服务台补充");
		return true;
	}

	public boolean sp_reversalGoods(long fphm, String syyh, String paytype, int questgoods, int questchange)
	{
		Vector data = new Vector();
		data.add(paytype);
		data.add(String.valueOf(questgoods));
		data.add(String.valueOf(questchange));

		// 发起冲正
		if (this.callBankFunc(PaymentBank.XKQT6, 0, String.valueOf(fphm), syyh, null, null, null, null, data))
			return true;
		else
		{
			new MessageBox("移动充值冲正失败\n\n" + bld.retmsg);
			return false;
		}
	}

	// 发送所有未发送冲正
	public boolean sp_sendAccountCz()
	{
		boolean ok = false;
		ProgressBox pb = null;

		try
		{

			pb = new ProgressBox();
			pb.setText("正在检查移动积分充值冲正数据,请等待......");

			File file = new File(ConfigClass.LocalDBPath);
			File[] filename = file.listFiles();

			for (int i = 0; i < filename.length; i++)
			{
				if (filename[i].getName().startsWith("YDCZ_JF_") || filename[i].getName().startsWith("YDCZ_CZ_"))
				{
					// 读取文件
					String name = filename[i].getAbsolutePath();

					// 检查文件是否为未删除文件
					File a = new File(ConfigClass.LocalDBPath + "/DEL_" + filename[i].getName());
					if (a.exists())
					{
						// 删除冲正文件
						sp_deleAccountCz(name);
						a.delete();
						continue;
					}

					// 显示冲正进度提示			
					pb.setText("正在发送移动积分充值冲正数据...");

					// 读取冲正数据
					Vector data = CommonMethod.readObjectFormFile(name);
					if (data == null)
					{
						new MessageBox("读取移动冲正文件数据失败!");
						return false;
					}

					// 发送冲正
					if (filename[i].getName().startsWith("YDCZ_JF_"))
					{
						long fphm = Convert.toLong(data.elementAt(0));
						String syyh = (String) data.elementAt(1);

						if (!this.sp_reversalShopper(fphm, syyh))
							return false;
					}
					else
					{
						String paytype = (String) data.elementAt(0);
						int questgoods = Convert.toInt(data.elementAt(1));
						int questchange = Convert.toInt(data.elementAt(2));
						long fphm = Convert.toLong(data.elementAt(3));
						String syyh = (String) data.elementAt(4);

						if (!this.sp_reversalGoods(fphm, syyh, paytype, questgoods, questchange))
							return false;
					}

					// 删除冲正文件
					sp_deleAccountCz(name);
				}
			}

			ok = true;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("冲正发生异常\n\n" + e.getMessage());
			return false;
		}
		finally
		{
			if (pb != null)
				pb.close();
			if (!ok)
			{
				new MessageBox("有冲正数据未发送,不能进行移动积分或充值交易!");
			}
		}
	}

	public boolean sp_deleAccountCz(String name)
	{
		try
		{
			if (name == null)
			{
				File file = new File(ConfigClass.LocalDBPath);
				File[] filename = file.listFiles();

				for (int i = 0; i < filename.length; i++)
				{
					if (filename[i].getName().startsWith("YDCZ_JF_") || filename[i].getName().startsWith("YDCZ_CZ_"))
					{
						sp_deleAccountCz(filename[i].getAbsolutePath());
					}
				}

				return true;
			}
			else
			{
				File file = new File(name);
				if (file.exists())
				{
					file.delete();
					if (file.exists())
					{
						new MessageBox("冲正文件没有被删除,请检查磁盘!");

						// 加入日志
						AccessDayDB.getDefault().writeWorkLog("冲正文件 " + name + " 没有删除成功");

						// 在本地标记此冲正文件需要被删除，待重启删除被占用的文件
						File a = new File(ConfigClass.LocalDBPath + "/DEL_" + name);
						a.createNewFile();

						return false;
					}
					else
					{
						return true;
					}
				}
				else
				{
					return true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			new MessageBox("删除冲正文件失败!\n\n" + e.getMessage());

			return false;
		}
	}

	public boolean sp_printOfflineChargeBill(long fphm)
	{
		String file = ConfigClass.LocalDBPath + "/YDLXCZ_" + fphm + ".dat";
		if (!PathFile.fileExist(file))
			return false;

		Vector data = CommonMethod.readObjectFormFile(file);
		if (data == null)
		{
			new MessageBox("读取离线充值密码文件失败!\n\n请顾客凭小票至服务台补打充值");
			return false;
		}

		// 只打印一次，删除离线充值密码文件
		PathFile.deletePath(file);

		// 打印
		try
		{
			Printer.getDefault().startPrint_Journal();
			String[] s = ((String) data.elementAt(0)).split("\\|");
			for (int i = 0; i < s.length; i++)
			{
				String[] s1 = s[i].split(":");
				String mode = "离线充值: ";
				if (s1[0].equals("2"))
					mode = "回馈充值: ";
				Printer.getDefault().printLine_Journal(mode + ManipulatePrecision.doubleToString(Convert.toDouble(s1[1]) / 100));
				Printer.getDefault().printLine_Journal("充值密码: " + s1[2]);
			}
			Printer.getDefault().printLine_Journal((String) data.elementAt(1));
			Printer.getDefault().cutPaper_Journal();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("打印离线充值密码发生异常\n\n" + ex.getMessage() + "\n\n请顾客凭小票至服务台补打充值");
		}
		return true;
	}
}
