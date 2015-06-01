package custom.localize.Bxmx;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Bxmx_SaleBS0PopOrData extends Bstd_SaleBS
{
	public String goodscouponcode;
	public double goodscouponye;

	protected Bxmx_FetchInfoDef fetchinfo;
	protected boolean isgoodscoupon;

	protected long srcfphm;
	protected String srcsyjh;
	protected String srcmkt;
	
	protected boolean isBatchSellCardOrCoupon;
	
	protected void initSaleData()
	{
		goodscouponcode = "";
		goodscouponye = 0;
		fetchinfo = null;
		isgoodscoupon = false;
		srcfphm = 0;
		srcsyjh = "";
		srcmkt = "";
		isBatchSellCardOrCoupon = false;
	}

	public void initNewSale()
	{
		initSaleData();
		super.initNewSale();
	}

	public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
	{
		super.writeSellObjectToStream(s);

		s.writeObject(goodscouponcode);
		s.writeObject(new Double(goodscouponye));
		s.writeObject(fetchinfo);
		s.writeObject(new Boolean(isgoodscoupon));
		s.writeObject(new Long(srcfphm));
		s.writeObject(srcsyjh);
		s.writeObject(srcmkt);
		s.writeObject(new Boolean(isBatchSellCardOrCoupon));
	}

	public void readStreamToSellObject(ObjectInputStream s) throws Exception
	{
		super.readStreamToSellObject(s);

		goodscouponcode = (String) s.readObject();
		goodscouponye = ((Double) s.readObject()).doubleValue();
		fetchinfo = (Bxmx_FetchInfoDef) s.readObject();
		isgoodscoupon = ((Boolean) s.readObject()).booleanValue();
		srcfphm = ((Long) s.readObject()).longValue();
		srcsyjh = (String) s.readObject();
		srcmkt = (String) s.readObject();
		isBatchSellCardOrCoupon = ((Boolean)s.readObject()).booleanValue();
	}

	public void findGoodsCMPOPInfo(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (SellType.ISCOUPON(saletype)||SellType.ISCARD(saletype))
		{
			goodsCmPop.add(null);
			return;
		}

		super.findGoodsCMPOPInfo(sg, goods, info);
	}
}
