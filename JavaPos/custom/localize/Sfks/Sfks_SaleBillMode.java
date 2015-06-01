package custom.localize.Sfks;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_SaleBillMode;

public class Sfks_SaleBillMode extends Bhls_SaleBillMode
{
    private String isPagePrint = "N";
    private int pageLines = 1;
    private String headTitle = "";
    private boolean isLoadPara = false;
    
    public boolean convertPayDetail(SalePayDef spd, SalePayDef spd1)
    {
	// 联华OK卡，SMART卡,需要汇总
	if (spd.paycode.equals("0305") || spd.paycode.equals("0303"))
	{
	    return true;
	}
	else
	{
	    return super.convertPayDetail(spd, spd1);
	}
    }
    
    public void printBill()
    {
	// 加载配置参数
	loadConfig();
	
	// 打印营业员分单联
	printYYYBill();
	
	// GlobalInfo.sysPara.fdprintyyy(N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
	// 超市或要参数要求打印小票联,才打印
	if ((GlobalInfo.sysPara.fdprintyyy != 'A' || !(GlobalInfo.syjDef.issryyy == 'Y' || (GlobalInfo.syjDef.issryyy == 'A' && !((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市")))))
	{
	    // 打印交易小票联
	    printSellBill();
	}
	
	// 打印附加的各个小票联
	printAppendBill();
	
	/*
         * 新银联接口不打印签购单 // 打印银联交易签购单 printBankBill();
         */
    }
    
    protected void printAppendBill()
    {
	// 打印面值卡联
	printMZKBill(1);
	
	// 打印银联退货凭证联
	printBankBackBill();
    }
    
    public void printBankBill()
    {
	// 在原始付款清单中,查找是否有银联卡付款方式
	for (int i = 0; i < originalsalepay.size(); i++)
	{
	    SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
	    PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);
	    if (mode.isbank == 'Y' && pay.batch != null && pay.batch.length() > 0)
	    {
		PaymentBank.printXYKDoc(pay.batch);
	    }
	}
    }
    
    public void printBankBackBill()
    {
	// 查找是否有银联卡退货付款方式
	for (int i = 0; i < salepay.size(); i++)
	{
	    SalePayDef pay = (SalePayDef) salepay.elementAt(i);
	    if (pay.paycode.equals("0304") || pay.paycode.equals("0306"))
	    {
		Printer.getDefault().startPrint_Journal();
		
		Printer.getDefault().printLine_Journal("       银联卡退货凭证");
		Printer.getDefault().printLine_Journal("");
		Printer.getDefault().printLine_Journal("商户名称:  " + GlobalInfo.sysPara.mername);
		Printer.getDefault().printLine_Journal("日期时间:  " + ManipulateDateTime.getCurrentDateTime());
		Printer.getDefault().printLine_Journal("收银机号:  " + GlobalInfo.syjDef.syjh);
		Printer.getDefault().printLine_Journal("银联卡号:  " + pay.payno);
		Printer.getDefault().printLine_Journal("退货金额:  " + ManipulatePrecision.doubleToString(0 - pay.ybje));
		Printer.getDefault().printLine_Journal("持卡人签名:");
		Printer.getDefault().printLine_Journal("");
		Printer.getDefault().printLine_Journal("");
		Printer.getDefault().printLine_Journal("");
		Printer.getDefault().printLine_Journal("");
		Printer.getDefault().printLine_Journal("注:14个工作日内又银行划账置原消费卡号内,注意查收");
		
		Printer.getDefault().cutPaper_Journal();
	    }
	}
    }
    
    public void printYYYBill()
    {
	int i = 0, j = 0, k = 0;
	Vector set = null;
	CalcRulePopDef calPop = null;
	SaleGoodsDef sgd = null;
	SalePayDef spd = null;
	String line;
	String[] prnInfo = null;
	double[] yfdpay = null;
	double yfdyy = 0;
	double hjpay = 0;
	
	// 系统参数定义为打印分单且是营业员小票，才打印营业员联
	
/*	if (!(GlobalInfo.sysPara.fdprintyyy != 'N' && (GlobalInfo.syjDef.issryyy == 'Y' || (GlobalInfo.syjDef.issryyy == 'A' && !((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市")))))
	{
	    return;
	}*/
	
	// 先把商品进行分组
	set = new Vector();
	for (i = 0; i < salegoods.size(); i++)
	{
	    sgd = (SaleGoodsDef) salegoods.elementAt(i);
	    
	    // 查找是否相同商品,按营业员柜组分组
	    for (j = 0; j < set.size(); j++)
	    {
		calPop = (CalcRulePopDef) set.elementAt(j);
		if (calPop.code.equals(sgd.yyyh) && calPop.gz.equals(sgd.gz))
		{
		    calPop.row_set.add(String.valueOf(i));
		    break;
		}
	    }
	    if (j >= set.size())
	    {
		calPop = new CalcRulePopDef();
		calPop.code = sgd.yyyh;
		calPop.gz = sgd.gz;
		calPop.row_set = new Vector();
		calPop.row_set.add(String.valueOf(i));
		set.add(calPop);
	    }
	}
	
	// 先把找零金额从相应付款方式金额中减出来
	for (k = 0; k < salepay.size(); k++)
	{
	    spd = (SalePayDef) salepay.elementAt(k);
	    if (spd.flag == '2')
	    {
		for (int n = 0; n < salepay.size(); n++)
		{
		    SalePayDef spd1 = (SalePayDef) salepay.elementAt(n);
		    if (spd1.flag == '1' && spd1.paycode.equals(spd.paycode))
		    {
			spd1.je -= spd.je;
			break;
		    }
		}
	    }
	}
	
	// 初始化付款分摊
	double je = 0;
	yfdpay = new double[salepay.size()];
	for (k = 0; k < yfdpay.length; k++)
	    yfdpay[k] = 0;
	yfdyy = 0;
	
	// 按分组进行分单打印
	for (i = 0; i < set.size(); i++)
	{
	    calPop = (CalcRulePopDef) set.elementAt(i);
	    
	    if (new MessageBox("请将营业员(" + calPop.code + ")的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印").verify() == GlobalVar.Exit)
	    {
		continue;
	    }
	    
	    Printer.getDefault().startPrint_Slip();
	    
	    if (isPagePrint.equals("Y"))
		Printer.getDefault().setPagePrint_Slip(true, pageLines);
	    
	    if (!headTitle.equals(""))
	    {
		prnInfo = headTitle.split("\\|");
		
		if (prnInfo.length > 0 && prnInfo[0].length() > 1)
		    Printer.getDefault().printLine_Slip(prnInfo[0]);
		
		if (prnInfo.length > 1)
		    Printer.getDefault().printLine_Slip(prnInfo[1]);
		
		if (prnInfo.length > 2)
		    Printer.getDefault().printLine_Slip(prnInfo[2]);
	    }
	    else
	    {
		Printer.getDefault().printLine_Slip("               FOXTOWN");
		Printer.getDefault().printLine_Slip("**************************************");
		Printer.getDefault().printLine_Slip("欢迎光临FoxTown");
	    }
	    
	    Printer.getDefault().printLine_Slip(salehead.rqsj + "   交易序号:" + salehead.fphm);
	    Printer.getDefault().printLine_Slip("机号:" + salehead.syjh + "   收银员:" + salehead.syyh + ((salehead.printnum > 0) ? "  **重印**" : ""));
	    Printer.getDefault().printLine_Slip("营业员:" + calPop.code + "   柜组:" + calPop.gz);
	    Printer.getDefault().printLine_Slip("货号      商品名称       数量       金额");
	    Printer.getDefault().printLine_Slip("--------------------------------------");
	    
	    // 打印商品
	    int hjsl = 0;
	    double hjje = 0, hjzk = 0;
	    for (j = 0; j < calPop.row_set.size(); j++)
	    {
		sgd = (SaleGoodsDef) salegoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
		
		line = Convert.appendStringSize("", sgd.barcode, 0, 10, Width);
		line = Convert.appendStringSize(line, sgd.name, 10, 14, Width);
		line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true), 24, 4, Width, 1);
		line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.hjje), 29, 9, Width, 1);
		
		Printer.getDefault().printLine_Slip(line);
		
		hjsl += (int) sgd.sl;
		hjje += sgd.hjje;
		hjzk += sgd.hjzk;
	    }
	    
	    Printer.getDefault().printLine_Slip("--------------------------------------");
	    Printer.getDefault().printLine_Slip("(" + SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead) + ") " + hjsl + "项合计: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjje - hjzk), 0, 16, 16, 1));
	    if (hjzk > 0)
		Printer.getDefault().printLine_Slip("总折扣:   " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjzk), 0, 10, 10));
	    
	    // 先要按0500电子券收券规则，将券付款分摊到各收券商品
	    // 先计算所有商品允许收券的金额和本营业员联内允许收券商品合计
	    String[] s = null;
	    int lastsaqsp = -1;
	    int lastsbqsp = -1;
	    double maxaqje = 0;
	    double maxbqje = 0;
	    double yyyaqje = 0;
	    double yyybqje = 0;
	    for (j = 0; j < salegoods.size(); j++)
	    {
		sgd = (SaleGoodsDef) salegoods.elementAt(j);
		s = sgd.memo.split(",");
		if (s.length >= 2)
		{
		    maxaqje = ManipulatePrecision.add(maxaqje, Double.parseDouble(s[0]));
		    maxbqje = ManipulatePrecision.add(maxbqje, Double.parseDouble(s[1]));
		    if (j > lastsaqsp && Double.parseDouble(s[0]) > 0)
			lastsaqsp = j;
		    if (j > lastsbqsp && Double.parseDouble(s[1]) > 0)
			lastsbqsp = j;
		    
		    //
		    for (k = 0; k < calPop.row_set.size(); k++)
		    {
			if (Integer.parseInt((String) calPop.row_set.elementAt(k)) == j)
			{
			    yyyaqje = ManipulatePrecision.add(yyyaqje, Double.parseDouble(s[0]));
			    yyybqje = ManipulatePrecision.add(yyybqje, Double.parseDouble(s[1]));
			}
		    }
		}
	    }
	    if (maxaqje <= 0)
		maxaqje = 1;
	    if (maxbqje <= 0)
		maxbqje = 1;
	    
	    // 确定是否最后一个收券的商品
	    boolean islastsaqsp = false;
	    boolean islastsbqsp = false;
	    for (k = 0; k < calPop.row_set.size(); k++)
	    {
		if (Integer.parseInt((String) calPop.row_set.elementAt(k)) == lastsaqsp)
		{
		    islastsaqsp = true;
		}
		
		if (Integer.parseInt((String) calPop.row_set.elementAt(k)) == lastsbqsp)
		{
		    islastsbqsp = true;
		}
	    }
	    
	    double aqpayje = 0;
	    double bqpayje = 0;
	    hjpay = 0;
	    PaymentFjk fjkobj = CreatePayment.getDefault().getPaymentFjk();
	    for (k = 0; k < salepay.size(); k++)
	    {
		spd = (SalePayDef) salepay.elementAt(k);
		if (spd.flag == '2')
		    continue;
		if (!(spd.paycode.equals("0500") && (fjkobj.getFjkPayType(spd).equals("A") || fjkobj.getFjkPayType(spd).equals("B"))))
		    continue;
		
		// 把剩余未分摊金额，直接分摊到最后一个收券商品
		if (fjkobj.getFjkPayType(spd).equals("A"))
		{
		    if (islastsaqsp)
		    {
			je = spd.je - yfdpay[k];
		    }
		    else
		    {
			je = ManipulatePrecision.doubleConvert(yyyaqje / maxaqje * spd.je, 2, 1);
		    }
		    
		    aqpayje += je;
		}
		else if (fjkobj.getFjkPayType(spd).equals("B"))
		{
		    if (islastsbqsp)
		    {
			je = spd.je - yfdpay[k];
		    }
		    else
		    {
			je = ManipulatePrecision.doubleConvert(yyybqje / maxbqje * spd.je, 2, 1);
		    }
		    
		    bqpayje += je;
		}
		
		yfdpay[k] += je;
		
		hjpay += je;
		
		if (je > 0)
		    Printer.getDefault().printLine_Slip(Convert.appendStringSize("", spd.payname + ": ", 0, 10, 10) + Convert.appendStringSize("", ManipulatePrecision.doubleToString(je), 0, 10, 10, 1));
	    }
	    
	    // 打印其他付款方式分摊
	    for (k = 0; k < salepay.size(); k++)
	    {
		spd = (SalePayDef) salepay.elementAt(k);
		if (spd.flag == '2')
		    continue;
		if (spd.paycode.equals("0500") && (fjkobj.getFjkPayType(spd).equals("A") || fjkobj.getFjkPayType(spd).equals("B")))
		    continue;
		
		// 把剩余未分摊金额，直接分摊到最后一个商品
		if (i == (set.size() - 1))
		{
		    je = spd.je - yfdpay[k];
		}
		else
		{
		    double qfk = ((aqpayje > yyyaqje) ? yyyaqje : aqpayje) + ((bqpayje > yyybqje) ? yyybqje : bqpayje);
		    s = fjkobj.getFjkPayTotal(salepay).split(",");
		    double allqfk = ((Double.parseDouble(s[0]) > maxaqje) ? maxaqje : Double.parseDouble(s[0])) + ((Double.parseDouble(s[1]) > maxbqje) ? maxbqje : Double.parseDouble(s[1]));
		    je = ManipulatePrecision.doubleConvert((hjje - hjzk - qfk) / (salehead.ysje - allqfk) * spd.je, 2, 1);
		    /*
                         * // 如果是最后一个付款方式,先计算损溢,然后将用成交价+损溢=实付 - 前面其他付款方式 =
                         * 最后一个付款方式的付款金额 if (k == (salepay.size() - 1)) { //
                         * 0500电子券部分溢余按是否收券单算,其他剩余溢余那商品金额分摊 double aqyy =
                         * ManipulatePrecision.doubleConvert(aqpayje -
                         * yyyaqje,2,1); if (aqyy < 0) aqyy = 0; double bqyy =
                         * ManipulatePrecision.doubleConvert(bqpayje -
                         * yyybqje,2,1); if (bqyy < 0) bqyy = 0; s =
                         * fjkobj.getFjkPayTotal(salepay).split(","); double
                         * allaqyy = Double.parseDouble(s[0]) - maxaqje; if
                         * (allaqyy < 0) allaqyy = 0; double allbqyy =
                         * Double.parseDouble(s[1]) - maxbqje; if (allbqyy < 0)
                         * allbqyy = 0; qfk =
                         * ((aqpayje>yyyaqje)?yyyaqje:aqpayje) +
                         * ((bqpayje>yyybqje)?yyybqje:bqpayje); allqfk =
                         * ((Double.parseDouble(s[0])>maxaqje)?maxaqje:Double.parseDouble(s[0])) +
                         * ((Double.parseDouble(s[1])>maxbqje)?maxbqje:Double.parseDouble(s[1]));
                         * double qtyy = ManipulatePrecision.doubleConvert((hjje -
                         * hjzk - qfk) / (salehead.ysje - allqfk) *
                         * (salehead.fk_sysy + salehead.sswr_sysy - allaqyy -
                         * allbqyy),2,1); double syje = aqyy + bqyy + qtyy;
                         * 
                         * je = (hjje - hjzk) + syje - hjpay; }
                         */
		}
		
		yfdpay[k] += je;
		
		hjpay += je;
		
		if (je > 0)
		    Printer.getDefault().printLine_Slip(Convert.appendStringSize("", spd.payname + ": ", 0, 10, 10) + Convert.appendStringSize("", ManipulatePrecision.doubleToString(je), 0, 10, 10, 1));
	    }
	    
	    // 打印溢余分摊
	    if (i == (set.size() - 1))
	    {
		je = (salehead.fk_sysy + salehead.sswr_sysy) - yfdyy;
	    }
	    else
	    {
		// 0500电子券部分溢余按是否收券单算,其他剩余溢余那商品金额分摊
		double aqyy = ManipulatePrecision.doubleConvert(aqpayje - yyyaqje, 2, 1);
		if (aqyy < 0)
		    aqyy = 0;
		double bqyy = ManipulatePrecision.doubleConvert(bqpayje - yyybqje, 2, 1);
		if (bqyy < 0)
		    bqyy = 0;
		s = fjkobj.getFjkPayTotal(salepay).split(",");
		double allaqyy = Double.parseDouble(s[0]) - maxaqje;
		if (allaqyy < 0)
		    allaqyy = 0;
		double allbqyy = Double.parseDouble(s[1]) - maxbqje;
		if (allbqyy < 0)
		    allbqyy = 0;
		double qfk = ((aqpayje > yyyaqje) ? yyyaqje : aqpayje) + ((bqpayje > yyybqje) ? yyybqje : bqpayje);
		double allqfk = ((Double.parseDouble(s[0]) > maxaqje) ? maxaqje : Double.parseDouble(s[0])) + ((Double.parseDouble(s[1]) > maxbqje) ? maxbqje : Double.parseDouble(s[1]));
		double qtyy = ManipulatePrecision.doubleConvert((hjje - hjzk - qfk) / (salehead.ysje - allqfk) * (salehead.fk_sysy + salehead.sswr_sysy - allaqyy - allbqyy), 2, 1);
		je = aqyy + bqyy + qtyy;
		
		// 把付款合计的尾差加入到溢余中
		je += hjpay - (je + (hjje - hjzk));
	    }
	    yfdyy += je;
	    Printer.getDefault().printLine_Slip(Convert.appendStringSize("", "收您: ", 0, 10, 10) + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjpay), 0, 10, 10, 1) + ((Math.abs(je) > 0) ? "  溢余: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(je), 0, 10, 10, 1) : ""));
	    
	    if (salehead.hykh == null)
		salehead.hykh = "";
	    
	    Printer.getDefault().printLine_Slip(Convert.appendStringSize("", "会员卡号: ", 0, 10, 10) + Convert.appendStringSize("", salehead.hykh, 0, 30, 30));
	    
	    if (!headTitle.equals(""))
	    {
		if (prnInfo.length > 3)
		    Printer.getDefault().printLine_Slip(prnInfo[3]);
	    }
	    else
	    {
		Printer.getDefault().printLine_Slip("谢谢惠顾");
	    }
	    
	    Printer.getDefault().cutPaper_Slip();
	}
	
