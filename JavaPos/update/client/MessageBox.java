package update.client;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class MessageBox 
{
	private Display display = null;
	protected Shell shell = null;
	private Label label = null;
	private Label label_1 = null;
	private int timer = 0;
	
	public boolean Choice = true;
	
	public MessageBox(String msg)
	{
		display = Display.getDefault();
		createContents1();
		
		createMesBox(msg);
		
		label_1.setText(Language.apply("任意键-继续  ESC-退出"));
		
		shell.open();
		shell.layout();
		
		shell.setFocus();
		
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	public MessageBox(String msg ,boolean flag)
	{
		this.open(msg,flag);
	}
	
	private void createMesBox(String msg)
	{
		label.setText(msg);
		
	     // 采用异步方式执行
    	display.asyncExec(new Runnable()
        {
            public void run()
            {   
        		shell.addKeyListener(new KeyListener()
        		{
        			public void keyPressed(KeyEvent arg0) {
        				// TODO Auto-generated method stub
        			}
        			
        			public void keyReleased(KeyEvent arg0) {
        				// TODO Auto-generated method stub
        				arg0.doit = false;
        				
        				if (arg0.keyCode == SWT.ESC)
        				{
        					Choice = false;
        				}
        				
						shell.close();
						shell.dispose();
        				return;
        			}
        			
        		});
        		
        		/*
            	while(true)
    			{
    				try
    				{
    					timer = timer + 1;
    				
    					//刷新界面交互
    					while (Display.getCurrent().readAndDispatch());
        				
    					Thread.sleep(10000);
    					
    				}
    				catch(Exception ex)
    				{
    					ex.printStackTrace();
    				}
    				
    				label.setText(String.valueOf(10-timer));
    				
    				if (timer >= 10)
    				{
    					if (shell != null)
    					{
    						shell.close();
    						shell.dispose();
    					}
    					break;
    				}
    				
    				
    			}
    			*/
            }
        });
		 
	}
	
	private void createMesBox(String msg ,boolean flag)
	{
		msg = msg + Language.apply("五秒后关闭");
		if (flag)
		{
			label.setForeground(SWTResourceManager.getColor(0, 0, 0));
			label.setText(Language.apply("成功提示:"));
			label_1.setText(msg);
		}
		else
		{
			label.setForeground(SWTResourceManager.getColor(255, 0, 0));
			label.setText(Language.apply("错误提示:"));
			label_1.setText(msg);
		}
		
	       // 采用异步方式执行
    	display.asyncExec(new Runnable()
        {
            public void run()
            {            
            	while(true)
    			{
    				try
    				{
    					timer = timer + 1;
    				
    					//刷新界面交互
    					while (Display.getCurrent().readAndDispatch());
        				
    					Thread.sleep(500);
    					
    				}
    				catch(Exception ex)
    				{
    					ex.printStackTrace();
    				}
    				
    				if (timer >= 10)
    				{
    					if (shell != null)
    					{
    						shell.close();
    						shell.dispose();
    					}
    					break;
    				}
    				
    				
    			}
            }
        });
		 
	}
	
	public void open(String msg ,boolean flag) 
	{
		display = Display.getDefault();
		createContents();
		
		createMesBox(msg ,flag);
		
		shell.open();
		shell.layout();
		
		shell.setFocus();
		
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() 
	{
		shell = new Shell(SWT.NONE | SWT.APPLICATION_MODAL);
		shell.addKeyListener(new KeyAdapter() 
		{
			public void keyReleased(KeyEvent e)
			{
				if (shell != null)
				{
					timer = 50;
				}
			}
		});
		shell.setSize(509, 147);
	
		Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setBounds(area.width/2-509/2,area.height/2-147/2 ,509,147);
		
		label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		label.setText(Language.apply("没有信息:"));
		label.setBounds(13, 17, 111, 33);

		label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		label_1.setText(Language.apply("暂无信息"));
		label_1.setBounds(15, 55, 482, 80);
	}
	
	protected void createContents1() 
	{
		shell = new Shell(SWT.NONE | SWT.APPLICATION_MODAL);
		shell.addKeyListener(new KeyAdapter() 
		{
			public void keyReleased(KeyEvent e)
			{
				if (shell != null)
				{
					timer = 50;
				}
			}
		});
		shell.setSize(509, 147);
	
		Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setBounds(area.width/2-509/2,area.height/2-147/2 ,509,147);
		
		label = new Label(shell, SWT.CENTER);
		label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		label.setText("label");
		label.setBounds(13, 39, 483, 33);

		label_1 = new Label(shell, SWT.CENTER);
		label_1.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		label_1.setText("label1");
		label_1.setBounds(13, 85, 483, 33);
	}
	
	public void close()
	{
		if (shell != null)
		{
			shell.close();
			shell.dispose();
			
			shell = null;
		}
	}
	
}
