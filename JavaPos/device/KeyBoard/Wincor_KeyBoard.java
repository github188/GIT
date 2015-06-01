package device.KeyBoard;

import java.util.Vector;

import com.efuture.javaPos.Device.Interface.Interface_KeyBoard;
import com.efuture.javaPos.Global.Language;


public class Wincor_KeyBoard implements Interface_KeyBoard
{
    
	public String getDiscription() 
	{
		return Language.apply("Wincor键盘");
	}

	public void close() {
		// TODO 自动生成方法存根
		
	}

	public Vector getPara() {
		// TODO 自动生成方法存根
		return null;
	}

	public boolean open() {
		// TODO 自动生成方法存根
		return true;
	}

	public void setEnable(boolean enable) {
		// TODO 自动生成方法存根
		
	}
}
