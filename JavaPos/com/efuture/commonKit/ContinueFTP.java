package com.efuture.commonKit;

  import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.eclipse.swt.custom.StyledText;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Global.Language;

     
 
     
       
  /**    
   * 支持断点续传的FTP实用类    
   * @author BenZhou    
   * @version 0.1 实现基本断点上传下载    
   * @version 0.2 实现上传下载进度汇报    
   * @version 0.3 实现中文目录创建及中文文件创建，添加对于中文的支持    
   */     
  public class ContinueFTP {    
      private static final PosLog logger = PosLog.getLog(ContinueFTP.class);   
         
      public FTPClient ftpClient = new FTPClient();      
      StyledText styledText;
      public ContinueFTP(StyledText styledText){      
          
          //设置将过程中使用到的命令输出到控制台      
          this.styledText = styledText;
      }
      
      public ContinueFTP(){      
             
          //设置将过程中使用到的命令输出到控制台      
          
      }      
            
      /**    
       * 连接到FTP服务器    
       * @param hostname 主机名    
       * @param port 端口    
       * @param username 用户名    
       * @param password 密码    
       * @return 是否连接成功    
       * @throws IOException    
       */     
      public boolean connect(String hostname,int port,String username,String password) throws Exception{     
             
          try   
          {   
          // ftpClient.setSoTimeout(10);   
           // ftpClient.setDefaultTimeout(10);   
            ftpClient.connect(hostname, port);   
            ftpClient.setSoTimeout(1800000);   
              
          }catch(Exception e)   
          {   
              throw new Exception(Language.apply("登陆异常，请检查主机端口"));   
          }   
             
          ftpClient.setControlEncoding("GBK");      
          if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){      
              if(ftpClient.login(username, password)){      
                     
                  return true;      
              }   
              else   
                  throw new Exception(Language.apply("登陆异常，请检查密码账号"));   
          }   
          else   
              throw new Exception(Language.apply("登陆异常"));   
           
      }      
       
      public String[] getFileList(String filedir) throws IOException   
      {   
          ftpClient.enterLocalPassiveMode();     
             
          FTPFile[] files =ftpClient.listFiles(filedir);   
             
          String[] sfiles =null;   
          if(files!=null)   
          {   
              sfiles= new String[files.length];   
              for(int i=0;i<files.length;i++)   
              {   
                  //System.out.println(files[i].getName());   
                  sfiles[i]=files[i].getName();   
              }   
          }   
          return sfiles;   
      }   
      /**    
       * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报    
       * @param remote 远程文件路径    
       * @param local 本地文件路径    
       * @return 上传的状态    
       * @throws IOException    
       */     
      public int download(String remote,String local) throws IOException{      
          //设置被动模式      
          ftpClient.enterLocalPassiveMode();     
            
          //设置以二进制方式传输      
          ftpClient.setFileType(FTP.BINARY_FILE_TYPE);      
          int result;      
                
          //检查远程文件是否存在      
          FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"),"iso-8859-1"));      
          if(files.length != 1){      
              //System.out.println("远程文件不存在");   
              logger.info(Language.apply("远程文件不存在"));   
              return DownloadStatus.Remote_File_Noexist;      
          }      
                
          long lRemoteSize = files[0].getSize();      
          File f = new File(local);      
          //本地存在文件，进行断点下载      
          if(f.exists()){      
              long localSize = f.length();      
              //判断本地文件大小是否大于远程文件大小      
              if(localSize >= lRemoteSize){    
                  logger.info(Language.apply("本地文件大于远程文件，下载中止"));   
                  return DownloadStatus.Local_Bigger_Remote;      
              }      
                
              //进行断点续传，并记录状态      
              FileOutputStream out = new FileOutputStream(f,true);    
              //找出本地已经接收了多少   
              ftpClient.setRestartOffset(localSize);      
              InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"),"iso-8859-1"));   
           try   
           {   
              byte[] bytes = new byte[1024];    
              //总的进度   
              long step = lRemoteSize /100;   
              if(lRemoteSize<100)   
                  step =lRemoteSize;   
              long process=localSize /step;      
              int c;      
                 
              while((c = in.read(bytes))!= -1){   
                  out.write(bytes,0,c);      
                  localSize+=c;      
                  long nowProcess = localSize /step;      
                  if(nowProcess > process){      
                      process = nowProcess;      
                      if(process % 10 == 0)      
                          logger.info(Language.apply("下载进度：")+process);   
                      //TODO 更新文件下载进度,值存放在process变量中      
                  }      
              }   
             }catch(Exception e)   
             {   
                 logger.info(Language.apply("下载文件时：")+e.getMessage());   
             }   
             finally   
             {   
                 if(in!=null)   
                     in.close();   
                 if(out!=null)   
                     out.close();    
             }   
                   
              //确认是否全部下载完毕   
              boolean isDo = ftpClient.completePendingCommand();      
              if(isDo){      
                  result = DownloadStatus.Download_From_Break_Success;      
              }else {      
                  result = DownloadStatus.Download_From_Break_Failed;      
              }      
          }else {      
              OutputStream out = new FileOutputStream(f);      
              InputStream in= ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"),"iso-8859-1"));   
           try   
           {   
              byte[] bytes = new byte[1024];     
              long step = lRemoteSize /100;   
              if(lRemoteSize<100)   
                  step =lRemoteSize;   
              long process=0;      
              long localSize = 0L;      
              int c;      
              while((c = in.read(bytes))!= -1){      
                     
                  out.write(bytes, 0, c);      
                   
                  localSize+=c;      
                     
                  long nowProcess = localSize /step;      
                    
                  if(nowProcess > process){      
                      process = nowProcess;      
                      if(process % 10 == 0)   
                          logger.info(Language.apply("下载进度：")+process);   
                      //TODO 更新文件下载进度,值存放在process变量中      
                  }      
              }    
           }catch(Exception e)   
           {   
               logger.info(Language.apply("下载文件时：")+e.getMessage());   
               e.printStackTrace();   
           }   
           finally   
           {   
             if(in!=null)   
                 in.close();   
             if(out!=null)   
                 out.close();    
           }   
              boolean upNewStatus = ftpClient.completePendingCommand();      
              if(upNewStatus){      
                  result = DownloadStatus.Download_New_Success;      
              }else {      
                  result = DownloadStatus.Download_New_Failed;      
              }      
          }      
          return result;      
      }      
            
      public boolean deleteFile(String remote)
      {
    	  try
		{
			return ftpClient.deleteFile(remote);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
      }
      /**    
       * 上传文件到FTP服务器，支持断点续传    
       * @param local 本地文件名称，绝对路径    
       * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构    
       * @return 上传结果    
       * @throws IOException    
       */     
      public int upload(String local,String remote,boolean brokenTrans ) throws IOException{      
          //设置PassiveMode传输      
          ftpClient.enterLocalPassiveMode();      
          //设置以二进制流的方式传输      
          ftpClient.setFileType(FTP.BINARY_FILE_TYPE);      
          ftpClient.setControlEncoding("GBK");      
          int result;      
          //对远程目录的处理      
          String remoteFileName = remote;      
          if(remote.contains("/")){      
              remoteFileName = remote.substring(remote.lastIndexOf("/")+1);      
              //创建服务器远程目录结构，创建失败直接返回      
              if(CreateDirecroty(remote, ftpClient)==UploadStatus.Create_Directory_Fail){      
                  return UploadStatus.Create_Directory_Fail;      
              }      
          }      
                
          //检查远程是否存在文件      
          FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));      
          if(brokenTrans && files.length == 1){      
              long remoteSize = files[0].getSize();      
              File f = new File(local);      
              long localSize = f.length();      
              if(remoteSize==localSize){      
                  return UploadStatus.File_Exits;      
              }else if(remoteSize > localSize){      
                  return UploadStatus.Remote_Bigger_Local;      
              }      
                    
              //尝试移动文件内读取指针,实现断点续传      
              result = uploadFile(remoteFileName, f, ftpClient, remoteSize);      
                    
              //如果断点续传没有成功，则删除服务器上文件，重新上传      
              if(result == UploadStatus.Upload_From_Break_Failed){      
                  if(!ftpClient.deleteFile(remoteFileName)){      
                      return UploadStatus.Delete_Remote_Faild;      
                  }      
                  result = uploadFile(remoteFileName, f, ftpClient, 0);      
              }      
          }else {      
              result = uploadFile(remoteFileName, new File(local), ftpClient, 0);      
          }      
          return result;      
      }      
      /**    
       * 断开与远程服务器的连接    
       * @throws IOException    
       */     
      public void disconnect() throws IOException{      
          if(ftpClient.isConnected()){    
              logger.info(Language.apply("超时时间设置：")+ftpClient.getSoTimeout());   
              ftpClient.disconnect();      
          }      
      }      
            
      /**    
       * 递归创建远程服务器目录    
       * @param remote 远程服务器文件绝对路径    
       * @param ftpClient FTPClient对象    
       * @return 目录创建是否成功    
       * @throws IOException    
       */     
      public int CreateDirecroty(String remote,FTPClient ftpClient) throws IOException{      
          int status = UploadStatus.Create_Directory_Success;      
          String directory = remote.substring(0,remote.lastIndexOf("/")+1);      
          if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"),"iso-8859-1"))){      
              //如果远程目录不存在，则递归创建远程服务器目录      
              int start=0;      
              int end = 0;      
              if(directory.startsWith("/")){      
                  start = 1;      
              }else{      
                  start = 0;      
              }      
              end = directory.indexOf("/",start);      
              while(true){      
                  String subDirectory = new String(remote.substring(start,end).getBytes("GBK"),"iso-8859-1");      
                  if(!ftpClient.changeWorkingDirectory(subDirectory)){      
                      if(ftpClient.makeDirectory(subDirectory)){      
                          ftpClient.changeWorkingDirectory(subDirectory);      
                      }else {      
                          System.out.println(Language.apply("创建目录失败"));      
                          return UploadStatus.Create_Directory_Fail;      
                      }      
                  }      
                        
                  start = end + 1;      
                  end = directory.indexOf("/",start);      
                        
                  //检查所有目录是否创建完毕      
                  if(end == start){      
                      break;      
                  }      
              }      
          }      
          return status;      
      }      
            
      /**    
       * 上传文件到服务器,新上传和断点续传    
       * @param remoteFile 远程文件名，在上传之前已经将服务器工作目录做了改变    
       * @param localFile 本地文件File句柄，绝对路径    
       * @param processStep 需要显示的处理进度步进值    
       * @param ftpClient FTPClient引用    
       * @return    
       * @throws IOException    
       */     
      public int uploadFile(String remoteFile,File localFile,FTPClient ftpClient,long remoteSize) throws IOException{      
          int status;      
          //显示进度的上传      
          long step = localFile.length() / 100;      
          long process = 0;      
          long localreadbytes = 0L;      
          RandomAccessFile raf = new RandomAccessFile(localFile,"r");      
          OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"),"iso-8859-1"));      
          //断点续传      
          if(remoteSize>0){      
              ftpClient.setRestartOffset(remoteSize);      
              process = remoteSize /step;      
              raf.seek(remoteSize);      
              localreadbytes = remoteSize;      
          }      
          byte[] bytes = new byte[1024];      
          int c;      
          while((c = raf.read(bytes))!= -1){      
              out.write(bytes,0,c);      
              localreadbytes+=c;      
              if(localreadbytes / step != process){      
                  process = localreadbytes / step;      
                  System.out.println(Language.apply("上传进度:") + process);  
                  if (styledText != null )
                  {
                	  styledText.append(remoteFile+"............."+process+"%\n");
                	  styledText.setSelection(styledText.getText().length());
                  }
                  //TODO 汇报上传状态      
              }      
          }      
          out.flush();      
          raf.close();      
          out.close();      
          boolean result =ftpClient.completePendingCommand();      
          if(remoteSize > 0){      
              status = result?UploadStatus.Upload_From_Break_Success:UploadStatus.Upload_From_Break_Failed;      
          }else {      
              status = result?UploadStatus.Upload_New_File_Success:UploadStatus.Upload_New_File_Failed;      
          }      
            
          return status;      
      }      
            
      public static void main(String[] args) {      
          ContinueFTP myFtp = new ContinueFTP();      
          try {      
              myFtp.connect("192.168.1.245", 21, "aircom", "123456");      
  //          myFtp.ftpClient.makeDirectory(new String("电视剧".getBytes("GBK"),"iso-8859-1"));      
  //          myFtp.ftpClient.changeWorkingDirectory(new String("电视剧".getBytes("GBK"),"iso-8859-1"));      
  //          myFtp.ftpClient.makeDirectory(new String("走西口".getBytes("GBK"),"iso-8859-1"));      
  //          System.out.println(myFtp.upload("E:\\yw.flv", "/yw.flv",5));      
  //          System.out.println(myFtp.upload("E:\\走西口24.mp4","/央视走西口/新浪网/走西口24.mp4"));      
              System.out.println(myFtp.download("2.txt", "H:\\sfa.txt"));      
              myFtp.disconnect();      
          } catch (Exception e) {      
                 
              System.out.println(Language.apply("连接FTP出错：")+e.getMessage());      
          }      
      }      
         
 
  }