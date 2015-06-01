package custom.localize.Jlbh;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.SocketService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Cmls.Cmls_DataService;
import custom.localize.Cmls.Cmls_SaleBS;

public class Jlbh_SaleBS extends Cmls_SaleBS
{
	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		if (SellType.ISCHECKINPUT(saletype))
		{
			return super.rowInfo(goodsDef);
		}
		else
		{
			String[] row = super.rowInfo(goodsDef);
			row[2] = goodsDef.name;
			return row;
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
		((Cmls_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
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

//		 标志是否为9开头扩展的控制
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
		
		String line1 = "";
		if (yh.charAt(0) != '0')
		{
			line += "D";
		}

		if (yh.charAt(1) != '0')
		{
			line1+=popDef.jssj.substring(0,popDef.jssj.indexOf("|"));
			line += "J";
		}

		if (yh.charAt(2) != '0')
		{
			line1+=popDef.jssj.substring(popDef.jssj.indexOf("|")+1,popDef.jssj.lastIndexOf("|"));
			line += "Q";
		}

		if (yh.charAt(3) != '0')
		{
			line1+=popDef.jssj.substring(popDef.jssj.lastIndexOf("|")+1);
			line += "Z";
		}
		
		if (yh.length() > 5 && yh.charAt(5) != '0')
		{
			line += "F";
		}

		if (line.length() > 0)
		{
			sg.name = line1+"(" + line + ")" + sg.name;
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
	
	public boolean saleCollectAccountPay()
	{
		if (super.saleCollectAccountPay())
		{
			String appendline = "";
			Payment p = null;
			Vector v = new Vector();
			double syje = 0;
			String sendline = "";
			for (int i = 0; i < payAssistant.size(); i++)
			{
				p = (Payment) payAssistant.elementAt(i);
				if (p == null) continue;
				
				if (p.getClass().getName().indexOf("JLCZK_PaymentMzk")>=0)
				{
					String cardno = p.salepay.payno;
					String pwd = p.salepay.str4;
					String je = Convert.increaseCharForward(ManipulatePrecision.doubleToString(p.salepay.je), 8);
					syje = ManipulatePrecision.doubleConvert(syje + p.salepay.je);
					appendline+=cardno+pwd+je;
					sendline += "|"+cardno+","+je.trim();
					v.add(String.valueOf(i));
				}
			}
			
			if (sendline.length() <=0) return true;
			
			if (sendline.length() > 0)sendline = sendline.substring(1);
			
			if (appendline.trim().length() > 0 && syje > 0)
			{
				//	1	包长	PKGLEN	4	
				//2	交易代码	PCODE	2	10 – 卡消费请求包
				String PCODE = "10";
				//3	回应码	RCODE	2	00 － 消费请求
				String RCODE = "00";
				//4	收款台号	POSID	4
				String POSID = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, 4);
				//5	收银员号	CASHID	4
				String CASHID = Convert.increaseCharForward(GlobalInfo.posLogin.gh, 4);
				//6	交易流水号	SEQID	10
				String SEQID = Convert.increaseCharForward(String.valueOf(saleHead.fphm),'0', 10);
				//7	消费金额	MONEY	（8,2）
				String MONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(syje), 8);
				//8	消费总金额	TMONEY	（8,2）	所有支付方式的总金额
				String TMONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(saleHead.ysje), 8);
				//9	找零金额	CHARGE	（8,2）
				String CHARGE = Convert.increaseCharForward(ManipulatePrecision.doubleToString(0), 8);
				//10	折扣帐户类型	DISTYPE	2	
				String DISTYPE = Convert.increaseCharForward("  ", 2);
				//11	折扣帐户存入额	DISDEPOSITMONEY	(8,2)	
				String DISDEPOSITMONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(0), 8);
				//12	折扣帐户使用金额	DISUSEMONEY	(8,2)
				String DISUSEMONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(0), 8);
				//13	折扣存商品明细	DISINDETAIL	不定长	
				
				String line1 = PCODE+RCODE+POSID+CASHID+SEQID+MONEY+TMONEY+CHARGE+DISTYPE+DISDEPOSITMONEY+DISUSEMONEY+appendline;
				line1 = Convert.increaseCharForward(String.valueOf(line1.length()),'0', 4)+line1;
				String retline1 = SocketService.getDefault(0).sendMessage(line1, null);
				
				//读取返回码
				if (retline1 == null) return false;
				String line = retline1;
				
				//int len = Convert.toInt(line.substring(0,4));
				line = line.substring(4);
				
				PCODE = Convert.newSubString(line, 0, 2);
				line =  Convert.newSubString(line, 2);
				if (!PCODE.equals("11"))
				{
					new MessageBox("交易代码不是卡消费处理包 "+PCODE);
					return false;
				}
				
				RCODE = Convert.newSubString(line, 0, 2);
				line =  Convert.newSubString(line, 2);
				if (!RCODE.equals("01"))
				{
					String msg = "";
					
					msg += "03 – 卡不存在\n";
					msg +="04 – 卡无效（过有效期）\n";
					msg +="05 – 已挂失\n";
					msg +="06 – 已冻结\n";
					msg +="07 – 已清户\n";
					msg +="08 – 密码错误\n";
					msg +="09 – 卡未启用\n";
					msg +="10 – 处理失败\n";
					msg +="11 – 卡已作废\n";
					msg +="12 – 卡临时挂失\n";
					new MessageBox("回应码失败 "+RCODE+"\n"+msg);
					
					return false;
				}
				
				//交易识别号	TRACEID	10	
				String TRACEID = Convert.newSubString(line, 0, 10);
				line =  Convert.newSubString(line, 10);
				//收款台号	POSID	4	
				if (!POSID.equals(Convert.newSubString(line, 0,4)))
				{
					new MessageBox("收银机号和传入的不匹配"+RCODE);
					return false;
				}
				line =  Convert.newSubString(line, 4);
				//收银员号	CASHID	4	
				if (!CASHID.equals(Convert.newSubString(line, 0,4)))
				{
					new MessageBox("收银员号和传入的不匹配"+RCODE);
					return false;
				}
				line =  Convert.newSubString(line, 4);
				
				//交易流水号	SEQID	10	
				line =  Convert.newSubString(line, 10);
				//结算日期	SETTDATE	10	作对帐用
				String SETTDATE = Convert.newSubString(line, 0,10);
				line =  Convert.newSubString(line, 10);
				//交易时间	TRANSTIME	19	
				String TRANSTIME = Convert.newSubString(line, 0,19);
				line =  Convert.newSubString(line, 19);
				//消费金额	MONEY	（8,2）	
				//String RMONEY = Convert.newSubString(line, 0,8);
				line =  Convert.newSubString(line, 8);
				//消费总金额	TMONEY	（8,2）	
				line =  Convert.newSubString(line, 8);
				//消费前积分	PSCORE	6	
				line =  Convert.newSubString(line, 6);
				//本次消费积分	CSCORE	6	
				line =  Convert.newSubString(line, 6);
				//消费后积分	LSCORE	6	
				line =  Convert.newSubString(line, 6);
				//找零金额	CHARGE	（8,2）	
				line =  Convert.newSubString(line, 8);
				//折扣帐户类型	DISTYPE	2	
				line =  Convert.newSubString(line, 2);
				//折扣户使用前金额	DISPMONEY	(8,2)	
				line =  Convert.newSubString(line, 8);
				//折扣帐户存入额	DISINMONEY	(8,2)	
				line =  Convert.newSubString(line, 8);
				//折扣户使用金额	DISUSEMONEY	(8,2）
				line =  Convert.newSubString(line, 8);	
				//折扣户余额	DISBALANCE	(8,2)	
				line =  Convert.newSubString(line, 8);
				//帐户金额有效期	DISVALIDDATE	10	
				line =  Convert.newSubString(line, 10);
				
				//不知名信息	8	
				line =  Convert.newSubString(line, 8);
				for (int in = 0; in < v.size(); in ++)
				{
					p = (Payment) payAssistant.elementAt(Convert.toInt(v.elementAt(in)));

					if (p.getClass().getName().indexOf("JLCZK_PaymentMzk")>=0)
					{
						String cardno = p.salepay.payno;
						int ix = -1;
						if ((ix =line.indexOf(cardno)) >= 0)
						{
							//卡号	CARDID	16	
							//消费前金额	PMONEY	(8,2)	
							//卡消费金额	CMONEY	(8,2)	
							//卡余额	LMONEY	(8,2)	
							//卡有效期	VALIDDATE	10	卡有效期
							String cardline = Convert.newSubString(line, ix,ix+50);
							String kje = cardline.substring(24, 32);
							String kye = cardline.substring(32,40);
							if (p.salepay.je != Convert.toDouble(kje.trim()))
							{
								 new MessageBox("金额不匹配");
								 return false;
							}
							
							((SalePayDef)salePayment.elementAt(Convert.toInt(v.elementAt(in)))).kye = Convert.toDouble(kye);
							((SalePayDef)salePayment.elementAt(Convert.toInt(v.elementAt(in)))).str2= cardline.substring(40);
						}	
					}
				}
				
				// 往POS库里面插入一条数据，防止红冲
				if (((Jlbh_NetService)NetService.getDefault()).sendJKLCard(GlobalInfo.sysPara.mktcode, saleHead.syjh, saleHead.fphm, saleHead.syyh, TRACEID, SETTDATE, TRANSTIME, String.valueOf(saleHead.ysje), sendline, null))
				{
					return true;
				}
				
			}
		}
		return false;
	}
}
