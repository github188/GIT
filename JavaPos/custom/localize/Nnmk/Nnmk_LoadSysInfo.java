package custom.localize.Nnmk;

import java.io.File;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.PathFile;

import custom.localize.Bcrm.Bcrm_LoadSysInfo;

public class Nnmk_LoadSysInfo extends Bcrm_LoadSysInfo
{
	public boolean startLoadInfo(Label lbl_message)
	{
		//检查日志文件 只保留最后50天
		String path = "C:\\GWK";
		
		if (PathFile.fileExist(path))
		{
			File a = new File(path);
			String[] list = a.list();
			for (int i=0; i < list.length; i++)
			{
				
				if (list[i].indexOf("javaposbanklog")>=0)
				{
					String path1 = path+"\\"+list[i];
					String path2 = path+"\\javaposbanklog"+new ManipulateDateTime().skipDateSign(ManipulateDateTime.getCurrentDateBySign(),-50);
					System.out.println(path1 +" "+path2+" "+path1.compareTo(path2));
					if (path1.compareTo(path2) < 0)
					{
						PathFile.deletePath(path+"\\"+list[i]);
					}
				}
			}
		}
		return super.startLoadInfo(lbl_message);
	}
}
