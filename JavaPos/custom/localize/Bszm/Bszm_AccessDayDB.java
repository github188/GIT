package custom.localize.Bszm;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.AccessDayDB;


public class Bszm_AccessDayDB extends AccessDayDB
{
	public class ActiveCouponDef
	{
		public String mkt;
		public long fphm;
		public String couponno;
	}

	public boolean writeActivCoupon(long fphm, String list)
	{
		try
		{
			String[] row = null;

			if (!GlobalInfo.dayDB.beginTrans()) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.dayDB.getTableColumns("SALECOUPON");
			if (ref == null || ref.length <= 0)
				ref = new String[] { "mkt", "fphm", "couponno" };

			String line = CommonMethod.getInsertSql("SALECOUPON", ref);

			if (!GlobalInfo.dayDB.setSql(line)) { return false; }

			ActiveCouponDef coupon = new ActiveCouponDef();

			if (list == null || list.equals(""))
				return false;

			row = list.split(",");

			for (int i = 0; i < row.length; i++)
			{
				coupon = new ActiveCouponDef();
				coupon.mkt = GlobalInfo.sysPara.mktcode;
				coupon.fphm = fphm;
				coupon.couponno = row[i].trim();

				if (!GlobalInfo.dayDB.setObjectToParam(coupon, ref)) { return false; }

				if (!GlobalInfo.dayDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.dayDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

}
