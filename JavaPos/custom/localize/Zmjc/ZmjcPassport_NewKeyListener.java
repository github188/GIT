package custom.localize.Zmjc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Device.BankTracker;
import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Device.MSR;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.Struct.ShortcutKeyDef;

import device.ICCard.Muti_ICCard;

public class ZmjcPassport_NewKeyListener implements KeyListener
{
	public static int[] keyFilter = null;

	private static boolean funcRun = false;
	public boolean funcRun_ext = false;
	
	public static int curInputMode = -1;

	public final int IntegerInput = 0; // 整数输入模式

	public final int DoubleInput = 1; // 数字输入模式

	public final int MsrInput = 2; // 刷卡输入模式

	public final int MsrKeyInput = 3; // 刷卡、键盘输入皆可模式
	
	public final int MsrKeyInputMarker = 4; // 刷卡、键盘输入皆可模式且手工输入*号显示

	public NewKeyEvent event = null;

	private int keyValue = 0;

	public int inputMode = -1;

	public boolean isPage = true;

	private int pagesize = 0;

	public boolean isBackSpace = true;

	public boolean isShortKey = true;

	public boolean isControl = false; // 当设定为true时，就算光标所在控件为不可编辑，还是响应案件

	private boolean isMsrKeyboard = true;

	private long firstmsrinputtime = -1;

	private int trackcount = 0;

	private boolean msrfinish = false;

	private StringBuffer trackbuffer = new StringBuffer();

	private boolean trackhaveprefix = false;

	private String track1 = "";

	private String track2 = "";

	private String track3 = "";
	
	private boolean isEditableResponseEvent = false;
	
	public String payCode = "";
	
	public void setEditableResponseEvent (boolean arg)
	{
		isEditableResponseEvent = arg;
	}

	public static int searchKey(int key)
	{
		
		if (curInputMode == 4)
		{
			if (key == 13)  //处理回车
				return GlobalVar.Enter;
			
			if (key == 27)	//处理ESC
				return GlobalVar.Exit;
		
			if(key ==16777223 )
				return GlobalVar.Validation;
		//	if ((key >=97 && key <=122 ) || (key>=65 && key <=90))
				return -1;
		}
		
		String e = String.valueOf(key);
		String[][] keypad = GlobalInfo.keypad;
		int validnum = GlobalInfo.validNum;

		if (keypad == null) { return -1; }
		
		for (int i = 0; i < validnum; i++)
		{
			//if (keypad[i][2].trim().equals(e) || keypad[i][3].trim().equals(e)) { return Integer.parseInt(keypad[i][1].trim()); }
			if (keypad[i][2].trim().equals(e) || keypad[i][3].trim().equals(e)) 
			{ 
				int k = Convert.toInt(keypad[i][1]);
				if (k==GlobalVar.Exit  || k==GlobalVar.Pay)
				{
					//只处理常用快捷键，否则字母无法输入 wangyong add by 2013.8.7
					return Integer.parseInt(keypad[i][1].trim()); 
				}
				break;
			}
			
			 
		}

		return -1;
	}

	public static int searchKeyCode(int value)
	{
		String[][] keypad = GlobalInfo.keypad;
		int validnum = GlobalInfo.validNum;

		if (keypad == null) { return 0; }

		for (int i = 0; i < validnum; i++)
		{
			if (Integer.parseInt(keypad[i][1].trim()) == value) { return Math.max(Integer.parseInt(keypad[i][2].trim()), Integer.parseInt(keypad[i][3].trim())); }
		}

		return 0;
	}
	
	public static boolean sendKey(int value,Control con)
	{
		int k = searchKeyCode(value);

		Event keyevent = new Event();
		keyevent.widget = con;
		keyevent.keyCode = k;
		keyevent.character = (char) k;
		keyevent.type = SWT.KeyDown;
		Display.getCurrent().post(keyevent);
		keyevent.type = SWT.KeyUp;
		Display.getCurrent().post(keyevent);

		return true;
	}

	public static boolean sendKey(int value)
	{
		int k = searchKeyCode(value);

		Event keyevent = new Event();
		keyevent.widget = Display.getCurrent().getFocusControl();
		keyevent.keyCode = k;
		keyevent.character = (char) k;
		keyevent.type = SWT.KeyDown;
		Display.getCurrent().post(keyevent);
		keyevent.type = SWT.KeyUp;
		Display.getCurrent().post(keyevent);

		return true;
	}

	public static boolean sendASII(int value)
	{
		Event keyevent = new Event();
		keyevent.widget = Display.getCurrent().getFocusControl();
		keyevent.keyCode = value;
		keyevent.character = (char) value;
		keyevent.type = SWT.KeyDown;
		Display.getCurrent().post(keyevent);
		keyevent.type = SWT.KeyUp;
		Display.getCurrent().post(keyevent);

		return true;
	}
	
