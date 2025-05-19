import java.math.BigInteger;
import java.security.SecureRandom;

public class User {
    private String username;
    private String password;

    public BigInteger publicKey;
    public BigInteger privateKey;
    public BigInteger modulus;

    public RSAUtil rsa; // ✨ ADD THIS

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        generateRSAKeys();
    }

    private void generateRSAKeys() {
        SecureRandom random = new SecureRandom();
        int bitLength = 512;
        BigInteger p = BigInteger.probablePrime(bitLength, random);
        BigInteger q = BigInteger.probablePrime(bitLength, random);
        modulus = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        publicKey = new BigInteger("65537");
        privateKey = publicKey.modInverse(phi);

        // ✨ Initialize the RSAUtil object
        rsa = new RSAUtil(publicKey, privateKey, modulus);
    }

    public boolean checkPassword(String pwd) {
        return this.password.equals(pwd);
    }

    public String getUsername() {
        return username;
    }
}
