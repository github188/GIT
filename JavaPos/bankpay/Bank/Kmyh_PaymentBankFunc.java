package bankpay.Bank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;

//昆明银行银联接口
//调用动态库（模块名：KMYH；动态库(dll文件）：ChaseApi.dll ；函数：int Abmcs(void *strIn, void *strOut)）

public class Kmyh_PaymentBankFunc  extends PaymentBankFunc{
	
	String path = "";
	
	//设置功能并返回
	public String[] getFuncItem(){
		
		String[] func=new String[8];
		
		func[0]="["+PaymentBank.XYKXF+"]"+"消费";
		func[1]="["+PaymentBank.XYKCX+"]"+"消费撤销";
		func[2]="["+PaymentBank.XYKTH+"]"+"隔日退货";
		func[3]="["+PaymentBank.XYKQD+"]"+"交易签到";
		func[4]="["+PaymentBank.XYKJZ+"]"+"交易结账";
		func[5]="["+PaymentBank.XYKYE+"]"+"余额查询";
		func[6]="["+PaymentBank.XYKCD+"]"+"重打任意笔签购单";
		func[7]="["+PaymentBank.XKQT1+"]"+"重打上笔签购单";
			
		return func;
	}
	
	public boolean getFuncLabel(int type,String[] grpLabelStr){
		
		switch(type){
		
		     case PaymentBank.XYKXF://消费
		    	 grpLabelStr[0]=null;
			     grpLabelStr[1]=null;
			     grpLabelStr[2]=null;
			     grpLabelStr[3]=null;
			     grpLabelStr[4]="交易金额";
			     break;
			         
		     case PaymentBank.XYKCX://消费撤销
		         grpLabelStr[0]="原参考号";
		         grpLabelStr[1]=null;
		         grpLabelStr[2]=null;
		         grpLabelStr[3]=null;
		         grpLabelStr[4]="交易金额";  
		         break;
		         
		     case PaymentBank.XYKTH://隔日退货
		         grpLabelStr[0]="原参考号";//
		         grpLabelStr[1]=null;
		         grpLabelStr[2]=null;
		         grpLabelStr[3]=null;
		         grpLabelStr[4]="交易金额";  
		         break;
		         
		     case PaymentBank.XYKQD://交易签到
		         grpLabelStr[0]=null;
		         grpLabelStr[1]=null;
		         grpLabelStr[2]=null;
		         grpLabelStr[3]=null;
		         grpLabelStr[4]="交易签到";  
		         break;
		         
		     case PaymentBank.XYKJZ://交易结账
		         grpLabelStr[0]=null;
		         grpLabelStr[1]=null;
		         grpLabelStr[2]=null;
		         grpLabelStr[3]=null;
		         grpLabelStr[4]="交易结账";  
		         break;
		         
		     case PaymentBank.XYKYE://余额查询
		         grpLabelStr[0]=null;
		         grpLabelStr[1]=null;
		         grpLabelStr[2]=null;
		         grpLabelStr[3]=null;
		         grpLabelStr[4]="余额查询";  
		         break;
		         
		     case PaymentBank.XYKCD://重打任意笔签单
		         grpLabelStr[0]="原参考号";
		         grpLabelStr[1]=null;
		         grpLabelStr[2]=null;
		         grpLabelStr[3]=null;
		         grpLabelStr[4]="重打任意笔签单";  
		         break;	  
		     case PaymentBank.XKQT1://重打上笔签单
		         grpLabelStr[0]=null;
		         grpLabelStr[1]=null;
		         grpLabelStr[2]=null;
		         grpLabelStr[3]=null;
		         grpLabelStr[4]="重打上笔签单";  
		         break;	
		}
		
		return true;	
	}
	