	public static boolean sendASII(int value,Control con)
	{
		Event keyevent = new Event();
		keyevent.widget = con;
		keyevent.keyCode = value;
		keyevent.character = (char) value;
		keyevent.type = SWT.KeyDown;
		Display.getCurrent().post(keyevent);
		keyevent.type = SWT.KeyUp;
		Display.getCurrent().post(keyevent);

		return true;
	}

	public boolean keyCodeFilter(KeyEvent e)
	{
		if (e.keyCode == SWT.SHIFT)
			return true;

		if (keyFilter != null)
		{
			for (int i = 0; i < keyFilter.length; i++)
			{
				if (e.keyCode == keyFilter[i]) { return true; }
			}
		}

		return false;
	}

	public static boolean Recordstatus = false;// 判断是否开始记录键盘输入

	public static String KeyRecord = null;// 键盘活动

	private String OldHelpMessage = "";// 上一次状态栏中的提示信息

	public void keyPressed(KeyEvent e)
	{
		// 有,.?;'[]\-=这些有两个档的键,keycode为0,但有character,而且不触发keyrelease事件的情况
		// 所以主动触发keyrelease事件
		boolean runKeyReleased = false;

		if ((e.keyCode == 0) && (e.character > 0))
		{
			e.keyCode = e.character;
			runKeyReleased = true;
		}

		// Shift 键放弃
		if (keyCodeFilter(e))
		{
			e.doit = false;
			return;
		}

		// 判断POS键值
		keyValue = searchKey(e.keyCode);

		// 记录键盘输入到字符串中
		if (Recordstatus)
		{
			if ((!OldHelpMessage.equals(GlobalInfo.statusBar.getHelpMessage())) && (GlobalInfo.statusBar.getHelpMessage().indexOf("ms") > 0))
			{
				String HelpMessage = GlobalInfo.statusBar.getHelpMessage();
				OldHelpMessage = HelpMessage;
				HelpMessage = HelpMessage.substring(HelpMessage.indexOf(":") + 1, HelpMessage.indexOf("m"));
				HelpMessage = String.valueOf(Integer.valueOf(HelpMessage.trim()).intValue() + 50);
				KeyRecord += "%" + HelpMessage + ",\n";
			}

			if (keyValue != GlobalVar.AutoTest)
				KeyRecord += keyValue != -1 ? String.valueOf(keyValue) + "," : "#" + e.keyCode + ",";
		}

		boolean bContinue = true;

		// 缺省TEXT控件处理
		if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Text"))
		{
			if ((!((Text) e.widget).getEnabled() || !((Text) e.widget).getEditable()) && !isControl)
			{
				keyValue = -1;
			}

			if (TextControlDo((Text) e.widget, e))
			{
				bContinue = false;
			}
		}

		// ȱʡCOMBO����ؼ缺省COMBO控件处理
		if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Combo"))
		{
			if (!((Combo) e.widget).getEnabled() && !isControl)
			{
				keyValue = -1;
			}

			if (ComboControlDo((Combo) e.widget, e))
			{
				bContinue = false;
			}
		}

		// 缺省TABLE控件处理
		if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Table"))
		{
			if (!((Table) e.widget).getEnabled() && !isControl)
			{
				keyValue = -1;
			}

			if (TableControlDo((Table) e.widget, e))
			{
				bContinue = false;
			}
		}

		if (!bContinue)
		{
			e.doit = false;
		}

		// 如果是刷卡槽输入,不允许使用快捷键
		if (((inputMode == MsrInput) || (inputMode == MsrKeyInput) ||(inputMode == MsrKeyInputMarker)) && isMsrKeyboard)
		{
			isShortKey = false;
		}

		// 如果是快捷键则不回显
		if (isShortKey && (GlobalInfo.keyList != null) && (keyValue == -1))
		{
			for (int i = 0; i < GlobalInfo.keyList.size(); i++)
			{
				ShortcutKeyDef skd = (ShortcutKeyDef) GlobalInfo.keyList.get(i);

				if (skd.getShortcutKey() == e.keyCode)
				{
					e.doit = false;

					break;
				}
			}
		}

		// 如果是功能键则不回显
		if ((keyValue != -1) && !((keyValue >= GlobalVar.Key0) && (keyValue <= GlobalVar.Decimal)))
		{
			e.doit = false;
		}

		// 执行自定义键盘事件
		if ((event != null) && !(((inputMode == MsrInput) || (inputMode == MsrKeyInput) || (inputMode == MsrKeyInputMarker)) && isMsrKeyboard))
		{
			event.keyDown(e, keyValue);
		}

		// 主动触发keyrelease事件
		if (runKeyReleased)
		{
			keyReleased(e);
		}
		else if (msrfinish && MSR.getDefault() != null && MSR.getDefault().postKeyReleased(trackhaveprefix))
		{
			// IBM4614刷卡槽发出的回车键不触发keyReleased事件,因此主动发出一次;并改变键值防止系统自主再次触发
			e.keyCode = SWT.SHIFT;
			e.character = 0;
			e.doit = false;

			keyReleasedDoEvent(e);
		}
	}

