package custom.localize.Nnmk;

import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.FjkInfoDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.MutiSelectEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Nnmk_MutiSelectBS extends MutiSelectBS
{
	public String text = null;
	public Vector contents = null;

	public void initBS(MutiSelectEvent event, MutiSelectForm form, int funcID, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		if (funcID == 301 || funcID == 310)
		{
			NewKeyListener.sendKey(GlobalVar.Enter);
		}
		//enterBS(event, form, funcID, content, cannotchoice, cannotchoice, cannotchoice, rowindex, cannotchoice, cannotchoice);
	}

	public boolean enterBS(MutiSelectEvent event, MutiSelectForm form, int funcID, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		if (funcID == 301)
		{
			PaymentMzk mzk = getSelectPaymentMzk();

			if (mzk == null)
			{
				mzk = CreatePayment.getDefault().getPaymentMzk();
				text = "面值卡";
			}
			else
			{
				//text = mzk.paymode.name;
				text = "面值卡";
			}
			StringBuffer cardno = new StringBuffer();
			String track1, track2, track3;

			if (contents == null)
			{
				contents = content;
				if (contents == null) contents = new Vector();
			}

			String cardno1 = null;
			String je1 = null;

			while (true)
			{
				String info = "请刷" + text;
				if (cardno1 != null) info = "上笔卡号" + cardno1 + "  余额" + je1;
				// 刷面值卡
				TextBox txt = new TextBox();
				if (!txt.open("请刷" + text, text, info, cardno, 0, 0, false, mzk.getAccountInputMode()))
				{
					break;
				}

				ProgressBox progress = null;

				try
				{
					progress = new ProgressBox();
					progress.setText("正在查询" + text + "信息，请等待.....");

					// 得到磁道信息
					track1 = txt.Track1;
					track2 = txt.Track2;
					track3 = txt.Track3;

					// 先发送冲正
					if (!mzk.sendAccountCz()) break;

					// 再查询
					if (!mzk.findMzkInfo(track1, track2, track3))
					{
						break;
					}

					// 在客显上显示面值卡号及余额
					LineDisplay.getDefault().displayAt(0, 0, Convert.increaseChar(ManipulatePrecision.doubleToString(mzk.mzkret.ye), 20));
					LineDisplay.getDefault().displayAt(1, 0, Convert.increaseChar(" ", 20));
					// LineDisplay.getDefault().displayAt(1, 1,
					// ManipulatePrecision.doubleToString(mzk.mzkret.ye));

					//
					progress.close();
					progress = null;
					//检查卡是否已经刷过
					boolean add = false;

					for (int i = 0; i < contents.size(); i++)
					{
						String row[] = (String[]) contents.elementAt(i);
						if (mzk.mzkret.cardno.equals(row[0]))
						{
							contents.removeElementAt(i);
							String row1[] = new String[] { mzk.mzkret.cardno, ManipulatePrecision.doubleToString(mzk.mzkret.ye), mzk.mzkret.status };
							contents.add(0, row1);
							add = true;
							break;
						}
					}

					if (!add)
					{
						String row1[] = new String[] { mzk.mzkret.cardno, ManipulatePrecision.doubleToString(mzk.mzkret.ye), mzk.mzkret.status };
						contents.add(0, row1);
					}

					//刷新后面的界面
					form.table.exchangeContent(contents);
					if (contents.size() > 0)
					{
						String row1[] = (String[]) contents.elementAt(0);
						cardno1 = row1[0];
						je1 = row1[1];
						form.table.setSelection(0);
					}
					event.content = contents;
					//计算总金额
					double je = 0;
					for (int i = 0; i < contents.size(); i++)
					{
						String row[] = (String[]) contents.elementAt(i);
						je = ManipulatePrecision.doubleConvert(je + Convert.toDouble(row[1]));
					}

					form.label.setText("卡数量:" + contents.size() + "   总余额:" + ManipulatePrecision.doubleToString(je));
				}
				catch (Exception er)
				{
					er.printStackTrace();
					new MessageBox(er.getMessage());
				}
				finally
				{
					if (progress != null) progress.close();
				}
			}
			
			return true;
		}

		if (funcID == 310)
		{
			StringBuffer cardno = new StringBuffer();
			String track1, track2, track3;
			ArrayList fjklist1 = null;

			Vector vv = new Vector();
			vv.add(new String[] { "打印券" });
			vv.add(new String[] { "电子券" });
			String[] title = { "券名称" };
			int[] width = { 530 };
			int choice = new MutiSelectForm().open("请选择查询券类型", title, width, vv);

			// 创建返券卡付款对象
			PaymentCoupon fjk = CreatePayment.getDefault().getPaymentCoupon();

			//选择返券卡的类型以便解析磁道
			fjk.choicFjkType();
			String cardno1 = null;
			String je1 = null;
			if (contents == null)
			{
				contents = content;
				if (contents == null) contents = new Vector();
			}
			while (true)
			{
				//刷面值卡
				String info = null;
				String text = null;
				if (choice == 0)
				{
					info = "打印券";
				}
				else
				{
					info = "电子券";
				}

				if (cardno1 != null) text = "上笔卡号" + cardno1 + "  余额" + je1;
				TextBox txt = new TextBox();
				if (!txt.open("请刷" + info, info, text, cardno, 0, 0, false, fjk.getAccountInputMode()))
				{
					break;
				}

				// 得到磁道信息
				track1 = txt.Track1;
				track2 = txt.Track2;
				track3 = txt.Track3;

				ProgressBox progress = null;
				try
				{
					progress = new ProgressBox();

					progress.setText("正在查询" + info + "信息，请等待.....");
					//再查询
					Nnmk_CouponQueryInfoBS coupon = new Nnmk_CouponQueryInfoBS();
					if (choice == 0)
					{
						coupon.findHYKZJ(track1, track2, track3);
					}
					else if (choice == 1)
					{
						coupon.findHYK(track1, track2, track3);
					}
					// 关闭
					progress.close();
					progress = null;

					fjklist1 = coupon.getCouponValue1();
					// 无结果
					//if (fjklist.size() < 1) return false;
					boolean add = false;

					for (int i = 0; i < contents.size(); i++)
					{
						String row[] = (String[]) contents.elementAt(i);
						String cardno2 = "";
						if (choice == 0)
						{
							cardno2 = Nnmk_CouponQueryInfoBS.mzkretZQ.cardno;
						}
						else
						{
							cardno2 = coupon.getCardno();
						}
						if (cardno2.equals(row[0]))
						{
							contents.removeElementAt(i);
							double tje = 0.0;
							//"卡号", "券名称", "截止时间", "余额A", "余额B", "余额F", 
							for (int j = 1; j <= fjklist1.size(); j++)
							{
								String row1[] = new String[10];
								row1[0] = cardno2;
								FjkInfoDef temp = (FjkInfoDef) fjklist1.get(j - 1);
								row1[1] = temp.cardname;
								row1[2] = temp.enddate;
								row1[3] = String.valueOf(temp.yeA + temp.yeB + temp.yeF);
								contents.add(0, row1);
								tje = tje +temp.yeA + temp.yeB + temp.yeF;
								je1 = String.valueOf(tje);
							}

							//{ coupon.getCardno(),((String[])fjklist.elementAt(0))[1]+ ((String[])fjklist.elementAt(0))[2],((String[])fjklist.elementAt(1))[1]+ ((String[])fjklist.elementAt(1))[2], ((String[])fjklist.elementAt(2))[1]+((String[])fjklist.elementAt(2))[2],((String[])fjklist.elementAt(3))[1]+ ((String[])fjklist.elementAt(3))[2],((String[])fjklist.elementAt(4))[1]+ ((String[])fjklist.elementAt(4))[2],((String[])fjklist.elementAt(5))[1]+ ((String[])fjklist.elementAt(5))[2],((String[])fjklist.elementAt(6))[1]+ ((String[])fjklist.elementAt(6))[2] };

							add = true;
							break;
						}
					}

					if (!add)
					{
						double tje = 0.0;
						for (int j = 1; j <= fjklist1.size(); j++)
						{
							String row1[] = new String[10];
							if (choice == 0)
							{
								row1[0] = Nnmk_CouponQueryInfoBS.mzkretZQ.cardno;
							}
							else
							{
								row1[0] = coupon.getCardno();
							}
							FjkInfoDef temp = (FjkInfoDef) fjklist1.get(j - 1);
							row1[1] = temp.cardname;
							row1[2] = temp.enddate;
							row1[3] = String.valueOf(temp.yeA + temp.yeB + temp.yeF);
							contents.add(0, row1);
							tje = tje +temp.yeA + temp.yeB + temp.yeF;
							je1 = String.valueOf(tje);
						}
					}

					//刷新后面的界面
					form.table.exchangeContent(contents);
					if (contents.size() > 0)
					{
						String row1[] = (String[]) contents.elementAt(0);
						cardno1 = row1[0];
						form.table.setSelection(0);
					}
					event.content = contents;
					//计算总金额
					double je = 0;
					for (int i = 0; i < contents.size(); i++)
					{
						String row[] = (String[]) contents.elementAt(i);
						je = ManipulatePrecision.doubleConvert(je + Convert.toDouble(row[3]));
					}
					//form.label.setText("卡数量:" + contents.size() + "   总余额:" + ManipulatePrecision.doubleToString(je));
					form.label.setText("卡数量:" + contents.size() + "   总余额:" + ManipulatePrecision.doubleToString(je));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				finally
				{
					if (progress != null) progress.close();

					if (fjklist1 != null)
					{
						fjklist1.clear();
						fjklist1 = null;
					}
				}
			}
			
			return true;
		}
		return false;
	}

	public boolean exitBS(MutiSelectEvent event, MutiSelectForm form, int funcid, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		try{
			if (funcid == 301)
			{
				//new MessageBox("Exit......................");
				if (contents != null && contents.size() > 0)
				{
					//计算总金额
					double je = 0;
					for (int i = 0; i < contents.size(); i++)
					{
						String row[] = (String[]) contents.elementAt(i);
						je = ManipulatePrecision.doubleConvert(je + Convert.toDouble(row[1]));
					}
					LineDisplay.getDefault().displayAt(0, 0, Convert.increaseChar(ManipulatePrecision.doubleToString(je), 20));
					LineDisplay.getDefault().displayAt(1, 0, Convert.increaseChar("", 20));
				}
			}
			if(funcid == 310){
				if (contents != null && contents.size() > 0)
				{
					//计算总金额
					double je = 0;
					for (int i = 0; i < contents.size(); i++)
					{
						String row[] = (String[]) contents.elementAt(i);
						je = ManipulatePrecision.doubleConvert(je + Convert.toDouble(row[3]));
					}
					LineDisplay.getDefault().displayAt(0, 0, Convert.increaseChar(ManipulatePrecision.doubleToString(je), 20));
					LineDisplay.getDefault().displayAt(1, 0, Convert.increaseChar("", 20));
				}
			}
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		return false;
	}

	public PaymentMzk getSelectPaymentMzk()
	{
		try
		{
			if (ConfigClass.CustomPayment != null && ConfigClass.CustomPayment.size() > 0)
			{
				String classname = null;

				Vector v = new Vector();

				for (int i = 0; i < ConfigClass.CustomPayment.size(); i++)
				{
					String s = (String) ConfigClass.CustomPayment.elementAt(i);

					String[] sp = s.split(",");

					if (sp.length >= 2 && sp[0].endsWith("PaymentMzk"))
					{
						for (int k = 0; k < GlobalInfo.payMode.size(); k++)
						{
							PayModeDef mdf = (PayModeDef) GlobalInfo.payMode.elementAt(k);

							if (!mdf.code.trim().equals(sp[1])) continue;

							if(sp[1].equals("0505")){
								v.add(new String[] { "储值卡", sp[0], sp[1] });
							}else{
								v.add(new String[] { mdf.name, sp[0], sp[1] });
							}
							
						}
					}
				}

				if (v.size() <= 0) return null;

				PayModeDef pmd = null;
				if (v.size() == 1)
				{
					classname = ((String[]) v.elementAt(0))[1];
					pmd = DataService.getDefault().searchPayMode(((String[]) v.elementAt(0))[2]);

					Class cl = CreatePayment.getDefault().payClassName(classname);
					PaymentMzk mzk = (PaymentMzk) cl.newInstance();
					mzk.initPayment(pmd, null);
					return mzk;
				}

				String[] title = { "面值卡名称" };
				int[] width = { 530 };
				int choice = new MutiSelectForm().open("请选择查询面值卡类型", title, width, v);

				if (choice == -1)
				{
					classname = ((String[]) v.elementAt(0))[1];

					text = ((String[]) v.elementAt(0))[0];
					pmd = DataService.getDefault().searchPayMode(((String[]) v.elementAt(0))[2]);
				}
				else
				{
					classname = ((String[]) v.elementAt(choice))[1];

					text = ((String[]) v.elementAt(choice))[0];
					pmd = DataService.getDefault().searchPayMode(((String[]) v.elementAt(choice))[2]);
				}

				if (classname == null) return null;

				Class cl = CreatePayment.getDefault().payClassName(classname);

				if (cl != null)
				{
					PaymentMzk mzk = (PaymentMzk) cl.newInstance();
					mzk.initPayment(pmd, null);
					return mzk;
				}
				else
				{
					new MessageBox("付款对象 " + classname + " 不存在");
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}
}