	public boolean getFuncText(int type, String[] grpTextStr){
		
		switch(type){
		
	     case PaymentBank.XYKXF://消费
	    	 grpTextStr[0]=null;
		     grpTextStr[1]=null;
		     grpTextStr[2]=null;
		     grpTextStr[3]=null;
		     grpTextStr[4]=null;
		     break;
		         
	     case PaymentBank.XYKCX://消费撤销
	         grpTextStr[0]=null;
	         grpTextStr[1]=null;
	         grpTextStr[2]=null;
	         grpTextStr[3]=null;
	         grpTextStr[4]=null;  
	         break;
	         
	     case PaymentBank.XYKTH://隔日退货
	         grpTextStr[0]=null;
	         grpTextStr[1]=null;
	         grpTextStr[2]=null;
	         grpTextStr[3]=null;
	         grpTextStr[4]=null;  
	         break;
	         
	     case PaymentBank.XYKQD://交易签到
	         grpTextStr[0]=null;
	         grpTextStr[1]=null;
	         grpTextStr[2]=null;
	         grpTextStr[3]=null;
	         grpTextStr[4]="请按回车键开始交易签到";  
	         break;
	         
	     case PaymentBank.XYKJZ://交易结账
	         grpTextStr[0]=null;
	         grpTextStr[1]=null;
	         grpTextStr[2]=null;
	         grpTextStr[3]=null;
	         grpTextStr[4]="请按回车键开始交易结账";  
	         break;
	         
	     case PaymentBank.XYKYE://余额查询
	         grpTextStr[0]=null;
	         grpTextStr[1]=null;
	         grpTextStr[2]=null;
	         grpTextStr[3]=null;
	         grpTextStr[4]="请按回车键开始余额查询";  
	         break;
	         
	     case PaymentBank.XYKCD://重打任意笔签单
	         grpTextStr[0]=null;
	         grpTextStr[1]=null;
	         grpTextStr[2]=null;
	         grpTextStr[3]=null;
	         grpTextStr[4]="请按回车键开始重打任意笔签单";  
	         break;	
	     case PaymentBank.XKQT1://重打上笔签单
	         grpTextStr[0]=null;
	         grpTextStr[1]=null;
	         grpTextStr[2]=null;
	         grpTextStr[3]=null;
	         grpTextStr[4]="请按回车键开始重打上笔签单";  
	         break;	
		}
		
		return true;
	}
	
