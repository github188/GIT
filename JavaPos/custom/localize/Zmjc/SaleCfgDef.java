package custom.localize.Zmjc;

import java.io.Serializable;

public class SaleCfgDef implements Serializable
{

	private static final long serialVersionUID = 0L;
    public static String[] ref = { "scpara", "sctype", "scvalue", "scname", "scseq", "scisbt" };
    
    public String scpara;
    public char sctype;
    public String scvalue;
    public String scname;
    public double scseq;
    public char scisbt;
}
