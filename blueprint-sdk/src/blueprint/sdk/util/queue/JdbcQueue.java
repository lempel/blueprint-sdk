/*
  END-USER LICENSE AGREEMENT FOR pns

  Copyright (c) 2013 Huray Positive, Inc. All rights reserved.

  THIS SOFTWARE IS PROVIDED BY HURAY POSITIVE INC. ``AS IS'' AND ANY EXPRESS
  OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
  NON-INFRINGEMENT, ARE DISCLAIMED.  IN NO EVENT SHALL HURAY POSITIVE INC.
  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
  THE POSSIBILITY OF SUCH DAMAGE.
 */

package blueprint.sdk.util.queue;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.sql.DataSource;

/**
 * Prototype for JDBC based Queue.<br>
 * <br>
 * Must call {@link JdbcQueue#init()} before use.<br>
 * 
 * @author Simon Lee
 * @since 2013. 8. 26.
 */
public abstract class JdbcQueue extends MessageQueue {
	/** DataSource for persistence */
	protected DataSource datasrc;

	/**
	 * Constructor
	 * 
	 * @param dsrc
	 *            DataSource for persistence
	 */
	public JdbcQueue(DataSource datasrc) {
		super();

		this.datasrc = datasrc;

		if (datasrc == null) {
			throw new NullPointerException("DataSource is null");
		}
	}

	/**
	 * create table & load queue contents from DataSource
	 */
	public void init() throws SQLException {
		synchronized (queue) {
			createTable();
			load();
		}
	}

	/**
	 * Clears queue
	 * 
	 * @throws JdbcQueueException
	 *             Can't delete
	 */
	@Override
	public void clear() throws JdbcQueueException {
		synchronized (queue) {
			try {
				emptyTable();
				queue.clear();
			} catch (SQLException e) {
				throw new JdbcQueueException(e);
			}
		}
	}

	/**
	 * Push an element to queue
	 * 
	 * @param element
	 * @throws JdbcQueueException
	 *             Can't insert
	 * @throws NullPointerException
	 *             null element
	 */
	@Override
	public void push(String element) {
		if (element == null) {
			throw new NullPointerException("Can't push null");
		}

		Element item = new Element();
		item.uuid = UUID.randomUUID().toString();
		item.content = element;

		try {
			insert(item);
			synchronized (queue) {
				queue.push(item);
			}
		} catch (SQLException e) {
			throw new JdbcQueueException(e);
		}

		try {
			MessageConsumer consumer = waiters.pop();
			if (consumer != null) {
				consumer.interrupt();
			}
		} catch (NoSuchElementException ignored) {
		}
	}

	/**
	 * Retrieves an element from queue.
	 * 
	 * @return queue element or null(queue is empty)
	 * @throws JdbcQueueException
	 *             Can't delete
	 */
	@Override
	public String pop() throws JdbcQueueException {
		String result = null;

		try {
			Element element = null;
			synchronized (queue) {
				element = queue.pop();
			}

			if (element != null) {
				try {
					delete(element);
					result = element.content;
				} catch (SQLException e) {
					synchronized (queue) {
						queue.push(element);
					}
					throw new JdbcQueueException(e);
				}
			}
		} catch (NoSuchElementException ignored) {
			// just return null
		}

		return result;
	}

	/**
	 * Load queue contents from DBMS
	 * 
	 * @throws SQLException
	 *             Can't load
	 */
	protected abstract void load() throws SQLException;

	/**
	 * Create table for queue (if not exits)
	 * 
	 * @throws SQLException
	 *             Can't create
	 */
	protected abstract void createTable() throws SQLException;

	/**
	 * Empty table for queue
	 * 
	 * @throws SQLException
	 *             Can't delete
	 */
	protected abstract void emptyTable() throws SQLException;

	/**
	 * Insert queue element to table
	 * 
	 * @param element
	 * @throws SQLException
	 *             Can't insert
	 */
	protected abstract void insert(Element element) throws SQLException;

	/**
	 * Delete queue element from table
	 * 
	 * @param element
	 * @throws SQLException
	 *             Can't delete
	 */
	protected abstract void delete(Element element) throws SQLException;
}
