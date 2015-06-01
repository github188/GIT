package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

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

//满洲里万达
//调用动态库（模块名：GZZC；动态库(dll文件）：ChaseInterface.dll  函数：int Abmcs(void *strIn, void *strOut)；）
public class Gzzc_mzl_PaymentBankFunc extends Gzzc_lyqf_PaymentBankFunc{
	    String path = ConfigClass.BankPath;

		
		//读取result文件
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
	            
	            String result[] = line.split("\\|");
	            if (result == null) return false;

	            int type = Integer.parseInt(bld.type.trim());
	             
	            if (result.length >= 2)
	            {
	    			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
	    			// int len = line.length();
	    			//防止字节和字符不一致问题
	    			//bld.memo = Convert.newSubString(line, 2, 21);
	            	bld.retcode  = result[0];  //返回码2
	            	if(!bld.retcode.equals("00"))
	            	{
	            		bld.retmsg = result[8].trim();   //错误说明40
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
	            	{
	                	bld.cardno = result[2];   //卡号20
	                	bld.authno = result[4];   //参考号12
	                	bld.bankinfo = result[11];  //银行代码
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
//	                    if (PathFile.fileExist(path + "\\request.txt"))
//						{
//							PathFile.deletePath(path + "\\request.txt");
//						}
//
//						if (PathFile.fileExist(path + "\\result.txt"))
//						{
//							PathFile.deletePath(path + "\\result.txt");
//						}
	                }
	                catch (IOException e)
	                {
	                    e.printStackTrace();
	                }
	            }
	        }
		}
		
		
	}

