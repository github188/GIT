package custom.localize.Wqbh;

import java.io.BufferedReader;
import java.util.Vector;

import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import device.ICCard.KTL512VWQ;


public class Wqbh_MenuFuncBS extends MenuFuncBS
{
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		// 记录菜单功能日志
		if (mfd != null && mfd.workflag == 'Y')
			AccessDayDB.getDefault().writeWorkLog("进入 \"[" + mfd.code + "]" + mfd.name + "\" 菜单", mfd.code);

		 if (Integer.parseInt(mfd.code) == StatusType.MN_DHY)
		{
			try{
//				 调用大会员接口 弹出大会员DLL界面
				String memberFuncReturn = "";
				if (RdPlugins.getDefault().getPlugins1().exec(1, GlobalInfo.syjStatus.syjh + "," + GlobalInfo.posLogin.gh))
					memberFuncReturn = ((String) RdPlugins.getDefault().getPlugins1().getObject()).trim();

				if (memberFuncReturn == null||memberFuncReturn.equals(""))
				{
					new MessageBox("Of_memberFunc接口调用失败!");
					return;
				}

				if (memberFuncReturn.length() < 2 || !memberFuncReturn.substring(0, 2).equals("00"))
				{
					new MessageBox("Of_memberFunc接口调用失败!");
					return;
				}

				String type = memberFuncReturn.substring(2, 3); // 回应标志
				if (type.equals("2"))
				{
					// 打印txt文件
					PosLog.getLog(getClass()).info("打印当前凭证TXT文件");
					PrintDHYDoc();// printDoc();//调用大会员付款方式的打印方法

				}
				else if (type.equals("3"))
				{
//					 弹出选择窗口  ---add
					String[] title = { "输入类型" };
					int[] width = { 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "刷卡输入" });
					contents.add(new String[] { "手机号输入" });
					
					//int inputtyep = -1;
					String track2 = "";
					int choice = new MutiSelectForm().open("请选择输入方式", title, width, contents);
					if (choice == -1||choice == 0)
					{
						
//						 弹出刷卡窗口
						StringBuffer cardno = new StringBuffer();
						TextBox txt = new TextBox();
						if (!txt.open("请刷大会员卡", "大会员号", "请将大会员卡从刷卡槽刷入", cardno, 0, 0, false,  TextBox.MsrInput))
							return;
						
						track2 = txt.Track2;
					}
					else 
					{
						ProgressBox pb = null;
						pb = new ProgressBox();
						pb.setText("正在输入卡号和密码,请等待...");
						track2 = new KTL512VWQ().findCard();
						if (pb != null)
						{
							pb.close();
							pb = null;
						}
					}

					
					//if(inputtyep ==0) track2 = cardno.toString();
					
					String cardFuncReturn = null;
					ProgressBox prg = new ProgressBox();
					prg.setText("正在查询会员卡,请稍等...");

					String funcCode = memberFuncReturn.substring(memberFuncReturn.length() - 2);
					if (RdPlugins.getDefault().getPlugins1().exec(8, GlobalInfo.syjStatus.syjh + "," + GlobalInfo.posLogin.gh + "," + track2 + "," + funcCode))
						cardFuncReturn = (String) RdPlugins.getDefault().getPlugins1().getObject();

					prg.close();
					prg = null;

					// new MessageBox( cardFuncReturn + "调用大会员查询函数成功");
					if (cardFuncReturn == null || cardFuncReturn.length() < 2 || !cardFuncReturn.substring(0, 2).equals("00"))
					{
						new MessageBox("调用大会员查询失败");
						return;
					}

					// 记录日志
					if (cardFuncReturn.length() > 20 && cardFuncReturn.substring(20, 21).equals("2"))
					{
						// 打印MemberReceipt.TXT文件
						PrintDHYDoc();// printDoc();
					}
				}
			}
			catch(Exception er)
	    	{
	    		er.printStackTrace();
	    		return ;
	    	}
		}else if (Integer.parseInt(mfd.code) == StatusType.MN_BACKPWD){
			try{
            //调用发送短信验证码功能	
//				 弹出刷卡窗口  ---new
				String[] title = { "输入类型" };
				int[] width = { 440 };
				Vector contents = new Vector();
				contents.add(new String[] { "手机号输入" });
				contents.add(new String[] { "大会员键盘输入" });
				
				String PhoneNo = "";
				int choice = new MutiSelectForm().open("请选择输入方式", title, width, contents);

				 if(choice == 0)
				{

					ProgressBox pb = null;
					pb = new ProgressBox();
					pb.setText("正在输入手机号,请等待...");
					PhoneNo = new KTL512VWQ().findCard();
					if (pb != null)
					{
						pb.close();
						pb = null;
					}
				}else if(choice == 1){
//					 弹出刷卡窗口
					StringBuffer cardno = new StringBuffer();
					TextBox txt = new TextBox();
					if (!txt.open("请输入大会员手机号", "手机号", "请将在大会员键盘输入手机号", cardno, 0, 0, false, TextBox.IntegerInput))
						return;
					
					PhoneNo = cardno.toString();
				}
				if (PhoneNo == null || PhoneNo.equals("")) return ;
				
//				String[] title1 = { "密码类型" };
//				int[] width1 = { 440 };
//				Vector contents1 = new Vector();
//				contents1.add(new String[] { "注册验证码" });
//				contents1.add(new String[] { "临时支付密码" });
				
//				int PwdType = 5;
//				int choice1 = new MutiSelectForm().open("请选择密码类型", title1, width1, contents1);
//				if (choice1==0)
//					PwdType=1;
				String memberInfoInPut="mobile="+PhoneNo+"&type=5";
				String memberInfoReturn = "";
				Wqbh_DHYInterface DHY=new Wqbh_DHYInterface();
				memberInfoReturn=DHY.PostMsgChk(memberInfoInPut);
				if (!memberInfoReturn.equals("")||!memberInfoReturn.equals(null)){//如果取到返回值就去解析返回的json数据
					JSONObject js=JSONObject.fromObject(memberInfoReturn);
					if (js.getString("status").equals("0")){//返回成功
						new MessageBox("调用发送短信验证码成功，请注意短信接收");
					}else{
						new MessageBox("调用发送短信验证码失败,错误码："+js.getString("status")+",错误原因："+js.getString("message"));
					}
						
				}
			}
				catch(Exception er)
		    	{
		    		er.printStackTrace();
		    		return ;
		    	}
		}else if (Integer.parseInt(mfd.code) == StatusType.MN_JFXX){//大会员积分查询
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
			Wqbh_DHYInterface DHY = new Wqbh_DHYInterface();
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
				JSONObject js = JSONObject.fromObject(memberInfoReturn);
				if (js.getString("status").equals("0")) {// 返回成功
					String data = js.getString("data");
					JSONObject jsdata = JSONObject.fromObject(data);
					String member = jsdata.getString("member");
					JSONObject jsmember = JSONObject.fromObject(member);
					String cardID = jsmember.getString("cardNo");
					String bonus = jsmember.getString("point");
					String validBonus = jsmember.getString("avlPoint");
					new MessageBox("卡号："+cardID+"\n"+"积分余额："+bonus+"\n"+"可用积分："+validBonus);
				}
			}
		}else if (Integer.parseInt(mfd.code) == StatusType.MN_REGIST){//大会员注册
			try{
				String[] title = { "输入类型" };
				int[] width = { 440 };
				Vector contents = new Vector();
				contents.add(new String[] { "手机号输入" });
				contents.add(new String[] { "大会员键盘输入" });
				
				String PhoneNo = "";
				int choice = new MutiSelectForm().open("请选择输入方式", title, width, contents);

				 if(choice == 0)
				{

					ProgressBox pb = null;
					pb = new ProgressBox();
					pb.setText("正在输入手机号,请等待...");
					PhoneNo = new KTL512VWQ().findCard();
					if (pb != null)
					{
						pb.close();
						pb = null;
					}
				}else if(choice == 1){
//					 弹出刷卡窗口
					StringBuffer cardno = new StringBuffer();
					TextBox txt = new TextBox();
					if (!txt.open("请输入大会员手机号", "手机号", "请在大会员键盘输入手机号", cardno, 0, 0, false, TextBox.IntegerInput))
						return;
					
					PhoneNo = cardno.toString();
				}
				if (PhoneNo == null || PhoneNo.equals("")) return ;
				String memberInfoInPut="mobile="+PhoneNo+"&type=1";
				String memberInfoReturn = "";
				Wqbh_DHYInterface DHY=new Wqbh_DHYInterface();
				memberInfoReturn=DHY.PostMsgChk(memberInfoInPut);
				if (!memberInfoReturn.equals("")||!memberInfoReturn.equals(null)){//如果取到返回值就去解析返回的json数据
					JSONObject js=JSONObject.fromObject(memberInfoReturn);
					if (js.getString("status").equals("0")){//返回成功
						new MessageBox("调用发送注册码成功，请注意短信接收");
					}else{
						new MessageBox("调用发送短信验证码失败,错误码："+js.getString("status")+",错误原因："+js.getString("message"));
						return;
					}
					StringBuffer pwd = new StringBuffer();
					TextBox txt = new TextBox();
					if (!txt.open("请输入注册验证码", "验证码", "请在大会员键盘输入注册验证码", pwd, 0, 0, false, TextBox.IntegerInput))
						return;
					String YZM = pwd.toString();
					memberInfoInPut="userName="+PhoneNo+"&userNameType=2&channel=5&verifyCode="+YZM;
					memberInfoReturn="";
					memberInfoReturn=DHY.MemberRegist(memberInfoInPut);
					if (!memberInfoReturn.equals("")||!memberInfoReturn.equals(null)){//如果取到返回值就去解析返回的json数据
						JSONObject jso=JSONObject.fromObject(memberInfoReturn);
						if (jso.getString("status").equals("0")){//返回成功
							new MessageBox("大会员注册成功！");
						}else{
							new MessageBox("调用大会员注册接口失败,错误码："+jso.getString("status")+",错误原因："+jso.getString("message"));
							return;
						}
						}
				}
			}
				catch(Exception er)
		    	{
		    		er.printStackTrace();
		    		return ;
		    	}
			
		}
		else
		{
			super.execFuncMenu(mfd, mffe);
		}
	}

	public void PrintDHYDoc()
	{
		
		ProgressBox pb = null;
		if(GlobalInfo.sysPara.bankprint<1) return;    
		String printName = "C:\\WandaMember\\MemberReceipt.TXT";
		try
		{
			 
			if (!PathFile.fileExist(printName))
            {	
				new MessageBox("万达大会员凭条不存在，无法打印!", null, false);
                return;
            }			
			pb = new ProgressBox();
	        pb.setText("正在打印万达大会员凭条,请等待...");
	        
	        for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
	        {
	        	PrintDoc_Start();

	            BufferedReader br = null;
	            
	            try
	            {
	            	 br = CommonMethod.readFileGBK(printName);

	            	 if (br == null)
	            	 {
                        new MessageBox("打开" + printName + "打印文件失败!");

                        return;
	            	 }
	            	 
	            	 String line = null;
	            	 
	            	 while ((line = br.readLine()) != null)
	            	 {	            		    
	            		 if ( line == null || line.length() <= 0)
	            			 continue;
	            		 if (line.indexOf("CUTPAPER") != -1 )
	            		 {
	            			 PrintDoc_End();
	            			 //new MessageBox("请撕下万达大会员凭条" );
	            			 continue;
	            		 }

	            		 PrintDoc_Print(line);
	            	 }
	            }
	            catch (Exception ex)
	            {
	            	new MessageBox(ex.getMessage());
	            }
	            finally
	            {
	            	if (br != null)
	            	{
	            		br.close();
	            	}
	            }

	            PrintDoc_End();
	        }
			
		}
		catch (Exception ex)
		{
			new MessageBox("打印万达大会员凭条发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	
		
	}

	private void PrintDoc_Print(String line)
	{
		switch(ConfigClass.RepPrintTrack)
		{
			case 1:
				Printer.getDefault().printLine_Normal(line);
				break;
			case 2:
				Printer.getDefault().printLine_Journal(line);
				break;
			case 3:
				Printer.getDefault().printLine_Slip(line);
				break;
			default:
				Printer.getDefault().printLine_Normal(line);
				break;
		}
	}

	private void PrintDoc_End()
	{
		switch(ConfigClass.RepPrintTrack)
		{
			case 1:
				Printer.getDefault().cutPaper_Normal();
				break;
			case 2:
				Printer.getDefault().cutPaper_Journal();
				break;
			case 3:
				Printer.getDefault().cutPaper_Slip();
				break;
			default:
				Printer.getDefault().cutPaper_Normal();
				break;
		}
	}

	private void PrintDoc_Start()
	{
		switch(ConfigClass.RepPrintTrack)
		{
			case 1:
				Printer.getDefault().startPrint_Normal();
				break;
			case 2:
				Printer.getDefault().startPrint_Journal();
				break;
			case 3:
				Printer.getDefault().startPrint_Slip();
				break;
			default:
				Printer.getDefault().startPrint_Normal();
				break;
		}
	}

	
}
