package com.efuture.javaPos.Payment.Bank;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

public class Ysty_PaymentBankFunc extends Bzhx_PaymentBankFunc
{
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		 try
		 { 
			 if (type == PaymentBank.XYKTH)
			 {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
			 }
			 
			 //	先删除上次交易数据文件
			 if (PathFile.fileExist("c:\\request.txt"))
			 {
                PathFile.deletePath("c:\\request.txt");
                
                if (PathFile.fileExist("c:\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
			 }
	         
			 if (PathFile.fileExist("c:\\result.txt"))
			 {
                PathFile.deletePath("c:\\result.txt");
                
                if (PathFile.fileExist("c:\\result.txt"))
                {
            		errmsg = "交易结果文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
			 }
			 
			 String track22 = "";
			 String track33 = "";
			 
			 if (type == PaymentBank.XKQT1 || type == PaymentBank.XKQT3)
			 {
				 StringBuffer cardno = new StringBuffer();
				 //输入顾客卡号
				 TextBox txt = new TextBox();
				 if (!txt.open("请刷中行信用卡", "中行卡号", "请将中行信用卡从刷卡槽刷入", cardno, 0, 0,false, getMemberInputMode()))
				 {
					new MessageBox("打开中行信用卡刷卡界面失败");
		            return false;
				 }
			 
				 track22 = txt.Track2;
				 track33 = txt.Track3;
			 }
			 
			 //	写入请求数据
			 if (!XYKWriteRequest(type, money, track1, track2, track3,track22,track33,oldseqno, oldauthno, olddate, memo))
			 {
				return false;
			 }
	         
			 if (bld.retbz != 'Y')
			 {
                // 调用接口模块
                if (PathFile.fileExist("c:\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("c:\\javaposbank.exe YSTY");
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
			 }
			 
			 //	打印签购单
			 if (XYKNeedPrintDoc())
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
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String track22,String track33,String oldseqno, String oldauthno,String olddate, Vector memo)
	 {
		 try
		 {
			 String line = "";

	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	            
	         //	流水号
	         String seq = "";
	           
	         if ((oldseqno == null) || (oldseqno.length() <= 0))
	         {
               seq = Convert.increaseInt(0, 6);
	         }
	         else
	         {
               try
               {
                   int num_seq = Integer.parseInt(oldseqno);
                   seq = Convert.increaseInt(num_seq, 6);
               }
               catch (Exception er)
               {
                   seq = Convert.increaseInt(0, 6);
               }
	         }
	         
	         //根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:				// 银联消费
	         	case PaymentBank.XKQT2:				// 积分卡消费	
	         	case PaymentBank.XKQT1:				// 资合信卡消费
	         		line = String.valueOf(type) + "," + jestr + "," + track2 + "," + track3 + "," + track22 + "," + track33 +",0,0";
	         	break;
	         	case PaymentBank.XYKCX:				// 银联撤消
	         	case PaymentBank.XKQT3:				// 资合信卡撤消
	         	case PaymentBank.XKQT4:				// 积分卡消费撤消		
	         		line = String.valueOf(type) + "," + seq + "," + jestr + "," + track2 + "," + track3 + "," + track22 + "," + track33 +",0,0";
	         	break;
	         	case PaymentBank.XYKYE:				// 查询余额
	         		line = String.valueOf(type) + "," + track2 + "," + track3 + ",0,0";
	         	break;
	         	case PaymentBank.XYKQD:				// 交易签到
	         		line = String.valueOf(type) + ",00" + GlobalInfo.syjDef.syjh;
	         	break;
	         	case PaymentBank.XYKJZ:				// 银联卡结账
	         	case PaymentBank.XKQT5:				// 资和信卡结账
	         	case PaymentBank.XKQT6:				// 积分卡结账
	         		line = String.valueOf(type);
	         	break;	
	         	default:
                   bld.retbz = 'Y';
	         	return true;
	         	
	         }
	         
	         PrintWriter pw = null;
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\request.txt");
	            if (pw != null)
	            {
	                pw.println(line);
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
			 new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
	         ex.printStackTrace();
			 return false;
		 }
	 }
}
