import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAUtil {
    public BigInteger n, d, e;

    private int bitlen = 1024;

    public RSAUtil() {
        SecureRandom r = new SecureRandom();
        BigInteger p = new BigInteger(bitlen, 100, r);
        BigInteger q = new BigInteger(bitlen, 100, r);
        n = p.multiply(q);
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("65537"); // Common prime
        d = e.modInverse(m);
    }

    public RSAUtil(BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
    }

    public synchronized BigInteger encrypt(String message) {
        return new BigInteger(message.getBytes()).modPow(e, n);
    }

    public synchronized String decrypt(BigInteger encrypted) {
        return new String(encrypted.modPow(d, n).toByteArray());
    }
}
