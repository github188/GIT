package custom.localize.Zmjc;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bcrm.Bcrm_SaleBS;

public class Zmjc_SaleBSClk extends Bcrm_SaleBS
{
	protected ClkDef clk;//常旅卡

	//初始化常旅卡
	public void initGWK()
	{
		clk = new ClkDef();
		clk.iszk=' ';
		clk.zklb="";
		clk.isaq=' ';
		clk.isbq=' ';
		saleHead.zmsy_gwk = clk;
	}
	
	public void initSellData()
    {
		super.initSellData();
		if (GlobalInfo.sysPara.isUseClk.charAt(0)=='Y')
		{
			initGWK();
			saleHead.str3="";//常旅卡号
		}
    }
	
	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (GlobalInfo.sysPara.isUseClk.charAt(0) == 'Y')
		{
			findGoodsCRMPop_Clk(sg, goods, info);
		}
		 else
		 {
			 //super.findGoodsCRMPop(sg, goods, info);
			 findGoodsCRMPop_Bcrm(sg, goods, info);
		 }
	}
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
	
	public void findGoodsCRMPop_Bcrm(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String newyhsp = "90000000";
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

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Zmjc_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype,isfjk, grouplist, saletype);
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
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
						contents.add(new String[] { "D", Language.apply("参与打折促销活动"), "0" });
					}
					else if (i == 1)
					{
						contents.add(new String[] { "J", Language.apply("参与减现促销活动"), "1" });
					}
					else if (i == 2)
					{
						contents.add(new String[] { "Q", Language.apply("参与返券促销活动"), "2" });
					}
					else if (i == 3)
					{
						contents.add(new String[] { "Z", Language.apply("参与赠品促销活动"), "3" });
					}
					else if (i == 5)
					{
						contents.add(new String[] { "F", Language.apply("参与积分活动"), "5" });
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
				String[] title = { Language.apply("代码"), Language.apply("描述") };
				int[] width = { 60, 400 };
				int choice = new MutiSelectForm().open(Language.apply("请选择参与满减满赠活动的规则"), title, width, contents);

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

			if (append) info.str1 = "9" + buff.toString();
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
	public void findGoodsCRMPop_Clk(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String newyhsp = "90000000";
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
		if(clk!=null && clk.cardno!=null && clk.cardno.trim().length()>0)
		{
			//如果刷了常旅卡
			cardtype="CLK";
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Zmjc_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype,isfjk,grouplist, saletype);
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
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
						contents.add(new String[] { "D", Language.apply("参与打折促销活动"), "0" });
					}
					else if (i == 1)
					{
						contents.add(new String[] { "J", Language.apply("参与减现促销活动"), "1" });
					}
					else if (i == 2)
					{
						contents.add(new String[] { "Q", Language.apply("参与返券促销活动"), "2" });
					}
					else if (i == 3)
					{
						contents.add(new String[] { "Z", Language.apply("参与赠品促销活动"), "3" });
					}
					else if (i == 5)
					{
						contents.add(new String[] { "F", Language.apply("参与积分活动"), "5" });
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
				String[] title = { Language.apply("代码"), Language.apply("描述") };
				int[] width = { 60, 400 };
				int choice = new MutiSelectForm().open(Language.apply("请选择参与满减满赠活动的规则"), title, width, contents);

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

			if (append) info.str1 = "9" + buff.toString();
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
	
	//刷常旅卡
	public void execCustomKey0(boolean keydownonsale)
	{
		try
		{
			if (GlobalInfo.sysPara.isUseClk.charAt(0)!='Y')
			{
				new MessageBox(Language.apply("操作失败：未启用常旅卡功能!"));
				return;
			}
			
			if (saleGoods.size()>0)
			{
				//录入商品之后 ，不允许刷常旅卡
				new MessageBox(Language.apply("操作失败：请在扫商品之前刷卡!"));
				return;
			}
			findGWK();
			saleEvent.setVIPInfo(getVipInfoLabel());//刷新界面会员控件值
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		
	}
	
	//查找常旅卡
	public boolean findGWK()
	{
		boolean blnRet = false;
		try
		{
			TextBox textBox = null;
			int flagClose = 1;
			
			while (flagClose != 0)
			{
				flagClose++;
				StringBuffer thdh = new StringBuffer("");//请在这里输入退货单号
				String title = Language.apply("请刷常旅卡:");
				String help = "";
				String isPASSWORD = ""; //当值为"PASSWORD"则属于PASSWORD模式 
				int modeType = TextBox.AllInput;//.MsrKeyInput;
				textBox = new TextBox();
				
				if (textBox.open(title, isPASSWORD, help, thdh, modeType))
				{
					if (thdh.toString().trim().length()<=0) 
					{
						new MessageBox(Language.apply("卡号不能为空!"));
						continue;
					}

					Zmjc_DataService data = (Zmjc_DataService) DataService.getDefault();
					if (!data.findClkInfo(thdh.toString().trim(), clk))
					{
						this.initGWK();
						PosLog.getLog(this.getClass().getSimpleName()).info("[" + thdh.toString().trim() + "]常旅卡校验未通过!");
						//new MessageBox("常旅卡校验未通过!");						
					}
					else
					{
						blnRet = true;
						saleHead.str3=clk.cardno;//常旅卡号
					}

					//录入提货单结束正常退出
					flagClose = 0;
				}
				else
				{
					new MessageBox(Language.apply("操作失败: 取消刷卡!"));
					break;
				}
			}
			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			blnRet = false;
		}
		return blnRet;
	}

	//在会员显示前加上当前证件标识
	public String getVipInfoLabel()
    {
		String clkType = "";
		try
		{
	    	if (clk != null && clk.cardno!=null && clk.cardno.trim().length()>0)
	    	{
    			//gwkType = "【" + clk.cardno + "证件】";
	    		clkType = clk.cardno;
	    	}
	    	clkType = clkType + super.getVipInfoLabel();
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
    	return clkType;
    }
	

	// iszdxp：是否为指定柜组查询,需要确认GlobalInfo.sysPara.yyygz != 'Y'
	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzcm, StringBuffer slbuf, boolean iszdxp)//findGoodsInfo_ZMJC_CLK
	{
		//return super.findGoodsInfo(code, yyyh, gz, dzcmscsj, isdzcm, slbuf, iszdxp);
		
		GoodsDef goodsDef = new GoodsDef();
		int searchFlag = 0;

		String yhsj = null;
		String scsj;

		// 设置查找商品的查找标志,1-超市销售/2-柜台销售检查营业员串柜/3-柜台销售不检查营业员串柜/4赠品
		if (GlobalInfo.syjDef.issryyy == 'N' || yyyh.equals(Language.apply("超市")))
		{
			searchFlag = 1; // 超市
		}
		else if ((GlobalInfo.sysPara.yyygz != 'N' && GlobalInfo.syjDef.issryyy != 'B' && gz != null && gz.length() > 0 && !gz.equals("多个柜")) || iszdxp)
		{
			searchFlag = 2; // 控制串柜
		}
		else
		{
			searchFlag = 3; // 不控制串柜
		}

		// 退货时不查找优惠,优惠时间以交易时间为准
		if (SellType.ISBACK(saletype))
		{
			yhsj = "";
		}
		else
		{
			yhsj = saleHead.rqsj;
		}

		// 生鲜商品生产时间
		scsj = convertDzcmScsj(dzcmscsj, isdzcm);

		// 盘点输入不控制串柜输入
		if (SellType.ISCHECKINPUT(saletype))
		{
			searchFlag = 3;
		}

		// 看板销售传入标记9,如何选择了家电发货地点则
		if (jdfhddcode != null && jdfhddcode.length() > 0)
		{
			searchFlag = 9;
			scsj = saleHead.jdfhdd; // scsj标记发货地点
		}

		StringBuffer saletype_tmp = new StringBuffer();
		saletype_tmp.append(saletype);
		if (GlobalInfo.sysPara.isUseClk.charAt(0)=='Y')	//是否启用常旅卡功能		
		{
			saletype_tmp.append("|");
			saletype_tmp.append(String.valueOf(clk.iszk));
			saletype_tmp.append("|");
			saletype_tmp.append(String.valueOf(clk.zklb));
			saletype_tmp.append("|");
			saletype_tmp.append(String.valueOf(clk.isaq));
			saletype_tmp.append("|");
			saletype_tmp.append(String.valueOf(clk.isbq));
			
			saletype_tmp.append("|");//wangyong add by 2014.12.09 for 柬埔寨
			saletype_tmp.append(String.valueOf(clk.num1));
		}
		// 开始查找商品
		int result = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, gz, scsj, yhsj, saletype_tmp.toString());//saletype
		switch (result)
		{
			case 0:
				break;
			case 4:// 商品存在多柜组

				if(inputGoodsGZ(goodsDef, searchFlag, code, scsj, yhsj, saletype_tmp.toString())!=0) return null;//saletype
				
				/* 
				//wangyong update by 2013.9.18 单独写到一个函数,是为了分支能界面自选柜组信息(替代现在的手输柜组号)
				//old bak start
				StringBuffer gzstr = new StringBuffer();
				boolean done = true;
				done = new TextBox().open("请输入[" + code.trim() + "]商品的柜组", "柜组号", "该商品有多个柜组，请输入柜组号以便销售", gzstr, 0, 0, false);
				if (!done)
				{
					return null;
				}
				else
				{
					searchFlag = 2;
					int ret = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, gzstr.toString(), scsj, yhsj, saletype);
					if (ret == 4)
					{
						new MessageBox("在指定柜组内未找到该商品\n请重新确定柜组是否正确");
						return null;
					}
					else if (ret != 0) { return null; }
				}
				//old bak end
				*/
				break;

			default:
				return null;
		}

		// 检查营业员串柜情况
		if (GlobalInfo.sysPara.yyygz != 'N' && GlobalInfo.syjDef.issryyy != 'B' && curyyygz.length() > 0)
		{
			String[] s = curyyygz.split(",");
			if (s.length > 1)
			{
				int i;
				for (i = 0; i < s.length; i++)
				{
					if (goodsDef.gz.equalsIgnoreCase(s[i]))
						break;
				}
				if (i >= s.length)
				{
					new MessageBox(Language.apply("该商品不是营业柜组范围内的商品\n\n营业员的营业柜组范围是\n") + curyyygz);
					return null;
				}
			}
		}

		// 使用代码销售时检查多单位商品
		if (code.equals(goodsDef.code) && goodsDef.isuid == 'Y') { return getMutiUnitChoice(goodsDef); }

		// 母商品选择子商品进行销售
		if (goodsDef.type == '6') { return getSubGoodsDef(goodsDef); }

		// 判断是否VIP折扣标志设置该单品是否享受VIP折扣
		if (GlobalInfo.sysPara.isHandVIPDiscount == 'A' && !isVIPZK)
		{
			goodsDef.name = "[" + goodsDef.name + "]";
			goodsDef.isvipzk = 'N';
		}

		return goodsDef;
	}
	
	public int inputGoodsGZ(GoodsDef goodsDef, int searchFlag, String code, String scsj, String yhsj, String djlb)
	{
		try
		{
			StringBuffer gzstr = new StringBuffer();
			boolean done = true;
			done = new TextBox().open(Language.apply("请输入[{0}]商品的柜组", new Object[] { code.trim() }), Language.apply("柜组号"), Language.apply("该商品有多个柜组，请输入柜组号以便销售"), gzstr, 0, 0, false);
			if (!done)
			{
				return -2;// null;
			}
			else
			{
				searchFlag = 2;
				int ret = DataService.getDefault().getGoodsDef(goodsDef, searchFlag, code, gzstr.toString(), scsj, yhsj, djlb);//saletype
				if (ret == 4)
				{
					new MessageBox(Language.apply("在指定柜组内未找到该商品\n请重新确定柜组是否正确"));
					return -4;// null;
				}
				// else if (ret != 0) { return null; }
				return ret;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 999;
		}
	}

	public boolean refreshSaleData()
	{
		boolean blnRet = false;
		try
		{
			blnRet = super.refreshSaleData();
			if (saleHead != null)
			{
				if (GlobalInfo.sysPara.isUseClk.charAt(0)=='Y')
				{
					if (saleHead.zmsy_gwk != null)
					{
						clk = (ClkDef) saleHead.zmsy_gwk;
						if (clk!=null) saleHead.str3 = clk.cardno;
						
					}
				}				
				
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return blnRet;
	}
	
}
