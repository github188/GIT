package com.efuture.commonKit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class TextBox
{
	public static final int AllInput = -1;
	public static final int IntegerInput = 0;
	public static final int DoubleInput = 1;
	public static final int MsrInput = 2;
	public static final int MsrKeyInput = 3;
	public static final int MsrRetTracks =100;
	
	public static final int AliPayInput = 200;
	
	public static boolean isZhcn = false;
	private Text text;
	private String value = null;
	private boolean result = true;
	private int curMode = DoubleInput;
	public String Track1 = null;
	public String Track2 = null;
	public String Track3 = null;

	public String hit = null;
	public int AutoInput = -1;

	public boolean open(String title, String lbl, String help, StringBuffer txt_value, int modeType)
	{
		return open(title, lbl, help, txt_value, 0, 0, false, modeType,-1);
	}

	public boolean open(String title, String lbl, String help, StringBuffer txt_value, int modeType,int limit)
	{
		return open(title, lbl, help, txt_value, 0, 0, false, modeType, limit);
	}
	
	public boolean open(String title, String lbl, String help, StringBuffer txt_value, final double min, final double max, final boolean flag, int modeType)
	{
		return open(title, lbl, help, txt_value, min, max, flag, modeType, -1);
	}

	public boolean open(String title, String lbl, String help, StringBuffer txt_value, final double min, final double max, final boolean flag)
	{
		return open(title, lbl, help, txt_value, min, max, flag, DoubleInput, -1);
	}

	public boolean open(String title, String lbl, String help, StringBuffer txt_value, final double min, final double max, final boolean flag, int modeType, int limit)
	{
		return this.open(title, lbl, help, txt_value, min, max, flag, modeType, limit, null, -1);
	}

	public boolean open(String title, String lbl, String help, StringBuffer txt_value, final double min, final double max, final boolean flag, int modeType, int limit, String hiit, int AutoInput)
	{
		this.hit = hiit;
		curMode = modeType;
		this.AutoInput = AutoInput;
		final Display display = Display.getDefault();
		final Shell shell = new Shell(GlobalVar.style_linux);
		shell.setLayout(new FormLayout());
		shell.setSize(shwidth, shheight);

		final Label titleLabel = new Label(shell, SWT.NONE);
		final FormData formData = new FormData();
		formData.right = new FormAttachment(0, 500);
		formData.left = new FormAttachment(0, 15);
		formData.bottom = new FormAttachment(0, 40);
		formData.top = new FormAttachment(0, 15);
		titleLabel.setLayoutData(formData);
		titleLabel.setText("Title");
		titleLabel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		titleLabel.setText(title);

		if (lbl.equals("PASSWORD")) text = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		else text = new Text(shell, SWT.BORDER);
		final FormData formData_1 = new FormData();
		formData_1.left = new FormAttachment(0, 15);
		formData_1.bottom = new FormAttachment(0, 80);
		formData_1.top = new FormAttachment(0, 55);
		formData_1.right = new FormAttachment(0, 500);
		text.setLayoutData(formData_1);
		text.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		if (limit != -1) text.setTextLimit(limit);

		if (txt_value != null)
		{
			text.setText(txt_value.toString());
		}

		text.selectAll();

		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
			}

			public void msrFinish(KeyEvent e, String track1, String track2, String track3)
			{
				Track1 = track1;
				Track2 = track2;
				Track3 = track3;

				shell.close();
				shell.dispose();

				while (Display.getCurrent().readAndDispatch())
					;
			}

			public void keyUp(KeyEvent e, int key)
			{
				switch (key)
				{
					case GlobalVar.Clear:
						text.setText("");
						break;
					case GlobalVar.Exit:
						result = false;
						shell.close();
						shell.dispose();

						break;
					case GlobalVar.Quantity:
						if (curMode == AllInput) NewKeyListener.addKey(text, "*");

						break;
					case GlobalVar.Back:
					case GlobalVar.Minu:
						if ((curMode == IntegerInput || curMode == DoubleInput) && 
							text.getText().length() <= 0)
						{
							NewKeyListener.addKey(text, "-");
						}
						else if(curMode == AliPayInput)
						{
							NewKeyListener.addKey(text, "");
							break;
						}
						else 
						{
							if (NewKeyListener.curInputMode != 4) //处理输入中文或所有可显字符
								NewKeyListener.addKey(text, "-");
						}
						break;
						
					case GlobalVar.Enter:

						if(GlobalInfo.sysPara.isNull != null && GlobalInfo.sysPara.isNull.equals("N"))
						{
							if (text.getText().length() <= 0) { return; }
						}

						value = text.getText();
						
						if (GlobalInfo.sysPara.isGoodsMoney0 != 'Y') 
						{
							if (flag)
							{
								try
								{
									double number = Double.parseDouble(value);
	
									if ((number < min) || (number > max))
									{
										if (hit == null)
										{
											new MessageBox(Language.apply("输入数据必须为{0}和{1}之间的数值",
											                              new Object[]{ManipulatePrecision.doubleToString(min, 4, 1, true),ManipulatePrecision.doubleToString(max, 4, 1, true)}));
//											new MessageBox("输入数据必须为 " + ManipulatePrecision.doubleToString(min, 4, 1, true) + " 和 "
//											               + ManipulatePrecision.doubleToString(max, 4, 1, true) + " 之间的数值");
										}
										else
										{
											new MessageBox(hit);
										}
										text.setFocus();
										text.selectAll();
										return;
									}
								}
								catch (Exception er)
								{
									new MessageBox(Language.apply("输入数据必须为数字"));
									text.setFocus();
									text.selectAll();
	
									return;
								}
							}
						}

						// 刷卡、键盘皆可模式,输入的文本作为二轨信息
						if (curMode == MsrKeyInput || curMode == MsrRetTracks)
						{
							Track2 = value;
						}

						shell.close();
						shell.dispose();

						break;
				}
			}
		};

		NewKeyListener key = new NewKeyListener();
		key.event = event;

		if (curMode != AllInput)
		{
			key.inputMode = curMode;
		}
		if (curMode == MsrInput || curMode == MsrKeyInput)
		{
			text.setData("MSRINPUT");
		}

		text.addKeyListener(key);

		final Label label_2 = new Label(shell, SWT.NONE);
		final FormData formData_2 = new FormData();
		formData_2.top = new FormAttachment(0, labeltop);
		formData_2.left = new FormAttachment(0, 15);
		formData_2.bottom = new FormAttachment(0, labelbottom);
		formData_2.right = new FormAttachment(0, 500);
		label_2.setLayoutData(formData_2);
		label_2.setText("提示");
		label_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_2.setAlignment(SWT.LEFT);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(help!=null?help:"");
		
		labelhelp = label_2;

		if (flag && (help == null || help.equals("")))
		{
			label_2.setText(Language.apply("输入数据必须为{0}和{1}之间的数值",
			                               new Object[]{ManipulatePrecision.doubleToString(min, 4, 1, true),ManipulatePrecision.doubleToString(max, 4, 1, true)}));
//			label_2.setText("输入数据必须为 " + ManipulatePrecision.doubleToString(min, 4, 1, true) + " 和 "
//			                + ManipulatePrecision.doubleToString(max, 4, 1, true) + " 之间的数值");
		}

		//Rectangle area = display.getPrimaryMonitor().getBounds();
		mouseModeInit(shell);
		
		shell.setLocation((GlobalVar.rec.x / 2) - (shell.getSize().x / 2), (GlobalVar.rec.y / 2) - (shell.getSize().y / 2));
		
		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
		
		shell.open();
		text.setFocus();
		shell.setActive();

		if (this.AutoInput > 0)
		{
			try
			{
				while (!(Display.getDefault().getFocusControl()).equals(text))
				{
				}
				System.out.println("Send Key");
				NewKeyListener.sendKey(this.AutoInput);
				while (display.readAndDispatch())
				{
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}
		}

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}

        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);

		//
		if (value != null)
		{
			if (txt_value.length() > 0)
			{
				txt_value.delete(0, txt_value.length());
			}

			txt_value.append(value);
		}

		return result;
	}
	
	private int shheight = 174;
	private int shwidth = 518;
	private Label labelhelp = null;
	private int labeltop = 95;
	private int labelbottom = 156;
	
	public void mouseModeInit(Shell shell)
	{
		if (!ConfigClass.MouseMode) return;
    	
		String labeltext = labelhelp.getText().replaceAll("\r\n","\n").replaceAll("\r", "\n");
		int helprow = labeltext.split("\n").length;
		int rowsize = (156 - 95)/3; 
		int muchsize = (3-helprow) * rowsize;
		FormData formData = (FormData)labelhelp.getLayoutData();
		formData.bottom = new FormAttachment(0, labelbottom-muchsize);
		shell.setSize(shwidth,shheight + 40 - muchsize);
		
		final Button button_1 = new Button(shell, SWT.NONE);
        final FormData fd_button_1 = new FormData();
        fd_button_1.left = new FormAttachment(0, 427);
        fd_button_1.right = new FormAttachment(0, 500);
        fd_button_1.bottom = new FormAttachment(0, 195 - muchsize);
        fd_button_1.top = new FormAttachment(0, 165 - muchsize);
        button_1.setLayoutData(fd_button_1);
        button_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1.setText(Language.apply("取消"));
        button_1.addSelectionListener(new SelectionAdapter() 
        {
        	public void widgetSelected(final SelectionEvent arg0)
        	{
        		text.setFocus();
        		
        		NewKeyListener.sendKey(GlobalVar.Exit);
        	}
        });
        
        // 当是不是刷卡模式或者是刷卡模式并且系统参数设置为刷卡允许手输入才加确认按钮
        if (curMode != MsrInput)// || (curMode == MsrInput && GlobalInfo.sysPara.msrspeed == 0))
        {        
	        final Button button_1_1 = new Button(shell, SWT.NONE);
			final FormData fd_button_1_1 = new FormData();
	        fd_button_1_1.left = new FormAttachment(0, 342);
	        fd_button_1_1.right = new FormAttachment(0, 415);
	        fd_button_1_1.bottom = new FormAttachment(0, 195 - muchsize);
	        fd_button_1_1.top = new FormAttachment(0, 165 - muchsize);
			button_1_1.setLayoutData(fd_button_1_1);
			button_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
			button_1_1.setText(Language.apply("确认"));
			button_1_1.addSelectionListener(new SelectionAdapter() 
	        {
	        	public void widgetSelected(final SelectionEvent arg0)
	        	{
	        		text.setFocus();
	        		
	        		NewKeyListener.sendKey(GlobalVar.Enter);
	        	}
	        });
        }
        else
        {
        	button_1.setText(Language.apply("放弃"));
        }
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
	}
}
