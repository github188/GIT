package com.efuture.javaPos.PrintTemplate;

import java.util.ArrayList;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SalePaySummaryDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;


public class SyySaleBillMode extends PrintTemplate
{
    protected static SyySaleBillMode syySaleBillMode = null;
    
    protected SaleSummaryDef ssd = null;
    protected ArrayList payList = null;
    
    protected final int SSBM_text  	= 0;
    protected final int SSBM_syjh  	= 1;
    protected final int SSBM_syyh  	= 2;
    protected final int SSBM_bc  		= 3;
    protected final int SSBM_time  	= 4;
    protected final int SSBM_ysje  	= 5;
    protected final int SSBM_sjfk  	= 6;
    protected final int SSBM_zl  		= 7;
    protected final int SSBM_sysy  	= 8;
    protected final int SSBM_zkje  	= 9;
    protected final int SSBM_xsbs  	= 10;
    protected final int SSBM_xsje  	= 11;
    protected final int SSBM_thbs  	= 12;
    protected final int SSBM_thje  	= 13;
    protected final int SSBM_hcbs  	= 14;
    protected final int SSBM_hcje  	= 15;
    protected final int SSBM_qxbs  	= 16;
    protected final int SSBM_qxje  	= 17;
    protected final int SSBM_payname 	= 18;
    protected final int SSBM_je  		= 19;
    protected final int SSBM_bs  		= 20;
    protected final int SSBM_paycode 	= 21;
    protected final int SSBM_syyname 	= 22;
    protected final int SSBM_date       = 23;
    
    public static SyySaleBillMode getDefault()
    {
        if (SyySaleBillMode.syySaleBillMode == null)
        {
            SyySaleBillMode.syySaleBillMode = CustomLocalize.getDefault()
                                                            .createSyySaleBillMode();
        }

        return SyySaleBillMode.syySaleBillMode;
    }

    public boolean ReadTemplateFile()
    {
        super.InitTemplate();

        return super.ReadTemplateFile(Title,
                                      GlobalVar.ConfigPath +
                                      "//SyySalePrintMode.ini");
    }

    public void setTemplateObject(SaleSummaryDef ssd, ArrayList payList)
    {
        this.ssd     = ssd;
        this.payList = payList;
    }

