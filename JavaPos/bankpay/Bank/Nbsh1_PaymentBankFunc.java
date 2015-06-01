package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.jacob.com.*;

import java.util.Currency;

import com.jacob.activeX.ActiveXComponent;

//成都-南部圣桦购物中心银联接口，联通支付  （掉jacob.jar包形式）
//先安装ALLINPAY_3.0.5.exe 程序 ，将jacob.dll放入SYSTEM32
//导入jacob包
public class Nbsh1_PaymentBankFunc extends PaymentBankFunc {
	
	String path = null;
	ActiveXComponent app = new ActiveXComponent("ALLINPAY.MisPos");
    ActiveXComponent req = new ActiveXComponent("ALLINPAY.RequestData");
    ActiveXComponent res = new ActiveXComponent("ALLINPAY.ResponseData");
    Variant result = null;
    
	public String[] getFuncItem()
    {
        String[] func = new String[10];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打任意笔签购单";
        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算"; 
        func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";
        func[8] = "[" + PaymentBank.XKQT2 + "]" + "查询交易明细";
        func[9] = "[" + PaymentBank.XKQT3 + "]" + "重打上笔签购单";
                
        return func;
    } 
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
		switch (type)
        {
        	case PaymentBank.XYKXF: //	消费
        		grpLabelStr[0] = null;
        		grpLabelStr[1] = null;
        		grpLabelStr[2] = null;
        		grpLabelStr[3] = null;
        		grpLabelStr[4] = "交易金额";
        	break;
        	case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = null;
                grpLabelStr[1] = "原流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日期";
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
        	case PaymentBank.XYKCD: //重打任意笔签购单    
                grpLabelStr[0] = null;
                grpLabelStr[1] = "原流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打签购单";
            break;
        	case PaymentBank.XYKQD: //签到   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //结算  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易结算";
            break;
        	case PaymentBank.XKQT1: //重打结算单  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打结算单";
            break;
        	case PaymentBank.XKQT2: //查询交易明细  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "查询交易明细";
            break;
        	case PaymentBank.XKQT3: //重打上笔签购单
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
            break;
        }
		
