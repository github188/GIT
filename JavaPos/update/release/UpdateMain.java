package update.release;

import com.swtdesigner.SWTResourceManager;



public class UpdateMain 
{

	public UpdateMain()
	{
		new UpdateForm();
	}
	
	public static void main(String[] args) 
	{
		if (args != null)
        {
			if (args.length > 0)
            {
				RefushConfPath(args[0]);
            }
        }
		
		new UpdateMain();
		
		SWTResourceManager.dispose();
		System.exit(0);
	}
	
	private static void RefushConfPath(String path)
	{
		UpdateBS.path = path; 
	}
}
