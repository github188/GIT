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
import com.efuture.javaPos.Struct.SaleGzSummaryDef;


public class ArkGroupBillMode extends PrintTemplate
{
    protected static ArkGroupBillMode arkGroupBillMode = null;
    protected SaleGzSummaryDef sgsd = null;
    protected ArrayList arkGroupListMode = null;
    protected final int AGB_text = 0; //文本
    protected final int AGB_syyh = 1; //收银员号
    protected final int AGB_syjh = 2; //收银机号
    protected final int AGB_class = 3;
    protected final int AGB_printTime = 4;
    protected final int AGB_gz = 5;
    protected final int AGB_xsbs = 6;
    protected final int AGB_xsje = 7;
    protected final int AGB_zke = 8;
    protected final int AGB_thbs = 9;
    protected final int AGB_thge = 10;
    protected final int AGB_thzk = 11;
    protected final int AGB_jybs = 12;
    protected final int AGB_jyje = 13;
    protected final int AGB_jyzk = 14;
    protected final int AGB_gzbs = 15;
    protected final int AGB_gzje = 16;
    protected final int AGB_gzzk = 17;
    protected final int AGB_gzbh = 18;

    public static ArkGroupBillMode getDefault()
    {
        if (ArkGroupBillMode.arkGroupBillMode == null)
        {
            ArkGroupBillMode.arkGroupBillMode = CustomLocalize.getDefault().createArkGroupBillMode();
        }

        return ArkGroupBillMode.arkGroupBillMode;
    }

    public boolean ReadTemplateFile()
    {
        super.InitTemplate();

        return super.ReadTemplateFile(Title, GlobalVar.ConfigPath + "//ArkGroupSalePrintMode.ini");
    }

    public void setTemplateObject(ArrayList arkGroupListMode)
    {
        this.arkGroupListMode = arkGroupListMode;
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
                    case AGB_text: //文本
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

                    case AGB_syyh: //收银员号
                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(0);

                        line = String.valueOf(sgsd.syyh);

                        break;

                    case AGB_syjh: //收银机号
                        line = GlobalInfo.syjStatus.syjh.trim();

                        break;

                    case AGB_class: //班次
                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(0);

                        if (sgsd.bc == '#')
                        	line = Language.apply("所有班");
                        else
                        	line = DataService.getDefault().getTimeNameByCode(sgsd.bc);
                        
                        break;

                    case AGB_printTime: //打印时间

                        ManipulateDateTime mdt = new ManipulateDateTime();
                        line = mdt.getDateBySlash() + " " + mdt.getTime();

                        break;

                    case AGB_gz: //柜组名称

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = sgsd.name.trim();

                        break;

                    case AGB_xsbs: //销售笔数

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = String.valueOf(sgsd.xsbs);

                        break;

                    case AGB_xsje: //销售金额

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = ManipulatePrecision.doubleToString(sgsd.xsje);

                        break;

                    case AGB_zke: //销售折扣

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = ManipulatePrecision.doubleToString(sgsd.xszk);

                        break;

                    case AGB_thbs: //退货笔数

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = String.valueOf(sgsd.thbs);

                        break;

                    case AGB_thge: //退货金额

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = ManipulatePrecision.doubleToString(sgsd.thje * (-1));

                        break;

                    case AGB_thzk: //退货折扣

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = ManipulatePrecision.doubleToString(sgsd.thzk);

                        break;

                    case AGB_jybs: //交易笔数

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = String.valueOf(sgsd.xsbs + sgsd.thbs);

                        break;

                    case AGB_jyje: //交易金额

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = ManipulatePrecision.doubleToString(sgsd.xsje - sgsd.thje);

                        break;

                    case AGB_jyzk: //交易折扣

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = ManipulatePrecision.doubleToString(sgsd.xszk - sgsd.thzk);

                        break;

                    case AGB_gzbs: //柜组笔数

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        int bscount = 0;

                        for (int i = 0; i < arkGroupListMode.size(); i++)
                        {
                            sgsd    = (SaleGzSummaryDef) arkGroupListMode.get(i);
                            bscount = bscount + (sgsd.xsbs + sgsd.thbs);
                        }

                        line = String.valueOf(bscount);

                        break;

                    case AGB_gzje: //柜组金额

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        double jecount = 0;

                        for (int i = 0; i < arkGroupListMode.size(); i++)
                        {
                            sgsd = (SaleGzSummaryDef) arkGroupListMode.get(i);

                            jecount = jecount + (sgsd.xsje - sgsd.thje);
                        }

                        line = ManipulatePrecision.doubleToString(jecount);

                        break;

                    case AGB_gzzk: //柜组折扣

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        double zkcount = 0;

                        for (int i = 0; i < arkGroupListMode.size(); i++)
                        {
                            sgsd = (SaleGzSummaryDef) arkGroupListMode.get(i);

                            zkcount = zkcount + (sgsd.xszk - sgsd.thzk);
                        }

                        line = ManipulatePrecision.doubleToString(zkcount);

                        break;

                    case AGB_gzbh: //柜组编号

                        if ((arkGroupListMode == null) || (arkGroupListMode.size() <= 0))
                        {
                            break;
                        }

                        sgsd = (SaleGzSummaryDef) arkGroupListMode.get(index);

                        line = String.valueOf(sgsd.gz);

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
        for (int i = 0; i < arkGroupListMode.size(); i++)
        {
            printVector(getCollectDataString(Detail, i, Width));
        }
    }
}
