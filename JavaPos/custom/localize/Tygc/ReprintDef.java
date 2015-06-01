package custom.localize.Tygc;

import java.io.Serializable;

// 德基小票重打原因
public class ReprintDef implements Cloneable,Serializable
{
	
	public static String[] ref = {"IWID","IWMEMO","IWSTATUS","IWSORT"};
	
	public int IWID;				
									
	public String IWMEMO;				

	public String IWSTATUS;				

	public int IWSORT;				
									
}