		return true;
    }
	
	public boolean getFuncText(int type, String[] grpTextStr)
    {
		switch (type)
		{
		 	case PaymentBank.XYKXF: 	// 消费
		        grpTextStr[0] = null;
		        grpTextStr[1] = null;
		        grpTextStr[2] = null;
		        grpTextStr[3] = null;
		        grpTextStr[4] = null;
		    break;
		 	case PaymentBank.XYKCX: 	// 消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XYKTH:		//隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
			break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//重打签购单    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打印";
            break;
		 	case PaymentBank.XYKQD: 	//交易签到   
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始签到";
            break;
		 	case PaymentBank.XYKJZ: 	//交易结算
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始结算";
            break;
		 	case PaymentBank.XKQT1: 	//重打结算单
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打结算单";
            break;
		 	case PaymentBank.XKQT2: 	//查询交易明细
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始查询交易明细";
            break;
		 	case PaymentBank.XKQT3: 	//重打上笔签购单
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打上笔签购单";
            break;
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{	
		try
		{
			if (!(type == PaymentBank.XYKXF || 
				  type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || 
				  type == PaymentBank.XYKQD ||
				  type == PaymentBank.XYKJZ || 
				  type == PaymentBank.XYKYE ||
				  type == PaymentBank.XYKCD ||
				  type == PaymentBank.XKQT1 ||
				  type == PaymentBank.XKQT2 ||
				  type == PaymentBank.XKQT3 ))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
			path = ConfigClass.BankPath;
			
			
			
			if (PathFile.fileExist(path + "\\print.txt"))
            {
                PathFile.deletePath(path + "\\print.txt");
            }
			
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            Dispatch allinpayApp = (Dispatch) app.getObject();
    	    
    	    if(allinpayApp != null)
    	    {
    	    	result = Dispatch.callN(allinpayApp, "TransProcess", new Object[]{req,res});
    	    	System.out.println(result);
    	    	bld.memo = String.valueOf(result);
//    	    	System.out.println(res.getProperty(new Object[]{"Rejcode"}).toString());
//    			System.out.println(Dispatch.callN(res, "GetValue",new Object[] { "rejCode" }).toString());
    	    }
			
            // 读取应答数据
            if (!XYKReadResult())
            {
                return false;
            }
            
            
            // 检查交易是否成功
            XYKCheckRetCode();
			
            //打印签购单
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc();
			}
            return true;		
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
            XYKSetError("XX","金卡异常XX:"+ex.getMessage());
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
            
			return false;
		}
	}
	
	

	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		
		 String transType = "";
		 
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
			 		transType = "2";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		transType = "3";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	transType = "4";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	transType = "18";
                break;	
                case  PaymentBank.XYKCD: 	// 重打任意笔签购单
                	transType = "16";
                break;
                case  PaymentBank.XYKQD: 	// 交易签到
                	transType = "1";
                break;
                case  PaymentBank.XYKJZ:
                	transType = "14";		// 交易结算
                break;
                case  PaymentBank.XKQT1:
                	transType = "19";		// 交易结算
                break;
                case  PaymentBank.XKQT2:
                	transType = "20";		// 查询交易明细
                break;
                case  PaymentBank.XKQT3:
                	transType = "16";		// 重打上笔签单
                break;
			 }
			 

	         String syyh = GlobalInfo.posLogin.gh;
	         
	         String mkt = GlobalInfo.sysPara.mktcode;
	         
	         String syjh = ConfigClass.CashRegisterCode;
	         
	         String cardType = "01";  //银行卡
	          
			 String jestr = String.valueOf(money);
			 
			 String trace = Convert.increaseCharForward(oldauthno, '0', 6); //原流水号  
			 
			 String transCheck = new ManipulateDateTime().getDateByEmpty()  + GlobalInfo.syjStatus.fphm; //new ManipulateDateTime().getTimeByEmpty()
	                          
	         String seqno = Convert.increaseCharForward(oldseqno, '0', 12); //原系统参考号
	        
	         String date = Convert.increaseChar(olddate, '0', 8); //原交易日期

	         Dispatch.callN(req, "PutValue", new Object[]{"PosNumber",syjh});
	         Dispatch.callN(req, "PutValue", new Object[]{"StoreNumber",mkt});
	         Dispatch.callN(req, "PutValue", new Object[]{"Operator",syyh});
	         Dispatch.callN(req, "PutValue", new Object[]{"CardType",cardType});
	 	     Dispatch.callN(req, "PutValue", new Object[]{"TransType",transType});
	 	     Dispatch.callN(req, "PutValue", new Object[]{"Amount",jestr});
	 	     Dispatch.callN(req, "PutValue", new Object[]{"OldTraceNumber",trace});
	 	     Dispatch.callN(req, "PutValue", new Object[]{"TransCheck",trace});
	 	     Dispatch.callN(req, "PutValue", new Object[]{"HostserialNumber",seqno});
	 	     Dispatch.callN(req, "PutValue", new Object[]{"TransDate",date});
	 	    
	 	     return true;
		 }
		 catch (Exception ex)
		 {
			 new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			 ex.printStackTrace();
			 return false;
		 }
	}

	//读取result文件
	public boolean XYKReadResult()
	{		
		
		try
        {
            
            int type = Integer.parseInt(bld.type.trim());
            
            if(result == null)
            {
            	return false;
            }             
            else //if (result != null)
            {
            	bld.retcode  =  Dispatch.callN(res, "GetValue",new Object[] { "rejCode" }).toString() ; //返回码
            	if(!(bld.retcode.trim().equals("00")))
            	{
            		bld.retmsg = bld.retcode + "," + Dispatch.callN(res, "GetValue",new Object[] { "RejCodeExplain" }).toString(); //错误说明
            		return false;
            	}
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
            	{
                	bld.cardno = Dispatch.callN(res, "GetValue",new Object[] { "RejCodeExplain" }).toString();   //卡号
                	bld.trace = Convert.toInt(Dispatch.callN(res, "GetValue",new Object[] { "OldTraceNumber" }).toString());   //流水号
                	bld.je = Convert.toDouble(Dispatch.callN(res, "GetValue",new Object[] { "Amount" }).toString());   //交易金额		
            	}  	
            	      
            }
            
			return true;
        }
		catch (Exception ex)
		{
			XYKSetError("XX","读取应答XX:"+ex.getMessage());
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();
            
			return false;
		}
		finally
        {
            
            {
                try
                {
                    req = null;
                    res = null;
                    app = null;
                    result = null;
                    
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
	}
	public boolean XYKCheckRetCode()
	{
		if (bld.memo.trim().equals("0") && bld.retcode.trim().equals("00"))
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
	
	public boolean XYKNeedPrintDoc(int type)
	{
		if (!checkBankSucceed())
	    {
	        return false;
	    }
		if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
			  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || 
			  type == PaymentBank.XYKCD || type == PaymentBank.XKQT3  )
		{
			return true;
		}
		else
			return false;
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
	
	
	public boolean checkDate(Text date)
	{
		String d = date.getText();
		if (d.length() < 6)
		{
			new MessageBox("日期格式错误\n日期格式《YYYYMMDD》");
			return false;
		}
		
		return true;
	}

	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name =path + "\\print.txt";
		try
		{
			if (!PathFile.fileExist(name))
			{
				new MessageBox("找不到签购单打印文件！！！");
				
				return ;
			}
			pb = new ProgressBox();
			
//			pb.setText("OY+状态 : "+GlobalInfo.sysPara.issetprinter +"...."+ Printer.getDefault().getStatus());
			pb.setText("正在打印银联签购单,请等待...");
			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
			{
				BufferedReader br = null;
				XYKPrintDoc_Start();
				try
				{
					br = CommonMethod.readFileGB2312(name);
					if (br == null)
					{
						new MessageBox("打开签购单文件失败");
						
						return ;
					}
					
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
							continue;
						if (line.equals("CUTPAPER"))
						{
							XYKPrintDoc_End();
							new MessageBox("请撕下客户签购单！！！");
							continue;
						}
						XYKPrintDoc_Print(line);
					}					
				}
				catch(Exception e)
				{
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
					try
					{
						br.close();
					}
					catch(IOException ie)
					{
						ie.printStackTrace();
					}					
				}
				XYKPrintDoc_End();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("打印签购单异常!!!\n" + e.getMessage());
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
			if (PathFile.fileExist(name))
			{
				PathFile.deletePath(name);
			}
		}
	}

}
