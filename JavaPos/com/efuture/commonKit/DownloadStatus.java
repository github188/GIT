package com.efuture.commonKit;

public class DownloadStatus {   
    public static int Remote_File_Noexist =1;    //远程文件不存在   
    public static int Local_Bigger_Remote =2;    //本地文件大于远程文件   
    public static int Download_From_Break_Success=3;    //断点下载文件成功   
    public static int Download_From_Break_Failed = 4;     //断点下载文件失败   
    public static int Download_New_Success = 5;           //全新下载文件成功   
    public static int Download_New_Failed = 6;            //全新下载文件失败   
} 