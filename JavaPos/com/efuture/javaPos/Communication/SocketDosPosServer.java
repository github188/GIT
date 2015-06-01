package com.efuture.javaPos.Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateByte;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.DosServer.HeadXSJDef;

public class SocketDosPosServer
{
	private String _head_iSum = "";//4+1 随机校验码
	private String _head_sForegroundCode = "";//8+1 收银机号 0001
	private String _head_iCommandCode = "";//4+1 命令代码 0010/（返回时，0001表示成功，否则表示失败）
	//private String _head_iCommandFirst = "00";//2+1 命令优先级 00（默认为00）
	private String _head_iRecordNum = "";//4+1 记录个数 0001
		
	private String _serverIP;
	private int _serverPort;
	private int _timeout;
	
	private final String _charset = "GBK"; 
	private final String _refFlag = "refSocket";
	private final int _headSize = 27;//包头长度
	private final int _bodyLenSize = 4;//包体长度字段长度	

	private final int _headSize_XSJ = 13;//新世纪储值卡 包头长度
	private final int _bodyLenSize_XSJ = 2;//新世纪储值卡 包体长度字段长度

	private String _error = "";//错误信息
	
	public SocketDosPosServer()
	{
		//测试
		/*_serverIP = "130.130.26.100";
		_serverPort = 9002;
		_timeout = 15000;*/
		this(GlobalInfo.sysPara.dosPosSvrAddress);//默认加载参数配置的服务器地址
	}
	
	public SocketDosPosServer(String server)
	{		
		if(server!=null)
		{
			String[] arr = server.split("\\|");
			if(arr.length==2)
			{
				setServer(arr[0].trim(), Convert.toInt(arr[1]), 0);
				return;
			}
			else if(arr.length>=3)
			{
				setServer(arr[0].trim(), Convert.toInt(arr[1]), Convert.toInt(arr[2]));
				return;
			}
		}
		PosLog.getLog(this.getClass()).info("SocketDosPosServer初始化失败：server=[" + String.valueOf(server) + "]");	
	}
	
	public SocketDosPosServer(String serverIP, int serverPort)
	{
		this(serverIP, serverPort, 6000);		
	}
	
	/**
	 * 初始化socket
	 * @param serverIP 服务器IP
	 * @param serverPort 服务器端口
	 * @param timeout 连接超时时间（毫秒，默认至少6000毫秒）
	 */
	public SocketDosPosServer(String serverIP, int serverPort, int timeout)
	{
		setServer(serverIP, serverPort, timeout);
	}
	
	/**
	 * 初始化socket
	 * @param serverIP 服务器IP
	 * @param serverPort 服务器端口
	 * @param timeout 连接超时时间（毫秒，默认至少6000毫秒）
	 */
	public void setServer(String serverIP, int serverPort, int timeout)
	{
		_serverIP = serverIP;
		_serverPort = serverPort;
		if(timeout>=6000)
		{
			_timeout = timeout;
		}
		else
		{
			_timeout = 6000;
		}
		PosLog.getLog(this.getClass()).info("SocketDosPosServer() serverIP=[" + serverIP + "],serverPort=[" + serverPort + "],timeout=[" + timeout + "].");
	}
	
	public String getError()
	{
		return this._error;
	}

	/**
	 * socket通讯（单行通讯记录）
	 * @param cmdcode 命令ID
	 * @param classObj 请求对象
	 * @return 包体集合(即vec.add(byte[]))
	 */
	public Vector socketSend(int cmdcode, Object classObj)
	{
		return socketSend(getRequestOne(cmdcode, classObj));
	}
	
	/**
	 * socket通讯（单行通讯记录）
	 * @param cmdcode 通讯命令ID
	 * @param bytePara 请求字符串
	 * @return
	 */
	public Vector socketSend(int cmdcode, byte[] bytePara)
	{
		return socketSend(getRequestOne(cmdcode, bytePara));
	}
	/**
	 * socket通讯（单行通讯记录）
	 * @param cmdcode 通讯命令ID
	 * @param strPara 请求字节流
	 * @return
	 */
	public Vector socketSend(int cmdcode, String strPara)
	{
		return socketSend(getRequestOne(cmdcode, ManipulateByte.getStringBytes(strPara)));
	}

