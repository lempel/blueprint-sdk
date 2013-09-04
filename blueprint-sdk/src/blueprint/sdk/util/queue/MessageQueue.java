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
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Simple Message Queue.
 * 
 * @author Simon Lee
 * @since 2013. 9. 4.
 */
public class MessageQueue {
	/** actual queue */
	protected LinkedList<Element> queue = new LinkedList<Element>();

	/** waiting consumers */
	protected LinkedList<MessageConsumer> waiters = new LinkedList<MessageConsumer>();

	public MessageQueue() {
		super();
	}

	/**
	 * Clears queue
	 * 
	 * @throws SQLException
	 *             Can't delete
	 */
	public void clear() {
		synchronized (queue) {
			queue.clear();
		}
	}

	/**
	 * @return consumer of this queue
	 */
	public MessageConsumer newConsumer() {
		return new MessageConsumer(this);
	}

	/**
	 * Push an element to queue
	 * 
	 * @param element
	 * @throws NullPointerException
	 *             null element
	 */
	public void push(String element) {
		if (element == null) {
			throw new NullPointerException("Can't push null");
		}

		Element item = new Element();
		item.uuid = UUID.randomUUID().toString();
		item.content = element;

		synchronized (queue) {
			queue.push(item);

			try {
				MessageConsumer consumer = waiters.pop();
				consumer.interrupt();
			} catch (NoSuchElementException ignored) {
			}
		}
	}

	/**
	 * Retrieves an element from queue.
	 * 
	 * @return queue element or null(queue is empty)
	 */
	public String pop() {
		String result = null;

		synchronized (queue) {
			try {
				Element element = queue.pop();
				result = element.content;
			} catch (NoSuchElementException ignored) {
				// just return null
			}
		}

		return result;
	}

	/**
	 * @return queue size
	 */
	public int size() {
		synchronized (queue) {
			return queue.size();
		}
	}
}

/**
 * Internal element of AbstractJdbcQueue
 * 
 * @author Simon Lee
 * @since 2013. 8. 27.
 */
class Element {
	/** UUID of content */
	public String uuid;
	/** actual queue content */
	public String content;
}