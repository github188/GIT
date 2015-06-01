package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
//北京燕莎 储值卡（交行）
public class BjysCzk_PaymentBankFunc extends PaymentBankFunc
{
	class ResponseData
	{
		public String TransType = " "; //交易类型
		public String Amount = " "; //交易金额
		public String RetCode = " "; //响应码
		public String CardId = " "; //卡号
		public String ExpDate = " "; //卡有效期
		public String AuthorCode = " "; //授权码
		public String PosMid = " ";//商户号
		public String PosTid = " "; //终端号
		public String PosDate = " "; //交易日期
		public String PosTime = " "; //交易时间
		public String PosJnl = " ";//系统参考号
		public String BatchNo = " "; //批次号
		public String VoiceNo = " "; //票据号
		public String OldVoiceNo = " ";//原票据号
		public String BonusInfo = " "; //卡别
	}
	
	ResponseData response = new ResponseData();
	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "联机退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "签到";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[5] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "原票据号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "余额查询";

				break;


			case PaymentBank.XYKTH: //联机退货   
				grpLabelStr[0] = "原系统参考号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "金额";

				break;
			case PaymentBank.XYKCD: //签购单重打
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
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
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

			case PaymentBank.XYKTH: //退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;
				
			case PaymentBank.XYKJZ: //交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";

