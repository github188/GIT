package com.efuture.javaPos.UI;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.PersonnelGoBS;
import com.efuture.javaPos.UI.Design.PersonnelGoForm;


public class PersonnelGoEvent
{
    private StyledText txtgotime = null;
    private Text txtpass = null;
    private StyledText txtname = null;
    private StyledText txtgh = null;
    private Label lblcalculagraph = null;
    protected Shell shell = null;
    protected MenuFuncEvent mffe = null;
    PersonnelGoBS pgb = null;
    int hour = 0;
    int minute = 0;
    int second = 0;
    String tempsecond = null;
    String tempminute = null;
    String temphour = null;
    String timestr = null;

    public PersonnelGoEvent(PersonnelGoForm pgf)
    {
        this.txtgotime       = pgf.getTxtgotime();
        this.txtpass         = pgf.getTxtpass();
        this.txtname         = pgf.getTxtname();
        this.txtgh           = pgf.getTxtgh();
        this.shell           = pgf.getShell();
        this.lblcalculagraph = pgf.getLblcalculagraph();
        
        //设定键盘事件
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

        this.txtpass.addKeyListener(key);
        
        pgb = CustomLocalize.getDefault().createPersonnelGoBS();
        
        init();
    }

    public void init()
    {
        try
        {
            txtgh.setText(GlobalInfo.posLogin.gh);
            txtname.setText(GlobalInfo.posLogin.name);
            txtgotime.setText(new ManipulateDateTime().getTime());

            // 开始离开
            pgb.setPersonGo();

            // 设置离开计时
            final int time = 1000;
            final Display display = Display.getCurrent();

            final Runnable timer = new Runnable()
            {
                public void run()
                {
                    if (lblcalculagraph.isDisposed())
                    {
                        return;
                    }

                    if (second == 60)
                    {
                        minute = minute + 1;
                        second = 0;
                    }
                    else if (minute == 60)
                    {
                        hour   = hour + 1;
                        minute = 0;
                        second = 0;
                    }
                    else if (hour == 24)
                    {
                        hour   = 0;
                        minute = 0;
                        second = 0;
                    }
                    else
                    {
                        second = second + 1;
                    }

                    if (second < 10)
                    {
                        tempsecond = "0" + second;
                    }
                    else
                    {
                        tempsecond = String.valueOf(second);
                    }

                    if (minute < 10)
                    {
                        tempminute = "0" + minute;
                    }
                    else
                    {
                        tempminute = String.valueOf(minute);
                    }

                    if (hour < 10)
                    {
                        temphour = "0" + hour;
                    }
                    else
                    {
                        temphour = String.valueOf(hour);
                    }

                    timestr = temphour + ":" + tempminute + ":" + tempsecond;
                    lblcalculagraph.setText(timestr);
                    display.timerExec(time, this);
                }
            };

            display.timerExec(time, timer);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
        if (key == GlobalVar.Enter)
        {
            if (pgb.checkComeBack(txtpass.getText(), timestr))
            {   
                shell.close();
                shell.dispose();
            }
            else
            {
                txtpass.selectAll();
            }
        }
        else if (key == GlobalVar.Exit || key == GlobalVar.Clear)
        {
        	pgb.disentangleScreen();
        }
    }
}
