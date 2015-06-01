package custom.localize.Ywjb;

import java.io.BufferedReader;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.LoadSysInfo;

public class Ywjb_LoadSysInfo extends LoadSysInfo {
    public boolean getNetNewData(Label lbl_message)
    {
    	if (super.getNetNewData(lbl_message))
    	{
            setLabelHint(lbl_message, "正在检查是否有银联卡签购单未打印");
            printQGD();
            
            return true;
    	}
    	
    	return false;
    }
    
    public void printQGD()
    {
        ProgressBox pb = null;

        try
        {
        	String printName = "c:\\gmc\\toprint.txt";
        	if (!PathFile.fileExist(printName))
        	{
        		return ;
        	}
        	
            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");
            
            for (int i = 0; i < 1; i++)
            {

                BufferedReader br = null;

                try
                {
                    br = CommonMethod.readFileGBK(printName);

                    if (br == null)
                    {
                        new MessageBox("打开" + printName + "打印文件失败!");

                        return;
                    }

                    //
                    String line = null;

                    while ((line = br.readLine()) != null)
                    {
                        if (line.trim().equals("/CUT"))
                        {
                           Printer.getDefault().cutPaper_Normal();
                           continue;
                        }

                        Printer.getDefault().printLine_Normal(line);
                    }
                }
                catch (Exception e)
                {
                    new MessageBox(e.getMessage());
                }
                finally
                {
                    if (br != null)
                    {
                        br.close();
                    }
                }

                //XYKPrintDoc_End();
                
            }
            
            PathFile.deletePath(printName);
        }
        catch (Exception ex)
        {
            new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
            ex.printStackTrace();
        }
        finally
        {
            if (pb != null)
            {
                pb.close();
                pb = null;
            }
        }
    }
}
