package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Global.GlobalInfo;

public class LfxcyICBC_PaymentBankFunc extends Bjcs_PaymentBankFunc
{
    private SaleBS saleBS = null;
    
    public String[] getFuncItem()
    {
	String[] func = new String[5];
	
	func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	func[3] = "[" + PaymentBank.XKQT2 + "]" + "其他交易";
	func[4] = "[" + PaymentBank.XKQT3 + "]" + "重打签购单";
	return func;
    }
    
    public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
	// 0-4对应FORM中的5个输入框
	// null表示该不用输入
	switch (type)
	{
	    case PaymentBank.XYKXF: // 消费
		grpLabelStr[0] = null;
		grpLabelStr[1] = null;
		grpLabelStr[2] = null;
		grpLabelStr[3] = null;
		grpLabelStr[4] = "交易金额";
		
		break;
	    
	    case PaymentBank.XYKCX: // 消费撤销
		grpLabelStr[0] = null;
		grpLabelStr[1] = null;
		grpLabelStr[2] = null;
		grpLabelStr[3] = null;
		grpLabelStr[4] = "交易金额";
		
		break;
	    
	    case PaymentBank.XYKTH: // 隔日退货
		grpLabelStr[0] = null;
		grpLabelStr[1] = null;
		grpLabelStr[2] = null;
		grpLabelStr[3] = null;
		grpLabelStr[4] = "交易金额";
		
		break;
	    
	    case PaymentBank.XKQT2: // 其他交易（签到,结算,交易一览,重打)
		grpLabelStr[0] = null;
		grpLabelStr[1] = null;
		grpLabelStr[2] = null;
		grpLabelStr[3] = null;
		grpLabelStr[4] = "其他交易";
		
		break;
	    
	    case PaymentBank.XKQT3: // 其他交易（签到,结算,交易一览,重打)
		grpLabelStr[0] = null;
		grpLabelStr[1] = null;
		grpLabelStr[2] = null;
		grpLabelStr[3] = null;
		grpLabelStr[4] = "重打签购单";
		
		break;
	    
	}
	return true;
    }
    
    public boolean getFuncText(int type, String[] grpTextStr)
    {
	switch (type)
	{
	    case PaymentBank.XYKXF: // 消费
		grpTextStr[0] = null;
		grpTextStr[1] = null;
		grpTextStr[2] = null;
		grpTextStr[3] = null;
		grpTextStr[4] = null;
		break;
	    case PaymentBank.XYKCX: // 消费撤销
		grpTextStr[0] = null;
		grpTextStr[1] = null;
		grpTextStr[2] = null;
		grpTextStr[3] = null;
		grpTextStr[4] = null;
		break;
	    case PaymentBank.XYKTH: // 隔日退货
		grpTextStr[0] = null;
		grpTextStr[1] = null;
		grpTextStr[2] = null;
		grpTextStr[3] = null;
		grpTextStr[4] = null;
		break;
	    case PaymentBank.XKQT2: // 交易签到
		grpTextStr[0] = null;
		grpTextStr[1] = null;
		grpTextStr[2] = null;
		grpTextStr[3] = null;
		grpTextStr[4] = "按回车键后请将操作转向工行POS";
		break;
	    
	    case PaymentBank.XKQT3: // 交易签到
		grpTextStr[0] = null;
		grpTextStr[1] = null;
		grpTextStr[2] = null;
		grpTextStr[3] = null;
		grpTextStr[4] = "按回车键后重打签购单";
		break;
	}
	return true;
    }
    
    public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
    {
	try
	{
	    if (type == PaymentBank.XKQT3)
	    {
		XYKPrintDoc();
		bld.retmsg ="打印完成";
		return true;
	    }
	    
	    if ((type != PaymentBank.XYKXF) 
		    && (type != PaymentBank.XYKCX) 
		    && (type != PaymentBank.XYKTH) 
		    && (type != PaymentBank.XKQT2))
	    {
		errmsg = "银联接口不支持该交易";
		new MessageBox(errmsg);
		
		return false;
	    }
	    
	    // 先删除上次交易数据文件
	    if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
	    {
		PathFile.deletePath("c:\\JavaPOS\\request.txt");
		
		if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
		{
		    errmsg = "交易请求文件request.txt无法删除,请重试";
		    XYKSetError("XX", errmsg);
		    new MessageBox(errmsg);
		    return false;
		}
	    }
	    
	    if (PathFile.fileExist("c:\\JavaPOS\\answer.txt"))
	    {
		PathFile.deletePath("c:\\JavaPOS\\answer.txt");
		
		if (PathFile.fileExist("c:\\JavaPOS\\answer.txt"))
		{
		    errmsg = "交易请求文件answer.txt无法删除,请重试";
		    XYKSetError("XX", errmsg);
		    new MessageBox(errmsg);
		    return false;
		}
	    }
	    
	    if (PathFile.fileExist("c:\\JavaPOS\\toprint.txt"))
	    {
		PathFile.deletePath("c:\\JavaPOS\\toprint.txt");
		
		if (PathFile.fileExist("c:\\JavaPOS\\toprint.txt"))
		{
		    errmsg = "打印文件toprint.txt无法删除,请重试";
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
		if (PathFile.fileExist("c:\\gmc\\gmc.exe"))
		{
		    CommonMethod.waitForExec("c:\\gmc\\gmc.exe");
		}
		else
		{
		    new MessageBox("找不到金卡工程模块 gmc.exe");
		    XYKSetError("XX", "找不到金卡工程模块 gmc.exe");
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
    
    public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
    {
	try
	{   
	    String line = "";
	    
	    String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
	    
	    for (int i = jestr.length(); i < 12; i++)
	    {
		jestr = "0" + jestr;
	    }
	    
	    if (memo.size() >= 2)
		saleBS = (SaleBS) memo.elementAt(2);
	    
	    // 根据不同的类型生成文本结构
	    switch (type)
	    {
		case PaymentBank.XYKXF:
		    if (saleBS != null)
			line = Convert.increaseCharForward(saleBS.saleHead.syjh, 3) + Convert.increaseCharForward(saleBS.saleHead.syyh, 6) + "C" + jestr;
		    else
			line = "0000000000000000000000";
		    break;
		case PaymentBank.XYKCX:
		    if (saleBS != null)
			line = Convert.increaseCharForward(saleBS.saleHead.syjh, 3) + Convert.increaseCharForward(saleBS.saleHead.syyh, 6) + "D" + jestr;
		    else
			line = "0000000000000000000000";
		    break;
		case PaymentBank.XYKTH:
		    if (saleBS != null)
			line = Convert.increaseCharForward(saleBS.saleHead.syjh, 3) + Convert.increaseCharForward(saleBS.saleHead.syyh, 6) + "R" + jestr;
		    else
			line = "0000000000000000000000";
		    break;
		
		case PaymentBank.XKQT1:
		    line = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, 3) + Convert.increaseCharForward(GlobalInfo.posLogin.gh, 6) + "0" + "000000000000000000";
			
		    break;
		
	    }
	    
	    PrintWriter pw = null;
	    
	    try
	    {
		pw = CommonMethod.writeFile("c:\\gmc\\request.txt");
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
    
    private void setRetInfo(int intRet)
    {
	switch (intRet)
	{
	    case -3:
		bld.retcode = "";
		bld.retmsg = "工行POS交易失败";
		break;
	    case -2:
		bld.retcode = "";
		bld.retmsg = "解析返回数据发生异常";
		break;
	    case -1:
		bld.retcode = "";
		bld.retmsg = "返回数据有误";
		break;
	    case 0:
		bld.retcode = "";
		bld.retmsg = "当前交易类型与返回类型不匹配";
		break;
	    case 1:
		bld.retmsg = "金卡交易成功";
		break;
	}
    }
    
    private int checkResultInfo(String line, int type)
    {
	//String curTradeType = "";
	boolean isOK = false;
	try
	{
	    if (line == null || line.length() < 40)
		return -1; // 返回数据长度有误
		
	    bld.retcode = line.substring(0, 2);
	    if (bld.retcode.equals("00") && line.length() == 40)
	    {
		bld.cardno = line.substring(2, 21).trim();
		bld.je = ManipulatePrecision.mul(Double.parseDouble(line.substring(22, 34)), 0.01);
		bld.trace = Long.parseLong(line.substring(line.length() - 6));
		//curTradeType = line.substring(21, 22);
		
		isOK = true;
	    }
	    
	    if (bld.retcode.equalsIgnoreCase("ER"))
	    {
		return -3;
	    }
	    
/*	    if (type == PaymentBank.XYKXF && curTradeType.equalsIgnoreCase("C") 
		    || (type == PaymentBank.XYKCX && curTradeType.equalsIgnoreCase("D")))
		    || (type == PaymentBank.XYKTH && curTradeType.equalsIgnoreCase("R")))*/
	    if (isOK)
		return 1;
	    else
		return 0;
	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	    return -2; // 发生异常
	}
    }
    
    public boolean XYKReadResult()
    {
	BufferedReader br = null;
	String line = "";
	
	try
	{
	    //XKQT2 无返回数据，直接在工行POS上显示结果
	    if (Integer.parseInt(bld.type) == PaymentBank.XKQT2)
	    {
		bld.retcode = "";
		bld.retmsg = "金卡其他交易";
		return true;
	    }
	    
	    if (!PathFile.fileExist("c:\\gmc\\answer.txt") || ((br = CommonMethod.readFileGBK("c:\\gmc\\answer.txt")) == null))
	    {
		XYKSetError("XX", "读取金卡工程应答数据失败!");
		new MessageBox("读取金卡工程应答数据失败!", null, false);
		
		return false;
	    }
	    
	    line = br.readLine();
	    
	    switch (Integer.parseInt(bld.type))
	    {
		case PaymentBank.XYKXF:
		    setRetInfo(checkResultInfo(line, PaymentBank.XYKXF));
		    break;
		case PaymentBank.XYKCX:
		    setRetInfo(checkResultInfo(line, PaymentBank.XYKCX));
		    break;
		case PaymentBank.XYKTH:
		    setRetInfo(checkResultInfo(line, PaymentBank.XYKTH));
		    break;
	    }
	    
	    return true;
	}
	catch (Exception ex)
	{
	    XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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
/*		    
		    if (PathFile.fileExist("c:\\gmc\\request.txt"))
		    {
			PathFile.deletePath("c:\\gmc\\request.txt");
		    }
		    
		    if (PathFile.fileExist("c:\\gmc\\answer.txt"))
		    {
			PathFile.deletePath("c:\\gmc\\answer.txt");
		    }*/
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
	    }
	}
    }
    
    public void XYKPrintDoc()
    {
	ProgressBox pb = null;
	
	try
	{
	    String printName = "";
	    
	    int type = Integer.parseInt(bld.type.trim());
	    
	    if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XKQT3))
	    {
		if (!PathFile.fileExist("c:\\gmc\\toPrint.txt"))
		{
		    new MessageBox("找不到签购单打印文件!");
		    
		    return;
		}
		else
		{
		    printName = "c:\\gmc\\toPrint.txt";
		}
	    }
	    else
	    {
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
		    
		    String line = null;
		    
		    while ((line = br.readLine()) != null)
		    {
			
			if (line.trim().equals("CUTPAPPER"))
			{
			    break;
			}
			
			XYKPrintDoc_Print(line);
		    }
		    
		}
		catch (Exception ex)
		{
		    new MessageBox(ex.getMessage());
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
	    
	    if (PathFile.fileExist("c:\\gmc\\toprint.txt"))
	    {
		PathFile.deletePath("c:\\gmc\\toprint.txt");
	    }
	}
    }
}
