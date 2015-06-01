package com.efuture.javaPos.PrintTemplate;

import java.util.ArrayList;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SaleManSummaryDef;


public class BusinessPerBillMode extends PrintTemplate
{
    protected static BusinessPerBillMode businessPerBillMode = null;
    protected SaleManSummaryDef smsd = null;
    protected ArrayList bussinessPerList = null;
    protected final int BPB_text = 0;
    protected final int BPB_syyh = 1;
    protected final int BPB_syjh = 2;
    protected final int BPB_bc = 3;
    protected final int BPB_PrintTime = 4;
    protected final int BPB_yyname = 5;
    protected final int BPB_xsbs = 6;
    protected final int BPB_xsje = 7;
    protected final int BPB_xszk = 8;
    protected final int BPB_thbs = 9;
    protected final int BPB_thje = 10;
    protected final int BPB_thzk = 11;
    protected final int BPB_jybs = 12;
    protected final int BPB_jyje = 13;
    protected final int BPB_jyzk = 14;
    protected final int BPB_yyhbs = 15;
    protected final int BPB_yyhje = 16;
    protected final int BPB_yyhzzk = 17;
    protected final int BPB_yyhh = 18;

    public static BusinessPerBillMode getDefault()
    {
        if (BusinessPerBillMode.businessPerBillMode == null)
        {
            BusinessPerBillMode.businessPerBillMode = CustomLocalize.getDefault().createBusinessPerBillMode();
        }

        return BusinessPerBillMode.businessPerBillMode;
    }

    public boolean ReadTemplateFile()
    {
        super.InitTemplate();

        return super.ReadTemplateFile(Title, GlobalVar.ConfigPath + "//BusinessPerPrintMode.ini");
    }

    public void setTemplateObject(ArrayList bussinessPerList)
    {
        this.bussinessPerList = bussinessPerList;
    }

    public String getItemDataString(PrintTemplateItem item, int index)
    {
        String line = null;

        try
        {
            line = extendCase(item, index);

            if (line == null)
            {
                switch (Integer.parseInt(item.code))
                {
                    case BPB_text: //文本
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

                    case BPB_syyh: //收银员号
                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(0);

                        line = String.valueOf(smsd.syyh);

                        break;                    	

                    case BPB_syjh: //收银机号
                        line = GlobalInfo.syjStatus.syjh.trim();

                        break;

                    case BPB_bc: //班次
                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(0);

                        if (smsd.bc == '#')
                        	line = Language.apply("所有班");
                        else
                        	line = DataService.getDefault().getTimeNameByCode(smsd.bc);

                        break;

                    case BPB_PrintTime: //打印时间

                        ManipulateDateTime mdt = new ManipulateDateTime();
                        line = mdt.getDateBySlash() + " " + mdt.getTime();

                        break;

                    case BPB_yyname: //营业员名称

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        if (smsd.name != null)
                        {
                            line = smsd.name.trim();
                        }
                        else
                        {
                            line = "";
                        }

                        break;

                    case BPB_xsbs: //销售笔数

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);
                        line = String.valueOf(smsd.xsbs);

                        break;

                    case BPB_xsje: //销售金额

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);
                        line = ManipulatePrecision.doubleToString(smsd.xsje);

                        break;

                    case BPB_xszk: //销售折扣

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        line = ManipulatePrecision.doubleToString(smsd.xszk);

                        break;

                    case BPB_thbs: //退货笔数

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        line = String.valueOf(smsd.thbs);

                        break;

                    case BPB_thje: //退货金额

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        line = ManipulatePrecision.doubleToString(smsd.thje);

                        break;

                    case BPB_thzk: //退货折扣

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        line = ManipulatePrecision.doubleToString(smsd.thzk);

                        break;

                    case BPB_jybs: //交易笔数

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        line = String.valueOf(smsd.xsbs + smsd.thbs);

                        break;

                    case BPB_jyje: //交易金额

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        line = ManipulatePrecision.doubleToString(smsd.xsje - smsd.thje);

                        break;

                    case BPB_jyzk: //交易折扣

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        line = ManipulatePrecision.doubleToString(smsd.xszk + smsd.thzk);

                        break;

                    case BPB_yyhbs: //营业员总笔数

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        int bscount = 0;

                        for (int i = 0; i < bussinessPerList.size(); i++)
                        {
                            smsd    = (SaleManSummaryDef) bussinessPerList.get(i);
                            bscount = bscount + (smsd.xsbs + smsd.thbs);
                        }

                        line = String.valueOf(bscount);

                        break;

                    case BPB_yyhje: //营业员总金额

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        double jecount = 0;

                        for (int i = 0; i < bussinessPerList.size(); i++)
                        {
                            smsd = (SaleManSummaryDef) bussinessPerList.get(i);

                            jecount = jecount + (smsd.xsje - smsd.thje);
                        }

                        line = ManipulatePrecision.doubleToString(jecount);

                        break;

                    case BPB_yyhzzk: //营业员总折扣

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        double zkcount = 0;

                        for (int i = 0; i < bussinessPerList.size(); i++)
                        {
                            smsd = (SaleManSummaryDef) bussinessPerList.get(i);

                            zkcount = zkcount + (smsd.xszk + smsd.thzk);
                        }

                        line = ManipulatePrecision.doubleToString(zkcount);

                        break;

                    case BPB_yyhh: //营业员编号

                        if ((bussinessPerList == null) || (bussinessPerList.size() <= 0))
                        {
                            break;
                        }

                        smsd = (SaleManSummaryDef) bussinessPerList.get(index);

                        line = String.valueOf(smsd.yyyh);

                        break;
                }
            }

            if ((line != null) && (Integer.parseInt(item.code) != 0) && (item.text != null) && !item.text.trim().equals(""))
            {
                //line = item.text + line;
                int maxline = item.length - Convert.countLength(item.text);
                line = item.text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
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
        for (int i = 0; i < bussinessPerList.size(); i++)
        {
            printVector(getCollectDataString(Detail, i, Width));
        }
    }
}