	public void keyReleased(KeyEvent e)
	{
		// Shift 键放弃
		if (keyCodeFilter(e)) { return; }

		keyReleasedDoEvent(e);
	}

	public void keyReleasedDoEvent(KeyEvent e)
	{
		// 在linux下 SWT 有可能发生 key 触发了 release 而没有触发 press,所以当无keyValue时重新查找一次
		boolean occukeypress = false;
		if (keyValue == -1)
		{
			keyValue = searchKey(e.keyCode);

			if (keyValue != -1)
			{
				occukeypress = true;
			}
		}

		if (!isControl)
		{
			// 缺省TEXT控件处理
			if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Text"))
			{
				if ((!((Text) e.widget).getEnabled() || !((Text) e.widget).getEditable()) && !isEditableResponseEvent)
				{
					keyValue = -1;
				}
			}

			// ȱʡCOMBO����ؼ缺省COMBO控件处理
			if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Combo"))
			{
				if (!((Combo) e.widget).getEnabled())
				{
					keyValue = -1;
				}

				isShortKey = false;
			}

			// 缺省TABLE控件处理
			if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Table"))
			{
				if (!((Table) e.widget).getEnabled())
				{
					keyValue = -1;
				}

				isShortKey = false;
			}
		}

		// 处理快捷键
		try
		{
			// 如果收银机状态为离开由不执行快捷键
			if (GlobalInfo.syjStatus != null && GlobalInfo.syjStatus.status != StatusType.STATUS_LEAVE && GlobalInfo.enablequickkey)
			{
				if (isShortKey && (GlobalInfo.keyList != null) && (keyValue == -1))
				{
					for (int i = 0; i < GlobalInfo.keyList.size(); i++)
					{
						ShortcutKeyDef skd = (ShortcutKeyDef) GlobalInfo.keyList.get(i);

						if (skd.getShortcutKey() == e.keyCode)
						{
							String[] keystr = skd.getKeyString().split(";");

							String menuid = "";
							for (int j = 0; j < keystr.length; j++)
							{
								int k = Integer.parseInt(keystr[j]);
								if (k == GlobalVar.MainList && j + 1 < keystr.length && Integer.parseInt(keystr[j + 1]) == GlobalVar.MainList)
								{
									// 菜单ID快捷键
									menuid = "";
									for (j += 2; j < keystr.length; j++)
									{
										int n = Integer.parseInt(keystr[j]);
										if (n >= GlobalVar.Key0 && n <= GlobalVar.Key9)
										{
											menuid += String.valueOf(n - GlobalVar.Key0);
										}
										else
										{
											// 有菜单ID快捷键且在角色功能内,执行菜单功能
											if (n == GlobalVar.Pay && menuid.length() > 0 && ("," + GlobalInfo.posLogin.funcmenu + ",").indexOf("," + menuid + ",") >= 0)
											{
												final MenuFuncDef newmfd = new MenuFuncDef();
												newmfd.code = menuid;
												newmfd.name = "菜单编号功能";
												newmfd.workflag = 'Y';

												CustomLocalize.getDefault().createMenuFuncBS().execFuncMenu(newmfd, null);
												menuid = "";
											}

											break;
										}
									}
									continue;
								}
								else
									k = searchKeyCode(k);

								if (k == 0)
								{
									continue;
								}

								Event event = new Event();
								event.widget = Display.getCurrent().getCursorControl();
								event.keyCode = k;
								event.character = (char) k;

								event.type = SWT.KeyDown;
								Display.getCurrent().post(event);

								event.type = SWT.KeyUp;
								Display.getCurrent().post(event);

								// 延时1ms,避免LINUX无规律多产生按键
								Thread.sleep(10);
							}

							// 有菜单ID快捷键且在角色功能内,执行菜单功能
							if (menuid.length() > 0 && ("," + GlobalInfo.posLogin.funcmenu + ",").indexOf("," + menuid + ",") >= 0)
							{
								final MenuFuncDef newmfd = new MenuFuncDef();
								newmfd.code = menuid;
								newmfd.name = "菜单编号功能";
								newmfd.workflag = 'Y';

								CustomLocalize.getDefault().createMenuFuncBS().execFuncMenu(newmfd, null);
								menuid = "";
							}
							break;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).debug(ex);
			ex.printStackTrace();
		}

		boolean bContinue = true;

		// 特定功能键处理
		if (!funcRun && !funcRun_ext)
		{
			/*
			 * 中免顾客信息窗口按键值与快捷键冲突,所以屏蔽此功能键 wangyong delete by 2013.6.17
			funcRun = true;

			// 执行功能键
			if (TaskExecute.getDefault().executeFuncKey(keyValue))
			{
				bContinue = false;
			}

			funcRun = false;*/
		}

		if (!bContinue)
		{
			e.doit = false;
		}

		// 如果是功能键则不回显
		if (keyValue != -1)
		{
			e.doit = false;
		}

		// 由刷卡槽事件发出的键盘事件,标记为触发自定义刷卡槽事件
		if (((e.data != null) && ((String) e.data).equals("MSR")) || MSR.havaMSR)
		{
			MSR.havaMSR = false;

			msrfinish = true;
			track1 = MSR.MSRTrack1;
			track2 = MSR.MSRTrack2;
			track3 = MSR.MSRTrack3;
		}

		// LINUX下会发生切换不回焦点的问题,所以由程序控制
		// 设置为默认需要切换回焦点
		e.data = null;

		// 执行自定义键盘事件
		if (event != null)
		{
			if (!msrfinish)
			{
				if (occukeypress)
				{
					event.keyDown(e, keyValue);
				}

				event.keyUp(e, keyValue);
			}
			else
			{
				// 定义了IC卡读卡键,不能直接用回车键结束触发,并且开启了IC卡功能,否则在读卡失败时将导致输入框响应回车按键
				if ((keyValue == GlobalVar.Enter) && GlobalInfo.isStartICCard && (ZmjcPassport_NewKeyListener.searchKeyCode(GlobalVar.ICInput) > 0) && (ICCard.getDefault() != null))
				{
					// 初始化IC卡卡号和金额
					ICCard.getDefault().initCard();

					// 无其他输入直接按回车启动读IC卡功能
					if ((track1.length() <= 0) && (track2.length() <= 0) && (track3.length() <= 0))
					{
						msrfinish = false;
						ZmjcPassport_NewKeyListener.sendKey(GlobalVar.ICInput);
					}
				}
				
				if ((keyValue == GlobalVar.Enter) && GlobalInfo.isStartBankTracker && (ZmjcPassport_NewKeyListener.searchKeyCode(GlobalVar.BankTracker) > 0) && ( BankTracker.getDefault() != null))
				{
					
					// 无其他输入直接按回车启动读IC卡功能
					if ((track1.length() <= 0) && (track2.length() <= 0) && (track3.length() <= 0))
					{
						msrfinish = false;
						ZmjcPassport_NewKeyListener.sendKey(GlobalVar.BankTracker);
					}
				}
				
				if (msrfinish)
				{
					msrfinish = false;
					event.msrFinish(e, track1, track2, track3);
					track1 = "";
					track2 = "";
					track3 = "";
				}
			}
		}
		else
		{
			if (msrfinish)
			{
				msrfinish = false;
				track1 = "";
				track2 = "";
				track3 = "";
			}
		}

		// 按键结束还原焦点
		if ((e.data == null) && !e.widget.isDisposed())
		{
			if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Text"))
			{
				((Text) e.widget).setFocus();
			}

			if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Combo"))
			{
				((Combo) e.widget).setFocus();
			}

			if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Table"))
			{
				((Table) e.widget).setFocus();
			}

			if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Button"))
			{
				((Button) e.widget).setFocus();
			}
		}

		// 标记本次按键结束
		keyValue = -1;
	}

	private boolean TextControlDo(Text txt, KeyEvent e)
	{
		if (!txt.getEditable() || !txt.getEnabled()) { return true; }

		// 重新开始键盘刷卡输入
		if (trackbuffer.length() <= 0)
		{
			isMsrKeyboard = true;
		}

		// IC读卡键处理
		if (keyValue == GlobalVar.ICInput)
		{
			if (ICInput("keydown")) { return true; }
		}
		
		if (keyValue == GlobalVar.BankTracker)
		{
			if (BankTrackerInput("keydown")) { return true; }
		}

		// 键盘刷卡槽处理
		if (((inputMode == MsrInput) || (inputMode == MsrKeyInput) || (inputMode == MsrKeyInputMarker)) && isMsrKeyboard)
		{
			PosLog.getLog(getClass()).debug("TextControl:"+e.keyCode+"  "+e.character+"  "+keyValue);
			MsrInputDo(txt, e);

			return true;
		}

		if (isBackSpace)
		{
			// 退格键
			if ((keyValue == GlobalVar.BkSp) && (txt.getText().length() > 0))
			{
				int iStart = txt.getSelection().x;
				int iSLength = txt.getSelectionCount();
				int iLength = txt.getText().length();
				String s;

				if (iSLength > 0)
				{
					s = txt.getText().substring(0, iStart);
					s += txt.getText().substring(iStart + iSLength, iLength);
					txt.setText(s);
					txt.setSelection(iStart);
				}
				else
				{
					if (iStart > 0)
					{
						if (iStart < iLength)
						{
							s = txt.getText().substring(0, iStart - 1);
							s += txt.getText().substring(iStart, iLength);
							txt.setText(s);
						}
						else
						{
							txt.setText(txt.getText().substring(0, iStart - 1));
						}

						txt.setSelection(iStart - 1);
					}
					else
					{
						txt.setSelection(0);
					}
				}

				return true;
			}
		}

		// 光标左
		if ((keyValue == GlobalVar.ArrowLeft) && (txt.getSelection().x > 0))
		{
			txt.setSelection(txt.getSelection().x - 1);

			return true;
		}

		// 光标右
		if (keyValue == GlobalVar.ArrowRight)
		{
			txt.setSelection(txt.getSelection().x + 1);

			return true;
		}
		
		// 整数输入
		if ((inputMode == IntegerInput) && (((keyValue != -1) && (keyValue < GlobalVar.Exit) && !((keyValue >= GlobalVar.Key0) && (keyValue <= GlobalVar.Key9))) || ((keyValue == -1) && !((e.character >= '0') && (e.character <= '9'))))) { return true; }

		// 小数输入
		if ((inputMode == DoubleInput) && (((keyValue != -1) && (keyValue < GlobalVar.Exit) && (!((keyValue >= GlobalVar.Key0) && (keyValue <= GlobalVar.Key9)) && !((keyValue == GlobalVar.Decimal) && (txt.getText().length() > 0) && (txt.getText().indexOf('.') < 0)))) || ((keyValue == -1) && (!((e.character >= '0') && (e.character <= '9')) && !((e.character == '.') && (txt.getText().length() > 0) && (txt.getText().indexOf('.') < 0)))))) { return true; }

		//
		if (((keyValue >= GlobalVar.Key0) && (keyValue <= GlobalVar.Div)) || (keyValue == GlobalVar.DoubleZero))
		{
			switch (keyValue)
			{
				case GlobalVar.Key0: // 数字0
					addKey(txt, "0");

					break;

				case GlobalVar.Key1: // 数字1
					addKey(txt, "1");

					break;

				case GlobalVar.Key2: // 数字2
					addKey(txt, "2");

					break;

				case GlobalVar.Key3: // 数字3
					addKey(txt, "3");

					break;

				case GlobalVar.Key4: // 数字4
					addKey(txt, "4");

					break;

				case GlobalVar.Key5: // 数字0
					addKey(txt, "5");

					break;

				case GlobalVar.Key6: // 数字6
					addKey(txt, "6");

					break;

				case GlobalVar.Key7: // 数字7
					addKey(txt, "7");

					break;

				case GlobalVar.Key8: // 数字8
					addKey(txt, "8");

					break;

				case GlobalVar.Key9: // 数字9
					addKey(txt, "9");

					break;

				case GlobalVar.DoubleZero: // 数字00
					addKey(txt, "00");

					break;

				case GlobalVar.Decimal: // 小数点
					addKey(txt, ".");

					break;

				case GlobalVar.Plus: // 加号
					addKey(txt, "+");

					break;

				case GlobalVar.Minu: // 减号
					addKey(txt, "-");

					break;

				case GlobalVar.Mul: // 乘号
					addKey(txt, "*");

					break;

				case GlobalVar.Div: // 除号
					addKey(txt, "/");

					break;
			}

			e.doit = false;

			return true;
		}

		return false;
	}

	public static void addKey(Text txt, String sLetter)
	{
		if (!txt.getEditable() || !txt.getEnabled()) { return; }

		int iStart = txt.getSelection().x;
		int iSLength = txt.getSelectionCount();
		int iLength = txt.getText().length();
		String s;

		if (iSLength > 0)
		{
			s = txt.getText().substring(0, iStart);
			s += sLetter;
			s += txt.getText().substring(iStart + iSLength, iLength);
			txt.setText(s);
		}
		else
		{
			if (iStart < iLength)
			{
				txt.insert(sLetter);
			}
			else
			{
				txt.append(sLetter);
			}
		}

		//
		txt.setSelection(iStart + sLetter.length());
	}

	private boolean ComboControlDo(Combo cmb, KeyEvent e)
	{
		e.doit = false;

		if (keyValue != GlobalVar.ArrowUp && keyValue != GlobalVar.ArrowDown)
		{
			e.doit = true;
		}

		switch (keyValue)
		{
			case GlobalVar.ArrowUp: // 上光标
				cmb.select(cmb.getSelectionIndex() - 1);

				return true;

			case GlobalVar.ArrowDown: // 下光标
				cmb.select(cmb.getSelectionIndex() + 1);

				return true;
		}

		return false;
	}

	private boolean TableControlDo(Table table, KeyEvent e)
	{
		e.doit = false;

		switch (keyValue)
		{
			case GlobalVar.ArrowUp:

				if (table.getSelectionIndex() > 0)
				{
					table.setSelection(table.getSelectionIndex() - 1);
				}

				return true;

			case GlobalVar.ArrowDown:

				if ((table.getSelectionIndex() < (table.getItemCount() - 1)) && (table.getItemCount() >= 0))
				{
					table.setSelection(table.getSelectionIndex() + 1);
				}

				return true;

			case GlobalVar.PageUp:

				if (!isPage) { return false; }

				if (table.getSelectionIndex() > 0)
				{
					int showpage = 0;

					if (pagesize == 0)
					{
						if (table.getSelectionIndex() > ((table.getBounds().height - table.getHeaderHeight()) / table.getItemHeight()))
						{
							pagesize = table.getSelectionIndex() - ((table.getBounds().height - table.getHeaderHeight()) / table.getItemHeight());
							showpage = pagesize - ((table.getBounds().height - table.getHeaderHeight()) / table.getItemHeight()) - 1;
						}
						else
						{
							showpage = 0;
						}
					}
					else
					{
						pagesize = pagesize - ((table.getBounds().height - table.getHeaderHeight()) / table.getItemHeight());
						showpage = pagesize - ((table.getBounds().height - table.getHeaderHeight()) / table.getItemHeight()) - 1;
					}

					if (showpage > 0)
					{
						table.setSelection(showpage);
					}
					else
					{
						table.setSelection(0);
						pagesize = 0;
					}
				}

				return true;

			case GlobalVar.PageDown:

				if (!isPage) { return false; }

				if (table.getSelectionIndex() < (table.getItemCount() - 1))
				{
					int showpage = 0;

					if (table.getSelectionIndex() < pagesize)
					{
						pagesize = table.getSelectionIndex();
					}

					pagesize = pagesize + ((table.getBounds().height - table.getHeaderHeight()) / table.getItemHeight());
					showpage = pagesize + ((table.getBounds().height - table.getHeaderHeight()) / table.getItemHeight()) + 1;

					if (showpage < (table.getItemCount() - 1))
					{
						table.setSelection(showpage);
					}
					else
					{
						table.setSelection(table.getItemCount() - 1);
						pagesize = table.getItemCount() - 1;
					}
				}

				return true;
		}

		return false;
	}

	private boolean MsrInputDo(Text txt, KeyEvent e)
	{
		// 放弃刷卡数据
		if (keyValue == GlobalVar.Exit)
		{
			msrfinish = false;
			firstmsrinputtime = -1;
			trackcount = 0;
			trackbuffer.delete(0, trackbuffer.length());

			track1 = "";
			track2 = "";
			track3 = "";

			return false;
		}

		if (((inputMode == MsrKeyInput) ) && (keyValue == GlobalVar.ArrowDown || keyValue == GlobalVar.ArrowUp || keyValue == GlobalVar.ArrowLeft || keyValue == GlobalVar.ArrowRight))
		{
			// 下一个按键不再进入键盘口刷卡槽处理,只做为键盘输入
			isMsrKeyboard = false;

			txt.setText(trackbuffer.toString());
			txt.setSelection(trackbuffer.length());

			return false;
		}

		// 记录第一个字符的输入时间
		if (trackbuffer.length() <= 0)
		{
			firstmsrinputtime = System.currentTimeMillis();
		}

		PosLog.getLog(getClass()).debug("键值："+keyValue);
		// 读到回车表示本次刷卡数据已完成
		if ((keyValue == GlobalVar.Enter) || (e.character == '\r') || (e.character == '\n'))
		{
			// 采用粘贴模式输入
			if ((trackbuffer.length() <= 0) && ((txt.getText().length() > 0) && !txt.getText().equals(txt.getSelectionText())))
			{
				// 磁道文本
				String text = txt.getText();
				trackbuffer.append(text);

				// 磁道数计数
				trackcount = 0;
				trackcount += text.split("\\?").length - 1;
				trackcount += text.split("/").length - 1;
			}

			// 如果尾部没有磁道结束符,则磁轨数加一
			String trackstr = trackbuffer.toString();
			if (!(trackstr.endsWith("\r") || trackstr.endsWith("\n") || trackstr.endsWith("?") || trackstr.endsWith("/")))
			{
				trackbuffer.append('\r');
				trackcount++;
			}

			// 检查输入平均时间是否合法,输入不合法放弃刷卡数据
			if (GlobalInfo.sysPara.msrspeed > 0)
			{
				long tm = System.currentTimeMillis() - firstmsrinputtime;

				if ((tm / trackbuffer.length()) > GlobalInfo.sysPara.msrspeed && ((inputMode != MsrKeyInput) && (inputMode != MsrKeyInputMarker)))
				{
					keyValue = GlobalVar.MsrError;

					msrfinish = false;
					firstmsrinputtime = -1;
					trackcount = 0;
					trackbuffer.delete(0, trackbuffer.length());

					track1 = "";
					track2 = "";
					track3 = "";

					txt.setText(Language.apply("刷卡输入无效"));
					txt.selectAll();

					return false;
				}
			}

			// 解析磁道信息
			StringBuffer tk1 = new StringBuffer();
			StringBuffer tk2 = new StringBuffer();
			StringBuffer tk3 = new StringBuffer();

			if ((MSR.getDefault() != null) && MSR.getDefault().parseTrack(trackbuffer, tk1, tk2, tk3))
			{
				track1 = tk1.toString();
				track2 = tk2.toString();
				track3 = tk3.toString();
			}
			else
			{
				int i;
				int j;
				int k;
				int flag;
				i = 0;
				j = trackbuffer.length();

				if ((trackcount == 1) || (trackcount == 2))
				{
					flag = 2;
				}
				else
				{
					flag = 1;
				}

				while ((i < j) && (flag <= 3))
				{
					// 确定磁道起始符
					while ((i < j) && ((trackbuffer.charAt(i) == '\n') || (trackbuffer.charAt(i) == '\r') || (trackbuffer.charAt(i) == '%') || (trackbuffer.charAt(i) == ';') || (trackbuffer.charAt(i) == '+')))
					{
						i++;
					}

					k = i;

					// 确定磁道结束符
					while ((k < j) && ((trackbuffer.charAt(k) != '\n') && (trackbuffer.charAt(k) != '\r') && (trackbuffer.charAt(k) != '?') && (trackbuffer.charAt(k) != '/')))
					{
						k++;
					}

					// 分别赋值给相应的磁道
					if ((i < j) && (k < j))
					{
						switch (flag)
						{
							case 1:
								track1 = trackbuffer.substring(i, k);

								break;

							case 2:
								track2 = trackbuffer.substring(i, k);

								break;

							case 3:
								track3 = trackbuffer.substring(i, k);

								break;
						}
					}

					//
					i = k + 1;
					flag++;
				}
			}

			// 标记磁道是否有前导符
			trackhaveprefix = false;
			if (trackbuffer.indexOf("%") >= 0)
				trackhaveprefix = true;
			if (trackbuffer.indexOf(";") >= 0)
				trackhaveprefix = true;
			if (trackbuffer.indexOf("+") >= 0)
				trackhaveprefix = true;

			// 开始新刷卡
			firstmsrinputtime = -1;
			trackcount = 0;
			trackbuffer.delete(0, trackbuffer.length());

			// 刷卡完成,返回为Enter
			msrfinish = true;
			keyValue = GlobalVar.Enter;

			return true;
		}

		// 记录磁轨数据,无效数据不记录
		if (e.character >= 32 && e.character <= 127)
		{
			trackbuffer.append(e.character);
		}

		// 磁轨数加一
		if ((e.character == '?') || (e.character == '/'))
		{
			trackcount++;
		}

		// 刷卡、键盘输入皆可的模式
		if ((inputMode == MsrKeyInput)||(inputMode == MsrKeyInputMarker))
		{
			addKey(txt, String.valueOf(e.character));

			if (trackbuffer.length() >= 2)
			{
				// 输入速率低，表明为键盘手工输入
				long tm = System.currentTimeMillis() - firstmsrinputtime;

				if ((GlobalInfo.sysPara.msrspeed <= 0) || ((tm / trackbuffer.length()) > GlobalInfo.sysPara.msrspeed))
				{
					// 下一个按键不再进入键盘口刷卡槽处理,只做为键盘输入
					
					if(inputMode == MsrKeyInput){
						isMsrKeyboard = false;
						txt.setText(trackbuffer.toString());
						txt.setSelection(trackbuffer.length());
					}else{
						String s = "";

						for (int i = 0; i < trackbuffer.length(); i++)
						{
							s += "*";
						}

						txt.setText(s);
						txt.setSelection(s.length());
					}
					
				}
				else
				{
					// 输入速率高，表明为键盘口刷卡输入,数据转为*号显示
					String s = "";

					for (int i = 0; i < trackbuffer.length(); i++)
					{
						s += "*";
					}

					txt.setText(s);
					txt.setSelection(s.length());
				}
			}
		}
		else
		{
			PosLog.getLog(getClass()).debug(String.valueOf(e.character)+"     "+trackbuffer.toString());
			// 显示到控件
			addKey(txt, "*");
		}
		

		// 必须是0,刷卡槽读入的数据,保证不会执行keyreleased事件
		keyValue = 0;

		return true;
	}

	public boolean BankTrackerInput(String keyinfo)
	{
		try
		{
			if (BankTracker.getDefault() == null) { return false; }

			String line = null;

			Control control = Display.getCurrent().getFocusControl();

			if (control.getClass().getName().equals("org.eclipse.swt.widgets.Text") && (control.getData() != null) && ((String) control.getData()).equals("MSRINPUT"))
			{
				if (Display.getCurrent() != null)
				{
					if ((control == null) || control.isDisposed()) { return true; }

					((Text) control).setText(Language.apply("请将操作转向刷卡设备..."));
					((Text) control).update();

					line = BankTracker.getDefault().getTracker();
					if (line == null  ||line.indexOf(";") <0)
					{
						if (line.trim().length() <= 0)
							line = Language.apply("刷卡失败");
						
						((Text) control).setText(line);
						((Text) control).setSelection(0, line.trim().length());

						return true;
					}

					if (line.indexOf(";") != -1)
					{
						String[] tmpTrack = line.split(";");
						if (tmpTrack.length > 0)
							MSR.MSRTrack1 = tmpTrack[0];
						if(tmpTrack.length>1)
							MSR.MSRTrack2 = tmpTrack[1];
						if (tmpTrack.length > 2)
							MSR.MSRTrack3 = tmpTrack[2];
					}
					else
					{
						MSR.MSRTrack1 = "";
						MSR.MSRTrack2 = line;
						MSR.MSRTrack3 = "";
					}

					// 标记有刷卡数据
					com.efuture.javaPos.Device.MSR.havaMSR = true;

					// ((Text) control).setText(line.substring(0, 8));
					((Text) control).setText("*************************");

					return true;
				}
			}

			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(getClass()).debug(er);
			return false;
		}
		finally
		{
		}
	
	}
	public boolean ICInput(String keyInfo)
	{
		try
		{
			if (ICCard.getDefault() == null) { return false; }

			String line = null;

			Control control = Display.getCurrent().getFocusControl();

			if (control.getClass().getName().equals("org.eclipse.swt.widgets.Text") && (control.getData() != null) && ((String) control.getData()).equals("MSRINPUT"))
			{
				if (Display.getCurrent() != null)
				{
					if ((control == null) || control.isDisposed()) { return true; }

					((Text) control).setText(Language.apply("正在读取IC卡,请等待..."));
					((Text) control).update();
					
					if ("device.ICCard.Muti_ICCard".equals(ConfigClass.ICCard1))
					{
						Muti_ICCard card = new Muti_ICCard();
						card.setCurPayCode(this.payCode);
						line = card.findCard();
					}
					else
					{
						line = ICCard.getDefault().findCard();
					}
					
					if (line == null || line.trim().length() <= 0)
					{
						String a = Language.apply("未插入IC卡 或 IC卡不在扫描范围内");
						((Text) control).setText(a);
						((Text) control).setSelection(0, a.length());

						return true;
					}
					else if (line.indexOf("error:") >= 0)
					{
						String a = line;
						((Text) control).setText(a);
						((Text) control).setSelection(0, a.length());

						return true;
					}

					if (line.indexOf(";") != -1)
					{
						String[] tmpTrack = line.split(";");
						if (tmpTrack.length > 0)
							MSR.MSRTrack1 = tmpTrack[0];
						if(tmpTrack.length>1)
							MSR.MSRTrack2 = tmpTrack[1];
						if (tmpTrack.length > 2)
							MSR.MSRTrack3 = tmpTrack[2];
					}
					else
					{
						MSR.MSRTrack1 = "";
						MSR.MSRTrack2 = line;
						MSR.MSRTrack3 = "";
					}

					// 标记有刷卡数据
					com.efuture.javaPos.Device.MSR.havaMSR = true;

					// 显示IC卡号
					// ((Text) control).setText(line.substring(0, 8));
					((Text) control).setText("*************************");

					return true;
				}
			}

			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(getClass()).debug(er);
			return false;
		}
		finally
		{
		}
	}
}