				break;
			case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键签购单重打";
            break;
		}

		return true;
	}
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if(type == PaymentBank.XYKCD){
				Printer.getDefault().printLine_Journal( "重打单据           \n");
				XYKPrintDoc();
				bld.retbz ='Y';
				bld.retmsg = "金卡工程调用成功";
				return true;
			}
             //  先删除上次交易数据文件
			if (PathFile.fileExist("c:\\dat\\request.txt")) {
				PathFile.deletePath("c:\\dat\\request.txt");

				if (PathFile.fileExist("c:\\dat\\request.txt")) {
					errmsg = "交易请求文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}
			if (PathFile.fileExist("c:\\dat\\Print.txt")) {
				PathFile.deletePath("c:\\dat\\Print.txt");

				if (PathFile.fileExist("c:\\dat\\Print.txt")) {
					errmsg = "交易打印文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}
			
			if (PathFile.fileExist("c:\\dat\\czk_js.txt")) {
				PathFile.deletePath("c:\\dat\\czk_js.txt");

				if (PathFile.fileExist("c:\\dat\\czk_js.txt")) {
					errmsg = "交易结账打印文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}
			if (PathFile.fileExist("c:\\dat\\result.txt")) {
				PathFile.deletePath("c:\\dat\\result.txt");

				if (PathFile.fileExist("c:\\dat\\result.txt")) {
					errmsg = "交易应答文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}
			
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,
					oldauthno, olddate, memo)) {
				return false;
			}
			// 调用接口模块
			if (PathFile.fileExist("c:\\dat\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("c:\\dat\\javaposbank.exe YSCZK" );
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			
			// 读取应答数据
			if (!XYKReadResult()) { return false; }
			
			// 检查交易是否成功
			XYKCheckRetCode();
			
			
			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKTH ||type == PaymentBank.XYKCX)
			{
				if(XYKNeedPrintDoc()){
					XYKPrintDoc();	
				}
			
			}
			if(type == PaymentBank.XYKJZ){
				XYKPrintDocJZ();
			}

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo) {
		
		
		if (PathFile.fileExist("c:\\dat\\request.txt"))
		{
			PathFile.deletePath("c:\\dat\\request.txt");

			if (PathFile.fileExist("c:\\dat\\request.txt"))
			{
				errmsg = "交易请求文件request.txt无法删除,请重试";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				return false;
			}
		}

		String line = "";

		String type1 = "";

		bld.je = money;
		String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
		jestr = Convert.increaseCharForward(jestr, '0', 12);

		//根据不同的类型生成文本结构
		
		String temp = "";
		switch (type)
		{
			case PaymentBank.XYKXF:
				type1 = "30";
			//	response.TransType = "30";
				temp = Convert.increaseChar(track2,' ',38)+ Convert.increaseChar(track2, ' ', 105) + jestr + "      " +"            ";
				break;
			case PaymentBank.XYKCX:
				type1 = "40";
			//	response.TransType = "40";
				temp = Convert.increaseChar(track2,' ',38)+ Convert.increaseChar(track2, ' ', 105) + jestr + Convert.increaseChar(oldseqno, ' ', 6)+"            ";
				break;
			case PaymentBank.XYKTH:
				type1 = "50";
			//	response.TransType = "50";
				temp = Convert.increaseChar(track2,' ',38)+ Convert.increaseChar(track2, ' ', 105) + jestr + "      "+Convert.increaseChar(oldseqno, ' ', 12);
				break;
			case PaymentBank.XYKYE:		// 查询余额
				type1 = "80";
			//	response.TransType = "80";
				temp = Convert.increaseChar(track2,' ',38)+ Convert.increaseChar(track2, ' ', 105) + "000000000000" + "      " +"            ";
				break;
			case PaymentBank.XYKQD:
				type1 = "91";
			//	response.TransType = "91";
				temp = Convert.increaseChar("",' ',38)+ Convert.increaseChar("", ' ', 105) + "000000000000" + "      " +"            ";
				break;
			case PaymentBank.XYKJZ:
				type1 = "92";
			//	response.TransType = "91";
				temp = Convert.increaseChar("",' ',38)+ Convert.increaseChar("", ' ', 105) + "000000000000" + "      " +"            ";
				break;
			default:
				return false;
		}
		ManipulateDateTime mdt = new ManipulateDateTime();
		
		String date = mdt.getDateByEmpty().substring(2);
		String time = mdt.getTimeByEmpty();
		
		line = "02" + type1 + temp + date + time +"010000" + Convert.increaseChar("",' ',40);

		PrintWriter pw = null;

		try
		{
			pw = CommonMethod.writeFile("c:\\dat\\request.txt");

			if (pw != null)
			{
				pw.print(line);
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
	private void XYKPrintDocJZ() {
		ProgressBox pb = null;
		try {
			if (!PathFile.fileExist("c:\\dat\\czk_js.txt")) {
				new MessageBox("找不到签购单打印文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印储值卡签购单,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++) {
				BufferedReader br = null;

				//
				Printer.getDefault().startPrint_Journal();

				try {
					// 由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					br = CommonMethod.readFileGB2312("c:\\dat\\czk_js.txt");

					if (br == null) {
						new MessageBox("打开结账签购单打印文件失败!");

						return;
					}

					//
					String line = null;

					while ((line = br.readLine()) != null) {
						Printer.getDefault().printLine_Journal(line + "\n");
					}
				} catch (Exception e) {
					new MessageBox(e.getMessage());
				} finally {
					if (br != null) {
						br.close();
					}
				}

				// 切纸
				Printer.getDefault().cutPaper_Journal();
			}
		} catch (Exception ex) {
			new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (pb != null) {
				pb.close();
				pb = null;
			}
		}	
	}

	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\dat\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\dat\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line = br.readLine();
			if(line.trim().equals("C1")){
				return true;
			}
			if(line.trim().equals("01")){
				errmsg = "查询发卡方";
				return false;
			}
			if(line.trim().equals("25")){
				errmsg = "记录不存在";
				return false;
			}
			if(line.trim().equals("51")){
				errmsg = "无足够的存款";
				return false;
			}
			if(line.trim().equals("64")){
				errmsg = "无足够的存款";
				return false;
			}
			if(line.trim().equals("78")){
				errmsg = "有效期错";
				return false;
			}
			
			if(line.trim().length()==2){
				new MessageBox("错误代码："+line.trim());

				return false;
			}
			
			//
			bld.type = Convert.newSubString(line, 2, 4).trim();
			response.RetCode = Convert.newSubString(line, 4, 6).trim();
			bld.retcode = response.RetCode;
			bld.retmsg = Convert.newSubString(line, 6, 46).trim();
			response.CardId = Convert.newSubString(line, 69, 88).trim();
			bld.cardno = response.CardId;
			
			response.PosMid = Convert.newSubString(line, 46, 61).trim();
			response.PosTid = Convert.newSubString(line, 61, 69).trim();
			response.BonusInfo = Convert.newSubString(line, 205, 224).trim();
			response.ExpDate = Convert.newSubString(line, 108, 112).trim();
			response.PosDate = Convert.newSubString(line, 112, 118).trim();
			response.PosTime = Convert.newSubString(line, 118, 124).trim(); 
			response.BatchNo= Convert.newSubString(line, 124, 130).trim();
			response.VoiceNo= Convert.newSubString(line, 130, 136).trim();
			response.PosJnl= Convert.newSubString(line, 148, 160).trim();
			response.AuthorCode = Convert.newSubString(line, 160, 166).trim();
			response.Amount = Convert.newSubString(line, 136, 148).trim();
			
			if(response.RetCode.trim().equals("01")){
				errmsg = "查询发卡方";
				return false;
			}
			if(response.RetCode.trim().equals("25")){
				errmsg = "记录不存在";
				return false;
			}
			if(response.RetCode.trim().equals("51")){
				errmsg = "无足够的存款";
				return false;
			}
			if(response.RetCode.trim().equals("64")){
				errmsg = "无足够的存款";
				return false;
			}
			if(response.RetCode.trim().equals("78")){
				errmsg = "有效期错";
				return false;
			}
			if ( !"00".equals(response.RetCode))
			{
				new MessageBox("交易失败！返回代码："+response.RetCode);
				
				return false;
			}
			if( bld.type.equals("30") || bld.type.equals("40") ||bld.type.equals("50")){
				String je = Convert.newSubString(line, 136, 148).trim();
				double j = Double.parseDouble(je);
				j = ManipulatePrecision.mul(j, 0.01);
				bld.je = j;
			}
			
			
			errmsg = bld.retmsg;
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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
					new MessageBox("result.txt 关闭失败\n重试后如果仍然失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean XYKNeedPrintDoc()
	{
			if (!response.RetCode.equals("00")) return false;

			StringBuffer sb = new StringBuffer();
			PrintWriter pw = null;
			String saleJe = "";
			try
			{
				//saleJe = String.valueOf(Double.parseDouble(response.Amount) / 100);
				double j = Double.parseDouble(response.Amount);
				j = ManipulatePrecision.mul(j, 0.01);
				saleJe = String.valueOf(j);
				if(saleJe.indexOf(".")!=-1){
					if((saleJe.length()-saleJe.indexOf("."))!=3){
						saleJe = saleJe +"0";
					}
				}else{
					saleJe = saleJe +".00";
				}
				
				sb.append("           预付卡              \r\n");
				sb.append("---------------------------------\r\n");
				sb.append("商户号(MERCHANT ID)："+response.PosMid +"\r\n");
				sb.append("商户名称(MERCHANT NAME)：  \r\n");
				sb.append("燕莎奥特莱斯购物中心 \r\n");
				sb.append("终端号(TERMINAL ID)："+response.PosTid +"\r\n");
				sb.append("---------------------------------\r\n");
				sb.append("卡号(CARD NO.)："+response.CardId  +"\r\n");
				sb.append("卡类别(CARD TYPE)："+ response.BonusInfo+"\r\n");
				sb.append("卡有效期(EXP. DATE)："+response.ExpDate   +"\r\n");
				sb.append("发卡行(ISSUER)： "+ response.BonusInfo+" \r\n");
				sb.append("系统参考号(REF. NO.)："+response.PosJnl   +"\r\n");
				sb.append("批次号(BATCH. NO.)：" +response.BatchNo  +"\r\n");
				sb.append("流水号(TRACE. NO. )：" +response.VoiceNo   +"\r\n");
				//sb.append("VOUCHER. NO.                     \r\n");
				//sb.append("凭证号：" +response.BatchNo  +"\r\n");
				sb.append("授权号(AUTH. NO.)：" +response.AuthorCode   +"\r\n");
				if (bld.type.trim().equals("30")){
					sb.append("交易类型(TRANS.TYPE. )：" +" 消费"  +"\r\n");
				}
				if (bld.type.trim().equals("50")){
					sb.append("交易类型(TRANS.TYPE. )：" +" 退货"  +"\r\n");
				}
				if (bld.type.trim().equals("40")){
					sb.append("交易类型(TRANS.TYPE. )：" +" 撤销"  +"\r\n");
				}
				sb.append("日期时间(DATE/TIME)：" +response.PosDate + " "+response.PosTime  +"\r\n");
				sb.append("交易金额(AMOUNT): "+saleJe+"\r\n");
				sb.append("---------------------------------\r\n");
				sb.append("持卡人签名(CARDHOLDER SIGNATURE)\r\n");
				sb.append(" \r\n");
				sb.append(" \r\n");
				sb.append(" \r\n");
				sb.append("---------------------------------\r\n");
				sb.append("本人已接受此单据金额的有关商品服务并愿意遵守与有关银行签订的持卡人合约一切规定\r\n");
				sb.append("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES\r\n");
				
				sb.toString();
				if (PathFile.fileExist("c:\\dat\\print.txt"))
					PathFile.deletePath("c:\\dat\\print.txt");

				pw = CommonMethod.writeFileAppendGBK("c:\\dat\\print.txt");

				if (pw != null)
				{
					pw.println(sb);
					pw.flush();
				}
				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			finally
			{
				if (pw != null)
					pw.close();
			}
	}
	
	public boolean XYKCheckRetCode() {
		if (bld.retcode.trim().equals("00")) {
			bld.retbz = 'Y';
			bld.retmsg = "金卡工程调用成功";

			return true;
		} else {
			bld.retbz = 'N';

			return false;
		}
	}
	public void XYKPrintDoc() {
		ProgressBox pb = null;
		try {
			if (!PathFile.fileExist("c:\\dat\\print.txt")) {
				new MessageBox("找不到签购单打印文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印储值卡签购单,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++) {
				BufferedReader br = null;

				//
				Printer.getDefault().startPrint_Journal();

				try {
					// 由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					br = CommonMethod.readFileGB2312("c:\\dat\\print.txt");

					if (br == null) {
						new MessageBox("打开签购单打印文件失败!");

						return;
					}

					//
					String line = null;

					while ((line = br.readLine()) != null) {
						Printer.getDefault().printLine_Journal(line + "\n");
					}
				} catch (Exception e) {
					new MessageBox(e.getMessage());
				} finally {
					if (br != null) {
						br.close();
					}
				}

				// 切纸
				Printer.getDefault().cutPaper_Journal();
			}
		} catch (Exception ex) {
			new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (pb != null) {
				pb.close();
				pb = null;
			}
		}
	}
}
