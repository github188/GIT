package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.AssemblyInfo;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.swtdesigner.SWTResourceManager;


/**
 * 背景的界面，增加图片或文字
 * @author root
 *
 */
public class BackgroundForm
{
    Display display = null;
    Label txt_internet = null;
    Shell sShell = null;
    Image originalImage = null;
    Label lbl_msg = null;
    StatusBarForm sbf=null;
    Label lbl_version = null;
    Label lbl_modtype = null;
    Label lbl_ipaddr = null;
    Label lbl_localDB = null;
        
    
    /**
     * This method initializes sShell
     */       
    public BackgroundForm(Shell shell)
    {
        display = Display.getDefault();
        sShell  = shell;

    	// 开启虚拟屏幕键盘
        boolean openscreenkeyboard = false;
    	if (Math.abs(ConfigClass.ScreenKeyboard) > 0 && PathFile.fileExist("OnScreenKeyboard.exe"))
    	{ 
    		try
			{
    			// 加载屏幕键盘程序OnScreenKeyboard.exe
    			if (GlobalVar.EnableScreenKeyboard(true) == 0) CommonMethod.waitForExec("OnScreenKeyboard.exe",false);
    			
    			// 改变窗口大小留出屏幕位置
				openscreenkeyboard = true;
				GlobalVar.rec.x -= Math.abs(ConfigClass.ScreenKeyboard);
				if (ConfigClass.ScreenKeyboard > 0) sShell.setBounds(0, 0, GlobalVar.rec.x, GlobalVar.rec.y);
				else sShell.setBounds(Math.abs(ConfigClass.ScreenKeyboard), 0, GlobalVar.rec.x, GlobalVar.rec.y);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				ConfigClass.ScreenKeyboard = 0;
			}
    	}
    	if (!openscreenkeyboard) sShell.setBounds(0, 0, GlobalVar.rec.x, GlobalVar.rec.y);
        sShell.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                
            }

            public void keyReleased(KeyEvent e)
            {
               
            }
        });

        // 加载背景图片
        ImageData data = new ImageData(ConfigClass.BackImagePath + "bkground.png");
        data    = data.scaledTo(sShell.getClientArea().width,sShell.getClientArea().height);
        originalImage = new Image(display, data);

        // 设置背景透明
        sShell.setBackgroundMode(SWT.INHERIT_DEFAULT);        
        sShell.setBackgroundImage(originalImage);
/*        
        final Point origin = new Point(0, 0);
        sShell.addListener(SWT.Paint, new Listener()
        {
            public void handleEvent(Event e)
            {
                GC gc = e.gc;
                gc.drawImage(originalImage, origin.x, origin.y);
            }
        });
*/
        lbl_msg = new Label(sShell, SWT.NONE);
        lbl_msg.setBounds(20, sShell.getClientArea().height - 100, 400, 30);
        lbl_msg.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        //lbl_msg.setBackground(SWTResourceManager.getColor(255, 255, 255));
        
        //
        lbl_version = new Label(sShell, SWT.NONE);
        lbl_version.setBounds(sShell.getClientArea().width - 310, sShell.getClientArea().height - 135, 310, 20);
        lbl_version.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
        //lbl_version.setBackground(SWTResourceManager.getColor(255, 255, 255));
        
        //
        lbl_modtype = new Label(sShell, SWT.NONE);
        lbl_modtype.setBounds(sShell.getClientArea().width - 310, sShell.getClientArea().height - 110, 310, 20);
        lbl_modtype.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
        //lbl_modtype.setBackground(SWTResourceManager.getColor(255, 255, 255));

        //
        lbl_ipaddr = new Label(sShell, SWT.NONE);
        lbl_ipaddr.setBounds(sShell.getClientArea().width - 310, sShell.getClientArea().height - 85, 310, 20);
        lbl_ipaddr.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
        //lbl_ipaddr.setBackground(SWTResourceManager.getColor(255, 255, 255));        
        
        //
        lbl_localDB = new Label(sShell, SWT.NONE);
        lbl_localDB.setBounds(sShell.getClientArea().width - 310, sShell.getClientArea().height - 60, 310, 20);
        lbl_localDB.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
        //lbl_localDB.setBackground(SWTResourceManager.getColor(255, 255, 255));        
    }

    public void open()
    {
        sShell.open();
        sShell.layout();
        
        // 显示版本号
        lbl_version.setText(Language.apply("程序版本: ") + AssemblyInfo.AssemblyVersion);
    }

    public void setVersionEanble(boolean b)
    {
    	if (b)
    	{
    		// 设置背景透明
            sShell.setBackgroundMode(SWT.INHERIT_DEFAULT);        
            sShell.setBackgroundImage(originalImage);
    	}
    	else
    	{
	        // 取消背景透明
    		sShell.setBackgroundMode(SWT.NO_BACKGROUND);
    		sShell.setBackgroundImage(null);
    	}

    	lbl_version.setVisible(b);
    	lbl_modtype.setVisible(b);
    	lbl_ipaddr.setVisible(b);
    	lbl_localDB.setVisible(b);
    }
    
    public void setLocalDBDate(String s)
    {
        lbl_localDB.setText(Language.apply("脱网数据: ") + s);
    }
    
    public void setModuleType(String s)
    {
    	lbl_modtype.setText(Language.apply("客户模块: ") + s);
    }
    
    public void setIPText(String s)
    {
    	lbl_ipaddr.setText(Language.apply("本机地址: ") + s);
    }    
    
    public boolean startLoadInfo()
    {
        // 下载开机信息
    	lbl_msg.setVisible(true);
        boolean done = LoadSysInfo.getDefault().startLoadInfo(lbl_msg);
        lbl_msg.setVisible(false);
         
        return done;
    }

    public void quitSysInfo()
    {
    	// 关机信息
    	setVersionEanble(true);
    	
    	lbl_msg.setVisible(true);
        LoadSysInfo.getDefault().quitLoadInfo(lbl_msg);
        lbl_msg.setVisible(false);
    }
    
    public void setStatusBarForm(StatusBarForm form)
    {
        form.setBounds(0, sShell.getSize().y - 60+GlobalVar.heightPL, sShell.getSize().x, 30);
    }

    public Shell getLastShell()
    {
        return sShell;
    }
    
    public void closeSaleForm()
    {
    	this.sbf.dispose();
    }
    
    public void waitClose()
    {
        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    public void closeShell()
    {
        originalImage.dispose();
        sShell.close();
        sShell.dispose();
    }
}
