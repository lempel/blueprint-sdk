/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.id;

import blueprint.sdk.util.Radix62Integer;

import java.security.SecureRandom;

/**
 * ID Generator
 *
 * @author lempel@gmail.com
 * @since 2017-09-06
 */
public class IdGenerator extends Radix62Integer {
    private static SecureRandom numberGenerator;

    public static String generateId(String prefix, String postfix) {
        return cleanseSuffix(prefix) + '_' + randomBase64UUID() + '_' + cleanseSuffix(postfix);
    }

    private static String cleanseSuffix(String suffix) {
        if (suffix == null) {
            return "null";
        } else if (suffix.isEmpty()) {
            return "empty";
        } else {
            return suffix.replaceAll("\\s", "_").replaceAll("/", "_");
        }
    }

    /**
     * Generate a base64 encoded UUID
     *
     * @return Base64 encoded UUID
     */
    public static String randomBase64UUID() {
        SecureRandom ng = numberGenerator;
        if (ng == null) {
            numberGenerator = ng = new SecureRandom();
        }

        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6] &= 0x0f;  /* clear version        */
        randomBytes[6] |= 0x40;  /* set to version 4     */
        randomBytes[8] &= 0x3f;  /* clear variant        */
        randomBytes[8] |= 0x80;  /* set to IETF variant  */

        int i1 = ((randomBytes[0] << 24) & 0xff000000) + ((randomBytes[1] << 16) & 0x00ff0000)
                + ((randomBytes[2] << 8) & 0x0000ff00) + (randomBytes[3] & 0x000000ff);
        int i2 = ((randomBytes[4] << 24) & 0xff000000) + ((randomBytes[5] << 16) & 0x00ff0000)
                + ((randomBytes[6] << 8) & 0x0000ff00) + (randomBytes[7] & 0x000000ff);
        int i3 = ((randomBytes[8] << 24) & 0xff000000) + ((randomBytes[9] << 16) & 0x00ff0000)
                + ((randomBytes[10] << 8) & 0x0000ff00) + (randomBytes[11] & 0x000000ff);
        int i4 = ((randomBytes[12] << 24) & 0xff000000) + ((randomBytes[13] << 16) & 0x00ff0000)
                + ((randomBytes[14] << 8) & 0x0000ff00) + (randomBytes[15] & 0x000000ff);

        return toRadix62(i1) + toRadix62(i2) + toRadix62(i3) + toRadix62(i4);
    }
}
