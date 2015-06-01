package custom.localize.Zmsy;

/**
 * 证件类型定义
 * @author yw
 *
 */
public class ZJTypeDef
{

	private static final long serialVersionUID = 0L;
	
	public static String[] ref = { "zjid", "zjname", "str1", "str2", "num1", "num2" };
	
	/**
	 * 证件ID
	 */
	public String zjid;
	
	/**
	 * 证件名称
	 */
	public String zjname;
	
	public String str1;//备用
	public String str2;//备用
	public double num1;//备用
	public double num2;//备用
}
