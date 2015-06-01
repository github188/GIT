package custom.localize.Bjkl;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

public class CardModule
{
	private static String result = "";
	private static CardModule card = new CardModule();

	private CardModule()
	{

	}

	public static CardModule getDefault()
	{
		return card;
	}

	// 1-获取会员
	public CustomerDef getCustomer(String track2)
	{

		String syyh = Convert.increaseCharForward(GlobalInfo.posLogin.gh, '0', 4); // 收银员号
		String syjh = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 4); // 收银机号
		String cardno = "";
		String pass = "";
		String track = track2;
		CustomerDef cust = new CustomerDef();
		boolean isHand = false;

		// 当传入的信息长度等于24时，说明传进来的是磁道信息
		if (track.length() == 13 || (track.length() == 24 && track2.startsWith("0618")) )
		{			
			if (track.length() == 13)
			{
				cardno = analyzeBarcode(track);
				if (cardno == null)
				{
					new MessageBox("此条码非会员条码!");
					return null;
				}
				pass = "00000000";
				isHand = true;
			}
			else
			{
				cardno = track.substring(0, 16);
				pass = track.substring(16, 24);
			}
			
			String line = "1," + syjh + syyh + cardno + pass;

			if (!execute(line))
				return null;

			if (result.startsWith("01"))
			{
				if (result.length() < 80)
				{
					new MessageBox("返回数据长度有误!");
					return null;
				}

				cust.code = result.substring(10, 26);
				cust.str3 = pass; // 记录密码，在交易提交时需要使用
				cust.name = "";

				cust.value1 = ManipulatePrecision.doubleConvert(Convert.toDouble(result.substring(26, 34)), 2, 1); // 卡余额
				// =零钞转存金额
				// +
				// 充值金额
				cust.valuememo = ManipulatePrecision.doubleConvert(Convert.toDouble(result.substring(34, 40)), 2, 1); // 卡积分
				cust.maxdate = result.substring(40, 50); // 卡有效期
				cust.valstr8 = result.substring(50, 52); // 折扣帐户类型
				cust.valnum1 = ManipulatePrecision.doubleConvert(Convert.toDouble(result.substring(52, 60)), 2, 1); // 折扣帐户余额
				// 季度回馈时，将积分按一定的比例转换成人民币，
				// 刷卡时，如果金额大于0，则自己优先使用这个金额付款

				cust.valnum2 = ManipulatePrecision.doubleConvert(Convert.toDouble(result.substring(60, 68)), 2, 1); // 折扣帐户扣率
				cust.valstr9 = result.substring(68, 78); // 折扣有效期
				cust.valstr10 = result.substring(78, 79); // 卡附属状态
				                                          // 1：零钱包询问状态2：默认存入1元以下
				                                          // 3：默认存入10元以下零钱；
				cust.zkl = 1; // 卡的默认折扣率
				cust.track = pass;
				cust.isHandInput = isHand; // 是否条码输入
				cust.isjf = 'Y'; // 会员卡可积分
				cust.ishy = 'H'; // 取促销会员价标志

				if ("1".equals(cust.valstr10))
				{
					cust.value4 = 1;
				}
				else if ("2".equals(cust.valstr10))
				{
					cust.value4 = 10;
				}

				// 计算返利
				if (track2.length() == 24 && result.substring(79, 80).equals("1"))
				{
					if (new MessageBox("卡积分：" + cust.valuememo + "\n\n该卡存在返利,是否进行返利计算?", null, true).verify() == GlobalVar.Key1)
					{
						cardCustSvc(cardno, pass, " 2", " 1");
						
						//积分返利后，重新获取卡信息
						cust = getCustomer(track2);
					}
				}

				return cust;
			}
			else
			{
				// 错误信息
				String[][] errs = { { "03", "卡不存在" }, { "04", "卡无效（过有效期）" }, { "05", "已挂失" }, { "06", "已冻结" }, { "07", "已清户" }, { "08", "密码错误" }, { "09", "卡未启用" }, { "10", "处理失败" }, { "11", "卡已作废" }, { "90", "报文格式不对" }, { "21", "与中心建立连接失败" }, };

				for (int i = 0; i < errs.length; i++)
				{
					if (result.startsWith(errs[i][0]))
					{
						new MessageBox("错误码 " + errs[i][0] + ":" + errs[i][1]);

						return null;
					}
				}
				
				new MessageBox("刷卡出现未知错误！\n\n错误码：" + result.substring(0,2));
			}
		}
		else
		{
			String typeId = "0";
			String mobileno = Convert.increaseCharForward("", ' ', 11); // 手机号
			String maincardid = Convert.increaseCharForward("", ' ', 16); // 手机号
			String subccardid = Convert.increaseCharForward("", ' ', 13); // 手机号
			if (track.length() == 11)
			{
				typeId = "1";
				mobileno = track;
				isHand = true;
			}
			else if (track.length() == 16)
			{
				typeId = "2";
				maincardid = track;
				isHand = true;
			}
//			else if (track.length() == 13)
//			{
//				typeId = "3";
//				subccardid = track;// analyzeBarcode(track);
//				isHand = true;
//			}
			else
			{
				new MessageBox("此卡非会员卡!");
				return null;
			}
			
			String line = "8," + syjh + syyh + typeId + mobileno + maincardid + subccardid;

			if (!execute(line))
				return null;

			if (result.startsWith("01"))
			{
				if (result.length() < 80)
				{
					new MessageBox("返回数据长度有误!");
					return null;
				}

				cust.code = result.substring(10, 26);
				cust.str3 = pass = result.substring(26, 34); // 记录密码，在交易提交时需要使用
				cust.name = result.substring(52, 84).trim();

				cust.zkl = 1; // 卡的默认折扣率
				cust.track = pass;
				cust.isHandInput = isHand; // 是否条码输入
				cust.isjf = 'Y'; // 会员卡可积分
				cust.ishy = 'H'; // 取促销会员价标志

				return cust;
			}
			else
			{
				// 错误信息
				String[][] errs = { { "02", "无手机号" }, { "03", "卡不存在" }, { "04", "卡无效（过有效期）" }, { "05", "已挂失" }, { "06", "已冻结" }, { "07", "已清户" }, { "08", "密码错误" }, { "09", "卡未启用" }, { "10", "处理失败" }, { "11", "卡已作废" }, { "12", "临时挂失" }, { "90", "报文格式不对" }, { "54", "与中心建立连接失败" }, };

				for (int i = 0; i < errs.length; i++)
				{
					if (result.startsWith(errs[i][0]))
					{
						new MessageBox("错误码 " + errs[i][0] + ":" + errs[i][1]);

						return null;
					}
				}
				new MessageBox("刷卡出现未知错误！\n\n错误码：" + result.substring(0,2));
			}
		}

