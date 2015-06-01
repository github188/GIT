package com.efuture.javaPos.Logic;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.PrepareMoneyDef;


public class PreMoneyBS
{
    public Vector getTableInfo()
    {
        // 读取上次输入的备用金
        Vector lastPreMoneyDef = new Vector();
        AccessLocalDB.getDefault().readPreMoneyDef(lastPreMoneyDef);

        //
        Vector tableInfo = new Vector();
        String[] row = null;

        // 设置备用金初始输入值
        for (int i = 0; i < GlobalInfo.payMode.size(); i++)
        {
            PayModeDef mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

            if (mode.iszl == 'Y' && mode.ismj == 'Y' && mode.type == '1')
            {
                row    = new String[3];
                row[0] = "[" + mode.code + "]" + mode.name;

                for (int j = 0; j < lastPreMoneyDef.size(); j++)
                {
                    PrepareMoneyDef pre = (PrepareMoneyDef) lastPreMoneyDef.elementAt(j);

                    if (mode.code.equals(pre.paycode))
                    {
                        row[1] = String.valueOf(ManipulatePrecision.doubleConvert(pre.je,2,1));
                    }
                }

                if (row[1] == null)
                {
                    row[1] = "";
                }

                if (GlobalInfo.ModuleType.indexOf("ZM")!=0)
                {
                	row[2] = ManipulatePrecision.doubleToString(mode.hl, 4, 1);
                }
                else
                {
                	row[2] = ManipulatePrecision.doubleToString(mode.hl, 6, 1);
                }
                
                tableInfo.add(row);
            }
        }

        return tableInfo;
    }

    //保存备用金
    public boolean savePreMoney(Vector tableInfo)
    {
        Vector preMoney = new Vector();
        String[] prePay = null;

        for (int i = 0; i < tableInfo.size(); i++)
        {
            prePay = (String[]) tableInfo.elementAt(i);

            if ((prePay[1].length() > 0) &&
                    (Double.parseDouble(prePay[1]) != 0))
            {
                PrepareMoneyDef pre = new PrepareMoneyDef();
                pre.syjh    = ConfigClass.CashRegisterCode;
                pre.syyh    = GlobalInfo.posLogin.gh;
                pre.paycode = CommonMethod.cutSquareBracket(prePay[0]);
                pre.je = Double.parseDouble(prePay[1]);
                preMoney.add(pre);
            }
        }

        //
        if (preMoney.size()>0)
        {
        AccessLocalDB.getDefault().writePreMoneyDef(preMoney);
        return true;
        }
        else
        {
        	if (new MessageBox(Language.apply("您未输入备用金或金额为0\n是否继续"),null,true).verify()!=GlobalVar.Key1)
        		return false;
        	else
        		return true;
        }
    }
}
