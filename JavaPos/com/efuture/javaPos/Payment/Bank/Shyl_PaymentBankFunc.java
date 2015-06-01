package com.efuture.javaPos.Payment.Bank;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

public class Shyl_PaymentBankFunc extends PaymentBankFunc
{
	class ResponseData
	{
		public String FuncRetCode;			//函数返回码
		public double amount;				//金额
		public String AuthenticationNumber;	//授权号	    
		public String BatchNumber;			//批次号		
		public String CardFlag	;			//卡标志		
		public String CardNumber;			//卡号			
		public String ExpireDate;			//有效期		
		public String HostSerialNumber	;	//系统参考号	
		public String MerchantNumber;		//商户号		
		public String OriginalTraceNumber;	//原始流水号	
		public String ReferenceNumber;		//凭证号		
		public String RejectCode;			//返回码		
		public String ROLData;				//交易总笔数总金额		
		public String TerminalNumber;		//POS机编号	
		public long TransType;				//交易类型
		public String TransDate;			//交易时间
	}
	
	ResponseData response = new ResponseData();
	
	long curFphm = 0;
	
    public String[] getFuncItem()
    {
    	String[] func = new String[4];
    	
    	func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
    	func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
    	func[2] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
    	func[3] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
    	
    	return func;
    }
    
	public boolean getFuncLabel(int type,String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch(type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "原凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "隔日退货";
				break;
			case PaymentBank.XYKQD://交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";
				break;    				
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;           				
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = "原凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
		}
		
		return true;
	}
	
	public boolean getFuncText(int type,String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch(type)
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
				grpTextStr[4] = "银联不支持该功能";
				break;
			case PaymentBank.XYKQD://交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "银联不支持该功能";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结算";
				break;    				
			case PaymentBank.XYKYE://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "银联不支持该功能";
				break;           				
			case PaymentBank.XYKCD://签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始签购单重打";
				break;
		}
		