    public String getItemDataString(PrintTemplateItem item, int index)
    {
        String line = null;
        SalePaySummaryDef spsd = null;

        try
        {
            line = extendCase(item,index);
            if (line == null)
            {        	
	            switch (Integer.parseInt(item.code))
	            {
	                case SSBM_text: //文本
	
						if (item.text == null)
						{
							line = "";
						}
						else
						{
							if (item.text.trim().indexOf("calc|") == 0)
							{
								line = super.calString(item.text, index);
							}
							else
							{
								line = item.text;
							}
						}
						break;
	
	                case SSBM_syjh: //收银机号
	
	                    if (GlobalInfo.syjDef.syjh == null)
	                    {
	                        line = "";
	                    }
	                    else
	                    {
	                        line = GlobalInfo.syjDef.syjh;
	                    }
	
	                    break;
	
	                case SSBM_syyh: //收银员号
	                    line = ssd.syyh;
	
	                    break;
	                   
	                case SSBM_syyname: //收银员名称
	                	
					   if (GlobalInfo.posLogin.operrange == 'Y')
					   {
			                 OperUserDef staff = new OperUserDef();

			                 if (!DataService.getDefault().getOperUser(staff, ssd.syyh.trim()))
			                 {
			                	 line = "";
			                 }
			                 else
			                 {
			                	 line = staff.name;
			                 }
					   }
					   else
					   {
						   line = GlobalInfo.posLogin.name;
					   }
	                	
	                	
	                	break;
	
	                case SSBM_bc: //班次
	                    line = DataService.getDefault().getTimeNameByCode(ssd.bc);
	
	                    break;
	
	                case SSBM_time: //打印时间
	
	                    ManipulateDateTime mdt = new ManipulateDateTime();
	                    line = mdt.getDateBySlash() + " " + mdt.getTime();
	
	                    break;
	
	                case SSBM_ysje: //应收金额
	                    line = ManipulatePrecision.doubleToString(ssd.ysje);
	
	                    break;
	
	                case SSBM_sjfk: //实收金额
	                    line = ManipulatePrecision.doubleToString(ssd.sjfk);
	
	                    break;
	
	                case SSBM_zl: //找零金额
	                    line = ManipulatePrecision.doubleToString(ssd.zl);
	
	                    break;
	
	                case SSBM_sysy: //损溢金额
	                    line = ManipulatePrecision.doubleToString(ssd.sysy);
	
	                    break;
	
	                case SSBM_zkje: //折扣金额
	                    line = ManipulatePrecision.doubleToString(ssd.zkje);
	
	                    break;
	
	                case SSBM_xsbs: //销售笔数
	                    line = String.valueOf(ssd.xsbs);
	
	                    break;
	
	                case SSBM_xsje: //销售金额
	                    line = ManipulatePrecision.doubleToString(ssd.xsje);
	
	                    break;
	
	                case SSBM_thbs: //退货笔数
	                    line = String.valueOf(ssd.thbs);
	
	                    break;
	
	                case SSBM_thje: //退货金额
	                    line = ManipulatePrecision.doubleToString(ssd.thje);
	
	                    break;
	
	                case SSBM_hcbs: //红冲笔数
	                    line = String.valueOf(ssd.hcbs);
	
	                    break;
	
	                case SSBM_hcje: //红冲金额
	                    line = ManipulatePrecision.doubleToString(ssd.hcje);
	
	                    break;
	
	                case SSBM_qxbs: //取消笔数
	                    line = String.valueOf(ssd.qxbs);
	
	                    break;
	
	                case SSBM_qxje: //取消金额
	                    line = ManipulatePrecision.doubleToString(ssd.qxje);
	
	                    break;
	
	                case SSBM_payname: //付款名称
	
	                    if ((payList == null) || (payList.size() <= 0))
	                    {
	                        break;
	                    }
	
	                    spsd = (SalePaySummaryDef) payList.get(index);
	                    line = spsd.payname;
	
	                    break;
	
	                case SSBM_je: //付款金额
	
	                    if ((payList == null) || (payList.size() <= 0))
	                    {
	                        break;
	                    }
	
	                    spsd = (SalePaySummaryDef) payList.get(index);
	
	                    line = ManipulatePrecision.doubleToString(spsd.je);
	
	                    break;
	
	                case SSBM_bs: //付款笔数
	
	                    if ((payList == null) || (payList.size() <= 0))
	                    {
	                        break;
	                    }
	
	                    spsd = (SalePaySummaryDef) payList.get(index);
	
	                    line = String.valueOf(spsd.bs);
	
	                    break;
	
	                case SSBM_paycode: //付款编码
	
	                    if ((payList == null) || (payList.size() <= 0))
	                    {
	                        break;
	                    }
	
	                    spsd = (SalePaySummaryDef) payList.get(index);
	
	                    line = String.valueOf(spsd.paycode);
	
	                    break;
	                case SSBM_date: //日期时间
	                	 line = ssd.date;
	                	break;
	            }
            }
            
            if (line != null && Integer.parseInt(item.code) != 0 && item.text != null && !item.text.trim().equals(""))
            {
                //line = item.text + line;
            	int maxline = item.length - Convert.countLength(item.text);
            	line = item.text + Convert.appendStringSize("",line,0,maxline,maxline,item.alignment);
            }

            return line;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
	
	public void printBill()
	{
		// 设置打印方式
		printSetPage();
		
		// 打印头部区域
		printHeader();

        // 打印明细区域
		printDetail();

		// 打印汇总区域
		printTotal();
		
        // 打印尾部区域
		printBottom();

        // 切纸
        printCutPaper();
	}
	
	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");
		
		// 循环打印明细
        for (int i = 0; i < payList.size(); i++)
        {
        	printVector(getCollectDataString(Detail,i,Width));
        }
	}
}
