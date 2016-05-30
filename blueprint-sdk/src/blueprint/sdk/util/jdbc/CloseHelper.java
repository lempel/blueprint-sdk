/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Closes JDBC resources.
 *
 * @author lempel@gmail.com
 * @since 2013. 8. 27.
 */
public class CloseHelper {
    /**
     * Closes given JDBC Resource
     *
     * @param con target to close
     */
    public static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Closes given JDBC Resource
     *
     * @param stmt target to close
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Closes given JDBC Resource
     *
     * @param rset target to close
     */
    @SuppressWarnings("WeakerAccess")
    public static void close(ResultSet rset) {
        if (rset != null) {
            try {
                rset.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Closes given JDBC Resource
     *
     * @param rset target to close
     */
    @SuppressWarnings("WeakerAccess")
    public static void close(ResultSetHelper rset) {
        if (rset != null) {
            try {
                rset.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Closes given JDBC Resources
     *
     * @param con target to close
     * @param stmt target to close
     */
    public static void close(Connection con, Statement stmt) {
        close(stmt);
        close(con);
    }

    /**
     * Closes given JDBC Resources
     *
     * @param stmt target to close
     * @param rset target to close
     */
    public static void close(Statement stmt, ResultSet rset) {
        close(rset);
        close(stmt);
    }

    /**
     * Closes given JDBC Resources
     *
     * @param stmt target to close
     * @param rset target to close
     */
    public static void close(Statement stmt, ResultSetHelper rset) {
        close(rset);
        close(stmt);
    }

    /**
     * Closes given JDBC Resources
     *
     * @param con target to close
     * @param stmt target to close
     * @param rset target to close
     */
    public static void close(Connection con, Statement stmt, ResultSet rset) {
        close(rset);
        close(stmt);
        close(con);
    }

    /**
     * Closes given JDBC Resources
     *
     * @param con target to close
     * @param stmt target to close
     * @param rset target to close
     */
    public static void close(Connection con, Statement stmt, ResultSetHelper rset) {
        close(rset);
        close(stmt);
        close(con);
    }
}
