package custom.localize.Zmsy;

/**
 * @author sf
 *
 */
public class THDDef
{
	private static final long serialVersionUID = 0L;
	
	public static String[] ref = { "fphm", "syjh", "thdh", "net_bz" };
	
	/**
	 * 发票号码
	 */
	public long fphm;
	
	/**
	 * 收银机号
	 */
	public String syjh;
	
	/**
	 * 提货单号
	 */
	public String thdh;
	
	/**
	 * 上传成功标志
	 */
	public char net_bz;
}
