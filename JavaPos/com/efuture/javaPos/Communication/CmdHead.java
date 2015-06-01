package com.efuture.javaPos.Communication;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;


//这个类是用来,管理包头
public class CmdHead
{
    private String ConfirmID = null; //效验
    private String CashierID = null; //收银机号
    private String cmdCode = null; //传输指令代码
    private String Pri = null; //优先级
    private String BackCode = null; //返回代码
    private String errorMessage = null; //返回错误信息

    public CmdHead(String cID, String cashID, String cmdCode, String pri,
                   String backCode, String errorMess)
    {
        this.ConfirmID    = ManipulatePrecision.getFilterNumberNoStr(cID);
        this.CashierID    = cashID;
        this.cmdCode      = cmdCode;
        this.Pri          = pri;
        this.BackCode     = backCode;
        this.errorMessage = errorMess;
    }

    public CmdHead(int cmdCode)
	{
    	 this.ConfirmID    = "2222";
    	
    	 if (ConfigClass.Market != null && !ConfigClass.Market.equals(""))
    	 {
    		 this.ConfirmID = ManipulatePrecision.getFilterNumberNoStr(ConfigClass.Market);
    	 }
    
		 this.CashierID    = ConfigClass.CashRegisterCode;
		 this.cmdCode      = String.valueOf(cmdCode);
		 this.Pri          = "01";
		 this.BackCode     = " ";
		 this.errorMessage = " ";
	}
    
    public CmdHead(String[] args)
    {
        this.ConfirmID    = ManipulatePrecision.getFilterNumberNoStr(args[0]);
        this.CashierID    = args[1];
        this.cmdCode      = args[2];
        this.Pri          = args[3];
        this.BackCode     = args[4];
        this.errorMessage = args[5];
    }

    public String headToString()
    {
        return ConfirmID + GlobalVar.divisionFlag2 + CashierID +
               GlobalVar.divisionFlag2 + cmdCode + GlobalVar.divisionFlag2 +
               Pri + GlobalVar.divisionFlag2 + BackCode +
               GlobalVar.divisionFlag2 + errorMessage +
               GlobalVar.divisionFlag1;
    }

    public String getCashierID()
    {
        return CashierID;
    }

    public void setCashierID(String cashierID)
    {
        CashierID = cashierID;
    }

    public String getCmdCode()
    {
        return cmdCode;
    }

    public void setCmdCode(String cmdCode)
    {
        this.cmdCode = cmdCode;
    }

    public String getConfirmID()
    {
        return ConfirmID;
    }

    public void setConfirmID(String confirmID)
    {
        ConfirmID = confirmID;
    }

    public String getPri()
    {
        return Pri;
    }

    public void setPri(String pri)
    {
        Pri = pri;
    }

    public String getBackCode()
    {
        return BackCode;
    }

    public void setBackCode(String backCode)
    {
        BackCode = backCode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
