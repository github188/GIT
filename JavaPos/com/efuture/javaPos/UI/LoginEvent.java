package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.UI.Design.LoginForm;


public class LoginEvent
{
    private Text txtStaffID = null;
    private Text txtPasswd = null;
    private Combo combo = null;
    private int inputName = 0;
    private int inputPassWord = 0;
    private Shell sShell = null;
    private LoginBS person = null;
    LoginForm lf = null;
    private int lastcombo = -1;
    
    public LoginEvent(LoginForm lf)
    {
        this.lf    = lf;
        txtStaffID = lf.txtStaffID;
        txtPasswd  = lf.txtPasswd;
        combo      = lf.combo;
        sShell     = lf.sShell;
        person     = CustomLocalize.getDefault().createLoginBS();
        
        // 初始化班次列表
        InitPosTimeCombo();

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

        NewKeyListener key = new NewKeyListener();
        key.event = event;

        txtStaffID.addKeyListener(key);
        txtPasswd.addKeyListener(key);
        combo.addKeyListener(key);

        //
        txtStaffID.setEditable(true);
        txtPasswd.setEditable(true);

        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        sShell.setBounds((GlobalVar.rec.x - sShell.getSize().x) / 2,
                         (GlobalVar.rec.y - sShell.getSize().y) / 2,
                         sShell.getSize().x,
                         sShell.getSize().y - GlobalVar.heightPL);
        
        //在处理新用户登录之前，增加用户注销的处理
        person.logoutDone();
    }

    private void InitPosTimeCombo()
    {
        ManipulateDateTime mdt = null;
        String time = null;
        String[] content = null;
        PosTimeDef postime = null;
        int assign = 0;

        try
        {
            if ((GlobalInfo.posTime != null) &&
                    (GlobalInfo.posTime.size() > 0))
            {
                mdt     = new ManipulateDateTime();
                time    = mdt.getTime();
                content = new String[GlobalInfo.posTime.size()];

                for (int i = 0; i < GlobalInfo.posTime.size(); i++)
                {
                    postime = (PosTimeDef) GlobalInfo.posTime.elementAt(i);

                    content[i] = postime.name;

                    if ((mdt.compareTime(time, postime.btime) >= 0) &&
                            (mdt.compareTime(time, postime.etime) <= 0))
                    {
                        assign = i;
                    }
                }

                combo.setItems(content);
                combo.select(assign);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
        finally
        {
            mdt     = null;
            time    = null;
            content = null;
            postime = null;
        }
    }

    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Exit:
            {
                // 登录界面正常状态不允许直接退出,调试模式才允许直接退出,系统将进入命令行
                if (ConfigClass.DebugMode || person.isExit())
                {
                    MessageBox me = new MessageBox(Language.apply("你确定要退出系统吗?"), null, true);

                    if (me.verify() == GlobalVar.Key1)
                    {
                        try
                        {
                            sShell.close();
                            sShell.dispose();
                        }
                        catch (Exception ex)
                        {
                        	ex.printStackTrace();
                        }
                    }
                }

                break;
            }
            case GlobalVar.Clear:
            	if (e.widget.equals(this.txtStaffID))
            	{
            		txtStaffID.setText("");
            	}
            	else if (e.widget.equals(this.txtPasswd))
            	{
            		txtPasswd.setText("");
            	}
            	break;
            case GlobalVar.ArrowUp:
            	if (e.widget.equals(combo) && !(combo.getSelectionIndex() == 0 && lastcombo == 0))
            	{
            		lastcombo = combo.getSelectionIndex();
            		break;
            	}
                e.data = "focus";
                txtStaffID.setFocus();
                txtStaffID.selectAll();
            	break;
            case GlobalVar.Enter:
            {
                if (e.widget.equals(txtStaffID))
                {
                    if ((txtStaffID.getText().length() > 0) && person.getLoginStaff(txtStaffID.getText()))
                    {
                        e.data = "focus";
                        txtPasswd.setFocus();
                        txtPasswd.selectAll();
                    }
                    else
                    {
                        txtStaffID.selectAll();
                    }
                }
                else if (e.widget.equals(txtPasswd))
                {
                	if (GlobalInfo.posLogin != null && !txtStaffID.getText().equals(GlobalInfo.posLogin.gh)) GlobalInfo.posLogin = null;
                	if (GlobalInfo.posLogin == null)
                	{
                		if (txtStaffID.getText().length() > 0 && person.getLoginStaff(txtStaffID.getText()))
                		{
                		}
                		else
                		{
                			e.data = "focus";
                			txtStaffID.setFocus();
                			txtStaffID.selectAll();
                			break;
                		}
                	}
                	
                    if (person.checkPasswd(txtPasswd.getText()))
                    {
                        e.data = "focus";                    	
                        combo.setFocus();
                    }
                    else
                    {
                        inputPassWord++;
                        txtPasswd.selectAll();
                    }

                    if (inputPassWord >= 3)
                    {
                        inputName++;
                        inputPassWord = 0;

                        e.data = "focus";
                        txtStaffID.setFocus();
                        txtStaffID.selectAll();
                    }
                }
                else if (e.widget.equals(combo))
                {
                	if (GlobalInfo.posLogin != null && !txtStaffID.getText().equals(GlobalInfo.posLogin.gh)) GlobalInfo.posLogin = null;
                	if (GlobalInfo.posLogin == null)
                	{
                		if (txtStaffID.getText().length() > 0 && person.getLoginStaff(txtStaffID.getText()))
                		{
                		}
                		else
                		{
                			e.data = "focus";
                			txtStaffID.setFocus();
                			txtStaffID.selectAll();
                			break;
                		}
                	}
                	
                	if (!person.checkPasswd(txtPasswd.getText()))
                	{
            			e.data = "focus";
            			txtPasswd.setFocus();
            			txtPasswd.selectAll();
                	}
                	else
                	{
	                    if (combo.getSelectionIndex() >= 0)
	                    {
	                        if (person.setPosTime(combo.getSelectionIndex()))
	                        {
	                            if (person.loginDone())
	                            {
	                                setDone();
	
	                                sShell.close();
	                                sShell.dispose();
	                            }
	                        }
	                    }
                	}
                }

                break;
            }
        }
    }

    public Shell getShell()
    {
        return sShell;
    }

    public void setDone() //完成登入界面
    {
        lf.loginDone = true;
    }
}
