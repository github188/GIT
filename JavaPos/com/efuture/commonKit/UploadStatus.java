package com.efuture.commonKit;

public class UploadStatus {   
    public static int Create_Directory_Fail = 7   ;   //远程服务器相应目录创建失败   
    public static int Create_Directory_Success = 8;   //远程服务器闯将目录成功   
    public static int Upload_New_File_Success = 9;    //上传新文件成功   
    public static int Upload_New_File_Failed =10;     //上传新文件失败   
    public static int File_Exits =11;                 //文件已经存在   
    public static int Remote_Bigger_Local =12;        //远程文件大于本地文件   
    public static int Upload_From_Break_Success = 13;  //断点续传成功   
    public static int Upload_From_Break_Failed = 14;   //断点续传失败   
    public static int Delete_Remote_Faild = 15;        //删除远程文件失败   
}  