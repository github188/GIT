package custom.localize.Ytpj;

import java.io.BufferedReader;
import java.util.Vector;

import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Ytpj_MenuFuncBS extends MenuFuncBS {
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe) {
		if (GlobalInfo.sysPara.mktcode.indexOf(",") >= 0)
			GlobalInfo.sysPara.mktcode = GlobalInfo.sysPara.mktcode.split(",")[1];
		// 记录菜单功能日志
		if (mfd != null && mfd.workflag == 'Y')
			AccessDayDB.getDefault().writeWorkLog(
					"进入 \"[" + mfd.code + "]" + mfd.name + "\" 菜单", mfd.code);

		if (Integer.parseInt(mfd.code) == StatusType.MN_DHY) {
			try {
				//				 调用大会员接口 弹出大会员DLL界面   GlobalInfo.sysPara.mktcode
				String memberFuncReturn = "";
				if (RdPlugins.getDefault().getPlugins1().exec(
						1,
						GlobalInfo.sysPara.mktcode + ","
								+ GlobalInfo.posLogin.gh))
					memberFuncReturn = ((String) RdPlugins.getDefault()
							.getPlugins1().getObject()).trim();

				if (memberFuncReturn == null || memberFuncReturn.equals("")) {
					new MessageBox("Of_memberFunc接口调用失败!");
					return;
				}

				if (memberFuncReturn.length() < 2
						|| !memberFuncReturn.substring(0, 2).equals("00")) {
					new MessageBox("Of_memberFunc接口调用失败!");
					return;
				}

				String type = memberFuncReturn.substring(2, 3); // 回应标志
				if (type.equals("2")) {
					// 打印txt文件
					PosLog.getLog(getClass()).info("打印当前凭证TXT文件");
					PrintDHYDoc();// printDoc();//调用大会员付款方式的打印方法

				} else if (type.equals("3")) {
					String track2 = "";
					StringBuffer cardno = new StringBuffer();
					TextBox txt = new TextBox();
					if (!txt.open("请刷大会员卡", "大会员号", "请将大会员卡从刷卡槽刷入", cardno, 0,
							0, false, TextBox.MsrInput))
						return;

					track2 = txt.Track2;

					String cardFuncReturn = null;
					ProgressBox prg = new ProgressBox();
					prg.setText("正在查询会员卡,请稍等...");

					String funcCode = memberFuncReturn.substring(3, 5);
					if (RdPlugins.getDefault().getPlugins1().exec(
							8,
							GlobalInfo.syjStatus.syjh + ","
									+ GlobalInfo.posLogin.gh + "," + track2
									+ "," + funcCode))
						cardFuncReturn = (String) RdPlugins.getDefault()
								.getPlugins1().getObject();

					prg.close();
					prg = null;

					// new MessageBox( cardFuncReturn + "调用大会员查询函数成功");
					if (cardFuncReturn == null || cardFuncReturn.length() < 2
							|| !cardFuncReturn.substring(0, 2).equals("00")) {
						new MessageBox("调用大会员查询失败");
						return;
					}

					// 记录日志
					if (cardFuncReturn.length() > 20
							&& cardFuncReturn.substring(20, 21).equals("2")) {
						// 打印MemberReceipt.TXT文件
						PrintDHYDoc();// printDoc();
					}
				}
			} catch (Exception er) {
				er.printStackTrace();
				return;
			}
		} else {
			super.execFuncMenu(mfd, mffe);
		}
	}

	public void PrintDHYDoc() {

		ProgressBox pb = null;
		if (GlobalInfo.sysPara.bankprint < 1)
			return;
		String printName="";
		printName = "C:\\Member\\MemberReceipt.TXT";
		try {

			if (!PathFile.fileExist(printName)) {
				new MessageBox("永泰大会员凭条不存在，无法打印!", null, false);
				return;
			}
			pb = new ProgressBox();
			pb.setText("正在打印永泰大会员凭条,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++) {
				PrintDoc_Start();

				BufferedReader br = null;

				try {
					br = CommonMethod.readFileGBK(printName);

					if (br == null) {
						new MessageBox("打开" + printName + "打印文件失败!");

						return;
					}

					String line = null;

					while ((line = br.readLine()) != null) {
						if (line == null || line.length() <= 0)
							continue;
						if (line.indexOf("CUTPAPER") != -1) {
							PrintDoc_End();
							//new MessageBox("请撕下万达大会员凭条" );
							continue;
						}

						PrintDoc_Print(line);
					}
				} catch (Exception ex) {
					new MessageBox(ex.getMessage());
				} finally {
					if (br != null) {
						br.close();
					}
				}

				PrintDoc_End();
			}

		} catch (Exception ex) {
			new MessageBox("打印永泰大会员凭条发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (pb != null) {
				pb.close();
				pb = null;
			}
		}

	}

	private void PrintDoc_Print(String line) {
		switch (ConfigClass.RepPrintTrack) {
		case 1:
			Printer.getDefault().printLine_Normal(line);
			break;
		case 2:
			Printer.getDefault().printLine_Journal(line);
			break;
		case 3:
			Printer.getDefault().printLine_Slip(line);
			break;
		default:
			Printer.getDefault().printLine_Normal(line);
			break;
		}
	}

	private void PrintDoc_End() {
		switch (ConfigClass.RepPrintTrack) {
		case 1:
			Printer.getDefault().cutPaper_Normal();
			break;
		case 2:
			Printer.getDefault().cutPaper_Journal();
			break;
		case 3:
			Printer.getDefault().cutPaper_Slip();
			break;
		default:
			Printer.getDefault().cutPaper_Normal();
			break;
		}
	}

	private void PrintDoc_Start() {
		switch (ConfigClass.RepPrintTrack) {
		case 1:
			Printer.getDefault().startPrint_Normal();
			break;
		case 2:
			Printer.getDefault().startPrint_Journal();
			break;
		case 3:
			Printer.getDefault().startPrint_Slip();
			break;
		default:
			Printer.getDefault().startPrint_Normal();
			break;
		}
	}

}
