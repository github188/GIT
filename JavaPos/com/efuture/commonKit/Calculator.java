package com.efuture.commonKit;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class Calculator
{
	
    private static String val = ""; //保存按钮值
    
    private static String exitVal = "";
    private static final int DEF_DIV_SCALE = 10;
    private Shell calculatorshell = null;
    private Button button1 = null;
    private Button button2 = null;
    private Button button3 = null;
    private Button button4 = null;
    private Button button5 = null;
    private Button button6 = null;
    private Button button7 = null;
    private Button button8 = null;
    private Button button9 = null;
    private Button button10 = null;
    private Button button11 = null;
    private Button button12 = null;
    private Button button13 = null;
    private Button button14 = null;
    private Button button15 = null;
    private Button button16 = null;
    private Button button17 = null;
    private Button button18 = null;
    private Button button19 = null;
    private Button button20 = null;
    private double save; //保存结果值  
    double value;
    boolean bool = false; //判断减号操作符的运算
    boolean boola = false; //判断除号操作符的运算
    char emblem = ' '; //保存新操作符
    char oldemblem = ' '; //保存旧的操作符   
    int point;
    private Label text = null;
    private Button button = null;
    int n1 = -1;
    private Matcher matcher = null;
    private Pattern pattern = null;
    public Vector v = new Vector();
    public Calculator()
    {
        this.open();
    }

    public void open()
    {
        final Display display = Display.getDefault();

        //读取配置键转换模板
        BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath +"//calc.ini");
        
        if (br != null)
    	{
    		String line = null;
	    	try
			{
				while ((line = br.readLine()) != null)
				{
					if (line.trim().length() <=0) continue;
					
					if (line.charAt(0) == ';') continue;
					
					v.add(line);
				}
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
        
        createContents();

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this,calculatorshell,null);
		
        calculatorshell.open();
        calculatorshell.layout();
        calculatorshell.setActive();

        while (!calculatorshell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
    }

    private void createContents()
    {
        calculatorshell = new Shell(GlobalVar.style);
        calculatorshell.setLayout(new FormLayout());

        calculatorshell.setText(Language.apply("迷你计算器......"));

        //Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
        calculatorshell.setSize(321, 295);

        calculatorshell.setBounds((GlobalVar.rec.x / 2) -
                                  (calculatorshell.getSize().x / 2),
                                  (GlobalVar.rec.y / 2) -
                                  (calculatorshell.getSize().y / 2),
                                  calculatorshell.getSize().x,
                                  calculatorshell.getSize().y -
                                  GlobalVar.heightPL);

        text = new Label(calculatorshell, SWT.RIGHT | SWT.BORDER);

        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 48);
        formData.top    = new FormAttachment(0, 19);
        formData.right  = new FormAttachment(0, 297);
        formData.left   = new FormAttachment(0, 10);
        text.setLayoutData(formData);
        text.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        if (exitVal.trim().length() <= 0)
        	text.setText("0");
        else
        	text.setText(exitVal);

        button1 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_1 = new FormData();
        formData_1.bottom = new FormAttachment(0, 201);
        formData_1.top    = new FormAttachment(0, 164);
        formData_1.right  = new FormAttachment(0, 58);
        formData_1.left   = new FormAttachment(0, 12);
        button1.setLayoutData(formData_1);
        button1.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button1.setText("1");
        button1.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    val = val + button1.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button2 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(button1, 0, SWT.BOTTOM);
        formData_2.top    = new FormAttachment(button1, 0, SWT.TOP);
        button2.setLayoutData(formData_2);
        button2.setText("2");
        button2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button2.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    val = val + button2.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button3 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_3 = new FormData();
        formData_3.top    = new FormAttachment(button2, 0, SWT.TOP);
        formData_3.bottom = new FormAttachment(button2, 0, SWT.BOTTOM);
        formData_3.left   = new FormAttachment(0, 131);
        button3.setLayoutData(formData_3);
        button3.setText("3");
        button3.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button3.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    System.out.println("widgetSelected()");
                    val = val + button3.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button4 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_4 = new FormData();
        formData_4.bottom = new FormAttachment(0, 149);
        formData_4.top    = new FormAttachment(0, 112);
        formData_4.right  = new FormAttachment(0, 58);
        formData_4.left   = new FormAttachment(0, 12);
        button4.setLayoutData(formData_4);
        button4.setText("4");
        button4.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button4.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    System.out.println("widgetSelected()");
                    val = val + button4.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button5           = new Button(calculatorshell, SWT.NONE);
        formData_2.right  = new FormAttachment(button5, 46, SWT.LEFT);
        formData_2.left   = new FormAttachment(button5, 0, SWT.LEFT);

        final FormData formData_5 = new FormData();
        formData_5.top    = new FormAttachment(button4, 0, SWT.TOP);
        formData_5.bottom = new FormAttachment(0, 148);
        formData_5.right  = new FormAttachment(0, 115);
        formData_5.left   = new FormAttachment(0, 69);
        button5.setLayoutData(formData_5);
        button5.setText("5");
        button5.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button5.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    System.out.println("widgetSelected()");
                    val = val + button5.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button6           = new Button(calculatorshell, SWT.NONE);
        formData_3.right  = new FormAttachment(button6, 0, SWT.RIGHT);

        final FormData formData_6 = new FormData();
        formData_6.bottom = new FormAttachment(button5, 0, SWT.BOTTOM);
        formData_6.top    = new FormAttachment(button5, 0, SWT.TOP);
        formData_6.right  = new FormAttachment(0, 178);
        formData_6.left   = new FormAttachment(0, 132);
        button6.setLayoutData(formData_6);
        button6.setText("6");
        button6.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button6.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    val = val + button6.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button7 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_7 = new FormData();
        formData_7.bottom = new FormAttachment(0, 99);
        formData_7.top    = new FormAttachment(0, 62);
        formData_7.right  = new FormAttachment(0, 58);
        formData_7.left   = new FormAttachment(0, 12);
        button7.setLayoutData(formData_7);
        button7.setText("7");
        button7.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button7.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    val = val + button7.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button8 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_8 = new FormData();
        formData_8.left   = new FormAttachment(button5, 0, SWT.LEFT);
        formData_8.bottom = new FormAttachment(0, 100);
        formData_8.top    = new FormAttachment(0, 63);
        formData_8.right  = new FormAttachment(0, 118);
        button8.setLayoutData(formData_8);
        button8.setText("8");
        button8.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button8.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    val = val + button8.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button9 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_9 = new FormData();
        formData_9.right  = new FormAttachment(button6, 0, SWT.RIGHT);
        formData_9.bottom = new FormAttachment(0, 100);
        formData_9.top    = new FormAttachment(0, 63);
        formData_9.left   = new FormAttachment(0, 133);
        button9.setLayoutData(formData_9);
        button9.setText("9");
        button9.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        button9.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    val = val + button9.getText();
                    text.setText(val);
                    button.setFocus();
                }
            });

        button10 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_10 = new FormData();
        formData_10.right = new FormAttachment(0, 238);
        formData_10.left  = new FormAttachment(0, 192);
        button10.setLayoutData(formData_10);
        button10.setText("+");
        button10.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        button10.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    if (emblem == ' ') //如果emblem等于空的话,将+赋于它
                    {
                        emblem = '+';
                    }

                    if ((oldemblem != '+') && (oldemblem != ' ')) //如果以前的oldmblem不等于加的话,并且不等于空的话
                    {
                        setEmblem(oldemblem);
                        emblem = oldemblem = '+';
                    }
                    else
                    {
                        setEmblem(emblem);
                    }

                    button.setFocus();
                }
            });

        button11 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_11 = new FormData();
        formData_11.bottom = new FormAttachment(button3, 0, SWT.BOTTOM);
        formData_11.top    = new FormAttachment(button3, 0, SWT.TOP);
        formData_11.right  = new FormAttachment(0, 238);
        formData_11.left   = new FormAttachment(0, 192);
        button11.setLayoutData(formData_11);
        button11.setText("-");
        button11.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        button11.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    if (emblem == ' ') //同上
                    {
                        emblem = '-';
                    }

                    if ((oldemblem != '-') && (oldemblem != ' '))
                    {
                        setEmblem(emblem);
                        emblem = oldemblem = '-';
                    }
                    else
                    {
                        setEmblem(emblem);
                    }

                    button.setFocus();
                }
            });
        button12 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_12 = new FormData();
        formData_12.top   = new FormAttachment(button6, 0, SWT.TOP);
        formData_12.right = new FormAttachment(0, 238);
        formData_12.left  = new FormAttachment(0, 192);
        button12.setLayoutData(formData_12);
        button12.setText("*");
        button12.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        button12.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    if (emblem == ' ') //同上
                    {
                        emblem = '*';
                    }

                    if ((oldemblem != '*') && (oldemblem != ' '))
                    {
                        setEmblem(oldemblem);
                        emblem = oldemblem = '*';
                    }
                    else
                    {
                        setEmblem(emblem);
                    }

                    button.setFocus();
                }
            });
        button13 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_13 = new FormData();
        formData_13.top   = new FormAttachment(0, 214);
        formData_13.right = new FormAttachment(0, 57);
        formData_13.left  = new FormAttachment(0, 11);
        button13.setLayoutData(formData_13);
        button13.setText("0");
        button13.setForeground(Display.getCurrent()
                                      .getSystemColor(SWT.COLOR_BLUE));
        button13.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    if (!text.getText().equals("0")) //如果文本框中没有单独的零,就可以输入
                    {
                        val = val + button13.getText();
                        text.setText(val);
                    }

                    button.setFocus();
                }
            });

        button14           = new Button(calculatorshell, SWT.NONE);
        formData_10.bottom = new FormAttachment(button14, 0, SWT.BOTTOM);
        formData_10.top    = new FormAttachment(button14, 0, SWT.TOP);

        final FormData formData_14 = new FormData();
        formData_14.left   = new FormAttachment(button3, 0, SWT.LEFT);
        formData_14.top    = new FormAttachment(0, 214);
        formData_14.right  = new FormAttachment(0, 180);
        button14.setLayoutData(formData_14);
        button14.setText(".");
        button14.setForeground(Display.getCurrent()
                                      .getSystemColor(SWT.COLOR_BLUE));
        button14.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    String str = text.getText();
                    
                    if (oldemblem == '+' || oldemblem == '-' || oldemblem == '*' || oldemblem == '/')
                    {
                        val = "0" + button14.getText();
                        text.setText(val);
                    }
                    else
                    {
                        int strpoint = str.indexOf(".");

                        if (strpoint < 0) //如果它小于零,进入 
                        {
                            val = text.getText() + button14.getText();
                            text.setText(val);
                        }
                    }
                    button.setFocus();
                }
            });

        button15 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_15 = new FormData();
        formData_15.bottom = new FormAttachment(button10, 0, SWT.BOTTOM);
        formData_15.top    = new FormAttachment(0, 214);
        formData_15.right  = new FormAttachment(0, 297);
        formData_15.left   = new FormAttachment(0, 251);
        button15.setLayoutData(formData_15);
        button15.setText("=");
        button15.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        button15.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    amount();
                }
            });

        button16 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_16 = new FormData();
        formData_16.bottom = new FormAttachment(0, 100);
        formData_16.top    = new FormAttachment(0, 63);
        formData_16.right  = new FormAttachment(0, 237);
        formData_16.left   = new FormAttachment(0, 191);
        button16.setLayoutData(formData_16);
        button16.setText("/");
        button16.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        button16.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    if (emblem == ' ') //同上
                    {
                        System.out.println("第一次");
                        emblem = '/';
                    }

                    if ((oldemblem != '/') && (oldemblem != ' '))
                    {
                        System.out.println("第二次");
                        setEmblem(emblem);
                        emblem = oldemblem = '/';
                    }
                    else
                    {
                        System.out.println("第三次");
                        setEmblem(emblem);
                    }

                    button.setFocus();
                }
            });

        text.setFocus();
        button17 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_17 = new FormData();
        formData_17.bottom = new FormAttachment(0, 100);
        formData_17.top    = new FormAttachment(0, 63);
        formData_17.right  = new FormAttachment(0, 296);
        formData_17.left   = new FormAttachment(0, 250);
        button17.setLayoutData(formData_17);
        button17.setText("C");
        button17.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        button17.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    text.setText("0");
                    val       = "";
                    save      = 0;
                    value     = 0;
                    emblem    = ' ';
                    oldemblem = ' ';
                    bool  = false;
                    boola = false;
                    button.setFocus();
                }
            });
        button18           = new Button(calculatorshell, SWT.NONE);
        formData_14.bottom = new FormAttachment(button18, 0, SWT.BOTTOM);
        formData_13.bottom = new FormAttachment(button18, 0, SWT.BOTTOM);

        final FormData formData_18 = new FormData();
        formData_18.left   = new FormAttachment(button2, 0, SWT.LEFT);
        formData_18.bottom = new FormAttachment(0, 253);
        formData_18.top    = new FormAttachment(0, 214);
        formData_18.right  = new FormAttachment(0, 115);
        button18.setLayoutData(formData_18);
        button18.setForeground(Display.getCurrent()
                                      .getSystemColor(SWT.COLOR_BLUE));
        button18.setText("+/-");
        button18.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    button.setFocus();
                }
            });

        button19           = new Button(calculatorshell, SWT.NONE);
        formData_12.bottom = new FormAttachment(button19, 0, SWT.BOTTOM);

        final FormData formData_19 = new FormData();
        formData_19.top    = new FormAttachment(button4, 0, SWT.TOP);
        formData_19.bottom = new FormAttachment(button6, 0, SWT.BOTTOM);
        formData_19.right  = new FormAttachment(0, 298);
        formData_19.left   = new FormAttachment(0, 252);
        button19.setLayoutData(formData_19);
        button19.setText("%");
        button19.setForeground(Display.getCurrent()
                                      .getSystemColor(SWT.COLOR_BLUE));
        button19.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    button.setFocus();
                }
            });
        button20 = new Button(calculatorshell, SWT.NONE);

        final FormData formData_20 = new FormData();
        formData_20.bottom = new FormAttachment(button3, 0, SWT.BOTTOM);
        formData_20.top    = new FormAttachment(button11, 0, SWT.TOP);
        formData_20.right  = new FormAttachment(0, 298);
        formData_20.left   = new FormAttachment(0, 252);
        button20.setLayoutData(formData_20);
        button20.setText("1/X");
        button20.setForeground(Display.getCurrent()
                                      .getSystemColor(SWT.COLOR_BLUE));
        button20.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    button.setFocus();
                }
            });

        button = new Button(calculatorshell, SWT.NONE);

        final FormData formData_21 = new FormData();
        formData_21.top    = new FormAttachment(button11, 0, SWT.TOP);
        formData_21.bottom = new FormAttachment(button20, 28, SWT.TOP);
        formData_21.right  = new FormAttachment(button20, 26, SWT.LEFT);
        formData_21.left   = new FormAttachment(button20, 0, SWT.LEFT);
        button.setLayoutData(formData_21);
        button.setVisible(true);
        button.setFocus();

        //		设定键盘事件
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

        NewKeyListener key = new NewKeyListener();
        key.event = event;

        button.addKeyListener(key);
        button1.addKeyListener(key);
        button2.addKeyListener(key);
        button3.addKeyListener(key);
        button4.addKeyListener(key);
        button5.addKeyListener(key);
        button6.addKeyListener(key);
        button7.addKeyListener(key);
        button8.addKeyListener(key);
        button9.addKeyListener(key);
        button10.addKeyListener(key);
        button11.addKeyListener(key);
        button12.addKeyListener(key);
        button13.addKeyListener(key);
        button14.addKeyListener(key);
        button15.addKeyListener(key);
        button16.addKeyListener(key);
        button17.addKeyListener(key);
        button18.addKeyListener(key);
        button19.addKeyListener(key);
        button20.addKeyListener(key);

        button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
                {
                    //在linux 下button 的enter 可以响应 release健  但window下不行，所以用选中代替
                    if (!System.getProperties().getProperty("os.name")
                                   .substring(0, 5).equals("Linux"))
                    {
                        int key = GlobalVar.Enter;//NewKeyListener.searchKey(13);

                        switch (key)
                        {
                            case GlobalVar.Enter:
                                amount();

                                break;
                        }
                    }
                }
            });

        button.setFocus();
    }

    public void keyPressed(KeyEvent e, int key)
    {
    	if (v.size() > 0)
    	{
    		String line = null;

				for (int i = 0; i < v.size();i++)
				{
					line = (String) v.elementAt(i);
					String lines[] = line.split("=");
					String templine = ","+lines[1].trim()+",";
					if (templine.indexOf(","+String.valueOf(key)+",") != -1)
					{
						key = Convert.toInt(lines[0]);
						break;
					}
				}
    	}
    	keyNum(key);
    }

    public void keyReleased(KeyEvent e, int key)
    {
    	//通过配置文件配置按键
    	
    	if (v.size() > 0)
    	{
    		String line = null;

				for (int i = 0; i < v.size();i++)
				{
					line = (String) v.elementAt(i);
					String lines[] = line.split("=");
					String templine = ","+lines[1].trim()+",";
					if (templine.indexOf(","+String.valueOf(key)+",") != -1)
					{
						key = Convert.toInt(lines[0]);
						break;
					}
				}
    	}
        keymethod(key);
    }

    public void setEmblem(char em)
    {
        switch (em)
        {
            case '+':

                try
                {
                    if (!val.equals("")) //val不等于空进入
                    {
                        save  = add(save, Double.parseDouble(val)); //两数相加
                        val   = "";
                        point = String.valueOf(save).indexOf(".");

                        //如果点后面只有0的话就进入
                        if (String.valueOf(save).substring(point + 1).equals("0"))
                        {
                            text.setText(String.valueOf(save).substring(0, point));
                        }
                        else
                        {
                            text.setText(String.valueOf(save));
                        }
                    }
                    else
                    {
                        save = Double.parseDouble(text.getText());
                    }

                    oldemblem = emblem;
                    boola     = true;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;

            case '-':

                try
                {
                    //bool等于false;代表第一次开始减
                    if (bool == false)
                    {
                        bool = true;

                        if (!val.equals(""))
                        {
                            if (save != 0.0)
                            {
                                save = sub(save, Double.parseDouble(val));
                            }
                            else
                            {
                                save = sub(Double.parseDouble(val), save);
                            }
                        }
                        else
                        {
                            save = Double.parseDouble(text.getText());
                        }
                    }
                    else
                    {
                        save = sub(save, Double.parseDouble(val));
                    }

                    val       = "";
                    point     = String.valueOf(save).indexOf(".");

                    if (String.valueOf(save).substring(point + 1).equals("0"))
                    {
                        text.setText(String.valueOf(save).substring(0, point));
                    }
                    else
                    {
                        text.setText(String.valueOf(save));
                    }

                    boola     = true;
                    oldemblem = emblem;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;

            case '*':

                if (!val.equals(""))
                {
                    if (save == 0.0)
                    {
                        save = 1;
                    }

                    save  = mul(save, Double.parseDouble(val));
                    val   = "";
                    point = String.valueOf(save).indexOf(".");

                    if (String.valueOf(save).substring(point + 1).equals("0"))
                    {
                        text.setText(String.valueOf(save).substring(0, point));
                    }
                    else
                    {
                        text.setText(String.valueOf(save));
                    }
                }
                else
                {
                    save = Double.parseDouble(text.getText());
                }

                boola = true;
                oldemblem = emblem;

                break;

            case '/':

                if (!val.equals("0"))
                {
                    if (!val.equals(""))
                    {
                        if (save == 0.0)
                        {
                            save = 1;
                        }

                        if (boola == false)
                        {
                            boola = true;
                            save  = div(Double.parseDouble(val), save);
                        }
                        else
                        {
                            save = div(save, Double.parseDouble(val));
                        }

                        point = String.valueOf(save).indexOf(".");

                        if (String.valueOf(save).substring(point + 1).equals("0"))
                        {
                            text.setText(String.valueOf(save).substring(0, point));
                        }
                        else
                        {
                            text.setText(String.valueOf(save));
                        }
                    }
                    else
                    {
                        save = Double.parseDouble(text.getText());
                    }
                }
                else
                {
                    save      = 0;
                    value     = 0;
                    emblem    = ' ';
                    oldemblem = ' ';
                    bool      = false;
                    boola     = false;
                }

                val = "";
                oldemblem = emblem;

                break;
        }
    }

    public static double add(double v1, double v2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.add(b2).doubleValue();
    }

    public static double sub(double v1, double v2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.subtract(b2).doubleValue();
    }

    public static double mul(double v1, double v2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.multiply(b2).doubleValue();
    }

    public static double div(double v1, double v2)
    {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    public static double div(double v1, double v2, int scale)
    {
        if (scale < 0)
        {
            throw new IllegalArgumentException(Language.apply("不允许小于0"));
        }

        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public void amount()
    {
        switch (emblem)
        {
            case '+':

                try
                {
                    value = add(save, Double.parseDouble(text.getText()));
                    point = String.valueOf(value).indexOf(".");

                    if (String.valueOf(value).substring(point + 1).equals("0"))
                    {
                        text.setText(String.valueOf(value).substring(0, point));
                    }
                    else
                    {
                        text.setText(String.valueOf(value));
                    }

                    boola = false;
                    val   = "";
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;

            case '-':
                value = sub(save, Double.parseDouble(text.getText()));
                point = String.valueOf(value).indexOf(".");

                if (String.valueOf(value).substring(point + 1).equals("0"))
                {
                    text.setText(String.valueOf(value).substring(0, point));
                }
                else
                {
                    text.setText(String.valueOf(value));
                }

                boola = false;
                bool = false;
                val = "";

                break;

            case '*':
                value = mul(save, Double.parseDouble(text.getText()));
                point = String.valueOf(value).indexOf(".");

                if (String.valueOf(value).substring(point + 1).equals("0"))
                {
                    text.setText(String.valueOf(value).substring(0, point));
                }
                else
                {
                    text.setText(String.valueOf(value));
                }

                boola = false;
                val = "";

                break;

            case '/':
                pattern = Pattern.compile("[1-9]");
                matcher = pattern.matcher(text.getText());

                if (matcher.find())
                {
                    value = div(save, Double.parseDouble(text.getText()));
                    point = String.valueOf(value).indexOf(".");

                    if (String.valueOf(value).substring(point + 1).equals("0"))
                    {
                        text.setText(String.valueOf(value).substring(0, point));
                    }
                    else
                    {
                        text.setText(String.valueOf(value));
                    }

                    boola = false;
                    val   = "";
                }
                else
                {
                    text.setText(Language.apply("对不起除数能为零..."));
                    val       = "";
                    save      = 0;
                    value     = 0;
                    emblem    = ' ';
                    oldemblem = ' ';
                    bool      = false;
                    boola     = false;
                }

                break;
        }

        emblem    = ' ';
        oldemblem = ' ';
        button.setFocus();
    }

    public void keymethod(int code)
    {
        switch (code)
        {
            /*case GlobalVar.Key0: //代表0

                if (isTextLength(text.getText()))
                {
                    if (!text.getText().equals("0"))
                    {
                        val = val + button13.getText();
                        text.setText(val);
                    }
                }

                button.setFocus();

                break;

            case GlobalVar.Key1: //代表1

                if (isTextLength(text.getText()))
                {
                    val = val + button1.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key2: //代表2

                if (isTextLength(text.getText()))
                {
                    val = val + button2.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key3: //代表3

                if (isTextLength(text.getText()))
                {
                    val = val + button3.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key4: //代表4

                if (isTextLength(text.getText()))
                {
                    val = val + button4.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key5: //代表5

                if (isTextLength(text.getText()))
                {
                    val = val + button5.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key6: //代表6

                if (isTextLength(text.getText()))
                {
                    val = val + button6.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key7: //代表7

                if (isTextLength(text.getText()))
                {
                    val = val + button7.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key8: //代表8

                if (isTextLength(text.getText()))
                {
                    val = val + button8.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key9: //代表9

                if (isTextLength(text.getText()))
                {
                    val = val + button9.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Decimal: //代表点

                String str = text.getText();
                int strpoint = str.indexOf(".");

                if (strpoint < 0)
                {
                    val = text.getText() + button14.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;*/

            case GlobalVar.BkSp:
                if (isTextLength(text.getText()) && val.length() > 0)
                {
                    val = val.substring(0,val.length()-1);
                    text.setText(val);
                }

                button.setFocus();
                break;
                
            case GlobalVar.Plus: //代表+

                if (emblem == ' ')
                {
                    emblem = '+';
                }

                if ((oldemblem != '+') && (oldemblem != ' '))
                {
                    setEmblem(oldemblem);
                    emblem = oldemblem = '+';
                }
                else
                {
                    setEmblem(emblem);
                }

                button.setFocus();

                break;

            case GlobalVar.Back:
            case GlobalVar.Minu: //代表-

                if (emblem == ' ')
                {
                    emblem = '-';
                }

                if ((oldemblem != '-') && (oldemblem != ' '))
                {
                    setEmblem(emblem);
                    emblem = oldemblem = '-';
                }
                else
                {
                    setEmblem(emblem);
                }

                button.setFocus();

                break;

            case GlobalVar.Mul:
            case GlobalVar.Quantity: //代表*

                if (emblem == ' ')
                {
                    emblem = '*';
                }

                if ((oldemblem != '*') && (oldemblem != ' '))
                {
                    setEmblem(oldemblem);
                    emblem = oldemblem = '*';
                }
                else
                {
                    setEmblem(emblem);
                }

                button.setFocus();

                break;

            case GlobalVar.Div: //代表/

                if (emblem == ' ')
                {
                    emblem = '/';
                }

                if ((oldemblem != '/') && (oldemblem != ' '))
                {
                    setEmblem(emblem);
                    emblem = oldemblem = '/';
                }
                else
                {
                    setEmblem(emblem);
                }

                button.setFocus();

                break;

            case GlobalVar.Validation:
            case GlobalVar.Pay:
            case GlobalVar.Enter: //代表回车
                amount();

                break;

            case GlobalVar.Exit: //代表ESC
                val = "";
                exitVal = text.getText();
                calculatorshell.close();
                calculatorshell.dispose();

                break;

            case GlobalVar.Clear: //代表C
                text.setText("0");
                val = "";
                save = 0;
                value = 0;
                emblem = ' ';
                oldemblem = ' ';
                bool = false;
                boola = false;
                button.setFocus();

                break;
        }
    }

    public void keyNum(int code)
    {
        switch (code)
        {
            case GlobalVar.Key0: //代表0

                if (isTextLength(text.getText()))
                {
                    if (!text.getText().equals("0"))
                    {
                        val = val + button13.getText();
                        text.setText(val);
                    }
                }

                button.setFocus();

                break;

            case GlobalVar.Key1: //代表1

                if (isTextLength(text.getText()))
                {
                    val = val + button1.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key2: //代表2

                if (isTextLength(text.getText()))
                {
                    val = val + button2.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key3: //代表3

                if (isTextLength(text.getText()))
                {
                    val = val + button3.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key4: //代表4

                if (isTextLength(text.getText()))
                {
                    val = val + button4.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key5: //代表5

                if (isTextLength(text.getText()))
                {
                    val = val + button5.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key6: //代表6

                if (isTextLength(text.getText()))
                {
                    val = val + button6.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key7: //代表7

                if (isTextLength(text.getText()))
                {
                    val = val + button7.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key8: //代表8

                if (isTextLength(text.getText()))
                {
                    val = val + button8.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Key9: //代表9

                if (isTextLength(text.getText()))
                {
                    val = val + button9.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;

            case GlobalVar.Decimal: //代表点

            	String str = text.getText();
                int strpoint = str.indexOf(".");

                if (strpoint < 0)
                {
                    val = text.getText() + button14.getText();
                    text.setText(val);
                }

                button.setFocus();

                break;
                
                /*String str = text.getText();
                
                if (oldemblem == '+' || oldemblem == '-' || oldemblem == '*' || oldemblem == '/')
                {
                    val = "0" + button14.getText();
                    text.setText(val);
                }
                else
                {
                    int strpoint = str.indexOf(".");

                    if (strpoint < 0)
                    {
                        val = text.getText() + button14.getText();
                        text.setText(val);
                    }
                }
                button.setFocus();

                break;
           */
        }
    }

    
    public boolean isTextLength(String valuestr)
    {
        if (valuestr.length() <= 32)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