	// 把找零金额从相应付款方式金额中加回去
	for (k = 0; k < salepay.size(); k++)
	{
	    spd = (SalePayDef) salepay.elementAt(k);
	    if (spd.flag == '2')
	    {
		for (int n = 0; n < salepay.size(); n++)
		{
		    SalePayDef spd1 = (SalePayDef) salepay.elementAt(n);
		    if (spd1.flag == '1' && spd1.paycode.equals(spd.paycode))
		    {
			spd1.je += spd.je;
			break;
		    }
		}
	    }
	}
    }
    
    public void loadConfig()
    {
	String line = GlobalVar.ConfigPath + "/Config.ini";
	Vector para = null;
	String customItem = "";
	
	try
	{
	    if (isLoadPara)
		return;
	    
	    para = CommonMethod.readFileByVector(line);
	    
	    if (para == null)
	    {
		return;
	    }
	    
	    for (int i = 0; i < para.size(); i++)
	    {
		String[] row = (String[]) para.elementAt(i);
		
		if ("CustomItem1".equalsIgnoreCase(row[0]))
		{
		    customItem = row[1];
		}
		else
		{
		    continue;
		}
	    }
	    
	    if (customItem.length() > 1 && customItem.indexOf(",") > 0)
	    {
		String[] tmp = null;
		
		tmp = customItem.split(",");
		
		if (tmp.length > 0)
		    isPagePrint = tmp[0].trim();
		if (tmp.length > 1)
		    pageLines = Integer.parseInt(tmp[1].trim());
		if (tmp.length > 2)
		    headTitle = tmp[2];
		
		isLoadPara = true;
	    }
	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	    isLoadPara = false;
	}
    }
    
    public static void main(String[] args)
    {
	Sfks_SaleBillMode instance = new Sfks_SaleBillMode();
	instance.loadConfig();
    }
}