		return null;
	}

	public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
	{
		String syyh = Convert.increaseCharForward(GlobalInfo.posLogin.gh, '0', 4); // 收银员号
		String syjh = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 4); // 收银机号
		String cardno = "";
		String pass = "";
		String track = req.track2;

		// 当传入的信息长度大于或等于24时，说明传进来的是磁道信息
		if (track.length() == 24)
		{
			cardno = track.substring(0, 16);
			pass = track.substring(16, 24);
		}
		else
		{
			new MessageBox("系统无法识别刷卡数据,不允许消费!");
			return false;
		}

		String line = "1," + syjh + syyh + cardno + pass;
		if (!execute(line))
		{
			new MessageBox("调用第三方卡系统接口查询卡信息出现问题!");

			return false;
		}

		if (result.startsWith("01"))
		{
			if (result.length() < 80)
			{
				new MessageBox("返回字符串长度不得小于85!");
				return false;
			}

			//
			// ret.cardno = result.substring(13, 29);
			// 磁道信息=卡号（前16位) + 密码（后8位）
			// 程序将卡号保存到 pay.payno 字段，方便发送消费信息时获取卡号和密码
			ret.cardno = cardno;
			ret.money = ret.ye = ManipulatePrecision.doubleConvert(Convert.toDouble(result.substring(26, 34)), 2, 1); // 卡余额
			ret.cardpwd = pass;
			return true;
		}
		else
		{
			// 错误信息
			String[][] errs = { { "03", "卡不存在" }, { "04", "卡无效（过有效期）" }, { "05", "已挂失" }, { "06", "已冻结" }, { "07", "已清户" }, { "08", "密码错误" }, { "09", "卡未启用" }, { "10", "处理失败" }, { "11", "卡已作废" }, { "90", "报文格式不对" }, { "21", "与中心建立连接失败" }, };

			for (int i = 0; i < errs.length; i++)
			{
				if (result.startsWith(errs[i][0]))
				{
					new MessageBox("错误码 " + errs[i][0] + ":" + errs[i][1]);

					return false;
				}
			}
			
			new MessageBox("刷卡出现未知错误！\n\n错误码：" + result.substring(0,2));
		}

		return false;
	}

	// 2-提交会员卡销售信息，并增加取积分
	public boolean submitSale(String line, StringBuffer sb)
	{
		try
		{
			System.out.println("卡接口发送数据:" + line);
			PosLog.getLog(getClass()).fatal(line);
			
			if (!execute(line))
			{
				new MessageBox("调用第三方卡系统接口 提交消费 信息出现问题!");

				return false;
			}

			StringBuffer info = new StringBuffer();

			System.out.println("卡接口返回数据:" + result);
			PosLog.getLog(getClass()).fatal(result);
			
			if (result.startsWith("01"))
			{

				if (result.length() < 161)
				{
					new MessageBox("返回字符串长度不得小于144");
					return false;
				}

				sb.append(result.substring(2, 12) + ","); // 交易识别号
				sb.append(result.substring(12, 16) + ",");
				sb.append(result.substring(16, 20) + ",");
				sb.append(result.substring(20, 30) + ","); // 交易流水号
				sb.append(result.substring(40, 59) + ","); // 交易时间
				sb.append(result.substring(59, 67) + ","); // 交易金额
				sb.append(result.substring(67, 75) + ","); // 交易总金额
				sb.append("1,"); // 交易类型 1-消费; 2-积分
				sb.append(result.substring(30, 40) + ",");// 结算日期
				sb.append("0" + ",");// 冲正状态 0-未冲正;1-对帐完成;2-已冲正
				sb.append(result.substring(93, 101) + ",");// 卡找零金额
				sb.append(result.substring(101, 103) + ","); // 折扣帐户类型
				sb.append(result.substring(111, 119) + ","); // 会员折扣帐户存入金额
				sb.append(result.substring(119, 127) + ","); // 会员折扣帐户使用金额

				double bcjf = Convert.toDouble(result.substring(81, 87));
				double ljjf = Convert.toDouble(result.substring(87, 93));
				double charge = Convert.toDouble(result.substring(93, 101));
				double disuseMoney = Convert.toDouble(result.substring(119, 127));
				double remainder = Convert.toDouble(result.substring(185, 193));  //卡余额

				sb.append("|" + String.valueOf(bcjf) + "," + String.valueOf(ljjf));
				sb.append("|" + String.valueOf(remainder));

				// if (bcjf > 0 || charge > 0 || disuseMoney > 0 )
				// {
				// double disBalance = Convert.toDouble(result.substring(127,
				// 135));
				// String cardno = result.substring(145 + 8, 161 + 8);
				//
				// info.append("积  分  卡  号: " + Convert.appendStringSize("",
				// cardno, 1, 16, 17, 0) + "\n");
				// info.append("零钱包存入金额: " + Convert.appendStringSize("", charge
				// + "", 1, 16, 17, 0) + "\n");
				// info.append("本次 消费 积分: " + Convert.appendStringSize("", bcjf
				// + "", 1, 16, 17, 0) + "\n");
				// info.append("累  计  积  分: " + Convert.appendStringSize("",
				// ljjf + "", 1, 16, 17, 0) + "\n");
				// //info.append("" + Convert.appendStringSize("", "", 1, 16,
				// 16, 0) + "\n");
				// info.append("折扣户使用金额: " + Convert.appendStringSize("",
				// disuseMoney + "", 1, 16, 17, 0) + "\n");
				// info.append("折扣 账户 余额: " + Convert.appendStringSize("",
				// disBalance + "", 1, 16, 17, 0) + "\n");
				// new MessageBox(info.toString());
				// }

				return true;
			}
			else if (result.startsWith("02"))
			{
				info.append("卡余额不足，请充值后再使用。" + "\n");
				info.append("卡    号: " + Convert.appendStringSize("", result.substring(20, 36), 1, 16, 16, 0) + "\n");
				info.append("卡余额: " + Convert.appendStringSize("", result.substring(36, 44), 1, 16, 16, 0) + "\n");
				new MessageBox(info.toString());
				return false;
			}
			else
			{

				// 错误信息
				String[][] errs = { { "03", "卡不存在" }, { "04", "卡无效（过有效期）" }, { "05", "已挂失" }, { "06", "已冻结" }, { "07", "已清户" }, { "08", "密码错误" }, { "09", "卡未启用" }, { "10", "处理失败" }, { "11", "折扣帐户余额不足" }, { "12", "前置机交易号错误" }, { "14", "折扣帐户余额不足" } };

				for (int i = 0; i < errs.length; i++)
				{
					if (result.substring(0, 2).equals((errs[i][0])))
					{
						new MessageBox("错误码 " + errs[i][0] + ":" + errs[i][1]);
						return false;
					}
				}

				new MessageBox("卡消费交易出现未知错误！\n\n错误码：" + result.substring(0,2));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("提交会员卡销售信息出现异常!" + e.getMessage());
		}

		return false;
	}

	// 3-客户通道
	public boolean cardCustSvc(String track, String pwd, String type, String flag)
	{
		String cardno = "";
		String pass = "";

		if (track.length() == 16)
		{
			if (track.startsWith("9999"))
			{
				new MessageBox("客服通道只准在刷卡情况下使用!");
				return false;
			}
			else if (track.startsWith("0618"))
			{
				cardno = track;
				pass = pwd;
			}
			else
			{
				new MessageBox("客服通道只容许刷会员卡!");
				return false;
			}
		}
		else
		{
			new MessageBox("卡号有问题!");
			return false;
		}

		String syyh = Convert.increaseCharForward(GlobalInfo.posLogin.gh, '0', 4); // 收银员号
		String syjh = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 4); // 收银机号

		String line = "3," + syjh + syyh + cardno + pass + type + flag;

		if (!execute(line))
			return false;

		if (result.startsWith("01"))
		{

			if (type.equals(" 2") && " 1".equals(flag))
			{
				new MessageBox("会员卡参加返利成功!");
				return true;
			}
			else if (" 0".equals(flag))
			{
				new MessageBox("设置零钱包为询问存入状态成功!");
				return true;
			}
			else if (" 1".equals(flag))
			{
				new MessageBox("申请开通1元零存成功!");
				return true;
			}
			else if (" 2".equals(flag))
			{
				new MessageBox("申请开通10元零存成功!");
				return true;
			}
			else if (" 4".equals(flag))
			{
				new MessageBox("设置不存入零钱包成功!");
				return false;
			}
		}
		else
		{
			// 错误信息
			String[][] errs = { { "03", "卡不存在" }, { "04", "卡无效（过有效期）" }, { "05", "已挂失" }, { "06", "已冻结" }, { "07", "已清户" }, { "08", "密码错误" }, { "09", "卡未启用" }, { "10", "处理失败" }, { "11", "卡已作废" }, { "12", "没有返利信息" }, { "90", "报文格式不对" }, { "21", "与中心建立连接失败" }, };

			for (int i = 0; i < errs.length; i++)
			{
				if (result.startsWith(errs[i][0]))
				{
					new MessageBox("错误码" + errs[i][0] + ":" + errs[i][1]);
					return false;
				}
			}
			
			new MessageBox("客户通道出现未知错误！\n\n错误码：" + result.substring(0,2));
		}
		return false;
	}

	// 5-售卡授权
	public String grantSaleCard()
	{
		String cardno = "";
		String pass = "";
		StringBuffer sb = new StringBuffer();
		TextBox txt = new TextBox();

		if (!txt.open(Language.apply("请刷安全卡"), Language.apply("卡号"), Language.apply("请将安全卡从刷卡槽刷入"), sb, 0, 0, false, 2))
			return null;

		String track = txt.Track2;

		if (track.length() >= 24)
		{
			cardno = track.substring(0, 16);
			pass = track.substring(16, 24);
		}
		else
		{
			new MessageBox("磁道信息有误,请重新刷卡!");
			return null;
		}

		String syjh = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 4); // 收银机号
		String code = "69";
		String reqcode = "00";

		String line = "5," + syjh + cardno + pass;

		if (!execute(line))
			return null;

		if (result.startsWith("01"))
			return result.substring(2, 8);

		if (result.startsWith("03"))
		{
			new MessageBox("无此用户!");
			return null;
		}
		else if (result.startsWith("10"))
		{
			new MessageBox("授权失败!");
			return null;
		}
		else
		{
			new MessageBox("出现未知错误，授权失败!\n\n错误码：" + result.substring(0,2));
			return null;
		}
	}

	// 4-售卡
	public String saleCard(String cardid, String payMethod, String cardtypesub, String money, String checkUserId, String payCard)
	{
		String syjh = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 4); // 收银机号
		String syyh = Convert.increaseCharForward(GlobalInfo.posLogin.gh, '0', 6); // 售卡人
		cardid = Convert.increaseCharForward(cardid, '0', 16); // 卡号
		payMethod = Convert.increaseCharForward(payMethod, '0', 1); // 支付方式：1 –
		                                                            // 现金 ,3 _
		                                                            // 银行卡,4 _
		                                                            // 信用卡,5 _
		                                                            // 汇款,6 _ 其他
		cardtypesub = Convert.increaseCharForward(cardtypesub, '0', 2); // 卡小类
		                                                                // 21 _
		                                                                // 200元,
		                                                                // 23 _
		                                                                // 500元,
		                                                                // 24 _
		                                                                // 1000元
		String jeStr = Convert.increaseCharForward(money, '0', 12);// 开卡金额

		ManipulateDateTime mdt = new ManipulateDateTime();
		String date = Convert.increaseCharForward(mdt.getDateBySign(), '0', 10); // 开卡日期
		String checkTime = Convert.increaseCharForward(mdt.getDateBySign() + " " + mdt.getTime(), '0', 19);// 复核时间\

		// 复核人
		checkUserId = Convert.increaseCharForward(checkUserId, '0', 6);
		String fphm = Convert.increaseCharForward(String.valueOf(GlobalInfo.syjStatus.fphm - 1), '0', 16); // POS流水号
		payCard = Convert.increaseCharForward(payCard, '#', 32);// 支付银行卡号，非银行卡支付用#
		                                                        // 代替
		String line = "4," + syjh + syyh + cardid + payMethod + cardtypesub + jeStr + date + checkTime + checkUserId + fphm + payCard;

		if (!execute(line))
			return null;

		if (result.startsWith("01"))
			return result.substring(8, 10);

		if (result.startsWith("02"))
		{
			new MessageBox("此卡无库存!");
			return null;
		}

		if (result.startsWith("10"))
		{
			new MessageBox("售卡失败!");
			return null;
		}

		return null;
	}

	// 6 礼品卡库存查询 激活储值卡
	public boolean checkStock(String track)
	{
		String syjh = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 4); // 收银机号
		String line = "6," + syjh + track;

		if (!execute(line))
			return false;

		if (result.startsWith("01"))
			return true;

		if (result.startsWith("02"))
		{
			new MessageBox("此卡无库存!");
			return false;
		}

		if (result.startsWith("10"))
		{
			new MessageBox("卡库存查询失败!");
			return false;
		}

		return false;
	}

	// 礼品卡 余额无密码查询
	public boolean searchLpkInfo(String barcode)
	{
		String cardno = "0001" + barcode;
		String syyh = Convert.increaseCharForward(GlobalInfo.posLogin.gh, '0', 4); // 收银员号
		String syjh = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 4); // 收银机号

		String line = "7," + syjh + syyh + cardno;
		if (!execute(line))
		{
			new MessageBox("调用第三方卡系统接口 礼品卡余额无密码查询 出现问题!");

			return false;
		}

		if ("01".equals(result.substring(0, 2)))
		{
			cardno = result.substring(13, 29);
			String ye = result.substring(29, 37);
			String date = result.substring(37, 47);

			new MessageBox("礼品卡信息：\n卡  号：" + cardno + "\n卡余额：" + ye + "\n有效期： " + date);

			return true;
		}
		else if ("90".equals(result.substring(0, 2)))
		{
			new MessageBox("报文格式不对!");
		}
		else if ("21".equals(result.substring(0, 2)))
		{
			new MessageBox("与中心建立连接失败!");
		}

		return false;
	}

	// 根据京客隆卡系统接口文档，解析条码
	// bar 卡条码 type 条码类型（1-会员卡条码,家庭卡子卡，2-礼品卡条码,3-储值卡条码
	private String analyzeBarcode(String bar)
	{
		String code = "";

		try
		{
			// 会员卡
			if ("995".equals(bar.substring(1, 4)))
			{
				code = "9999" + bar.substring(0, bar.length() - 1);
			}
			// 家庭卡
			else if ("61".equals(bar.substring(0, 2)) || "62".equals(bar.substring(0, 2)) || "63".equals(bar.substring(0, 2)) || "64".equals(bar.substring(0, 2)) || "65".equals(bar.substring(0, 2)) || "66".equals(bar.substring(0, 2)))
			{
				code = "9999" + bar.substring(0, bar.length() - 1);
			}
			else if ("66".equals(bar.substring(4, 6)))
			{
				code = "0001" + bar.substring(0, bar.length() - 1);
			}
			else
			{
				new MessageBox("输入条码错误，请检查!");
				return null;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		return code;
	}

	public double[] convertCardno(String cardno)
	{
		if (cardno.trim().equals("") || cardno.trim().length() < 16)
			return null;

		String amtCode = cardno.substring(6, 8);

		if (amtCode.equals("02"))
			return new double[] { 21, 200 };

		if (amtCode.equals("05"))
			return new double[] { 23, 500 };

		if (amtCode.equals("10"))
			return new double[] { 24, 1000 };

		return null;
	}

	// 调用会员卡接口
	private boolean execute(String request)
	{
		String errmsg = "";
		ProgressBox pb = new ProgressBox();
		BufferedReader br = null;
		try
		{
			pb.setText("正在调用第三方卡接口,请稍等...");
			// 先删除上次交易数据文件
			result = "";
			String path = ".\\";
			if (PathFile.fileExist(path + "request.txt"))
			{
				PathFile.deletePath(path + "request.txt");

				if (PathFile.fileExist(path + "request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(path + "result.txt"))
			{
				PathFile.deletePath(path + "result.txt");

				if (PathFile.fileExist(path + "result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile(path + "request.txt");
				if (pw != null)
				{
					pw.println(request);
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
					pw = null;
				}
			}

			// 调用接口模块
			if (PathFile.fileExist(path + "javaposbank.exe"))
			{
				/*PosLog log = PosLog.getLog(getClass());
				log.debug("\r\n====== 调用之前  ================");
				log.debug(log.getJVMMemoryInfo());
				log.debug(log.getOSMemoryInfo());*/
				
				CommonMethod.waitForExec(path + "javaposbank.exe BJKL");				
				
				/*log.debug("\r\n====== 调用之后 ================");
				log.debug(log.getJVMMemoryInfo());
				log.debug(log.getOSMemoryInfo());*/
			}
			else
			{
				new MessageBox("找不到工程模块 javaposbank.exe");
				return false;
			}

			if (!PathFile.fileExist(path + "result.txt") || ((br = CommonMethod.readFileGBK(path + "result.txt")) == null))
			{
				new MessageBox("读取工程应答数据失败!", null, false);
				return false;
			}

			result = br.readLine();

			if (result.length() <= 0)
			{
				new MessageBox("调用接口失败!");
				return false;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("调用会员接口出现异常：" + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if (br != null)
				{
					br.close();
					br = null;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if (pb != null)
			{
				pb.close();
				pb = null;
			}

		}

		return true;
	}
}