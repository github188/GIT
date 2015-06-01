package custom.localize.Ywjb;

import java.io.BufferedReader;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;


public class Ywjb_MenuFuncBS extends MenuFuncBS
{
    public final static int MN_QTCX   = 204;							//其他查询
    
    public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
    {
        switch (Integer.parseInt(mfd.code))
        {
            case MN_QTCX:
            	openQtxStj(mfd, mffe);  
                break;
            case StatusType.MN_QTXSTJ:
            	openQtCx(mfd, mffe);
            	break;
            default:
                return false;
        }

        return true;
    }

    public void printButtonEvent()
    {
        String path = "c:\\posrepext_prn.txt";

        if (PathFile.fileExist(path))
        {
            BufferedReader br = null;

            try
            {
                br = CommonMethod.readFileGBK(path);

                String line = null;

                while ((line = br.readLine()) != null)
                {
                    Printer.getDefault().printLine_Journal(line);
                }

                Printer.getDefault().cutPaper_Journal();
            }
            catch (Exception er)
            {
                er.printStackTrace();

                return;
            }
            finally
            {
                try
                {
                    if (br != null)
                    {
                        br.close();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                PathFile.deletePath(path);
            }
        }
    }

    private void openQtCx(MenuFuncDef mfd, MenuFuncEvent mffe)
    {
        if (PathFile.fileExist(GlobalVar.HomeBase + "\\posreport\\posreport.exe"))
        {
            try
            {
                CommonMethod.waitForExec(GlobalVar.HomeBase + "\\posreport\\posreport.exe " + printButtonHandle + "," + GlobalInfo.posLogin.gh, "posreport.exe");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
