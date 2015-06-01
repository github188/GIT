package com.efuture.javaPos.UI;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.Design.MenuFuncForm;
import com.swtdesigner.SWTResourceManager;

public class MenuFuncEvent
{
	private static int placeFlag = 0;
	private String role;
	private ArrayList currManinMenuArray = null;
	private ArrayList currTwoLevelMenuArray = null;
	private ArrayList currThreeLevelMenuArray = null;
	private ArrayList currMenuManinInfoArray = null;
	private ArrayList currMenuTwoLevelInfoArray = null;
	private ArrayList currMenuThreeLevelInfoArray = null;
	private Shell shell = null;
	private int numy = 0;
	private NewKeyListener key = null;
	private KeySelectionAdapter ksa = null;
	private MenuFuncBS mfbs = null;

	public MenuFuncEvent(MenuFuncForm mff)
	{
		this.currManinMenuArray = mff.getCurrManinMenuArray();
		this.currTwoLevelMenuArray = mff.getCurrTwoLevelMenuArray();
		this.currThreeLevelMenuArray = mff.getCurrThreeLevelMenuArray();
		this.role = mff.getRole();
		this.shell = mff.getShell();

		mfbs = CustomLocalize.getDefault().createMenuFuncBS();

		// print Button 事件
		mfbs.printButtonHandle = mff.print.handle;
		mff.print.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent arg0)
			{
				mfbs.printButtonEvent();
			}

