package com.efuture.commonKit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

//这个类用于消息框类,所有程序都调用
public class MessageBox
{
	private Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"
	private Button buttonDispose = null;
	NewKeyListener key = null;
	private int height = -1;
	private int width = -1;
	private int text_width = -1;
	private final int x = 20;
	private final int y = 33;
	private final int lineheight = 33;
	Label label = null;
	int info = -1;
	public final int min_MessageWidth = 50;
	public final int min_Messageheight = 30;
	public Button[] buttons = null;
	private boolean isValidateKey = false;
	private boolean oneOrtwo = false;
	private int lineheight_new = 33;
	private int textSize = 18;
	private int textWidth = 13;

	public MessageBox(String line1, int keyValue)
	{
		Create(line1, null, false, 0, 0, keyValue);
	}

	public MessageBox(String line1, Shell parent, boolean flag, int keyValue)
	{
		Create(line1, parent, flag, 0, 0, keyValue);
	}

	public MessageBox(String line1, Shell parent, boolean flag, final int X_POSITION, final int Y_POSITION, int keyValue)
	{
		Create(line1, parent, flag, X_POSITION, Y_POSITION, keyValue);
	}

	public MessageBox(String line1)
	{
		Create(line1, null, false, 0, 0, -1);
	}

	public MessageBox(String line1, Shell parent, boolean flag)
	{
		Create(line1, parent, flag, 0, 0, -1);
	}

	public MessageBox(String line1, Shell parent, boolean flag, final int X_POSITION, final int Y_POSITION)
	{
		Create(line1, parent, flag, X_POSITION, Y_POSITION, -1);
	}
	
	

