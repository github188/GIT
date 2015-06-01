package custom.localize.Jwyt;

import com.efuture.javaPos.Global.GlobalInfo;

public class Jwyt_EwmGiftDef
{
	public static class EwmGiftHead
	{
		public static String[] ref = { "mktcode", "syjh", "syyh", "transseq", "editor", "couponname", "couponid", "markingid" };

		public String mktcode;// In Nvarchar2, -- 门店号
		public String syjh;// In Nvarchar2, -- 收银机号
		public String syyh;// IN VARCHAR2, -- 收银员号
		public String transseq;// IN VARCHAR2, -- 相关单据id 请在这里写入二维码返回表的 tansSeq 字段
		public String editor;// IN VARCHAR2, -- 编辑者 收银员号
		public String couponname; // IN VARCHAR2 , -- "二维码接口返回的凭证名称,券的名称"
		public String couponid; // IN VARCHAR2 , --返回的凭证ID
		public String markingid; // IN INTEGER, --返回的营销编号

		public EwmGiftHead()
		{
			mktcode = GlobalInfo.sysPara.mktcode;
			syjh = GlobalInfo.syjStatus.syjh;
			syyh = GlobalInfo.syjStatus.syyh;
			editor = GlobalInfo.syjStatus.syyh;
		}
	}

	public static class EwmGiftDetail
	{
		public static String[] ref = { "mktcode","serial" ,"goodscode","goodsnum", "qty", "price", "cost" };

		// public String sheetid;// in VARCHAR2 , -- 单号，由JAVA_SENDDEPARTUSE 返回。
		public String mktcode; // IN VARCHAR2,
		public String serial;// in INTEGER , -- 序号，显示用
		public String goodscode;// in INTEGER , -- 商品编码
		public String goodsnum;// in INTEGER , -- 包装单品数，即每包都多少单品
		public String qty;// in NUMBER , -- 领用数量
		public String price;//
		public String cost;// ,--领用价，从JAVA_FINDDEPARTUSEGOODS获得cost

		public EwmGiftDetail()
		{
			mktcode = GlobalInfo.sysPara.mktcode;
		}
	}
}
