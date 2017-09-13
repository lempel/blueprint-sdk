package blueprint.sdk.util;

/**
 * Radix 62 Integer
 *
 * @author lempel@gmail.com
 * @since 2017-09-06
 */
public class Radix62Integer {
    private static byte[] digits = new byte[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z'
    };

    /**
     * Convert given integer to radix 62 integer
     *
     * @param value target integer
     * @return radix 62 integer as String
     */
    public static String toRadix62(int value) {
        byte[] result = new byte[6];

        long current = value & 0x00000000ffffffffL;
        for (int i = 5; i >= 0; i--) {
            int m = (int) (current % 62L);
            current = current / 62L;
            result[i] = digits[m];

        }

        int start = 0;
        for (int i = 0; i <= 5; i++) {
            if (result[i] != digits[0]) {
                start = i;
                break;
            }
        }

        return new String(result, start, result.length - start);
    }

    public static byte[] fromRadix62(String value) {
        byte[] result = new byte[16];

        // TODO implement
        throw new RuntimeException("Not implemented yet");
    }
}
