package custom.localize.Zmjc;

import java.io.Serializable;

/**
 * @author sf
 *
 */
public class NationalityDef implements Serializable
{

	private static final long serialVersionUID = 0L;
	public static String[] ref = { "PCRCODE", "PCRCNAME", "PCRENAME" };
    
//	public String nseqno;	//序列号
//	public String nrowno;//行号
	public String PCRCODE;//编码
	public String PCRCNAME;//国籍中文
	public String PCRENAME;//国籍英文
    
}