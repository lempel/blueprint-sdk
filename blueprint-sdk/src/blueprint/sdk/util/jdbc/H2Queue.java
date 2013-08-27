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

import javax.sql.DataSource;

/**
 * H2 based AbstractJdbcQueue implementation (example).
 * 
 * @author Simon Lee
 * @since 2013. 8. 27.
 */
public class H2Queue extends AbstractJdbcQueue {
	/** H2 Connection */
	protected Connection con = null;

	/** schema for queue */
	protected static final String SCHEMA = "BLUEPRINT";
	/** table for queue */
	protected static final String TABLE = "QUEUE";

	/**
	 * Constructor
	 * 
	 * @param datasrc
	 *            DataSource for persistence
	 */
	public H2Queue(DataSource datasrc) {
		super(datasrc);
	}

	/**
	 * Check connection to H2
	 * 
	 * @throws SQLException
	 */
	protected void checkConnection() throws SQLException {
		if (con == null || con.isClosed()) {
			con = datasrc.getConnection();
		}
	}

	@Override
	protected void createTable() throws SQLException {
		checkConnection();

		Statement stmt = con.createStatement();
		try {
			stmt.executeUpdate("CREATE SCHEMA " + SCHEMA);
		} catch (SQLException e) {
			if (e.getErrorCode() != 90078) {
				throw e;
			}
		}

		try {
			stmt.executeUpdate("CREATE TABLE " + SCHEMA + "." + TABLE + " ( UUID CHAR(36) NOT NULL, CONTENT VARCHAR)");
			stmt.executeUpdate("ALTER TABLE " + SCHEMA + "." + TABLE + " ADD CONSTRAINT QUEUE_IDX_01 UNIQUE (UUID)");
		} catch (SQLException e) {
			if (e.getErrorCode() != 42101) {
				throw e;
			}
		} finally {
			CloseHelper.close(stmt);
		}
	}

	@Override
	protected void load() throws SQLException {
		checkConnection();

		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = con.createStatement();
			rset = stmt.executeQuery("SELECT UUID, CONTENT FROM " + SCHEMA + "." + TABLE + "");
			while (rset.next()) {
				Element item = new Element();
				item.uuid = rset.getString(1);
				item.content = rset.getString(2);
				queue.add(item);
			}
		} finally {
			CloseHelper.close(stmt, rset);
		}
	}

	@Override
	protected void insert(Element element) throws SQLException {
		checkConnection();

		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("INSERT INTO " + SCHEMA + "." + TABLE + " (UUID, CONTENT) VALUES ('" + element.uuid
					+ "', '" + element.content + "')");
		} finally {
			CloseHelper.close(stmt);
		}
	}

	@Override
	protected void delete(Element element) throws SQLException {
		checkConnection();

		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("DELETE FROM " + SCHEMA + "." + TABLE + " WHERE UUID = '" + element.uuid + "'");
		} finally {
			CloseHelper.close(stmt);
		}
	}
}
