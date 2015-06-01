package com.efuture.defineKey;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.KeyPadSet;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class inputText
{
    private Text text;
    protected Shell shell;
    private String name = null;
    private String code = "";
    private KeyPadSet padset=null;
    private int keyindex = -1;
    
    public inputText(String arg, Shell pa,KeyPadSet set	,int index)
    {
        name   = arg;
        padset = set;
        keyindex = index;  
    }

    /**
     * Launch the application
     *
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            inputText window = new inputText("aaaa", null,null,-1);
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open()
    {
        final Display display = Display.getDefault();
        createContents();
        
        Rectangle area = display.getPrimaryMonitor().getBounds();
        
        shell.setBounds((area.width / 2) - (342 / 2),
                         (area.height / 2) - (153 / 2), 342, 153);
        shell.open();
        shell.layout();
        shell.setActive();
        text.setFocus();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(SWT.NONE|SWT.SYSTEM_MODAL);
        
        shell.setLayout(new FormLayout());

        shell.addShellListener(new ShellAdapter()
            {
                public void shellClosed(ShellEvent e)
                {
                	
                	
                }
            });
        

        //shell.setLocation(ManipulateStr.covnertPoint(parent.getLocation(),parent.getSize(),shell.getSize()));
        shell.setText("SWT Application");

        final Label label = new Label(shell, SWT.NONE);
        final FormData formData = new FormData();
        formData.right = new FormAttachment(100, -25);
        formData.bottom = new FormAttachment(0, 45);
        formData.top = new FormAttachment(0, 15);
        formData.left = new FormAttachment(0, 23);
        label.setLayoutData(formData);
        label.setAlignment(SWT.CENTER);
        label.setText(Language.apply("请为{0}确定按键",new Object[]{name}));
//        label.setText("请为" + name + "确定按键");

        text              = new Text(shell, SWT.BORDER);
        final FormData formData_1 = new FormData();
        formData_1.right = new FormAttachment(100, -25);
        formData_1.bottom = new FormAttachment(0, 90);
        formData_1.top = new FormAttachment(0, 60);
        formData_1.left = new FormAttachment(0, 23);
        text.setLayoutData(formData_1);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        text.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        text.addKeyListener(new KeyListener()
            {
                public void keyPressed(KeyEvent e)
                {
                    e.doit = false;

                    // 有,.?;'[]\-=这些有两个档的键,keycode为0,但有character,而且不触发keyrelease事件的情况
                    // 所以主动触发keyrelease事件
                    
                    if (e.keyCode == SWT.CTRL) return;
                    
                    if (e.keyCode == 0 && e.character > 0)
                    {
                    	e.keyCode = e.character;
                    	keyReleased(e);
                    }
                }

                public void keyReleased(KeyEvent e)
                {
                	if (e.keyCode == SWT.CTRL) return;
                	
                	if (e.stateMask == SWT.CTRL)
                		code = String.valueOf(e.keyCode)+"+"+String.valueOf(e.stateMask);
                	else
                		code = String.valueOf(e.keyCode);
                    
                    if(e.keyCode == SWT.SHIFT)
                    {
                    	code="";
                    	shell.close();
                    	shell.dispose();
                    	return;
                    }
                    
                    else if (!(name = padset.search(code,keyindex)).equals("null"))
                    {
                    	MessageDiagram me=new MessageDiagram(shell);
                    	me.open(Language.apply("此键值已设定为 '{0}' \n 确定是否强行修改",new Object[]{name}),true);
//                    	me.open("此键值已设定为 '"+name+"' \n 确定是否强行修改",true);
                       if(me.getDone()==1)
                       {
                    	   if(padset.deleteValue(name, code))
                    	   {
                    		   shell.close();
                               shell.dispose();
                    	   }
                    	   else
                    	   {
                    		   new MessageDiagram(shell).open(name+Language.apply("键强行删除失败\n请先手工删除 '{0}' 的键值",new Object[]{name}),false);
//                    		   new MessageDiagram(shell).open(name+"键强行删除失败\n请先手工删除 '"+name+"' 的键值",false);
                    		   text.setFocus();
                    	   }
                       }
                       else
                    	   text.setFocus();
                    	   
                    }
                    else
                    {
                    	shell.close();
                        shell.dispose();
                        //label_1.setText("设定成功");
                    }
                    
                }
            });

        final Label label_1 = new Label(shell, SWT.NONE);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(0, 135);
        formData_2.right = new FormAttachment(text, 0, SWT.RIGHT);
        formData_2.top = new FormAttachment(0, 105);
        formData_2.left = new FormAttachment(text, 0, SWT.LEFT);
        label_1.setLayoutData(formData_2);
        label_1.setText(Language.apply("SHIFT 键退出此窗口"));

        // label_1.setText("Label");
        //
    }

    public String getReturn()
    {
        return code;
    }
}
