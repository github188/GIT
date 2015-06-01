package custom.localize.Nbbh;

import java.io.Serializable;
import java.util.Date;

public class RulesDef implements Serializable {
	private static final long serialVersionUID = 1L;
	public static String[] ref = { "TRGCODE", "TRGNAME", "TRGMEMO",
			"TRGDETAIL", "TRGMKT", "TRGQZ", "TRGFONT1", "TRGBOLD1", "TRGFONT2",
			"TRGBOLD2", "TRGFONT3", "TRGBOLD3", "TRGBD", "TRGSIZE1",
			"TRGSIZE2", "TRGSIZE3", "TRGSTART", "TRGEND", "TRGMONEY" };
	public String TRGCODE;// 编码
	public String TRGNAME;// 券名称
	public String TRGMEMO;// 券使用范围
	public String TRGDETAIL;// 券使用说明
	public String TRGMKT;// 门店
	public String TRGQZ;// 券种
	public String TRGFONT1;// 字体
	public String TRGBOLD1;// 加粗
	public String TRGFONT2;// 字体
	public String TRGBOLD2;// 加粗
	public String TRGFONT3;// 字体
	public String TRGBOLD3;// 加粗
	public String TRGBD;// 必打
	public String TRGSIZE1;// 字号
	public String TRGSIZE2;// 字号
	public String TRGSIZE3;// 字号
	public String TRGSTART;// 开始时间
	public String TRGEND;// 结束时间
	public double TRGMONEY;// 起送条件金额

}
