package posserver.Configure.CmdDef;


public class CmdParaStruct 
{		
	public String Name = "";
	public String Type = "";
	public String TypeDesc = "";
	
	public CmdParaStruct Copy()
	{
		CmdParaStruct cps = new CmdParaStruct();
		cps.Name = Name;
		cps.Type = Type;
		cps.TypeDesc = TypeDesc;
		return cps;
	}
}
