/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.util.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;

/**
 * Prevent NullPointerException from ResultSet.<br>
 * (getString, getBytes)<br>
 *
 * @author lempel@gmail.com
 * @since 2009. 2. 26.
 */
@SuppressWarnings("WeakerAccess")
public class ResultSetHelper {
    private final transient ResultSet rset;

    public ResultSetHelper(final ResultSet rset) {
        this.rset = rset;
    }

    public boolean next() throws SQLException {
        return rset.next();
    }

    public void close() throws SQLException {
        if (rset != null) {
            rset.close();
        }
    }

    public void wasNull() throws SQLException {
        rset.wasNull();
    }

    public String getString(final int index) throws SQLException {
        String result = rset.getString(index);
        if (result == null) {
            result = "";
        }
        return result.trim();
    }

    public boolean getBoolean(final int index) throws SQLException {
        return rset.getBoolean(index);
    }

    public byte getByte(final int index) throws SQLException {
        return rset.getByte(index);
    }

    public int getInt(final int index) throws SQLException {
        return rset.getInt(index);
    }

    public long getLong(final int index) throws SQLException {
        return rset.getLong(index);
    }

    public float getFloat(final int index) throws SQLException {
        return rset.getFloat(index);
    }

    public double getDouble(final int index) throws SQLException {
        return rset.getDouble(index);
    }

    public BigDecimal getBigDecimal(final int index) throws SQLException {
        return rset.getBigDecimal(index);
    }

    public byte[] getBytes(final int index) throws SQLException {
        byte[] result = rset.getBytes(index);
        if (result == null) {
            result = new byte[0];
        }
        return result;
    }

    public Date getDate(final int index) throws SQLException {
        return rset.getDate(index);
    }

    public Time getTime(final int index) throws SQLException {
        return rset.getTime(index);
    }

    public Timestamp getTimestamp(final int index) throws SQLException {
        return rset.getTimestamp(index);
    }

    public InputStream getAsciiStream(final int index) throws SQLException {
        return rset.getAsciiStream(index);
    }

    public InputStream getBinaryStream(final int index) throws SQLException {
        return rset.getBinaryStream(index);
    }

    public String getString(final String index) throws SQLException {
        String result = rset.getString(index);
        if (result == null) {
            result = "";
        }
        return result.trim();
    }

    public boolean getBoolean(final String index) throws SQLException {
        return rset.getBoolean(index);
    }

    public byte getByte(final String index) throws SQLException {
        return rset.getByte(index);
    }

    public int getInt(final String index) throws SQLException {
        return rset.getInt(index);
    }

    public long getLong(final String index) throws SQLException {
        return rset.getLong(index);
    }

    public float getFloat(final String index) throws SQLException {
        return rset.getFloat(index);
    }

    public double getDouble(final String index) throws SQLException {
        return rset.getDouble(index);
    }

    public BigDecimal getBigDecimal(final String index) throws SQLException {
        return rset.getBigDecimal(index);
    }

    public byte[] getBytes(final String index) throws SQLException {
        byte[] result = rset.getBytes(index);
        if (result == null) {
            result = new byte[0];
        }
        return result;
    }

    public Date getDate(final String index) throws SQLException {
        return rset.getDate(index);
    }

    public Time getTime(final String index) throws SQLException {
        return rset.getTime(index);
    }

    public Timestamp getTimestamp(final String index) throws SQLException {
        return rset.getTimestamp(index);
    }

    public InputStream getAsciiStream(final String index) throws SQLException {
        return rset.getAsciiStream(index);
    }

    public InputStream getBinaryStream(final String index) throws SQLException {
        return rset.getBinaryStream(index);
    }

    public SQLWarning getWarnings() throws SQLException {
        return rset.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        rset.clearWarnings();
    }

    public String getCursorName() throws SQLException {
        return rset.getCursorName();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return rset.getMetaData();
    }

    public Object getObject(final int index) throws SQLException {
        return rset.getObject(index);
    }

    public Object getObject(final String index) throws SQLException {
        return rset.getObject(index);
    }

    public int findColumn(final String index) throws SQLException {
        return rset.findColumn(index);
    }

    public Reader getCharacterStream(final int index) throws SQLException {
        return rset.getCharacterStream(index);
    }

    public Reader getCharacterStream(final String index) throws SQLException {
        return rset.getCharacterStream(index);
    }

    public boolean first() throws SQLException {
        return rset.first();
    }

    public boolean last() throws SQLException {
        return rset.last();
    }

    public int getRow() throws SQLException {
        return rset.getRow();
    }

    public boolean previous() throws SQLException {
        return rset.previous();
    }

    public int getFetchDirection() throws SQLException {
        return rset.getFetchDirection();
    }

    public void setFetchDirection(final int dir) throws SQLException {
        rset.setFetchDirection(dir);
    }

    public int getFetchSize() throws SQLException {
        return rset.getFetchSize();
    }

    public void setFetchSize(final int dir) throws SQLException {
        rset.setFetchSize(dir);
    }

    public Statement getStatement() throws SQLException {
        return rset.getStatement();
    }

    public Blob getBlob(final int index) throws SQLException {
        return rset.getBlob(index);
    }

    public Clob getClob(final int index) throws SQLException {
        return rset.getClob(index);
    }

    public Blob getBlob(final String index) throws SQLException {
        return rset.getBlob(index);
    }

    public Clob getClob(final String index) throws SQLException {
        return rset.getClob(index);
    }
}