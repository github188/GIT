package device.KeyBoard;

import java.util.Vector;

import jpos.JposException;
import jpos.POSKeyboard;
import jpos.events.DataEvent;
import jpos.events.DataListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_KeyBoard;
import com.efuture.javaPos.Global.Language;


public class IBMJPOS_KeyBoard implements Interface_KeyBoard
{
    POSKeyboard keyBoard = null;
    KeyRobotEvent event = null;
    Thread tread = null;
    int i = 1;
    String cusname = "Default";
    
    public boolean open()
    {
        if (DeviceName.deviceKeyBoard.length() <= 0)
        {
            return false;
        }

        keyBoard = new POSKeyboard();

        event = new KeyRobotEvent();

        try
        {
            keyBoard.open(DeviceName.deviceKeyBoard.split(",")[0].split(";")[0].trim());
            
            if (DeviceName.deviceKeyBoard.split(",")[0].split(";").length > 1)
            {
            	cusname = DeviceName.deviceKeyBoard.split(",")[0].split(";")[1];
            }
            
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            new MessageBox(Language.apply("打开JPOS键盘异常:") + "\n" + e.getMessage());
        }

        return false;
    }

    public void close()
    {
        try
        {
        	//if (tread != null) tread.stop();
    		
        	if (keyBoard != null) keyBoard.close();
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
        }
    }

