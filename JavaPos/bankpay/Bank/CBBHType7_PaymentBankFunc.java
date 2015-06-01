package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateByte;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//重百--银行卡（1），调用动态库（模块名：CQXDS；动态库(dll文件）：posinf.dll；函数：int umsbankproc(char * REQ, char * RSP)；）
public class CBBHType7_PaymentBankFunc extends PaymentBankFunc
{
	String crc ;
	public String[] getFuncItem()
	{
		String[] func = new String[3];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "当日撤消";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		/*func[3] = "[" + PaymentBank.XYKQD + "]"	+ "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";  
		func[6] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[7] = "[" + PaymentBank.XYKCD + "]" + "重打指定流水";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "分期消费";
		func[9] = "[" + PaymentBank.XKQT3 + "]" + "分期消费取消";*/
		
		return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
         //		0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch(type)
		{
			case PaymentBank.XYKXF : // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX : //消费撤销
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH :
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
				/*case PaymentBank.XYKQD : //签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ : //交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";
				break;
			case PaymentBank.XKQT1 : //重打结算单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打结算单";
				break;
			case PaymentBank.XYKYE : //查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询余额";
				break;
				
			case PaymentBank.XYKCD : //重打签购单
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
			case PaymentBank.XKQT2 : //分期消费
				grpLabelStr[0] = null;//"分期期数";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "分期总金额";
				break;
			case PaymentBank.XKQT3 : //分期消费取消
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "分期消费取消";
				break;*/
		}
		
		return true;
	}
	
	public boolean getFuncText(int type, String[] grpTextStr)
	{
//		null表示必须用户输入,不为null表示缺省显示无需改变
		switch(type)
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
			case PaymentBank.XYKTH: //隔日退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
				/*	case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ: //交易结算
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结算";
				break;
			case PaymentBank.XKQT1: //重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打结算单";
				break;
			case PaymentBank.XYKYE: //查询余额
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始查询余额";
				break;
			case PaymentBank.XYKCD: //重打签购单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打签购单";
				break;	
			case PaymentBank.XKQT2 : //分期消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始分期消费";
				break;
			case PaymentBank.XKQT3 : //分期消费取消
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键分期消费取消";
				break;*/
		}
		
		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{

			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH ))
			{			
				  new MessageBox("银联接口不支持此交易类型！！！");
				  
				  return false;
		    }
			
				BufferedReader br = null;
			
				 // 先删除上次交易数据文件
	            if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
	            {
	                PathFile.deletePath(ConfigClass.BankPath + "\\request.txt");
	                
	                if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
	                {
	            		new MessageBox("删除上次交易的请求文件失败");
	            		return false;   	
	                }
	            }
	            
	            if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
	            {
	                PathFile.deletePath(ConfigClass.BankPath + "\\result.txt");
	                
	                if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
	                {
	            		new MessageBox("删除上次交易的应答文件失败");
	            		return false;   	
	                }
	            }
	            
	            
				String request = "71011    280105  0700000000000000000000000000000000000000754";//请求文件内容
							
		        PrintWriter pw = null;
	            pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");
	            if (pw != null)
	            {
	                pw.println(request);
	                pw.flush();
	            }
	        	if (pw != null)
	        	{
	        		pw.close();
	        	}
	        	
	        	if (PathFile.fileExist(ConfigClass.BankPath + "\\javaposbank.exe"))
				{
					CommonMethod.waitForExec(ConfigClass.BankPath + "\\javaposbank.exe CQXDS");
				}
				else
				{
					new MessageBox("找不到" + ConfigClass.BankPath + "\\javaposbank.exe");
					return false;
				}
				
				if ((br = CommonMethod.readFileGB2312(ConfigClass.BankPath + "\\result.txt")) == null)
				{
					new MessageBox("读取金卡工程应答数据失败!", null, false);

					return false;
				}
				String result = "111";
				result = br.readLine();
				
				if(result != null && result.length() > 0)result = result.split(",")[1];else return false;
				
				if(result.substring(0,2).equals("00"))
				{
	/*				String track = ManipulateByte.getString(ManipulateByte.subBytes(result.getBytes(),122,300));//全部磁道信息
					String track1= ManipulateByte.getString(ManipulateByte.subBytes(track.getBytes(),0,120));//磁道1
					String track2=ManipulateByte.getString(ManipulateByte.subBytes(track.getBytes(),120,37));//磁道2
					String track3=ManipulateByte.getString(ManipulateByte.subBytes(track.getBytes(),157,104));//磁道3
	*/		
					
					String rtrack = ManipulateByte.getString(ManipulateByte.getStringBytes(result,122,423));//全部磁道信息
					PosLog.getLog(this.getClass()).info("result：【"+result+"】");
					String rtrack1= ManipulateByte.getString(ManipulateByte.getStringBytes(rtrack,0,121));//磁道1
					String rtrack2=ManipulateByte.getString(ManipulateByte.getStringBytes(rtrack,120,158));//磁道2
					String rtrack3=ManipulateByte.getString(ManipulateByte.getStringBytes(rtrack,157,262));//磁道3
					result = track1.trim()+";"+track2.trim()+";"+track3.trim()+";";

//					卡号
					bld.cardno = rtrack2.split("=")[0];
					//金额
					bld.je = money;
					
					bld.retmsg = "金卡工程调用成功！！！";
					bld.retbz = 'Y';
					return true;
				}
			
				bld.retmsg =  "金卡工程调用失败";
				errmsg = bld.retmsg ;
				bld.retbz = 'N';
				return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			XYKSetError("XX", "金卡异常XX" + e.getMessage());
			new MessageBox("调用金卡工程处理模块异常!!!\n" + e.getMessage() , null, false);
			
			return false;
		}
	}

}