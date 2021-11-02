package data;

public class PasswordHashImp implements PasswordHash{
    private final String passwordHash;
    private final String passwordSalt;

    public PasswordHashImp(String passwordHash, String passwordSalt) {
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

}
