package bankpay.Bank;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;


public class YlswYcgm_PaymentBankFunc  extends YlswWhf_PaymentBankFunc
{
	public String[] getFuncItem()
    {
        String[] func = new String[7];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "银联结账";
        func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[6] = "[" + PaymentBank.XYKCD + "]" + "按凭证号打印";
        
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
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
			break;
        	case PaymentBank.XYKQD: // 银联签到
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
        	break;	
        	case PaymentBank.XYKJZ: //银联内卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "银联结账";
            break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
            break;
        	case PaymentBank.XYKCD:		// 按凭证号打印
        		grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "按凭证号打印";
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
		 	case PaymentBank.XYKQD: 	//交易签到
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易签到";
            break;
		 	case PaymentBank.XYKJZ: 	//银联结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始内卡银联结账";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD:		// 按凭证号打印
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键凭证号打印";
		 	break;	
	
		}
		
		return true;
    }
	
	 public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		// PaymentBank.XYKXF: // 消费
		// PaymentBank.XYKCX: // 消费撤销
		// PaymentBank.XYKTH: // 隔日退货
		// PaymentBank.XYKYE: // 余额查询
		// PaymentBank.XYKCD: // 重打签购单
		// PaymentBank.XYKQD: // 交易签到
		// PaymentBank.XYKJZ: // 交易结帐
		
		try
		{
		    if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ))
		    {
			errmsg = "银联接口不支持该交易";
			new MessageBox(errmsg);
			
			return false;
		    }
		    
		    // 先删除上次交易数据文件
		    if (PathFile.fileExist("C:\\gmc\\request.txt"))
		    {
			PathFile.deletePath("C:\\gmc\\request.txt");
			
			if (PathFile.fileExist("C:\\gmc\\request.txt"))
			{
			    errmsg = "交易请求文件request.txt无法删除,请重试";
			    XYKSetError("XX", errmsg);
			    new MessageBox(errmsg);
			    return false;
			}
		    }
		    
		    if (PathFile.fileExist("C:\\gmc\\result.txt"))
		    {
			PathFile.deletePath("C:\\gmc\\result.txt");
			
			if (PathFile.fileExist("C:\\gmc\\result.txt"))
			{
			    errmsg = "交易请求文件result.txt无法删除,请重试";
			    XYKSetError("XX", errmsg);
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
			if (PathFile.fileExist("C:\\gmc\\javaposbank.exe"))
			{
			    CommonMethod.waitForExec("C:\\gmc\\javaposbank.exe YLSWWHF");
			}
			else
			{
			    new MessageBox("找不到金卡工程模块 javaposbank.exe");
			    XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
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
		    
		    // 打印签购单
		    if (XYKNeedPrintDoc())
		    {
			
			XYKPrintDoc();
		    }
		    
		    return true;
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		    XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
		    new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
		    
		    return false;
		}
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		 String line = "";
		 String mtype1 = "";
		 PrintWriter pw = null;
		 
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
			 		mtype1 = "00";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		mtype1 = "01";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	mtype1 = "02";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	mtype1 = "03";
                break;	
                case PaymentBank.XYKCD: 	// 重打签购单
                	mtype1 = "04";
                break;
                case PaymentBank.XYKQD:     // 交易签到
                	mtype1 = "05";
                break;
                case PaymentBank.XYKJZ:     // 交易结帐
                	mtype1 = "06";
                break;
                default:
                    return false;
			 }
			 
			 //’00’-消费|’01’-撤销|’02’-退货|’03’-查余额|‘04’重打指定流水|‘05’签到|‘06’结算
			 //收银机号(8-左对齐,右补空格)
			 //+操作员号(8-左对齐，右补空格)
			 //+交易类型(2)
			 //+金额(12-无小数点，左补0保留两个小数)
			 //+原交易日期(8-YYYYMMDD,退货时填写)
			 //+原系统参考号(12-退货时用)
			 //+原流水号(6-右对齐，左补0)
			 //+二磁道数据(37-左对齐,右补空格)
			 //+三磁道数据(104-左对齐,右补空格)
			 //+交易校验数据(3位，从0~9的随机字符)
			 
	         //收银机号
	         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
	         //收银员号
	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
	         //交易类型
	         String type1 = mtype1;
	         //金额
			 String jestr = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1)),'0',12);
	         //原交易日期
	         String olddate1 = Convert.increaseChar(olddate,' ',8);
	         //原系统参考号
	         String oldsysref = Convert.increaseChar(oldauthno,' ',12);
	         //原流水号
	         String oldseqno1 = Convert.increaseCharForward(oldseqno,'0',6);
	         
	         //校验位
	         String crc = XYKGetCRC();
	         
	         line = syjh+syyh+type1+jestr+olddate1+oldsysref+oldseqno1+crc;
	    
	         bld.crc = crc;
	         bld.type = type1;
	    	
	         try
	         {
	            pw = CommonMethod.writeFile("C:\\gmc\\request.txt");
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
