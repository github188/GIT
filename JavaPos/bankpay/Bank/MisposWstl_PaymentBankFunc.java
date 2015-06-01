package bankpay.Bank;

import java.io.BufferedReader;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class MisposWstl_PaymentBankFunc extends PaymentBankFunc
{
	public String PosNumber;//收银机号
	public String StoreNumber;//门店号
	public String Operator;//操作员号
	public String CardNumber;//卡号
	public String CardType;//卡代码
	public String TransType;//交易类型
	public String Amount;// 金额
	public String OldTraceNumber;//原始流水号
	public String AuthNumber;// 授权码
	public String HostserialNumber;// 原系统参考号
	public String TransDate;//原交易日期
	public String ExpireDate;//有效期
	public String Memo;//其他信息
	public String TransCheck;//交易唯一标识
	
	
	public String[] getFuncItem()
    {
		String[] func = new String[10];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
		func[6] = "[" + PaymentBank.XKQT1 + "]" + "重打上笔签购单";
		func[7] = "[" + PaymentBank.XKQT2 + "]" + "重打结算单";
		func[8] = "[" + PaymentBank.XKQT3 + "]" + "查询交易明细";
		func[9] = "[" + PaymentBank.XKQT4 + "]" + "充值";
     
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
				grpLabelStr[0] = "原始流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XKQT4://充值
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "充值金额";
			case PaymentBank.XYKQD://交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = "原始流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
			case PaymentBank.XKQT1://签购单重打上一笔
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
			case PaymentBank.XKQT2://重打结算单
				grpLabelStr[0] = "原始流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询交易明细";
				break;
			case PaymentBank.XKQT3://查询交易明细
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询交易明细";
				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示必须用户输入,不为null表示缺省显示无需改变
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
			case PaymentBank.XKQT4://充值  
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKQD://交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";
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
			case PaymentBank.XKQT1://签购单重打上一笔
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键重打上一笔签购单";
				break;
			case PaymentBank.XKQT2://重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键重打结算单";
				break;
			case PaymentBank.XKQT3://查询交易明细
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键查询交易明细";
				break;
		}

		return true;
	}
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && 
					(type != PaymentBank.XYKCX) && 
					(type != PaymentBank.XYKYE) && 
					(type != PaymentBank.XYKCD) && 
					(type != PaymentBank.XYKQD) &&
					(type != PaymentBank.XYKJZ) &&
					(type != PaymentBank.XKQT1) &&
					(type != PaymentBank.XKQT2) &&
					(type != PaymentBank.XKQT3) &&
					(type != PaymentBank.XKQT4) 
					)
		            {
		                errmsg = "MISPOS接口不支持该交易";
		                new MessageBox(errmsg);

		                return false;
		            }
		
            
            if (PathFile.fileExist(ConfigClass.BankPath+"\\Print.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath+"\\Print.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath+"\\Print.txt"))
                {
            		errmsg = "签购单打印文件print.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
                // 调用接口模块
               if(sendRequest()){
            	   // 检查交易是否成功
                   if (!XYKCheckRetCode()) return false;
                   
   				// 打印签购单
   				if (XYKNeedPrintDoc(type))
   				{
   					XYKPrintDoc();
   				}
   				return true;
               }
            }
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		return false;
	}
	
	private boolean sendRequest()
	{
		try{
			    ActiveXComponent app = new ActiveXComponent("ALLINPAY.MisPos");
			    ActiveXComponent req=new ActiveXComponent("ALLINPAY.RequestData");
			    ActiveXComponent res=new ActiveXComponent("ALLINPAY.ResponseData");
			    Variant result=null;

			    Dispatch.callN(req, "PutValue", new Object[]{"PosNumber",PosNumber});  //收银机号
			    Dispatch.callN(req, "PutValue", new Object[]{"StoreNumber",StoreNumber}); //门店号
			    Dispatch.callN(req, "PutValue", new Object[]{"Operator",Operator});    //操作员号
			    Dispatch.callN(req, "PutValue", new Object[]{"CardNumber",CardNumber});   //卡号
			    Dispatch.callN(req, "PutValue", new Object[]{"CardType",CardType});    //卡代码
			    Dispatch.callN(req, "PutValue", new Object[]{"TransType", TransType});   //交易类型
			    Dispatch.callN(req, "PutValue", new Object[]{"Amount", Amount});   // 金额
			    Dispatch.callN(req, "PutValue", new Object[]{"OldTraceNumber",OldTraceNumber}); //原始流水号 
			    Dispatch.callN(req, "PutValue", new Object[]{"AuthNumber",AuthNumber});    // 授权码
			    Dispatch.callN(req, "PutValue", new Object[]{"HostserialNumber",HostserialNumber});  // 原系统参考号
			    Dispatch.callN(req, "PutValue", new Object[]{"TransDate",TransDate});    //原交易日期
			    Dispatch.callN(req, "PutValue", new Object[]{"ExpireDate",ExpireDate});   //有效期
			    Dispatch.callN(req, "PutValue", new Object[]{"Memo",Memo});          //其他信息
			    Dispatch.callN(req, "PutValue", new Object[]{"TransCheck",TransCheck});   //交易唯一标识
			    

			    Dispatch allinpayApp = (Dispatch) app.getObject();
			    String line = "";
			    if(allinpayApp!=null){
			    	result=Dispatch.callN(allinpayApp, "TransProcess", new Object[]{req,res});
			    	line = line + result+"\n";
			    	if(result.toString().trim().equals("0")){
			    		bld.retcode = Dispatch.callN(res, "GetValue", new Object[]{"rejCode"}).toString();
			    		line = line + bld.retcode+"\n";
			    		String tc = Dispatch.callN(res, "GetValue", new Object[]{"TransCheck"}).toString();
			    		if (isXYKCheckTransCheck(TransType))  
			    		{
			    			if(!tc.trim().equals(TransCheck)){
				    			bld.retmsg = "返回的交易标识与请求不一致!";
				    			return false;
				    		}
			    		}
				    	if(bld.retcode.equals("00")){
				    		bld.je = Double.parseDouble(Dispatch.callN(res, "GetValue", new Object[]{"Amount"}).toString());
				    		bld.kye = Double.parseDouble(Dispatch.callN(res, "GetValue", new Object[]{"BalanceAmount"}).toString()); 
				    		bld.trace = Long.parseLong(Dispatch.callN(res, "GetValue", new Object[]{"PosTraceNumber"}).toString()); 
				    		bld.authno = Dispatch.callN(res, "GetValue", new Object[]{"HostSerialNumber"}).toString();
				    		bld.memo = Dispatch.callN(res, "GetValue", new Object[]{"AuthNumber"}).toString();
				    		bld.bankinfo = Dispatch.callN(res, "GetValue", new Object[]{"IssName"}).toString();
				    		bld.cardno = Dispatch.callN(res, "GetValue", new Object[]{"CardNumber"}).toString();
				    		return true;
				    	}else{
				    		bld.retmsg = Dispatch.callN(res, "GetValue", new Object[]{"RejCodeExplain"}).toString();
				    		return false;
				    	}
			    	}else{
			    		bld.retcode = Dispatch.callN(res, "GetValue", new Object[]{"rejCode"}).toString();
				    	bld.retmsg = Dispatch.callN(res, "GetValue", new Object[]{"RejCodeExplain"}).toString();
			    		return false;
			    	}
			    }else{
			        return	false;
			    }
		}catch (Exception e)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + e.getMessage(), null, false);
			e.printStackTrace();
			System.out.println(e);
			return false;
		}
		
		
	}
	
	//消费，撤销，退货,充值需要判断唯一标示是否一致
	private boolean isXYKCheckTransCheck(String type)
	{
		if ((type == "2") || (type == "3" ) ||   (type == "6" ) )
        {           
            return true;
        } 
		else
		{
			return false;
		}
		
	}
	public boolean XYKCheckRetCode()
	 {
       if (bld.retcode.trim().equals("00"))
       {
           bld.retbz = 'Y';

           return true;
       }
       else
       {
           bld.retbz = 'N';

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
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		try
		{
			ManipulateDateTime md = new ManipulateDateTime();
			 PosNumber = GlobalInfo.syjDef.syjh;
			 StoreNumber = GlobalInfo.sysPara.mktcode;//门店号
			 Operator = GlobalInfo.posLogin.gh;//操作员号
			 CardNumber = "";//卡号
			 CardType = "02";//卡代码
			 TransType = "";//交易类型
			 Amount = "";// 金额
			 OldTraceNumber = "";//原始流水号
			 AuthNumber = "";// 授权码
			 HostserialNumber = "";// 原系统参考号
			 TransDate = "";//原交易日期
			 ExpireDate = "";//有效期
			 Memo = "";//其他信息
//			 TransCheck = md.getDateByEmpty()+md.getTimeByEmpty()+GlobalInfo.syjDef.syjh+GlobalInfo.syjStatus.fphm;//交易唯一标识
			 TransCheck = ""; //调试的一个不传效验码，这个现在根据具体情况设置
			
			 //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		TransType = "2";
	         		Amount = String.valueOf(money);
	         	break;
	         	case PaymentBank.XYKCX:
	         		TransType = "3";
	         		OldTraceNumber = oldseqno;
	         		Amount = String.valueOf(money);
	         	break;
	         	case PaymentBank.XKQT4:
	         		TransType = "6";
	         		Amount = String.valueOf(money);
	         	break;
	         	case PaymentBank.XYKQD:
	         		TransType = "1";
	         	break;
	         	case PaymentBank.XYKYE:
	         		TransType = "7";
	         	break;
	         	case PaymentBank.XYKCD:
	         		TransType = "16";
	                OldTraceNumber = oldseqno;	
	         	break;
	         	case PaymentBank.XKQT1:
	         		TransType = "16";
	         	break;
	         	case PaymentBank.XYKJZ:
	         		TransType = "14";
	         	break;	 
	         	case PaymentBank.XKQT2:
	         		TransType = "19";
	         		break;
	         	case PaymentBank.XKQT3:
	         		TransType = "20";
	         		break;
	         }
	         
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean checkDate(Text date)
	{
		String date1 = date.getText();
		if (date1.length() > 4)
		{
			new MessageBox("请输入日期\n日期格式《MMDD》");
			return false;
		}
		return true;
	}
		
	public boolean XYKNeedPrintDoc(int type)
	{
		if ((type == PaymentBank.XYKXF) || 
				(type == PaymentBank.XYKCX) ||  
				(type == PaymentBank.XYKCD) || 
				(type == PaymentBank.XYKJZ) ||
				(type == PaymentBank.XKQT1) ||
				(type == PaymentBank.XKQT2) ||
				(type == PaymentBank.XKQT4) 
				)
	            {
	                
	                return true;
	            }
       
       return false;
	}
	
	public void XYKPrintDoc()
	{
        ProgressBox pb = null;
        if(GlobalInfo.sysPara.bankprint<1) return ;
        String printName = ConfigClass.BankPath+"\\Print.txt";
        try
        {
            if (!PathFile.fileExist(printName))
            {
                new MessageBox("找不到签购单打印文件!");

                return;
            }
            
            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");

            for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
            {
                BufferedReader br = null;

                XYKPrintDoc_Start();

                try
                {
                    //
                    br = CommonMethod.readFileGBK(printName);

                    if (br == null)
                    {
                        new MessageBox("打开签购单打印文件失败!");

                        return;
                    }

                    //
                    String line = null;

                    while ((line = br.readLine()) != null)
                    {
                        if (line.length() <= 0)
                        {
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
            
			if (PathFile.fileExist(printName))
            {
                PathFile.deletePath(printName);
            }
        }
    }

}

