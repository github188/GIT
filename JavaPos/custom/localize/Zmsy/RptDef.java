package custom.localize.Zmsy;

/**
 * 报表定义类
 *
 */
public class RptDef
{
	private static final long serialVersionUID = 0L;
	
	public static String[] ref = { "goodscode", "goodsname", "kcsl", "gzcode", "ppcode", "gzname", "ppname" };
	
	/**
	 * 商品编码
	 */
	public String goodscode;
	
	/**
	 * 商品名称
	 */
	public String goodsname;
	
	/**
	 * 提货单号
	 */
	public double kcsl;
	
	/**
	 * 柜组
	 */
	public String gzcode;
	
	/**
	 * 柜组名称
	 */
	public String gzname;
	
	/**
	 * 品牌
	 */
	public String ppcode;
	
	/**
	 * 品牌名称
	 */
	public String ppname;
}
