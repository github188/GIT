package custom.localize.Gwzx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bcrm.Bcrm_SaleBS;

public class Gwzx_SaleBS extends Bcrm_SaleBS
{
	public boolean isSamePop(SaleGoodsDef salegoods1, GoodsDef goods1, GoodsPopDef popDef1, String mjrule1, SaleGoodsDef salegoods2, GoodsDef goods2, GoodsPopDef popDef2, String mjrule2)
	{
		if ((popDef1.memo.indexOf(",") == 0) || (popDef2.memo.indexOf(",") == 0)) { return false; }

		// 截取出满减规则
		String mjdh1 = popDef1.memo.substring(0, popDef1.memo.indexOf(","));
		String mjdh2 = popDef2.memo.substring(0, popDef2.memo.indexOf(","));
		
		// 如果满减规则单据编号不同，一定不是同规则
		if (!mjdh1.equalsIgnoreCase(mjdh2)) return false;
		
		if (Convert.toInt(popDef1.ppcode) == 1) // 按品牌分组
		{
			if (salegoods1.ppcode.equals(salegoods2.ppcode)) return true;
			else return false;
		}
		
		if (Convert.toInt(popDef1.ppcode) == 2) // 按品牌+柜组分组
		{
			if (salegoods1.ppcode.equals(salegoods2.ppcode) && salegoods1.gz.equals(salegoods2.gz)) return true;
			else return false;
		}
		
		if (Convert.toInt(popDef1.ppcode) == 3) // 按商品
		{
			if (salegoods1.code.equals(salegoods2.code)) return true;
			else return false;
		}
		
		if (Convert.toInt(popDef1.ppcode) == 4) // 按商品+柜组
		{
			if (salegoods1.code.equals(salegoods2.code) && salegoods1.gz.equals(salegoods2.gz)) return true;
			else return false;
		}
		
		
		// 选的是同一个满减规则，且允许跨柜则认为是同一个规则，要进行合计，其实就是按柜组分组
		if (mjdh1.equalsIgnoreCase(mjdh2) && 
			((popDef1.ppcode.equalsIgnoreCase("Y") && popDef2.ppcode.equalsIgnoreCase("Y")) || ((!popDef1.ppcode.equalsIgnoreCase("Y") || !popDef2.ppcode.equalsIgnoreCase("Y")) && salegoods1.gz.equalsIgnoreCase(salegoods2.gz))))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String cardno = null;
		String cardtype = null;
		String isfjk = "";
		String grouplist = "";
		String newyhsp = "90000000";

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2) isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Gwzx_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype,isfjk,grouplist,saletype);
		
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}
		
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == Convert.toInt(newyhsp) || hhflag == 'Y')
		{
			popDef.yhspace = Convert.toInt(newyhsp);
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}

		//将收券规则放入GOODSDEF 列表
		goods.memo = popDef.str2;
		goods.num1 = popDef.num1;
		goods.num2 = popDef.num2;
		goods.str4 = popDef.mode;
		info.char3 = popDef.type;

		// 促销联比例
		sg.xxtax = Convert.toDouble(popDef.ksrq); // 促销联比例
		goods.xxtax = Convert.toDouble(popDef.ksrq);
		if (goods.memo == null) goods.memo = "";

		// 增加CRM促销信息
		crmPop.add(popDef);

		// 标志是否为9开头扩展的控制
		boolean append = false;
		// 无促销,此会员不允许促销
		if (popDef.yhspace == 0)
		{
			append = false;
			info.str1 = "0000";
		}
		else if (popDef.yhspace == Integer.parseInt(newyhsp))
		{
			append = true;
			info.str1 = newyhsp;
		}
		else
		{
			
			if (String.valueOf(popDef.yhspace).charAt(0) != '9')
			{
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
				else info.str1 = Convert.increaseInt(popDef.yhspace, 4);
				
				append = false;
			}
			else 
			{
				info.str1 = String.valueOf(popDef.yhspace);
				
				append = true;
			}
			//询问参加活动类型 满减或者满增
			String yh = info.str1;
			
			if (append) yh = yh.substring(1);
			
			StringBuffer buff = new StringBuffer(yh);
			Vector contents = new Vector();

			for (int i = 0; i < buff.length(); i++)
			{
				// 2-任选促销/1-存在促销/0-无促销
				if (buff.charAt(i) == '2')
				{
					if (i == 0)
					{
						contents.add(new String[] { "D", "参与打折促销活动", "0" });
					}
					else if (i == 1)
					{
						contents.add(new String[] { "J", "参与减现促销活动", "1" });
					}
					else if (i == 2)
					{
						contents.add(new String[] { "Q", "参与返券促销活动", "2" });
					}
					else if (i == 3)
					{
						contents.add(new String[] { "Z", "参与赠品促销活动", "3" });
					}
					else if (i == 5)
					{
						contents.add(new String[] { "F", "参与积分活动", "5" });
					}
				}
			}

			if (contents.size() <= 1)
			{
				if (contents.size() > 0)
				{
					String[] row = (String[]) contents.elementAt(0);
					int i = Integer.parseInt(row[2]);
					buff.setCharAt(i, '1');
				}
			}
			else
			{
				String[] title = { "代码", "描述" };
				int[] width = { 60, 400 };
				int choice = new MutiSelectForm().open("请选择参与满减满赠活动的规则", title, width, contents);

				for (int i = 0; i < contents.size(); i++)
				{
					if (i != choice)
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '0');
					}
					else
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '1');
					}
				}
			}

			if (append) info.str1 = "9"+buff.toString();
			else info.str1 = buff.toString();
		}

		String line = "";
		
		String yh = info.str1;
		if (append) yh = info.str1.substring(1);
		

		if (yh.charAt(0) != '0')
		{
			line += "D";
		}

		if (yh.charAt(1) != '0')
		{
			line += "J";
		}

		if (yh.charAt(2) != '0')
		{
			line += "Q";
		}

		if (yh.charAt(3) != '0')
		{
			line += "Z";
		}
		
		if (yh.length() > 5 && yh.charAt(5) != '0')
		{
			line += "F";
		}

		if (line.length() > 0)
		{
			sg.name = "(" + line + ")" + sg.name;
		}

		if (!append)
		{
			// str3记录促销组合码
			if (GlobalInfo.sysPara.iscrmtjprice == 'Y') sg.str3 = info.str1 + String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
			else sg.str3 = info.str1;
		}
		else
		{
			sg.str3 = info.str1;
		}
		// 将商品属性码,促销规则加入SaleGoodsDef里
		sg.str3 += (";" + goods.specinfo);
		sg.str3 += (";" + popDef.memo);
		sg.str3 += (";" + popDef.poppfjzkl);
		sg.str3 += (";" + popDef.poppfjzkfd);
		sg.str3 += (";" + popDef.poppfj);

		// 只有找到了规则促销单，就记录到小票
		if (!info.str1.equals("0000") || !info.str1.equals(newyhsp))
		{
			sg.zsdjbh = popDef.djbh;
			sg.zszkfd = popDef.poplsjzkfd;
		}
	}
	
	public void initSellData()
	{
		super.initSellData();
		// 收银机组
		saleHead.str1 = GlobalInfo.syjDef.priv;
	}
}
