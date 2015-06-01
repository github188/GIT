package custom.localize.Bjys;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;

public class Bjys_AppendBusinessPerEvent 
{
	private Shell shell = null;
	private Text txtBusPerOne = null;
	private Text txtBusPerTwo = null;
	private Text txtBusPerThree = null;
	private Bjys_AppendBusinessPerBS babpbs = null;
	private StringBuffer buffer = null;
	
	private Text focus = null;

	public Bjys_AppendBusinessPerEvent(Bjys_AppendBusinessPerForm babpf,StringBuffer buffer)
	{
		shell = babpf.getShell();
		txtBusPerOne = babpf.getTxtBusPerOne();
		txtBusPerTwo = babpf.getTxtBusPerTwo();
		txtBusPerThree = babpf.getTxtBusPerThree();
		babpbs	= new Bjys_AppendBusinessPerBS();
		this.buffer = buffer;
		//this.saleGoodsDef = saleGoodsDef;
		
//		设定键盘事件
        NewKeyEvent event = new NewKeyEvent()
	    {
	            public void keyDown(KeyEvent e,int key)
	            {
	            	keyPressed(e,key);
	            }
	
	            public void keyUp(KeyEvent e,int key)
	            {
	            	keyReleased(e,key);
	            }
	     };
	     
	     FocusListener listener = new FocusListener()
	     {
	          public void focusGained(FocusEvent e)
	          {
	               if (focus != e.widget)
	               {
	                    focus.setFocus();
	               }
	           }

	           public void focusLost(FocusEvent e)
	           {
	            	
	           }
	      };
	        
	     NewKeyListener key = new NewKeyListener();
	     key.event = event;
	     key.inputMode = key.IntegerInput;
	     
	     txtBusPerOne.addKeyListener(key);
	     txtBusPerOne.addFocusListener(listener);
	     txtBusPerTwo.addKeyListener(key);
	     txtBusPerTwo.addFocusListener(listener);
	     txtBusPerThree.addKeyListener(key);
	     txtBusPerThree.addFocusListener(listener);
	     
	     setFocus(txtBusPerOne);
	     txtBusPerOne.selectAll();
	}
	

	public void keyPressed(KeyEvent e,int key)
    {
		
    }

    public void keyReleased(KeyEvent e,int key)
    {
    	String yyyid = null;
    	
    	try
		{
			switch(key)
			{
				case GlobalVar.Enter:
					if (txtBusPerOne == e.getSource())
					{
						yyyid = txtBusPerOne.getText();
							
						if (babpbs.findYyyh(yyyid))
						{
							buffer.append(yyyid + " ;");
							setFocus(txtBusPerTwo);
							txtBusPerTwo.selectAll();
						}
						else
						{
							txtBusPerOne.selectAll();
						}
						
					}
					else if (txtBusPerTwo == e.getSource())
					{
						yyyid = txtBusPerTwo.getText();
							
						if (babpbs.findYyyh(yyyid))
						{
							buffer.append(yyyid + " ;");
							setFocus(txtBusPerThree);
							txtBusPerThree.selectAll();
						}
						else
						{
							txtBusPerTwo.selectAll();
						}
						
					}
					else
					{
						yyyid = txtBusPerThree.getText();
							
						if (babpbs.findYyyh(yyyid))
						{
							buffer.append(yyyid + " ");
							
							shell.close();
							shell.dispose();
							shell = null;
						}
						else
						{
							txtBusPerThree.selectAll();
						}
					}
				break;	
				case GlobalVar.Validation:
						
					Text text = (Text)e.getSource();
						
					yyyid = text.getText();
							
					if (!babpbs.findYyyh(yyyid))
					{
						text.selectAll();
						return;
					}
					
					buffer.delete(0,buffer.length());
					
					buffer.append(txtBusPerOne.getText() + " ;" + txtBusPerTwo.getText() + " ;" + txtBusPerThree.getText() + " ");
						
					shell.close();
					shell.dispose();
					shell = null;
				break;	
				case  GlobalVar.Exit:
					buffer.delete(0,buffer.length());
					
					shell.close();
					shell.dispose();
					shell = null;
				break;
			}
		}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    private void setFocus(Text focus)
    {
        this.focus = focus;
        focus.setFocus();
    }
}
