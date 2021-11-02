package util;

import data.PasswordHash;
import data.PasswordHashImp;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityProvider {
    public static final Pattern p = Pattern.compile("Basic (.*)");

    public static String createBasicAuthToken(String username, String password) {
        return "Basic " + Helpers.toBase64(username + ":" + password);
    }

    public static String[] parseFromBasicAuthToken(String token) {
        Matcher matcher = p.matcher(token);
        if (!matcher.find()) {
            return null;
        }
        String base64Token = matcher.group(1);

        String[] splited = Helpers.fromBase64(base64Token).split(":");
        if (splited.length != 2) {
            return null;
        }
        return splited;
    }


    public static final int SALT_SIZE = 16;
    public static final int ITERATION_COUNT = 65536;
    public static final int KEY_LENGTH = 128;
    public static final String HASHING_ALGORITHM = "PBKDF2WithHmacSHA1";

    public static PasswordHash generatePasswordHash(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_SIZE];
        random.nextBytes(salt);
        return generatePasswordWithSalt(password,salt);
    }

    public static boolean isValid(String password, String passwordHashBase64, String saltBase64) {
        byte[] salt = Helpers.fromBase64ToBytes(saltBase64);
        PasswordHash pwd = generatePasswordWithSalt(password,salt);
        return pwd.getPasswordHash().equals(passwordHashBase64);
    }

    public static boolean isValid(String password, PasswordHash hash) {
        return isValid(password,hash.getPasswordHash(),hash.getPasswordSalt());
    }

    private static PasswordHash generatePasswordWithSalt(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASHING_ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return new PasswordHashImp(Helpers.toBase64(hash),Helpers.toBase64(salt));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}