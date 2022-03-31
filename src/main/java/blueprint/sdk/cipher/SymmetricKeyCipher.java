/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.cipher;

import blueprint.sdk.core.concurrent.lock.Mutex;
import blueprint.sdk.util.StringUtil;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * Symmetric key Cipher
 *
 * @author lempel@gmail.com
 * @since 2022-03-31
 */
public class SymmetricKeyCipher {
    public static final String AES_ECP_PKCS5 = "AES/ECB/PKCS5Padding";

    private final Cipher cipher;
    private final Mutex mutex;
    private final Base64.Encoder b64enc;
    private final Base64.Decoder b64dec;

    public SymmetricKeyCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        this(AES_ECP_PKCS5);
    }

    public SymmetricKeyCipher(String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance(algorithm);
        mutex = new Mutex();
        b64enc = Base64.getEncoder();
        b64dec = Base64.getDecoder();
    }

    public SecretKey toSecretKey(String key) {
        SecretKey ret;
        String algorithm = cipher.getAlgorithm();

        switch (algorithm) {
            case AES_ECP_PKCS5:
                System.out.println("key length = " + key.length());
                String paddedKey = StringUtil.lpadSpace(key, 32); // max 32
                ret = new SecretKeySpec(paddedKey.getBytes(StandardCharsets.UTF_8), "AES");
                break;
            default:
                throw new IllegalArgumentException("unexpected algorithm - " + algorithm);
        }

        return ret;
    }

    /**
     * Encrypt input with given key
     *
     * @param key   symmetric key
     * @param input input
     * @return encrypted Base64 String
     * @throws IOException encryption failure
     */
    public String encrypt(String key, String input) throws IOException {
        mutex.lock();
        try {
            byte[] b64 = b64enc.encode(doFinal(key, Cipher.ENCRYPT_MODE, input.getBytes(StandardCharsets.UTF_8)));
            return new String(b64);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IOException("encryption failure", e);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Decrypt input with given key
     *
     * @param key   symmetric key
     * @param input encrypted Base64 String
     * @return decrypted String
     * @throws IOException decryption failure
     */
    public String decrypt(String key, String input) throws IOException {
        mutex.lock();
        try {
            return new String(doFinal(key, Cipher.DECRYPT_MODE, b64dec.decode(input)));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IOException("decryption failure", e);
        } finally {
            mutex.unlock();
        }
    }

    private byte[] doFinal(String key, int cipherMode, byte[] input)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(cipherMode, toSecretKey(key));

        return cipher.doFinal(input);
    }
}