			public void keyReleased(KeyEvent arg0)
			{
			}
		});

		// 是否有最小角色功能
		if (mff.appendDefaultRole())
		{
			if (this.role == null || this.role.trim().equals(""))
				this.role = "0005,0501,0502,0507,0006,0602";
			if (this.role.indexOf("0005") < 0)
			{
				this.role += ",0005"; // 退出主菜单
			}

			if (this.role.indexOf("0501") < 0)
			{
				this.role += ",0501"; // 重新登录
			}

			if (this.role.indexOf("0502") < 0)
			{
				this.role += ",0502"; // 重新登录
			}

			if (this.role.indexOf("0507") < 0)
			{
				this.role += ",0507"; // 退出系统
			}

			if (this.role.indexOf("0006") < 0)
			{
				this.role += ",0006"; // 关于主菜单
			}
			/*
			 * if (this.role.indexOf("0601") < 0) { this.role += ",0601"; //
			 * 帮助菜单 }
			 */
			if (this.role.indexOf("0602") < 0)
			{
				this.role += ",0602"; // 关于菜单
			}
		}

		// 设定键盘事件
		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
				keyPressed(e, key);
			}

			public void keyUp(KeyEvent e, int key)
			{
				keyReleased(e, key);
			}
		};

		key = new NewKeyListener();
		key.event = event;
		ksa = new KeySelectionAdapter();

		initAllButtonListen();
		initMostlyMenu();

		// 居中显示,去掉菜单第三列
		shell.setSize(540, 510);

		// Rectangle rec =
		// Display.getCurrent().getPrimaryMonitor().getClientArea();
		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);
	}

	private void initAllButtonListen()
	{
		try
		{
			final PaintListener p = new PaintListener()
			{
				public void paintControl(PaintEvent e)
				{
					Control control = Display.getCurrent().getFocusControl();

					if (control.getClass().getName().equals("org.eclipse.swt.widgets.Button"))
					{
						e.gc.setBackground(SWTResourceManager.getColor(37, 58, 254));
						e.gc.setForeground(SWTResourceManager.getColor(255, 255, 255));
						e.gc.fillRectangle(4, 4, control.getBounds().width - 8, control.getBounds().height - 8);
						e.gc.setFont(control.getFont());

						// int width = calcTextWidth(e.gc, ((Button)
						// control).getText());
						int height = e.gc.getFontMetrics().getHeight();
						e.gc.drawString(((Button) control).getText(), 4, (control.getBounds().height - height) / 2, true);
						e.gc.dispose();
					}
				}
				/*
				 * private int calcTextWidth(GC gc, String text) { int stWidth =
				 * 0;
				 * 
				 * for (int i = 0; i < text.length(); i++) { char c =
				 * text.charAt(i); stWidth += gc.getAdvanceWidth(c); }
				 * 
				 * return stWidth; }
				 */
			};

			final FocusAdapter focus = new FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
					if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Button"))
					{
						((Button) e.widget).addPaintListener(p);
						((Button) e.widget).redraw();
					}
				}

				public void focusLost(FocusEvent e)
				{
					if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Button"))
					{
						((Button) e.widget).removePaintListener(p);
						((Button) e.widget).redraw();
					}
				}
			};

			shell.addKeyListener(key);

			for (int i = 0; i < currManinMenuArray.size(); i++)
			{
				Button button = (Button) currManinMenuArray.get(i);
				button.addKeyListener(key);
				button.addSelectionListener(ksa);
				button.addFocusListener(focus);
			}

			for (int j = 0; j < currTwoLevelMenuArray.size(); j++)
			{
				Button button = (Button) currTwoLevelMenuArray.get(j);
				button.addKeyListener(key);
				button.addSelectionListener(ksa);
				button.addFocusListener(focus);
			}

			for (int k = 0; k < currThreeLevelMenuArray.size(); k++)
			{
				Button button = (Button) currThreeLevelMenuArray.get(k);
				button.addKeyListener(key);
				button.addSelectionListener(ksa);
				button.addFocusListener(focus);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void initMostlyMenu()
	{
		int j = 0;

		try
		{
			if (role != null)
			{
				placeFlag = 1;

				int index = 0;

				String[] roleArray = role.split(",");
				Iterator iterator = GlobalInfo.menuFunArray.iterator();
				currMenuManinInfoArray = new ArrayList();

				while (iterator.hasNext())
				{
					MenuFuncDef mfd = (MenuFuncDef) iterator.next();

					for (int i = 0; i < roleArray.length; i++)
					{
						if (roleArray[i].trim().equals("0000"))
							continue;

						if (mfd.code.trim().equals(roleArray[i].trim()) && (mfd.level == 1) && (mfd.showflag == 'Y') && (mfd.enableflag == 'Y'))
						{
							if (index >= currManinMenuArray.size())
								break;

							SaveCurrButton scb = new SaveCurrButton();

							Button button = (Button) currManinMenuArray.get(index);

							String btnText = "  " + String.valueOf(++j) + ". " + mfd.name;
							button.setText(btnText);

							button.setVisible(true);
							scb.setButton(button);
							scb.setMenuFuncDef(mfd);
							currMenuManinInfoArray.add(scb);

							if (index == 0)
							{
								button.setBackground(SWTResourceManager.getColor(0, 64, 128));
								button.setForeground(SWTResourceManager.getColor(255, 255, 255));
								rigthEnter(button, GlobalVar.ArrowUp, null);
							}

							index = index + 1;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void getTwoLevelMenu(String id, int keycode)
	{
		int j = 0;

		try
		{
			Iterator iterator = GlobalInfo.menuFunArray.iterator();
			int index = 0;
			currMenuTwoLevelInfoArray = new ArrayList();

			for (int i = 0; i < currTwoLevelMenuArray.size(); i++)
			{
				Button button = (Button) currTwoLevelMenuArray.get(i);
				button.setVisible(false);
			}

			if (role != null)
			{
				String[] roleArray = role.split(",");

				while (iterator.hasNext())
				{
					MenuFuncDef mfd = (MenuFuncDef) iterator.next();

					for (int i = 0; i < roleArray.length; i++)
					{
						if (roleArray[i].trim().equals("0000"))
							continue;

						if (mfd.code.trim().equals(roleArray[i].trim()) && mfd.sjcode.trim().equals(id) && (mfd.level == 2) && (mfd.showflag == 'Y') && (mfd.enableflag == 'Y'))
						{
							if (index >= currTwoLevelMenuArray.size())
								break;

							SaveCurrButton scb = new SaveCurrButton();
							Button button = (Button) currTwoLevelMenuArray.get(index);
							button.setVisible(true);

							if ((keycode == GlobalVar.ArrowRight) || (keycode == GlobalVar.Enter) || (keycode == GlobalVar.Key1) || (keycode == GlobalVar.Key2) || (keycode == GlobalVar.Key3) || (keycode == GlobalVar.Key4) || (keycode == GlobalVar.Key5) || (keycode == GlobalVar.Key6) || (keycode == GlobalVar.Key7) || (keycode == GlobalVar.Key8) || (keycode == GlobalVar.Key9))
							{
								if (index == 0)
								{
									button.setFocus();
									button.setBackground(SWTResourceManager.getColor(0, 64, 128));
									button.setForeground(SWTResourceManager.getColor(255, 255, 255));
								}

								placeFlag = 2;
								numy = 0;
							}

							j++;

							String btnText = "  " + String.valueOf(j) + ". " + mfd.name;
							button.setText(btnText);
							scb.setButton(button);
							scb.setMenuFuncDef(mfd);
							currMenuTwoLevelInfoArray.add(scb);
							index = index + 1;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void getThreeLevelMenu(String id, int keycode)
	{
		int j = 0;

		try
		{
			Iterator iterator = GlobalInfo.menuFunArray.iterator();
			int index = 0;
			currMenuThreeLevelInfoArray = new ArrayList();

			for (int i = 0; i < currThreeLevelMenuArray.size(); i++)
			{
				Button button = (Button) currThreeLevelMenuArray.get(i);
				button.setVisible(false);
			}

			if (role != null)
			{
				String[] roleArray = role.split(",");

				while (iterator.hasNext())
				{
					MenuFuncDef mfd = (MenuFuncDef) iterator.next();

					for (int i = 0; i < roleArray.length; i++)
					{
						if (roleArray[i].trim().equals("0000"))
							continue;

						if (mfd.code.trim().equals(roleArray[i].trim()) && mfd.sjcode.trim().equals(id) && (mfd.level == 3) && (mfd.showflag == 'Y') && (mfd.enableflag == 'Y'))
						{
							if (index >= currThreeLevelMenuArray.size())
								break;

							SaveCurrButton scb = new SaveCurrButton();
							Button button = (Button) currThreeLevelMenuArray.get(index);
							button.setVisible(true);

							if ((keycode == GlobalVar.ArrowRight) || (keycode == GlobalVar.Enter))
							{
								if (index == 0)
								{
									button.setFocus();
									button.setBackground(SWTResourceManager.getColor(0, 64, 128));
									button.setForeground(SWTResourceManager.getColor(255, 255, 255));
								}

								placeFlag = 3;
								numy = 0;
							}

							String btnText = "  " + String.valueOf(j++) + ". " + mfd.name;
							button.setText(btnText);

							scb.setButton(button);
							scb.setMenuFuncDef(mfd);
							currMenuThreeLevelInfoArray.add(scb);
							index = index + 1;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void rigthEnter(Object object, int keycode, KeyEvent e)
	{
		SaveCurrButton scb = null;

		switch (placeFlag)
		{
			case 1:

				for (int i = 0; i < currMenuManinInfoArray.size(); i++)
				{
					scb = (SaveCurrButton) currMenuManinInfoArray.get(i);

					if (scb.getButton() == object)
					{
						MenuFuncDef mfd = scb.getMenuFuncDef();

						if (mfd.mjflag == 'N')
						{
							if (e != null)
							{
								e.data = "";
							}

							getTwoLevelMenu(mfd.code, keycode);

							break;
						}
						else
						{
							if (keycode == GlobalVar.Enter)
							{
								mfbs.execFuncMenu(mfd, this);

								break;
							}

							break;
						}
					}
				}

				break;

			case 2:

				for (int i = 0; i < currMenuTwoLevelInfoArray.size(); i++)
				{
					scb = (SaveCurrButton) currMenuTwoLevelInfoArray.get(i);

					if (scb.getButton() == object)
					{
						MenuFuncDef mfd = scb.getMenuFuncDef();

						if (mfd.mjflag == 'N')
						{
							if (e != null)
							{
								e.data = "";
							}

							getThreeLevelMenu(mfd.code, keycode);

							break;
						}
						else
						{
							for (int j = 0; j < currThreeLevelMenuArray.size(); j++)
							{
								Button button = (Button) currThreeLevelMenuArray.get(j);
								button.setVisible(false);
							}

							if (keycode == GlobalVar.Enter)
							{
								mfbs.execFuncMenu(mfd, this);

								break;
							}

							break;
						}
					}
				}

				break;

			case 3:

				for (int i = 0; i < currMenuThreeLevelInfoArray.size(); i++)
				{
					scb = (SaveCurrButton) currMenuThreeLevelInfoArray.get(i);

					if (scb.getButton() == object)
					{
						MenuFuncDef mfd = scb.getMenuFuncDef();

						if (mfd.mjflag == 'Y')
						{
							if (keycode == GlobalVar.Enter)
							{
								mfbs.execFuncMenu(mfd, this);

								break;
							}

							break;
						}
					}
				}

				break;
		}
	}

	private void moveUpLevelMenu(String id)
	{
		try
		{
			int index = 0;
			Iterator iterator = null;

			switch (placeFlag)
			{
				case 1:
					break;

				case 2:
					iterator = currMenuManinInfoArray.iterator();

					while (iterator.hasNext())
					{
						SaveCurrButton scb = (SaveCurrButton) iterator.next();

						if (scb.getMenuFuncDef().code.trim().equals(id))
						{
							Button button = (Button) currManinMenuArray.get(index);
							button.setFocus();
							button.setBackground(SWTResourceManager.getColor(0, 64, 128));
							button.setForeground(SWTResourceManager.getColor(255, 255, 255));

							break;
						}

						index = index + 1;
					}

					placeFlag = 1;
					numy = index;

					break;

				case 3:
					iterator = currMenuTwoLevelInfoArray.iterator();

					while (iterator.hasNext())
					{
						SaveCurrButton scb = (SaveCurrButton) iterator.next();

						if (scb.getMenuFuncDef().code.trim().equals(id))
						{
							Button button = (Button) currTwoLevelMenuArray.get(index);
							button.setFocus();
							button.setBackground(SWTResourceManager.getColor(0, 64, 128));
							button.setForeground(SWTResourceManager.getColor(255, 255, 255));

							break;
						}

						index = index + 1;
					}

					placeFlag = 2;
					numy = index;

					break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void dispose()
	{
		if (currManinMenuArray != null)
		{
			currManinMenuArray.clear();
			currManinMenuArray = null;
		}

		if (currTwoLevelMenuArray != null)
		{
			currTwoLevelMenuArray.clear();
			currTwoLevelMenuArray = null;
		}

		if (currTwoLevelMenuArray != null)
		{
			currTwoLevelMenuArray.clear();
			currTwoLevelMenuArray = null;
		}

		if (currThreeLevelMenuArray != null)
		{
			currThreeLevelMenuArray.clear();
			currThreeLevelMenuArray = null;
		}

		if (currMenuManinInfoArray != null)
		{
			currMenuManinInfoArray.clear();
			currMenuManinInfoArray = null;
		}

		if (currMenuTwoLevelInfoArray != null)
		{
			currMenuTwoLevelInfoArray.clear();
			currMenuTwoLevelInfoArray = null;
		}

		if (currMenuThreeLevelInfoArray != null)
		{
			currMenuThreeLevelInfoArray.clear();
			currMenuThreeLevelInfoArray = null;
		}

		if (mfbs != null)
		{
			mfbs = null;
		}

		if (shell != null)
		{
			shell.dispose();
			shell = null;
		}
	}

	public void keyPressed(KeyEvent e, int key)
	{
		try
		{
			SaveCurrButton scb = null;

			switch (key)
			{
				case GlobalVar.Key1:
				case GlobalVar.Key2:
				case GlobalVar.Key3:
				case GlobalVar.Key4:
				case GlobalVar.Key5:
				case GlobalVar.Key6:
				case GlobalVar.Key7:
				case GlobalVar.Key8:
				case GlobalVar.Key9:

					switch (placeFlag)
					{
						case 1:

							if ((key - 1) <= currMenuManinInfoArray.size())
							{
								((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
								((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								numy = key - 2;
								scb = (SaveCurrButton) currMenuManinInfoArray.get(numy);

								rigthEnter(scb.getButton(), key, e);

								break;
							}
							else
							{
								numy = currMenuManinInfoArray.size() - 1;
							}

							break;

						case 2:

							if ((key - 1) <= currMenuTwoLevelInfoArray.size())
							{
								((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
								((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								numy = key - 2;
								scb = (SaveCurrButton) currMenuTwoLevelInfoArray.get(numy);

								scb.getButton().setFocus();
								scb.getButton().setBackground(SWTResourceManager.getColor(0, 64, 128));
								scb.getButton().setForeground(SWTResourceManager.getColor(255, 255, 255));

								rigthEnter(scb.getButton(), key, e);

								break;
							}
							else
							{
								numy = currMenuTwoLevelInfoArray.size() - 1;
							}

							break;

						case 3:
							break;
					}

					break;

				case GlobalVar.ArrowUp:

					if (numy > 0)
					{
						numy = numy - 1;

						switch (placeFlag)
						{
							case 1:
								((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
								((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								scb = (SaveCurrButton) currMenuManinInfoArray.get(numy);

								scb.getButton().setFocus();
								scb.getButton().setBackground(SWTResourceManager.getColor(0, 64, 128));
								scb.getButton().setForeground(SWTResourceManager.getColor(255, 255, 255));
								rigthEnter(scb.getButton(), GlobalVar.ArrowUp, e);

								break;

							case 2:
								((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
								((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								scb = (SaveCurrButton) currMenuTwoLevelInfoArray.get(numy);

								scb.getButton().setFocus();
								scb.getButton().setBackground(SWTResourceManager.getColor(0, 64, 128));
								scb.getButton().setForeground(SWTResourceManager.getColor(255, 255, 255));
								rigthEnter(scb.getButton(), GlobalVar.ArrowUp, e);

								break;

							case 3:
								((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
								((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								scb = (SaveCurrButton) currMenuThreeLevelInfoArray.get(numy);

								scb.getButton().setFocus();
								scb.getButton().setBackground(SWTResourceManager.getColor(0, 64, 128));
								scb.getButton().setForeground(SWTResourceManager.getColor(255, 255, 255));
								rigthEnter(scb.getButton(), GlobalVar.ArrowUp, e);

								break;
						}
					}
					else
					{
						numy = 0;
					}

					break;

				case GlobalVar.ArrowDown:

					switch (placeFlag)
					{
						case 1:

							if ((numy + 1) < currMenuManinInfoArray.size())
							{
								((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
								((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								numy = numy + 1;
								scb = (SaveCurrButton) currMenuManinInfoArray.get(numy);
								scb.getButton().setFocus();
								scb.getButton().setBackground(SWTResourceManager.getColor(0, 64, 128));
								scb.getButton().setForeground(SWTResourceManager.getColor(255, 255, 255));
								rigthEnter(scb.getButton(), GlobalVar.ArrowDown, e);

								break;
							}
							else
							{
								numy = currMenuManinInfoArray.size() - 1;
							}

							break;

						case 2:

							if ((numy + 1) < currMenuTwoLevelInfoArray.size())
							{
								((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
								((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								numy = numy + 1;
								scb = (SaveCurrButton) currMenuTwoLevelInfoArray.get(numy);

								scb.getButton().setFocus();
								scb.getButton().setBackground(SWTResourceManager.getColor(0, 64, 128));
								scb.getButton().setForeground(SWTResourceManager.getColor(255, 255, 255));
								rigthEnter(scb.getButton(), GlobalVar.ArrowDown, e);

								break;
							}
							else
							{
								numy = currMenuTwoLevelInfoArray.size() - 1;
							}

							break;

						case 3:

							if ((numy + 1) < currMenuThreeLevelInfoArray.size())
							{
								((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
								((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								numy = numy + 1;
								scb = (SaveCurrButton) currMenuThreeLevelInfoArray.get(numy);
								scb.getButton().setFocus();
								scb.getButton().setBackground(SWTResourceManager.getColor(0, 64, 128));
								scb.getButton().setForeground(SWTResourceManager.getColor(255, 255, 255));
								rigthEnter(scb.getButton(), GlobalVar.ArrowDown, e);

								break;
							}
							else
							{
								numy = currMenuThreeLevelInfoArray.size() - 1;
							}

							break;
					}

					break;

				case GlobalVar.MemberGrant://重百这里要跟左键键公用 add maxun
				case GlobalVar.ArrowLeft:
				case GlobalVar.Key0:

					switch (placeFlag)
					{
						case 1:

							// 没有
							break;

						case 2:

							if (currThreeLevelMenuArray != null)
							{
								for (int j = 0; j < currThreeLevelMenuArray.size(); j++)
								{
									Button button = (Button) currThreeLevelMenuArray.get(j);
									button.setVisible(false);
								}
							}

							for (int i = 0; i < currMenuTwoLevelInfoArray.size(); i++)
							{
								scb = (SaveCurrButton) currMenuTwoLevelInfoArray.get(i);

								if (scb.getButton() == e.getSource())
								{
									MenuFuncDef mfd = scb.getMenuFuncDef();
									((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
									((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
									moveUpLevelMenu(mfd.sjcode);

									break;
								}
							}

							break;

						case 3:

							for (int i = 0; i < currMenuThreeLevelInfoArray.size(); i++)
							{
								scb = (SaveCurrButton) currMenuThreeLevelInfoArray.get(i);

								if (scb.getButton() == e.getSource())
								{
									MenuFuncDef mfd = scb.getMenuFuncDef();
									((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
									((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
									moveUpLevelMenu(mfd.sjcode);

									break;
								}
							}

							break;
					}

					break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{
    		case GlobalVar.Validation://重百这里要跟右键公用 add maxun
			case GlobalVar.ArrowRight:

				switch (placeFlag)
				{
					case 1:
						((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
						((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
						rigthEnter(e.getSource(), GlobalVar.ArrowRight, e);

						if ((currMenuTwoLevelInfoArray != null) && (currMenuTwoLevelInfoArray.size() > 0))
						{
							SaveCurrButton scb = (SaveCurrButton) currMenuTwoLevelInfoArray.get(0);
							rigthEnter(scb.getButton(), GlobalVar.ArrowUp, e);
						}

						break;

					case 2:
						((Button) e.getSource()).setBackground(new Color(Display.getCurrent(), 238, 238, 238));
						((Button) e.getSource()).setForeground(new Color(Display.getCurrent(), 0, 0, 0));
						rigthEnter(e.getSource(), GlobalVar.ArrowRight, e);

						break;

					case 3:
						break;
				}

				break;

			case GlobalVar.Enter:
				rigthEnter(e.widget, key, e);

				break;

			case GlobalVar.Exit:
				shell.close();
				shell.dispose();
				shell = null;

				break;
		}
	}

	class KeySelectionAdapter extends SelectionAdapter
	{
		public void widgetSelected(SelectionEvent e)
		{
			// 在linux 下button 的enter 可以响应 release健 但window下不行，所以用选中代替
			if (!System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
			{
				// 确定点的是那个按钮
				boolean find = false;
				int i = 0;
				if (!find)
				{
					for (i = 0; i < currMenuManinInfoArray.size(); i++)
					{
						if (((SaveCurrButton) currMenuManinInfoArray.get(i)).getButton() == e.widget)
						{
							placeFlag = 1;
							find = true;
							break;
						}
					}
				}
				if (!find)
				{
					for (i = 0; i < currMenuTwoLevelInfoArray.size(); i++)
					{
						if (((SaveCurrButton) currMenuTwoLevelInfoArray.get(i)).getButton() == e.widget)
						{
							placeFlag = 2;
							find = true;
							break;
						}
					}
				}
				if (!find)
				{
					for (i = 0; i < currMenuThreeLevelInfoArray.size(); i++)
					{
						if (((SaveCurrButton) currMenuThreeLevelInfoArray.get(i)).getButton() == e.widget)
						{
							placeFlag = 3;
							find = true;
							break;
						}
					}
				}

				//
				// int key = NewKeyListener.searchKey(New);
				rigthEnter(e.widget, GlobalVar.Enter, null);
			}
		}
	}
}

class SaveCurrButton
{
	private Button button = null;
	private MenuFuncDef mfd = null;

	public void setButton(Button button)
	{
		this.button = button;
	}

	public Button getButton()
	{
		return button;
	}

	public void setMenuFuncDef(MenuFuncDef mfd)
	{
		this.mfd = mfd;
	}

	public MenuFuncDef getMenuFuncDef()
	{
		return mfd;
	}
}