	/**
	 * socket通讯
	 * @param vecByteArr 字节vector数组
	 * @return 包体集合(即vec.add(byte[]))
	 */
	public Vector socketSend_oldBak(Vector vecByteArr)
	{
		//if(_head_iCommandCode.equals("85")) return socketSend_test(vecByteArr);
		Socket socket = null;
		OutputStream ops = null;
		InputStream ips = null;
		Vector responseVecByte = new Vector();
		String msg="";
		try
		{
			if (vecByteArr == null || vecByteArr.size() <= 0) { return null; }
			long start = System.currentTimeMillis();
			setHelpMessage("正在发送" + this._head_iCommandCode + "号命令到CRM...");
			this._error="";
			PosLog.getLog(getClass()).info("socket new");
			socket = new Socket(_serverIP, _serverPort);
			socket.setSoTimeout(_timeout);
			ops = new DataOutputStream(socket.getOutputStream());
			ips = new DataInputStream(socket.getInputStream());

			PosLog.getLog(getClass()).info("------------发送-------------------start 流行数=[" + vecByteArr.size() + "]");
			for(int i=0; i<vecByteArr.size(); i++)
			{
				ops.write((byte[])vecByteArr.elementAt(i));
			}			
			ops.flush();
			PosLog.getLog(getClass()).info("------------发送-------------------end");
			
			//读取包头
			byte[] readBuffer = new byte[_headSize];
			int iReadBuffer = 0;//缓冲长度
			while (iReadBuffer == 0)
			{
				iReadBuffer = ips.read(readBuffer, iReadBuffer, _headSize - iReadBuffer);
				if (iReadBuffer >= _headSize) break;
			}			
			//System.out.println("response_head=" + new String(readBuffer));
			PosLog.getLog(getClass()).info("response_head=" + new String(readBuffer).replace('\0', ' '));
			
			//检查返回的包头是否合法
			if(!checkResponse((byte[])vecByteArr.elementAt(0), readBuffer)) return null;
			
			//根据包头中的iRecordNum来判断要读取的次数
			int iRecordNum = getResponseHead_RecordNum(readBuffer);//记录行数
			/*if(iRecordNum<=0)
			{
				PosLog.getLog(getClass()).info("包头合法，但返回记录行数为0");
				return null;
			}*/
			PosLog.getLog(getClass()).info("iRecordNum=[" + iRecordNum + "]");
			int iRecordNumCount=0;//记录循环器
			int bodyLenTmp=0;//包体长度
			while (iRecordNumCount < iRecordNum)
			{
				//取包体长度
				bodyLenTmp=0;
				iReadBuffer = 0;
				readBuffer = new byte[_bodyLenSize];
				while (iReadBuffer == 0)
				{
					iReadBuffer = ips.read(readBuffer, iReadBuffer, _bodyLenSize - iReadBuffer);
					if (iReadBuffer >= _bodyLenSize) break;
				}
				bodyLenTmp = Convert.toInt(new String(readBuffer, _charset).replace('\0', ' '));//包体长度
				System.out.println("body_len=[" + bodyLenTmp + "]");
				PosLog.getLog(getClass()).info("body_len=[" + bodyLenTmp + "]");
				
				//读取包体内容
				iReadBuffer = 0;
				readBuffer = new byte[bodyLenTmp];
				while (iReadBuffer == 0)
				{
					iReadBuffer = ips.read(readBuffer, iReadBuffer, bodyLenTmp - iReadBuffer);
					if (iReadBuffer >= bodyLenTmp) break;
				}
				System.out.println("body=" + new String(readBuffer, _charset).replace('\0', ' '));
				responseVecByte.add(readBuffer);//添加到vector用于返回
				if(readBuffer!=null && readBuffer.length>0)
				{
					for(int i=0; i<readBuffer.length; i++)
					{
						PosLog.getLog(getClass()).info("readBuffer[" + i + "] = " + readBuffer[i]);
					}
				}
				
				iRecordNumCount++;
			}
			PosLog.getLog(getClass()).info("------------获取byte[]-------------------end");
			long end = System.currentTimeMillis();
			msg = this._head_iCommandCode +"号CRM命令通讯结束，耗时" + (end - start) + " ms";
			setHelpMessage(msg);
			
			return responseVecByte;

		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			_error = er.getMessage();
			setHelpMessage( this._head_iCommandCode +"号CRM命令通讯异常：" + _error);
			return null;
		}
		finally
		{
			try
			{
				if (ops != null) ops.close();
				if (ips != null) ips.close();
				if (socket != null) socket.close();
			}
			catch (IOException e)
			{
				PosLog.getLog(getClass()).error(e);
				e.printStackTrace();
				_error = e.getMessage();
			}

		}		

	}
	