		return true;
	}
	
    public boolean XYKExecute(int type, double money, String track1,
                              String track2, String track3, String oldseqno,
                              String oldauthno,String olddate,Vector memo)
    {
        try
        {
        	if (type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKJZ && type != PaymentBank.XYKCD)
        	{
        		errmsg = "银联接口不支持该交易";
        		XYKSetError("XX",errmsg);
        		new MessageBox(errmsg);
        		return false;
        	}
  	
        	// 得到当前小票号
        	if (memo != null && memo.size() >= 2)
        	{
        		curFphm = Long.parseLong((String)memo.elementAt(1));
        	}
        	
        	// 先删除上次交易数据文件
            if (PathFile.fileExist("c:\\bankmis\\request.txt"))
            {
                PathFile.deletePath("c:\\bankmis\\request.txt");
                
                if (PathFile.fileExist("c:\\bankmis\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }                
            }

            if (PathFile.fileExist("c:\\bankmis\\response.txt"))
            {
                PathFile.deletePath("c:\\bankmis\\response.txt");
                
                if (PathFile.fileExist("c:\\bankmis\\response.txt"))
                {
            		errmsg = "交易请求文件response.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }                
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,oldauthno,olddate,memo))
            {
                return false;
            }

            // 调用接口模块
            if (PathFile.fileExist("c:\\bankmis\\bankmis.exe"))
            {
                CommonMethod.waitForExec("c:\\bankmis\\bankmis.exe");
            }
            else
            {
            	errmsg = "找不到金卡工程模块 bankmis.exe";
            	XYKSetError("XX",errmsg);
                new MessageBox(errmsg);

                return false;
            }

            // 读取应答数据
            if (!XYKReadResult())
            {
                return false;
            }

            // 检查交易是否成功
            XYKCheckRetCode();

            // 打印签购单
            if (XYKNeedPrintDoc())
            {
            	if (type == PaymentBank.XYKJZ)
            	{
            		XYKPrintJzDoc();
            	}
            	else
            	{
            		XYKPrintDoc();
            	}
            }

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
            XYKSetError("XX","调用异常"+ ex.getMessage());
            
            return false;
        }
    }
    
    public boolean XYKWriteRequest(int type, double money, String track1,
							            String track2, String track3,
							            String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		StringBuffer sbstr = null;
		
		try
		{
			sbstr = new StringBuffer();
			
			// 组织请求数据
			// 交易金额,操作员号,原始流水号,暂时不用,日期时间,二磁道,三磁道,交易类型
			sbstr.append(ManipulatePrecision.doubleToString(money) + ",");
			sbstr.append(GlobalInfo.posLogin.gh + ",");
			sbstr.append(oldseqno + ",");
			sbstr.append(",");
			sbstr.append(new ManipulateDateTime().getDateByEmpty() + ",");
			sbstr.append(CommonMethod.isNull(track2, "") + ",");
			sbstr.append(CommonMethod.isNull(track3, "") + ",");
			if (type == PaymentBank.XYKXF)
				sbstr.append("3,");
			else if (type == PaymentBank.XYKCX)
				sbstr.append("5,");
			else if (type == PaymentBank.XYKJZ)
				sbstr.append("7,");
			else if (type == PaymentBank.XYKCD)
				sbstr.append("12,");
			else return false;

			// 写入请求数据
			if (!rtf.writeFile("c:\\bankmis\\request.txt",sbstr.toString()))
			{
				errmsg = "写入金卡工程请求数据失败!";
				XYKSetError("XX",errmsg);
				new MessageBox(errmsg, null, false);
				
				return false;
			}
			
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();
			XYKSetError("XX","请求异常"+ex.getMessage());
			
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
    
    public boolean XYKReadResult()
    {
        try
        {
            if (!PathFile.fileExist("c:\\bankmis\\response.txt") || !rtf.loadFile("c:\\bankmis\\response.txt"))
            {
            	errmsg = "读取金卡工程应答数据失败!";
            	XYKSetError("XX",errmsg);
                new MessageBox(errmsg, null, false);

                return false;
            }

            // 读取请求数据
            String line = rtf.nextRecord();
            rtf.close();
            String[] linelist = line.replaceAll(",,", ",  ,").trim().split(",");

            if (linelist.length != 16)
            {
            	errmsg = "金卡工程应答数据格式错误";
            	XYKSetError("XX",errmsg);
                new MessageBox(errmsg, null, false);

                return false;
            }

            response.FuncRetCode = linelist[0].trim();
            response.amount = Convert.toDouble(linelist[1].trim());
            response.AuthenticationNumber = linelist[2].trim();  
            response.BatchNumber = linelist[3].trim();
            response.CardFlag = linelist[4].trim();
            response.CardNumber = linelist[5].trim();
            response.ExpireDate = linelist[6].trim();
            response.HostSerialNumber = linelist[7].trim();
            response.MerchantNumber = linelist[8].trim();
            response.OriginalTraceNumber = linelist[9].trim();
            response.ReferenceNumber = linelist[10].trim();
            response.RejectCode = linelist[11].trim();
            response.ROLData = linelist[12].trim();	
            response.TerminalNumber = linelist[13].trim();
            response.TransType = Convert.toLong(linelist[14].trim());
            response.TransDate = linelist[15].trim();
            
            //
            bld.retcode  = (response.FuncRetCode.equals("0")?response.RejectCode : response.FuncRetCode);
            bld.retmsg   = XYKReadRetMsg(bld.retcode).trim();
            bld.cardno   = response.CardNumber;
            bld.trace    = Convert.toLong(response.ReferenceNumber);
            if (response.CardFlag.length() >= 2)
            {
            	String bankid;
            	if (response.CardFlag.substring(0,2).equals("00"))
            	{
            		bankid = response.CardFlag.substring(2,6);
            	}
            	else
            	{
            		bankid = "WK" + response.CardFlag.substring(0,2);
            	}
            	
        		bld.bankinfo = bankid + XYKReadBankName(bankid);
            }

            //
            errmsg = bld.retmsg;

            return true;
        }
        catch (Exception ex)
        {
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();
            XYKSetError("XX","应答异常"+ex.getMessage());
            
            return false;
        }
    }

    public boolean XYKNeedPrintDoc()
    {
        if (!checkBankSucceed())
        {
            return false;
        }

        int type = Integer.parseInt(bld.type.trim());

        // 消费，消费撤销，交易结账，重打签购单
        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || (type == PaymentBank.XYKJZ) || (type == PaymentBank.XYKCD))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public String EscDoubleContorl(boolean b)
    {
    	if (b)
    	{
    		return (char)0x1b + "!" + (char)0x10; 
    	}
    	else
    	{
    		return (char)0x1b + "!" + (char)0x01;
    	}
    }
    
    public void XYKPrintDoc()
    {
        ProgressBox pb = null;
        StringBuffer sb = new StringBuffer();

        try
        {
            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");

            XYKPrintDoc_Start();

            XYKPrintDoc_Print("*************************************");
            XYKPrintDoc_Print("       上  海  银  联  商  务");
            XYKPrintDoc_Print("   (ShangHai UnionPay Merchant)");
            XYKPrintDoc_Print("*************************************");
            XYKPrintDoc_Print("商户名称(Merchant):" + XYKReadRetMsg("SHNAME"));
            XYKPrintDoc_Print("商户编号(Merchant NO.):" + response.MerchantNumber);
            XYKPrintDoc_Print("终 端 号(Terminal ID):" + response.TerminalNumber);
            XYKPrintDoc_Print("机号:" + GlobalInfo.syjDef.syjh + " 收银员:" + GlobalInfo.posLogin.gh + " 交易序号:" + curFphm);
            int type = Integer.parseInt(bld.type.trim());
            if (type != PaymentBank.XYKCD)
            {
            	XYKPrintDoc_Print("-------------------------------------");
            }
            else
            {
            	XYKPrintDoc_Print("-------**重打印(Print Again)**-------");
            }
            sb.delete(0,sb.length());
            sb.append("交易类型"+ EscDoubleContorl(true) +"(Trans.Type):" + EscDoubleContorl(false));
            if (response.TransType == 3) sb.append("消费" + EscDoubleContorl(true) + "(Sale)" + EscDoubleContorl(false));
            else sb.append("撤销" + EscDoubleContorl(true) + "(Void Sale)" + EscDoubleContorl(false));
            XYKPrintDoc_Print(sb.toString());
            XYKPrintDoc_Print("发卡银行(Issuer Bank):" + bld.bankinfo.substring(4));
            XYKPrintDoc_Print("卡号(Card Number):" + bld.cardno);
            XYKPrintDoc_Print("有 效 期(Exp.Date):" + response.ExpireDate);
            XYKPrintDoc_Print("批 次 号(Batch NO.):" + response.BatchNumber);
            XYKPrintDoc_Print("凭 证 号(Voucher NO.)" + response.ReferenceNumber);
            XYKPrintDoc_Print("交易时间(Date/Time):" + response.TransDate);
            XYKPrintDoc_Print("参 考 号(Ref.NO.):" + response.HostSerialNumber);
            XYKPrintDoc_Print("授 权 号(Auth NO.):" + response.AuthenticationNumber);
            XYKPrintDoc_Print("金    额" + EscDoubleContorl(true) + "(Amount):   RMB:" + ManipulatePrecision.doubleToString(response.amount * ((response.TransType==3)?1:-1)) + EscDoubleContorl(false));
            XYKPrintDoc_Print("-------------------------------------");
            XYKPrintDoc_Print("持卡人签名(CardHolder Signature):");
            XYKPrintDoc_Print("");
            XYKPrintDoc_Print("");
            XYKPrintDoc_Print("");
            XYKPrintDoc_Print("=====================================");
            XYKPrintDoc_Print("本人同意支付上述款项");
            XYKPrintDoc_Print("I Acknowledge Satisfactory Receipt of");
            XYKPrintDoc_Print("Re lative Goods/Services");

            XYKPrintDoc_End();
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
    
    public void XYKPrintJzDoc()
    {
        ProgressBox pb = null;

        try
        {
            pb = new ProgressBox();
            pb.setText("正在打印银联结算单,请等待...");

            Printer.getDefault().startPrint_Journal();

            Printer.getDefault().printLine_Journal("上  海  银  联  商  务");
            Printer.getDefault().printLine_Journal("   商  户  结  算  单");
            Printer.getDefault().printLine_Journal("****************************");
            Printer.getDefault().printLine_Journal("商 户 名 称");
            Printer.getDefault().printLine_Journal(XYKReadRetMsg("SHNAME"));
            Printer.getDefault().printLine_Journal("");
            Printer.getDefault().printLine_Journal("商 户 号 :" + response.MerchantNumber);
            Printer.getDefault().printLine_Journal("终 端 号 :" + response.TerminalNumber);
            Printer.getDefault().printLine_Journal("日 期/时 间");
            Printer.getDefault().printLine_Journal(response.TransDate);
            Printer.getDefault().printLine_Journal("批次号：" + response.BatchNumber);
            Printer.getDefault().printLine_Journal("----------------------------------");
            int nkxfbs=0,nkcxbs=0,wkxfbs=0,wkcxbs=0;
            double nkxfje=0,nkcxje=0,wkxfje=0,wkcxje=0;
            if (response.ROLData.length() >= 10 ) nkxfbs = Convert.toInt(response.ROLData.substring(0,10));
            if (response.ROLData.length() >= 26 ) nkxfje = ManipulatePrecision.doubleConvert(Convert.toDouble(response.ROLData.substring(10,26)) / 100,2,1);
            if (response.ROLData.length() >= 36 ) nkcxbs = Convert.toInt(response.ROLData.substring(26,36));
            if (response.ROLData.length() >= 52 ) nkcxje = ManipulatePrecision.doubleConvert(Convert.toDouble(response.ROLData.substring(36,52)) / 100,2,1);
            if (response.ROLData.length() >= 62 ) wkxfbs = Convert.toInt(response.ROLData.substring(52,62));
            if (response.ROLData.length() >= 78 ) wkxfje = ManipulatePrecision.doubleConvert(Convert.toDouble(response.ROLData.substring(62,78)) / 100,2,1);
            if (response.ROLData.length() >= 88 ) wkcxbs = Convert.toInt(response.ROLData.substring(78,88));
            if (response.ROLData.length() >= 104) wkcxje = ManipulatePrecision.doubleConvert(Convert.toDouble(response.ROLData.substring(88,104)) / 100,2,1);
            Printer.getDefault().printLine_Journal("          内卡交易");
            Printer.getDefault().printLine_Journal("消费  " + Convert.appendStringSize("",String.valueOf(nkxfbs),0,4,4,0) + "笔 " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(nkxfje),0,10,10,1));
            Printer.getDefault().printLine_Journal("撤销  " + Convert.appendStringSize("",String.valueOf(nkcxbs),0,4,4,0) + "笔 " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(nkcxje*-1),0,10,10,1));
            Printer.getDefault().printLine_Journal("-----------------------------------");
            Printer.getDefault().printLine_Journal("总计  " + Convert.appendStringSize("",String.valueOf(nkxfbs+nkcxbs),0,4,4,0) + "笔 " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(nkxfje - nkcxje),0,10,10,1));
            Printer.getDefault().printLine_Journal("===================================");
            Printer.getDefault().printLine_Journal("          外卡交易");
            Printer.getDefault().printLine_Journal("消费  " + Convert.appendStringSize("",String.valueOf(wkxfbs),0,4,4,0) + "笔 " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(wkxfje),0,10,10,1));
            Printer.getDefault().printLine_Journal("撤销  " + Convert.appendStringSize("",String.valueOf(wkcxbs),0,4,4,0) + "笔 " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(wkcxje*-1),0,10,10,1));
            Printer.getDefault().printLine_Journal("-----------------------------------");
            Printer.getDefault().printLine_Journal("总计  " + Convert.appendStringSize("",String.valueOf(wkxfbs+wkcxbs),0,4,4,0) + "笔 " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(wkxfje - wkcxje),0,10,10,1));

            Printer.getDefault().cutPaper_Journal();
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
