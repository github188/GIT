package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
/**
 * 天水建行
 * @author Administrator
 *
 */
public class Tsjh_PaymentBankFunc extends PaymentBankFunc {
	
	public String[] getFuncItem()
	{
		String[] func = new String[11];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[4] = "[" + PaymentBank.XYKCD + "]" + "重打印上一笔";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "分期付款";
		func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易日结";
		func[7] = "[" + PaymentBank.XKQT2 + "]" + "分期付款撤销";
		func[8] = "[" + PaymentBank.XKQT3 + "]" + "分期付款退货";
		func[9] = "[" + PaymentBank.XKQT4 + "]" + "重打印任意一笔";
		func[10] = "[" + PaymentBank.XYKQD + "]" + "签到";
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
				grpLabelStr[0] = "原票据号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原票据号";
				grpLabelStr[1] = "参考号";
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
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
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
                grpLabelStr[0] = "原票据号";
                grpLabelStr[1] = "分期编号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
                break;
            case PaymentBank.XKQT3: //分期付款退货
                grpLabelStr[0] = "原票据号";
                grpLabelStr[1] = "分期编号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
                break;
            case PaymentBank.XKQT4://重打印任意一笔
				grpLabelStr[0] = "原票据号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打印任意一笔";
				break;
            case PaymentBank.XYKQD://签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "签到";
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
			case PaymentBank.XKQT2://分期付款撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打印任意一笔";
				break;
			case PaymentBank.XKQT3://分期付款退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打印任意一笔";
				break;
			case PaymentBank.XKQT4://重打印任意一笔
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打印任意一笔";
				break;
			case PaymentBank.XYKQD://签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始签到";
				break;
			default:
				return false;
		}

		return true;
	}
	
	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("00"))
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
			
			String result[] = line.split(",");

			bld.retcode = result[0];

			if (!bld.retcode.equals("00"))
			{
				bld.retmsg = result[1];
				return false;
			}

			bld.retmsg = result[1];
			
			bld.type = result[2];

			bld.cardno = result[7];

			bld.bankinfo = result[4];

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
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XYKQD)
					&& (type != PaymentBank.XYKTH) && (type != PaymentBank.XKQT1) && (type != PaymentBank.XYKJZ) && (type != PaymentBank.XKQT2) && (type != PaymentBank.XKQT3) && (type != PaymentBank.XKQT4))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			//先删除上次交易数据文件
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

			//写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			if (bld.retbz != 'Y')
			{
				//调用接口模块
				if (PathFile.fileExist(ConfigClass.BankPath+"\\javaposbank.exe"))
				{
					CommonMethod.waitForExec(ConfigClass.BankPath+"\\javaposbank.exe TSJH");
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
			if ((type != PaymentBank.XYKQD) && (type !=PaymentBank.XYKYE))
			{
				if (XYKNeedPrintDoc())
				{
					XYKPrintDoc();
				}
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

			String stroldterm = "";

			String strolddate = "";

			switch (type)
			{
			case PaymentBank.XYKQD: //签到
				strtypecode = "00";

				break;

				case PaymentBank.XYKXF: //消费
					strtypecode = "03";

					break;

				case PaymentBank.XYKCX: //消费撤销
					strtypecode = "04";

					break;

				case PaymentBank.XYKTH: //隔日退货   
					strtypecode = "05";

					break;
				case PaymentBank.XYKYE: //余额查询    
					strtypecode = "06";

					break;

				case PaymentBank.XYKCD: //签购单重打
				case PaymentBank.XKQT4: //重打任意笔
					strtypecode = "20";

					break;

				case PaymentBank.XKQT1: //分期付款
					strtypecode = "14";

					break;
					
				case PaymentBank.XYKJZ: //交易结账
					strtypecode = "02";

					break;
				case PaymentBank.XKQT2: //分期付款撤销
					strtypecode = "15";

					break;
				case PaymentBank.XKQT3: //分期付款退货
					strtypecode = "16";

					break;
				default:
					return false;
			}

			// 交易金额
			strje = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 12);

			// 流水号
			// 签购单重打
				if (oldseqno != null)
				{
					strseqno = Convert.increaseChar(oldseqno, 6);
				}
				else
				{
					strseqno = "000000";
				}
			
		      
			// 原终端号==参考号
				if (oldseqno != null)
				{
					stroldterm = Convert.increaseChar(oldauthno, 12);
				}
				else
				{
					stroldterm = "000000000000";
				}
			
			// 原交易日期
			if (olddate != null)
			{
				strolddate = Convert.increaseChar(olddate, 4);
			}
			else
			{
				strolddate = Convert.increaseChar("", 4);
			}


//			 传入串
			String cmd = strtypecode+"," + strje+"," + strolddate+"," + strseqno+"," + stroldterm;

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
        return true;
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
	
	public void XYKPrintDoc()
	{
		if (GlobalInfo.sysPara.bankprint <= 0) return;

		ProgressBox pb = null;
		
		try
		{
			
			String fileName="";
			
				if (!PathFile.fileExist("C:\\CCB\\TRANS.PRN"))
				{
					new MessageBox("找不到签购单打印文件!");

					return;
				}
				else
				{
					fileName = "C:\\CCB\\TRANS.PRN";
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
//						if (line.length() <= 0)
//						{
//							continue;
//						}
						
						if (line.trim().equals("CUT"))
						{					
							// 切纸
							XYKPrintDoc_End();
							
							continue;
						}
						
						  XYKPrintDoc_Print(line);
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
				XYKPrintDoc_End();
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
