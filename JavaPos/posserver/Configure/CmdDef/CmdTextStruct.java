package posserver.Configure.CmdDef;

import java.util.Vector;


public class CmdTextStruct 
{		
	public String Cmd_XX_Mode = "";
	public String Sql_XX_Type = "";
	public String Tran_XX_Sql = "";
	public Vector Tran_XX_Para = new Vector();
	public Vector Tran_XX_Col = new Vector();

	public CmdTextStruct Copy()
	{
		CmdTextStruct cts = new CmdTextStruct();
		cts.Cmd_XX_Mode = Cmd_XX_Mode;
		cts.Sql_XX_Type = Sql_XX_Type;
		cts.Tran_XX_Sql = Tran_XX_Sql;
		
		for (int i = 0 ;i < Tran_XX_Para.size();i++)
		{
			cts.Tran_XX_Para.add(((CmdParaStruct)Tran_XX_Para.get(i)).Copy());
		}
		
		for (int i = 0 ;i < Tran_XX_Col.size();i++)
		{
			cts.Tran_XX_Col.add(((CmdParaStruct)Tran_XX_Col.get(i)).Copy());
		}
		
		return cts;
	}
	
	/*
	public String Tran_XX_ParaName = "";
	public String Tran_XX_ParaType = "";
	public String Tran_XX_ColName = "";
	public String Tran_XX_ColType = "";
	*/
}
