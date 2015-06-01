package posserver.Configure.CmdDef;

import java.util.Vector;

import posserver.Configure.Common.KeyValueStruct;

public class ProcStruct
{
	public String ProcName = "";
	public String PackAge = "";
	public boolean IsSql = false;
	public Vector ProcPara = new Vector();
	
	public ProcStruct Clone()
	{
		ProcStruct ps = new ProcStruct();
		
		ps.ProcName = this.ProcName;
		ps.PackAge = this.PackAge;
		ps.IsSql = this.IsSql;
		
		for (int i = 0;i < ProcPara.size();i++)
		{
			ProcParaStruct pps = (ProcParaStruct)ProcPara.get(i);
			
			ProcParaStruct pps1 = new ProcParaStruct();
			
			if (pps.CfgDataType != null)
			{
				pps1.CfgDataType = new KeyValueStruct();
				pps1.CfgDataType.key = pps.CfgDataType.key;
				pps1.CfgDataType.value = pps.CfgDataType.value;
			}
			
			pps1.CfgInOutType = pps.CfgInOutType;
			pps1.DataType = pps.DataType;
			pps1.DataTypeName = pps.DataTypeName;
			pps1.InOutType = pps.InOutType;
			pps1.Name = pps.Name;
			ps.ProcPara.add(pps1);
		}
		
		return ps;
	}
}
