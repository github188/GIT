package com.efuture.javaPos.Device.SerialPort;



import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;

import com.efuture.javaPos.Global.Language;

public class Javax_ParallelConnection 
{
	 private String portname;
	    private OutputStream os;
	    private CommPortIdentifier portId;
	    private ParallelPort sPort;
	    private boolean open;

	    public Javax_ParallelConnection(String name)
	    {
	        portname = name;
	        open     = false;
	    }

	    public void openConnection() throws ParallelConnectionException
	    {
	        // Obtain a CommPortIdentifier object for the port you want to open.
	        try
	        {
	        	// List Port
	        	boolean ok = false;
	        	StringBuffer sb = new StringBuffer();
	        	sb.append(Language.apply("必须是以下端口\n"));
	        	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	        	while(portList != null)
	        	{
	        		CommPortIdentifier p = (CommPortIdentifier)portList.nextElement();
	        		if (p == null) break;
	        		else
	        		{
	        			sb.append(p.getName());
	        			sb.append("\n");
	        			if (p.getName().equals(portname))
	        			{
	        				ok = true;
	        				break;
	        			}
	        		}
	        	}
	        	if (!ok)
	        	{
	        		throw new ParallelConnectionException(sb.toString());
	        	}
	        	
	        	//
	            portId = CommPortIdentifier.getPortIdentifier(portname);
	        }
	        catch (NoSuchPortException e)
	        {
	            throw new ParallelConnectionException(portname+":"+e.getMessage());
	        }
	        
	        // Open the port represented by the CommPortIdentifier object. Give
	        // the open call a relatively long timeout of 30 seconds to allow
	        // a different application to reliquish the port if the user
	        // wants to.
	        try
	        {
	            sPort = (ParallelPort) portId.open("Parallel" + portname,30000);
	        }
	        catch (PortInUseException e)
	        {
	            throw new ParallelConnectionException(portname+" Open Error:"+e.getMessage());
	        }

	        // Open the input and output streams for the connection. If they won't
	        // open, close the port before throwing an exception.
	        try
	        {
	            os = sPort.getOutputStream();
	        }
	        catch (IOException e)
	        {
	            sPort.close();
	            throw new ParallelConnectionException("Error opening i/o streams");
	        }

	        open = true;
	    }

	    /**
	    * Close the port and clean up associated elements.
	    */
	    public void closeConnection()
	    {
	        // If port is alread closed just return.
	        if (!open)
	        {
	            return;
	        }

	        // Check to make sure sPort has reference to avoid a NPE.
	        if (sPort != null)
	        {
	            try
	            {
	                // close the i/o streams.
	                os.close();
	            }
	            catch (IOException e)
	            {
	                System.err.println(e);
	            }

	            // Close the port.
	            sPort.close();
	        }

	        open = false;
	    }

	    /**
	    * Reports the open status of the port.
	    * @return true if port is open, false if port is closed.
	    */
	    public boolean isOpen()
	    {
	        return open;
	    }

	    public void sendChar(char c)
	    {
	        try
	        {
	            os.write((int) c);
	            os.flush();
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	    }

	    public void sendString(String s)
	    {
	        try
	        {
	        	// 直接os.write(s.getBytes())有一定机率导致JVM退出
	        	// 特别是打印挂单时收银机号和收银员号有9时,怀疑为DLL的冲突
	        	// 一个一个输出并捕获异常则能解决
	        	byte[] b = s.getBytes();
	        	for (int i=0;i<b.length;i++)
	        	{
	        		try
	        		{
	        			os.write(b[i]);
	        		}
	        		catch (IOException e)
	        		{
	        			
	        		}
	        	}
	            //os.write(s.getBytes());
	            os.flush();
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	    }
}
