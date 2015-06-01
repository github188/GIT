package bankpay.Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
//北京燕莎交行银行卡
public class Bjyskl_PaymentBankFunc extends PaymentBankFunc {

	class ResponseData {
		public String InterFaceType = "0";// 接口类型

		public String TransType = "0"; // 交易类型

		public String CreditPin = "0"; // 持卡人密码

		public String OperNo = "0"; // 操作员工号

		public String Amount = "0"; // 交易金额

		public String Track1 = "0"; // 磁道一

		public String Track2 = "0"; // 磁道二

		public String Track3 = "0"; // 磁道三

		public String OldVoiceNo = "0"; // 撤消票据号

		public String RetCode = "0"; // 响应码

		public String CardId = "0"; // 卡号

		public String ExpDate = "0"; // 卡有效期

		public String PalPid = "0"; // 身份证号

		public String AuthorCode = "0"; // 授权码

		public String PosMid = "0";// 商户号

		public String PosTid = "0"; // 终端号

		public String PosDate = "0"; // 交易日期

		public String PosTime = "0"; // 交易时间

		public String PosJnl = "0";// POS交易号

		public String BatchNo = "0"; // 批次号

		public String VoiceNo = "0"; // 票据号

		public String Balance = "0"; // 余额

		public String BonusInfo = "0"; // 卡别(中奖标志),当未中奖时返回卡别信息,当中奖时返回中奖信息

		public String tmp1 = "0"; // 附加信息1

		public String tmp2 = "0"; // 附加信息2

		public String flag1 = "0"; // 标志信息1 卡类别 “00”银行卡 “11”开联卡

		public String flag2 = "0"; // 标志信息2

		public String lrc = "0"; // 验证码
		
		public String oldDate = "";
		
		public String oldMoney = "";
	}

	ResponseData response = new ResponseData();

