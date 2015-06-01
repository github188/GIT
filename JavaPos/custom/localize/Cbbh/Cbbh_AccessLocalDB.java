package custom.localize.Cbbh;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Cbbh_AccessLocalDB extends Bcrm_AccessLocalDB
{

	public void paraInitDefault()
	{
		super.paraInitDefault();

        GlobalInfo.sysPara.iscrmtjprice = 'N';
		GlobalInfo.sysPara.isUseBankReadTrack = 'N';
		GlobalInfo.sysPara.noCustSendToPop = 'Y';
		GlobalInfo.sysPara.noCustFindPop = 'N';
		GlobalInfo.sysPara.dosPosSvrAddress = "";//130.130.26.100|9002|10000
		GlobalInfo.sysPara.dosPosSvrCmdList = "";//1,10,11,58,13,49,24,45/1,10,75,76,77,80,83,86,52
		GlobalInfo.sysPara.cbMzkSvrAddress = "";
		GlobalInfo.sysPara.cbXsjMzkSvrAddress = "";
		GlobalInfo.sysPara.bankPayList = "";
		GlobalInfo.sysPara.isUnityMzkSrv = 'N';	
		GlobalInfo.sysPara.noBackPaycodeList = "0405,0402,0400,0401,0508,42";
		GlobalInfo.sysPara.istcl='N';
		GlobalInfo.sysPara.isfp='N';
		GlobalInfo.sysPara.iser = 'N';
		GlobalInfo.sysPara.isbackpay = "";
		GlobalInfo.sysPara.issalepay = "";
		GlobalInfo.sysPara.isnewpop = "N";
		GlobalInfo.sysPara.isnewmktcode="N";
		GlobalInfo.sysPara.isprintback="Y";
		GlobalInfo.sysPara.sljd = '0';
	}

	public void paraConvertByCode(String code, String value)
	{
		try
		{
			super.paraConvertByCode(code, value);
			
			GlobalInfo.sysPara.refundByPos = 'N';
			GlobalInfo.sysPara.issaleby0 = 'Y';
			
			if (code.equals("ES") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.noCustSendToPop = value.trim().charAt(0);
				return;
			}
			if (code.equals("ET") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.noCustFindPop = value.trim().charAt(0);
				return;
			}	
			if (code.equals("IF") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isUseBankReadTrack = value.trim().charAt(0);
				return;
			}	
			if (code.equals("IH") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cbMzkSvrAddress = value.trim();
				return;
			}
			if (code.equals("II") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cbXsjMzkSvrAddress = value.trim();
				return;
			}
			if (code.equals("IJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.bankPayList = value.trim();
				return;
			}
			if (code.equals("IK") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isUnityMzkSrv = value.trim().charAt(0);
				return;
			}
			if (code.equals("IL") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.noBackPaycodeList = value.trim();
				return;
			}
			if (code.equals("IM") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.istcl = value.trim().charAt(0);
				return;
			}
			if (code.equals("IN") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isfp = value.trim().charAt(0);
				return;
			}
			if (code.equals("IO") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.iser = value.trim().charAt(0);
				return;
			}
			if (code.equals("IP") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isbackpay = value.trim();
				return;
			}
			if (code.equals("IQ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.issalepay  = value.trim();
				return;
			}
			if (code.equals("IR") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isnewpop  = value.trim();
				return;
			}
			if (code.equals("IS") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isnewmktcode  = value.trim();
				return;
			}
			if (code.equals("IT") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isprintback  = value.trim();
				return;
			}
			if (code.equals("IU") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.sljd = value.trim().charAt(0);
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
