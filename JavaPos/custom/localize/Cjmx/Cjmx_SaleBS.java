package custom.localize.Cjmx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Cmls.Cmls_SaleBS;

public class Cjmx_SaleBS extends Cmls_SaleBS
{

	boolean cxRebate = false;

	public boolean paySellPop()
	{
		//doCmPopWriteData();
		if (calcCXRebate())
		{
			cxRebate = true;
		}
		return super.paySellPop();
	}

	class CxRebateDef
	{
		public String pmbillno;//促销单号
		public String addrule;//累计规则 'YYYYY' 
		public String Zklist;// 折扣列表 1:X|2:Y|3:Z 
		public String pmrule;//价随量变规则 
		public String etzkmode2;//1-阶梯折扣 2-统一打折 
		public String seq; //规则序号 
		public double zkfd;
		public String bz;//阶梯折扣时  Y/N是否循环   统一折扣时  Y/N是否启用上限数量 
		public double maxnum; //上限数量 

		public double zsl;

		public double sl_cond; // 满足数量条件
		public double zkl_result;//折扣率
		public double cursl; //计算阶梯折扣时，记录当前数量

		//供应商 柜组 品牌 类别 商品
		public String gys;
		public String gz;
		public String pp;
		public String catid;
		public String code;

		Vector list = new Vector();
	};

	public boolean calcCXRebate()
	{
		// 价随量变的促销

		Vector group = new Vector();
		// 先进行分组
		for (int i = 0; i < goodsSpare.size(); i++)
		{
			SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(i);
			if (sid.Zklist == null || sid.Zklist.length() <= 0 || sid.Zklist.equals("0"))
			{
				continue;
			}
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			int n = 0;
			for (; n < group.size(); n++)
			{
				CxRebateDef cx = (CxRebateDef) group.elementAt(n);
				String condition = null;
				if (sid.Zklist.indexOf("1:") == 0) condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				else condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				//？？ cx.seq.equals(sid.seq) 不相同
				if (cx.addrule.equals(sid.addrule) && cx.Zklist.equals(condition) && cx.pmbillno.equals(sid.pmbillno) && cx.bz.equals(sid.bz))
				{
					boolean cond = true;
					if (cx.addrule.length() > 0 && cx.addrule.charAt(0) == 'Y')
					{
						cond = cond && cx.gys.equals(goodsDef.str1);
					}

					if (cx.addrule.length() > 1 && cx.addrule.charAt(1) == 'Y')
					{
						cond = cond && cx.gz.equals(goodsDef.gz);
					}

					if (cx.addrule.length() > 2 && cx.addrule.charAt(2) == 'Y')
					{
						cond = cond && cx.pp.equals(goodsDef.ppcode);
					}

					if (cx.addrule.length() > 3 && cx.addrule.charAt(3) == 'Y')
					{
						cond = cond && cx.catid.equals(goodsDef.catid);
					}

					if (cx.addrule.length() > 4 && cx.addrule.charAt(4) == 'Y')
					{
						cond = cond && cx.code.equals(goodsDef.code);
					}

					if (cond)
					{
						cx.zsl = ManipulatePrecision.doubleConvert(cx.zsl + saleGoodsDef.sl);
						cx.list.add(String.valueOf(i));
						break;
					}
				}
			}

			if (n >= group.size())
			{
				CxRebateDef cx = new CxRebateDef();
				cx.pmbillno = sid.pmbillno;/*促销单号*/
				cx.addrule = sid.addrule;/*累计规则 'YYYYY'*/
				if (sid.Zklist.indexOf("1:") == 0) cx.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				else cx.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				cx.pmrule = sid.pmrule;/*价随量变规则*/
				cx.etzkmode2 = sid.etzkmode2;/*1-阶梯折扣 2-统一打折*/
				cx.seq = sid.seq; /*规则序号*/
				cx.zkfd = Convert.toDouble(sid.zkfd);
				cx.bz = sid.bz;
				cx.maxnum = sid.maxnum;

				cx.zsl = ManipulatePrecision.doubleConvert(saleGoodsDef.sl);
				cx.list.add(String.valueOf(i));

				cx.gys = goodsDef.str1;
				cx.gz = goodsDef.gz;
				cx.pp = goodsDef.ppcode;
				cx.catid = goodsDef.catid;
				cx.code = goodsDef.code;

				group.add(cx);
			}
		}

		if (group.size() <= 0) return false;
		//检查促销生效
		for (int i = 0; i < group.size(); i++)
		{
			CxRebateDef cx = (CxRebateDef) group.elementAt(i);

			//new MessageBox("开始检查促销生效信息"+cx.Zklist+" "+i);

			String zklist = cx.Zklist;
			String[] zk = zklist.split("\\|");
			int n = zk.length - 1;
			for (; n >= 0; n--)
			{
				String rule = zk[n];
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));
				if (cx.zsl < sl)
				{
					continue;
				}
				else
				{
					cx.sl_cond = sl;
					cx.zkl_result = zkl;
					break;
				}
			}

