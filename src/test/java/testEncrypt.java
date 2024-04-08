
import org.guanzon.appdriver.base.MySQLAESCrypt;

public class testEncrypt {
    public static void main (String [] args){
        System.out.println(MySQLAESCrypt.Encrypt("sa", "08220326"));
        System.out.println(MySQLAESCrypt.Encrypt("Atsp,imrtptd", "08220326"));
    }
}
