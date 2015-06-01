package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
//长安长百新市
public class Yxbr_PaymentBankFunc extends PaymentBankFunc{

	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
		func[6] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "交易一览";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKJZ: //结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结账";

				break;

			case PaymentBank.XYKTH: //隔日退货   
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCD: //签购单重打
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
				
			case PaymentBank.XYKQD: //交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
				
			case PaymentBank.XKQT1: //交易一览
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易一览";
				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKTH: //退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;

			case PaymentBank.XYKJZ: //结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";

				break;
				
			case PaymentBank.XYKCD: //签购单重打上笔
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打签购单";

				break;
				
			case PaymentBank.XKQT1: //签购单重打指定
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车查看交易一览";

				break;
		}

		return true;
	}
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			// 调用接口模块
			if (PathFile.fileExist(ConfigClass.BankPath+"\\javaposbank.exe"))
			{
				if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
				{
					PathFile.copyPath(ConfigClass.BankPath+"\\request.txt", ConfigClass.BankPath+"\\LastRequest.txt");
					PathFile.deletePath(ConfigClass.BankPath+"\\request.txt");

					if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
					{
						errmsg = "交易请求文件request.TXT无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
				{
					PathFile.copyPath(ConfigClass.BankPath+"\\result.txt", ConfigClass.BankPath+"\\LastResult.txt");
					PathFile.deletePath(ConfigClass.BankPath+"\\result.txt");

					if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
					{
						errmsg = "交易请求文件result.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				String line = "";

				//String type1 = "";
				String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
				switch (type)
				{
					case PaymentBank.XYKXF:
						line = "1,"+GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh+","+jestr+","+track2+","+track3;
						break;
					case PaymentBank.XYKCX:
						line = "5,"+GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh+","+jestr+","+track2+","+track3+","+oldseqno;
						break;
					case PaymentBank.XYKTH:
						line = "2,"+GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh+","+jestr+","+track2+","+track3+","+olddate.substring(0,8)+","+","+oldseqno+","+oldauthno;
						break;
					case PaymentBank.XYKYE:		// 查询余额
						line = "12,"+GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh+","+track2+","+track3;
						break;
					case PaymentBank.XYKCD:		// 重打上笔票据
						line = "91,"+GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh+","+oldseqno;
						break;
					case PaymentBank.XYKJZ:		// 交易结账
						line = "7,"+GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh;
						break;
					case PaymentBank.XYKQD:		// 交易签到
						line = "6,"+GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh;
						break;
					case PaymentBank.XKQT1:		// 交易一览
						line = "92,"+GlobalInfo.syjDef.syjh+","+GlobalInfo.posLogin.gh;
						break;
					default:
						return false;
				}
				//bld.type = type1;
				
				PrintWriter pw = null;

				try
				{
					pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");

					if (pw != null)
					{
						pw.print(line);
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

				CommonMethod.waitForExec(ConfigClass.BankPath+"\\javaposbank.exe EBRING","javaposbank.exe");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult()) { return false; }

			// 检查交易是否成功
			if(!XYKCheckRetCode()){
				return false;
			}

			
			//System.err.println("head");
			// 打印签购单
			
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc();
			}

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

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
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line = br.readLine();
//函数返回值(0或1)，retcode,pan,tal_amount,txn_date,txn_time,bank_no,bank_name,exp_date,batch_no,systrace,voucher_no,ref_no,auth_no,pid,reference,remark
//              银联返回码 ，主账号，余额，   交易日期， 交易时间，银行号，  银行名称，  卡有效期  批次号      流水号    凭证号       参考号   授权号   身份证号  备注   中奖信息
			String[] ret = line.split(","); 
			bld.retcode = ret[1];
			
			if (bld.retcode.trim().equals("00"))
			{
				if (bld.type.equals("6")||bld.type.equals("7")||bld.type.equals("91")||bld.type.equals("92")) return true;
				if(ret.length>2) bld.cardno = ret[2];
				if(ret.length>3) bld.kye = ManipulatePrecision.doubleConvert(Double.parseDouble(ret[3])/100,2,1);
				if(ret.length>7)bld.bankinfo = ret[7];
				if(ret.length>10)bld.trace = Long.parseLong((ret[10]));
				if(ret.length>12)bld.authno = (ret[12]);
			}
			else
			{
				if (ret.length > 2) bld.retmsg = ret[17].trim();
			}

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
					// TODO 自动生成 catch 块
					new MessageBox("result.txt 关闭失败\n重试后如果仍然失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void XYKPrintDoc_Start()
	{
			Printer.getDefault().startPrint_Normal();
	}
	
	public void XYKPrintDoc_Print(String printStr)
	{
			Printer.getDefault().printLine_Normal(printStr);
	}
	
	public void XYKPrintDoc_End()
	{
			Printer.getDefault().cutPaper_Normal();
	}


	public void XYKPrintDoc()
	{
		ProgressBox pb = null;

		try
		{
			String printName = ConfigClass.BankPath+"\\YXBR_CONFIG\\print\\print.txt";
			if(GlobalInfo.sysPara.bankprint<1) return ;
			if (!PathFile.fileExist(printName))
			{
				new MessageBox("找不到签购单打印文件!");
				return;
			}
			
			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");
			 
			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
	         {
	        	 XYKPrintDoc_Start();

	             BufferedReader br = null;
	             
	             try
	             {
	            	
	            	 br = CommonMethod.readFileGBK(printName);
	            	 
	            	 if (br == null)
	            	 {
                       new MessageBox("打开" + printName + "打印文件失败!");

                       return;
	            	 }
	            	   	 
	            	 String line = null;
	            	 while ((line = br.readLine()) != null)
	            	 {
                       if (line.trim().equals("CUTPAPER"))
                       {
                    	   Printer.getDefault().cutPaper_Normal();
                    	   continue;
                       }

                       XYKPrintDoc_Print(line);
	            	 }

	             }
	             catch (Exception ex)
	             {
	            	 new MessageBox(ex.getMessage());
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
	public boolean XYKCheckRetCode() {
		if (bld.retcode.trim().equals("00")) {
			bld.retbz = 'Y';
			bld.retmsg = "金卡工程调用成功";

			return true;
		} else {
			bld.retbz = 'N';
			if(bld.retmsg.trim().equals("")){
				bld.retmsg = "交易失败:"+bld.retcode;
			}
			return false;
		}
	}
	protected boolean XYKNeedPrintDoc(int type)
	{
		if (type != PaymentBank.XYKXF && type != PaymentBank.XYKTH && type != PaymentBank.XYKCX && type != PaymentBank.XYKCD && type != PaymentBank.XYKJZ && type != PaymentBank.XKQT1)
		{
			return false;
		}
		return true;
	}

}
