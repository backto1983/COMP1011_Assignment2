package comp1011_assigment2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This is the PasswordGenerator class; it defines methods that allow password hashing
 * @author Henrique
 */
public class PasswordGenerator {

    /**
     * This method will hash a password with a given salt and return it as a String
     * @param passwordToEncrypt
     * @param salt
     * @return String generatedPassword
     */
    public static String getSHA512Pwd(String passwordToEncrypt, byte[] salt) {
        String generatedPassword = null;
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            
            byte[] bytes = md.digest(passwordToEncrypt.getBytes());
            
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100,16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch(NoSuchAlgorithmException e) {
            System.err.println(e);
        }
        return generatedPassword;
    }
    
    /**
     * This method will create a random salt consisting of 16 bytes
     * @return 
     * @throws java.security.NoSuchAlgorithmException
     */
    public static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}
