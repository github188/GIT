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
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//商丘乐易工行接口，调用动态库（模块名：MAS；动态库(dll文件）：KeeperClient.dll；函数：int __stdcall misposTrans(void* input, void* output)；）
public class SqlyICBC_PaymentBankFunc extends PaymentBankFunc {
	
	String path = "C:\\gmc";
	String fq = "01";
	public String[] getFuncItem()
	{
		String[] func = new String[10];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "退货";
		func[3] = "[" + PaymentBank.XYKQD + "]"	+ "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打上笔签单";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "积分消费";
		func[9] = "[" + PaymentBank.XKQT3 + "]" + "分期付款交易";
		
		return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
        //0-4对应FORM中的5个输入框
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
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH : //退货
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = "原终端号";
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD : //签到
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
			case PaymentBank.XYKYE : //查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询余额";
				break;
				
			case PaymentBank.XYKCD : //重打签购单
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
				
			case PaymentBank.XKQT1 : //重打上笔签购单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
			case PaymentBank.XKQT2 : //积分消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "积分消费";
				break;
			case PaymentBank.XKQT3 : //分期付款交易
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
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
			case PaymentBank.XYKQD: //交易签到
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
				grpTextStr[4] = "按回车键重打签购单";
				break;	
			case PaymentBank.XKQT1: //重打上笔签购单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键重打上笔签购单";
				break;
			case PaymentBank.XKQT2: //积分消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "积分消费";
				break;
			case PaymentBank.XKQT3: //分期付款交易
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
		}
		
		return true;
	}
	
//	public boolean checkBankOperType(int operType, SaleBS saleBS, PaymentBank payObj){
//		
//		boolean ok = false;
//		if(saleBS != null)
//		{
//			if(
//			    ((SellType.ISSALE(saleBS.saletype) || saleBS.isRefundStatus()) && operType != PaymentBank.XYKXF) ||
//			    ((SellType.ISBACK(saleBS.saletype) && !saleBS.isRefundStatus()) && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH)
//			  )
//			    
//			{
//				// 销售交易或者扣回时,只允许选择0(消费)
//				// 退货交易且非扣回时,只允许选择1(撤销),2(退货)
//				ok = false;
//				
//			}
//		}
//		else
//		{
//			if(// 删除付款时只允许选择1(撤销),2(退货)
//					// 交易红冲时只允许选择1(撤销)
//					// 后台退货时只允许选择1(撤销),2(退货)
//					// 非小票交易不允许选择0(消费)
//					(payObj != null && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH) ||
//					(payObj != null && SellType.ISHC(payObj.salehead.djlb) && operType != PaymentBank.XYKCX) ||
//					(payObj != null && SellType.ISBACK(payObj.salehead.djlb) && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH) ||
//					(!salebyself && operType == PaymentBank.XYKXF)
//			 )
//			{
//				ok = false;
//			}
//			
//		}
//		
//		if(!ok){
//			new MessageBox("不允许进行该银联操作");
//			return false;
//		}
//		
//		if(operType == PaymentBank.XYKTH && ((","+GlobalInfo.sysPara.saleAppendStatus+",").indexOf(","+GlobalInfo.posLogin.role+",")<0))
//		{
//			new MessageBox("该收银员没有退货权限");
//			return false;
//		}
//		return true;
//	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || type == PaymentBank.XYKQD ||
				  type == PaymentBank.XYKJZ || type == PaymentBank.XYKYE ||
				  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 ||
				  type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3 ) )
			{			
				  new MessageBox("银联接口不支持此交易类型！！！");
				  
				  return false;
		    }
			
			if(ConfigClass.BankPath != null && !"".equals(ConfigClass.BankPath.trim()) )
			{
				path = ConfigClass.BankPath;
			}
			
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
			
			
			//////////选择：消费、快速消费、积分消费/////////////
			if(type == PaymentBank.XYKXF)
			{
				//多种支付方式，选择
				String code = "";
				String[] title = { "代码","消费类型 "};
				int[] width = { 60, 440 };
				Vector contents = new Vector();
				contents.add(new String[] { "1", "消费" });
				contents.add(new String[] { "2", "积分消费" });
				contents.add(new String[] { "3", "分期付款交易" });
				int choice = new MutiSelectForm().open("请选择消费类型", title, width, contents, true);
				if (choice == -1)
				{
					errmsg = "没有选择消费类型";
					return false;
				}else {
					String[] row = (String[]) (contents.elementAt(choice));
					code = row[0];
					if(code.equals("2"))
					{
						type = PaymentBank.XKQT2;  //积分消费
					}
					else if(code.equals("3"))
					{
						type = PaymentBank.XKQT3;  //分期交易
					}
				}
			}
			
            //输入分期期数
			if(type == PaymentBank.XKQT3)
			{
				StringBuffer bf = new StringBuffer(); 
	            TextBox txt = new TextBox();
	            if (!txt.open("请刷分期付款的期数", "分期期数", "请填正整数", bf, 0, 0, false, -1))
	    		{
	    			return true;
	    		}
	            fq = bf.toString();
	            
			}

			
			if (!XYKWriteRequest(type,money,track1,track2,track3,oldseqno,oldauthno,olddate,memo)) return false;
			
			if (PathFile.fileExist(path + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(path + "\\javaposbank.exe MAS");
			}
			else
			{
				errmsg = "找不到金卡工程模块 MAS";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				
				return false;
			}
			
			if (!XYKReadResult(type))
			{
				return false;
			}
			
			XYKCheckRetCode();
			
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc();
			}
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			XYKSetError("XX", "金卡异常XX" + e.getMessage());
			new MessageBox("调用金卡工程处理模块异常!!!\n" + e.getMessage() , null, false);
			
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

	
	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		