	private void Create(final String line1, Shell parent, final boolean flag, final int X_POSITION, final int Y_POSITION, final int keyValue)
	{
		if (keyValue == -1)
			isValidateKey = false;
		else if (keyValue != GlobalVar.Enter)
			isValidateKey = true;

		GlobalInfo.enablequickkey = false;

		final Display display = Display.getDefault();
		display.syncExec(new Runnable()
		{
			public void run()
			{
				Control focus = Display.getDefault().getFocusControl();
				String line = null;
				String choice ="\n"+Language.apply("1-是 / 2-否");

				if (flag == false)
				{
					line = line1 + "\n";
				}
				else
				{
					oneOrtwo = true;
					isValidateKey = true;
					line = line1 + "\n" + choice + "\n";
				}

				String[] lines = line.split("\n");
				height = (lines.length + 2) * lineheight;

				int max_length = -1;

				for (int i = 0; i < lines.length; i++)
				{
					if (Convert.countLength(lines[i]) > max_length)
					{
						max_length = Convert.countLength(lines[i]);
					}
				}

				text_width = max_length * 13;
				width = text_width + (x * 2);

				if (width < min_MessageWidth)
				{
					width = min_MessageWidth; // min of messageBox width
				}

				if (height < min_Messageheight)
				{
					height = min_Messageheight;
				}

				// 创建窗口
				createSShell(lines);

				// 键盘事件
				NewKeyEvent event = new NewKeyEvent()
				{
					public void keyDown(KeyEvent e, int key)
					{
						info = key;
					}

					public void keyUp(KeyEvent e, int key)
					{
						if (oneOrtwo)
						{
							if (key == GlobalVar.Key1 || key == GlobalVar.Key2)
							{
								e.doit = false;

								GlobalInfo.enablequickkey = true;
								sShell.close();
								sShell.dispose();
								while (Display.getCurrent().readAndDispatch());
							}
						}
						else if (keyValue == -1 || key == keyValue)
						{
							e.doit = false;

							GlobalInfo.enablequickkey = true;
							sShell.close();
							sShell.dispose();
							while (Display.getCurrent().readAndDispatch());
						}
						//
						else if (keyValue == 100 )
						{
							e.doit = false;

							GlobalInfo.enablequickkey = true;
							sShell.close();
							sShell.dispose();
							while (Display.getCurrent().readAndDispatch());
						}
					}
				};
				NewKeyListener key = new NewKeyListener();
				key.event = event;
				buttonDispose.addKeyListener(key);
				sShell.addKeyListener(key);

				// 鼠标按钮
				if (buttons != null && buttons.length > 0)
				{
					for (int i = 0; i < buttons.length; i++)
					{
						buttons[i].addSelectionListener(new SelectionAdapter()
						{
							public void widgetSelected(SelectionEvent selectionevent)
							{
								Button btn = (Button) selectionevent.widget;

								char c = btn.getText().trim().charAt(0);
								if (c >= '0' && c <= '9')
									info = GlobalVar.Key0 + (c - '0');
								else if (btn.getText().trim().startsWith(Language.apply("任意键")) || btn.getText().trim().startsWith("确定"))
									info = GlobalVar.Enter;
								else
									info = GlobalVar.Exit;

								GlobalInfo.enablequickkey = true;
								sShell.close();
								sShell.dispose();

								while (Display.getCurrent().readAndDispatch());
							}
						});

						buttons[i].addKeyListener(new KeyAdapter()
						{
							public void keyReleased(KeyEvent keyevent)
							{
								Button btn = (Button) keyevent.widget;
								int index = Convert.toInt(btn.getData());
								int key = NewKeyListener.searchKey(keyevent.keyCode);
								if (key == GlobalVar.ArrowLeft || key == GlobalVar.ArrowRight)
								{
									if (key == GlobalVar.ArrowLeft)
										index--;
									if (key == GlobalVar.ArrowRight)
										index++;
									if (index < 0)
										index = buttons.length - 1;
									if (index > buttons.length - 1)
										index = 0;
									buttons[index].forceFocus();
								}
								else
								{
									info = key;

									GlobalInfo.enablequickkey = true;
									sShell.close();
									sShell.dispose();
									while (Display.getCurrent().readAndDispatch());
									/*
									 * // 先找有对应按键触发的按钮 index = -1; for (int
									 * i=0;i<buttons.length;i++) { char c =
									 * buttons[i].getText().trim().charAt(0); if
									 * (c >= '0' && c <= '9' && key ==
									 * GlobalVar.Key0 + (c - '0')) { index = i;
									 * break; } } // 再找任意键可触发的按钮 if (index < 0)
									 * { for (int i=0;i<buttons.length;i++) {
									 * char c =
									 * buttons[i].getText().trim().charAt(0); if
									 * (!(c >= '0' && c <= '9')) { index = i;
									 * break; } } } if (index >= 0) {
									 * buttons[index].forceFocus(); info = key;
									 * 
									 * sShell.close(); sShell.dispose();
									 * 
									 * while
									 * (Display.getCurrent().readAndDispatch());
									 * }
									 */
								}
							}
						});
					}
				}

				// 定位
				// Rectangle area = display.getPrimaryMonitor().getBounds();
				sShell.setSize(width, height);
				int xx = (GlobalVar.rec.x / 2) - (width / 2);
				int yy = (GlobalVar.rec.y / 2) - (height / 2);
				xx = xx + X_POSITION;
				yy = yy + Y_POSITION;
				sShell.setLocation(xx, yy);

				// 加载背景图片
				Image bkimg = ConfigClass.changeBackgroundImage(this, sShell, null);

				if (buttons != null && buttons.length > 0)
					buttons[0].forceFocus();
				sShell.open();
				sShell.forceActive();
				while (!sShell.isDisposed())
				{
					if (buttons == null || buttons.length <= 0)
						buttonDispose.forceFocus();

					if (!display.readAndDispatch())
					{
						display.sleep();
					}
				}

				// 释放背景图片
				ConfigClass.disposeBackgroundImage(bkimg);

				if (focus != null && !focus.isDisposed())
				{
					focus.setFocus();
				}
			}
		});
	}
	
	
	public MessageBox(String line1, Shell parent, boolean flag, final int X_POSITION, final int Y_POSITION,final int keyValue,final boolean isSecMonitor,final int time,boolean isbotton,int textHigh,int textWidth,int textSize)
	{
		Create(line1, parent, flag, X_POSITION, Y_POSITION, keyValue,isSecMonitor,time,isbotton,textHigh,textWidth,textSize);
	}
	
//	文字内容,父Shell,显示选项文字描述,坐标x,坐标y,键值,是否显示在第二屏，倒计时时间(s),是否显示按钮
	private void Create(final String line1, Shell parent, final boolean flag, final int X_POSITION, final int Y_POSITION, final int keyValue,final boolean isSecMonitor,final int time,boolean isbotton,int textHigh,final int textWidth,int textSize)
	{

		if (keyValue == -1)
			isValidateKey = false;
		else if (keyValue != GlobalVar.Enter)
			isValidateKey = true;

		GlobalInfo.enablequickkey = false;
		
		final boolean bakMouseMode = ConfigClass.MouseMode;
		//是否显示按钮
		if(isbotton){
			ConfigClass.MouseMode = true;
		}
		if(textHigh > 0) lineheight_new = textHigh;
		if(textSize > 0) this.textSize = textSize; 
		if(textWidth >0) this.textWidth = textWidth;
		final Display display = Display.getDefault();
		display.syncExec(new Runnable()
		{
			public void run()
			{
				Control focus = Display.getDefault().getFocusControl();
				String line = null;
				String choice ="\n"+Language.apply("1-是 / 2-否");

				if (flag == false)
				{
					line = line1 + "\n";
				}
				else
				{
					oneOrtwo = true;
					isValidateKey = true;
					line = line1 + "\n" + choice + "\n";
				}

				String[] lines = line.split("\n");
				height = (lines.length + 2) * lineheight_new;
				//height = (lines.length + 2) * 60;
				int max_length = -1;

				for (int i = 0; i < lines.length; i++)
				{
					if (Convert.countLength(lines[i]) > max_length)
					{
						max_length = Convert.countLength(lines[i]);
					}
				}

				//text_width = max_length * 13;
				text_width = max_length * textWidth;
				width = text_width + (x * 2);

				if (width < min_MessageWidth)
				{
					width = min_MessageWidth; // min of messageBox width
				}

				if (height < min_Messageheight)
				{
					height = min_Messageheight;
				}

				// 创建窗口
				createSShellNew(lines);

				// 键盘事件
				NewKeyEvent event = new NewKeyEvent()
				{
					public void keyDown(KeyEvent e, int key)
					{
						info = key;
					}

					public void keyUp(KeyEvent e, int key)
					{
						if (oneOrtwo)
						{
							if (key == GlobalVar.Key1 || key == GlobalVar.Key2)
							{
								e.doit = false;

								GlobalInfo.enablequickkey = true;
								ConfigClass.MouseMode = bakMouseMode;
								sShell.close();
								sShell.dispose();
								while (Display.getCurrent().readAndDispatch());
							}
						}
						else if (keyValue == -1 || key == keyValue)
						{
							e.doit = false;

							GlobalInfo.enablequickkey = true;
							ConfigClass.MouseMode = bakMouseMode;
							sShell.close();
							sShell.dispose();
							while (Display.getCurrent().readAndDispatch());
						}
					}
				};
				NewKeyListener key = new NewKeyListener();
				key.event = event;
				buttonDispose.addKeyListener(key);
				sShell.addKeyListener(key);

				// 鼠标按钮
				if (buttons != null && buttons.length > 0)
				{
					for (int i = 0; i < buttons.length; i++)
					{
						buttons[i].addSelectionListener(new SelectionAdapter()
						{
							public void widgetSelected(SelectionEvent selectionevent)
							{
								Button btn = (Button) selectionevent.widget;

								char c = btn.getText().trim().charAt(0);
								if (c >= '0' && c <= '9')
									info = GlobalVar.Key0 + (c - '0');
								else if (btn.getText().trim().startsWith("任意键") || btn.getText().trim().startsWith("确定"))
									info = GlobalVar.Enter;
								else
									info = GlobalVar.Exit;

								GlobalInfo.enablequickkey = true;
								ConfigClass.MouseMode = bakMouseMode;
								sShell.close();
								sShell.dispose();

								while (Display.getCurrent().readAndDispatch());
							}
						});

						buttons[i].addKeyListener(new KeyAdapter()
						{
							public void keyReleased(KeyEvent keyevent)
							{
								Button btn = (Button) keyevent.widget;
								int index = Convert.toInt(btn.getData());
								int key = NewKeyListener.searchKey(keyevent.keyCode);
								if (key == GlobalVar.ArrowLeft || key == GlobalVar.ArrowRight)
								{
									if (key == GlobalVar.ArrowLeft)
										index--;
									if (key == GlobalVar.ArrowRight)
										index++;
									if (index < 0)
										index = buttons.length - 1;
									if (index > buttons.length - 1)
										index = 0;
									buttons[index].forceFocus();
								}
								else
								{
									info = key;

									GlobalInfo.enablequickkey = true;
									ConfigClass.MouseMode = bakMouseMode;
									sShell.close();
									sShell.dispose();
									while (Display.getCurrent().readAndDispatch());
									/*
									 * // 先找有对应按键触发的按钮 index = -1; for (int
									 * i=0;i<buttons.length;i++) { char c =
									 * buttons[i].getText().trim().charAt(0); if
									 * (c >= '0' && c <= '9' && key ==
									 * GlobalVar.Key0 + (c - '0')) { index = i;
									 * break; } } // 再找任意键可触发的按钮 if (index < 0)
									 * { for (int i=0;i<buttons.length;i++) {
									 * char c =
									 * buttons[i].getText().trim().charAt(0); if
									 * (!(c >= '0' && c <= '9')) { index = i;
									 * break; } } } if (index >= 0) {
									 * buttons[index].forceFocus(); info = key;
									 * 
									 * sShell.close(); sShell.dispose();
									 * 
									 * while
									 * (Display.getCurrent().readAndDispatch());
									 * }
									 */
								}
							}
						});
					}
				}
				//height = 200;
				//width = 400;
				// 定位
				// Rectangle area = display.getPrimaryMonitor().getBounds();
				sShell.setSize(width, height);
				int xx = (GlobalVar.rec.x / 2) - (width / 2);
				int yy = (GlobalVar.rec.y / 2) - (height / 2);
				if(isSecMonitor){
					if (Display.getDefault().getMonitors().length > 1)
			        {
			            Rectangle area = null;

			            for (int i = 0; i < Display.getDefault().getMonitors().length; i++)
			            {
			                if (!Display.getDefault().getMonitors()[i].equals(Display.getDefault().getPrimaryMonitor()))
			                {
			                    area = Display.getDefault().getMonitors()[i].getBounds();

			                    break;
			                }
			            }

			            System.out.println("test 1");

			            if (area == null)
			            {
			                System.out.println("test 2");

			                return;
			            }

			            if (area.width < 1000)
			            {
			                GlobalVar.secFont = -4;
			            }

			            xx      = (area.x/ 2) - (width / 2)+GlobalVar.rec.x;
			            if(area.y>0){
			            	 yy      = (area.y/ 2) - (height / 2);
			            }
			        }
				}
				xx = xx + X_POSITION;
				yy = yy + Y_POSITION;
				sShell.setLocation(xx, yy);

				// 加载背景图片
				Image bkimg = ConfigClass.changeBackgroundImage(this, sShell, null);

				if (buttons != null && buttons.length > 0)
					buttons[0].forceFocus();
				sShell.open();
				sShell.forceActive();
				
				//倒计时自动关闭窗口
				if(time>0){
					int timer = time*10;
	            	while(true)
	    			{
	    				try
	    				{
	    					timer = timer - 1;
	    				
	    					//刷新界面交互
	    					while (Display.getCurrent().readAndDispatch());
	        				
	    					Thread.sleep(100);
	    					if(sShell.isDisposed()||sShell == null) break;
	    					if (timer <= 0)
	    					{
	    						if (sShell != null)
	    						{
									GlobalInfo.enablequickkey = true;
	    							ConfigClass.MouseMode = bakMouseMode;
	    							sShell.close();
	    							sShell.dispose();
	    						}
	    						break;
	    					}
	    				}
	    				catch(Exception ex)
	    				{
	    					ex.printStackTrace();
	    				}
	    			}		
				}
				
				while (!sShell.isDisposed())
				{
					if (buttons == null || buttons.length <= 0)
						buttonDispose.forceFocus();

					if (!display.readAndDispatch())
					{
						display.sleep();
					}
				}

				// 释放背景图片
				ConfigClass.disposeBackgroundImage(bkimg);

				if (focus != null && !focus.isDisposed())
				{
					focus.setFocus();
				}
			}
		});
	
	}

