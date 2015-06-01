package custom.localize.Wjyt;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.ElectronicScale;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import java.util.*;

import custom.localize.Bstd.Bstd_SaleBS;

public class Wjyt_SaleBS extends Bstd_SaleBS
{
	public boolean doShowInfoFinish()
	{
		try
		{
			MessageBox me = null;

			if (!SellType.ISCHECKINPUT(this.saletype))
			{
				int num = saleEvent.table.getSelectionIndex();
				if (num >= 0)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(num);
					// 2代表电子称称重商品
					if (sgd.flag == '2')
					{
						if (GlobalInfo.sysPara.elcScaleMode == '2')
						{
							boolean isOK = false;

							/*
							 * if
							 * (!ElectronicScale.getDefault().setPrice(sgd.jg))
							 * { me = new MessageBox("价格发送失败\n按[确认键]删除当前行商品");
							 * 
							 * if (me.verify() == GlobalVar.Validation)
							 * saleEvent.deleteCurrentGoods();
							 * saleEvent.code.selectAll(); return false; }
							 * 
							 * // 非轮询 if (GlobalInfo.sysPara.elcScaleMode ==
							 * '1') { me = new MessageBox("请将[" + sgd.name +
							 * "]商品放在称台上\n按[确认键]获取重量后请将商品拿离", null, false,
							 * GlobalVar.Validation);
							 * 
							 * if (me.verify() == GlobalVar.Validation) { if
							 * (ElectronicScale.getDefault().run()) { sgd.sl =
							 * ElectronicScale.getDefault().getWeight();
							 * sgd.hjje =
							 * ManipulatePrecision.doubleConvert(sgd.sl *
							 * sgd.jg, 2, 1); isOK = true; } } }
							 */

							// 轮询

							Vector data = new Vector(2);
							data.add(new Double(0.0));
							data.add(new Double(0.0));
							new ElecScaleRealtimeUpdateForm(sgd.name, sgd.jg, data).open();

							if (data != null && data.size() > 1)
							{
								sgd.sl = ((Double) data.elementAt(0)).doubleValue();
								sgd.hjje = ((Double) data.elementAt(1)).doubleValue();
								isOK = true;
							}

							if (!isOK)
							{
								me = new MessageBox("获取重量或价格失败\n按[确认键]删除当前商品");

								if (me.verify() == GlobalVar.Validation)
									saleEvent.deleteCurrentGoods();

								saleEvent.code.selectAll();
								return false;
							}

							// 获取重量和价格后重新计算应收
							calcGoodsYsje(num);
							getZZK(sgd);
							calcHeadYsje();

							getSaleGoodsDisplay();
							// 显示合计
							saleEvent.setTotalInfo();
							// 显示商品大字信息
							saleEvent.setCurGoodsBigInfo();

						}
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public void execCustomKey0(boolean keydownonsale)
	{
		if ((GlobalInfo.sysPara.elcScaleMode == '1' || GlobalInfo.sysPara.elcScaleMode == '2'))
		{
			if (!ElectronicScale.getDefault().setIgnorePeer())
				new MessageBox("去皮操作失败，请重试");
		}
	}
}