//		strncpy(inputData.TransType,szRequest[0],2);   //交易指令 
//		strncpy(inputData.TransAmount,szRequest[1],12);  //交易金额
//		strncpy(inputData.ReferNo,szRequest[2],8);      //系统检索号
//		strncpy(inputData.TransDate,szRequest[3],8);    //交易日期 
//		strncpy(inputData.TerminalId,szRequest[4],15);   //交易终端号
//		strncpy(inputData.platId,szRequest[5],20);     //收银机号
//		strncpy(inputData.operId,szRequest[6],20);     //操作员号
//		strncpy(inputData.PreInput,szRequest[7],256);	//预输入项
//		strncpy(inputData.AddDatas,szRequest[8],256);   //固定输入项
//		strncpy(inputData.InstallmentTimes,szRequest[9],2); //分期期数
//		strncpy(inputData.FuncID,szRequest[10],4);    //分行特色脚本ID号
		  
		try
		{
			String line = "";
			String jestr = String.valueOf((long)ManipulatePrecision.doubleConvert(money*100, 2, 1));
			String je = Convert.increaseCharForward(jestr, '0', 12);
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 20); //收银员号
			String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 20); //收银机号
			String seqno = Convert.increaseCharForward(oldseqno, ' ', 8);  //对应FORM中的5个输入框第一个Text 系统检索号
			String authno = Convert.increaseCharForward(oldauthno, ' ', 15); //第二个Text  原交易终端号
			String date = Convert.increaseChar(olddate, ' ', 8); //第三个Text   原交易日期
			
			String input1 = Convert.increaseChar("", ' ', 256); //预输入项256
	        String input2 = Convert.increaseChar("", ' ', 256); //固定输入项256
	        String funcId = Convert.increaseChar("", ' ', 4); //分行特色脚本ID号
	        
	        if(type == PaymentBank.XKQT3) //分期付款需要传入分期
			{
				fq = Convert.increaseCharForward(fq, '0', 2);
			}else
			{
				fq = Convert.increaseCharForward("", ' ', 2);
			}
	        //fq = Convert.increaseCharForward("", ' ', 2);
	        
	        if(type == PaymentBank.XYKCX )
	        {
	        	seqno = Convert.increaseCharForward(oldseqno, '0', 8);  //对应FORM中的5个输入框第一个Text 系统检索号
				date = Convert.increaseChar(olddate, '0', 8); //第三个Text   原交易日期
	        }
	        
	        if(type == PaymentBank.XYKTH)
	        {
	        	seqno = Convert.increaseCharForward(oldseqno, '0', 8);  //对应FORM中的5个输入框第一个Text 系统检索号
				authno = Convert.increaseCharForward(oldauthno, ' ', 15); //第二个Text  原交易终端号
				date = Convert.increaseChar(olddate, '0', 8); //第三个Text   原交易日期
	        }
	        
	        if(type == PaymentBank.XYKCD)//重打上笔则系统检索号为空
			{ 
				
				seqno = Convert.increaseCharForward(oldseqno, '0', 8); //系统检索号
			}
	        
			if(type == PaymentBank.XKQT2) //积分消费需要预输入项和固定式输入项
			{
				input1 = Convert.increaseChar("AMT1="+je, ' ', 256); //预输入项256
				funcId = "3004";
			}
			
			
			
			if(type == PaymentBank.XYKYE || type == PaymentBank.XYKCD || 
			   type == PaymentBank.XKQT1 || type == PaymentBank.XYKQD || 
			   type == PaymentBank.XYKJZ || type == PaymentBank.XKQT2  )
			{
				je = Convert.increaseCharForward("", ' ', 12);
			}
			
			String trans = "";
				
			switch(type)
			{
				case PaymentBank.XYKXF:
					trans = "05"  ;
					break;
				case PaymentBank.XYKCX:  //撤销和退货同一交易指令

				case PaymentBank.XYKTH:
					trans = "04"  ;
					break;	
				case PaymentBank.XYKQD:
					trans = "09"  ;
					break;
				case PaymentBank.XYKJZ:
					trans = "15"  ;
					break;
				case PaymentBank.XYKYE:
					trans = "10"  ;
					break;
				case PaymentBank.XYKCD:  //重打印上笔和重打印指定一笔同一交易指令

				case PaymentBank.XKQT1:
					trans = "13"  ;
					break;
				case PaymentBank.XKQT2: //积分消费
					trans = "89"  ;
					break;
				case PaymentBank.XKQT3: //分期付款交易
					trans = "12"  ;
					break;
			}
			line = trans + "," + je + "," + seqno + "," + date + "," + authno + "," + syjh + "," +syyh + "," + input1 + "," + input2 +"," + fq + "," +funcId;       
			PrintWriter pw = null;
			try
			{
				pw = CommonMethod.writeFile(path + "\\request.txt");
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
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("写入金卡工程数据异常!!!\n" + e.getMessage(), null, false);
			
			return false;
		}
	}
	
	public boolean XYKReadResult(int type)
	{
		
//		strncpy(szResult[0],outputData.TransType,2);  交易指令
//		strncpy(szResult[1],outputData.CardNo,19);   交易卡号
//		strncpy(szResult[2],outputData.Amount,12);   交易金额 
//		strncpy(szResult[3],outputData.TransDate,8); 交易日期 
//		strncpy(szResult[4],outputData.ReferNo,8);   系统检索号
//		strncpy(szResult[5],outputData.AuthNo,6);    授权号
//		strncpy(szResult[6],outputData.RspCode,2);   返回码
//		strncpy(szResult[7],outputData.TerminalId,15);	交易终端号 
//		strncpy(szResult[8],outputData.RspMessage,100);  错误描述信息
//		strncpy(szResult[9],outputData.TerminalTraceNo,6);  终端流水号

		BufferedReader br = null;
		try
		{
			if ( PathFile.fileExist(path + "result.txt") || (br = CommonMethod.readFileGB2312(path + "\\result.txt")) == null)
			{
				errmsg = "读取金卡应答数据失败！！！\n	";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg, null, false);
				
				return false;
			}
			
			String line = null;
			line = br.readLine();
			
			if (line == null || line.length() < 0)
			{
				errmsg = "返回数据错误!";
				return false;
			}				
			String[] str = line.split(",");
			if(str.length != 10 ) //如果返回的字段不为10则有错误
			{
				errmsg = "返回数据错误!";
				return false;
			}
			
			bld.retcode = str[6];
			
			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节数不等时，汉字后面的内容倒着计算位置
			// int len = line.length();
			if (!bld.retcode.equals("00"))
			{
				bld.retmsg = str[8].trim();				
				errmsg = bld.retmsg ;
				bld.retbz = 'N';
				
				return false;
			}
			else
			{
				bld.retmsg = "金卡工程调用成功！！！";
				bld.retbz = 'Y';
			}
			
		//签到，结账，重打不需要获取详细信息
			if (type == PaymentBank.XYKQD || type == PaymentBank.XYKJZ || 
				type == PaymentBank.XYKCD ||type == PaymentBank.XKQT1)
			{
				return true;
			}
			
			
//			bld.bankinfo = str[8].trim();     //发卡行名称
			
			bld.cardno = str[1].trim();  //卡号
			
			bld.trace = Convert.toLong(str[4].trim());   //系统检索号
			
//			String s = str[4].trim();
//			if (s.matches("^\\s*\\d+\\s*$"))
//			{
//				bld.trace = Long.parseLong(s);
//			}			
			
			//金额
			if( type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				type == PaymentBank.XYKTH || type == PaymentBank.XKQT2 || 
				type == PaymentBank.XKQT3)
			{
				double d = Double.parseDouble(str[2]);
				bld.je = ManipulatePrecision.mul(d,0.01);
			}
			
			//授权号
			bld.authno = str[5].trim();
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			XYKSetError("XX", "读取应答数据XX" + e.getMessage());
			new MessageBox("读取金卡工程数据异常" + e.getMessage(), null, false);
			
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
	
	
	public boolean checkDate(Text date)
	{
		String d = date.getText();
		if (d.length() > 8)
		{
			new MessageBox("日期格式错误\n日期格式《YYYYMMDD》");
			return false;
		}
		
		return true;
	}
	
	public boolean checkBankSucceed() {
		if (bld.retbz == 'N') {
			errmsg = bld.retmsg;

			return false;
		} else {
			errmsg = "交易成功";

			return true;
		}
	}
	
	public boolean XYKNeedPrintDoc(int type)
	{
	if(bld.retbz != 'Y')
	{
		return false;
	}
	if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
		  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || 
		  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 ||
		  type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3 )
	{
		return true;
	}
	else
		return false;
	
	}
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name =path + "\\ICBCPRTTKT.txt";
		try
		{
			if (!PathFile.fileExist(name))
			{
				new MessageBox("找不到签购单打印文件！！！");
				
				return ;
			}
			pb = new ProgressBox();
			pb.setText("正在打印签购单文件，请等待。。。" + "\t OY : " + GlobalInfo.sysPara.issetprinter);
			
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
