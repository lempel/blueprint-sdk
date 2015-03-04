/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Character Set related methods for KSC5601 (EUC-KR) & ISO-8859_1 (Unicode)
 *
 * @author Sangmin Lee
 * @since 2002. 07. 30
 */
public class CharsetUtil {
    private static final String KO_KR = "ko";
    private static final String ENC_KSC5601 = "KSC5601";
    private static final String ENC_8859_1 = "8859_1";
    /**
     * System Language
     */
    private static final String systemLang;

    static {
        systemLang = Locale.getDefault().getLanguage();
        defaultEncoding = new OutputStreamWriter(System.out).getEncoding();
    }

    /**
     * System.out's encoding type
     */
    private static final String defaultEncoding;

    /**
     * 8859_1 -> KSC5601 (Unicode to EUC-KR)
     *
     * @param target String to convert
     * @return transcoded String
     * @throws UnsupportedEncodingException
     */
    public static String from8859to5601(final String target) throws UnsupportedEncodingException {
        return new String(target.getBytes(ENC_8859_1), ENC_KSC5601);
    }

    /**
     * KSC5601 -> 8859_1 (EUC-KR to Unicode)
     *
     * @param target String to convert
     * @return transcoded String
     * @throws UnsupportedEncodingException
     */
    public static String from5601to8859(final String target) throws UnsupportedEncodingException {
        return new String(target.getBytes(ENC_KSC5601), ENC_8859_1);
    }

    public static String to8859(final String src) {
        String res;
        try {
            if (KO_KR.equals(systemLang)) {
                res = new String(src.getBytes(), ENC_8859_1);
            } else {
                res = src;
            }
        } catch (UnsupportedEncodingException exUE) {
            res = src;
        }

        return res;
    }

    public static String to5601(final String src) {
        String res;
        try {
            if (KO_KR.equals(systemLang)) {
                res = src;
            } else {
                res = new String(src.getBytes(), ENC_KSC5601);
            }
        } catch (UnsupportedEncodingException exUE) {
            res = src;
        }

        return res;
    }

    public static String from8859(final String src) {
        String res;
        try {
            if (KO_KR.equals(systemLang)) {
                res = new String(src.getBytes(ENC_8859_1));
            } else {
                res = src;
            }
        } catch (UnsupportedEncodingException exUE) {
            res = src;
        }

        return res;
    }

    public static String from5601(final String src) {
        String res;
        try {
            if (KO_KR.equals(systemLang)) {
                res = src;
            } else {
                res = new String(src.getBytes(ENC_KSC5601));
            }
        } catch (UnsupportedEncodingException exUE) {
            res = src;
        }

        return res;
    }

    public static String getDefaultEncoding() {
        return defaultEncoding;
    }
}