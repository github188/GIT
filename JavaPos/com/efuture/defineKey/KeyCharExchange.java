package com.efuture.defineKey;

import org.eclipse.swt.SWT;


public class KeyCharExchange
{
    public static char keyChar(int code)
    {
        switch (code)
        {
            case 1:
                return '0';

            case 2:
                return '1';

            case 3:
                return '2';

            case 4:
                return '3';

            case 5:
                return '4';

            case 6:
                return '5';

            case 7:
                return '6';

            case 8:
                return '7';

            case 9:
                return '8';

            case 10:
                return '9';

            case 11:
                return '.';

            default:
                return (char) 0;
        }
    }
    
    public static String keyexchange_new(String code)
    {
    	if (code.split("\\+").length >1)
    	{
    		String codes[] = code.split("\\+");
    		String statemask = keyexchange(Integer.parseInt(codes[1]));
    		if (statemask.trim().length() > 0)
    			return statemask+"+"+keyexchange(Integer.parseInt(codes[0]));
    		else
    			return keyexchange(Integer.parseInt(codes[0]));
    	}
    	return keyexchange(Integer.parseInt(code));
    }

    public static String keyexchange(int code)
    {
        String cha = null;
        
        switch (code)
        {
            case 0:
                cha = new String();

                break;

            case 13:
                cha = "Enter";

                break;

            case SWT.DEL:
                cha = "Delete";

                break;

            case SWT.INSERT:
                cha = "Insert";

                break;
                
            case SWT.HOME:
                cha = "Home";

                break;

            case SWT.PAGE_DOWN:
                cha = "Page Down";

                break;

            case SWT.PAGE_UP:
                cha = "Page Up";

                break;

            case SWT.ARROW_LEFT:
                cha = "Arrow Left";

                break;

            case SWT.ARROW_DOWN:
                cha = "Arrow Down";

                break;

            case SWT.ARROW_UP:
                cha = "Arrow Up";

                break;

            case SWT.ARROW_RIGHT:
                cha = "Arrow Right";

                break;

            case SWT.KEYPAD_0:
                cha = "Num 0";

                break;

            case SWT.KEYPAD_1:
                cha = "Num 1";

                break;

            case SWT.KEYPAD_2:
                cha = "Num 2";

                break;

            case SWT.KEYPAD_3:
                cha = "Num 3";

                break;

            case SWT.KEYPAD_4:
                cha = "Num 4";

                break;

            case SWT.KEYPAD_5:
                cha = "Num 5";

                break;

            case SWT.KEYPAD_6:
                cha = "Num 6";

                break;

            case SWT.KEYPAD_7:
                cha = "Num 7";

                break;

            case SWT.KEYPAD_8:
                cha = "Num 8";

                break;

            case SWT.KEYPAD_9:
                cha = "Num 9";

                break;

            case SWT.KEYPAD_ADD:
            	cha = "Num +";
            	
            	break;
            	
            case SWT.KEYPAD_SUBTRACT:
            	cha = "Num -";
            	
            	break;
            
            case SWT.KEYPAD_MULTIPLY:
            	cha = "Num *";
            	
            	break;
            
            case SWT.KEYPAD_DIVIDE:
                cha = "Num /";

                break;

            case SWT.KEYPAD_CR:
                cha = "Num Enter";

                break;

            case SWT.KEYPAD_DECIMAL:
                cha = "Num .";

                break;

            case 32:
                cha = "SPACE";

                break;
                
            case SWT.F1:
                cha = "F1";

                break;

            case SWT.F10:
                cha = "F10";

                break;

            case SWT.F11:
                cha = "F11";

                break;

            case SWT.F12:
                cha = "F12";

                break;

            case SWT.F2:
                cha = "F2";

                break;

            case SWT.F3:
                cha = "F3";

                break;

            case SWT.F4:
                cha = "F4";

                break;

            case SWT.F5:
                cha = "F5";

                break;

            case SWT.F6:
                cha = "F6";

                break;

            case SWT.F7:
                cha = "F7";

                break;

            case SWT.F8:
                cha = "F8";

                break;

            case SWT.F9:
                cha = "F9";

                break;

            case SWT.END:
                cha = "End";

                break;

            case SWT.ESC:
                cha = "Esc";

                break;

            case SWT.CTRL:
                cha = "Ctrl";

                break;

            case SWT.ALT:
                cha = "Alt";

                break;

            case SWT.SHIFT:
                cha = "Shift";

                break;

            case 8:
                cha = "Back Space";

                break;

            default:
                cha = (char) code + "";
        }

        
        return cha;
    }
    
}
