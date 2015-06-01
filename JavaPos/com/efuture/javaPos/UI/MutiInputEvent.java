package com.efuture.javaPos.UI;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.UI.Design.MutiInputForm;


public class MutiInputEvent
{
	MutiInputForm form = null;

    public MutiInputEvent(MutiInputForm form)
    {
        this.form = form;

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

		if (form.txt1_mode != form.txt2_mode)
		{
	        NewKeyListener key = new NewKeyListener();
	        key.event     = event;
	        key.inputMode = form.txt1_mode;
	        form.text_1.addKeyListener(key);

	        NewKeyListener key2 = new NewKeyListener();
	        key2.event     = event;
	        key2.inputMode = form.txt2_mode;
	        form.text_2.addKeyListener(key2);
		}
		else
		{
	        NewKeyListener key = new NewKeyListener();
	        key.event     = event;
	        key.inputMode = form.txt1_mode;

	        form.text_1.addKeyListener(key);
	        form.text_2.addKeyListener(key);
		}

        //Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
        form.shell.setBounds(((GlobalVar.rec.x - form.shell.getSize().x) / 2) + 1,
                             (GlobalVar.rec.y - form.shell.getSize().y) / 2,
                             form.shell.getSize().x,
                             form.shell.getSize().y - GlobalVar.heightPL);
    }

    public void keyPressed(KeyEvent e, int key)
    {
    }

    public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Enter:
    			if (e.widget.equals(form.text_1))
				{
    				if (form.text_1.getText().length() > 0)
    	            {
                        form.text_2.setFocus();
                        form.text_2.selectAll();
    	                e.data = "focus";
    	            }
				}
    			else if (e.widget.equals(form.text_2))
    			{
    				if (form.text_2.getText().length() > 0)
    	            {
                        form.txt_1 = form.text_1.getText();
                        form.txt_2 = form.text_2.getText();
                        form.result = true;
                        form.shell.close();
                        form.shell.dispose();
    	            }
    			}
                break;

            case GlobalVar.Exit:
            	form.result = false;
                form.shell.close();
                form.shell.dispose();
                break;
            case GlobalVar.Validation:
                form.shell.close();
                form.shell.dispose();
                break;
        }
    }
}
