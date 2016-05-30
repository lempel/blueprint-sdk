/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.Node;

import java.util.Iterator;

/**
 * JXPath(apache commons) helper
 *
 * @author lempel@gmail.com
 * @since 2014. 3. 18.
 */
public class JXPathHelper {
    /**
     * @param xpath XPath to evaluate
     * @param target target node
     * @return String or null(not found)
     */
    public static String evaluate(String xpath, Node target) {
        return evaluate(xpath, JXPathContext.newContext(target));
    }

    /**
     * @param xpath XPath to evaluate
     * @param context target context, JXPathContext.newContext(Node)
     * @return String or null(not found)
     */
    public static String evaluate(String xpath, JXPathContext context) {
        String result = null;

        try {
            result = (String) context.getValue(xpath);
        } catch (JXPathNotFoundException ignored) {
        }

        return result;
    }

    /**
     * @param xpath XPath to evaluate
     * @param target target node
     * @return Pointer or null(not found)
     */
    public static Pointer evaluatePointer(String xpath, Node target) {
        return evaluatePointer(xpath, JXPathContext.newContext(target));
    }

    /**
     * @param xpath XPath to evaluate
     * @param context target context, JXPathContext.newContext(Node)
     * @return Pointer or null(not found)
     */
    @SuppressWarnings("WeakerAccess")
    public static Pointer evaluatePointer(String xpath, JXPathContext context) {
        Pointer result = null;

        try {
            result = context.getPointer(xpath);
        } catch (JXPathNotFoundException ignored) {
        }

        return result;
    }

    /**
     * @param xpath XPath to evaluate
     * @param target target node
     * @return Pointer or null(not found)
     */
    public static Node evaluateNode(String xpath, Node target) {
        return evaluateNode(xpath, JXPathContext.newContext(target));
    }

    /**
     * @param xpath XPath to evaluate
     * @param context target context, JXPathContext.newContext(Node)
     * @return Pointer or null(not found)
     */
    @SuppressWarnings("WeakerAccess")
    public static Node evaluateNode(String xpath, JXPathContext context) {
        Node result = null;

        try {
            Pointer ptr = context.getPointer(xpath);
            if (ptr != null) {
                result = (Node) ptr.getNode();
            }
        } catch (JXPathNotFoundException ignored) {
        }

        return result;
    }

    /**
     * @param xpath XPath to evaluate
     * @param target target node
     * @return Iterator or null(not found)
     */
    public static Iterator<String> evaluateIterator(String xpath, Node target) {
        return evaluateIterator(xpath, JXPathContext.newContext(target));
    }

    /**
     * @param xpath XPath to evaluate
     * @param context target context, JXPathContext.newContext(Node)
     * @return Iterator or null(not found)
     */
    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public static Iterator<String> evaluateIterator(String xpath, JXPathContext context) {
        Iterator<String> result = null;

        try {
            result = context.iterate(xpath);
        } catch (JXPathNotFoundException ignored) {
        }

        return result;
    }

    /**
     * @param expr XPath to evaluate
     * @param target target node
     * @return Iterator or null(not found)
     */
    public static Iterator<String> evaluateIterator(CompiledExpression expr, Node target) {
        return evaluateIterator(expr, JXPathContext.newContext(target));
    }

    /**
     * @param expr XPath to evaluate
     * @param context target context, JXPathContext.newContext(Node)
     * @return Iterator or null(not found)
     */
    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public static Iterator<String> evaluateIterator(CompiledExpression expr, JXPathContext context) {
        Iterator<String> result = null;

        try {
            result = expr.iterate(context);
        } catch (JXPathNotFoundException ignored) {
        }

        return result;
    }

    /**
     * @param xpath XPath to evaluate
     * @param target target node
     * @return Iterator or null(not found)
     */
    public static Iterator<Pointer> evaluateIteratorPointers(String xpath, Node target) {
        return evaluateIteratorPointers(xpath, JXPathContext.newContext(target));
    }

    /**
     * @param xpath XPath to evaluate
     * @param context target context, JXPathContext.newContext(Node)
     * @return Iterator or null(not found)
     */
    @SuppressWarnings("unchecked")
    public static Iterator<Pointer> evaluateIteratorPointers(String xpath, JXPathContext context) {
        Iterator<Pointer> result = null;

        try {
            result = context.iteratePointers(xpath);
        } catch (JXPathNotFoundException ignored) {
        }

        return result;
    }

    /**
     * @param expr XPath to evaluate
     * @param target target node
     * @return Iterator or null(not found)
     */
    public static Iterator<Pointer> evaluateIteratorPointers(CompiledExpression expr, Node target) {
        return evaluateIteratorPointers(expr, JXPathContext.newContext(target));
    }

    /**
     * @param expr XPath to evaluate
     * @param context target context, JXPathContext.newContext(Node)
     * @return Iterator or null(not found)
     */
    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public static Iterator<Pointer> evaluateIteratorPointers(CompiledExpression expr, JXPathContext context) {
        Iterator<Pointer> result = null;

        try {
            result = expr.iteratePointers(context);
        } catch (JXPathNotFoundException ignored) {
        }

        return result;
    }
}