	public Vector socketSend(Vector vecByteArr)
	{
		Socket socket = null;
		Vector responseVecByte = new Vector();
		String msg="";
		
		try
		{
			if (vecByteArr == null || vecByteArr.size() <= 0) { return null; }
			long start = System.currentTimeMillis();
			setHelpMessage("正在发送" + this._head_iCommandCode + "号命令到CRM...");
			this._error="";
			PosLog.getLog(getClass()).info("socket new2 start");
			byte[] byteBuffer = new byte[1024*5];
			socket = new Socket(_serverIP, _serverPort);
			socket.setSoTimeout(_timeout);
			PosLog.getLog(getClass()).info("ReceiveBufferSize=[" + socket.getReceiveBufferSize() + "]");
			socket.setReceiveBufferSize(1024*11);
			PosLog.getLog(getClass()).info("------------发送-------------------start 流行数=[" + vecByteArr.size() + "]");
			for(int i=0; i<vecByteArr.size(); i++)
			{
				socket.getOutputStream().write((byte[])vecByteArr.elementAt(i));
			}
			socket.getOutputStream().flush();
			PosLog.getLog(getClass()).info("------------发送-------------------end");
			
			if (Convert.toInt(_head_iCommandCode)==CmdDosDef.GETREFUNDMONEY_EX)
			{
				Thread.sleep(200);//获取扣回信息时，延时200ms接收数据
			}
			socket.getInputStream().read(byteBuffer);
			socket.getInputStream().close();
			PosLog.getLog(getClass()).info("------------解析结果-------------------start");
			//getSubstringByte
			/*for(int i=0; i<readBuffer.length; i++)
			{
				PosLog.getLog(getClass()).info("readBuffer[" + i + "] = " + readBuffer[i]);
			}*/
			PosLog.getLog(getClass()).info("result=[" + new String(byteBuffer, _charset).replace('\0', ' ').trim() + "]");
			//PosLog.getLog(getClass()).info("------------解析结果-------------------start");
			
			//读取包头
			int startIndex=0;
			byte[] byteHead = getSubstringByte(byteBuffer,startIndex,_headSize);
			startIndex+=_headSize;
			PosLog.getLog(getClass()).info("response_head=" + new String(byteHead).replace('\0', ' '));
			
			//检查返回的包头是否合法
			if(!checkResponse((byte[])vecByteArr.elementAt(0), byteHead)) return null;
			
			//根据包头中的iRecordNum来判断要读取的次数
			int iRecordNum = getResponseHead_RecordNum(byteHead);//记录行数
			/*if(iRecordNum<=0)
			{
				PosLog.getLog(getClass()).info("包头合法，但返回记录行数为0");
				return null;
			}*/
			PosLog.getLog(getClass()).info("iRecordNum=[" + iRecordNum + "]");

			int iRecordNumCount=0;//记录循环器
			int bodyLenTmp=0;//包体长度
			while (iRecordNumCount < iRecordNum)
			{
				PosLog.getLog(getClass()).info("开始读取包体明细[" + (iRecordNumCount+1) + "/" + iRecordNum + "]");
				//取包体长度
				bodyLenTmp=0;				 
				byte[] byteBodyLen = getSubstringByte(byteBuffer, startIndex, _bodyLenSize);
				startIndex+=_bodyLenSize;
				bodyLenTmp = Convert.toInt(new String(byteBodyLen, _charset).replace('\0', ' '));//包体长度				
				PosLog.getLog(getClass()).info("body_len=[" + bodyLenTmp + "]");
				
				//读取包体内容
				byte[] byteBody = getSubstringByte(byteBuffer, startIndex, bodyLenTmp);
				startIndex+=bodyLenTmp;
				PosLog.getLog(getClass()).info("body=[" + new String(byteBody, _charset).replace('\0', ' ') + "]");
				responseVecByte.add(byteBody);//添加到vector用于返回
				
				if(ConfigClass.DebugMode && byteBody!=null && byteBody.length>0)
				{//日志记录二进制值
					for(int i=0; i<byteBody.length; i++)
					{
						PosLog.getLog(getClass()).info("byteBody[" + i + "] = " + byteBody[i]);
					}
				}
				
				PosLog.getLog(getClass()).info("第[" + (iRecordNumCount+1) + "/" + iRecordNum + "]读取包体明细end");
				iRecordNumCount++;
			}
			PosLog.getLog(getClass()).info("------------解析结果-------------------end");
			long end = System.currentTimeMillis();
			msg = this._head_iCommandCode +"号CRM命令通讯结束，耗时" + (end - start) + " ms";
			setHelpMessage(msg);
			
			return responseVecByte;
			

		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			_error = er.getMessage();
			setHelpMessage( this._head_iCommandCode +"号CRM命令通讯异常：" + _error);
			return null;
		}
		finally
		{
			try
			{
				if (socket != null) socket.close();
			}
			catch (IOException e)
			{
				PosLog.getLog(getClass()).error(e);
				e.printStackTrace();
				_error = e.getMessage();
			}
		}		

	}
		
