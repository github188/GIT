package update.client;

import com.swtdesigner.SWTResourceManager;

public class UpdatePosMain
{
	public UpdatePosMain()
	{
		 if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
		 {
			 GlobalVar.heightPL = 30;
			 GlobalVar.style    = GlobalVar.style_linux;
		 }
		 else
         {
             GlobalVar.heightPL = 0;
             GlobalVar.style    = GlobalVar.style_windows;
         }
		 
		 new UpdatePosForm();  
	}
	
	public static void main(String[] args) 
	{
		try 
		{
			if (args != null)
	        {
				if (args.length >= 1)
	            {
					GlobalVar.UpdateMode = args[0];
	            }
				
				if (args.length >= 2)
				{
					RefushConfPath(args[1]);
				}
	        }
			
			if (GlobalVar.UpdateMode.equals("N")) return;
			
			new UpdatePosMain();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		SWTResourceManager.dispose();
		System.exit(0);
	}
	
	private static void RefushConfPath(String path)
	{
		GlobalVar.ConfigPath = path;
	        
		GlobalVar.UpdateConfig = path + "/Update.ini";
		GlobalVar.PosIdConfig = path + "/PosID.ini";
	
		GlobalVar.oldUpdateFile = GlobalVar.ConfigPath + "/"+ GlobalVar.ftpUpdateFile;
		
	 }
}