	public String[] getFuncItem() {
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKTH + "]" + "脱机退货";
		func[2] = "[" + PaymentBank.XYKCX + "]" + "撤销";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr) {
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type) {
		case PaymentBank.XYKXF:// 消费
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = "请 刷 卡";
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKTH:// 脱机退货
			grpLabelStr[0] = "原参考号";
			grpLabelStr[1] = "原交易日期";
			grpLabelStr[2] = "原交易金额";
			grpLabelStr[3] = "请 刷 卡";
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKCX:// 隔日退货
			grpLabelStr[0] = "原参考号";
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = "请 刷 卡";
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKQD:// 交易签到
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "交易签到";
			break;
		case PaymentBank.XYKJZ:// 交易结账
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "交易结账";
			break;
		case PaymentBank.XYKYE:// 余额查询
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

		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr) {
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
		switch (type) {
		case PaymentBank.XYKXF:// 消费
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		case PaymentBank.XYKTH:// 脱机退货
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		case PaymentBank.XYKCX:// 隔日退货
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "";
			break;
		case PaymentBank.XYKQD:// 交易签到
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始交易签到";
			break;
		case PaymentBank.XYKJZ:// 交易结账
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始交易结账";
			break;
		case PaymentBank.XYKYE:// 余额查询
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
            grpTextStr[4] = "按回车键签购单重打";
        break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo) {
		try { 
			if(type == PaymentBank.XYKCD){
				Printer.getDefault().printLine_Journal( "重打单据           \n");
				XYKPrintDoc();
				bld.retbz ='Y';
				bld.retmsg = "金卡工程调用成功";
				return true;
			}
			if (type != PaymentBank.XYKXF && type != PaymentBank.XYKTH
					&& type != PaymentBank.XYKCX && type != PaymentBank.XYKQD
					&& type != PaymentBank.XYKJZ && type != PaymentBank.XYKYE) {
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);
				return false;
			}

			// 先删除上次交易数据文件
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
					errmsg = "交易应答文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}
			if (PathFile.fileExist("c:\\dat\\pos_js.txt") && PathFile.fileExist("c:\\dat\\pos_wjs.txt")) {
				PathFile.deletePath("c:\\dat\\pos_js.txt");
				PathFile.deletePath("c:\\dat\\pos_wjs.txt");
				if (PathFile.fileExist("c:\\dat\\pos_js.txt")&& PathFile.fileExist("c:\\dat\\pos_wjs.txt")) {
					errmsg = "签购单文件无法删除,请重试";
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

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,
					oldauthno, olddate, memo)) {
				return false;
			}
			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKTH || type == PaymentBank.XYKCX || type == PaymentBank.XYKYE){
				new MessageBox("请使用密码键盘");
			}
			
			// 调用接口模块
			// 调用接口模块
			if (PathFile.fileExist("c:\\dat\\javaposbank.exe")) {
				CommonMethod.waitForExec("c:\\dat\\javaposbank.exe YSKL");
			} else {
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult()) {
				return false;
			}
			
			// 检查交易是否成功
			XYKCheckRetCode();
			
			
			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKTH
					|| type == PaymentBank.XYKCX) {
				if (XYKNeedPrintDoc()) {
					XYKPrintDoc();
				} else
					new MessageBox("生成签购单文件失败!");
			}
			if(type == PaymentBank.XYKJZ){
				XYKPrintDocJZ();
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	private void XYKPrintDocJZ() {

		ProgressBox pb = null;
		try {
			if (!PathFile.fileExist("c:\\dat\\pos_js.txt")||!PathFile.fileExist("c:\\dat\\pos_wjs.txt")) {
				new MessageBox("找不到签购单打印文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++) {
				BufferedReader br = null;

				//
				Printer.getDefault().startPrint_Journal();

				try {
					// 由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					
						br = CommonMethod.readFileGB2312("c:\\dat\\pos_js.txt");

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
				
				for (int n = 0; n < GlobalInfo.sysPara.bankprint; n++) {
					BufferedReader br1 = null;

					//
					Printer.getDefault().startPrint_Journal();

					try {
						// 由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
						
							br1 = CommonMethod.readFileGB2312("c:\\dat\\pos_wjs.txt");

						if (br1 == null) {
							new MessageBox("打开签购单打印文件失败!");

							return;
						}

						//
						String line = null;

						while ((line = br1.readLine()) != null) {
							Printer.getDefault().printLine_Journal(line + "\n");
						}
					} catch (Exception e) {
						new MessageBox(e.getMessage());
					} finally {
						if (br1 != null) {
							br1.close();
						}
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

	public boolean XYKWriteRequest(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo) {
		StringBuffer sbstr = null;
		response.InterFaceType = "1";
		response.flag1 = "00";
		try {
			sbstr = new StringBuffer();

			// 组织请求数据
			// 操作类型,交易类型,卡类型,收银机编号,操作员,金额,收银流水号,原交易流水号,预留字段

			if (type == PaymentBank.XYKXF)
				response.TransType = "1"; // 消费
			else if (type == PaymentBank.XYKTH){
				response.TransType = "6"; // 脱机退货
				response.oldDate = oldauthno;
				response.oldMoney = olddate;
			}
				
			else if (type == PaymentBank.XYKCX)
				response.TransType = "2"; // 隔日退货
			else if (type == PaymentBank.XYKQD)
				response.TransType = "3"; // 交易签到
			else if (type == PaymentBank.XYKJZ)
				response.TransType = "4"; // 交易结账
			else if (type == PaymentBank.XYKYE)
				response.TransType = "5"; // 余额查询
			else {
				throw new Exception("无效的交易类型!");
			}

			String jestr = String.valueOf((long) ManipulatePrecision
					.doubleConvert(money * 100, 2, 1));
			jestr = Convert.increaseCharForward(jestr, '0', 11);
			response.Amount = jestr;

			response.OperNo = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ',
					8);

			if (track1 != null) {
				response.Track1 = Convert.increaseChar(track2, 119);
			} else {
				response.Track1 = Convert.increaseChar("", 119);
			}
			if (track2 != null) {
				response.Track2 = Convert.increaseChar(track2, 119);
			} else {
				response.Track2 = Convert.increaseChar("", 119);
			}

			if (track3 != null) {
				response.Track3 = Convert.increaseChar(track3, 119);
			} else {
				response.Track3 = Convert.increaseChar("", 119);
			}

			if (type == PaymentBank.XYKCX || type == PaymentBank.XYKTH) {
				response.OldVoiceNo = oldseqno;
			}else{
				response.OldVoiceNo = Convert.increaseChar("",'0', 7);
			}
			
		/*	response.RetCode = Convert.increaseChar("",'0', 3); // 响应码

			response.CardId = Convert.increaseChar("",'0', 20); // 卡号

			response.ExpDate = Convert.increaseChar("",'0', 6); // 卡有效期

			response.PalPid = Convert.increaseChar("",'0', 20); // 身份证号

			response.AuthorCode = Convert.increaseChar("",'0', 8); // 授权码

			response.PosMid = Convert.increaseChar("",'0', 16);// 商户号

			response.PosTid = Convert.increaseChar("",'0', 10); // 终端号

			response.PosDate = Convert.increaseChar("",'0', 10); // 交易日期

			response.PosTime = Convert.increaseChar("",'0', 10); // 交易时间

			response.PosJnl = Convert.increaseChar("",'0', 8);// POS交易号

			response.BatchNo = Convert.increaseChar("",'0', 8); // 批次号

			response.VoiceNo = Convert.increaseChar("",'0', 8); // 票据号

			response.Balance = Convert.increaseChar("",'0', 12); // 余额

			response.BonusInfo = Convert.increaseChar("",'0', 12); // 卡别(中奖标志),当未中奖时返回卡别信息,当中奖时返回中奖信息

			response.tmp1 = Convert.increaseChar("",'0', 30); // 附加信息1

			response.tmp2 = Convert.increaseChar("",'0', 30); // 附加信息2

	//		response.flag1 = Convert.increaseChar("",'0', 3); // 标志信息1 卡类别 “00”银行卡 “11”开联卡

			response.flag2 = Convert.increaseChar("",'0', 30); // 标志信息2

			response.lrc = Convert.increaseChar("",'0', 4); // 验证码
*/
		   sbstr.append(response.InterFaceType + "," + response.TransType
					+ "," + response.CreditPin + "," + response.OperNo + ","
					+ response.Amount + "," + response.Track1 + ","
					+ response.Track2 + "," + response.Track3 + ","
					+ response.OldVoiceNo + "," + response.RetCode + ","
					+ response.CardId + "," + response.ExpDate + ","
					+ response.PalPid + "," + response.AuthorCode + ","
					+ response.PosMid + "," + response.PosTid + ","
					+ response.PosDate + "," + response.PosTime + ","
					+ response.PosJnl + "," + response.BatchNo + ","
					+ response.VoiceNo + "," + response.Balance + ","
					+ response.BonusInfo + "," + response.tmp1 + ","
					+ response.tmp2 + "," + response.flag1 + ","
					+ response.flag2 + "," + response.lrc);

			// 写入请求数据
			if (!rtf.writeFile("c:\\dat\\request.txt", sbstr.toString())) {
				new MessageBox("写入金卡工程请求数据失败!", null, false);

				return false;
			}

			return true;
		} catch (Exception ex) {
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		} finally {
			if (sbstr != null) {
				sbstr.delete(0, sbstr.length());
				sbstr = null;
			}

		}
	}

	public boolean XYKReadResult() {
		try {
			if (!PathFile.fileExist("c:\\dat\\result.txt")
					|| !rtf.loadFileByGBK("c:\\dat\\result.txt")) {
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line = rtf.nextRecord();
			rtf.close();
			String[] result;
			if(line.trim().equals("911")){
				new MessageBox("解析卡号错误");
				return false;
			}
			if(line.trim().equals("912")){
				new MessageBox("密码错误");
				return false;
			}
			
			if(line.trim().equals("-20")){
				bld.retcode = line.trim();
				return true;
			}
			
			if(line.trim().equals("-10")){
				new MessageBox("流水号错误");
				return false;
			}
			
			if(line.trim().equals("01")){
				errmsg = "查询发卡方";
				return false;
			}
			if(line.trim().equals("25")){
				errmsg = "记录不存在";
				return false;
			}
			if(line.trim().equals("33")){
				errmsg = "到期卡";
				return false;
			}
			if(line.trim().equals("51")){
				errmsg = "余额不足";
				return false;
			}
			if(line.trim().equals("55")){
				errmsg = "正常卡密码错";
				return false;
			}
			if(line.trim().equals("61")){
				errmsg = "超限额拒绝";
				return false;
			}
			if(line.trim().equals("75")){
				errmsg = "超过密码次数";
				return false;
			}
			if(line.trim().equals("87")){
				errmsg = "MAC不正确";
				return false;
			}
			if(line.trim().equals("91")){
				errmsg = "路由错";
				return false;
			}
			if(line.trim().equals("96")){
				errmsg = "系统故障";
				return false;
			}
			if(line.trim().equals("FF")){
				errmsg = "通讯故障,请重试";
				return false;
			}
			
			if (line.indexOf(',') > -1) {
				result = line.split(",");

				if (result.length != 25) {
					new MessageBox("金卡工程应答数据格式错误", null, false);

					return false;
				} else {
				//	response.InterFaceType = result[0].trim();
				//	response.TransType = result[1].trim();
				//	response.CreditPin = result[2].trim();
					response.OperNo = result[0].trim();
					response.Amount = result[1].trim();
					response.Track1 = result[2].trim();
					response.Track2 = result[3].trim();
					response.Track3 = result[4].trim();
					response.OldVoiceNo = result[5].trim();
					response.RetCode = result[6].trim();
					response.CardId = result[7].trim();
					response.ExpDate = result[8].trim();
					response.PalPid = result[9].trim();
					response.AuthorCode = result[10].trim();
					response.PosMid = result[11].trim();
					response.PosTid = result[12].trim();
					response.PosDate = result[13].trim();
					response.PosTime = result[14].trim();
					response.PosJnl = result[15].trim();
					response.BatchNo = result[16].trim();
					response.VoiceNo = result[17].trim();
					response.Balance = result[18].trim();
					response.BonusInfo = result[19].trim();
					response.tmp1 = result[20].trim();
					response.tmp2 = result[21].trim();
					response.flag1 = result[22].trim();
					response.flag2 = result[23].trim();
					response.lrc = result[24].trim();
				}
				
				if(response.RetCode.trim().equals("01")){
					errmsg = "查询发卡方";
					return false;
				}
				if(response.RetCode.trim().equals("25")){
					errmsg = "记录不存在";
					return false;
				}
				if(response.RetCode.trim().equals("33")){
					errmsg = "到期卡";
					return false;
				}
				if(response.RetCode.trim().equals("51")){
					errmsg = "余额不足";
					return false;
				}
				if(response.RetCode.trim().equals("55")){
					errmsg = "正常卡密码错";
					return false;
				}
				if(response.RetCode.trim().equals("61")){
					errmsg = "超限额拒绝";
					return false;
				}
				if(response.RetCode.trim().equals("75")){
					errmsg = "超过密码次数";
					return false;
				}
				if(response.RetCode.trim().equals("87")){
					errmsg = "MAC不正确";
					return false;
				}
				if(response.RetCode.trim().equals("91")){
					errmsg = "路由错";
					return false;
				}
				if(response.RetCode.trim().equals("96")){
					errmsg = "系统故障";
					return false;
				}
				if(response.RetCode.trim().equals("FF")){
					errmsg = "通讯故障,请重试";
					return false;
				}
				
				if (!response.RetCode.equals("00")) {
					bld.retcode = response.RetCode;
					new MessageBox("交易失败！");
					return false;
				}
			}else{
				new MessageBox("错误返回码："+line.trim());
				return false;
				
			}
			
			if(!(response.Balance.equals(""))){
				double ye = Double.parseDouble(response.Balance);
				ye = ManipulatePrecision.mul(ye, 0.01);
				bld.trace = Long.parseLong(response.VoiceNo);
			}
			if(!(response.Amount.equals(""))){
				double j = Double.parseDouble(response.Amount);
				j = ManipulatePrecision.mul(j, 0.01);
				bld.je = j;
			}
			
			bld.retcode = response.RetCode;
			bld.cardno = response.CardId;
			
			//
			errmsg = bld.retmsg;

			return true;
		} catch (Exception ex) {
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
		
	}

	public boolean XYKNeedPrintDoc() {
		if (!response.RetCode.equals("00"))
			return false;

		StringBuffer sb = new StringBuffer();
		PrintWriter pw = null;
		String saleJe = "";
		try {
			double j = Double.parseDouble(response.Amount);
			saleJe = String.valueOf(j);
			
			if(saleJe.indexOf(".") != -1){
				if((saleJe.length()-saleJe.indexOf(".")) !=3){
					saleJe = saleJe +"0";
				}
			}else{
				saleJe = saleJe +".00";
			}
			sb.append("          交通银行              \r\n");
			sb.append("---------------------------------\r\n");
			sb.append("商户号(MERCHANT ID)：" + response.PosMid + "\r\n");
			sb.append("商户名称(MERCHANT NAME)： \r\n");
			sb.append("燕莎奥特莱斯购物中心 \r\n");
			sb.append("终端号(TERMINAL ID)：" + response.PosTid + "\r\n");
			sb.append("---------------------------------\r\n");
			sb.append("卡号(CARD NO.)：" + response.CardId + "\r\n");
			sb.append("卡类别(CARD TYPE)："+ response.BonusInfo+"\r\n");
			sb.append("卡有效期(EXP. DATE)：" + response.ExpDate + "\r\n");
			// sb.append("ISSUER \r\n");
			// sb.append("发卡行： \r\n");
			sb.append("系统参考号(REF. NO.)：" + response.VoiceNo + "\r\n");
			sb.append("批次号(BATCH. NO.)：" + response.BatchNo + "\r\n");
			sb.append("流水号(TRACE. NO.)：" + response.PosJnl + "\r\n");
			// sb.append("VOUCHER. NO. \r\n");
			// sb.append("凭证号：" +response.BatchNo +"\r\n");
			sb.append("授权号(AUTH. NO.)：" + response.AuthorCode + "\r\n");
			if (response.TransType.equals("1")
					|| response.TransType.equals("01")) {
				sb.append("交易类型(TRANS.TYPE.)：" + " 消费" + "\r\n");
			}
			if (response.TransType.equals("2")
					|| response.TransType.equals("02")) {
				sb.append("交易类型(TRANS.TYPE.)：" + " 撤销" + "\r\n");
			}
			if (response.TransType.equals("6")
					|| response.TransType.equals("06")) {
				sb.append("交易类型：(TRANS.TYPE.)" + " 脱机退货" + "\r\n");
				sb.append("原交易日期(OLDDATE)：" + response.oldDate 	+ "\r\n");
				
				if(response.oldMoney.indexOf(".") != -1){
					if((response.oldMoney.length()-response.oldMoney.indexOf(".")) !=3){
						response.oldMoney = response.oldMoney +"0";
					} 
				}else{
					response.oldMoney = response.oldMoney +".00";
				}
				sb.append("原交易金额(OLDMONEY): " + response.oldMoney + "\r\n");
			}
			sb.append("日期时间(DATE/TIME)：" + response.PosDate + " " + response.PosTime
					+ "\r\n");
			sb.append("交易金额(AMOUNT): " + saleJe + "\r\n");
			sb.append("---------------------------------\r\n");
			sb.append("持卡人签名(CARDHOLDER SIGNATURE)\r\n");
			sb.append(" \r\n");
			sb.append(" \r\n");
			sb.append(" \r\n");
			sb.append("---------------------------------\r\n");
			sb.append("本人已接受此单据金额的有关商品服务并愿意遵守与有关银行签订的持卡人合约一切规定\r\n");
			sb.append("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES\r\n");

			sb.toString();
			if (PathFile.fileExist("c:\\dat\\Print.txt"))
				PathFile.deletePath("c:\\dat\\Print.txt");

			pw = CommonMethod.writeFileAppendGBK("c:\\dat\\Print.txt");

			if (pw != null) {
				pw.println(sb);
				pw.flush();
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (pw != null)
				pw.close();
		}
	}

	public void XYKPrintDoc() {
		ProgressBox pb = null;
		try {
			if (!PathFile.fileExist("c:\\dat\\print.txt") ) {
				new MessageBox("找不到签购单打印文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

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

	public boolean XYKCheckRetCode() {
		if (bld.retcode.trim().equals("00")||bld.retcode.trim().equals("-20")) {
			bld.retbz = 'Y';
			bld.retmsg = "金卡工程调用成功";

			return true;
		} else {
			bld.retbz = 'N';

			return false;
		}
	}

	public boolean checkDate(Text date) {
		return true;
	}

}
