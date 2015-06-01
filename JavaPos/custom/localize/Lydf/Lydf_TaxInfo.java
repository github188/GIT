package custom.localize.Lydf;

import java.io.Serializable;

public class Lydf_TaxInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	public String saletype;
	public String oprtype;
	public String Ret; // 返回值
	public String RQ; // 日期
	public String JE;// 合计税额金额
	public String SE; // 合计税额
	public String PH;// 电子票号
	public String SK;// 税控吗
	public String BZ; // 备注
	public String LSH;//
	public String memo;

	public Lydf_TaxInfo()
	{
		saletype = "";
		oprtype = "";
		Ret = ""; // 返回值
		RQ = ""; // 日期
		JE = "0.00";// 合计税额金额
		SE = "0.00"; // 合计税额
		PH = "";// 电子票号
		SK = "";// 税控吗
		BZ = ""; // 备注
		LSH = "";//
		memo = "";
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(SK);
		sb.append("#");
		sb.append(RQ);
		sb.append("#");
		sb.append(LSH);
		sb.append("#");
		sb.append(JE);
		sb.append("#");
		sb.append(SE);
		sb.append("#");
		sb.append(BZ);

		return sb.toString();
	}
}
