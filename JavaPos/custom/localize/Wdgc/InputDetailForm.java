package custom.localize.Wdgc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class InputDetailForm
{
	
	public static final int AllInput = -1;
	public static final int IntegerInput = 0;
	public static final int DoubleInput = 1;
	public static final int MsrInput = 2;
	public static final int MsrKeyInput = 3;
	public static final int MsrRetTracks =100;
	
	public static boolean isZhcn = false;
	private Text text;
	private Text text_3;
	private Text text_2;
	//private String value = null;
	private boolean result = true;
	private int curMode = AllInput;
	public String  oldTrace = null;
	public String  oldBatch = null;
	public String  oldDate = null;
	
	public String hit = null;
	public int AutoInput = -1;

	public static void main(String arg[]){
		InputDetailForm id = new InputDetailForm();
		id.open();
	}
	

	public boolean open()
	{
		//this.hit = hiit;
		//curMode = modeType;
		//this.AutoInput = AutoInput;
		final Display display = Display.getDefault();
		final Shell shell = new Shell(GlobalVar.style_linux);
		shell.setLayout(new FormLayout());
		shell.setSize(369, 197);
		
		final Label titleLabel = new Label(shell, SWT.NONE);
		final FormData formData = new FormData();
		formData.left = new FormAttachment(0, 15);
		formData.bottom = new FormAttachment(0, 40);
		formData.top = new FormAttachment(0, 15);
		titleLabel.setLayoutData(formData);
		titleLabel.setText("会员购物消费退货");
		titleLabel.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		

		text = new Text(shell, SWT.BORDER );
		//else text = new Text(shell, SWT.BORDER);
		formData.right = new FormAttachment(text, 0, SWT.RIGHT);
		final FormData formData_1 = new FormData();
		formData_1.right = new FormAttachment(0, 345);
		formData_1.left = new FormAttachment(0, 125);
		formData_1.bottom = new FormAttachment(0, 80);
		formData_1.top = new FormAttachment(0, 55);
		text.setLayoutData(formData_1);
		text.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		//if (limit != -1) text.setTextLimit(limit);
		text.setFocus();
		
		text_2 = new Text(shell, SWT.BORDER);
		final FormData formData_1_1 = new FormData();
		formData_1_1.right = new FormAttachment(text, 0, SWT.RIGHT);
		formData_1_1.top = new FormAttachment(0, 95);
		formData_1_1.bottom = new FormAttachment(0, 120);
		formData_1_1.left = new FormAttachment(text, 0, SWT.LEFT);
		text_2.setLayoutData(formData_1_1);
		text_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		text_3 = new Text(shell, SWT.BORDER);
		final FormData formData_1_1_1 = new FormData();
		formData_1_1_1.right = new FormAttachment(text_2, 0, SWT.RIGHT);
		formData_1_1_1.bottom = new FormAttachment(0, 165);
		formData_1_1_1.top = new FormAttachment(0, 140);
		formData_1_1_1.left = new FormAttachment(text_2, 0, SWT.LEFT);
		text_3.setLayoutData(formData_1_1_1);
		text_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label = new Label(shell, SWT.NONE);
		final FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(text, 0, SWT.BOTTOM);
		fd_label.right = new FormAttachment(text, -5, SWT.LEFT);
		fd_label.top = new FormAttachment(text, 0, SWT.TOP);
		fd_label.left = new FormAttachment(titleLabel, 0, SWT.LEFT);
		label.setLayoutData(fd_label);
		label.setText("原流水号");
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		Label label_1;
		label_1 = new Label(shell, SWT.NONE);
		final FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(text_2, 0, SWT.BOTTOM);
		fd_label_1.top = new FormAttachment(text_2, 0, SWT.TOP);
		fd_label_1.right = new FormAttachment(label, 85, SWT.LEFT);
		fd_label_1.left = new FormAttachment(label, 0, SWT.LEFT);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("原批次号");
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		
		final Label label_2 = new Label(shell, SWT.NONE);
		final FormData fd_label_2 = new FormData();
		fd_label_2.right = new FormAttachment(text_3, -5, SWT.LEFT);
		fd_label_2.bottom = new FormAttachment(text_3, 0, SWT.BOTTOM);
		fd_label_2.top = new FormAttachment(text_3, 0, SWT.TOP);
		fd_label_2.left = new FormAttachment(label_1, 0, SWT.LEFT);
		label_2.setLayoutData(fd_label_2);
		label_2.setText("原交易日期");
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		

		//text.selectAll();

		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
			}

			public void keyUp(KeyEvent e, int key)
			{
				switch (key)
				{
					case GlobalVar.Exit:
						result = false;
						shell.close();
						shell.dispose();

						break;
						
					case GlobalVar.Enter:
						if (e.widget.equals(text)){
							if (text.getText().length() <= 0) { return; }

							oldTrace = text.getText();
							text_2.selectAll();
							text_2.setFocus();
							e.data = "focus";
						}
						else if(e.widget.equals(text_2)){
							if (text_2.getText().length() <= 0) { return; }

							oldBatch = text_2.getText();
							text_3.selectAll();
							text_3.setFocus();
							e.data = "focus";
						}
						else if(e.widget.equals(text_3)){
							if (text_3.getText().length() <= 0) { return; }

							oldDate = text_3.getText();
							shell.close();
							shell.dispose();
						}
										
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
		text_2.addKeyListener(key);
		text_3.addKeyListener(key);
		//Rectangle area = display.getPrimaryMonitor().getBounds();
		//mouseModeInit(shell);
		
	//	shell.setLocation((GlobalVar.rec.x / 2) - (shell.getSize().x / 2), (GlobalVar.rec.y / 2) - (shell.getSize().y / 2));
		int width = shell.getMonitor().getClientArea().width;
		int height = shell.getMonitor().getClientArea().height;
		int x = shell.getSize().x;
		int y = shell.getSize().y;
		if (x > width)
		{
			shell.getSize().x = width;
		}
		if (y > height)
		{
			shell.getSize().y = height;
		}
		shell.setLocation((width - x) / 2, (height - y) / 2);
		
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
		

		return result;
	}


	public String getOldBatch()
	{
		return oldBatch;
	}


	public void setOldBatch(String oldBatch)
	{
		this.oldBatch = oldBatch;
	}


	public String getOldDate()
	{
		return oldDate;
	}


	public void setOldDate(String oldDate)
	{
		this.oldDate = oldDate;
	}


	public String getOldTrace()
	{
		return oldTrace;
	}


	public void setOldTrace(String oldTrace)
	{
		this.oldTrace = oldTrace;
	}

	
//	private int shheight = 174;
/*	
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
        button_1.setText("取消");
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
			button_1_1.setText("确认");
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
        	button_1.setText("放弃");
        }
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
	}
	*/
}
