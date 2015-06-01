package com.efuture.javaPos.Payment.Bank;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

public class Shylnew_PaymentBankFunc extends PaymentBankFunc
{
	class ResponseData
	{
		public String FuncRetCode;			//函数返回码
		public String PosNumber;			//POS机号 
		public String StoreNumber;			//门店号
		public String Operator;				//操作员号
		public String CardType;				//卡种代码
		public String CardName;				//卡种名称
		public String TransType;			//交易类型
		public String TransAmoun;			//金额
		public String Tips;					//小费
		public String Total;				//总计
		public String BalanceAmount;		//余额
		public String PosTraceNumber;		//流水号
		public String OldTraceNumber;		//原始流水号
		public String ExpireDate;			//有效期
		public String BatchNumber;			//批次号
		public String MerchantNumber;		//商户号
		public String MerchantName;			//商户名
		public String TerminalNumber;		//终端号
		public String HostSerialNumber;		//系统参考号
		public String AuthNumber;			//授权码
		public String RejCode;				//返回码
		public String IssNumber;			//发卡行名称
		public String IssName;				//发卡行名称
		public String CardNumber;			//卡号
		public String TransDate;			//交易日期
		public String TransTime;			//交易时间
		public String RejCodeExplain;		//返回码解释
		public String CardBack;				//卡片回收标志
		public String TransCheck;			//交易唯一标识（类似收银机发票号）
	}
	
	ResponseData response = new ResponseData();
	
	long curFphm = 0;
	
    public String[] getFuncItem()
    {
    	String[] func = new String[6];
    	
    	func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
    	func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
    	func[2] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
    	func[3] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
    	func[4] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";
    	func[5] = "[" + PaymentBank.XKQT2 + "]" + "查询交易明细";
    	
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
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";
				break;    				          				
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
			case PaymentBank.XKQT1://重打结算单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打结算";
				break;
			case PaymentBank.XKQT2://查询交易明细
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询交易";
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
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结算";
				break;    				        				
			case PaymentBank.XYKCD://签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始签购单重打";
				break;
			case PaymentBank.XKQT1://重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打结算单";
				break;
			case PaymentBank.XKQT2://查询交易明细
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始查询交易明细";
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
        	if (type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKJZ && type != PaymentBank.XYKCD && type != PaymentBank.XKQT1 && type != PaymentBank.XKQT2)
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
            if (PathFile.fileExist("C:\\SHUPMS\\EXEInterface\\request.txt"))
            {
                PathFile.deletePath("C:\\SHUPMS\\EXEInterface\\request.txt");
                
                if (PathFile.fileExist("C:\\SHUPMS\\EXEInterface\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }                
            }

            if (PathFile.fileExist("C:\\SHUPMS\\EXEInterface\\response.txt"))
            {
                PathFile.deletePath("C:\\SHUPMS\\EXEInterface\\response.txt");
                
                if (PathFile.fileExist("C:\\SHUPMS\\EXEInterface\\response.txt"))
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
            if (PathFile.fileExist("C:\\SHUPMS\\EXEInterface\\EXEInterface.exe"))
            {
                CommonMethod.waitForExec("C:\\SHUPMS\\EXEInterface\\EXEInterface.exe");
            }
            else
            {
            	errmsg = "找不到金卡工程模块 EXEInterface.exe";
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

            // 打印由银联接口完成
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
			// POS编号,门店编号,操作员编号,银行卡(01),交易类型,交易金额,原始流水号,交易标识,其他信息
			sbstr.append(GlobalInfo.syjDef.syjh + ",");
			sbstr.append(GlobalInfo.sysPara.mktcode + ",");
			sbstr.append(GlobalInfo.posLogin.gh + ",");
			sbstr.append("01" + ",");
			if (type == PaymentBank.XYKXF)
				sbstr.append("2,");
			else if (type == PaymentBank.XYKCX)
				sbstr.append("3,");
			else if (type == PaymentBank.XYKJZ)
				sbstr.append("14,");
			else if (type == PaymentBank.XYKCD)
				sbstr.append("16,");
			else if (type == PaymentBank.XKQT1)
				sbstr.append("19,");
			else if (type == PaymentBank.XKQT2)
				sbstr.append("20,");
			else return false;			
			sbstr.append(ManipulatePrecision.doubleToString(money) + ",");
			sbstr.append(oldseqno + ",");
			sbstr.append(",,");

			// 写入请求数据
			if (!rtf.writeFile("C:\\SHUPMS\\EXEInterface\\request.txt",sbstr.toString()))
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
            if (!PathFile.fileExist("C:\\SHUPMS\\EXEInterface\\response.txt") || !rtf.loadFileByGBK("C:\\SHUPMS\\EXEInterface\\response.txt"))
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

            if (linelist.length != 29)
            {
            	errmsg = "金卡工程应答数据格式错误";
            	XYKSetError("XX",errmsg);
                new MessageBox(errmsg, null, false);

                return false;
            }

            response.FuncRetCode = linelist[0].trim();
            response.PosNumber = linelist[1].trim();
            response.StoreNumber = linelist[2].trim();
            response.Operator = linelist[3].trim();
            response.CardType = linelist[4].trim();
            response.CardName = linelist[5].trim();
            response.TransType = linelist[6].trim();
            response.TransAmoun = linelist[7].trim();
            response.Tips = linelist[8].trim();
            response.Total = linelist[9].trim();
            response.BalanceAmount = linelist[10].trim();
            response.PosTraceNumber = linelist[11].trim();
            response.OldTraceNumber = linelist[12].trim();
            response.ExpireDate = linelist[13].trim();
            response.BatchNumber = linelist[14].trim();
            response.MerchantNumber = linelist[15].trim();
            response.MerchantName = linelist[16].trim();
            response.TerminalNumber = linelist[17].trim();
            response.HostSerialNumber = linelist[18].trim();
            response.AuthNumber = linelist[19].trim();
            response.RejCode = linelist[20].trim();
            response.IssNumber = linelist[21].trim();
            response.IssName = linelist[22].trim();
            response.CardNumber = linelist[23].trim();
            response.TransDate = linelist[24].trim();
            response.TransTime = linelist[25].trim();
            response.RejCodeExplain = linelist[26].trim();
            response.CardBack = linelist[27].trim();
            response.TransCheck = linelist[28].trim();
            
            //
            bld.retcode  = (response.FuncRetCode.equals("0")?response.RejCode : response.FuncRetCode);
            bld.retmsg   = response.RejCodeExplain;
            bld.cardno   = response.CardNumber;
            bld.trace    = Convert.toLong(response.PosTraceNumber);
            bld.bankinfo = response.IssNumber + response.IssName;

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
}
