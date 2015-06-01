package custom.localize.Cctd;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import custom.localize.Bstd.Bstd_NetService;

public class Cctd_NetService extends Bstd_NetService
{
	public int sendExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		//由于百货和超市表结构不同，str3清空
		for (int i=0;i< saleGoods.size();i++)
		((SaleGoodsDef)saleGoods.elementAt(i)).str3 = "";
		
		for (int i=0;i< salePayment.size();i++)
		{
			if ((((SalePayDef)salePayment.elementAt(i)).paycode.equals("0508")||((SalePayDef)salePayment.elementAt(i)).paycode.equals("0508")) && ((SalePayDef)salePayment.elementAt(i)).idno.indexOf("AAA")> 0)
			{
				((SalePayDef)salePayment.elementAt(i)).idno = "AAA,"+((SalePayDef)salePayment.elementAt(i)).idno;
			}
		}
			
		
		return sendSaleData(saleHead, saleGoods, salePayment, retValue, getMemCardHttp(CmdDef.SENDCRMSELL), CmdDef.SENDCRMSELL);
	}
	
	/**
	<configure CmdCode="131">
	<CmdType>HttpCmd</CmdType>
	<CmdMemo>JFXF</CmdMemo>
	<CmdTran>1</CmdTran>
	<StartTrans>true</StartTrans>
	<Cmd_01_Mode>MemoryCourse</Cmd_01_Mode>
	<Sql_01_Type>*</Sql_01_Type>
		<Tran_01_Sql>{call java_findruleshop(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,)}</Tran_01_Sql>
	<Tran_01_ParaName>mktcode,code,gz,uid,rule,catid,ppcode,rqsj,cardno,cardtype</Tran_01_ParaName>
  	<Tran_01_ParaType>s,s,s,s,s,s,s,s,s,s</Tran_01_ParaType>
	<Tran_01_ColName>recode,remsg,isjf,couponrule,memo,str1,str2,str3,num1,num2</Tran_01_ColName>
    <Tran_01_ColType>i,s,c,s,s,s,s,s,f,f</Tran_01_ColType>
	</configure>*/
	
	public boolean getGoodsIsJFXF(SaleHeadDef salehead,GoodsDef goods, SpareInfoDef info, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
        String[] values = 
        {
            GlobalInfo.sysPara.mktcode,GlobalInfo.sysPara.jygs ,goods.code, goods.gz, goods.uid,
            "0", goods.catid, goods.ppcode,salehead.rqsj, "", ""
        };
String[] args = 
      {
          "mktcode","jygs", "code", "gz", "uid", "rule", "catid",
          "ppcode", "rqsj", "cardno", "cardtype"
      };
		try
		{
			head = new CmdHead(555);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(http, line, "查询本商品积分消费规则失败");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] {"isjf","couponrule","memo","str1","str2","str3","num1","num2"});

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					info.char3 = (row[0].charAt(0));
					goods.str4 = row[1];
					return true;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	public boolean javaGetCustXF(String[] row, String cardno, String syjh, String string)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { cardno};
		String[] args = { "cardno" };

		try
		{
			head = new CmdHead(CmdDef.GETCUSTXF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.GETCUSTXF), line, "查询会员信息\n请到会员中心查询积分!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "yearxf", "monthxf", "str1", "str2", "str3", "str4", "num1", "num2", "num3", "num4" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					row[0] = row1[0];
					row[1] = row1[1];
					row[2] = row1[2];
					row[3] = row1[3];
					row[4] = row1[4];
					row[5] = row1[5];
					row[6] = row1[6];
					row[7] = row1[7];
					row[8] = row1[8];
					row[9] = row1[9];
					
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(ex.getMessage());
			PosLog.getLog(getClass()).error(ex);
			return false;
		}
	}
}
