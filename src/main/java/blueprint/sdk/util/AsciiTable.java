/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

/**
 * ASCII Code Table
 *
 * @author lempel@gmail.com
 * @Since 2015-12-21
 */
public class AsciiTable {
    /**
     * ASCII Code : NULL
     */
    public static final byte NULL = (byte) 0x00;
    /**
     * ASCII Code : Start of heading
     */
    public static final byte SOH = (byte) 0x01;
    /**
     * ASCII Code : Start of text
     */
    public static final byte STX = (byte) 0x02;
    /**
     * ASCII Code : End pf text
     */
    public static final byte ETX = (byte) 0x03;
    /**
     * ASCII Code : End of transmission
     */
    public static final byte EOT = (byte) 0x04;
    /**
     * ASCII Code : Enquiry
     */
    public static final byte ENQ = (byte) 0x05;
    /**
     * ASCII Code : Acknowledge
     */
    public static final byte ACK = (byte) 0x06;
    /**
     * ASCII Code : Bell
     */
    public static final byte BELL = (byte) 0x07;
    /**
     * ASCII Code : Backspace
     */
    public static final byte BS = (byte) 0x08;
    /**
     * ASCII Code : Horizontal tab
     */
    public static final byte TAB = (byte) 0x09;
    /**
     * ASCII Code : NL line feed, new line
     */
    public static final byte LF = (byte) 0x0a;
    /**
     * ASCII Code : Vertical tab
     */
    public static final byte VT = (byte) 0x0b;
    /**
     * ASCII Code : NP form feed, new page
     */
    public static final byte FF = (byte) 0x0c;
    /**
     * ASCII Code : Carriage return
     */
    public static final byte CR = (byte) 0x0d;
    /**
     * ASCII Code : Shift out
     */
    public static final byte SO = (byte) 0x0e;
    /**
     * ASCII Code : Shift in
     */
    public static final byte SI = (byte) 0x0f;
    /**
     * ASCII Code : Data line esacpe
     */
    public static final byte DLE = (byte) 0x10;
    /**
     * ASCII Code : Device control 1
     */
    public static final byte DC1 = (byte) 0x11;
    /**
     * ASCII Code : Device control 2
     */
    public static final byte DC2 = (byte) 0x12;
    /**
     * ASCII Code : Device control 3
     */
    public static final byte DC3 = (byte) 0x13;
    /**
     * ASCII Code : Device control 4
     */
    public static final byte DC4 = (byte) 0x14;
    /**
     * ASCII Code : Negative acknowledge
     */
    public static final byte NAK = (byte) 0x15;
    /**
     * ASCII Code : Synchronous idle
     */
    public static final byte SYN = (byte) 0x16;
    /**
     * ASCII Code : End of transmission block
     */
    public static final byte ETB = (byte) 0x17;
    /**
     * ASCII Code : Cancel
     */
    public static final byte CAN = (byte) 0x18;
    /**
     * ASCII Code : End of medium
     */
    public static final byte EM = (byte) 0x19;
    /**
     * ASCII Code : Substitute
     */
    public static final byte SUB = (byte) 0x1a;
    /**
     * ASCII Code : Escape
     */
    public static final byte ESC = (byte) 0x1b;
    /**
     * ASCII Code : File Separator
     */
    public static final byte FS = (byte) 0x1c;
    /**
     * ASCII Code : Group Separator
     */
    public static final byte GS = (byte) 0x1d;
    /**
     * ASCII Code : Record Separator
     */
    public static final byte RS = (byte) 0x1e;
    /**
     * ASCII Code : Unit separator
     */
    public static final byte US = (byte) 0x1f;
    /**
     * ASCII Code : ' '
     */
    public static final byte SPACE = (byte) 0x20;
    /**
     * ASCII Code : '!'
     */
    public static final byte EXCLAMATION = (byte) 0x21;
    /**
     * ASCII Code : '"'
     */
    public static final byte DOUBLE_QUOTE = (byte) 0x22;
    /**
     * ASCII Code : '#'
     */
    public static final byte SHARP = (byte) 0x23;
    /**
     * ASCII Code : '$'
     */
    public static final byte DOLLAR = (byte) 0x24;
    /**
     * ASCII Code : '%'
     */
    public static final byte PERCENT = (byte) 0x25;
    /**
     * ASCII Code : '&'
     */
    public static final byte AND = (byte) 0x26;
    /**
     * ASCII Code : '''
     */
    public static final byte QUOTE = (byte) 0x27;
    /**
     * ASCII Code : '('
     */
    public static final byte L_PAREN = (byte) 0x28;
    /**
     * ASCII Code : ')'
     */
    public static final byte R_PAREN = (byte) 0x29;
    /**
     * ASCII Code : '*'
     */
    public static final byte MULTIPLY = (byte) 0x2a;
    /**
     * ASCII Code : '+'
     */
    public static final byte PLUS = (byte) 0x2b;
    /**
     * ASCII Code : ','
     */
    public static final byte COMMA = (byte) 0x2c;
    /**
     * ASCII Code : '-'
     */
    public static final byte DASH = (byte) 0x2d;
    /**
     * ASCII Code : '.'
     */
    public static final byte DOT = (byte) 0x2e;
    /**
     * ASCII Code : '/'
     */
    public static final byte SLASH = (byte) 0x2f;
    /**
     * ASCII Code : '0'
     */
    public static final byte NUM_0 = (byte) 0x30;
    /**
     * ASCII Code : '1'
     */
    public static final byte NUM_1 = (byte) 0x31;
    /**
     * ASCII Code : '2'
     */
    public static final byte NUM_2 = (byte) 0x32;
    /**
     * ASCII Code : '3'
     */
    public static final byte NUM_3 = (byte) 0x33;
    /**
     * ASCII Code : '4'
     */
    public static final byte NUM_4 = (byte) 0x34;
    /**
     * ASCII Code : '5'
     */
    public static final byte NUM_5 = (byte) 0x35;
    /**
     * ASCII Code : '6'
     */
    public static final byte NUM_6 = (byte) 0x36;
    /**
     * ASCII Code : '7'
     */
    public static final byte NUM_7 = (byte) 0x37;
    /**
     * ASCII Code : '8'
     */
    public static final byte NUM_8 = (byte) 0x38;
    /**
     * ASCII Code : '9'
     */
    public static final byte NUM_9 = (byte) 0x39;
    /**
     * ASCII Code : ':'
     */
    public static final byte COLON = (byte) 0x3a;
    /**
     * ASCII Code : ';'
     */
    public static final byte SEMI_COLON = (byte) 0x3b;
    /**
     * ASCII Code : '<'
     */
    public static final byte LESS_THAN = (byte) 0x3c;
    /**
     * ASCII Code : '='
     */
    public static final byte EQUAL = (byte) 0x3d;
    /**
     * ASCII Code : '>'
     */
    public static final byte GREATER_THAN = (byte) 0x3e;
    /**
     * ASCII Code : '?'
     */
    public static final byte QUESTION = (byte) 0x3f;
    /**
     * ASCII Code : '@'
     */
    public static final byte AT = (byte) 0x40;
    /**
     * ASCII Code : 'A'
     */
    public static final byte A = (byte) 0x41;
    /**
     * ASCII Code : 'B'
     */
    public static final byte B = (byte) 0x42;
    /**
     * ASCII Code : 'C'
     */
    public static final byte C = (byte) 0x43;
    /**
     * ASCII Code : 'D'
     */
    public static final byte D = (byte) 0x44;
    /**
     * ASCII Code : 'E'
     */
    public static final byte E = (byte) 0x45;
    /**
     * ASCII Code : 'F'
     */
    public static final byte F = (byte) 0x46;
    /**
     * ASCII Code : 'G'
     */
    public static final byte G = (byte) 0x47;
    /**
     * ASCII Code : 'H'
     */
    public static final byte H = (byte) 0x48;
    /**
     * ASCII Code : 'I'
     */
    public static final byte I = (byte) 0x49;
    /**
     * ASCII Code : 'J'
     */
    public static final byte J = (byte) 0x4a;
    /**
     * ASCII Code : 'K'
     */
    public static final byte K = (byte) 0x4b;
    /**
     * ASCII Code : 'L'
     */
    public static final byte L = (byte) 0x4c;
    /**
     * ASCII Code : 'M'
     */
    public static final byte M = (byte) 0x4d;
    /**
     * ASCII Code : 'N'
     */
    public static final byte N = (byte) 0x4e;
    /**
     * ASCII Code : 'O'
     */
    public static final byte O = (byte) 0x4f;
    /**
     * ASCII Code : 'P'
     */
    public static final byte P = (byte) 0x50;
    /**
     * ASCII Code : 'Q'
     */
    public static final byte Q = (byte) 0x51;
    /**
     * ASCII Code : 'R'
     */
    public static final byte R = (byte) 0x52;
    /**
     * ASCII Code : 'S'
     */
    public static final byte S = (byte) 0x53;
    /**
     * ASCII Code : 'T'
     */
    public static final byte T = (byte) 0x54;
    /**
     * ASCII Code : 'U'
     */
    public static final byte U = (byte) 0x55;
    /**
     * ASCII Code : 'V'
     */
    public static final byte V = (byte) 0x56;
    /**
     * ASCII Code : 'W'
     */
    public static final byte W = (byte) 0x57;
    /**
     * ASCII Code : 'X'
     */
    public static final byte X = (byte) 0x58;
    /**
     * ASCII Code : 'Y'
     */
    public static final byte Y = (byte) 0x59;
    /**
     * ASCII Code : 'Z'
     */
    public static final byte Z = (byte) 0x5a;
    /**
     * ASCII Code : '['
     */
    public static final byte L_BRACE = (byte) 0x5b;
    /**
     * ASCII Code : '\'
     */
    public static final byte BACK_SLASH = (byte) 0x5c;
    /**
     * ASCII Code : ']'
     */
    public static final byte R_BRACE = (byte) 0x5d;
    /**
     * ASCII Code : '^'
     */
    public static final byte SQRT = (byte) 0x5e;
    /**
     * ASCII Code : '_'
     */
    public static final byte UNDER_SCORE = (byte) 0x5f;
    /**
     * ASCII Code : '`'
     */
    public static final byte BACKQUOTE = (byte) 0x60;
    /**
     * ASCII Code : 'a'
     */
    public static final byte a = (byte) 0x61;
    /**
     * ASCII Code : 'b'
     */
    public static final byte b = (byte) 0x62;
    /**
     * ASCII Code : 'c'
     */
    public static final byte c = (byte) 0x63;
    /**
     * ASCII Code : 'd'
     */
    public static final byte d = (byte) 0x64;
    /**
     * ASCII Code : 'e'
     */
    public static final byte e = (byte) 0x65;
    /**
     * ASCII Code : 'f'
     */
    public static final byte f = (byte) 0x66;
    /**
     * ASCII Code : 'g'
     */
    public static final byte g = (byte) 0x67;
    /**
     * ASCII Code : 'h'
     */
    public static final byte h = (byte) 0x68;
    /**
     * ASCII Code : 'i'
     */
    public static final byte i = (byte) 0x69;
    /**
     * ASCII Code : 'j'
     */
    public static final byte j = (byte) 0x6a;
    /**
     * ASCII Code : 'k'
     */
    public static final byte k = (byte) 0x6b;
    /**
     * ASCII Code : 'l'
     */
    public static final byte l = (byte) 0x6c;
    /**
     * ASCII Code : 'm'
     */
    public static final byte m = (byte) 0x6d;
    /**
     * ASCII Code : 'n'
     */
    public static final byte n = (byte) 0x6e;
    /**
     * ASCII Code : 'o'
     */
    public static final byte o = (byte) 0x6f;
    /**
     * ASCII Code : 'p'
     */
    public static final byte p = (byte) 0x70;
    /**
     * ASCII Code : 'q'
     */
    public static final byte q = (byte) 0x71;
    /**
     * ASCII Code : 'r'
     */
    public static final byte r = (byte) 0x72;
    /**
     * ASCII Code : 's'
     */
    public static final byte s = (byte) 0x73;
    /**
     * ASCII Code : 't'
     */
    public static final byte t = (byte) 0x74;
    /**
     * ASCII Code : 'u'
     */
    public static final byte u = (byte) 0x75;
    /**
     * ASCII Code : 'v'
     */
    public static final byte v = (byte) 0x76;
    /**
     * ASCII Code : 'w'
     */
    public static final byte w = (byte) 0x77;
    /**
     * ASCII Code : 'x'
     */
    public static final byte x = (byte) 0x78;
    /**
     * ASCII Code : 'y'
     */
    public static final byte y = (byte) 0x79;
    /**
     * ASCII Code : 'z'
     */
    public static final byte z = (byte) 0x7a;
    /**
     * ASCII Code : '{'
     */
    public static final byte L_CURLY_BRACE = (byte) 0x7b;
    /**
     * ASCII Code : '|'
     */
    public static final byte PIPE = (byte) 0x7c;
    /**
     * ASCII Code : '}'
     */
    public static final byte R_CURLY_BRACE = (byte) 0x7d;
    /**
     * ASCII Code : '~'
     */
    public static final byte TILT = (byte) 0x7e;
    /**
     * ASCII Code : DEL
     */
    public static final byte DEL = (byte) 0x7f;
}