	/**
	 * 获取请求包头字节流
	 * @param cmdcode 通讯命令ID
	 * @param iRecordNum 记录行数
	 * @param syjh 收银机号
	 * @return
	 */
	public byte[] getRequestHead(int cmdcode, int iRecordNum, String syjh)
	{
		_head_iSum = Convert.increaseChar(ManipulatePrecision.getRandom(), '\0', 4 + 1);
		_head_iCommandCode = String.valueOf(cmdcode);
		_head_sForegroundCode = syjh;

		String head = _head_iSum + Convert.increaseChar(_head_sForegroundCode, '\0', 8 + 1) + //SYJH
				Convert.increaseChar(ManipulateStr.PadLeft(_head_iCommandCode, 4, '0'), '\0', 4 + 1) + //命令代码 10
				Convert.increaseChar("00", '\0', 2 + 1) + //命令优先级
				Convert.increaseChar(ManipulateStr.PadLeft(String.valueOf(iRecordNum), 4, '0'), '\0', 4 + 1);//记录个数

		//System.out.println("request_head=[" + head + "]");
		PosLog.getLog(this.getClass()).info("request_head=[" + head.replace('\0', ' ') + "]");

		return head.getBytes();
	}
	
	/**
	 * 获取请求包头字节流
	 * @param cmdcode 通讯命令ID
	 * @param iRecordNum 记录行数
	 * @return
	 */
	public byte[] getRequestHead(int cmdcode, int iRecordNum)
	{
		if (GlobalInfo.syjStatus != null)
			_head_sForegroundCode = GlobalInfo.syjStatus.syjh;
		else
			_head_sForegroundCode = "0000";
		return getRequestHead(cmdcode, iRecordNum, _head_sForegroundCode);
	}
	
	/**
	 * 获取单个记录请求的请求数据流
	 * @param cmdcode 通讯命令ID
	 * @param classObj 请求对象
	 * @return
	 */
	public Vector getRequestOne(int cmdcode, Object classObj)
	{
		return getRequestOne(cmdcode, 1, getClassByte(classObj));
	}
	
	/**
	 * 获取单个记录请求的请求数据流
	 * @param cmdcode 通讯命令ID
	 * @param body 包体数据流
	 * @return
	 */
	public Vector getRequestOne(int cmdcode, byte[] body)
	{
		return getRequestOne(cmdcode, 1, body);
	}
	/**
	 * 获取记录请求的请求数据流
	 * @param cmdcode 通讯命令ID
	 * @param iRecordNum 记录行数
	 * @param body 包体
	 * @return
	 */
	public Vector getRequestOne(int cmdcode, int iRecordNum, byte[] body)
	{
		try
		{
			this._error="";
			Vector vecByteArr = new Vector();
			vecByteArr.add(getRequestHead(cmdcode, iRecordNum));//head
			vecByteArr.add(getBodyLen(body.length));//body_len
			vecByteArr.add(body);//body
			return vecByteArr;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			this._error=ex.getMessage();
			return null;
		}
		
	}
	
	/**
	 * 获取请求包头字节流
	 * @param cmdcode 通讯命令ID
	 * @return
	 */
	public byte[] getRequestHead(int cmdcode)
	{
		return getRequestHead(cmdcode, 1);
	}
		
	/**
	 * 获取响应的记录行数
	 * @param headByte
	 * @return
	 */
	public int getResponseHead_RecordNum(byte[] headByte)
	{
		int iRecordNum=0;
		try
		{			
			String strHead = getString(headByte).replace('\0', ' ');
			iRecordNum = Convert.toInt(strHead.substring(22, 22+5));
			_head_iRecordNum = String.valueOf(iRecordNum);
			//System.out.println("包头返回记录行数 iRecordNum=[" + iRecordNum + "]");
			PosLog.getLog(this.getClass()).info("包头返回记录行数 iRecordNum=[" + iRecordNum + "]");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
		}
		return iRecordNum;
	}
		