	/*
	 * 
	 * 参数说明：int type:交易类型
	 *        double money:交易金额
	 *        String track1:银行卡的磁道1信息
	 *        String track2:银卡卡的磁道2信息
	 *        String track3:银行卡的磁道3信息
	 *        String oldseqno:界面中第1个Text中传入的值
	 *        String:oldauthno:界面中第2个Text中传入的值
	 *        String olddate:界面中第3个Text中传入的值
	 *        Vector memo:一个备用的参数
	 * 
	 * 
	 */
	public boolean XYKExecute(int type,double money,String track1,String track2,String track3,
			                    String oldseqno,String oldauthno,String olddate,Vector memo){
		
		try{
			
			if(!(type==PaymentBank.XYKXF||type==PaymentBank.XYKCX||type==PaymentBank.XYKTH||
				 type==PaymentBank.XYKQD||type==PaymentBank.XYKJZ||type==PaymentBank.XYKYE||
				 type==PaymentBank.XYKCD||type == PaymentBank.XKQT1)){
				
				new MessageBox("银联接口不支持的交易类型！");
				return false;
			}
			
			path =ConfigClass.BankPath;
			
			//删除上次交易的请求文件request.txt
			if(PathFile.fileExist(path+"\\request.txt")){
				
				PathFile.deletePath(path+"\\request.txt");
				
				if(PathFile.fileExist(path+"request.txt")){
					
					//M:设置错误信息，第一个参数为返回码:retcode ，第二个参数为返回信息和错误消息:retmsg,errmsg
					errmsg = "交易请求文件request.txt无法删除，请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}
			//删除上次交易的应答文件result.txt
			if(PathFile.fileExist(path+"\\result.txt")){
				
				PathFile.deletePath(path+"\\result.txt");
				
				if(PathFile.fileExist(path+"\\result.txt")){
					
					errmsg = "交易应答文件result.txt无法删除，请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}
			
			//删除上次交易的打印文件
			if (PathFile.fileExist(path + "\\print.txt"))
            {
                PathFile.deletePath(path + "\\print.txt");
            }
			if (PathFile.fileExist(path + "\\Settle.txt"))
            {
                PathFile.deletePath(path + "\\Settle.txt");
            }
			
			//M:要写入请求文件request.txt中的串
			String line=XYKgetRequest(type,money,track1,track2,track3,oldseqno,oldauthno,olddate,memo);
			
			//M:将line写入到request.txt中
			PrintWriter pw=CommonMethod.writeFile(path+"\\request.txt");
			
			if(pw!=null){
				pw.print(line);
				pw.flush();
				pw.close();
			}
			
			//调用接口模块 
			if(PathFile.fileExist(path+"\\javaposbank.exe")){
				
				CommonMethod.waitForExec(path+"\\javaposbank.exe KMYH");
				
			}else{
				
				XYKSetError("XX","找不到金卡工程模块javaposbank.exe");
				new MessageBox(errmsg);
				return false;
			}
			
			//读取应答文件result.txt 
			if(!XYKreadResult(type)){
				return false;
			}
			
			//检查交易是否成功
			if(!XYKCheckRetCode()){
				return false;
			}
			
			if(XYKNeedPrintDoc(type)){
				
				XYKPrintDoc();
				
			}
			
			return true;
			
			
		}catch(Exception e){
			
			e.printStackTrace();
			XYKSetError("XX","金卡异常XX:"+e.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + e.getMessage(), null, false);
			return false;
		}
		
	}
	
	public String XYKgetRequest(int type,double money,String track1,String track2,String track3,
			                    String oldseqno,String oldauthno,String olddate,Vector memo){
		String line="";
		String mtype1="";
		
		switch(type){
		
		   case PaymentBank.XYKXF:  //消费
			    mtype1="0";
			    break;
		   case PaymentBank.XYKCX:  //消费撤销
			    mtype1="5";
			    break;
		   case PaymentBank.XYKTH:  //隔日退货
			    mtype1="4";
			    break;
		   case PaymentBank.XYKYE:  //余额查询
			    mtype1="7";
			    break;
		   case PaymentBank.XYKCD:  //重打任意笔签购单
			    mtype1="8";
			    break;
		   case PaymentBank.XYKQD:  //交易签到
			    mtype1="L";
			    break;
		   case PaymentBank.XYKJZ:  //交易结账
			    mtype1="9";
			    break;
		   case PaymentBank.XKQT1:  //重打印上笔
			    mtype1="D";
			    break;
			
		}
		
			
		//M：交易金额	N	12	信用卡消费金额，char(12)，没有小数点"."，精确到分，最后两位为小数位，不足左补0
		String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
		jestr = Convert.increaseCharForward(jestr, '0', 12);
		
		//M:POS员工号	ANS	8	不足右补空格  ---收银员号：syyh
		String syyh=Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 16);	
		
		//M:小票号
		String xph =Convert.increaseChar(String.valueOf(GlobalInfo.syjStatus.fphm),' ',20);
		
		//M:原参考号
		String seqno = Convert.increaseChar(oldseqno, ' ', 12) ;
		
		//M:POS机号	ANS	8	不足右补空格   ---收银机号：syjh
		//M:Convert.increaseChar(String str, char c, int num):接收一个给定的字符串，指定此字符串位数，如果不足就用char c右补位
		String  syjh=Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 16);
								
		//交易类型+交易金额+收银员号+小票号+原交易系统参考号+款台号
		line = mtype1 + "|" + jestr + "|" + syyh + "|" + xph + "|" + seqno + "|" + syjh + "|\0"  ;
		
		
		return line;
	}

	//M:从result.txt读取银行返回的信息，并且转化成程序所需的信息
	public boolean XYKreadResult(int type){
		
		BufferedReader br=null;
		
		try{
			
			if(!PathFile.fileExist(ConfigClass.BankPath+"\\result.txt")||
			   (br=CommonMethod.readFileGBK(ConfigClass.BankPath+"\\result.txt"))==null){
				
				XYKSetError("XX","读取金卡工程应答文件result.txt失败!");
				new MessageBox(errmsg,null,false);
				return false;
			}
			
			String line = br.readLine();

            if (line == null || line.length() <= 0)
            {
                return false;
            }
            
            String result[] = line.split(",");
            if (result == null) return false;
            
            bld.memo		= result[0];
			
            if (result.length >= 2)
            {
    			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
    			// int len = line.length();
    			//防止字节和字符不一致问题
    			//bld.memo = Convert.newSubString(line, 2, 21);
            	
            	bld.retcode  = result[1].split("\\|")[0];  //返回码
            	
            	if(!bld.retcode.equals("00"))
            	{
            		bld.retmsg = bld.retcode + "," +result[1].split("\\|")[8];   //错误说明
            		return false;
            	}
            	
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
            	{
                	bld.cardno = result[1].split("\\|")[2];   //卡号
                	bld.authno = result[1].split("\\|")[4];   //原参考号
                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble((result[1].split("\\|")[3]) ),100),2,1);   //交易金额		
            	}  	
            	      	
    			
            }
			
			return true;
			
		}catch(Exception e){
			
			XYKSetError("XX","读取应答XX:"+e.getMessage());
            new MessageBox("读取金卡工程应答数据异常!" + e.getMessage(), null, false);
            e.printStackTrace();
            return false;
			
		}finally{
			
			if(br!=null){
				try {
					br.close();
					if(PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
					{
						PathFile.deletePath(ConfigClass.BankPath+"\\request.txt");
					}
					if(PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
					{
						PathFile.deletePath(ConfigClass.BankPath+"\\result.txt");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
		
	}
	
	//检查交易是否成功
	public boolean XYKCheckRetCode(){
		
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
	
	//检查是否需要打印
	public boolean XYKNeedPrintDoc(int type){
		
		if(!checkBankSucceed()){
			
			return false;
		}
		
		if( (type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX)||
			(type == PaymentBank.XYKTH) || (type == PaymentBank.XYKJZ)||
			(type == PaymentBank.XYKCD) || (type == PaymentBank.XKQT1) )
		{	
			
			return true;
			
		}else{
			
			return false;
		}
	}
	
	public boolean checkBankSucceed(){
		
		if(bld.retbz=='N'){
			
			errmsg = bld.retmsg;
			
			return false;
			
		}else{
			
			errmsg = "交易成功";
			
			return true;
		}
	}
	
	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = null;
		if(type == PaymentBank.XYKJZ)
		{
			name =path + "\\Settle.txt";
		}
		else{
			name =path + "\\print.txt";
		}

		try
		{
			if (!PathFile.fileExist(name))
			{
				if(type == PaymentBank.XYKJZ)
				{
					new MessageBox("找不到结算单打印文件！！！");
				}else
				{
					new MessageBox("找不到签购单打印文件！！！");
				}
				
				return ;
			}
			pb = new ProgressBox();
			pb.setText("正在打印,请等待...");
		
			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
			{
				BufferedReader br = null;
				XYKPrintDoc_Start();
				try
				{
					br = CommonMethod.readFileGB2312(name);
					if (br == null)
					{							
						if(type == PaymentBank.XYKJZ)
						{
							new MessageBox("打开结算单文件失败");
						}else
						{
							new MessageBox("打开签购单文件失败");
						}
						
						return ;
					}
					
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
							continue;
						//银行签购单模板添加 "CUTPAPER" 标记
						//当程序里面读取到这个字符是，打印机切纸
						if (line.indexOf("CUTPAPER") >= 0)
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
				if(type == PaymentBank.XYKJZ)
				{
					XYKPrintDoc_End();
				}
//				XYKPrintDoc_End();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(type == PaymentBank.XYKJZ)
			{
				new MessageBox("打印结算单异常!!!\n" + e.getMessage());
			}else
			{
				new MessageBox("打印签购单异常!!!\n" + e.getMessage());
			}			
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