package posserver.Configure.SysConfig;

import java.util.Vector;

public class PosServerSysStruct
{   
	public String tasktime = "";
	public String inspecttime = "";
	public String dayenddef = "";
	public String autodayendtime = "";
	public String fileupdatedef = "";
	public String fileupdatestarttime = "";
	public String fileupdateendtime = "";
	public String localdbtype = "";
	public String maxWait = "";
	public String cashareadef = "";
	public String cashconnserver = "";
	
	public Vector datasource = new Vector();
	
	public String username = "";
	public String password = "";
	public String parameteraddress  = "";
	public Vector globalftppath = new Vector(); 
	public Vector globalparameter = new Vector(); 
}
