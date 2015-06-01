package custom.localize.Jcgj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Jcgj_Svc
{
	private String methordName = "";
	private SaleHeadDef saleHead = null;
	private String cardSell = "";
	public static boolean isInitSucc = false;
	//	public static CustomerDef ysCust = null;
	public static int inputType = -2;

	public Jcgj_Svc(String methordName, SaleHeadDef saleHead, String cardSell)
	{
		this.methordName = methordName;
		if (saleHead != null) this.saleHead = saleHead;
		if (cardSell != null && cardSell.length() > 0) this.cardSell = cardSell;
	}

	public boolean writeRequest()
	{
		// 删除之前生成的请求和应答文件
		if (!deleteFile()) return false;

		String req = "";

		// 查询会员卡函数 或者 通过手机号查询会员卡函数
		if (methordName.equalsIgnoreCase("svc_inq") || methordName.equalsIgnoreCase("svc_inq_by_phone") || methordName.equalsIgnoreCase("svc_inq_vip"))
		{
			if (cardSell.length() < 1)
			{
				req = methordName + "," + Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10)
				+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10);
			}
			else
			{
				req = methordName + "," + cardSell;
			}
		}
		// 会员卡初始化函数
		else if (methordName.equalsIgnoreCase("svc_init"))
		{
			req = methordName;
		}
		// 积分累计函数
		else if (methordName.equalsIgnoreCase("svc_score"))
		{
			req = methordName + "," + Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10) // 收银机号
					+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10) // 收银员号
					+ Convert.increaseChar(String.valueOf(saleHead.fphm), ' ', 20) // 小票号
					+ Convert.increaseChar(saleHead.hykh, ' ', 19) // 会员卡号
					+ Convert.increaseChar(String.valueOf((long) ManipulatePrecision.doubleConvert(saleHead.bcjf * 100, 2, 1)), ' ', 12) // 本次积分
					+ Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(saleHead.hjzje * 100, 2, 1)), '0', 12) // 合计金额
					+ Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(saleHead.hjzke * 100, 2, 1)), '0', 12); // 合计折扣
		}
		// 面值卡消费函数
		else if (methordName.equalsIgnoreCase("svc_nsale"))
		{
			req = methordName + "," + cardSell;
		}
		// 积分换购函数
		else if (methordName.equalsIgnoreCase("svc_use_score"))
		{
			req = methordName + "," + cardSell;
		}
		// 零钞转存函数
		else if (methordName.equalsIgnoreCase("svc_change"))
		{
			req = methordName + "," + cardSell;
		}
		// 卡系统操作提交
		else if (methordName.equalsIgnoreCase("svc_commit"))
		{
			req = methordName;
		}
		// 整单撤销
		else if (methordName.equalsIgnoreCase("svc_void_sale"))
		{
			req = methordName + "," + Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10)
					+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10)
					+ Convert.increaseChar(String.valueOf(ManipulatePrecision.doubleConvert((saleHead.hjzje - saleHead.hjzke) * 100, 2, 1)), ' ', 12)
					+ Convert.increaseChar(String.valueOf(saleHead.fphm), ' ', 20);
		}
		// 预售卡
		else if (methordName.equalsIgnoreCase("svc_pre_card_sale"))
		{
			req = methordName + "," + cardSell;
		}
		// 预售卡
		else if (methordName.equalsIgnoreCase("svc_card_sale"))
		{
			req = methordName + "," + cardSell;
		}
		else
		{
			new MessageBox("不存在的接口函数：" + methordName);
			return false;
		}

		PrintWriter pw = null;
		try
		{
			pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");

			if (pw != null)
			{
				pw.print(req);
				pw.flush();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(methordName + " 写入请求文件时发生异常" + e.getMessage());
			return false;
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

	public boolean execSvc()
	{
		// 调用接口
		try
		{
			if (this.methordName.equals("svc_inq") || this.methordName.equals("svc_inq_by_phone") || this.methordName.equals("svc_pre_card_sale") || this.methordName.equals("svc_inq_vip"))
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\JavaPosCard.exe SVC", "JavaPosCard.exe");
			}
			else
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\JavaPosCard.exe SVC");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(methordName + " 调用接口时发生异常" + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean readResult(Jcgj_YsCardDef card)
	{
		// 读取应答
		BufferedReader br = null;
		try
		{
			if (!PathFile.fileExist(ConfigClass.BankPath + "\\result.txt")
					|| ((br = CommonMethod.readFileGBK(ConfigClass.BankPath + "\\result.txt")) == null))
			{
				new MessageBox(methordName + " 读取应答数据失败");

				return false;
			}

			// 读取应答数据
			String line = br.readLine();
			if (line != null)
			{
				String[] ret = line.split(",");
				if (ret.length < 2)
				{
					new MessageBox(methordName + " 返回数据格式不正确");
					return false;
				}
				else if (!ret[0].equalsIgnoreCase(methordName))
				{
					new MessageBox(methordName + " 应答函数与请求函数不符");
					return false;
				}

				// 调用函数返回成功
				if (ret[1].equals("0"))
				{
					// 通过手机号查询会员卡函数
					// 查询会员卡函数
					if (ret[0].equalsIgnoreCase("svc_inq") || ret[0].equalsIgnoreCase("svc_inq_by_phone") || ret[0].equalsIgnoreCase("svc_inq_vip"))
					{
						String retCode = Convert.newSubString(ret[2], 0, 2);
						String retMsg = Convert.newSubString(ret[2], 2, 22);
						if (retCode.equals("00"))
						{
							card.cardNo = Convert.newSubString(ret[2], 22, 41);
							card.cvn2 = Convert.newSubString(ret[2], 41, 44);
							card.type = Convert.newSubString(ret[2], 44, 46);
							card.ye = Double.parseDouble(Convert.newSubString(ret[2], 46, 58)) / 100;
							card.jf = Double.parseDouble(Convert.newSubString(ret[2], 58, 70)) / 100;
							card.ljjf = Convert.newSubString(ret[2], 70, 82);
							card.sqjf = Convert.newSubString(ret[2], 82, 94);
							card.name = Convert.newSubString(ret[2], 94, 114);
							card.level = Convert.newSubString(ret[2], 114, 115);
							card.birthday = Convert.newSubString(ret[2], 115, 123);
							card.validate = Convert.newSubString(ret[2], 123, 131);
							card.jfzq = Convert.newSubString(ret[2], 131, 139);
							card.jfxs = Convert.newSubString(ret[2], 139, 143);
							card.yemx = Convert.newSubString(ret[2], 143, 565);
							return true;
						}
						else
						{
							new MessageBox(ret[0] + " " + retMsg);
							return false;
						}
					}
					// 整单撤销函数
					else if (ret[0].equalsIgnoreCase("svc_void_sale"))
					{
						String retCode = Convert.newSubString(ret[2], 0, 2);
						String retMsg = Convert.newSubString(ret[2], 2, 22);
						if (retCode.equals("00"))
						{
							return true;
						}
						else
						{
							new MessageBox(ret[0] + " " + retMsg);
							return false;
						}
					}
					// 会员卡初始化函数
					else if (ret[0].equalsIgnoreCase("svc_init"))
					{
						return true;
					}
					// 积分函数
					else if (ret[0].equalsIgnoreCase("svc_score"))
					{
						return true;
					}
					// 面值卡消费函数
					else if (ret[0].equalsIgnoreCase("svc_nsale"))
					{
						return true;
					}
					// 积分消费函数
					else if (ret[0].equalsIgnoreCase("svc_use_score"))
					{
						return true;
					}
					// 卡系统操作提交
					else if (ret[0].equalsIgnoreCase("svc_commit"))
					{
						return true;
					}
					// 零钞转存
					else if (ret[0].equalsIgnoreCase("svc_change"))
					{
						return true;
					}
					// 预售卡
					else if (ret[0].equalsIgnoreCase("svc_pre_card_sale"))
					{
						String retCode = Convert.newSubString(ret[2], 0, 2);
						String retMsg = Convert.newSubString(ret[2], 2, 22);
						if (retCode.equals("00"))
						{
							card.cardNo = Convert.newSubString(ret[2], 22, 41);
							return true;
						}
						else
						{
							new MessageBox(ret[0] + " " + retMsg);
							return false;
						}
					}
					// 售卡通知
					else if (ret[0].equalsIgnoreCase("svc_card_sale"))
					{
						String retCode = Convert.newSubString(ret[2], 0, 2);
						String retMsg = Convert.newSubString(ret[2], 2, 22);
						if (retCode.equals("00") || retCode.equals("S0"))
						{
							return true;
						}
						else
						{
							new MessageBox(ret[0] + " " + retMsg);
							return false;
						}
					}
					else
					{
						new MessageBox("不存在的接口函数：" + methordName);
						return false;
					}
				}
				else
				{
					String error = "";
					error = ret[0] + " 函数返回值不正确\n" + ret[1];
					if (ret.length > 2 && ret[2].trim().length() > 0) error = error + " " + ret[2];
					new MessageBox(error);
					return false;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(methordName + " 读取应答数据时发生异常" + e.getMessage());
			return false;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
					if (!deleteFile()) return false;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public String getMethordName()
	{
		return methordName;
	}

	public boolean deleteFile()
	{
		if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
		{
			PathFile.copyPath(ConfigClass.BankPath + "\\request.txt", ConfigClass.BankPath + "\\LastRequest.txt");
			PathFile.deletePath(ConfigClass.BankPath + "\\request.txt");

			if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
			{
				new MessageBox(methordName + " 交易请求文件request.TXT无法删除,请重试");
				return false;
			}
		}

		if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
		{
			PathFile.copyPath(ConfigClass.BankPath + "\\result.txt", ConfigClass.BankPath + "\\LastResult.txt");
			PathFile.deletePath(ConfigClass.BankPath + "\\result.txt");

			if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
			{
				new MessageBox(methordName + " 交易请求文件result.txt无法删除,请重试");
				return false;
			}
		}
		return true;
	}

	public boolean doYsCard(Jcgj_YsCardDef card)
	{
		if (!methordName.equalsIgnoreCase("svc_init") && !isInitSucc)
		{
			new MessageBox(methordName + " YS卡系统没有初始化！无法进行卡交易");
			return false;
		}
		if (writeRequest() && execSvc() && readResult(card)) return true;
		else return false;
	}
}
