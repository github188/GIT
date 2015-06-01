package custom.localize.Bszm;

import java.util.Vector;

import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_DataService;

public class Bszm_SaleBS1Cmpop extends Bszm_SaleBS0Form
{
	protected Vector popinfo = new Vector();
	
	public void findGoodsCMPOPInfo(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		Vector popitem = null;

		if (!SellType.ISSALE(this.saletype))
			return;

		// 会员状态
		String cardno = null;
		String cardtype = null;
		if (curCustomer != null)
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
		}

		// 查找商品的促销结果集
		Vector popvec = ((Bstd_DataService) DataService.getDefault()).findCMPOPGoods(saleHead.rqsj, goods, cardno, cardtype);
		goodsCmPop.add(popvec);

		// 按活动档期分组
		if (popvec != null)
		{
			// 只保留同档期类别最后一个档期,从后往前倒序搜索
			for (int i = popvec.size() - 1; i >= 0; i--)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);

				// 忽略的档期规则
				if (cmp.popmode == 'C' || cmp.ruleinfo.popmode == 'C')
				{
					popvec.remove(i);
					i = popvec.size();
					continue;
				}

				// 从后往前仍次查找匹配的dqtype，若找到相同的dqtype，则将找到的删除，保证同一种类型的档期只有一条
				if (i - 1 >= 0 && !((CmPopGoodsDef) popvec.elementAt(i - 1)).dqid.equals(cmp.dqid))
				{
					// 找到集合中前面同类别的档期
					int j = i - 1;
					for (; j >= 0; j--)
					{
						CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(j);
						if (cmp.dqinfo.dqtype != null && !cmp.dqinfo.dqtype.trim().equals("") && cmp.dqinfo.dqtype.equals(cmp1.dqinfo.dqtype))
						{
							popvec.remove(j);
							break;
						}
					}

					// 重新找需要选择的规则
					// 若popvect没有与最后一个元素相同的cmp时，
					// 此时j=-1,接着会以popvect倒数第二个元素为准，接着查找与第二个元素相同类型的档期
					if (j >= 0)
					{
						// 重新设i值
						i = popvec.size();
						continue;
					}
				}
			}

			// 选择规则或去掉需要放弃的规则
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);

				// 同档期活动存在多种规则始终只保留一个规则
				if (i + 1 < popvec.size() && ((CmPopGoodsDef) popvec.elementAt(i + 1)).dqid.equals(cmp.dqid))
				{
					// 手工选择一个规则
					if (cmp.dqinfo.ruleselmode == '0')
					{
						Vector contents = new Vector();
						for (int j = i; j < popvec.size(); j++)
						{
							CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(j);
							if (!cmp1.dqid.equals(cmp.dqid))
								break;
							contents.add(new String[] { cmp1.ruleid, cmp1.ruleinfo.rulename });
						}
						if (contents.size() <= 1)
							continue;
						String[] title = { "规则代码", "规则描述" };
						int[] width = { 100, 400 };
						int choice = -1;
						do
						{
							choice = new MutiSelectForm().open("请选择该商品参与[" + cmp.dqinfo.name + "]活动的促销形式", title, width, contents);
						} while (choice == -1);

						// 删除未选择的规则
						String choicerule = ((String[]) contents.elementAt(choice))[0];
						for (int j = i; j < popvec.size(); j++)
						{
							CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(j);
							if (!cmp1.dqid.equals(cmp.dqid))
								break;
							if (!cmp1.ruleid.equals(choicerule))
							{
								popvec.remove(j);
								j--;
							}
						}

						// 重新找需要选择的规则
						i = -1;
						continue;
					}
					else if (cmp.dqinfo.ruleselmode == '1') // 只参加最后一个规则
					{
						// 删除同档期前面的规则,保留最后一个规则
						for (int j = i; j < popvec.size(); j++)
						{
							CmPopGoodsDef cmp1 = (CmPopGoodsDef) popvec.elementAt(j);
							if (!cmp1.dqid.equals(cmp.dqid))
								break;
							if (j + 1 < popvec.size() && ((CmPopGoodsDef) popvec.elementAt(j + 1)).dqid.equals(cmp1.dqid))
							{
								popvec.remove(j);
								j--;
							}
						}

						// 重新找需要选择的规则
						i = -1;
						continue;
					}
					else if (cmp.dqinfo.ruleselmode == '3') // 3-按分组规则选择
					{
					}
					else if (cmp.dqinfo.ruleselmode == '4') // 4-手工选择多个规则
					{
					}
				}
			}

			// 从集合中去掉设置为取消促销的规则
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);
				if (cmp.popmode == 'N' || cmp.ruleinfo.popmode == 'N')
				{
					popvec.remove(i);
					i--;
				}
			}

			// 按规则的优先级倒序排，优先级大的排前面先执行
			if (popvec.size() > 1)
			{
				boolean sort = false;
				Vector newpopvec = new Vector();
				for (int i = 0; i < popvec.size(); i++)
				{
					CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);

					// 找到比自己优先级低的规则，并把自己插到该规则前
					int j = 0;
					for (; j < newpopvec.size(); j++)
					{
						CmPopGoodsDef cmp1 = (CmPopGoodsDef) newpopvec.elementAt(j);
						if (cmp.ruleinfo.pri > cmp1.ruleinfo.pri)
							break;
					}
					if (j >= newpopvec.size())
						newpopvec.add(cmp);
					else
					{
						newpopvec.insertElementAt(cmp, j);
						sort = true;
					}
				}
				if (sort)
					goodsCmPop.setElementAt(newpopvec, goodsCmPop.size() - 1);
			}
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(i);
				popitem = new Vector();
				
				String[] infoPop = new String[10];

				infoPop[0] = String.valueOf(cmp.cmpopseqno);
				infoPop[1] = cmp.dqinfo.dqid;
				infoPop[2] = cmp.dqinfo.name;
				infoPop[3] = cmp.ruleinfo.ruleid;
				infoPop[4] = cmp.ruleinfo.rulename;
				infoPop[5] = cmp.codeid;
				infoPop[6] = convertPoptype(cmp.codemode);
				infoPop[7] = converCondition(cmp.codemode,cmp.condsl,cmp.condje);
				infoPop[8] = cmp.ksrq + " " + cmp.kssj;
				infoPop[9] = cmp.jsrq + " " + cmp.jssj;

				popitem.add(infoPop);
			}

			popinfo.add(popitem);
		}
 

	}
	
	private String converCondition(char condmode,double consl,double conje)
	{
		String retStr = "";
		//(0-无达到条件/1-达到数量条件/2-达到金额条件/3-同时达到数量金额条件)
		switch(condmode)
		{
			case '0' :
				retStr = "无条件";
				break;
			case '1' :
				retStr = "数量["+ consl + "]";
				break;
				
			case '2' :
				retStr = "金额[" + conje +"]";
				break;
				
			case '3' :
				retStr = "数量["+ consl + "] " + "金额[" + conje +"]";
				break;
		}
		return retStr;
	}
	
	private String convertPoptype(char type)
	{
		//商品参与方式(0-全场/1-按单品/2-按柜组/3-按品牌/4-按品类/5-按柜组+品牌/
		//6-按柜组+品类/7-按品牌+品类/8-按柜+品+类/9-按条码(子商品)/A-按属性1/B-按属性2/
		//C-按属性3/D-按属性4/E-按属性5/F-按属性6/G-按属性7/H-按属性8)
		switch(type)
		{
		case '0':
			return "全场";
		case '1':
			return "单品";
		case '2':
			return "柜组";
		case '3':
			return "品牌";
		case '4':
			return "品类";
		case '5':
			return "柜组+品牌";
		case '6':
			return "柜组+品类";
		case '7':
			return "品牌+品类";
		case '8':
			return "柜组+品牌+品类";
		case '9':
			return "条码(子商品)";
		}
		return "";
	}
}
