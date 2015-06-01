package bankpay.Bank;

import java.io.BufferedReader;
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

  
 class KeeperClient 
 {
     //	调用动态库的方法
	 static native String misposTrans(String input);
	 
	 static
	 {
		 System.loadLibrary("JNIKeeperClient");
	 }
 }
  //宜春青龙，工商银行接口。
public class ICBCYcql_PaymentBankFunc extends PaymentBankFunc
{
	/*
	// 使用JNA技术，在Java中调用动态库
	public  interface KeeperClient extends Library
	{
		KeeperClient INSTANCE = (KeeperClient)Native.loadLibrary("KeeperClient",KeeperClient.class);
		
		public int misposTrans(String in, PointerByReference point); //动态库函数
	}
	*/
	
	public String[] getFuncItem()
    {
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打上笔签购单";
		
     
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
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = "原终端号";
				grpLabelStr[2] = "交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;   
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
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
			case PaymentBank.XYKQD://交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XKQT1://重打上笔签购单
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
			case PaymentBank.XYKTH://隔日退货 
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
			case PaymentBank.XKQT1://重打上笔签购单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始签购单重打";
				break;
		}

		return true;
	}
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (!((type == PaymentBank.XYKXF) || 
					(type == PaymentBank.XYKCX) || 
					(type == PaymentBank.XYKYE) || 
					(type == PaymentBank.XYKCD) || 
					(type == PaymentBank.XYKQD) ||
					(type == PaymentBank.XYKJZ) ||
					(type == PaymentBank.XKQT1) ||
					(type == PaymentBank.XYKTH) 
					) )
		            {
		                errmsg = "MISPOS接口不支持该交易";
		                new MessageBox(errmsg);

		                return false;
		            }
		
            
            if (PathFile.fileExist(ConfigClass.BankPath + "\\ICBCPRTTKT.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath + "\\ICBCPRTTKT.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath + "\\ICBCPRTTKT.txt"))
                {
            		errmsg = "签购单打印文件print.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            // 得到请求数据
            String rs = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
           
            // 调用动态库函数
             rs = KeeperClient.misposTrans(rs);
            
            //读取返回数据
             if (rs == null)
             {
            	 new MessageBox("调用动态库KeeperClient.dll失败！！");
            	 
            	 return false;
             }
            if(!XYKReadResult(rs)) return false;
           // 检查交 否成功
            if (!XYKCheckRetCode()) return false;
                   
   				// 打印签购单
   			if (XYKNeedPrintDoc(type))
   				{
   					XYKPrintDoc();
   				}
            
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		return false;
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
	
	public String XYKgetRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		//这里要生成一个1825个字符长度的字符串，手动赋值消耗最小的。
		String line = "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"			 
			+ "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"			 
			+ "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"
			+ "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" 
			+ "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"
		    + "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"
		    + "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"
		    + "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"
		    + "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"
		    + "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"
		    + "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"
		    + "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222";
		StringBuffer sb = new StringBuffer(line);	
		line = null;
		try
		{
			 String jestr = ManipulatePrecision.doubleToString(money);
			 String platId = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 20);			
			 String operId = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 20);//操作员号
			 String TransType = "";//交易类型
			 String Amount = Convert.increaseCharForward(jestr, '0', 12);;// 金额
			 String ReferNo = Convert.increaseChar(" ", ' ', 8);// 系统检索号
			 String TerminalId = Convert.increaseChar(" ", ' ', 15);// 原交易终端号
			 String OldAuthDate = Convert.increaseChar(" ", ' ', 8);//原交易日期
		     		
			 //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		TransType = "05";	
	         		sb.replace(21, 33, Amount);
	         		line = TransType + Amount + platId + operId ;
	         	break; 
	         	case PaymentBank.XYKCX:
	         		TransType = "04";
	         		ReferNo = Convert.increaseChar(oldseqno, '0',8);
	         		OldAuthDate = ManipulateDateTime.staticGetDateBySlash();
	         		sb.replace(216, 224,ReferNo);
	         		sb.replace(406, 414, OldAuthDate);
	         		sb.replace(232, 247, TerminalId);
	         	break;
	         	case PaymentBank.XYKTH:
	         		TransType = "04";
	         		ReferNo = Convert.increaseChar(oldseqno, '0',8);
	         		TerminalId = Convert.increaseChar(oldauthno, '0', 15);
	         		OldAuthDate = Convert.increaseChar(olddate, ' ', 8);
	         		sb.replace(216, 224,ReferNo);
	         		sb.replace(406, 414, OldAuthDate);
	         		sb.replace(232, 247, TerminalId);
	         	break;
	         	case PaymentBank.XYKQD:
	         		TransType = "09";
	         	break;
	         	case PaymentBank.XYKYE:
	         		TransType = "10";
	         	break;
	         	case PaymentBank.XYKCD:
	         		TransType = "13";
	         		ReferNo = Convert.increaseChar(oldseqno, '0',8);
	         		sb.replace(216, 224,ReferNo);
	         	break;
	         	case PaymentBank.XKQT1:
	         		TransType = "13";
	         		ReferNo = "00000000";
	         		sb.replace(216, 224,ReferNo);
	         	break;
	         	case PaymentBank.XYKJZ:
	         		TransType = "15";
	         	break;	         	
	         }
	         
	        sb.replace(0, 2, TransType);			
			sb.replace(1785,1805,platId);
			sb.replace(1805, 1825, operId);				         
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();
		}
		return sb.toString();
	}
	
	public boolean XYKReadResult(String line)
	{
		try
		{
			if (line == null || line == "" || line.length() < 2)
				return false;

			bld.retcode = line.substring(230,232);
			
			if(bld.retcode.equals("00"))
			{
				//bld.je = Double.parseDouble(line.substring(21, 33));
				bld.trace = Long.parseLong(line.substring(504, 510));
				bld.authno = line.substring(216, 224);
				bld.bankinfo = line.substring(464, 504);
				bld.cardno = line.substring(2, 19);
				bld.memo = line.substring(232, 247);
				line = null;
				return true;
			}
			else
			{
				bld.retmsg = line.substring(1361, 1461);
				line = null;
				return false;
			}
		}catch (Exception e)
		{
			line = null;
			new MessageBox("读取金卡工程返回数据异常!\n\n" + e.getMessage(), null, false);
			e.printStackTrace();
			System.out.println(e);
			return false;
		}
	}
	
	public boolean checkDate(Text date)
	{
		String date1 = date.getText();
		if (date1.length() > 8)
		{
			new MessageBox("请输入日期\n日期格式《YYYYMMDD》");
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
				(type == PaymentBank.XYKTH) ||
				(type == PaymentBank.XKQT1)
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
        String printName = ConfigClass.BankPath + "\\ICBCPRTTKT.txt";
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
