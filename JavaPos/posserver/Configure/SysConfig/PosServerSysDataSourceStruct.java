package posserver.Configure.SysConfig;

import java.util.Vector;

public class PosServerSysDataSourceStruct 
{
	public String id = "";
	public String datasouremode = "";
	public String datasourename = "";
	public String datasoureurl = "";
	public Vector cmddefcmdcode = new Vector();
	public Vector fileupdatecmdcode = new Vector();
	public Vector createtablecmdcode = new Vector();
	public String inputencoder = "";
	public String outencoder = "";
}
