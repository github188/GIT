package com.efuture.javaPos.Logic;

import java.io.File;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class DeleteCzDataBS 
{
	public void DeleteCzData() {

		try {
			ProgressBox pgb = null;
			File dir = new File(ConfigClass.LocalDBPath);
			
			if (!dir.exists()) {
				new MessageBox(Language.apply("没有面值卡冲正信息!"));
				return;
			}
			
				File[] tmp = dir.listFiles();
				Vector v = new Vector();
				int k = 0;
				for (int i = 0; i < tmp.length; i++) {
					if (tmp[i].isFile() && isCzFile(tmp[i].getName().trim())) {
						k++;
						ManipulateDateTime mdt = new ManipulateDateTime();
						mdt.setTimeInMill(tmp[i].lastModified());
						v.add(new String[] { String.valueOf(k),
								tmp[i].getName().trim(),
								mdt.getDateTimeString() });
					}
				}
				
				String czInfo = Language.apply("存在以下面值卡冲正信息,放弃请按’退出‘键");				
				
				if(v.size() <= 0) {
					czInfo = Language.apply("没有面值卡冲正信息!");
					new MessageBox(czInfo);
					return;
				}
				
				if (new MutiSelectForm().open(czInfo, new String[] {
						Language.apply("序号"), Language.apply("文件名"), Language.apply("修改时间") }, new int[] { 60, 230, 230 }, v) < 0) {
					return;
				}
				
				if (new MessageBox(Language.apply("删除冲正数据可能导致某些交易未被正确冲回!\n\n请确保本机的冲正数据都是已被后台处理过的\n\n你确定要删除这些无用的冲正数据吗？"),null,true).verify() != GlobalVar.Key1)
				{
					return;
				}

				for (int i = 0; i < tmp.length; i++) {
					if (tmp[i].isFile()) {

						if (isCzFile(tmp[i].getName().trim())) {
							if (pgb == null) {
								pgb = new ProgressBox();
							}

							pgb.setText(Language.apply("正在删除") + tmp[i].getName().trim());
							tmp[i].delete();
						}
					}
				}

			if (pgb != null) {
				pgb.close();
			}

			new MessageBox(Language.apply("删除冲正数据成功!"));
		} catch (Exception ex) {
			ex.printStackTrace();
			new MessageBox(Language.apply("删除冲正数据失败"));
		}
	}
	
	private boolean isCzFile(String filename)
	{
		if (filename.endsWith(".cz"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
