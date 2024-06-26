
import org.guanzon.appdriver.base.MySQLAESCrypt;

public class testEncrypt {
    public static void main (String [] args){
        System.out.println(MySQLAESCrypt.Encrypt("sysadmin", "08220326"));
//        System.out.println(MySQLAESCrypt.Encrypt("michael07", "08220326"));
        
        System.out.println("SysAdmin : " + MySQLAESCrypt.Decrypt("91BAAE7C67F5C5C5D90E21451F664EF4", "08220326"));
        System.out.println("NetWare  : " + MySQLAESCrypt.Decrypt("91BAAE7C67F5C5C5D90E21451F664EF4", "08220326"));
        System.out.println("Machine  : " + MySQLAESCrypt.Decrypt("DBA6A83E015307C1095EB022A4217AF8", "08220326"));
    }
}
