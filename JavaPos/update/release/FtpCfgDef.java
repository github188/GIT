package update.release;

public class FtpCfgDef 
{
    public String FtpIP = "";
    public int FtpPort = 21;
    public String FtpPath = "";
    public String FtpUser = "anonymous";
    public String FtpPwd = "";
    public int FtpTimeout = 120000;
    public int FtpDefaultTimeout = 120000;
    public int FtpDataTimeout = 120000;
    public String Ftppasv = "N";
    public boolean isanonymous = false;
    public int Status = 1;//(0-成功,1-未上传,2-正在上传,3-失败)
    public String Message = "";
}