	public int verify()
	{
		for (int i = 0; ConfigClass.MessageBoxVerify != null && i < ConfigClass.MessageBoxVerify.size(); i++)
		{
			String[] key = ((String) ConfigClass.MessageBoxVerify.elementAt(i)).split("\\,");
			if (info == Convert.toInt(key[0]) && key.length >= 2) { return Convert.toInt(key[1]); }

		}
		return info;
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell(String[] arg)
	{
		if (ConfigClass.MouseMode)
		{
			sShell = new Shell(GlobalVar.style_windows);
			height += 25;
		}
		else
		{
			sShell = new Shell(GlobalVar.style_linux);
		}
		sShell.setText(Language.apply("提示"));
		buttonDispose = new Button(sShell, SWT.NONE);
		buttonDispose.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				if (isValidateKey)
					return;
				info = GlobalVar.Enter; // NewKeyListener.searchKey(13);

				GlobalInfo.enablequickkey = true;
				sShell.close();
				sShell.dispose();

				while (Display.getCurrent().readAndDispatch());
			}
		});
		buttonDispose.setBounds(new org.eclipse.swt.graphics.Rectangle(0, 0, 1, 1));

		for (int i = 0; i < arg.length; i++)
		{
			boolean showmsg = true;
			int y1 = 0, x1 = 0;

			// 动态增加鼠标按钮
			if (ConfigClass.MouseMode && i == arg.length - 1)
			{
				String[] btntext = null;
				if (arg[i] != null && arg[i].split("/").length >= 2)
				{
					btntext = arg[i].split("/");

					showmsg = false;
				}
				else
				{
					if (arg[i] != null && arg[i].indexOf(" - ") > 0 && arg[i].trim().charAt(0) >= '1' && arg[i].trim().charAt(0) <= '9')
					{
						int wd = 0;
						btntext = new String[arg[i].trim().charAt(0) - '0' + 1];
						for (int j = 0; j < btntext.length; j++)
						{
							if (j == btntext.length - 1)
								btntext[j] = Language.apply(" 放弃 ");
							else
								btntext[j] = " " + (j + 1) + Language.apply("-选 ");
							wd += Convert.countLength(btntext[j]) * 13;
						}
						if (wd > text_width)
						{
							x1 = wd - text_width;
							width = wd + (x * 2) + (btntext.length - 1) * 13;
						}
					}
					else
						btntext = new String[] { Language.apply("  确定  ") };

					y1 = 2;

					height += y1 * lineheight;
				}

				if (btntext != null && btntext.length > 0)
				{
					int maxbtntext = 0;
					buttons = new Button[btntext.length];
					for (int j = 0; j < btntext.length; j++)
					{
						if (Convert.countLength(btntext[j]) > maxbtntext)
						{
							maxbtntext = Convert.countLength(btntext[j]);
						}
					}
					maxbtntext = maxbtntext * 13;

					for (int j = 0; j < btntext.length; j++)
					{
						buttons[j] = new Button(sShell, SWT.NONE);
						buttons[j].setData(String.valueOf(j));
						buttons[j].setText(btntext[j].trim());
						buttons[j].setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
						buttons[j].setBounds(x + (text_width + x1 - maxbtntext * btntext.length) / 2 + (j * (maxbtntext + 13)), y + ((i + y1) * lineheight), maxbtntext, lineheight);
					}
				}
			}

			if (showmsg)
			{
				label = new Label(sShell, SWT.CENTER);
				label.setBounds(new org.eclipse.swt.graphics.Rectangle(x, y + (i * lineheight), text_width, lineheight));
				label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
				label.setText(arg[i]);
			}
		}
	}
	
	private void createSShellNew(String[] arg)
	{
		if (ConfigClass.MouseMode)
		{
			sShell = new Shell(GlobalVar.style_windows);
			height += 25;
		}
		else
		{
			sShell = new Shell(GlobalVar.style_linux);
		}
		sShell.setText("提示");
		buttonDispose = new Button(sShell, SWT.NONE);
		buttonDispose.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				if (isValidateKey)
					return;
				info = GlobalVar.Enter; // NewKeyListener.searchKey(13);

				GlobalInfo.enablequickkey = true;
				sShell.close();
				sShell.dispose();

				while (Display.getCurrent().readAndDispatch());
			}
		});
		buttonDispose.setBounds(new org.eclipse.swt.graphics.Rectangle(0, 0, 1, 1));

		for (int i = 0; i < arg.length; i++)
		{
			boolean showmsg = true;
			int y1 = 0, x1 = 0;

			// 动态增加鼠标按钮
			if (ConfigClass.MouseMode && i == arg.length - 1)
			{
				String[] btntext = null;
				if (arg[i] != null && arg[i].split("/").length >= 2)
				{
					btntext = arg[i].split("/");

					showmsg = false;
				}
				else
				{
					if (arg[i] != null && arg[i].indexOf(" - ") > 0 && arg[i].trim().charAt(0) >= '1' && arg[i].trim().charAt(0) <= '9')
					{
						int wd = 0;
						btntext = new String[arg[i].trim().charAt(0) - '0' + 1];
						for (int j = 0; j < btntext.length; j++)
						{
							if (j == btntext.length - 1)
								btntext[j] = " 放弃 ";
							else
								btntext[j] = " " + (j + 1) + "-选 ";
							wd += Convert.countLength(btntext[j]) * 13;
						}
						if (wd > text_width)
						{
							x1 = wd - text_width;
							width = wd + (x * 2) + (btntext.length - 1) * 13;
						}
					}
					else
						btntext = new String[] { "  确定  " };

					y1 = 2;

					height += y1 * lineheight;
				}

				if (btntext != null && btntext.length > 0)
				{
					int maxbtntext = 0;
					buttons = new Button[btntext.length];
					for (int j = 0; j < btntext.length; j++)
					{
						if (Convert.countLength(btntext[j]) > maxbtntext)
						{
							maxbtntext = Convert.countLength(btntext[j]);
						}
					}
					maxbtntext = maxbtntext * 26;

					for (int j = 0; j < btntext.length; j++)
					{
						buttons[j] = new Button(sShell, SWT.NONE);
						buttons[j].setData(String.valueOf(j));
						buttons[j].setText(btntext[j].trim());
						buttons[j].setFont(SWTResourceManager.getFont("宋体", textSize, SWT.NONE));
						buttons[j].setBounds(x + (text_width + x1 - maxbtntext * btntext.length) / 2 + (j * (maxbtntext + 13)), y + ((i + y1) * lineheight_new), maxbtntext, lineheight_new);
					}
				}
			}

			if (showmsg)
			{
				label = new Label(sShell, SWT.CENTER);
				label.setBounds(new org.eclipse.swt.graphics.Rectangle(x, y + (i * lineheight), text_width, lineheight));
				label.setFont(SWTResourceManager.getFont("宋体", textSize, SWT.NONE));
				label.setText(arg[i]);
			}
		}
	}
}
