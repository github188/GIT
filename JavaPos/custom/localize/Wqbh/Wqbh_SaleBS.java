package custom.localize.Wqbh;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Device.SecMonitor;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Bcrm.Bcrm_SaleBS;
import device.ICCard.KTL512VWQ;

public class Wqbh_SaleBS extends Bcrm_SaleBS {
	public double jfje = 0;

	 public void printHang(int maxGD)
	 {
	 }

	public OperUserDef inputRebateGrant(int index) {
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null)
			return null;
		if (staff.dpzkl * 100 >= 100) {
			new MessageBox("该员工授权卡无法授权单品打折");
			return null;
		}

		return staff;
	}

	public boolean allowQuickExitSell() {
		if (NewKeyListener.searchKeyCode(GlobalVar.MainList) > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void execCustomKey1(boolean keydownonsale) {
		new FjkInfoQueryBS().QueryFjkInfo();
	}

	public void execCustomKey0(boolean keydownonsale) {
		// 大会员键弹出刷卡框

		if (isPreTakeStatus()) {
			new MessageBox("预售提货状态下不允许重新刷卡");
			return;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0
				&& !isNewUseSpecifyTicketBack(false)) {
			new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
			return;
		}

		if (saleGoods.size() > 0 && !memberAfterGoodsMode()
				&& !isNewUseSpecifyTicketBack(false)) {
			new MessageBox("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡");
			return;
		}

		// 弹出刷卡窗口 ---new
		String[] title = { "输入类型" };
		int[] width = { 440 };
		Vector contents = new Vector();
		contents.add(new String[] { "刷卡输入" });
		contents.add(new String[] { "手机号输入" });
		contents.add(new String[] { "大会员键盘输入" });

		String track2 = "";
		int choice = new MutiSelectForm().open("请选择输入方式", title, width,
				contents);

		if (choice == 1) {

			ProgressBox pb = null;
			pb = new ProgressBox();
			pb.setText("正在输入卡号和密码,请等待...");
			track2 = new KTL512VWQ().findCard();
			if (pb != null) {
				pb.close();
				pb = null;
			}
		} else if (choice == 2) {
			// 弹出刷卡窗口
			StringBuffer cardno = new StringBuffer();
			TextBox txt = new TextBox();
			if (!txt.open("请输入大会员手机号", "手机号", "请将在大会员键盘输入手机号", cardno, 0, 0,
					false, TextBox.IntegerInput))
				return;

			track2 = cardno.toString();
		} else {

			// 弹出刷卡窗口
			StringBuffer cardno = new StringBuffer();
			TextBox txt = new TextBox();
			if (!txt.open("请刷大会员卡", "大会员号", "请将大会员卡从刷卡槽刷入", cardno, 0, 0,
					false, TextBox.MsrInput))
				return;

			track2 = txt.Track2;

		}

		if (track2 == null || track2.equals(""))
			return;

		// 查找会员卡 调用dll 获取大会员详细信息及积分余额
		CustomerDef cust = sendMemberDHYInfo(track2);

		if (cust == null)
			return;
		saleHead.str6 = track2; // 记录大会员磁道信息 用于调用大会员DLL时发送
		saleHead.num1 = 1; // 大会员刷卡标识 用于红冲时区分是否大会员
		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false)) {
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			saleEvent.setVIPInfo(getVipInfoLabel());
			return;
		} else {
			// 记录会员卡
			memberGrantFinish(cust);
			saleEvent.setVIPInfo(getVipInfoLabel());
		}

		return;
	}

	public CustomerDef sendMemberDHYInfo(String track2) {

 		if (GlobalInfo.sysPara.isNEWDHY.equals("Y")) {
			// 新接口方式
			// 1.发送track2给接口，获取会员信息
			// 2.将获取到的会员信息上传到百货后台（对应命令133，Wqbh_NetService中sendMemberInfo方法）
			String memberInfoReturn = findMemberNewDHYCard(track2);
			if (memberInfoReturn != null
					&& memberInfoReturn.trim().length() > 0) {
				/**
				 * status 必选 Int 0:成功,非0：错误码 
				 * message 可选 String 失败原因描述 
				 * data 可选Json 具体类型如下： data的json构成 
				 * uid 必选 String 会员ID 
				 * loginToken 可选 String
				 * token存入缓存，不同类型返回的token不同，有效期也不同UID+appid+channel+当前时间的时间戳做MD5加密
				 * member 可选 Json 会员信息 
				 * member的json构成 
				 * uid 必选 String 会员ID 
				 * mobile 可选 String 手机号
				 * cardNo 可选 String 卡号 
				 * point 可选 String 积分，格式0.00
				 * nickName 可选 String 昵称 
				 * birthday 可选 int 出生日期（时间戳） 
				 * birthType 可选
				 * int 生日类型（birthType枚举） 
				 * memberGrade 可选 int 会员等级 
				 * gender 可选 Int 性别（gender枚举)
				 */
				try {
					JSONObject js = JSONObject.parseObject(memberInfoReturn);
					if (js.getString("status").equals("0")) {// 返回成功
						String data = js.getString("data");
						JSONObject jsdata = JSONObject.parseObject(data);
						if(saleHead!=null){
						saleHead.str1 = jsdata.getString("loginToken");
						saleHead.str8 = jsdata.getString("uid");
						}
						//用系统参数来存会员ID，避免后面传空值
						GlobalInfo.sysPara.commMerchantId=jsdata.getString("uid");
						String member = jsdata.getString("member");
						JSONObject jsmember = JSONObject.parseObject(member);
						String cardID = jsmember.getString("cardNo");
						String bonus = jsmember.getString("point");
						String validBonus = jsmember.getString("avlPoint");
						String certificateType = "";
						String certificate = "";
						String sex = jsmember.getString("gender");
						String phone = jsmember.getString("mobile");
						String memberLevel =jsmember.getString("memberGrade");
						String address = jsmember.getString("address");
						String email = jsmember.getString("email");
						String name = jsmember.getString("nickName");
						String track = jsmember.getString("track2");
						Wqbh_NetService wn = new Wqbh_NetService();
						CustomerDef cust = new CustomerDef();
						if ("".equals(track)|| track==null) track=phone;
						if (wn.sendMemberInfo(cust, name, track, cardID,
								bonus, validBonus, certificateType,
								certificate, sex, phone, memberLevel, address,
								email)) {
							return cust;
						}
					}else{
						new MessageBox("大会员登录失败，代码："+js.getString("status")+"错误描述："+js.getString("message"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} else {
			/**
			 * 1.调用dll获取大会员详细信息 2.将大会员详细信息上传到百货后台 3.生成CustomerDef对象返回
			 */

			String memberInfoReturn = findMemberDHYCard(track2);

			if (memberInfoReturn != null
					&& memberInfoReturn.trim().length() > 0) {
				/**
				 * char ResultCode[2] // 应答代码 
				 * char CardID[19] // 卡号 
				 * char Bonus[12] // 当前积分余额 
				 * char ValidBonus[12] // 当前可用积分 
				 * char CertificateType[2] // 证件类型：居民身份证 01、士官证 02、学生证 03、驾驶证 04、护照、05、港澳通行证 06、其他07 
				 * char Certificate[32] // 证件号 
				 * char Sex[2] // 性别 男01 女02 
				 * char Phone[11] // 手机 
				 * char MemberLevel[1] //会员级别：注册会员 0、金卡会员 1、白金卡会员 2、钻石卡会员 3 char Address[80] // 地址
				 * char Email[40] // 电子邮件 
				 * char ResultText[42] // 回应信息
				 */
				if (memberInfoReturn.substring(0, 2).equals("00")) {
					byte[] mir = memberInfoReturn.getBytes();
					String cardID = new String(subBytes(mir, 2, 19)).trim();
					String bonus = new String(subBytes(mir, 21, 12)).trim();
					String validBonus = new String(subBytes(mir, 33, 12))
							.trim();
					String certificateType = new String(subBytes(mir, 45, 2))
							.trim();
					String certificate = new String(subBytes(mir, 47, 32))
							.trim();
					String sex = new String(subBytes(mir, 79, 2)).trim();
					String phone = new String(subBytes(mir, 81, 11)).trim();
					String memberLevel = new String(subBytes(mir, 92, 1))
							.trim();
					String address = new String(subBytes(mir, 93, 80)).trim();
					String email = new String(subBytes(mir, 173, 40)).trim();
					String name = new String(subBytes(mir, 213, 32)).trim();
					Wqbh_NetService wn = new Wqbh_NetService();
					CustomerDef cust = new CustomerDef();
					if (wn.sendMemberInfo(cust, name, track2, cardID, bonus,
							validBonus, certificateType, certificate, sex,
							phone, memberLevel, address, email)) {
						return cust;
					}
				}
			}
		}

		return null;

	}

	public String findMemberDHYCard(String track2) {
		/**
		 * 1.调用dll获取大会员详细信息 2.将大会员详细信息上传到百货后台 3.生成CustomerDef对象返回
		 */
		String syjh = GlobalInfo.syjDef.syjh;
		String syyh = GlobalInfo.posLogin.gh;
		// String track = Convert.increaseChar(track2,40);
		String memberInfoReturn = "";
		PosLog.getLog(getClass()).info(
				"收银机号:" + syjh + " 收银员号：" + syyh + " 轨道号:" + track2.trim());

		if (RdPlugins.getDefault().getPlugins1().exec(12,
				syjh + "," + syyh + "," + track2.trim())) {
			memberInfoReturn = (String) RdPlugins.getDefault().getPlugins1()
					.getObject();
			return memberInfoReturn;
		}
		PosLog.getLog(getClass()).info(
				"小票号:" + saleHead.fphm + " " + memberInfoReturn);

		return null;

	}

	public String findMemberNewDHYCard(String track2) {
		/**
		 * 1.调用url获取大会员详细信息 2.将大会员详细信息上传到百货后台 3.生成CustomerDef对象返回
		 */
		String syjh = GlobalInfo.syjDef.syjh;
		String syyh = GlobalInfo.posLogin.gh;
		Wqbh_DHYInterface DHY = new Wqbh_DHYInterface();
		PosLog.getLog(getClass()).info(
				"收银机号:" + syjh + " 收银员号：" + syyh + " 轨道号:" + track2.trim());
		int userNameType = 0;
		if (track2.trim().length() != 11) {// 卡号不是11位就不是手机号
			userNameType = 7;
			//track2=track2.substring(0,15);
		} else
			userNameType = 2;
		String memberInfoInPut = "";
		String memberInfoReturn = "";
		memberInfoInPut = "userName=" + track2 + "&userNameType="
				+ userNameType;
		memberInfoReturn = DHY.MemberLogin(memberInfoInPut);
		if (!memberInfoReturn.equals("") || !memberInfoReturn.equals(null)) {// 如果取到返回值就去解析返回的json数据
			return memberInfoReturn;
		}
		return null;

	}

	// 验证大会员支付密码
	public String chkPWD(String uid, String loginToken) {
		/**
		 * 1.调用url获取大会员详细信息
		 */
		Wqbh_DHYInterface DHY = new Wqbh_DHYInterface();
		String memberInfoInPut = "";
		String memberInfoReturn = "";
		StringBuffer payPwd = new StringBuffer();
		String[] title = { "输入类型" };
		int[] width = { 440 };
		Vector contents = new Vector();
		contents.add(new String[] { "串口键盘输入" });
		contents.add(new String[] { "大会员键盘输入" });

		String pwd = "";
		int choice = new MutiSelectForm().open("请选择输入方式", title, width,
				contents);

		if (choice == 0) {

			ProgressBox pb = null;
			pb = new ProgressBox();
			pb.setText("正在输入卡号和密码,请等待...");
			pwd = new KTL512VWQ().findCard();
			if (pb != null) {
				pb.close();
				pb = null;
			}
		} else if (choice == 1) {
			// 弹出刷卡窗口
			TextBox txt = new TextBox();
			if (!txt.open("请输入大会员支付密码", "PASSWORD", "请将在大会员键盘输入会员卡的注册密码或临时密码", payPwd, 0, 0,
					false, TextBox.MsrRetTracks))
				return null;

			pwd = payPwd.toString();
		}
			memberInfoInPut = "uid=" + uid + "&loginToken=" + loginToken
					+ "&payPwd=" + pwd;
			System.out.println(memberInfoInPut);
			memberInfoReturn = DHY.CheckPayPwd(uid,memberInfoInPut);
			if (!memberInfoReturn.equals("") || !memberInfoReturn.equals(null)) {// 如果取到返回值就去解析返回的json数据
				return memberInfoReturn;
			}
		
		return null;

	}

	public boolean memberGrant() {
		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0 && !memberAfterGoodsMode()
				&& !isNewUseSpecifyTicketBack(false)) {
			new MessageBox("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡");
			return false;
		}

		// // 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		// if (saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		// {
		// new MessageBox("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡");
		// return false;
		// }

		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();

		// 读取会员卡
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;

		// 解析出磁道和选择的类型
		String[] s = track2.split(",");
		track2 = s[0];

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);
		if (cust == null)
			return false;
		if (cust.func.length() > 6 && cust.func.charAt(6) == 'Y') {
			new MessageBox("请使用大会员键刷此卡!");
			return false;
		}

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false)) {
			if (cust.status == null || cust.status.trim().length() <= 0
					|| cust.status.charAt(0) != 'Y') {
				new MessageBox("该顾客卡已失效!");
				return false;
			}
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str6 = "";
			return true;
		} else {
			if (memberGrantFinish(cust)) {
				saleHead.str6 = "";
				return true;
			}
			// 记录会员卡

			return false;
		}
	}

	public void getVIPZK(int index, int type) {
		if (type != this.vipzk2) {
			return;
		}

		char zszflag = 'Y';

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods
				.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
		SpareInfoDef spareInfo = (SpareInfoDef) this.goodsSpare
				.elementAt(index);

		if (goodsDef.poptype != '0') {
			if ((goodsDef.pophyjzkl % 10.0D) >= 1.0D) {
				zszflag = 'Y';
			} else {
				zszflag = 'N';
			}
		}

		if ((checkMemberSale()) && (this.curCustomer != null)
				&& (goodsDef.isvipzk == 'Y')) {
			calcVIPZK(index);

			if ((getZZK(saleGoodsDef) >= 0.01D) && (goodsDef.hyj < 1.0D)) {
				if ((spareInfo.char1 != 'Y')
						|| ((saleGoodsDef.yhzke > 0.0D) && (zszflag != 'Y'))) {
					zszflag = 'Y';
					spareInfo.char1 = 'N';
				}

				if (zszflag == 'Y') {
					double zkl = ManipulatePrecision.doubleConvert(
							(saleGoodsDef.hjje - getZZK(saleGoodsDef))
									/ saleGoodsDef.hjje, 2, 1);

					if (spareInfo.char1 == 'Y') {
						double[] nvalues = { this.curCustomer.value1,
								this.curCustomer.value2,
								this.curCustomer.value3,
								this.curCustomer.value4,
								this.curCustomer.value5 };

						if ((zkl >= nvalues[0]) && (zkl <= nvalues[1])) {
							if ((this.curCustomer.func.length() > 3)
									&& (this.curCustomer.func.charAt(3) == 'Y')) {
								saleGoodsDef.hyzke = ManipulatePrecision
										.doubleConvert(
												(1.0D - nvalues[2])
														* (saleGoodsDef.hjje - getZZK(saleGoodsDef)),
												2, 1);
							}
						} else if (zkl > nvalues[1]) {
							if ((this.curCustomer.func.length() > 4)
									&& (this.curCustomer.func.charAt(4) == 'Y')) {
								if (nvalues[3] == 0.0D) {
									nvalues[3] = goodsDef.hyj;
								}

								zkl = ManipulatePrecision
										.doubleConvert((1.0D - nvalues[3])
												* saleGoodsDef.hjje, 2, 1);

								if (zkl > getZZK(saleGoodsDef)) {
									saleGoodsDef.hyzke = ManipulatePrecision
											.doubleConvert(zkl
													- getZZK(saleGoodsDef), 2,
													1);
								}
							}
						} else if ((zkl < nvalues[0])
								&& (this.curCustomer.func.length() > 5)
								&& (this.curCustomer.func.charAt(5) == 'Y')) {
							saleGoodsDef.hyzke = ManipulatePrecision
									.doubleConvert(
											(1.0D - nvalues[4])
													* (saleGoodsDef.hjje - getZZK(saleGoodsDef)),
											2, 1);
						}

						double zkl1 = ManipulatePrecision
								.doubleConvert((1.0D - goodsDef.hyj)
										* saleGoodsDef.hjje, 2, 1);

						if (zkl1 > getZZK(saleGoodsDef)) {
							saleGoodsDef.hyzke += ManipulatePrecision
									.doubleConvert(zkl1 - getZZK(saleGoodsDef),
											2, 1);
						}
					} else if (goodsDef.hyj < zkl) {
						zkl = ManipulatePrecision
								.doubleConvert((1.0D - goodsDef.hyj)
										* saleGoodsDef.hjje, 2, 1);

						if (zkl > getZZK(saleGoodsDef)) {
							saleGoodsDef.hyzke = ManipulatePrecision
									.doubleConvert(zkl - getZZK(saleGoodsDef),
											2, 1);
						}
					}
				}
			} else {
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(
						(1.0D - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
			}

			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		}

		spareInfo.char1 = 'N';
		getZZK(saleGoodsDef);
	}

	public void calcAllRebate(int index) {
		// char zszflag = 'Y';
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods
				.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
		// SpareInfoDef spareInfo = (SpareInfoDef)
		// this.goodsSpare.elementAt(index);

		if (isSpecifyBack(saleGoodsDef)) {
			return;
		}

		if (SellType.ISBATCH(this.saletype)) {
			return;
		}

		if (SellType.ISEARNEST(this.saletype)) {
			return;
		}

		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) {
			return;
		}

		saleGoodsDef.hyzke = 0.0D;
		saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		saleGoodsDef.yhzke = 0.0D;
		saleGoodsDef.yhzkfd = 0.0D;
		saleGoodsDef.plzke = 0.0D;
		saleGoodsDef.zszke = 0.0D;

		if (goodsDef.poptype == '0') {
			return;
		}

		if ((saleGoodsDef.lsj > 0.0D)
				&& ((goodsDef.poptype == '1') || (goodsDef.poptype == '7'))) {
			if ((saleGoodsDef.lsj > goodsDef.poplsj)
					&& (goodsDef.poplsj > 0.0D)) {
				saleGoodsDef.yhzke = ((saleGoodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl);
				saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
			}
		} else if ((1.0D > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0.0D)) {
			saleGoodsDef.yhzke = (saleGoodsDef.hjje * (1.0D - goodsDef.poplsjzkl));
			saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
		}

		saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(
				saleGoodsDef.yhzke, 2, 1);

		saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
	}

	public void calcVIPZK(int index) {
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods
				.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
		SpareInfoDef spareInfo = (SpareInfoDef) this.goodsSpare
				.elementAt(index);

		if ((!(checkMemberSale())) || (this.curCustomer == null)) {
			return;
		}

		if ((!this.saletype.equals(SellType.RETAIL_SALE))
				&& (!this.saletype.equals(SellType.PREPARE_SALE))) {
			goodsDef.hyj = 1.0D;
			spareInfo.char1 = 'N';

			return;
		}

		GoodsAmountDef VIPZK = new GoodsAmountDef();

		if (DataService.getDefault().findAmountDef(VIPZK, saleGoodsDef.code,
				saleGoodsDef.gz, this.curCustomer.type, 0.0D)) {
			goodsDef.hyj = VIPZK.plhyj;
			spareInfo.char1 = VIPZK.memo.charAt(0);
		} else {
			goodsDef.hyj = this.curCustomer.zkl;
			spareInfo.char1 = 'Y';
		}
	}

	public void sendSecMonitor(String label, String[] value, int index) {
		if (SecMonitor.secMonitor == null)
			return;

		if (label.equalsIgnoreCase("goods")) {
			String line = "";
			line = Convert.appendStringSize("", "商品名:", 0, 7, 90, 0);
			line = Convert.appendStringSize(line, value[2] + "[" + value[1]
					+ "]", 7, 34, 90, 0);

			line = Convert.appendStringSize(line, "数量:", 44, 5, 90, 0);
			line = Convert.appendStringSize(line, value[4], 49, 6, 90, 1);

			line = Convert.appendStringSize(line, "应付:", 56, 5, 90, 0);
			line = Convert.appendStringSize(line, value[7], 61, 9, 90, 1);

			String line1 = "";
			line1 = Convert.appendStringSize(line1, "会员号:", 0, 7, 90, 0);
			line1 = Convert.appendStringSize(line1, getVipInfoLabel(), 7, 34,
					90, 0);

			line1 = Convert.appendStringSize(line1, "总量:", 44, 5, 90, 0);
			line1 = Convert.appendStringSize(line1, getTotalQuantityLabel(),
					49, 8, 90, 1);

			line1 = Convert.appendStringSize(line1, "总付:", 56, 5, 90, 0);
			line1 = Convert.appendStringSize(line1, getSellPayMoneyLabel(), 61,
					9, 90, 1);

			line += ("#@#" + (20 + GlobalVar.secFont) + "#@#0_0_255");
			line1 += ("#@#" + (20 + GlobalVar.secFont) + "#@#255_0_0");

			SecMonitor.secMonitor.monitorShowGoodsInfo(line, line1, index);
		} else if (label.equalsIgnoreCase("pay")
				|| label.equalsIgnoreCase("total")) {
			String line = "";
			line = Convert.appendStringSize(line, "应付金额:", 0, 9, 90, 0);
			line = Convert.appendStringSize(line, getSellPayMoneyLabel(), 10,
					11, 90, 1);
			line = Convert.appendStringSize(line, "已付金额:", 25, 9, 90, 0);
			line = Convert.appendStringSize(line, ManipulatePrecision
					.doubleToString(saleHead.sjfk, 2, 1), 36, 11, 90, 1);
			line += ("#@#" + (28 + GlobalVar.secFont));

			String line1 = "";
			line1 = Convert.appendStringSize(line1, "未付金额:", 0, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, getPayBalanceLabel(), 10,
					11, 90, 1);
			line1 += ("#@#" + (28 + GlobalVar.secFont));

			if (label.equalsIgnoreCase("pay"))
				SecMonitor.secMonitor.monitorShowPayInfo(line, line1);
			else
				SecMonitor.secMonitor.monitorShowTotalInfo(line, line1);
		} else if (label.equalsIgnoreCase("change")) {
			String line = "";
			line = Convert.appendStringSize(line, "应付金额:", 0, 9, 90, 0);
			line = Convert.appendStringSize(line, getSellPayMoneyLabel(), 10,
					11, 90, 1);
			line = Convert.appendStringSize(line, "已付金额:", 25, 9, 90, 0);
			line = Convert.appendStringSize(line, ManipulatePrecision
					.doubleToString(saleHead.sjfk, 2, 1), 36, 11, 90, 1);
			line += ("#@#" + (28 + GlobalVar.secFont));

			String line1 = "";
			line1 = Convert.appendStringSize(line1, "找零金额:", 0, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, ManipulatePrecision
					.doubleToString(saleHead.zl), 10, 11, 90, 1);
			line1 = Convert.appendStringSize(line1, "损益金额:", 25, 9, 90, 0);
			line1 = Convert.appendStringSize(line1, ManipulatePrecision
					.doubleToString(saleHead.fk_sysy), 36, 11, 90, 1);
			line1 += ("#@#" + (28 + GlobalVar.secFont) + "#@#255_0_0");

			SecMonitor.secMonitor.monitorShowChangeInfo(line, line1);
		} else if (label.equalsIgnoreCase("phone")) {
			String line = "";
			line = Convert.appendStringSize(line, "欢迎光临", 21, 9, 90, 0);
			String line1 = "";
			line1 = Convert.appendStringSize(line1, "手机号:", 6, 7, 90, 2);
			line1 = Convert.appendStringSize(line1, value[0], 14, 15, 90, 0);
//			line1 = Convert.appendStringSize(line1, "密码:", 30, 7, 90, 2);
//			line1 = Convert.appendStringSize(line1, value[1], 37, 8, 90, 0);
			line1 += ("#@#" + (28 + GlobalVar.secFont));

			SecMonitor.secMonitor.monitorShowPhoneInfo(line, line1);
		} else if (label.equalsIgnoreCase("DHYJF")){
			String line = "";
			line = Convert.appendStringSize(line, "您本次消费积分抵用可以节约"+value[0]+"元", 0, 60, 60, 0);
			String line1 = "";
			line1 = Convert.appendStringSize(line1, "请问您使用多少积分?", 0, 60,60 , 0);
			SecMonitor.secMonitor.monitorShowPhoneInfo(line, line1);
		}
		else {
			SecMonitor.secMonitor.monitorShowWelcomeInfo();
		}
	}

	// 新CRM满减促销
	public boolean doCrmPop() {
		boolean haveCrmPop = false;

		// 清空，放满减描述
		saleHead.str2 = "";

		// 默认总是不进行分摊付款的
		apportionPay = false;

		// 先总是无满减规则方式的付款
		isPreparePay = payNormal;

		if (!SellType.ISSALE(saletype)) {
			return false;
		}

		if (SellType.NOPOP(saletype))
			return false;

		if (SellType.ISEARNEST(saletype)) {
			return false;
		}

		if (SellType.ISPREPARETAKE(saletype)) {
			return false;
		}

		// 先进行直接打折
		int i = 0;
		double hjzszk = 0;
		for (i = 0; i < saleGoods.size(); i++) {
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(i);
			double zkl = ((GoodsDef) goodsAssistant.elementAt(i)).maxzkl;

			// 不计算换购商品
			if (((SpareInfoDef) goodsSpare.elementAt(i)).char2 == 'Y')
				continue;

			if (mjrule.charAt(0) == '9')
				mjrule = mjrule.substring(1);

			if (mjrule.charAt(0) == '1') {
				double sj = saleGoodsDef.hjje - getZZK(saleGoodsDef);
				double dz = ManipulatePrecision.mul(sj, goodsPop1.poplsjzkl);

				double minje = saleGoodsDef.hjje * zkl;

				if (dz < minje) {
					saleGoodsDef.zszke = ManipulatePrecision.sub(sj, minje);
					saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
					saleGoodsDef.zsdjbh = goodsPop1.djbh;
				} else {
					saleGoodsDef.zszke = ManipulatePrecision.sub(sj, dz);
					saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
					saleGoodsDef.zsdjbh = goodsPop1.djbh;
				}
				// 计算价格精度
				if (saleGoodsDef.zszke > 0)
					saleGoodsDef.zszke = getConvertRebate(i, saleGoodsDef.zszke);

				getZZK(saleGoodsDef);
				hjzszk += saleGoodsDef.zszke;

				haveCrmPop = true;
			}
		}

		if (hjzszk > 0) {
			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();

			new MessageBox("有商品参加活动促销，总共可打折 "
					+ ManipulatePrecision.doubleToString(hjzszk));
		}

		// 在VIP促销需要除券计算模式下，计算VIP前先提示输入券付款
		boolean vippaycw = false;
		if (GlobalInfo.sysPara.vipPayExcp == 'Y' && checkMemberSale()
				&& curCustomer != null && curCustomer.iszk == 'Y'
				&& GlobalInfo.sysPara.vipPromotionCrm != null
				&& GlobalInfo.sysPara.vipPromotionCrm.equals("2")) {
			// 提示先输入券付款
			if (new MessageBox("券付款不参与VIP折扣,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键")
					.verify() != GlobalVar.Exit) {
				// 开始预付除外付款方式
				isPreparePay = payPopPrepare;

				// 打开付款窗口
				new SalePayForm().open(saleEvent.saleBS);

				// 付款完成，开始新交易
				if (this.saleFinish) {
					sellFinishComplete();

					// 预先付款就已足够,不再继续后续付款
					doRulePopExit = true;
					return false; // 表示没有满减促销,取消付款时无需恢复
				}
			}

			// 进入实付剩余付款方式,只允许非券付款方式进行付款
			isPreparePay = payPopOther;

			// 标记已输入除外付款，后面满减时不再输入除外付款
			vippaycw = true;
		}

		// 如果为VIP折扣区间的打折方式，在满减前计算
		if (checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y'
				&& GlobalInfo.sysPara.vipPromotionCrm != null
				&& GlobalInfo.sysPara.vipPromotionCrm.equals("2")) {
			// vipzk2表示按下付款键时才计算VIP折扣
			for (int k = 0; k < saleGoods.size(); k++) {
				getVIPZK(k, vipzk2);
			}

			// 重算小票应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();

			// 显示会员卡折扣总金额
			if (saleHead.hyzke > 0)
				new MessageBox("会员折扣总金额 ：" + saleHead.hyzke);
		}

		// 检查促销折扣控制 如果低于折扣率,不进行满减,返券,返礼促销
		for (int j = 0; j < saleGoods.size(); j++) {
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(j);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(j);
			double zkl = 0;
			if (saleGoodsDef.hjje != 0) {
				zkl = ManipulatePrecision.doubleConvert(
						(saleGoodsDef.hjje - getZZK(saleGoodsDef))
								/ saleGoodsDef.hjje, 2, 1);
			}

			if (zkl < goodsPop1.pophyjzkfd) {

				if (mjrule.charAt(0) == '9') {
					StringBuffer buff = new StringBuffer(mjrule);
					for (int z = 2; z < buff.length(); z++) {
						buff.setCharAt(z, '0');
					}
					mjrule = buff.toString();
				} else {
					mjrule = mjrule.charAt(0) + "000";
				}
				((SpareInfoDef) goodsSpare.elementAt(j)).str1 = mjrule;

				if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
					saleGoodsDef.str3 = mjrule
							+ String.valueOf(Convert.increaseInt(
									goodsPop1.yhspace, 5).substring(4))
							+ saleGoodsDef.str3.substring(saleGoodsDef.str3
									.indexOf(";"));
				else
					saleGoodsDef.str3 = mjrule
							+ saleGoodsDef.str3.substring(saleGoodsDef.str3
									.indexOf(";"));
			}
		}

		// 检查是否需要分摊
		for (int j = 0; j < saleGoods.size(); j++) {
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			if (mjrule.charAt(0) == '9' && mjrule.length() > 3
					&& mjrule.charAt(2) == '1')
				apportionPay = true;
			if (mjrule.charAt(1) == '1')
				apportionPay = true;
			if (apportionPay) {
				break;
			}
		}

		// 再查找是否存在满减或减现
		int j = 0;
		Vector set = new Vector();
		CalcRulePopDef calPop = null;

		// 先按商品分组促销规则
		for (i = 0; i < saleGoods.size(); i++) {
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			GoodsDef goodsDef = ((GoodsDef) goodsAssistant.elementAt(i));
			GoodsPopDef goodsPop = (GoodsPopDef) crmPop.elementAt(i);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			String ruleCode = goodsDef.specinfo;

			if (mjrule.charAt(0) == '9')
				mjrule = mjrule.substring(1);
			// 选择了不参与减现继续下一个商品
			if (mjrule.equals("N") || (mjrule.charAt(1) != '1')) {
				continue;
			}

			// 查找是否相同促销规则
			for (j = 0; j < set.size(); j++) {
				calPop = (CalcRulePopDef) set.elementAt(j);

				int oldIndex = Integer.parseInt((String) calPop.row_set
						.elementAt(0));
				SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods
						.elementAt(oldIndex);
				GoodsDef goodsDef1 = ((GoodsDef) goodsAssistant
						.elementAt(oldIndex));
				GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop
						.elementAt(oldIndex);
				String mjrule1 = ((SpareInfoDef) goodsSpare.elementAt(oldIndex)).str1;

				if (mjrule1.charAt(0) == '9')
					mjrule1 = mjrule1.substring(1);
				// 判断是否为同规则促销
				if (isSamePop(saleGoodsDef, goodsDef, goodsPop, mjrule,
						saleGoodsDef1, goodsDef1, goodsPop1, mjrule1)) {
					calPop.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= set.size()) {
				calPop = new CalcRulePopDef();
				calPop.code = saleGoodsDef.code;
				calPop.gz = saleGoodsDef.gz;
				calPop.uid = saleGoodsDef.uid;
				calPop.rulecode = ruleCode;
				calPop.catid = saleGoodsDef.catid;
				calPop.ppcode = saleGoodsDef.ppcode;
				calPop.popDef = goodsPop;
				calPop.row_set = new Vector();
				calPop.row_set.add(String.valueOf(i));
				set.add(calPop);
			}
		}

		// 无规则促销
		if (set.size() <= 0) {
			return haveCrmPop;
		}

		// 满减前先对所有商品进行舍分处理
		this.calcSellPayMoney(true);

		// 引用促销规则集合，用于付款分摊时进行判断，只有一个规则自动平摊到每个商品
		rulePopSet = set;

		// 检查是否要除券
		boolean havepaycw = false;
		for (i = 0; i < set.size(); i++) {
			calPop = (CalcRulePopDef) set.elementAt(i);

			if (calPop.popDef.catid.equals("Y")) {
				havepaycw = true;
				break;
			}
		}

		// 前面已经进行了VIP除外付款输入,不再输入除外付款
		if (vippaycw)
			havepaycw = false;

		// 循环两次
		// 第一次先检查是否有满足条件的规则,如果没有则直接返回
		// 第二次检查除券外是否还有满足条件的规则,如果不需要除券,则只用循环一次
		int nwhile = 1;
		do {
			// 开始计算商品分组参与计算的合计金额
			for (i = 0; i < set.size(); i++) {
				// 如果是能进入第二次循环,说明有交易金额是满足促销条件的规则促销
				// 如果需要扣除券付款,先输入券付款方式
				if ((nwhile >= 2) && havepaycw) {
					// 提示先输入券付款
					if (GlobalInfo.sysPara.mjPaymentRule.trim().length() > 0
							&& new MessageBox(
									"本笔交易有活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键")
									.verify() != GlobalVar.Exit) {
						// 开始预付除外付款方式
						isPreparePay = payPopPrepare;

						// 打开付款窗口
						new SalePayForm().open(saleEvent.saleBS);

						// 付款完成，开始新交易
						if (this.saleFinish) {
							sellFinishComplete();

							// 预先付款就已足够,不再继续后续付款
							doRulePopExit = true;
							return false;
						}
					}

					// 进入实付剩余付款方式,只允许非券付款方式进行付款
					isPreparePay = payPopOther;

					// 券除外付款只输入一次
					havepaycw = false;
				}

				// 计算同规则商品参与促销的合计
				calPop = (CalcRulePopDef) set.elementAt(i);
				double sphj = 0;
				for (j = 0; j < calPop.row_set.size(); j++) {
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods
							.elementAt(Integer.parseInt((String) calPop.row_set
									.elementAt(j)));
					sphj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje
							- getZZK(saleGoodsDef));
				}

				// 如果只有一组促销规则,计算前存在的付款方式都算需要除外的付款
				// 如果有多个组促销规则,除外金额为该商品已分摊的付款金额
				for (j = 0; j < calPop.row_set.size(); j++) {
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare
							.elementAt(Integer.parseInt((String) calPop.row_set
									.elementAt(j)));
					if (spinfo.payft == null)
						continue;
					for (int n = 0; n < spinfo.payft.size(); n++) {
						String[] s = (String[]) spinfo.payft.elementAt(n);

						if (!calPop.popDef.catid.equals("Y")) {
							String[] pay = GlobalInfo.sysPara.mjPaymentRule
									.split(",");
							int x = 0;
							for (x = 0; x < pay.length; x++) {
								if (s[1].equals(pay[x].trim())) {
									break;
								}
							}

							if (x >= pay.length)
								sphj -= Convert.toDouble(s[3]);
						} else {
							sphj -= Convert.toDouble(s[3]);
						}
					}
				}

				if (sphj <= 0) {
					set.remove(i);
					i--;
					continue;
				}

				// 满减限额
				double limitje = 0;
				if (calPop.popDef.sl <= 0)
					limitje = 99999999;
				else
					limitje = calPop.popDef.sl;

				// 检查是否满足条件
				if (calPop.popDef.gz.equals("1")) // 按金额满减
				{
					double mjje = 0;
					calPop.popje = sphj;

					int num = 0;

					// 已参与满减的金额
					double yfmj = 0;

					if (GlobalInfo.sysPara.mjtype == 'Y') {
						// 检查是否存在促销条件,现在全部的条件都在此地设定 用分号分隔
						if ((calPop.popDef.str3 != null)
								&& (calPop.popDef.str3.trim().length() > 0)) {
							String[] row = calPop.popDef.str3.split(";");

							for (int c = row.length - 1; c >= 0; c--) {
								if ((row[c] == null)
										|| (row[c].split(",").length != 4)) {
									continue;
								}

								double a = Convert
										.toDouble(row[c].split(",")[0]); // 参加下限
								double b = Convert
										.toDouble(row[c].split(",")[1]); // 参加上限
								double t = Convert
										.toDouble(row[c].split(",")[2]); // 满减条件
								double je = Convert
										.toDouble(row[c].split(",")[3]); // 满减金额

								if ((je == 0) || (b == 0)) {
									continue;
								}

								if ((ManipulatePrecision.doubleConvert(sphj
										- yfmj) >= a)
										&& (ManipulatePrecision
												.doubleConvert(sphj - yfmj) <= b)) {
									// 如果满减条件为0，直接取定义的满减金额
									if (t == 0) {
										if (je < limitje) {
											mjje = je;
										} else {
											mjje = limitje;
										}

										break;
									}

									// 浮点运算1 = 0.999999,需要进位到两位小数再取整
									// 浮点运算299/300 =
									// 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
									num = ManipulatePrecision.integerDiv(sphj
											- yfmj, t);

									double bcje = 0;
									if (num > 0) {
										bcje = num * je;
									}

									if (bcje > limitje) {
										bcje = limitje;
									}

									mjje += bcje;
									yfmj = ManipulatePrecision
											.doubleConvert(num * t + yfmj);
									if (mjje >= limitje
											|| (GlobalInfo.sysPara.mjloop == 'N' && yfmj > 0)) {
										break;
									} else {
										continue;
									}
								} else {
									continue;
								}
							}
						}
					} else {
						if (calPop.popDef.poplsj > 0) {
							// 浮点运算1 = 0.999999,需要进位到两位小数再取整
							// 浮点运算299/300 =
							// 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
							num = ManipulatePrecision.integerDiv(sphj - yfmj,
									calPop.popDef.poplsj);
						}

						// 满足促销条件，不超过满减限额，不超过参与打折的金额
						double bcje = num * calPop.popDef.pophyj;
						if (bcje + mjje > limitje)
							bcje = limitje - mjje;
						if (bcje > 0 && (bcje + mjje <= calPop.popje)) {
							mjje += bcje;
							yfmj += num * calPop.popDef.poplsj;
						}

						// 检查是否存在附加促销条件
						// 允许递归计算满减
						if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N') {

						} else if (calPop.popDef.str3 != null
								&& calPop.popDef.str3.trim().length() > 0) {
							String[] row = calPop.popDef.str3.split(";");

							for (int c = 0; c < row.length; c++) {
								if (row[c] == null
										|| row[c].split(",").length != 2)
									continue;

								double a = Convert
										.toDouble(row[c].split(",")[0]); // 满减条件
								double b = Convert
										.toDouble(row[c].split(",")[1]); // 满减金额

								if (a == 0 || b == 0)
									continue;

								// 浮点运算1 = 0.999999,需要进位到两位小数再取整
								// 浮点运算299/300 =
								// 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
								num = ManipulatePrecision.integerDiv(sphj
										- yfmj, a);

								// 满足促销条件，不超过满减限额，不超过参与打折的金额
								bcje = num * b;
								if (bcje + mjje > limitje)
									bcje = limitje - mjje;
								if (bcje > 0 && (bcje + mjje <= calPop.popje)) {
									mjje += bcje;
									yfmj += num * a;
								}

								if (yfmj > 0
										&& GlobalInfo.sysPara.mjloop == 'N') {
									break;
								}
							}
						}
					}

					if (mjje > 0) {
						calPop.mult_Amount = mjje;
					} else {
						set.remove(i);
						i--;
					}
				} else if (calPop.popDef.gz.equals("2")) // 按百分比减现
				{
					// 无效的减现比例
					if ((calPop.popDef.poplsjzkl <= 0)
							|| (calPop.popDef.poplsjzkl >= 1)
							|| (sphj * calPop.popDef.poplsjzkl > limitje)) {
						set.remove(i);
						i--;
					} else {
						calPop.popje = sphj;
					}
				} else {
					set.remove(i);
					i--;
				}
			}

			// 无有效的、满足条件的规则促销
			if (set.size() <= 0) {
				return haveCrmPop;
			}

			// 循环计数,如果不需要除券,则不用进行第二次循环
			nwhile++;
			if (!havepaycw)
				nwhile++;
		} while (nwhile <= 2);

		String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");

		boolean exsit = false;
		for (int jj = 0; jj < salePayment.size(); jj++) {
			SalePayDef spay = (SalePayDef) salePayment.elementAt(jj);
			for (int ii = 0; ii < pay.length; ii++) {

				if (spay.paycode.equals(pay[ii].trim())) {
					exsit = true;
					break;
				}
			}

			if (exsit)
				break;
		}

		// 满减和收券选其一时
		if (!(GlobalInfo.sysPara.ismj == 'Y' && exsit)) {
			// str2记录规则串描述供小票打印
			saleHead.str2 = "";

			// 分摊满减折扣金额
			for (i = 0; i < set.size(); i++) {
				calPop = (CalcRulePopDef) set.elementAt(i);
				double je = 0;
				double hj = 0;

				// 按金额满减
				if (calPop.popDef.gz.equals("1")) {
					je = calPop.mult_Amount;
					je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					String line1 = "";
					for (int x = 0; x < calPop.row_set.size(); x++) {
						line1 += ","
								+ String.valueOf(Convert
										.toInt((String) calPop.row_set
												.elementAt(x)) + 1);
					}

					line1 = line1.substring(1);

					saleHead.str2 += calPop.popDef.kssj + "\n" + "满减："
							+ Convert.increaseChar(String.valueOf(je), 8) + "("
							+ line1 + ")\n";

					// 提示满减规则
					new MessageBox("参加活动的金额为 "
							+ ManipulatePrecision.doubleToString(calPop.popje)
							+ " 元\n\n减现 "
							+ ManipulatePrecision.doubleToString(je) + " 元");
				}

				// 按百分比减现
				if (calPop.popDef.gz.equals("2")) {
					je = calPop.popje * calPop.popDef.poplsjzkl;
					je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					String line1 = "";
					for (int x = 0; x < calPop.row_set.size(); x++) {
						line1 += "," + (String) calPop.row_set.elementAt(x);
					}

					line1 = line1.substring(1);

					saleHead.str2 += calPop.popDef.kssj + "\n" + "满减："
							+ Convert.increaseChar(String.valueOf(je), 8) + "("
							+ line1 + ")\n";

					// 提示满减规则
					new MessageBox(
							"现有促销减现 "
									+ ManipulatePrecision
											.doubleToString(calPop.popDef.poplsjzkl * 100)
									+ "%\n\n你目前可参加活动的金额为 "
									+ ManipulatePrecision
											.doubleToString(calPop.popje)
									+ " 元\n\n你目前可以减现 "
									+ ManipulatePrecision.doubleToString(je)
									+ " 元");
				}

				// 记录规则促销单据信息
				for (j = 0; j < calPop.row_set.size(); j++) {
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods
							.elementAt(Integer.parseInt((String) calPop.row_set
									.elementAt(j)));
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare
							.elementAt(Integer.parseInt((String) calPop.row_set
									.elementAt(j)));
					GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(Integer
							.parseInt((String) calPop.row_set.elementAt(j)));
					hj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje
							- getZZK(saleGoodsDef) - getftje(spinfo));
					saleGoodsDef.zsdjbh = calPop.popDef.djbh;
					saleGoodsDef.zszkfd = popDef.poplsjzkfd;
				}

				// 分摊满减折扣到各商品
				double yfd = 0;
				// int row = -1;
				// double lje = -1;
				for (j = 0; j < calPop.row_set.size(); j++) {
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods
							.elementAt(Integer.parseInt((String) calPop.row_set
									.elementAt(j)));
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare
							.elementAt(Integer.parseInt((String) calPop.row_set
									.elementAt(j)));
					// GoodsPopDef popDef = (GoodsPopDef)
					// crmPop.elementAt(Integer.parseInt((String)
					// calPop.row_set.elementAt(j)));

					// 把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度
					double lszszk = 0;

					// 把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度

					if (j == (calPop.row_set.size() - 1)) {
						lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2,
								1);
						saleGoodsDef.zszke = ManipulatePrecision.doubleConvert(
								saleGoodsDef.zszke + lszszk, 2, 1);
					} else {
						lszszk = ManipulatePrecision
								.doubleConvert(
										(saleGoodsDef.hjje
												- getZZK(saleGoodsDef) - getftje(spinfo))
												/ hj * je, 2, 1);
						double oldzszke = saleGoodsDef.zszke;
						saleGoodsDef.zszke += lszszk;
						saleGoodsDef.zszke = getConvertRebate(
								Integer.parseInt((String) calPop.row_set
										.elementAt(j)), saleGoodsDef.zszke);
						saleGoodsDef.zszke = getConvertRebate(
								Integer.parseInt((String) calPop.row_set
										.elementAt(j)), saleGoodsDef.zszke,
								getGoodsApportionPrecision());
						lszszk = ManipulatePrecision.doubleConvert(
								saleGoodsDef.zszke - oldzszke, 2, 1);
					}
					getZZK(saleGoodsDef);

					// 计算已分摊的金额
					yfd += lszszk;
				}
			}

			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			haveCrmPop = true;
		}
		return haveCrmPop;

	}

	public double calcRefundBalance() {
		SalePayDef paydef = null;
		boolean done = true;
		// String msg = "需要补齐以下券种:\n";
		// String msg1 = "您多扣回以下券种:\n";
		double je = 0;
		// 退券交易必须完全匹配
		if (SellType.ISCOUPON(saletype) && SellType.ISBACK(saletype)) {
			for (int i = 0; i < refundlist.size(); i++) {
				String[] row = (String[]) refundlist.elementAt(i);
				char type = row[0].charAt(0);
				double value = Convert.toDouble(row[2]);

				for (int j = 0; j < refundPayment.size(); j++) {
					paydef = (SalePayDef) refundPayment.elementAt(j);
					if (paydef.idno != null && paydef.idno.length() > 0
							&& paydef.idno.charAt(0) == type) {
						value = value - paydef.ybje;
						paydef.isused = 'Y';
					}
				}

				if (value > 0) {
					// msg +=
					// row[1]+":"+ManipulatePrecision.doubleToString(value)+"\n";
					je += value;
					done = false;
				} else {
					// msg1 +=
					// row[1]+":"+ManipulatePrecision.doubleToString(value*-1)+"\n";
				}
			}

			// 如果金额《=0 判断是否有多余付款，如果存在，及删除
			if (je <= 0) {
				boolean de = true;
				for (int j = 0; j < refundPayment.size(); j++) {
					paydef = (SalePayDef) refundPayment.elementAt(j);
					if (paydef.isused != 'Y') {
						delSaleRefundObject(j);
						j--;
						de = false;
					}
				}

				if (!de) {
					new MessageBox("买券扣回存在多余付款方式，系统已经自动删除");
				}
			}

			return je;
		}

		// 退货扣回时，使用不同券付款，需授权
		if (SellType.ISBACK(saletype)) {
			double sykh = refundTotal; // 记录计算剩余金额
			Vector yfcoupon = null; // 记录已付款券种及金额
			double jfkh = jfje;
			for (int j = 0; j < refundPayment.size(); j++) {
				paydef = (SalePayDef) refundPayment.elementAt(j);
				boolean isGrant = true; // 需要授权
				boolean isdis = true;
				boolean isykh = false;
				for (int i = 0; i < refundlist.size(); i++) {

					sykh = ManipulatePrecision.doubleConvert(sykh, 2, 0);
					String[] row = (String[]) refundlist.elementAt(i);
					char type = row[0].charAt(0);
					double refundvalue = Convert.toDouble(row[2]);
					// 剩余总扣回金额-付款金额<0时提示
					if (sykh - paydef.je < 0) {
						if (isdis) {
							new MessageBox("扣回金额超过总扣回金额!");
							isdis = false;
						}
						delSaleRefundObject(j);
						j--;
						isGrant = false;
						break;
					}
					// 判断券种是否已经记录付款了
					boolean isfk = false;
					double value = refundvalue;
					if ((curGrant.priv.length() > 7 && curGrant.priv.charAt(7) != 'Y')) {
						if (yfcoupon == null)
							yfcoupon = new Vector();
						for (int q = 0; q < yfcoupon.size(); q++) {
							String[] qrow = (String[]) yfcoupon.elementAt(q);
							if (type == qrow[0].charAt(0)) {
								if (refundvalue == Convert.toDouble(qrow[2])) {
									isfk = true;
								} else if (refundvalue > Convert
										.toDouble(qrow[2])) {
									value = refundvalue
											- Convert.toDouble(qrow[2]);
								}
							}
						}
						if (isfk)
							continue;
					}

					// 付款券种相同时
					if ((paydef.idno != null && paydef.idno.length() > 0
							&& paydef.idno.charAt(0) == type && paydef.paycode
							.substring(0, 2).equals("05"))
							&& (curGrant.priv.length() > 7 && curGrant.priv
									.charAt(7) != 'Y')) {
						// 扣回券种金额 小于 付款金额时
						if (value < paydef.ybje) {
							// 如果有积分补现，判断是否为折扣f付款，且付款金额<=原付款金额+积分补现金额
							if (jfkh > 0) {
								if (paydef.payname.indexOf("折扣f") != -1
										&& paydef.ybje <= value + jfkh) {
									yfcoupon.add(row);
									sykh = sykh - paydef.je;
									i--;
									jfkh = value + jfkh - paydef.ybje;
									isGrant = false;
									isykh = true;
									break;
								} else {
									new MessageBox(paydef.payname + "金额"
											+ paydef.je + "大于原返券金额+积分补现"
											+ (value + jfkh));
									// 弹出授权界面
									if ((curGrant.priv.length() > 7 && curGrant.priv
											.charAt(7) == 'Y')
											|| changeBackSalePayOperGrant()) {
										sykh = sykh - paydef.je;
										isGrant = false;
										jfkh = 0;
										isykh = true;
										break;
									} else {
										delSaleRefundObject(j);
										j--;
										isGrant = false;
										isykh = true;
										break;
									}
								}
							}

							new MessageBox(paydef.payname + "金额" + paydef.je
									+ "大于原返券金额" + value);

							// 弹出授权界面
							if ((curGrant.priv.length() > 7 && curGrant.priv
									.charAt(7) == 'Y')
									|| changeBackSalePayOperGrant()) {
								sykh = sykh - paydef.je;
								isGrant = false;
								isykh = true;
								break;
							} else {
								delSaleRefundObject(j);
								j--;
								isGrant = false;
								isykh = true;
								break;
							}
						} else if (value == paydef.ybje) // 扣回券种金额 等于 付款金额时
						{
							yfcoupon.add(row);
							sykh = sykh - paydef.je;
							i--;
							isGrant = false;
							isykh = true;
							break;
						} else {
							yfcoupon.add(new String[] { row[0], row[1],
									String.valueOf(paydef.ybje) });
							sykh = sykh - paydef.je;
							value = value - paydef.ybje;
							isykh = true;
							isGrant = false;
							break;
						}
					} else if ((paydef.idno != null && paydef.idno.length() > 0 && paydef.idno
							.charAt(0) != type)
							&& paydef.paycode.substring(0, 2).equals("05")
							&& (curGrant.priv.length() > 7 && curGrant.priv
									.charAt(7) != 'Y')) {
						// 如果有积分补现，判断是否为折扣f付款，且付款金额<=原付款金额+积分补现金额
						if (jfkh > 0) {
							if (paydef.payname.indexOf("折扣f") != -1
									&& paydef.ybje <= jfkh) {
								// yfcoupon.add(row);
								sykh = sykh - paydef.je;
								i--;
								jfkh = jfkh - paydef.ybje;
								isGrant = false;
								isykh = true;
								break;
							} else {
								new MessageBox(paydef.payname + "金额"
										+ paydef.je + "大于积分补现" + jfkh);
								// 弹出授权界面
								if ((curGrant.priv.length() > 7 && curGrant.priv
										.charAt(7) == 'Y')
										|| changeBackSalePayOperGrant()) {
									sykh = sykh - paydef.je;
									isGrant = false;
									jfkh = 0;
									isykh = true;
									break;
								} else {
									delSaleRefundObject(j);
									j--;
									isGrant = false;
									isykh = true;
									break;
								}
							}
						}

						isGrant = true;
					}// 非券付款时
					else {
						sykh -= paydef.je;
						isGrant = false;
						isykh = true;
						break;
					}
				}

				if (isGrant == true && paydef.idno != null
						&& paydef.idno.length() > 0
						&& paydef.paycode.substring(0, 2).equals("05")) {

					if ((paydef.payname.indexOf("折扣f") != -1 && paydef.ybje <= jfkh)
							|| (curGrant.priv.length() > 7 && curGrant.priv
									.charAt(7) == 'Y')
							|| changeBackSalePayOperGrant()) {
						sykh = sykh - paydef.je;
					} else {
						delSaleRefundObject(j);
						j--;
					}
				} else if ((!paydef.paycode.substring(0, 2).equals("05"))
						&& !isykh) {
					if (isdis && sykh - paydef.je < 0) {
						new MessageBox("扣回金额超过总扣回金额!");
						delSaleRefundObject(j);
						j--;
					} else {
						sykh -= paydef.je;
					}
				}
			}
			/*
			 * if(refundlist.size()==0 && refundTotal>0){ for (int j = 0; j <
			 * refundPayment.size(); j++) { sykh =
			 * ManipulatePrecision.doubleConvert(sykh,2,0); paydef =
			 * (SalePayDef) refundPayment.elementAt(j);
			 * 
			 * //付款券种不同时 if ((paydef.idno != null && paydef.idno.length() > 0)&&
			 * paydef.paycode.substring(0,2).equals("05") &&
			 * (curGrant.priv.length() > 7 && curGrant.priv.charAt(7) != 'Y')) {
			 * if (sykh - paydef.je < 0) { new MessageBox("扣回金额超过总扣回金额!");
			 * delSaleRefundObject(j); j--; } else { if
			 * (changeBackSalePayOperGrant()) { sykh = sykh - paydef.je; } else {
			 * delSaleRefundObject(j); j--; } }
			 *  } //非券付款时 else { if (sykh - paydef.je < 0) { new
			 * MessageBox("扣回金额超过总扣回金额!"); delSaleRefundObject(j); j--; } else {
			 * sykh -= paydef.je; }
			 *  }
			 *  } }
			 */
			sykh = ManipulatePrecision.doubleConvert(sykh, 2, 0);
			return sykh;
		}

		/*
		 * //old if (SellType.ISBACK(saletype)) { double sykh = refundTotal;
		 * //记录计算剩余金额 boolean isok = false; //if(refundPayment.size()>0) sykh =
		 * 0; for (int i = 0; i < refundlist.size(); i++) { String[] row =
		 * (String[]) refundlist.elementAt(i); char type = row[0].charAt(0);
		 * double value = Convert.toDouble(row[2]); //String name = row[1];
		 * 
		 * for (int j = 0; j < refundPayment.size(); j++) { sykh =
		 * ManipulatePrecision.doubleConvert(sykh,2,0); paydef = (SalePayDef)
		 * refundPayment.elementAt(j);
		 * 
		 * //if ((paydef.idno != null && paydef.idno.length() > 0 &&
		 * paydef.idno.charAt(0) == type)|| !paydef.paycode.substring(0,
		 * 2).equals("05")) //付款券种相同时 if ((paydef.idno != null &&
		 * paydef.idno.length() > 0 && paydef.idno.charAt(0) == type &&
		 * paydef.paycode.substring(0,2).equals("05"))) { if (sykh - paydef.je <
		 * 0) { new MessageBox("扣回金额超过总扣回金额!"); delSaleRefundObject(j); j--; }
		 * else { if (value < paydef.ybje) { new
		 * MessageBox(paydef.idno.charAt(0) + "券扣回金额" + paydef.je + "大于原返券金额" +
		 * value); if (changeBackSalePayOperGrant()) {
		 * 
		 * value = value - paydef.ybje; paydef.isused = 'Y'; sykh = sykh -
		 * paydef.je; } else { delSaleRefundObject(j); j--; } } else { value =
		 * value - paydef.ybje; paydef.isused = 'Y'; sykh = sykh - paydef.je;
		 *  } }
		 *  } //付款券种不同时 else if ((paydef.idno != null && paydef.idno.length() >
		 * 0 && paydef.idno.charAt(0) != type)&&
		 * paydef.paycode.substring(0,2).equals("05") && (curGrant.priv.length() >
		 * 7 && curGrant.priv.charAt(7) != 'Y')) { if (sykh - paydef.je < 0) {
		 * new MessageBox("扣回金额超过总扣回金额!"); delSaleRefundObject(j); j--; } else {
		 * if (changeBackSalePayOperGrant()) { value = value - paydef.ybje;
		 * paydef.isused = 'Y'; sykh = sykh - paydef.je; } else {
		 * delSaleRefundObject(j); j--; } }
		 *  } //非券付款时 else { if (sykh - paydef.je < 0) { new
		 * MessageBox("扣回金额超过总扣回金额!"); delSaleRefundObject(j); j--; } else { //
		 * value = value - paydef.je; //paydef.isused = 'Y'; sykh -= paydef.je; }
		 *  } }
		 *  } if(refundlist.size()==0 && refundTotal>0){ for (int j = 0; j <
		 * refundPayment.size(); j++) { sykh =
		 * ManipulatePrecision.doubleConvert(sykh,2,0); paydef = (SalePayDef)
		 * refundPayment.elementAt(j);
		 * 
		 * //付款券种不同时 if ((paydef.idno != null && paydef.idno.length() > 0)&&
		 * paydef.paycode.substring(0,2).equals("05") && (curGrant.priv.length() >
		 * 7 && curGrant.priv.charAt(7) != 'Y')) { if (sykh - paydef.je < 0) {
		 * new MessageBox("扣回金额超过总扣回金额!"); delSaleRefundObject(j); j--; } else {
		 * if (changeBackSalePayOperGrant()) { paydef.isused = 'Y'; sykh = sykh -
		 * paydef.je; } else { delSaleRefundObject(j); j--; } }
		 *  } //非券付款时 else { if (sykh - paydef.je < 0) { new
		 * MessageBox("扣回金额超过总扣回金额!"); delSaleRefundObject(j); j--; } else { //
		 * value = value - paydef.je; //paydef.isused = 'Y'; sykh -= paydef.je; }
		 *  }
		 *  } } sykh = ManipulatePrecision.doubleConvert(sykh,2,0); return sykh;
		 *  }
		 */

		double khje = 0;

		// 计算已输入的扣回
		for (int i = 0; i < refundPayment.size(); i++) {
			paydef = (SalePayDef) refundPayment.elementAt(i);

			khje += paydef.je;
		}

		// 计算扣回余额
		double ye = refundTotal - khje;
		if (ye < 0)
			ye = 0;

		if (!done && ye <= 0) {
			// new MessageBox("请按照提示付足扣券金额");
			return 1;
		}

		return ManipulatePrecision.doubleConvert(ye, 2, 1);
	}

	public boolean doRefundEvent() {
		if (!SellType.ISBACK(saletype))
			return true;

		if (GlobalInfo.sysPara.refundByPos == 'N')
			return true;

		if (!GlobalInfo.isOnline) {
			if (isNewUseSpecifyTicketBack()) {
				new MessageBox("必须在联网状态下检查退货扣回！");
				return false;
			} else {
				return true;
			}
		}

		double dhyjf = 0;
		String jf ="";
		if (saleHead.str6.length()>0){
	      jf = findDHYJF();
		  if (jf != null && !jf.trim().equals("")) {
			dhyjf = Double.parseDouble(jf.trim());
		  }
		}
		if ((saleHead.num1 == 1)) {
			boolean b = false;
			while ((saleHead.str6 == null || saleHead.str6.trim().equals(""))) {
				// 读取会员卡
				HykInfoQueryBS bs = CustomLocalize.getDefault()
						.createHykInfoQueryBS();
				saleHead.str6 = bs.readMemberCard();
				if (saleHead.str6 == null || saleHead.str6.trim().equals("")) {
					if (new MessageBox("本笔小票有大会员刷卡,必须刷大会员卡进行退货!\n是否继续刷大会员？",
							null, true).verify() != GlobalVar.Key2) {
						continue;
					} else {
						PosLog.getLog(getClass()).info(
								"小票号:" + saleHead.fphm + " 收银员选择不刷大会员卡");
						break;
					}
				}
			    jf = findDHYJF();
				if (jf != null && !jf.trim().equals("")) {
					dhyjf = Double.parseDouble(jf.trim());
				}
				if (saleHead.str6 == null || saleHead.str6.trim().equals("")
						|| jf.trim().equals("")) {
					if (new MessageBox("本笔小票有大会员刷卡，查询大会员积分失败！\n是否继续查询会员积分？",
							null, true).verify() == GlobalVar.Key2) {
						saleHead.str6 = "";
						b = true;
						break;
					}
				}
			}
			if (b)
				return false;
		}

		// isRefundPayStatus = true;
		// String ss = null;
		// if (ss.equals("AA")) return true;

		// 清除扣回付款集合
		if (refundPayment == null)
			refundPayment = new Vector();
		else
			refundPayment.clear();
		if (refundAssistant == null)
			refundAssistant = new Vector();
		else
			refundAssistant.clear();

		// 获取需要扣回的金额
		ProgressBox pb = new ProgressBox();
		char bc = saleHead.bc;
		try {
			saleHead.bc = '#';
			// 发送当前退货小票到后台数据库
			pb.setText("正在发送退货小票用于计算扣回金额......");
			if (!this.saleEvent.saleBS.saleSummary()) {
				new MessageBox("交易数据汇总失败!");

				return false;
			}
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods,
					salePayment)) {
				new MessageBox("交易数据校验错误!");

				return false;
			}

			// 发送当前退货小票以计算扣回
			// jdfhdd标记当前发送的是用于计算扣回的小票信息
			String oldfhdd = saleHead.jdfhdd;
			saleHead.jdfhdd = "KHINV";
			if (GlobalInfo.sysPara.refundByPos == 'B') {
				if (DataService.getDefault().doRefundExtendSaleData(saleHead,
						saleGoods, salePayment, null) != 0) {
					saleHead.jdfhdd = oldfhdd;
					return false;
				}
			} else {
				// = 'Y',扣回在付款前进行处理，生成缺省付款便于发送小票
				Vector tempPay = new Vector();
				SalePayDef tempsp = new SalePayDef();
				tempsp.syjh = saleHead.syjh;
				tempsp.fphm = saleHead.fphm;
				tempsp.rowno = 1;
				tempsp.flag = '1';
				tempsp.paycode = "KHFK";
				tempsp.payname = "扣回虚拟付款";
				tempsp.ybje = saleHead.ysje;
				tempsp.hl = 1;
				tempsp.je = saleHead.ysje;
				tempPay.add(tempsp);
				if (DataService.getDefault().doRefundExtendSaleData(saleHead,
						saleGoods, tempPay, null) != 0) {
					saleHead.jdfhdd = oldfhdd;
					return false;
				}
			}

			saleHead.jdfhdd = oldfhdd;

			// 调用后台过程返回需要扣回的金额
			pb.setText("正在获取退货小票的扣回金额......");
			RefundMoneyDef rmd = new RefundMoneyDef();
			if ((saleHead.num1 == 1)) {
				Wqbh_NetService wn = new Wqbh_NetService();
				//
				if (!wn.getDHYRefundMoney(saleHead.mkt, saleHead.syjh,
						saleHead.fphm, dhyjf, rmd)) {
					return false;
				}
				// saleHead.num6 = rmd.jfkhje + rmd.fqkhje + rmd.qtkhje;
			} else {
				if (!NetService.getDefault().getRefundMoney(saleHead.mkt,
						saleHead.syjh, saleHead.fphm, rmd)) {
					return false;
				}

			}

			// 关闭提示
			if (pb != null) {
				pb.close();
				pb = null;
			}

			// 存在家电下乡返款扣回，不允许退货
			if (rmd.jdxxfkje > 0) {
				new MessageBox("该退货小票存在家电下乡返款\n请退返款之后再进行退货交易");
				return false;
			}
			// rmd.jfkhje = 0.5;
			// 无扣回金额,不用输入
			refundTotal = rmd.jfkhje + rmd.fqkhje + rmd.qtkhje;
			jfje = rmd.jfkhje;
			// 员工缴费和结算单如果存在扣回，不允许通过
			if ((SellType.isJF(saletype) || SellType.isJS(saletype))
					&& Math.abs(refundTotal) > 0) {
				new MessageBox("员工缴费 或 结算单 不允许存在扣回\n");
				return false;
			}

			// liwj test
			/* refundTotal = 1; */
			if (refundTotal <= 0)
				return true;

			StringBuffer s = new StringBuffer();
			s.append("该退货小票总共需要扣回 "
					+ ManipulatePrecision.doubleToString(refundTotal)
					+ " 元\n\n");
			if (SellType.ISBACK(saletype)) {
				if (refundlist == null)
					refundlist = new Vector();
				else
					refundlist.removeAllElements();
				// rmd.qtdesc = "A,A券,116";
				String[] rows = rmd.qtdesc.split("\\|");

				for (int i = 0; i < rows.length; i++) {
					if (!(rows[i].length() > 0))
						continue;
					String row[] = rows[i].split(",");
					refundlist.add(row);
					s.append(Convert.appendStringSize("", row[1], 0, 15, 10)
							+ " :" + Convert.increaseCharForward(row[2], 10)
							+ "\n");
				}
				if (rmd.jfkhje > 0)
					s.append(Convert.appendStringSize("", "积分补现", 0, 15, 10)
							+ " :"
							+ Convert.increaseCharForward(String
									.valueOf(rmd.jfkhje), 10) + "\n");
			} else {
				if (rmd.jfdesc.length() > 0)
					s.append(rmd.jfdesc + "\n");
				else if (rmd.jfkhje > 0)
					s.append("其中因为积分原因需扣回 "
							+ ManipulatePrecision.doubleToString(rmd.jfkhje)
							+ " 元\n");
				if (rmd.fqdesc.length() > 0)
					s.append(rmd.fqdesc + "\n");
				else if (rmd.fqkhje > 0)
					s.append("其中因为返券原因需扣回 "
							+ ManipulatePrecision.doubleToString(rmd.fqkhje)
							+ " 元\n");
				if (rmd.qtdesc.length() > 0)
					s.append(rmd.qtdesc + "\n");
				else if (rmd.qtkhje > 0)
					s.append("其中因为其他原因需扣回 "
							+ ManipulatePrecision.doubleToString(rmd.qtkhje)
							+ " 元\n");
			}
			// 有扣回不允许退货
			if (GlobalInfo.sysPara.refundAllowBack != 'Y' && refundTotal > 0) {
				s.append("\n扣回金额大于0,不能进行退货\n");
				refundMessageBox(s.toString());

				return false;
			}

			refundMessageBox(s.toString());
		} catch (Exception er) {
			er.printStackTrace();
		} finally {
			saleHead.bc = bc;
			if (pb != null) {
				pb.close();
				pb = null;
			}
		}

		// 标记扣回开始
		refundFinish = false;
		isRefundPayStatus = true;

		// 打开扣回付款输入窗口
		new SalePayForm().open(saleEvent.saleBS, true);

		isRefundPayStatus = false;
		return refundFinish;
	}

	public String findDHYJF() {
		return findDHYJF(saleHead.str6);
	}

	public String findDHYJF(String track) {
		String result="";
		if (GlobalInfo.sysPara.isNEWDHY.equals("Y")){//参数开启走新会员流程
			result= findMemberNewDHYCard(track);
			JSONObject js = JSONObject.parseObject(result);
			if (js.getString("status").equals("0")) {// 返回成功
				String data = js.getString("data");
				JSONObject jsdata = JSONObject.parseObject(data);
				String member = jsdata.getString("member");
				JSONObject jsmember = JSONObject.parseObject(member);
				if(saleHead!=null){
				saleHead.str8=jsmember.getString("uid");
				saleHead.str1 = jsdata.getString("loginToken");
				}
				GlobalInfo.sysPara.commMerchantId=jsmember.getString("uid");
				String validBonus = jsmember.getString("avlPoint");
			return validBonus;
			}
		}else
		    result = findMemberDHYCard(track);
		if (result != null && result.length() > 0) {
			return result.substring(33, 45).trim();
		}
		return null;
	}

	public boolean checkIsSalePay(String code) {
		if (!SellType.ISBACK(saletype))
			return false;
		// 付款代码为两位 说明是父级
		if (code.length() == 2 && !code.equals("01"))
			return false;
		// if ((curGrant.priv.length() > 7 && curGrant.priv.charAt(7) == 'Y') ||
		// code.substring(0, 2).equals("03")) { return false; }
		if ((curGrant.priv.length() > 7 && curGrant.priv.charAt(7) == 'Y')) {
			return false;
		}

		boolean b = true;
		for (int i = 0; i < backPayment.size(); i++) {
			SalePayDef spd = (SalePayDef) backPayment.elementAt(i);
			// 如果code为03开头 则判断原付款方式有03开头就可以
			if (code.substring(0, 2).equals("03")) {
				if (spd.paycode.trim().substring(0, 2).equals("03"))
					return false;
			}

			if (spd.paycode.trim().equals(code.trim())) {
				b = false;
			}
		}
		if (b && (curGrant.priv.length() > 7 && curGrant.priv.charAt(7) != 'Y')) {
			if (changeBackSalePayOperGrant()) {
				b = false;
			}
		}
		return b;
	}

	public void addSalePayObject(SalePayDef spay, Payment payobj) {
		// true 需要授权 false 不需要授权
		boolean b = true;
		if (SellType.ISBACK(saletype) && backPayment != null
				&& backPayment.size() > 0 && spay.payname.indexOf("扣回") == -1) {
			// 没有权限时，需检查付款方式是否匹配
			if (curGrant.priv.length() > 7 && curGrant.priv.charAt(7) != 'Y') {
				/*
				 * //原小票若有银联付款方式，则直接过 boolean haveBank = false; for (int i = 0;
				 * i < backPayment.size(); i++) { SalePayDef spd = (SalePayDef)
				 * backPayment.elementAt(i);
				 * 
				 * if (spd.paycode.substring(0, 2).equals("03")) {
				 * //true表示有银联付款方式，可以不授权，直接付款 haveBank = true; b = false; break; }
				 *  }
				 */
				// 如果原交易不含银联付款方式则检查付款是否匹配
				// if (!haveBank)
				// {
				String paycode = payobj.salepay.paycode;
				double payje = payobj.salepay.je;
				Vector indexV = new Vector();
				double total = 0;

				for (int i = 0; i < backPayment.size(); i++) {
					SalePayDef spd = (SalePayDef) backPayment.elementAt(i);
					// spd.num3用于记录此付款方式已退的金额
					if (spd.num3 == spd.je)
						continue;
					// 付款方式匹配 则进一步判断是否为券付款
					if (spd.paycode.equals(paycode)) {
						// 如果是券付款，需要匹配券种是否一致
						if (spay.idno != null && spay.idno.length() > 0
								&& paycode.substring(0, 2).equals("05")) {

							if (spd.idno.charAt(0) == spay.idno.charAt(0)) {
								total = total + (spd.je - spd.num3);
								indexV.add(String.valueOf(i));
							}
						} else {
							// 如果不是券付款，则加入序号
							total = total + (spd.je - spd.num3);
							indexV.add(String.valueOf(i));
						}
					} else if (spd.paycode.substring(0, 2).equals("03")
							&& paycode.substring(0, 2).equals("03")) {
						total = total + (spd.je - spd.num3);
						indexV.add(String.valueOf(i));
					}
				}

				if (total > 0 && payje > total) {
					new MessageBox(payobj.salepay.payname + "退款金额" + payje
							+ "大于原付款方式金额" + total);
				} else if (total > 0 && indexV.size() > 0) {
					double syje = payje;
					// 如果indexV大于0，则有未标记付款，且付款方式相同的，需判断金额是否匹配
					for (int t = 0; t < indexV.size(); t++) {
						if (syje <= 0)
							break;
						String index = (String) indexV.elementAt(t);
						SalePayDef spd1 = (SalePayDef) backPayment
								.elementAt(Integer.parseInt(index));
						if (spd1.je >= syje) {
							spd1.num3 = spd1.num3 + syje;
							syje = 0;
						} else {
							spd1.num3 = spd1.num3 + spd1.je;
							syje = syje - spd1.je;
						}
					}
					if (syje == 0) {
						b = false;
					}
				} else {

					new MessageBox(payobj.salepay.payname + "退款方式已足够" + "\n"
							+ "或原付款不存在此付款,继续付款需授权!");

				}

				/*
				 * 
				 * for (int i = 0; i < backPayment.size(); i++) { SalePayDef spd =
				 * (SalePayDef) backPayment.elementAt(i);
				 * //spd.num3用于记录此付款方式已退的金额 if(spd.num3 == spd.je) continue;
				 * //付款方式匹配 且金额小于原付款 则进一步判断是否需要授权 if
				 * (spd.paycode.equals(payobj.salepay.paycode) && spd.je >=
				 * payobj.salepay.je) { //如果是券交易，需要匹配券种是否一致
				 * if(spay.paycode.substring(0,2).equals("05")){
				 * if(spd.idno.charAt(0) == spay.idno.charAt(0)){
				 *  } }else{ if(spd.num3 + payobj.salepay.je <=spd.je ){
				 * spd.num3 =spd.num3 + payobj.salepay.je; b = false; break; }
				 *  }
				 * 
				 * 
				 * }else if(spd.paycode.equals(payobj.salepay.paycode) && spd.je <
				 * payobj.salepay.je){ new
				 * MessageBox(payobj.salepay.payname+"退款金额"+payobj.salepay.je+"大于原付款金额"+spd.je); } }
				 */
				if (b) {
					changeBackSalePayOperGrant();

				}
				// }

			}
			if (b
					&& (SellType.ISBACK(saletype) && (curGrant.priv.length() > 7 && curGrant.priv
							.charAt(7) != 'Y'))) {
				if (spay.paycode.equals("0610")) {
					boolean dhyisok = false;
					while (!dhyisok) {
						PosLog.getLog(getClass()).info(
								"该笔交易金额与原交易金额不符，进行撤回大会员付款  小票号:"
										+ saleHead.fphm + " 卡轨道号:" + spay.str2
										+ " 付款金额:" + spay.je + " 卡余额:"
										+ spay.kye);
						if (!payobj.cancelPay()) {
							if (new MessageBox(Language
									.apply("撤回大会员付款失败,是否重试?"), null, true)
									.verify() == GlobalVar.Key2) {
								PosLog.getLog(getClass()).info(
										"该笔交易撤回不成功  小票号:" + saleHead.fphm
												+ " 卡轨道号:" + spay.str2
												+ " 付款金额:" + spay.je + " 卡余额:"
												+ spay.kye);

								break;
							}
						} else {
							PosLog.getLog(getClass()).info(
									"该笔交易撤回成功  小票号:" + saleHead.fphm + " 卡轨道号:"
											+ spay.str2 + " 付款金额:" + spay.je
											+ " 卡余额:" + spay.kye);

							break;
						}

					}

				}
				return;
			}
		}

		// 标记本行付款唯一序号,用于删除对应商品的分摊
		if (spay != null)
			spay.num5 = salePayUnique++;

		// 加入付款明细
		salePayment.add(spay);
		payAssistant.add(payobj);

		// 找零付款方式不计算损益
		// 付款金额已足够,计算付款损溢
		if (spay.flag != '2' && saleEvent.saleBS.calcPayBalance() <= 0) {
			// 先计算可找零金额
			PaymentChange pc = CreatePayment.getDefault().getPaymentChange(
					saleEvent.saleBS);
			StringBuffer buff = new StringBuffer();
			pc.calcPreChange(buff);
			double zl = Convert.toDouble(buff.toString());

			// 实际付款 - 找零 超过应付时，本笔付款产生了损溢，记入该付款方式溢余
			if (ManipulatePrecision.doubleConvert(saleHead.sjfk - saleyfje
					- salezlexception - zl) > 0) {
				spay.num1 = ManipulatePrecision.doubleConvert(spay.num1
						+ (saleHead.sjfk - saleyfje - salezlexception - zl));
			}
		}

		// 再分摊付款到商品明细
		paymentApportion(spay, payobj);
	}

	// 人员授权
	public boolean changeBackSalePayOperGrant() {
		OperUserDef staff = DataService.getDefault()
				.personGrant("修改退货付款方式权限授权");

		if (staff == null)
			return false;
		// tu测试
		// staff.priv = "NNNNNNNY";
		if (!(staff.priv.length() > 7 && staff.priv.charAt(7) == 'Y')) {
			new MessageBox("该工号没有权限!");
			return false;
		}

		// 设置本笔交易授权
		curGrant.privth = staff.privth;
		curGrant.privqx = staff.privqx;
		curGrant.privdy = staff.privdy;
		curGrant.privgj = staff.privgj;
		curGrant.priv = staff.priv;
		curGrant.dpzkl = staff.dpzkl;
		curGrant.zpzkl = staff.zpzkl;
		curGrant.thxe = staff.thxe;
		curGrant.privje1 = staff.privje1;
		curGrant.privje2 = staff.privje2;
		curGrant.privje3 = staff.privje3;
		curGrant.privje4 = staff.privje4;
		curGrant.privje5 = staff.privje5;
		curGrant.grantgz = staff.grantgz;

		// 设置当前授权卡为员工卡
		cursqkh = staff.gh;
		cursqktype = '1';
		cursqkzkfd = staff.privje1;

		// 设置本笔小票员工授权卡号
		saleHead.ghsq = cursqkh;

		// 提示
		if (GlobalInfo.sysPara.grtpwdshow == 'Y')
			new MessageBox("员工卡授权本笔交易成功");
		else
			new MessageBox("员工卡[" + cursqkh + "]授权本笔交易");

		// 当前为退货交易，记录退货授权
		if (SellType.ISBACK(saletype)) {
			// saleHead.thsq = saleHead.thsq+","+cursqkh;
			// 退货,扣回金额,支付方式
			if (saleHead.thsq.indexOf(",") != -1) {
				String s[] = saleHead.thsq.split(",");
				if (s.length > 2) {
					saleHead.thsq = s[0] + "," + s[1] + "," + staff.gh;
				} else {
					saleHead.thsq = saleHead.thsq + "," + staff.gh;
				}
			} else {
				saleHead.thsq = saleHead.thsq + ",," + staff.gh;
			}

			// new MessageBox("授权退货,限额为 " +
			// ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
		}
		String log = "授权修改退款付款方式, 小票号:" + saleHead.fphm + "  授权工号:" + staff.gh;
		AccessDayDB.getDefault().writeWorkLog(log);
		return true;
	}

	public void delSalePayObject(int index) {
		// 先删除商品的付款分摊
		paymentApportionDelete(index);

		if (SellType.ISBACK(saletype) && backPayment != null
				&& backPayment.size() > 0) {
			// 退款方式
			SalePayDef spd = (SalePayDef) salePayment.elementAt(index);
			String paycode = spd.paycode;
			double syje = spd.je;

			for (int i = 0; i < backPayment.size(); i++) {
				// 原付款方式
				SalePayDef spd1 = (SalePayDef) backPayment.elementAt(i);
				// 付款代码一样
				if (paycode.equals(spd1.paycode)
						|| (paycode.substring(0, 2).equals("03") && spd1.paycode
								.substring(0, 2).equals("03")))
				// if (paycode.equals(spd1.paycode))
				{
					if (syje <= 0)
						break;
					// 付款代码为券
					if (spd.idno != null && spd.idno.length() > 0
							&& paycode.substring(0, 2).equals("05")) {
						if (spd.idno.charAt(0) == spd1.idno.charAt(0)) {
							if (spd1.je >= syje) {
								spd1.num3 = spd1.num3 - syje;
								syje = 0;
							} else {
								spd1.num3 = spd1.num3 - spd1.je;
								syje = syje - spd1.je;
							}
						}
					} else {
						if (spd1.num3 >= syje) {
							spd1.num3 = spd1.num3 - syje;
							syje = 0;
						} else {
							spd1.num3 = 0;
							syje = syje - spd1.num3;
						}
					}
				}
			}
		}

		// 删除付款明细
		salePayment.removeElementAt(index);
		payAssistant.removeElementAt(index);
	}

	// 删除商品
	public boolean deleteGoods(int index) {
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if (saleGoodsDef.str9 != null && saleGoodsDef.str9.length() > 0) {
			if (!GlobalInfo.sysPara.isDel.equals("Y")) {
				new MessageBox("不允许删除电子开票商品！");
				return false;
			}
		}
		return super.deleteGoods(index);
	}

	// 输入折扣
	public boolean inputRebate(int index) {
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if (saleGoodsDef.str9 != null) {
			if (saleGoodsDef.str9.length() > 0) {
				new MessageBox("专柜商品不允许折扣！");
				return false;
			}
		}
		return super.inputRebate(index);
	}

	// 输入折让金额
	public boolean inputRebatePrice(int index) {
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if (saleGoodsDef.str9 != null) {
			if (saleGoodsDef.str9.length() > 0) {
				new MessageBox("专柜商品不允许折让！");
				return false;
			}
		}
		return super.inputRebatePrice(index);
	}

	// 输入数量
	public boolean inputQuantity(int index, double quantity) {
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if (saleGoodsDef.str9 != null) {
			if (saleGoodsDef.str9.length() > 0) {
				new MessageBox("专柜商品不允许修改数量！");
				return false;
			}
		}
		return super.inputQuantity(index, quantity);
	}

	public boolean allowEditGoods() {
		// 前台售卡时不允许修改商品
		if (saletype.equals(SellType.CARD_SALE)) {
			new MessageBox("售卡不允许修改交易信息");
			return false;
		}

		// 团购时不允许修改商品
		if (saletype.equals(SellType.GROUPBUY_SALE) && !Groupbuy_Change()) {
			new MessageBox("团购不允许修改交易信息");
			return false;
		}

		// 会员卡必须在商品输完后刷,那么刷卡以后不能修改商品
		if (GlobalInfo.sysPara.customvsgoods == 'B' && checkMemberSale()) {
			new MessageBox("已刷VIP卡,不能再修改商品\n\n请付款或取消VIP卡后再输入");
			return false;
		}

		if (isPreTakeStatus()) {
			new MessageBox("预售提货状态下不允许修改商品状态");
			return false;
		}

		if ((SellType.PREPARE_BACK.equals(this.saletype))) {
			new MessageBox("预售退货状态下不允许修改商品状态");
			return false;
		}

		// 已经积分换购了的商品不允许进行修改,只能删除
		if (saleEvent.table.getSelectionIndex() >= 0
				&& goodsSpare.size() > saleEvent.table.getSelectionIndex()) {
			SpareInfoDef info = (SpareInfoDef) goodsSpare
					.elementAt(saleEvent.table.getSelectionIndex());
			if (info != null && info.char2 == 'Y') {
				new MessageBox("当前商品是已进行积分换购,不允许修改\n\n请删除后重新输入");
				return false;
			}
		}
		if (saleGoods.size() > 0) {
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(0);
			if (saleGoodsDef.str9 != null) {
				if (saleGoodsDef.str9.length() > 0) {
					new MessageBox("专柜商品不允许折扣！");
					return false;
				}
			}
		}

		return true;
	}

	public void enterInputCODE() {

		if (isPreTakeStatus()) {
			new MessageBox("预售提货状态下不允许修改商品状态");
			return;
		}

		if (GlobalInfo.sysPara.EBillandSgd.equals("N") && saleGoods.size() > 0) {
			SaleGoodsDef sg = ((SaleGoodsDef) saleGoods.elementAt(0));
			if (sg.str9 != null && sg.str9.trim().length() > 0) {
				new MessageBox("已调取电子单据,不允许录入手工单!");
				return;
			}
		}

		boolean findok = false;

		if (saleEvent.code.getText().trim().length() > 30) {
			new MessageBox("非合法的商品编码不允许进行销售\n当前编码长度"
					+ saleEvent.code.getText().length());
			saleEvent.code.selectAll();
			return;
		}

		// 盘点
		if (SellType.ISCHECKINPUT(saletype)) {
			String code = saleEvent.code.getText().trim();

			if (code.length() <= 0 && saleGoods.size() > 0) {

				code = ((SaleGoodsDef) saleGoods
						.elementAt(saleGoods.size() - 1)).barcode;
			}

			if (code.length() > 0
					&& findCheckGoods(code, saleEvent.yyyh.getText(),
							getGzCode(saleEvent.gz.getText()))) {
				findok = true;
			}
		} else if (SellType.ISCOUPON(saletype)) {
			// 买券
			if (findCoupon(saleEvent.code.getText(), saleEvent.yyyh.getText(),
					getGzCode(saleEvent.gz.getText()))) {
				findok = true;
			}
		} else if (SellType.isJS(saletype)) {
			// 缴费
			if (findJSDetail(saleEvent.code.getText(),
					saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText()))) {
				findok = true;
			}
		} else if (SellType.isJF(saletype)) {
			// 结算
			if (findJFDetail(saleEvent.code.getText(),
					saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText()))) {
				findok = true;
			}
		} else {
			// 超市或开发模式直接按回车 = 扫描上一个商品
			String code = saleEvent.code.getText().trim();
			// if (code.length() <= 0)
			// {
			// GoodsSearchForm window = new GoodsSearchForm();
			// window.open();
			// }

			if ((GlobalInfo.sysPara.quickinputsku == 'Y'
					&& saleEvent.yyyh.getText().trim().equals("超市") || ConfigClass
					.isDeveloperMode())
					&& code.length() <= 0 && saleGoods.size() > 0) {
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(saleGoods
						.size() - 1);
				if (sg.inputbarcode != null && sg.inputbarcode.length() > 0)
					code = sg.inputbarcode;
				else
					code = sg.barcode;
			}

			if (code.length() > 0
					&& findGoods(code, saleEvent.yyyh.getText(),
							getGzCode(saleEvent.gz.getText()))) {
				findok = true;
			}
		}

		// 清除输入框
		if (findok) {
			refreshSaleForm();
			// new MessageBox(saleEvent.code.getText());
			doShowInfoFinish();
		} else {
			saleEvent.code.selectAll();
		}
	}

	public boolean checkKh() {
		boolean done = false;
		try {
			if (SellType.ISCOUPON(saletype) && SellType.ISBACK(saletype)) {
				StringBuffer buff = new StringBuffer();
				for (int i = 0; i < refundlist.size(); i++) {
					String[] row = (String[]) refundlist.elementAt(i);
					buff.append(Convert.appendStringSize("", row[1], 0, 15, 10)
							+ " :" + Convert.increaseCharForward(row[2], 10)
							+ "\n");
				}

				buff.append("退券交易不能使用议价权");
				new MessageBox(buff.toString());
				return done;
			}

			if (GlobalInfo.posLogin.priv.trim().length() > 3
					&& GlobalInfo.posLogin.priv.trim().charAt(3) == 'Y') {
				done = true;
				return done;
			}
			// 收银机付款精度截断后进行比较，判断是否需要授权
			if (getDetailOverFlow(calcRefundBalance(),
					GlobalInfo.sysPara.refundScale) <= 0) {
				done = true;
				return done;
			}

			OperUserDef staff = DataService.getDefault().personGrant("扣回权限授权");
			if (staff == null)
				return done;

			if (staff.priv.trim().length() > 3
					&& staff.priv.trim().charAt(3) == 'Y') {
				// 设置本笔交易授权
				curGrant.privth = staff.privth;
				curGrant.privqx = staff.privqx;
				curGrant.privdy = staff.privdy;
				curGrant.privgj = staff.privgj;
				curGrant.priv = staff.priv;
				curGrant.dpzkl = staff.dpzkl;
				curGrant.zpzkl = staff.zpzkl;
				curGrant.thxe = staff.thxe;
				curGrant.privje1 = staff.privje1;
				curGrant.privje2 = staff.privje2;
				curGrant.privje3 = staff.privje3;
				curGrant.privje4 = staff.privje4;
				curGrant.privje5 = staff.privje5;
				curGrant.grantgz = staff.grantgz;

				// 设置当前授权卡为员工卡
				cursqkh = staff.gh;
				cursqktype = '1';
				cursqkzkfd = staff.privje1;

				// 设置本笔小票员工授权卡号
				saleHead.ghsq = cursqkh;

				// 退货,扣回金额,支付方式
				if (saleHead.thsq.indexOf(",") != -1) {
					String s[] = saleHead.thsq.split(",");
					if (s.length > 2) {
						saleHead.thsq = s[0] + "," + staff.gh + "," + s[2];
					} else {
						saleHead.thsq = s[0] + "," + staff.gh;
					}
				} else {
					saleHead.thsq = saleHead.thsq + "," + staff.gh;
				}
				String log = "授权修改扣回金额, 小票号:" + saleHead.fphm + "  授权工号:"
						+ staff.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
				done = true;
				return done;
			} else {
				new MessageBox("此工号没有扣回权限,必须付全扣回金额");
				return done;
			}
		} catch (Exception er) {
			er.printStackTrace();
			return done;
		} finally {
			if (done) {
				// 检查是否一个付款方式都没有输入
				if (refundPayment.size() <= 0) {
					if (new MessageBox("没有输入任何扣回，是否继续", null, true).verify() == GlobalVar.Key1) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
	}

	public void takeBackTicketInfo(SaleHeadDef thsaleHead, Vector thsaleGoods,
			Vector thsalePayment) {
		super.takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);
		saleHead.num1 = thsaleHead.num1;
	}

	public static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		for (int i = begin; i < begin + count; i++)
			bs[i - begin] = src[i];
		return bs;
	}

	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm) {
		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab)
				waitlab = true;
			else
				return false;

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox(Language.apply("小票附加信息输入失败,不能完成交易!"));
				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在汇总交易数据,请等待....."));
			if (!saleSummary())
			{
				new MessageBox(Language.apply("交易数据汇总失败!"));

				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在校验数据平衡,请等待....."));
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox(Language.apply("交易数据校验错误!"));

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, Language.apply("正在输入客户信息,请等待......"));
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, Language.apply("正在打开钱箱,请等待....."));
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, Language.apply("正在记账付款数据,请等待....."));
				if (!saleCollectAccountPay())
				{
					new MessageBox(Language.apply("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!"));

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				setSaleFinishHint(status, Language.apply("正在写入交易数据,请等待......"));
				saleHead.str1="";
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox(Language.apply("交易数据写盘失败!"));
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, Language.apply("正在清除断点保护数据,请等待......"));
				clearBrokenData();

				//
				setSaleFinishHint(status, Language.apply("正在清除付款冲正数据,请等待......"));
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox(Language.apply("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!"));
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

				// 上传当前小票
				setSaleFinishHint(status, Language.apply("正在上传交易小票数据,请等待......"));
				boolean bsend = GlobalInfo.isOnline;
				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment))
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
				}

				// 发送当前收银状态
				setSaleFinishHint(status, Language.apply("正在上传收银机交易汇总,请等待......"));
 				DataService.getDefault().sendSyjStatus();

				doEvaluation(this.saleHead, this.saleGoods, this.salePayment);
				
				// 打印小票
				setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
					printSaleBill();
				}

				// 标记本次交易已完成
				saleFinish = true;
			}
			if (GlobalInfo.sysPara.isNEWDHY.equals("Y")) {// 参数开启走新接口流程
				if ((saleHead.num1 == 1) || (saleHead.num2 >= 1)) {// 刷了大会员卡的或者使用了大会员积分的
					String memberReqInput = "";
					String memberResOutput = "";
					Wqbh_DataService WDds = new Wqbh_DataService();
					// 现金消费额
					String relPay = String.valueOf(WDds.calcPayFPMoney(
							saleHead, saleGoods, salePayment));
					Wqbh_DHYInterface DHY = new Wqbh_DHYInterface();
					String mktcode = GlobalInfo.sysPara.mktcode;
					if(GlobalInfo.sysPara.mktcode.indexOf(",")!=-1){
						mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",")+1);
					}
			 		String tradeNo = mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+saleHead.fphm+"|"+"0";
			 		String oldorder =mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+saleHead.yfphm+"|"+"0";
					if (SellType.ISSALE(saleHead.djlb)) {// 销售，同步交易
						JSONObject productInfos = new JSONObject();
						for (int i = 0; i < saleGoods.size(); i++) {
							SaleGoodsDef goods = (SaleGoodsDef) saleGoods
									.get(i);
							productInfos.put("productId",goods.code);
							productInfos.put("count",String.valueOf(goods.sl));
							JSONObject productInfo = new JSONObject();
							productInfo.put("price", goods.jg);
							productInfo.put("title", goods.name);
							productInfos.put("productInfo",productInfo.toString());
						}
						ArrayList list=new ArrayList();
						list.add(productInfos);
						JSONObject remark = new JSONObject();
						remark.put("tradeNo", tradeNo);
						remark.put("storeId", GlobalInfo.sysPara.WHstoreId);
						memberReqInput = "memberId=" + saleHead.str8
								+ "&productInfos=" + list + "&totalPrice="
								+ saleHead.ysje + "&realPay=" + relPay
								+ "&usePoint=" + saleHead.num3
								+ "&returnPoint=" + saleHead.bcjf
								+ "&tradeCode=8002&tradeSrc=4&remark="
								+ remark.toString();
						memberResOutput = DHY.SyncSaleMsg(memberReqInput);
					} else if (SellType.ISBACK(saleHead.djlb)) {// 退货，同步退货
						tradeNo = mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+saleHead.fphm+"|"+"0";
						
						memberReqInput = "memberId="+ saleHead.str8
								+ "&orderNo=" +oldorder+"&remark="+tradeNo+"&refundAmount="
								+ saleHead.ysje + "&cash=" + relPay + "&point="
								+ saleHead.bcjf + "&tradeCode=8002&tradeSrc=4";
						memberResOutput = DHY.SyncBackMsg(memberReqInput);
					}
					if (memberResOutput != null
							&& memberResOutput.trim().length() > 0) {
						JSONObject js = JSONObject.parseObject(memberResOutput);
						if (js.getString("status").equals("0"))// 同步交易接口返回成功
						{
							PosLog.getLog(getClass()).info("小票号:"+saleHead.fphm +"同步交易/退货 "+js.getString("message") );
						}else{
							new MessageBox("大会员同步交易/同步退货/取消交易接口返回数据失败："+js.getString("status")+js.getString("message"));
						}
							
					}
				}
			}
			// 返回到正常销售界面
			backToSaleStatus();

			// 保存本次的小票头
			if (saleFinish && saleHead != null)
			{
				lastsaleHead = saleHead;
			}
            
			// 清除本次交易数据
			this.initNewSale();

			// 关闭钱箱
			setSaleFinishHint(status, Language.apply("正在等待关闭钱箱,请等待......"));
			if (GlobalInfo.sysPara.closedrawer == 'Y')
			{
				// 如果钱箱能返回状态，采用等待钱箱关闭的方式来关闭找零窗口
				if (CashBox.getDefault().canCheckStatus())
				{
					// 等待钱箱关闭,最多等待一分钟
					int cnt = 0;
					while (CashBox.getDefault().getOpenStatus() && cnt < 30)
					{
						Thread.sleep(2000);

						cnt++;
					}

					// 等待一分钟后,钱箱还未关闭，标记为要等待按键才关闭找零窗口
					if (CashBox.getDefault().getOpenStatus() && cnt >= 30)
					{
						waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
						waitKeyCloseForm.append("Y");
					}
				}
				else
				{
					// 标记为要等待按键才关闭找零窗口
					waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
					waitKeyCloseForm.append("Y");
				}
			}

			// 交易完成
			setSaleFinishHint(status, Language.apply("本笔交易结束,开始新交易"));

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("完成交易时发生异常:\n\n") + ex.getMessage());

			return saleFinish;
		}
		
	}
	
	public boolean autoPay()
	{
		if (SellType.ISSALE(saletype) && (saleHead.num1 == 1)) {
			String jf = findDHYJF();
			if (jf != null && !jf.trim().equals("")) {
				double dhyjf = Double.parseDouble(jf.trim());
				if (dhyjf > 0) {
					sendSecMonitor("DHYJF",new String[]{jf},-1);
					// 询问只是为了提醒收银员，没有任何实际作用，所以没有逻辑处理
					if (new MessageBox("您本次消费积分抵用可以节约" + jf + "元，请问您使用多少积分?", null,
							true).verify() != GlobalVar.Key2) {
						PayModeDef payMode = DataService.getDefault()
								.searchPayMode("0610");
						// 创建一个付款方式对象
						Payment pay = CreatePayment.getDefault().createPaymentByPayMode(payMode,
										this);
						if (pay == null)
							return false;

						SalePayDef sp = null;
						TextBox txt = new TextBox();
						String Yxje = "";
						StringBuffer Unyxje = new StringBuffer();
						double yf = calcPayBalance();
						while (Yxje.trim().equals("")) {
							if (!txt.open("请在键盘输入使用积分", "支付积分", "可用积分余额为" + jf,
									Unyxje, 0, 0, false, TextBox.DoubleInput))
								return false;
							Yxje = Unyxje.toString();
							if (Convert.toDouble(Yxje) > yf
									|| Convert.toDouble(Yxje) < 0||Convert.toDouble(Yxje)>dhyjf) {
								Yxje = "";
								new MessageBox("请输入正确的使用积分");
								continue;
							} else{
								PaymentMzk pm =(WqbhDHY_PaymentMzk) pay;
								pm.mzkreq.track2 = saleHead.str6;
								if (pm.sendMzkSale(pm.mzkreq, pm.mzkret))
								break;
								}
						}
//						if (saleHead.str6.length() == 11) {
//							String result = chkPWD(saleHead.str8, saleHead.str1);
//							JSONObject rs = JSONObject.parseObject(result);
//							if (!rs.getString("status").equals("0")) {// 验证支付密码接口返回成功
//								new MessageBox("验证支付密码错误，错误代码："
//										+ rs.getString("status") + "\n错误原因："
//										+ rs.getString("message"));
//								return false;
//							}
//						}
						if (!pay.createSalePay(Yxje))
							return false;
						sp = pay.salepay;

						// 付款记账
						return payAccount(pay, sp);
					} else
						return false;
				} else
					return false;
			} else
				return false;
		} else
			return false;
	}
}
