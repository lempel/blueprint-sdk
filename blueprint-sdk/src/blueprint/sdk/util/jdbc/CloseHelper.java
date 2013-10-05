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
 * @author Sangmin Lee
 * @since 2013. 8. 27.
 */
public class CloseHelper {
	/**
	 * Closes given JDBC Resource
	 * 
	 * @param con
	 */
	public static void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ignored) { // NOPMD by Sangmin Lee
			}
		}
	}

	/**
	 * Closes given JDBC Resource
	 * 
	 * @param stmt
	 */
	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ignored) { // NOPMD by Sangmin Lee
			}
		}
	}

	/**
	 * Closes given JDBC Resource
	 * 
	 * @param rset
	 */
	public static void close(ResultSet rset) {
		if (rset != null) {
			try {
				rset.close();
			} catch (SQLException ignored) { // NOPMD by Sangmin Lee
			}
		}
	}

	/**
	 * Closes given JDBC Resource
	 * 
	 * @param rset
	 */
	public static void close(ResultSetHelper rset) {
		if (rset != null) {
			try {
				rset.close();
			} catch (SQLException ignored) { // NOPMD by Sangmin Lee
			}
		}
	}

	/**
	 * Closes given JDBC Resources
	 * 
	 * @param con
	 * @param stmt
	 */
	public static void close(Connection con, Statement stmt) {
		close(stmt);
		close(con);
	}

	/**
	 * Closes given JDBC Resources
	 * 
	 * @param stmt
	 * @param rset
	 */
	public static void close(Statement stmt, ResultSet rset) {
		close(rset);
		close(stmt);
	}

	/**
	 * Closes given JDBC Resources
	 * 
	 * @param stmt
	 * @param rset
	 */
	public static void close(Statement stmt, ResultSetHelper rset) {
		close(rset);
		close(stmt);
	}

	/**
	 * Closes given JDBC Resources
	 * 
	 * @param con
	 * @param stmt
	 * @param rset
	 */
	public static void close(Connection con, Statement stmt, ResultSet rset) {
		close(rset);
		close(stmt);
		close(con);
	}

	/**
	 * Closes given JDBC Resources
	 * 
	 * @param con
	 * @param stmt
	 * @param rset
	 */
	public static void close(Connection con, Statement stmt, ResultSetHelper rset) {
		close(rset);
		close(stmt);
		close(con);
	}
}
