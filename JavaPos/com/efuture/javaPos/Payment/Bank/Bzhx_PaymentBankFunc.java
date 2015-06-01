package com.efuture.javaPos.Payment.Bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;

public class Bzhx_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
    {
        String[] func = new String[16];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "中行结账";
        func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[7] = "[" + PaymentBank.XKQT1 + "]" + "信贷卡消费";
        func[8] = "[" + PaymentBank.XKQT2 + "]" + "积分卡消费";
        func[9] = "[" + PaymentBank.XKQT3 + "]" + "信贷卡消费撤销";
        func[10] = "[" + PaymentBank.XKQT4 + "]" + "积分卡消费撤销";
        func[11] = "[" + PaymentBank.XKQT5 + "]" + "信贷卡结账";
        func[12] = "[" + PaymentBank.XKQT6 + "]" + "积分卡结账";
        func[13] = "[" + PaymentBank.XKQT7 + "]" + "重打中行结账单";
        func[14] = "[" + PaymentBank.XKQT8 + "]" + "重打信贷卡结账单";
        func[15] = "[" + PaymentBank.XKQT9 + "]" + "重打积分卡结账单";
        
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
	            grpLabelStr[3] = "请 刷 卡";
	            grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "交易金额";

            break;
            case PaymentBank.XYKTH: //隔日退货   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "无效";

            break;
        	case PaymentBank.XYKQD: //交易签到
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //中行结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "中行结账";
            break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "余额查询";
            break;
        	case PaymentBank.XYKCD: //签购单重打
               grpLabelStr[0] = null;
               grpLabelStr[1] = null;
               grpLabelStr[2] = null;
               grpLabelStr[3] = null;
               grpLabelStr[4] = "重打上笔签购单";
            break;
        	case PaymentBank.XKQT1: //	资和信卡消费
        		grpLabelStr[0] = null;
 	            grpLabelStr[1] = null;
 	            grpLabelStr[2] = null;
 	            grpLabelStr[3] = "请 刷 卡";
 	            grpLabelStr[4] = "交易金额";
        	break;	
        	case PaymentBank.XKQT2: //	积分卡消费
        		grpLabelStr[0] = null;
 	            grpLabelStr[1] = null;
 	            grpLabelStr[2] = null;
 	            grpLabelStr[3] = "请 刷 卡";
 	            grpLabelStr[4] = "交易金额";
            break;	
        	case PaymentBank.XKQT3: //	信贷卡消费撤销
        		grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XKQT4: //积分卡消费撤销
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "交易金额";

            break;
        	case PaymentBank.XKQT5: //信贷卡卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "信贷卡结账";
            break;
         	case PaymentBank.XKQT6: //积分卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "积分卡结账";
            break;
         	case PaymentBank.XKQT7: //重打中行结账单
         		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打中行结账单";
         	break;	
         	case PaymentBank.XKQT8: //重打信贷卡结账单
         		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打信贷卡结账单";
         	break;	
         	case PaymentBank.XKQT9: //重打资和信结账单
         		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打积分卡结账单";
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
		 	 case PaymentBank.XYKTH: //退货
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "银联不支持该功能";

	        break;
		 	case PaymentBank.XYKQD: 	//交易签到
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易签到";
            break;
		 	case PaymentBank.XYKJZ: 	//中行结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始中行结账";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "开始签购单重打";
            break;
		 	case PaymentBank.XKQT1: 	// 信贷卡消费
		 	      grpTextStr[0] = null;
			      grpTextStr[1] = null;
			      grpTextStr[2] = null;
			      grpTextStr[3] = null;
			      grpTextStr[4] = null;
            break;
		 	case PaymentBank.XKQT2: 	// 积分卡消费
		 	      grpTextStr[0] = null;
			      grpTextStr[1] = null;
			      grpTextStr[2] = null;
			      grpTextStr[3] = null;
			      grpTextStr[4] = null;
			break;
		 	case PaymentBank.XKQT3: 	// 信贷卡撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XKQT4: 	// 积分卡消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XKQT5: 	//	信贷卡结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始信贷卡结账";
	        break;
		 	case PaymentBank.XKQT6: 	//	积分卡结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始积分卡结账";
	        break;
		 	case PaymentBank.XKQT7: //重打中行结账单
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "重打中行结账单";
		 	break;	
		 	case PaymentBank.XKQT8: //重打信贷卡结账单
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "重打信贷卡结账单";
		 	break;	
		 	case PaymentBank.XKQT9: //重打资和信结账单
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "重打积分卡结账单";
		 	break;
		}
		 
		return true;
    }
	
	public boolean callBankFunc(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno,String olddate,Vector memo)
	{
		if (memo != null && memo.size() > 0)
		{
			if (type == PaymentBank.XYKXF && (memo.get(0).equals("0341") || memo.get(0).equals("0344") || memo.get(0).equals("0323")))
			{
				type = PaymentBank.XYKXF;
			}
			else if (type == PaymentBank.XYKXF && memo.get(0).equals("0347"))
			{
				type = PaymentBank.XKQT1; 
			}
			else if (type == PaymentBank.XYKCX  && (memo.get(0).equals("0341") || memo.get(0).equals("0344") || memo.get(0).equals("0323")))
			{
				type = PaymentBank.XYKCX;
			}
			else if (type == PaymentBank.XYKCX  && memo.get(0).equals("0347"))
			{
				type = PaymentBank.XKQT3;
			}
		}
		else
		{
			if (type == PaymentBank.XKQT4)
	 		{
	 			type = PaymentBank.XYKCX;
	 		}
		}
		
		return super.callBankFunc(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);	
	}
	
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
                	CommonMethod.waitForExec("c:\\javaposbank.exe BZHX");
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
	
	public boolean XYKNeedPrintDoc()
    {
        if (!checkBankSucceed())
        {
            return false;
        }

        int type = Integer.parseInt(bld.type.trim());

        // 消费，消费撤销，重打签购单
        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
                (type == PaymentBank.XYKJZ) || (type == PaymentBank.XKQT1) ||
                (type == PaymentBank.XKQT2) || (type == PaymentBank.XKQT3) ||
                (type == PaymentBank.XKQT4) || (type == PaymentBank.XKQT5) ||
        		(type == PaymentBank.XKQT6 || type == PaymentBank.XKQT7)   ||
        		(type == PaymentBank.XKQT8 || type == PaymentBank.XKQT9))
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
        if (bld.retcode.trim().equals("0"))
        {
            bld.retbz  = 'Y';
            bld.retmsg = "金卡工程调用成功";

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
	         		line = String.valueOf(type) + "," + jestr + "," + track2 + "," + track3 + "," + track22 + "," + track33 +",0";
	         	break;
	         	case PaymentBank.XYKCX:				// 银联撤消
	         	case PaymentBank.XKQT3:				// 资合信卡撤消
	         	case PaymentBank.XKQT4:				// 积分卡消费撤消		
	         		line = String.valueOf(type) + "," + seq + "," + jestr + "," + track2 + "," + track3 + "," + track22 + "," + track33 +",0";
	         	break;
	         	case PaymentBank.XYKYE:				// 查询余额
	         		line = String.valueOf(type) + "," + track2 + "," + track3 + ",0";
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
	 
	 public boolean XYKReadResult()
	 {
		 BufferedReader br = null;
		 
		 try
		 {
			 if (!PathFile.fileExist("c:\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\result.txt")) == null))
			 {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);

                return false;
			 }
			 
			 String line = br.readLine();
			 
			 if (line.length() <= 0)
			 {
                return false;
			 }
			 
			 if (line.indexOf(",") != -1)
			 {
                bld.retcode = line.trim().substring(0, line.indexOf(","));
                bld.retmsg  = line.trim().substring(line.indexOf(',') + 1);
			 }
			 else
			 {
                bld.retcode = line;
			 }
			 
			 if ((bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XYKCX))) ||  
				 (bld.type.equals(String.valueOf(PaymentBank.XKQT1)) || bld.type.equals(String.valueOf(PaymentBank.XKQT2))) || 
				 (bld.type.equals(String.valueOf(PaymentBank.XKQT3)) || bld.type.equals(String.valueOf(PaymentBank.XKQT4))) 
				 && bld.retcode.equals("0"))
			 {
				 BufferedReader br1 = null;
				 
				 try
	             {
					 //加入打印签购单
					 br1 = CommonMethod.readFileGBK("c:\\u\\print\\print.txt");
					 
					 if (br1 == null)
					 {
                        new MessageBox("打开c:\\u\\print\\print.txt打印文件失败!");

                        return false;
					 }
					 
					 String line1 = null;
					 
					 while ((line1 = br1.readLine()) != null)
					 {
                        if (line1.indexOf("卡号:") != -1)
                        {
                            bld.cardno = line1.substring(line1.indexOf("卡号:") +
                                                         3);
                        }
                        else if (line1.indexOf("流水号:") != -1)
                        {
                            try
                            {
                                bld.trace = Long.parseLong(line1.substring(line1.indexOf("流水号:") + 4));
                            }
                            catch (Exception er)
                            {
                                new MessageBox(er.getMessage());
                            }
                        }
					 }
					 
	             }
				 catch (Exception ex)
				 {
					 new MessageBox(ex.getMessage());
				 }
				 finally
				 {
                    if (br1 != null)
                    {
                        br1.close();
                    }
				 }
			 }

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
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
			 }
         }
		 
		 return true;
	 }
	 
	 public void XYKPrintDoc()
	 {
		 ProgressBox pb = null;
		 
		 try
		 {
			 String printName = "";
			 if ((bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XYKCX))) ||
				 (bld.type.equals(String.valueOf(PaymentBank.XKQT1)) || bld.type.equals(String.valueOf(PaymentBank.XKQT2)))	 ||
				 (bld.type.equals(String.valueOf(PaymentBank.XKQT3)) || bld.type.equals(String.valueOf(PaymentBank.XKQT4)))  
				 )
			 {
                if (!PathFile.fileExist("C:\\u\\print\\print.txt"))
                {
                    new MessageBox("找不到签购单打印文件!");

                    return;
                }
                else
                {
                    printName = "C:\\u\\print\\print.txt";
                }
			 }
			 else if ((bld.type.equals(String.valueOf(PaymentBank.XYKCD))))
			 {
                if (PathFile.fileExist("C:\\u\\print\\reprint.txt"))
                {
                	printName = "C:\\u\\print\\reprint.txt";
                }
                else if (PathFile.fileExist("C:\\u\\print\\print.txt"))
                {
                	printName = "C:\\u\\print\\print.txt";
                }
                else
                {
                    new MessageBox("找不到签购单重打印文件!");

                    return;                   
                }
			 }
			 else if ((bld.type.equals(String.valueOf(PaymentBank.XYKJZ))))
			 {
                if (!PathFile.fileExist("C:\\u\\print\\settzh.txt"))
                {
                    new MessageBox("找不到中行结账打印文件!");

                    return;
                }
                else
                {
                    printName = "C:\\u\\print\\settzh.txt";
                }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT5)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\settzhx.txt"))
				 {
                    new MessageBox("找不到资合信结账打印文件!");

                    return;
				 }
				 else
				 {
                    printName = "C:\\u\\print\\settzhx.txt";
				 }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT6)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\settjfk.txt"))
				 {
                    new MessageBox("找不到积分卡结账打印文件!");

                    return;
				 }
				 else
				 {
                    printName = "C:\\u\\print\\settjfk.txt";
				 }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT7)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\resettzh.txt"))
				 {
                    new MessageBox("找不到中行重印结账打印文件!");

                    return;
				 }
				 else
				 {
                    printName = "C:\\u\\print\\resettzh.txt";
				 }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT8)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\resettzhx.txt"))
				 {
                    new MessageBox("找不到资合信重印结账打印文件!");

                    return;
				 }
				 else
				 {
                    printName = "C:\\u\\print\\resettzhx.txt";
				 }
			 }
			 else if (bld.type.equals(String.valueOf(PaymentBank.XKQT9)))
			 {
				 if (!PathFile.fileExist("C:\\u\\print\\resetjfk.txt"))
				 {
                    new MessageBox("找不到积分卡重印结账打印文件!");

                    return;
				 }
				 else
				 {
                    printName = "C:\\u\\print\\resetjfk.txt";
				 }
			 }
			 else
			 {
                new MessageBox("此金卡工程操作没有打印文件");

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

                    //
                    String line = null;

                    while ((line = br.readLine()) != null)
                    {
                        if (line.trim().equals("CUT"))
                        {
                            break;
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
		 }
	 }
	 
	/* public void XYKPrintDoc_Print(String printStr)
	 {
        if (onceprint)
        {
            Printer.getDefault().printLine_Normal(printStr);
        }
        else
        {
            printdoc.println(printStr);
        }
	 }*/
	 
	/* public void XYKPrintDoc_End()
	 {
        if (onceprint)
        {
            Printer.getDefault().cutPaper_Normal();
        }
        else
        {
            printdoc.flush();
            printdoc.close();
            printdoc = null;
        }
	 }*/
	 
	 public int getMemberInputMode()
	 {
    	return TextBox.MsrInput;
	 }
	 
	 public boolean checkBankOperType(int operType,SaleBS saleBS,PaymentBank payObj)
	 {
		 if (!super.checkBankOperType(operType, saleBS, payObj)) return false;
		 
		 if (saleBS == null && (operType == PaymentBank.XKQT1 || operType == PaymentBank.XKQT2) && !salebyself)
		 {
			 new MessageBox("不允许进行该银联操作,请重新选择");
			 return false;
		 }
		 
		 return true;
	 }
}


