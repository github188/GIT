package custom.localize.Nbbh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Nbbh_SaleBillMode extends Cmls_SaleBillMode {
	protected Nbbh_AccessLocalDB localDB = (Nbbh_AccessLocalDB) AccessLocalDB.getDefault();
	
	public void printMode(RulesDef def){
		printLine(Language.apply("券名称:") +"#@# "+ def.TRGSIZE1.trim() + "|" + def.TRGBOLD1.trim() + "|"+ def.TRGFONT1.trim() + "#@#" + def.TRGNAME + "\n");
		printLine("                                       " + "\n");
		printLine(Language.apply("券使用范围:") +"#@# "+ def.TRGSIZE2.trim() + "|" + def.TRGBOLD2.trim() + "|"+ def.TRGFONT2.trim() + "#@#" + def.TRGMEMO + "\n");
		printLine("                                       " + "\n");
		printLine(Language.apply("券使用说明:") +"#@# "+ def.TRGSIZE3.trim() + "|" + def.TRGBOLD3.trim() + "|"+ def.TRGFONT3.trim() + "#@#" + def.TRGDETAIL + "\n");	
		printLine("                                       " + "\n");
	}

	// 打印规则联
	public void printSaleRules() {
		if(1==1)
		{//改为调用第三方EXE打印券信息
			try
			{
				//this.salehead.ysje
				String exePath = "c:\\pos95\\pos98\\pos.exe";
				if (PathFile.fileExist(exePath))
	            {
	            	CommonMethod.waitForExec(exePath + " " + String.valueOf(this.salehead.ysje), false);
	            }
	            else
	            {
	            	PosLog.getLog(this.getClass().getSimpleName()).info("未找到打印EXE：" + exePath);
	            }
			}
			catch(Exception ex)
			{
				PosLog.getLog(this.getClass().getSimpleName()).info(ex);
				PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			}
			return;//调用第三方EXE打印
		}

		
	    Vector PrintRules=null;
		String path = GlobalVar.ConfigPath+"\\Nbbh-px.ini";
		BufferedReader br = null;
		boolean bn=false;
		int n = 0;
		PrintRules=new Vector();
		if (!localDB.getRule(PrintRules,String.valueOf(salehead.ysje),salehead.rqsj)){
			new MessageBox("【打印规则信息】读取失败!");
			return;
		}
		if (PrintRules == null || PrintRules.size() <= 0) {
			return;
		}
		for (int i = 0; i < PrintRules.size(); i++) {
			RulesDef def = (RulesDef) PrintRules.elementAt(i);

			if (def.TRGBD.equals("Y")) {
				if (!bn){
				  bn=true;
				  printLine("                                       " + "\n\n");
				  printLine(Language.apply("收银机号:") + salehead.syjh + "     "+Language.apply("交易号:") + Convert.increaseLong(salehead.fphm, 8));
				  printLine(Language.apply("收银员号:") + salehead.syyh + "     "+Language.apply("门店号:") + GlobalInfo.sysPara.mktcode);
				  printLine("=======================================" + "\n\n");
				  printLine("                                       " + "\n\n");
				}  
				printMode(def);
				PrintRules.remove(i);
			}
		}
		try {
			int j = GlobalInfo.sysPara.printNo;
			for (int i = 0; i < PrintRules.size(); i++) {
				RulesDef def = (RulesDef) PrintRules.elementAt(i);
				if (GlobalInfo.sysPara.printNo > 0) {// 控制随机打行数的参数大于0
					if (!PathFile.fileExist(path)// 读取配置文件中已经打到的行数
							|| ((br = CommonMethod.readFile(path)) == null)) {
						new MessageBox("读取排序数据失败!", null, false);
						return;
					}
					int PrintNo = Convert.toInt(br.readLine());
					if (PrintRules.size() - PrintNo >= GlobalInfo.sysPara.printNo) {// 剩余的条数够打
						if ((j >= 0) && (i >= PrintNo)) {
							j = j - 1;
							if (!bn){
								  bn=true;
								  printLine("                                       " + "\n\n");
								  printLine(Language.apply("收银机号:") + salehead.syjh + "     "+Language.apply("交易号:") + Convert.increaseLong(salehead.fphm, 8));
								  printLine(Language.apply("收银员号:") + salehead.syyh + "     "+Language.apply("门店号:") + GlobalInfo.sysPara.mktcode);
								  printLine("=======================================" + "\n\n");
								  printLine("                                       " + "\n\n");
								}
							printMode(def);
							n = i+1;

						}
					} else if ((PrintRules.size() - PrintNo > 0)//
							&& (PrintRules.size() - PrintNo < GlobalInfo.sysPara.printNo)) {
						if ((j >= 0)
								&& (i <= GlobalInfo.sysPara.printNo
										- (PrintRules.size() - PrintNo))) {
							j = j - 1;
							if (!bn){
								  bn=true;
								  printLine("                                       " + "\n\n");
								  printLine(Language.apply("收银机号:") + salehead.syjh + "     "+Language.apply("交易号:") + Convert.increaseLong(salehead.fphm, 8));
								  printLine(Language.apply("收银员号:") + salehead.syyh + "     "+Language.apply("门店号:") + GlobalInfo.sysPara.mktcode);
								  printLine("=======================================" + "\n\n");
								  printLine("                                       " + "\n\n");
								}
							printMode(def);
							n = i+1;
						} else if ((j >= 0) && (i > PrintNo)) {
							j = j - 1;
							printMode(def);

						}
					} else if (PrintRules.size() - PrintNo < 0) {
						if ((j >= 0) && (i <= GlobalInfo.sysPara.printNo)) {
							j = j - 1;
							if (!bn){
								  bn=true;
								  printLine("                                       " + "\n\n");
								  printLine(Language.apply("收银机号:") + salehead.syjh + "     "+Language.apply("交易号:") + Convert.increaseLong(salehead.fphm, 8));
								  printLine(Language.apply("收银员号:") + salehead.syyh + "     "+Language.apply("门店号:") + GlobalInfo.sysPara.mktcode);
								  printLine("=======================================" + "\n\n");
								  printLine("                                       " + "\n\n");
								}
							printMode(def);
							n = i+1;
						}

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter pw = null;// 写入打印到的行数
		try {
			pw = CommonMethod.writeFile(path);

			if (pw != null) {
				pw.println(n);
				pw.flush();
			}
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		printLine("\n\n");
		printCutPaper();
	}
  
	
//	 打印面值卡联
	public void printMZKBill(int type)
	{
		int i = 0;
		
		if (GlobalInfo.sysPara.mzkbillnum <= 0) { return; }

		try
		{
			// 先检查是否有需要打印的付款方式
			for (i = 0; i < originalsalepay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				if (mode == null)
				{
					continue;
				}

				if ((type == 1) && (mode.type == '4'))
				{
					break;
				}

				if ((type == 2) && CreatePayment.getDefault().isPaymentFjk(mode.code))
				{
					break;
				}
			}

			if (i >= originalsalepay.size()) { return; }

			for (int n = 0; n < GlobalInfo.sysPara.mzkbillnum; n++)
			{
				
				// 开始新打印
				printStart();

				if (type == 1)
				{
					Printer.getDefault().printLine_Journal("\n        "+Language.apply("消费卡")+ "    " + Language.apply("**重印"+salehead.printnum+"**"));
				}

				if (type == 2)
				{
					Printer.getDefault().printLine_Journal("\n        "+Language.apply("返券卡"));
				}

				Printer.getDefault().printLine_Journal(Language.apply(" 门店号:") + GlobalInfo.sysPara.mktcode + "   "+Language.apply("交易时间:") + salehead.rqsj.substring(0, 10));
				Printer.getDefault().printLine_Journal(Language.apply(" 交易号:") + Convert.increaseLong(salehead.fphm, 8) + "     "+Language.apply("收银机号:") + salehead.syjh);
				Printer.getDefault().printLine_Journal(Language.apply(" 收银员:") + GlobalInfo.posLogin.name + "     "+Language.apply("交易类型:") + String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead)));
				Printer.getDefault().printLine_Journal(" --------------------------------");
				Printer.getDefault().printLine_Journal(Language.apply(" 卡号")+"                     "+Language.apply("消费金额"));

				int num = 0;
				double hj = 0;
				String line = null;

				for (i = 0; i < originalsalepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
					PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

					if (((type == 1) && (mode.type == '4')) || ((type == 2) && (CreatePayment.getDefault().isPaymentFjk(mode.code))))
					{
						num++;
						line = Convert.appendStringSize("", pay.payno, 1, 20, 40, 0);
						line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb)), 25, 7, 40, 0);

						Printer.getDefault().printLine_Journal(line);

						if (pay.hl == 0)
						{
							pay.hl = 1;
						}

						hj += (pay.ybje * pay.hl);
					}
				}

				Printer.getDefault().printLine_Journal(" --------------------------------");
				
				if (type == 1)
				{
					Printer.getDefault().printLine_Journal(Language.apply(" 本次共 {0} 张消费卡" ,new Object[]{num+""}));
				}

				if (type == 2)
				{
					Printer.getDefault().printLine_Journal(Language.apply(" 本次共 {0} 张返券卡消费" ,new Object[]{num+""}));
				}

				Printer.getDefault().printLine_Journal(Language.apply(" 合计金额")+ ManipulatePrecision.doubleToString(hj * SellType.SELLSIGN(salehead.djlb))+ "  " + "元");
//				printCutPaper();
				Printer.getDefault().cutPaper_Journal();
				
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	public void printBill()
	{
		int choice = GlobalVar.Key1;

		// 开始打印前的发票号
		salefph = Printer.getDefault().getCurrentSaleFphm();
		
		//开始打印前的抬头
		salefpname = Printer.getDefault().getCurrentSaleFpName();

		// 重打印小票时，如果是非超市且参数定义既打印机制单又打营业员联，才提示选择打印部分
		if (('N' != (GlobalInfo.syjDef.issryyy)) && (salehead.printnum > 0) && GlobalInfo.sysPara.fdprintyyy == 'Y')
		{
			StringBuffer info = new StringBuffer();
			info.append(Convert.appendStringSize("", Language.apply("请按键选择重打印内容"), 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", Language.apply("1、打印全部小票单据"), 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", Language.apply("2、只打印机制小票单"), 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", Language.apply("3、只打印营业员列印"), 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", Language.apply("按其他键则放弃重打印"), 1, 30, 30, 2) + "\n");

			choice = new MessageBox(info.toString(), null, false).verify();
		}

		int num = 1;
		boolean sequenceflag = true;
		if (GlobalInfo.sysPara.printyyhsequence == 'B')
			sequenceflag = false;

		while (num <= 2)
		{
			if (sequenceflag)
			{
				if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key3))
				{
					if (((YyySaleBillMode) YyySaleBillMode.getDefault()).isLoad())
					{
						printYyyBillPrintMode();
					}
					else
					{
						// 打印营业员分单联
						printYYYBill();
					}
				}

				sequenceflag = false;
			}
			else
			{

				if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key2))
				{
					String[] ss = null;
					//如果是正常销售就不穿重打原因字段
					if(salehead.printnum == 0)
					{
						String printType = "1";              //发票打印类型  1正常销售 2重打
						
						String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
						String usedfphnum = "";

						String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(salehead.fphm) ,printType ,"0" ,salehead.syyh ,startfph, usedfphnum,ManipulateDateTime.getDateTimeByClock()};
						ss = s;
					}
					
					// 根据参数控制打印销售小票的份数
					printnum = 0;
					
					for (int salebillnum = 0; salebillnum < GlobalInfo.sysPara.salebillnum; salebillnum++)
					{

						// 打印交易小票联
						printSellBill();
						printnum++;
					}

					// 打印附加的各个小票联
					printAppendBill();
					
					if(salehead.printnum == 0)
					{
						ss[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm()-1);
						new Nbbh_NetService().postReprint(ss);
					}
				}

				sequenceflag = true;
			}

			num = num + 1;
		}

		// 记录本笔小票用的发票张数
		saveSaleFphm(salefph);
		
		try {
			// 打印规则联
			printSaleRules();
		} catch (Exception er) {
			er.printStackTrace();
		}
	}
}
