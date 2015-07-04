package test;

/**
 * Created by IntelliJ IDEA.
 * User: fire
 * Date: 2005-6-7
 */
public class TestPassword {
    public static void main(String[] args) {
        TestPassword tp=new TestPassword();
      // String pass= tp.addPass("1000000000020315","0000000");
        String pass= tp.addPass("0000000115","0000000");
        System.out.println("pass"+pass);
    }

    public String addPass(String cardNO,String password){
       long k;
       int a;
        String s;
        k=123456789;
        for(int i=0;i<cardNO.length();i++){
            a=(int)cardNO.charAt(i)%13+1;
            k=(k * a)% 9999999 + 1;
        }
        k=k%98989898+99;

        for(int i=0;i<password.length();i++){
           a=(int)password.charAt(i)%17+1;
           k=(k%9876543+1)*a;
        }
       s=Long.toString(k+100000000);
       s=s.substring(s.length()-8,(s.length()-8)+6);
       s= s + verify(s);

        return s;
    }
   private char verify(String s){
       int odd=0,env=0;
       int I;
        I=0;
       for(;I<s.length();){
        odd = odd +(int)s.charAt(I) - (int)'0';
        I = I + 2;
       }
        I=1;
       for(;I<s.length();){
           env = env + (int)s.charAt(I) -(int) '0';
        I = I + 2;
       }
       I = (env + odd * 3)%10;
       return (char)(I + (int)'0');
   }
}
