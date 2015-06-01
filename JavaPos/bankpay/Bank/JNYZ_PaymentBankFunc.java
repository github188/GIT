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
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
/**
 * 济南银座商城
 * @author Administrator
 *
 */
public class JNYZ_PaymentBankFunc extends PaymentBankFunc {
	
	public String[] getFuncItem()
	{
		String[] func = new String[11];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[4] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "分期付款";
		func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易日结";
		func[7] = "[" + PaymentBank.XKQT2 + "]" + "分期付款撤销";
		func[8] = "[" + PaymentBank.XKQT3 + "]" + "电子现金支付";
		func[9] = "[" + PaymentBank.XKQT4 + "]" + "电子现金查余";
		func[10] = "[" + PaymentBank.XKQT5 + "]" + "电子现金充值";
		
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "检索号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
			case PaymentBank.XKQT1://分期付款
				grpLabelStr[0] = null;
				grpLabelStr[1] = "分期编号";
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
            case PaymentBank.XYKJZ: //结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "结账";
                break;
            case PaymentBank.XKQT2: //分期付款撤销
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = "分期编号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
                break;    
            case PaymentBank.XKQT3://电子现金支付
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
            case PaymentBank.XKQT4: //电子现金查余
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "电子现金查余";
                break;
            case PaymentBank.XKQT5: //电子现金充值
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "电子现金充值";
                break;
			default:
				return false;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX://消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKYE://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始签购单重打";
				break;
			case PaymentBank.XKQT1://分期付款
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XKQT2://分期付款
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			 case PaymentBank.XKQT3://电子现金支付
				 grpTextStr[0] = null;
				 grpTextStr[1] = null;
				 grpTextStr[2] = null;
				 grpTextStr[3] = null;
				 grpTextStr[4] = null;
				break;
			 case PaymentBank.XKQT4://电子现金查余
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = null;
					break;
				case PaymentBank.XKQT5://电子现金充值
					grpTextStr[0] = null;
					grpTextStr[1] = null;
					grpTextStr[2] = null;
					grpTextStr[3] = null;
					grpTextStr[4] = null;
					break;
			default:
				return false;
		}

		return true;
	}
	
	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("000000"))
		{
			bld.retbz = 'Y';
			bld.retmsg = "金卡工程调用成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';

			return false;
		}
	}

	
	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist(ConfigClass.BankPath+"\\result.txt") || ((br = CommonMethod.readFileGBK(ConfigClass.BankPath+"\\result.txt")) == null))
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

			if (!bld.retcode.equals("000000"))
			{
				bld.retmsg = Convert.newSubString(line, 8, 48).trim();
				return true;
			}

			bld.retmsg = Convert.newSubString(line, 8, 48).trim();

			int type = Integer.parseInt(bld.type.trim());

	        // 消费，消费撤销，重打签购单
	        if (type == PaymentBank.XYKJZ)
	        {
	        	return true;
	        }
	        	
			if (Convert.newSubString(line, 48, 54).length() > 0)
			{
				bld.trace = Long.parseLong(Convert.newSubString(line, 48, 54).trim());
			}

			bld.cardno = Convert.newSubString(line, 66, 85).trim();

//			bld.bankinfo = Convert.newSubString(line, 89, 91) + XYKReadBankName(Convert.newSubString(line, 89, 91).trim());
			bld.bankinfo = "金卡工程";

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
				}
				catch (IOException e)
				{
					new MessageBox("result.txt 关闭失败\n重试后如果仍难失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}

	
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD)
					&& (type != PaymentBank.XYKTH) && (type != PaymentBank.XKQT1) && (type != PaymentBank.XYKJZ)&& (type != PaymentBank.XKQT2) && (type != PaymentBank.XKQT3) && (type != PaymentBank.XKQT4)&& (type != PaymentBank.XKQT5))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

		/*	//先删除上次交易数据文件
			if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath+"\\request.txt");

				if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath+"\\result.txt");

				if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}
*/
			//写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			if (bld.retbz != 'Y')
			{
				//调用接口模块
				if (PathFile.fileExist(ConfigClass.BankPath+"\\javaposbank.exe"))
				{
					CommonMethod.waitForExec(ConfigClass.BankPath+"\\javaposbank.exe HZXL");
				}
				else
				{
					new MessageBox("找不到金卡工程模块 javaposbank.exe");
					XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
					return false;
				}

				//读取应答数据
				if (!XYKReadResult()) { return false; }

				// 检查交易是否成功
				XYKCheckRetCode();
			}

			//打印签购单
			if (XYKNeedPrintDoc())
			{
				XYKPrintDoc(type);
			}

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
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
				case PaymentBank.XKQT3: //电子现金支付
					strtypecode = "30";

					break;
				case PaymentBank.XKQT4: //电子现金查余
					strtypecode = "31";

					break;
				case PaymentBank.XKQT5: //电子现金充值
					strtypecode = "34";

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
//			strsyjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, 10);
			strsyjh = Convert.increaseChar("", 10);

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
				pw = CommonMethod.writeFile(ConfigClass.BankPath+"\\request.txt");

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
	
	protected boolean XYKNeedPrintDoc()
	{
        if (!checkBankSucceed())
        {
            return false;
        }

        int type = Integer.parseInt(bld.type.trim());

        // 消费，消费撤销，重打签购单
        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
                (type == PaymentBank.XYKJZ) || (type == PaymentBank.XKQT1)|| (type == PaymentBank.XKQT2))
        {
            return true;
        }
        else
        {
            return false;
        }
	}
	
	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			errmsg = bld.retmsg;

			return false;
		}
		else
		{
			errmsg = "交易成功";

			return true;
		}
	}
	
	public void XYKPrintDoc(int type)
	{
		if (GlobalInfo.sysPara.bankprint <= 0) return;

		ProgressBox pb = null;

		
		try
		{
			
			String fileName="";
			
			if(type == PaymentBank.XYKJZ)
			{
				if (!PathFile.fileExist(ConfigClass.BankPath+"\\bank\\transtotal.txt"))
				{
					new MessageBox("找不到签购单打印文件!");

					return;
				}
				else
				{
					fileName = ConfigClass.BankPath+"\\bank\\transtotal.txt";
				}
			}
			else
			{
				if (!PathFile.fileExist(ConfigClass.BankPath+"\\bank\\receipt.txt"))
				{
					new MessageBox("找不到签购单打印文件!");
	
					return;
				}
				else
				{
					fileName = ConfigClass.BankPath+"\\bank\\receipt.txt";
				}
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				XYKPrintDoc_Start();

				BufferedReader br = null;

				try
				{
					br = CommonMethod.readFileGBK(fileName);

					if (br == null)
					{
						new MessageBox("打开签购单打印文件失败!");

						return;
					}

					//
					String line = null;

					while ((line = br.readLine()) != null)
					{
						/*if (line.length() <= 0)
						{
							continue;
						}*/
						
						if (line.trim().equals("CUT"))
						{					
							// 切纸
							Printer.getDefault().cutPaper_Journal();
							
							continue;
						}
						
						
						Printer.getDefault().printLine_Journal(line + "\n");
					}
				}
				catch (Exception e)
				{
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
					{
						br.close();
					}
				}
				
				Printer.getDefault().cutPaper_Journal();
			}
		}
		catch (Exception ex)
		{
			new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
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
	}
}