			if (n < 0)
			{
				group.removeElementAt(i);
				i--;
			}
		}

		boolean done = false;
		//开始计算促销
		for (int i = 0; i < group.size(); i++)
		{
			CxRebateDef cx = (CxRebateDef) group.elementAt(i);

			String zkcheck1 = cx.Zklist;
			String[] zkcheck2 = zkcheck1.split("\\|");
			if (zkcheck2[0] != null)
			{
				double sl1 = Convert.toDouble(zkcheck2[0].substring(0, zkcheck2[0].indexOf(":")));
				//new MessageBox(String.valueOf(sl1));
				if (sl1 > 1)
				{
					cx.Zklist = "1:1|" + cx.Zklist;
				}
				//new MessageBox(cx.Zklist);
			}

			for (int x = 0; x < cx.list.size(); x++)
			{
				int index = Convert.toInt(cx.list.elementAt(x));
				SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(index);
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				double yhzke = 0;
				//new MessageBox("计算促销 "+Convert.toInt(cx.etzkmode2)+" "+cx.bz);
				//  不循环且为阶梯折扣时
				if (Convert.toInt(cx.etzkmode2) == 1 && cx.bz.trim().equals("N"))
				{
					yhzke = caculateSL(cx, saleGoodsDef, saleGoodsDef.sl, cx.Zklist.split("\\|"), 0);
				}
				else if (Convert.toInt(cx.etzkmode2) == 1 && cx.bz.trim().equals("Y"))
				{
					yhzke = caculateSL1(cx, saleGoodsDef, saleGoodsDef.sl, cx.Zklist.split("\\|"), 0);
					//new MessageBox("done");
				}
				else if (Convert.toInt(cx.etzkmode2) == 2 && cx.bz.trim().equals("Y"))
				{
					if (cx.cursl + saleGoodsDef.sl > cx.maxnum)
					{
						double num1 = ManipulatePrecision.doubleConvert(cx.maxnum - cx.cursl);
						yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * (num1) * (1 - cx.zkl_result));
						cx.cursl = cx.maxnum;
					}
					else
					{
						yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1 - cx.zkl_result));
						cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + saleGoodsDef.sl);
					}
				}
				else
				{
					yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1 - cx.zkl_result));
				}

				if (yhzke >= 0)
				{

					saleGoodsDef.hyzke = 0;
					saleGoodsDef.zszke = 0;
					saleGoodsDef.lszke = 0;
					saleGoodsDef.lszre = 0;
					saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzr = 0;
					saleGoodsDef.yhzke = yhzke;
					saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
					getZZK(saleGoodsDef);
					saleGoodsDef.yhzkfd = cx.zkfd;
					saleGoodsDef.yhdjbh = cx.pmbillno;
					if (sid.str1 != null)
					{
						StringBuffer buf = new StringBuffer(sid.str1.trim());
						for (int z = 0; z < buf.length(); z++)
						{
							buf.setCharAt(z, '0');
						}
						sid.str1 = buf.toString();
						saleGoodsDef.isvipzk = 'N';
					}

					done = true;
				}
			}

		}

		if (done)
		{
			//重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			return true;
		}
		return false;

	}

	public double caculateSL(CxRebateDef cx, SaleGoodsDef sgd, double use_sl, String[] zk, int index)
	{
		if (index >= zk.length) return 0;

		if (index == 0)
		{
			int n = 0;
			for (; n < zk.length; n++)
			{
				String rule = zk[n];
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double sl1 = -1;
				if ((n + 1) < zk.length)
				{
					sl1 = Convert.toDouble(zk[n + 1].substring(0, zk[n + 1].indexOf(":")));
				}

				if (sl1 < 0)
				{

					double use_sl1 = ManipulatePrecision.doubleConvert(cx.cursl + use_sl - sl);
					if (use_sl1 <= 0)
					{
						continue;
					}
					use_sl = use_sl1;
					index = n;
					break;
				}
				else if (cx.cursl > sl && cx.cursl >= sl1)
				{

					continue;
				}
				else if (cx.cursl >= sl && cx.cursl < sl1)
				{
					if (sl1 - cx.cursl <= use_sl) index = n + 1;
					else index = n;
					break;
				}
				else if (ManipulatePrecision.doubleConvert(cx.cursl + use_sl) >= sl1 && ManipulatePrecision.doubleConvert(cx.cursl + use_sl) > sl)
				{
					index = n;
					break;

				}
				else if (ManipulatePrecision.doubleConvert(cx.cursl + use_sl) >= sl)
				{
					index = n;
					break;
				}
			}

			if (n >= zk.length)
			{
				cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + use_sl);
				return 0;
			}
		}

		String rule = zk[index];

		double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
		double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));

		boolean done_b = false;
		if ((index + 1) < zk.length)
		{
			double sl1 = Convert.toDouble(zk[index + 1].substring(0, zk[index + 1].indexOf(":")));
			if (cx.cursl >= sl1) done_b = true;
		}

		//证明有下一及
		if ((index + 1) < zk.length)
		{
			String rule1 = zk[index + 1];
			double sl1 = Convert.toDouble(rule1.substring(0, rule1.indexOf(":")));

			//本级需要计算的数量
			double x_sl = (cx.cursl + use_sl - sl1);
			if (x_sl < 0)
			{
				cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + use_sl);
				double zkje = ManipulatePrecision.doubleConvert(sgd.jg * (use_sl) * (1 - zkl));
				return zkje;
			}
			else
			{
				x_sl = ManipulatePrecision.doubleConvert(sl1 - sl);
			}
			double zkje = ManipulatePrecision.doubleConvert(sgd.jg * x_sl * (1 - zkl));

			//当前已经计算的数量
			cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + x_sl);
			//当前未计算的数量
			double y_sl = ManipulatePrecision.doubleConvert(use_sl - x_sl);
			return zkje + caculateSL(cx, sgd, y_sl, zk, (index + 1));
		}
		else
		//没有下一集，用本级计算
		{
			double zkje = ManipulatePrecision.doubleConvert(sgd.jg * use_sl * (1 - zkl));
			return zkje;
		}
	}

	//	阶梯且循环
	public double caculateSL1(CxRebateDef cx, SaleGoodsDef sgd, double use_sl, String[] zk, int index)
	{
		double sumzk = 0;
		for (int i = 0; i < use_sl; i++)
		{
			cx.cursl++;

			int n = zk.length - 1;
			for (; n >= 0; n--)
			{
				String rule = zk[n];
				//new MessageBox(rule);
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));

				//代表为最后一级，需要循环
				if (cx.cursl >= sl && n == (zk.length - 1))
				{
					cx.cursl = 0;
					sumzk = ManipulatePrecision.doubleConvert(sumzk + sgd.jg * (1 - zkl));
					break;
				}

				if (cx.cursl >= sl)
				{
					sumzk = ManipulatePrecision.doubleConvert(sumzk + sgd.jg * (1 - zkl));
					break;
				}

			}
		}

		return sumzk;
	}

	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		super.findGoodsCRMPop(sg, goods, info);

		String cardno = null;
		String cardtype = null;
		String isfjk = "";
		String grouplist = "";

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2) isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}

		if (!GlobalInfo.isOnline) { return; }
		if (GlobalInfo.sysPara.isGroupJSLB != 'N' || !SellType.isGroupbuy(this.saletype))
		{
			//查询商品价随量变信息
			((Cjmx_NetService) NetService.getDefault()).findBatchRule(info, sg.code, sg.gz, sg.uid, goods.str1, sg.catid, sg.ppcode, saleHead.rqsj,
																		cardno, cardtype, isfjk, grouplist, saletype, GlobalInfo.localHttp);
			if (info.Zklist != null && info.Zklist.trim().length() > 1) sg.name = "B" + sg.name;
		}
	}

	public void execCustomKey0(boolean keydownonsale)
	{
		if (!saleEvent.saleform.getFocus().equals(saleEvent.code)) { return; }
		//execCustomKey(keydownonsale, GlobalVar.CustomKey0);
		TextBox txt = new TextBox();
		StringBuffer buffer = new StringBuffer();
		if (!txt.open("请扫商品条码", "商品条码", "", buffer, 0, 0, false, TextBox.MsrInput)) 
		{ 
			saleEvent.saleform.setFocus(saleEvent.code);
			return; 
		}
		saleEvent.code.setText(String.valueOf(txt.Track2));
		saleEvent.saleform.setFocus(saleEvent.code);
		enterInput();
	}
}