    public void setEnable(boolean enable)
    {
        try
        {
            if (enable)
            {
                if (!keyBoard.getClaimed())
                {
                    keyBoard.claim(1000);
                    keyBoard.setDeviceEnabled(true);
                    keyBoard.setDataEventEnabled(true);
                    keyBoard.addDataListener(event);
                }
            }
            else
            {
                if (keyBoard.getClaimed())
                {
                    keyBoard.removeDataListener(event);
                    keyBoard.setDeviceEnabled(false);
                    keyBoard.setDataEventEnabled(false);
                    keyBoard.release();
                }
            }
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
        finally
        {
        	if (tread == null)
        	{
	            // 创建后台线程监控键盘设备,如果键盘设备关闭,则重新开启
        		tread = new IBMJPOS_KeyBoard_Thread(keyBoard,DeviceName.deviceKeyBoard.split(",")[0].split(";")[0].trim());
        		tread.start();
        	}
        }        
    }

    class KeyRobotEvent implements DataListener
    {
        Event event = new Event();
        
        public void dataOccurred(DataEvent arg0)
        {
        	
            try
            {
                int key = (int) keyBoard.getPOSKeyData();
                sendKey1(key);
                keyBoard.setDataEventEnabled(true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void sendKey1(int keycode)
        {
            for (int i = 1; i < DeviceName.deviceKeyBoard.split(",").length;
                     i++)
            {
                try
                {
                    if (keycode == Integer.parseInt(DeviceName.deviceKeyBoard.split(",")[i]))
                    {
                        return;
                    }
                }
                catch (Exception er)
                {
                    er.printStackTrace();
                }
            }
 
            switch (keycode)
            {
                case 75:
                    event.keyCode = (int) 'a';
                    event.character = 'a';

                    break;

                case 76:
                    event.keyCode = (int) 'b';
                    event.character = 'b';

                    break;

                case 79:
                    event.keyCode = (int) 'c';
                    event.character = 'c';

                    break;

                case 78:
                    event.keyCode = (int) 'd';
                    event.character = 'd';

                    break;

                case 77:
                    event.keyCode = (int) 'e';
                    event.character = 'e';

                    break;

                case 59:
                    event.keyCode = (int) 'f';
                    event.character = 'f';

                    break;

                case 60:
                    event.keyCode = (int) 'g';
                    event.character = 'g';

                    break;

                case 63:
                    event.keyCode = (int) 'h';
                    event.character = 'h';

                    break;

                case 62:
                    event.keyCode = (int) 'i';
                    event.character = 'i';

                    break;

                case 61:
                    event.keyCode = (int) 'j';
                    event.character = 'j';

                    break;

                case 107:
                    event.keyCode = (int) 'k';
                    event.character = 'k';

                    break;

                case 108:
                    event.keyCode = (int) 'l';
                    event.character = 'l';

                    break;

                case 111:
                    event.keyCode = (int) 'm';
                    event.character = 'm';

                    break;

                case 110:
                    event.keyCode = (int) 'n';
                    event.character = 'n';

                    break;

                case 109:
                    event.keyCode = (int) 'o';
                    event.character = 'o';

                    break;

                case 123:
                    event.keyCode = (int) '/';
                    event.character = '/';

                    break;

                case 124:
                    event.keyCode = (int) '7';
                    event.character = '7';

                    break;

                case 127:
                    event.keyCode = (int) '4';
                    event.character = '4';

                    break;

                case 126:
                    event.keyCode = (int) '1';
                    event.character = '1';

                    break;

                case 125:
                    event.keyCode = (int) '0';
                    event.character = '0';

                    break;

                case 12:
                    event.keyCode = (int) '8';
                    event.character = '8';

                    break;

                case 15:
                    event.keyCode = (int) '5';
                    event.character = '5';

                    break;

                case 14:
                    event.keyCode = (int) '2';
                    event.character = '2';

                    break;

                case 13:
                    event.keyCode = (int) '[';
                    event.character = '[';
                    
                    break;

                case 27:
                    event.keyCode = SWT.ESC;

                    break;

                case 28:
                    event.keyCode = (int) '9';
                    event.character = '9';

                    break;

                case 31:
                    event.keyCode = (int) '6';
                    event.character = '6';

                    break;

                case 30:
                    event.keyCode = (int) '3';
                    event.character = '3';

                    break;

                case 29:
                    event.keyCode = (int) '.';
                    event.character = '.';

                    break;

                case 139:
                    event.keyCode = (int) 'p';
                    event.character = 'p';

                    break;

                case 140:
                    event.keyCode = (int) 'q';
                    event.character = 'q';

                    break;

                case 143:
                    event.keyCode = (int) 'r';
                    event.character = 'r';

                    break;

                case 142:
                    event.keyCode = (int) 's';
                    event.character = 's';

                    break;

                case 141:
                    event.keyCode = (int) 't';
                    event.character = 't';

                    break;

                case 175:
                    event.keyCode = (int) 'u';
                    event.character = 'u';

                    break;

                case 156:
                    event.keyCode = (int) 'v';
                    event.character = 'v';

                    break;

                case 159:
                    event.keyCode = (int) 'w';
                    event.character = 'w';

                    break;

                case 158:
                    event.keyCode = (int) 'x';
                    event.character = 'x';

                    break;

                case 157:
                    event.keyCode = (int) 'y';
                    event.character = 'y';

                    break;

                case 191:
                    event.keyCode = (int) 'z';
                    event.character = 'z';

                    break;

                case 172:
                    event.keyCode = (int) ',';
                    event.character = ',';

                    break;

                case 155:
                    event.keyCode = (int) '`';
                    event.character = '`';

                    break;

                case 174:
                    event.keyCode = (int) '=';
                    event.character = '=';

                    break;

                case 173:
                    event.keyCode = 13;

                    break;

                case 187:
                    event.keyCode = (int) ']';
                    event.character = ']';

                    break;

                case 188:
                    event.keyCode = SWT.HOME;

                    break;

                case 171:
                    event.keyCode = SWT.END;

                    break;

                case 190:
                    event.keyCode = SWT.INSERT;

                    break;

                case 189:
                    event.keyCode = SWT.DEL;

                    break;

                case 80:
                    event.keyCode = SWT.F1;

                    break;

                default:
                    System.out.println("miss " + keycode);

                    return;
            }
            
            cusMethod(keycode);
            
            Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        if (Display.getCurrent() != null)
                        {	
                            event.widget = Display.getCurrent().getFocusControl();
                            event.doit = true;

                            event.type = SWT.KeyDown;

                            Display.getCurrent().post(event);

                            event.type = SWT.KeyUp;

                            Display.getCurrent().post(event);
                        }
                    }
                });
        }
        
        public void cusMethod(int keycode)
        {
        	if (cusname.equals("Szxw"))
        	{
        		switch(keycode)
        		{
                	case 77:
                		event.keyCode = 8;
                		break;
                    case 141:
                        event.keyCode = 13;
                        break;
                    case 173:
                        event.keyCode = (int) 't';
                        event.character = 't';
                        break;
                    case 187:
                        event.keyCode = SWT.ESC;

                        break;
                    case 27:
                        event.keyCode = (int) ']';
                        event.character = ']';
                        break;
                        
        		}
        	}
        	
        	if (cusname.equals("Cczz"))
        	{
        		switch(keycode)
        		{
                    case 141:
                        event.keyCode = 13;
                        break;
                    case 173:
                        event.keyCode = (int) 't';
                        event.character = 't';
                        break;
                    case 158:
                    	event.keyCode = SWT.F1;

                        break;
                    case 80:
                        event.keyCode = (int) 'x';
                        event.character = 'x';

                        break;

                        
        		}
        	}
        	
        	if (cusname.equals("Tcrc"))
        	{
        		switch(keycode)
        		{
                    case 159:
                        event.keyCode = SWT.ARROW_UP;
                        break;
                    case 158:
                        event.keyCode = SWT.ARROW_DOWN;
                        break;
                    case 142:
                    	event.keyCode = SWT.ARROW_LEFT;
                        break;
                    case 174:
                        event.keyCode = SWT.ARROW_RIGHT;
                        break;

                        
        		}
        	}
        	
        	if (cusname.equals("Nmzd"))
        	{
        		switch(keycode)
        		{
                    case 173:
                        event.keyCode = (int) 'p';
                        event.character = 'p';
                        break;   
                    case 139:
                        event.keyCode = (int) 't';
                        event.character = 't';
                        break;
                    case 141:
                    	event.keyCode = 13;
                        break;
                    case 60:
                    	event.keyCode = SWT.ARROW_UP;
                        break;
                    case 63:
                    	event.keyCode = SWT.ARROW_DOWN;
                        break;
                    case 79:
                    	event.keyCode = SWT.ARROW_LEFT;
                        break;
                    case 111:
                    	event.keyCode = SWT.ARROW_RIGHT;
                        break;
                        
        		}
        	}
        	
        	if (cusname.equals("Bjys"))
        	{
        		switch(keycode)
        		{
        			case 189:
        				event.keyCode = 13;
                    break;
        			case 173:
        				 event.keyCode = SWT.DEL;
                    break;     
        		}
        	}
        	
        	if (cusname.equals("Wqbh"))
        	{
        		switch(keycode)
        		{
        			case 123:
        				 event.keyCode = 8;
                    break;     
        		}
        	}
        	
        }
    }
    
    

	public Vector getPara() {
		Vector v = new Vector();
		v.add(new String[]{Language.apply("JPOS逻辑名"),"POSKeyBoard1"});
		return v;
	}

	public String getDiscription() 
	{
		return Language.apply("IBM的JPOS驱动方式键盘");
	}
}