	public boolean checkResponse(byte[] reqByte, byte[] resByte)
	{
		try
		{
			this._error="";
			String req = this.getString(reqByte).replace('\0', ' ');
			String res = this.getString(resByte).replace('\0', ' ');
			if(Convert.toInt(res.substring(14, 14+5).toString().trim())!=1)
			{
				this._error = "通讯命令失败返回";
				PosLog.getLog(this.getClass()).info("req_head=[" + req + "],res_head=[" + res + "]");
				return false;
			}
			if(Convert.toInt(req.substring(0, 0+5).toString())!=Convert.toInt(res.substring(0, 0+5).toString()))
			{
				this._error = "请求与返回包头的校验码不匹配";
				PosLog.getLog(this.getClass()).info("req_head=[" + req + "],res_head=[" + res + "]");
				return false;
			}
			if(Convert.toInt(req.substring(5, 5+9).toString())!=Convert.toInt(res.substring(5, 5+9).toString()))
			{
				this._error = "请求与返回包头的收银机号不匹配";
				PosLog.getLog(this.getClass()).info("req_head=[" + req + "],res_head=[" + res + "]");
				return false;
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			this._error = "检查返回包头合法性时异常" + ex.getMessage();
			return false;
		}
	}
	
	public Vector copyByteVector(Vector vecByte1, Vector vecByte2)
	{
		if(vecByte1==null || vecByte1==null)
		{
			return null;
		}
		Vector vec = new Vector();
		int i=0;
		for(i=0; i<vecByte1.size(); i++)
		{
			vec.add(vecByte1.elementAt(i));
		}
		for(i=0; i<vecByte2.size(); i++)
		{
			vec.add(vecByte2.elementAt(i));
		}
		
		return vec;
	}
	
	public byte[] copyByteArr(byte[] byte1, byte[] byte2)
	{
		byte[] bodysub = new byte[byte1.length + byte2.length];
		
		System.arraycopy(byte1,0,bodysub,0,byte1.length);
		System.arraycopy(byte2,0,bodysub,byte1.length,byte2.length);
		return bodysub;
	}
	
	public byte[] getBodyLen(int byteLen)
	{
		return ManipulateStr.PadLeft(String.valueOf(byteLen), 4, '0').getBytes();
	}
	
	public String getString(byte[] byt)
	{
		return ManipulateByte.getString(byt, this._charset, '\0', ' ');
	}
	
	/**
	 * 批量获取byte
	 * @param vecObj
	 * @return
	 */
	public Vector getClassByte(Vector vecObj)
	{
		if(vecObj==null || vecObj.size()<=0) return null;
		
		try
		{
			Vector vec = new Vector();
			byte[] byt=null;
			for(int i=0; i<vecObj.size(); i++)
			{
				if(i==0)
				{
					byt = getClassByte(vecObj.elementAt(i));//包体
					if(byt==null)
					{
						this._error="读取对象的数据流时异常,";
						PosLog.getLog(this.getClass()).info(this._error);
						return null;
					}
					vec.add(this.getBodyLen(byt.length));//包长
					vec.add(byt);//包体
				}
				else
				{
					byt = getClassByte(vecObj.elementAt(i));//包体
					if(byt==null)
					{
						this._error="读取对象的数据流时异常,";
						PosLog.getLog(this.getClass()).info(this._error);
						return null;
					}	
					vec.add(this.getBodyLen(byt.length));//包长
					vec.add(byt);//包体
				}
			}
			return vec;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			this._error="读取对象的数据流时异常," + ex.getMessage();
			PosLog.getLog(this.getClass()).info(this._error);
			PosLog.getLog(this.getClass()).error(ex);
			return null;
		}
		
	}
	
	/**
	 * 获取对象的包体信息（包长+包体）
	 * @param classObj 对象
	 * @return
	 */
	public Vector getClassByteAndLen(Object classObj)
	{
		Vector vec = new Vector();
		byte[] byt = getClassByte(classObj);
		if(byt!=null && byt.length>0)
		{
			vec.add(this.getBodyLen(byt.length));
			vec.add(byt);
			return vec;
		}
		else
			return null;
		
	}
	
	/**
	 * 获取类对象流
	 * @param classObj
	 * @return
	 */
	public byte[] getClassByte(Object classObj)
	{
		byte[] objByte = null;
		try
		{
			if (classObj == null) return null;

			Class cls = classObj.getClass();
			String[] ref = null;
			try
			{
				ref = (String[]) cls.getDeclaredField(_refFlag).get(classObj);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				PosLog.getLog(getClass()).error("getClassByte ex:" + e);
				return null;				
			}
			String[] refItem;			
			Object fieldValue;
			String fieldName;
			char fieldType;
			int fieldLen;
			for (int i = 0; i < ref.length; i++)
			{
				if (ref[i].length() <= 0) continue;

				refItem = ref[i].split("\\|");
				if (refItem.length < 3) continue;
				/**
				 * refItem[0]字段名
				 * refItem[1]字段类型
				 *      C char 1
				 *      S string 变长
				 *      I short 2
				 *      L int/long 4
				 *      D double 8
				 * refItem[2]字段长度
				 */
				fieldName = refItem[0];
				fieldType = (refItem[1] + " ").toUpperCase().charAt(0);
				fieldLen = Convert.toInt(refItem[2]);

				byte[] filedByte = new byte[fieldLen];
				if(fieldName.equals("name"))
				{
					System.out.println("");
				}
				fieldValue = cls.getDeclaredField(fieldName).get(classObj);
				if (fieldType == 'C')
				{
					if(fieldValue==null) fieldValue="";
					filedByte = ManipulateByte.getCharBytes((String.valueOf(fieldValue) + '\0').charAt(0));
				}
				else if (fieldType == 'S')
				{
					if(fieldValue==null) fieldValue="";
					filedByte = ManipulateByte.getStringBytes(String.valueOf(fieldValue), fieldLen);
				}
				else if (fieldType == 'I')
				{
					filedByte = ManipulateByte.getShortBytes(Convert.toShort(fieldValue));
				}
				else if (fieldType == 'L')
				{
					filedByte = ManipulateByte.getLong4Bytes(Convert.toLong(fieldValue));
				}
				else if (fieldType == 'D')
				{
					filedByte = ManipulateByte.getDoubleBytes(Convert.toDouble(fieldValue));
				}
				else
				{
					if(fieldValue==null) fieldValue="";
					filedByte = ManipulateByte.getStringBytes(String.valueOf(fieldValue), fieldLen);
				}

				if (objByte == null)
				{
					objByte = filedByte;
				}
				else
				{
					objByte = this.copyByteArr(objByte, filedByte);
				}
				//System.out.println("request: fieldName=[" + fieldName + "],fieldValue=[" + fieldValue + "],byte[]_length=[" + objByte.length + "]");
				PosLog.getLog(this.getClass()).info("request: fieldName=[" + fieldName + "],fieldValue=[" + fieldValue + "],byte[]_length=[" + objByte.length + "]");
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error("getClassBytew ex:" + ex);
			return null;
		}
		return objByte;
	}
	
	/**
	 * 获取类对象值（通过字节流)
	 * @param byt 字节流
	 * @param classObj 对象
	 * @return
	 */
	public Object getClassObjValue(byte[] byt, Object classObj)
	{
		Object objRet = classObj;
		try
		{
			if (classObj == null) return null;			

			Class cls = classObj.getClass();
			String[] ref = null;
			try
			{
				ref = (String[]) cls.getDeclaredField(_refFlag).get(classObj);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				PosLog.getLog(getClass()).error("getClassValue ex:" + e);
				return null;
			}

			String[] refItem;
			int byteAllLen = 0;
			String fieldName;
			char fieldType;
			int fieldLen;

			Field f;
			for (int i = 0; i < ref.length; i++)
			{
				refItem = ref[i].split("\\|");
				if (refItem.length < 3) continue;
				/**
				 * refItem[0]字段名
				 * refItem[1]字段类型
				 * 		C char 1
				 *      S string 变长
				 *      I short 2
				 *      L int/long 4
				 *      D double 8
				 * refItem[2]字段长度
				 */
				fieldName = refItem[0];
				fieldType = (refItem[1] + " ").toUpperCase().charAt(0);
				fieldLen = Convert.toInt(refItem[2]);
				f = cls.getDeclaredField(fieldName);

				byteAllLen += fieldLen;
				//读取指定字段的byte，并转换成对应的类型，并赋值到obj
				byte[] filedByte = new byte[fieldLen];
				int count = 0;
				int j = byteAllLen - fieldLen;
				for (; j < byteAllLen; j++)
				{
					filedByte[count] = byt[j];
					count++;
				}

				//转换byte--对应类型值
				if (fieldType == 'C')
				{
					f.setChar(classObj, ManipulateByte.getChar(filedByte));
				}
				else if (fieldType == 'S')
				{
					f.set(classObj, getString(filedByte));
				}
				else if (fieldType == 'I')
				{
					f.setShort(classObj, ManipulateByte.getShort(filedByte));
				}
				else if (fieldType == 'L')
				{
					f.setLong(classObj, ManipulateByte.getLong4(filedByte));
				}
				else if (fieldType == 'D')
				{
					f.setDouble(classObj, ManipulateByte.getDouble(filedByte));
				}
				else
				{
					f.set(classObj, this.getString(filedByte));
				}

				//System.out.println("response: fieldType=[" + String.valueOf(fieldType) + "],fieldName=[" + fieldName + "],fieldValue=[" + f.get(classObj) + "]");
				PosLog.getLog(getClass()).info("response: fieldType=[" + String.valueOf(fieldType) + "],fieldName=[" + fieldName + "],fieldValue=[" + f.get(classObj) + "]");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).error("getClassByte ex2:" + ex);
			return null;
		}
		return objRet;
	}
	
	private void setHelpMessage(String msg)
	{
		PosLog.getLog(getClass()).info(msg);
		if (GlobalInfo.statusBar != null) GlobalInfo.statusBar.setHelpMessage(msg);
	}

	public Vector socketSend_XSJ(Vector vecByteArr)
	{
		Socket socket = null;
		OutputStream ops = null;
		InputStream ips = null;
		Vector responseVecByte = new Vector();
		String msg="";
		try
		{
			if (vecByteArr == null || vecByteArr.size() <= 0) { return null; }
			long start = System.currentTimeMillis();
			setHelpMessage("正在发送[" + this._head_iCommandCode + "]号命令到MzkServer...");
			this._error="";
			PosLog.getLog(getClass()).info("socket new");
			socket = new Socket(_serverIP, _serverPort);
			socket.setSoTimeout(_timeout);
			ops = new DataOutputStream(socket.getOutputStream());
			ips = new DataInputStream(socket.getInputStream());

			PosLog.getLog(getClass()).info("------------发送-------------------start 流行数=[" + vecByteArr.size() + "]");
			for(int i=0; i<vecByteArr.size(); i++)
			{
				ops.write((byte[])vecByteArr.elementAt(i));
			}			
			ops.flush();
			PosLog.getLog(getClass()).info("------------发送-------------------end");
			
			//读取包头
			byte[] readBuffer = new byte[_headSize_XSJ];
			int iReadBuffer = 0;//缓冲长度
			while (iReadBuffer == 0)
			{
				iReadBuffer = ips.read(readBuffer, iReadBuffer, _headSize_XSJ - iReadBuffer);
				if (iReadBuffer >= _headSize_XSJ) break;
			}			
			//System.out.println("response_head=" + new String(readBuffer));
			PosLog.getLog(getClass()).info("response_head=" + new String(readBuffer).replace('\0', ' '));
			
			//检查返回的包头是否合法
			HeadXSJDef resObj = checkResponse_XSJ((byte[])vecByteArr.elementAt(0), readBuffer);
			if(resObj==null) return null;
			
			//根据包头中的iRecordNum来判断要读取的次数
			int iRecordNum = resObj.iRecordNum;//getResponseHead_RecordNum(readBuffer);//记录行数
			PosLog.getLog(getClass()).info("iRecordNum=[" + iRecordNum + "]");
			int iRecordNumCount=0;//记录循环器
			int bodyLenTmp=0;//包体长度
			while (iRecordNumCount < iRecordNum)
			{
				//取包体长度
				bodyLenTmp=0;
				iReadBuffer = 0;
				readBuffer = new byte[_bodyLenSize_XSJ];
				while (iReadBuffer == 0)
				{
					iReadBuffer = ips.read(readBuffer, iReadBuffer, _bodyLenSize_XSJ - iReadBuffer);
					if (iReadBuffer >= _bodyLenSize_XSJ) break;
				}
				bodyLenTmp = ManipulateByte.getShort(readBuffer);//Convert.toInt(new String(readBuffer, _charset).replace('\0', ' '));//包体长度
				System.out.println("body_len=[" + bodyLenTmp + "]");
				
				//读取包体内容
				iReadBuffer = 0;
				readBuffer = new byte[bodyLenTmp];
				while (iReadBuffer == 0)
				{
					iReadBuffer = ips.read(readBuffer, iReadBuffer, bodyLenTmp - iReadBuffer);
					if (iReadBuffer >= bodyLenTmp) break;
				}
				//System.out.println("body=" + new String(readBuffer, _charset).replace('\0', ' '));
				responseVecByte.add(readBuffer);//添加到vector用于返回
				
				iRecordNumCount++;
			}
			PosLog.getLog(getClass()).info("------------获取byte[]-------------------end\n");
			long end = System.currentTimeMillis();
			msg = this._head_iCommandCode +"号Mzk命令通讯结束，耗时" + (end - start) + " ms";
			setHelpMessage(msg);
			
			return responseVecByte;

		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			_error = er.getMessage();
			setHelpMessage( this._head_iCommandCode +"号Mzk命令通讯异常：" + _error);
			return null;
		}
		finally
		{
			try
			{
				if (ops != null) ops.close();
				if (ips != null) ips.close();
				if (socket != null) socket.close();
			}
			catch (IOException e)
			{
				PosLog.getLog(getClass()).error(e);
				e.printStackTrace();
				_error = e.getMessage();
			}

		}		

	}
	
	public HeadXSJDef checkResponse_XSJ(byte[] reqByte, byte[] resByte)
	{
		try
		{
			this._error="";
			HeadXSJDef reqObj = (HeadXSJDef)this.getClassObjValue(reqByte, HeadXSJDef.class.newInstance());
			HeadXSJDef resObj = (HeadXSJDef)this.getClassObjValue(resByte, HeadXSJDef.class.newInstance());
			if(resObj.iCommandCode!=1)//返回1表示成功，否则失败
			{
				this._error = "通讯命令失败返回";
				PosLog.getLog(this.getClass()).info("通讯命令失败返回");
				return null;
			}
			if(resObj.iSum!=reqObj.iSum)
			{
				this._error = "请求与返回包头的校验码不匹配";
				PosLog.getLog(this.getClass()).info("请求与返回包头的校验码不匹配");
				return null;
			}
			if(resObj.sForegroundCode==null || reqObj.sForegroundCode==null || resObj.sForegroundCode.replace('\0', ' ').equalsIgnoreCase(reqObj.sForegroundCode.replace('\0', ' '))==false)
			{
				this._error = "请求与返回包头的收银机号不匹配";
				PosLog.getLog(this.getClass()).info("请求与返回包头的收银机号不匹配");
				return null;
			}
			return resObj;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass()).error(ex);
			this._error = "检查返回包头合法性时异常" + ex.getMessage();
			return null;
		}
	}
	
	public Vector getRequestOne_XSJ(int cmdcode, int iRecordNum, Object classObj)
	{
		return getRequestOne_XSJ(cmdcode, iRecordNum, getClassByte(classObj));
	}
	public Vector getRequestOne_XSJ(int cmdcode, int iRecordNum, byte[] body)
	{
		try
		{
			this._error="";
			this._head_iCommandCode = String.valueOf(cmdcode);
			this._head_iRecordNum = String.valueOf(iRecordNum); 
			Vector vecByteArr = new Vector();
			vecByteArr.add(getRequestHead_XSJ(cmdcode, iRecordNum, ""));//head
			vecByteArr.add(ManipulateByte.getShortBytes((short)body.length));//body_len		
			vecByteArr.add(body);//body
			return vecByteArr;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			this._error=ex.getMessage();
			return null;
		}
		
	}
	
	public byte[] getRequestHead_XSJ(int cmdcode, int iRecordNum, String syjh)
	{
		HeadXSJDef reqObj = new HeadXSJDef();
		reqObj.iSum = Convert.toShort(ManipulatePrecision.getRandom());
		reqObj.sForegroundCode = Convert.increaseChar(GlobalInfo.syjStatus.syjh, '\0', 4 + 1);
		reqObj.iCommandCode = (short)cmdcode;
		reqObj.iCommandFirst = 0;
		reqObj.iRecordNum = (short)iRecordNum;
		PosLog.getLog(this.getClass()).info("getRequestHead_XSJ 拼装完毕");
		return this.getClassByte(reqObj);		
	}
	
	/**
	 * 从byte数组获取指定长度的byte数组
	 * @param sourceByte 源数据
	 * @param startIndex 开始位置
	 * @param len 长度
	 * @return 指定长度的byte数组
	 */
	public byte[] getSubstringByte(byte[] sourceByte, int startIndex, int len)
	{
		byte[] byt = null;
		try
		{
			if(len<0 || sourceByte==null || startIndex<0) return null;
			byt = new byte[len];
			int j=0;
			int endIndex=startIndex+len;
			if(endIndex>sourceByte.length) endIndex=sourceByte.length;
			if(startIndex>endIndex) startIndex=endIndex;
			for(int i=startIndex; i<endIndex; i++)
			{
				byt[j] = sourceByte[i];
				j++;
			}
			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass()).error(ex);
		}
		return byt;
		
	}
}


