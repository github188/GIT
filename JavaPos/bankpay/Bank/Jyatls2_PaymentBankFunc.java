package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Payment.PaymentBank;
//三亚港华城广场的银联接口

//调用动态库（模块名：NJYSMZDABC；动态库(dll文件）：softpos.dll；函数：int  CreditTransABC( char *strin, char *strout )；）
public class Jyatls2_PaymentBankFunc extends Jyatls_PaymentBankFunc 
{
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
				  type == PaymentBank.XKQT1 ))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
//			获得金卡文件路径
			path = ConfigClass.BankPath;
//			path = getBankPath(paycode);
			
			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");
				if (PathFile.fileExist(path + "\\request.txt"))
				{
					errmsg = "交易“request.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist(path + "\\result.txt"))
			{
				PathFile.deletePath(path + "\\result.txt");
				if (PathFile.fileExist(path + "\\result.txt"))
				{
					errmsg = "交易“result.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist(path + "\\toprint.txt"))
            {
                PathFile.deletePath(path + "\\toprint.txt");
            }
			
			
			
//			多种支付方式，选择
//			String code = "";
//			String[] title = { "代码", "应用类型" };
//			int[] width = { 60, 440 };
//			Vector contents = new Vector();
//			contents.add(new String[] { "1", "银行卡" });
//			contents.add(new String[] { "2", "卡、DCC" });
//			contents.add(new String[] { "3", "工行积分、分期" });
//			contents.add(new String[] { "4", "银商预付卡" });
//			contents.add(new String[] { "5", "亚盟卡" });
//			contents.add(new String[] { "6", "重庆通卡" });
//			contents.add(new String[] { "7", "其他应用" });
//			contents.add(new String[] { "8", "统一预付卡" });
//			
//			int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
//			if (choice == -1)
//			{
//				errmsg = "没有选择应用类型";
//				return false;
//			}else {
//				String[] row = (String[]) (contents.elementAt(choice));
//				code = row[0];
//			}
			
			

			
			
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(path+"\\javaposbank.exe NJYSMZDABC","javaposbank");
            	//CommonMethod.waitForExec(path+"\\gmc.exe");
            }
            else
            {
                new MessageBox("找不到金卡工程模块 javaposbank.exe");
                XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
                return false;
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
//				XYKPrintDoc(type);
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
	
	
	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist(path+"\\result.txt") || ((br = CommonMethod.readFileGBK(path+"\\result.txt")) == null))
            {
            	XYKSetError("XX","读取应答失败,交易失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String line = br.readLine();

            if (line == null || line.length() <= 0)
            {
                return false;
            }
            
            int type = Integer.parseInt(bld.type.trim());
             
            if (line.length() >= 2)
            {
    			//bld.memo = Convert.newSubString(line, 2, 21);
            	bld.retcode  = line.substring(0,2);  //返回码2
            	if(!bld.retcode.equals("00"))
            	{
            		bld.retmsg = "响应码不正确,交易失败";   //错误说明
            		return false;
            	}
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
            	{
                	bld.cardno = line.substring(2,21);   //卡号19
                	bld.trace = Integer.parseInt(line.substring(34,40));   //流水号16
                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(line.substring(22,34) ),100),2,1);   //交易金额	
//                	//优惠标识
//                	bld.memo = line.substring(95,96);
//                	//刷卡金额(实付金额)
//                	bld.allotje = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(line.substring(96,108) ),100),2,1); 
//                	//优惠/积分金额（银行支付）
//                	bld.kye = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(line.substring(108,120) ),100),2,1); 
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
            if (br != null)
            {
                try
                {
                    br.close();
                    if (PathFile.fileExist(path + "\\request.txt"))
					{
						PathFile.deletePath(path + "\\request.txt");
					}

					if (PathFile.fileExist(path + "\\result.txt"))
					{
						PathFile.deletePath(path + "\\result.txt");
					}
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
	}
}
