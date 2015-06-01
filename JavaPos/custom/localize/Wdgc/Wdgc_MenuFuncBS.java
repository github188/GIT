package custom.localize.Wdgc;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Wdgc_MenuFuncBS extends MenuFuncBS
{
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		// 记录菜单功能日志
		if (mfd != null && mfd.workflag == 'Y')
			AccessDayDB.getDefault().writeWorkLog("进入 \"[" + mfd.code + "]" + mfd.name + "\" 菜单", mfd.code);

		if (Integer.parseInt(mfd.code) == StatusType.MN_EXITST)
		{
			openExitSt(mfd, mffe);
		}
		else if (Integer.parseInt(mfd.code) == StatusType.MN_DHY)
		{
			// 调用大会员接口 弹出大会员DLL界面
			Dhy_PaymentBankFunc pbfunc = new Dhy_PaymentBankFunc(); // 调用大会员付款方式的打印方法
			String memberFuncReturn = null;
			if (RdPlugins.getDefault().getPlugins1().exec(1, GlobalInfo.syjStatus.syjh + "," + GlobalInfo.posLogin.gh))
				memberFuncReturn = ((String) RdPlugins.getDefault().getPlugins1().getObject()).trim();

			if (memberFuncReturn == null)
			{
				new MessageBox("Of_memberFunc接口调用失败!");
				return;
			}

			if (memberFuncReturn.length() < 2 || !memberFuncReturn.substring(0, 2).equals("00"))
			{
				new MessageBox("Of_memberFunc接口调用失败!\n" + memberFuncReturn.replaceAll("\\s+", " ").trim());
				return;
			}

			String type = memberFuncReturn.substring(2, 3); // 回应标志
			if (type.equals("2"))
			{
				// 打印txt文件
				PosLog.getLog(getClass()).info("打印当前凭证TXT文件");
				pbfunc.XYKPrintDoc();// printDoc();//调用大会员付款方式的打印方法

			}
			else if (type.equals("3"))
			{
				// 弹出刷卡窗口
				StringBuffer cardno = new StringBuffer();
				TextBox txt = new TextBox();
				if (!txt.open("请刷大会员卡", "大会员号", "请将大会员卡从刷卡槽刷入", cardno, 0, 0, false, TextBox.MsrRetTracks))
					return;

				PosLog.getLog(getClass()).fatal("Track1:" + txt.Track1 + " ;Track2:" + txt.Track2 + " ;Track3:" + txt.Track3 );
				
				if (null == txt.Track2 || txt.Track2.equals("") || txt.Track2.length() >= 40)
				{
					new MessageBox("读取磁道信息有问题！！！");
					return ;
				}
				
				String cardFuncReturn = null;
				ProgressBox prg = new ProgressBox();
				prg.setText("正在查询会员卡,请稍等...");

				String funcCode = memberFuncReturn.substring(memberFuncReturn.length() - 2);
				if (RdPlugins.getDefault().getPlugins1().exec(8, GlobalInfo.syjStatus.syjh + "," + GlobalInfo.posLogin.gh + "," + txt.Track2 + "," + funcCode))
					cardFuncReturn = (String) RdPlugins.getDefault().getPlugins1().getObject();

				prg.close();
				prg = null;

				if (cardFuncReturn == null || cardFuncReturn.length() < 2  )
				{
					new MessageBox("Of_CardFunc接口调用失败");
					return;
				}
				else if (!cardFuncReturn.substring(0, 2).equals("00"))
				{
					new MessageBox("Of_CardFunc接口返回的错误信息：\n" + cardFuncReturn.replaceAll("\\s+", " ").trim());
				}
				else if (cardFuncReturn.length() > 20 && cardFuncReturn.substring(20, 21).equals("2"))
				{
					// 打印MemberReceipt.TXT文件
					pbfunc.XYKPrintDoc();// printDoc();
				}
			}
		}
		else
		{
			super.execFuncMenu(mfd, mffe);
		}
	}

	private void openExitSt(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			me = new MessageBox("你确定要退出收银系统吗?\n\n等待20秒，严禁关机过程中断电.", null, true);

			if (me.verify() == GlobalVar.Key1)
			{
				// 关闭菜单功能窗口
				if (mffe != null)
					mffe.dispose();

				// 关闭收银主窗口
				if ((GlobalInfo.saleform != null) && GlobalInfo.saleform.closeForm())
				{
					// 记录登出日志
					AccessDayDB.getDefault().writeWorkLog("收银员登出", StatusType.WORK_RELOGIN);

					// 退出系统
					GlobalInfo.background.quitSysInfo();
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
	}

	/*
	 * private void printDoc_Print(String printStr) { String port =
	 * Wdgc_ConfigClass.Wdport; if (port == null || port.length() <= 0)
	 * Printer.getDefault().printLine_Journal(printStr); else if
	 * (port.trim().equals("1")) {
	 * Printer.getDefault().printLine_Normal(printStr); } else if
	 * (port.trim().equals("2")) {
	 * Printer.getDefault().printLine_Journal(printStr); } else if
	 * (port.trim().equals("3")) {
	 * Printer.getDefault().printLine_Slip(printStr); } }
	 * 
	 * private void printDoc_Start() { String port = Wdgc_ConfigClass.Wdport; if
	 * (port == null || port.length() <= 0) {
	 * Printer.getDefault().startPrint_Journal(); } else if
	 * (port.trim().equals("1")) { Printer.getDefault().startPrint_Normal(); }
	 * else if (port.trim().equals("2")) {
	 * Printer.getDefault().startPrint_Journal(); } else if
	 * (port.trim().equals("3")) { Printer.getDefault().startPrint_Slip(); } }
	 * 
	 * private void printDoc_End() { String port = Wdgc_ConfigClass.Wdport; if
	 * (port == null || port.length() <= 0)
	 * Printer.getDefault().cutPaper_Journal(); else if
	 * (port.trim().equals("1")) { Printer.getDefault().cutPaper_Normal(); }
	 * else if (port.trim().equals("2")) {
	 * Printer.getDefault().cutPaper_Journal(); } else if
	 * (port.trim().equals("3")) { Printer.getDefault().cutPaper_Slip(); } }
	 * 
	 * private void printDoc() { ProgressBox pb = null;
	 * if(GlobalInfo.sysPara.bankprint<1) return; String printName =
	 * "C:\\JavaPOS\\org.javapos.lib\\MemberRecpipt.TXT"; try { if
	 * (!PathFile.fileExist(printName)) { new MessageBox("签购单文本不存在无法打印!", null,
	 * false); return; } pb = new ProgressBox(); pb.setText("正在打印银联签购单,请等待...");
	 * 
	 * for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++) {
	 * printDoc_Start();
	 * 
	 * BufferedReader br = null;
	 * 
	 * try { br = CommonMethod.readFileGBK(printName);
	 * 
	 * if (br == null) { new MessageBox("打开" + printName + "打印文件失败!");
	 * 
	 * return; }
	 * 
	 * String line = null; while ((line = br.readLine()) != null) { if
	 * (line.length() <= 0) { continue; } printDoc_Print(line); } } catch
	 * (Exception ex) { new MessageBox(ex.getMessage()); } finally { if (br !=
	 * null) { br.close(); } }
	 * 
	 * printDoc_End(); }
	 * 
	 * } catch (Exception ex) { new MessageBox("打印签购单发生异常\n\n" +
	 * ex.getMessage()); ex.printStackTrace(); } finally { if (pb != null) {
	 * pb.close(); pb = null; }
	 * 
	 * if (PathFile.fileExist(printName)) { PathFile.deletePath(printName); } }
	 * }
	 */

}
