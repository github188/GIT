package custom.localize.Nbbh;

import java.io.Serializable;

//小票重打原因
public class ReprintDef implements Cloneable,Serializable
{
	
	public static String[] ref = {"IWID","IWMEMO","IWSTATUS","IWSORT"};
	
	public int IWID;				
									
	public String IWMEMO;				

	public String IWSTATUS;				

	public int IWSORT;				
									
}
