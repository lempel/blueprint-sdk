/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.msoffice;

import blueprint.sdk.util.LoggerHelper;
import blueprint.sdk.util.StringUtil;
import blueprint.sdk.util.Validator;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Extract pivot source table from Microsoft Excel (xlsx only) files.
 *
 * @author lempel@gmail.com
 * @since 2017-11-09
 */
public class PivotSourceExtractor {
    private static final Logger L = LoggerHelper.get();

    /**
     * ".*pivotCacheDefinition\d+\.xml"
     */
    private static final String PIVOT_CACHE_DEF_REGEX = ".*pivotcachedefinition\\d+\\.xml";
    /**
     * ".*pivotCacheRecords\d+\.xml"
     */
    private static final String PIVOT_CACHE_REC_REGEX = ".*pivotcacherecords\\d+\\.xml";

    /**
     * Context for whole extraction process
     */
    private class ExtractorContext extends ArrayList<TableContext> {
        TableContext getTableContext(int tableId) {
            TableContext result = null;

            for (TableContext tableContext : this) {
                if (tableContext.tableId == tableId) {
                    result = tableContext;
                    break;
                }
            }

            return result;
        }
    }

    /**
     * Context for a table
     */
    private class TableContext {
        int tableId = -1;

        TableDefinition tableDefinition = new TableDefinition();

        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        TableContext(int tableId) {
            this.tableId = tableId;
        }
    }

    /**
     * Table definition of pivot source table
     */
    private class TableDefinition extends ArrayList<ColumnDefinition> {
        ColumnDefinition getColumnDefinition(int columnId) {
            ColumnDefinition result = null;

            for (ColumnDefinition columnDefinition : this) {
                if (columnDefinition.columnId == columnId) {
                    result = columnDefinition;
                    break;
                }
            }

            return result;
        }
    }

    /**
     * Column definition of pivot source table
     */
    private class ColumnDefinition extends ArrayList<String> {
        int columnId = -1;

        ColumnDefinition(int columnId) {
            super();

            this.columnId = columnId;
        }
    }

    /**
     * Output Formats.<br>
     * You can guess form names.<br>
     */
    public enum OutputFormat {
        CSV_WITH_HEADER, CSV_WITHOUT_HEADER, JSON_WITH_FIELD_NAME, JSON_WITHOUT_FIELD_NAME
    }

    /**
     * XPathFactory for internal use
     */
    private XPathFactory xpFactory = XPathFactory.newInstance();
    /**
     * XML Transformer for debug
     */
    private Transformer xmlTransformer;

    // https://msdn.microsoft.com/en-us/library/dd922181(v=office.12).aspx

    /**
     * Extract pivot source tables from given xlsx file
     *
     * @param filename     xlsx filename
     * @param outputFormat output format
     * @return formatted source tables
     * @throws IOException              Can't read xlsx file
     * @throws XPathExpressionException Invalid XPath (probably due to xlsx format change)
     */
    public String[] extract(String filename, OutputFormat outputFormat) throws IOException, XPathExpressionException {

        ExtractorContext context = new ExtractorContext();

        // can't guarantee definition comes first.
        // use two passes.
        processDefinitions(filename, context);
        processRecords(filename, context);

        List<String> result = convert(outputFormat, context);

        return result.toArray(new String[result.size()]);
    }

    /**
     * Process pivot cache definitions of given xlsx file
     *
     * @param filename xlsx filename
     * @param context  extractor context
     * @throws IOException              Can't read xlsx file
     * @throws XPathExpressionException Invalid XPath (probably due to xlsx format change)
     */
    private void processDefinitions(String filename, ExtractorContext context) throws IOException, XPathExpressionException {
        ZipFile zipFile = new ZipFile(filename);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = Validator.nvl(entry.getName()).toLowerCase();
            try {
                if (entryName.matches(PIVOT_CACHE_DEF_REGEX)) {
                    Document doc = readEntry(zipFile, entry);
                    extractDefinition(context, doc, entryName);
                }
            } catch (ParserConfigurationException | SAXException e) {
                L.error("Can't read definition entry - {}", entryName, e);
            }
        }
    }

    /**
     * Process pivot cache records of given xlsx file
     *
     * @param filename xlsx filename
     * @param context  extractor context
     * @throws IOException              Can't read xlsx file
     * @throws XPathExpressionException Invalid XPath (probably due to xlsx format change)
     */
    private void processRecords(String filename, ExtractorContext context) throws IOException, XPathExpressionException {
        ZipFile zipFile = new ZipFile(filename);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = Validator.nvl(entry.getName()).toLowerCase();
            try {
                if (entryName.matches(PIVOT_CACHE_REC_REGEX)) {
                    Document doc = readEntry(zipFile, entry);
                    extractRecords(context, doc, entryName);
                }
            } catch (ParserConfigurationException | SAXException e) {
                L.error("Can't read records entry - {}", entryName, e);
            }
        }
    }

    /**
     * Convert source tables to Strings
     *
     * @param outputFormat output format
     * @param context      extractor context
     * @return String representations of source tables
     */
    private List<String> convert(OutputFormat outputFormat, ExtractorContext context) {
        List<String> result = new ArrayList<>();

        context.forEach(tableContext -> {
            StringBuilder builder = new StringBuilder();

            BiConsumer<StringBuilder, String> appendString = (b, s) -> {
                if (s.contains(",")) {
                    b.append('"');
                    b.append(s);
                    b.append('"');
                } else {
                    b.append(s);
                }
            };

            BiConsumer<StringBuilder, TableContext> toJsonAsArray = (b, c) -> {
                b.append('[');
                c.rows.forEach(row -> {
                    if (b.length() > 1) {
                        b.append(',');
                    }
                    b.append('[');
                    for (int i = 0; i < row.size(); i++) {
                        if (i > 0) {
                            b.append(',');
                        }
                        b.append('"');
                        b.append(row.get(i));
                        b.append('"');
                    }
                    b.append(']');
                });
                b.append(']');
            };

            switch (outputFormat) {
                case CSV_WITH_HEADER:
                    if (!tableContext.headers.isEmpty()) {
                        StringBuilder headerBuilder = new StringBuilder();
                        tableContext.headers.forEach(header -> {
                            if (headerBuilder.length() > 0) {
                                headerBuilder.append(", ");
                            }
                            appendString.accept(headerBuilder, header);
                        });
                        builder.append(headerBuilder.toString());
                        builder.append('\n');
                    }
                case CSV_WITHOUT_HEADER:
                    if (!tableContext.rows.isEmpty()) {
                        tableContext.rows.forEach(row -> {
                            StringBuilder rowBuilder = new StringBuilder();
                            row.forEach(column -> {
                                if (rowBuilder.length() > 0) {
                                    rowBuilder.append(", ");
                                }
                                appendString.accept(rowBuilder, column);
                            });
                            builder.append(rowBuilder.toString());
                            builder.append('\n');
                        });
                    }
                    break;
                case JSON_WITH_FIELD_NAME:
                    if (tableContext.rows.isEmpty()) {
                        toJsonAsArray.accept(builder, tableContext);
                    } else {
                        builder.append('[');
                        tableContext.rows.forEach(row -> {
                            if (builder.length() > 1) {
                                builder.append(',');
                            }
                            builder.append('{');
                            for (int i = 0; i < row.size(); i++) {
                                if (i > 0) {
                                    builder.append(',');
                                }
                                builder.append('"');
                                builder.append(tableContext.headers.get(i));
                                builder.append('"');
                                builder.append(':');
                                builder.append('"');
                                builder.append(row.get(i));
                                builder.append('"');
                            }
                            builder.append('}');
                        });
                        builder.append(']');
                    }
                    break;
                case JSON_WITHOUT_FIELD_NAME:
                    toJsonAsArray.accept(builder, tableContext);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported output format - " + outputFormat);
            }

            String source = builder.toString();
            result.add(source);
        });

        return result;
    }

    /**
     * Extract pivot cache definition from give XML Document
     *
     * @param context   extractor context
     * @param doc       XML Document
     * @param entryName entry name
     * @throws IOException              Can't read xlsx file
     * @throws XPathExpressionException Invalid XPath (probably due to xlsx format change)
     */
    private void extractDefinition(ExtractorContext context, Document doc, String entryName)
            throws IOException, XPathExpressionException {

        // read pivot table definition
        int tableId = getPivotTableId(entryName) - 1;
        L.info("reading pivot table definition {}", tableId);

        TableContext tableContext = new TableContext(tableId);
        context.add(tableContext);

        TableDefinition tableDefinition = tableContext.tableDefinition;
        List<String> headers = tableContext.headers;

        XPath xp = xpFactory.newXPath();
        NodeList fields = (NodeList) xp.evaluate("//cacheField", doc, XPathConstants.NODESET);
        if (fields.getLength() > 0) {
            for (int i = 0; i < fields.getLength(); i++) {
                Node cacheField = fields.item(i);

                //L.debug("node = {}", nodeToString(node));

                String fieldName = String.valueOf(xp.evaluate("@name", cacheField, XPathConstants.STRING));
                String itemCount = StringUtil.nvl(xp.evaluate("sharedItems/@count", cacheField, XPathConstants.STRING), "0");

                L.debug("  {}", i);
                L.debug("    field = {}", fieldName);
                L.debug("    count = {}", itemCount);

                headers.add(fieldName);

                ColumnDefinition columnDefinition = new ColumnDefinition(i);

                if (!"0".equals(itemCount)) {
                    NodeList sharedItems = (NodeList) (xp.evaluate("sharedItems/*", cacheField, XPathConstants.NODESET));
                    for (int x = 0; x < sharedItems.getLength(); x++) {
                        Node childNode = sharedItems.item(x);
                        String v = (String) xp.evaluate("@v", childNode, XPathConstants.STRING);
                        columnDefinition.add(v);
                        L.debug("    v = '{}'", v);
                    }
                }

                tableDefinition.add(columnDefinition);
            }
        }
    }

    /**
     * Extract pivot cache records from give XML Document
     *
     * @param context   extractor context
     * @param doc       XML Document
     * @param entryName entry name
     * @throws IOException              Can't read xlsx file
     * @throws XPathExpressionException Invalid XPath (probably due to xlsx format change)
     */
    private void extractRecords(ExtractorContext context, Document doc, String entryName)
            throws IOException, XPathExpressionException {

        // read pivot table records
        int tableId = getPivotTableId(entryName) - 1;
        L.info("reading pivot table records {}", tableId);

        TableContext tableContext = context.getTableContext(tableId);
        TableDefinition tableDefinition = tableContext.tableDefinition;
        List<List<String>> rows = tableContext.rows;

        XPath xp = xpFactory.newXPath();
        NodeList records = (NodeList) xp.evaluate("//r", doc, XPathConstants.NODESET);
        if (records.getLength() > 0) {
            for (int i = 0; i < records.getLength(); i++) {
                Node record = records.item(i);

                //L.debug("node = {}", nodeToString(record));
                L.debug("  row {}", i + 1);

                List<String> columns = new ArrayList<>();
                rows.add(columns);

                NodeList childNodes = record.getChildNodes();
                for (int x = 0; x < childNodes.getLength(); x++) {
                    Node childNode = childNodes.item(x);
                    String v = (String) xp.evaluate("@v", childNode, XPathConstants.STRING);

                    try {
                        ColumnDefinition columnDefinitions = tableDefinition.getColumnDefinition(x);
                        int intValue = Integer.parseInt(v);

                        if (columnDefinitions.size() > intValue) {
                            v = columnDefinitions.get(intValue);
                        }
                    } catch (NumberFormatException ignored) {
                    }

                    L.debug("    column {} = {}", x + 1, v);
                    columns.add(v);
                }
            }
        }
    }

    /**
     * Read an XML from given zip file/entry
     *
     * @param file  target zip file
     * @param entry target entry
     * @return XML Document of given entry
     * @throws IOException                  Can't read file
     * @throws ParserConfigurationException Can't parse as XML
     * @throws SAXException                 Can't parse as XML
     */
    private Document readEntry(ZipFile file, ZipEntry entry)
            throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(file.getInputStream(entry));
    }

    /**
     * Get given entry's pivot table id
     *
     * @param entryName name of {@link ZipEntry}
     * @return id of pivot table or 0 for non pivot table entries
     */
    private int getPivotTableId(String entryName) {
        int result = 0;
        String name = Validator.nvl(entryName).toLowerCase();
        if (name.matches(PIVOT_CACHE_DEF_REGEX)) {
            int pos1 = name.indexOf("pivotcachedefinition") + 20;
            int pos2 = name.indexOf(".xml");

            result = Integer.parseInt(name.substring(pos1, pos2));
        } else if (name.matches(PIVOT_CACHE_REC_REGEX)) {
            int pos1 = name.indexOf("pivotcacherecords") + 17;
            int pos2 = name.indexOf(".xml");

            result = Integer.parseInt(name.substring(pos1, pos2));
        }

        return result;
    }

    /**
     * Convert XML Node to String
     *
     * @param node XML Node
     * @return String representation of given node
     * @throws TransformerException Can't transform
     */
    private String nodeToString(Node node) throws TransformerException {
        if (xmlTransformer == null) {
            TransformerFactory trFactory = TransformerFactory.newInstance();
            xmlTransformer = trFactory.newTransformer();
        }

        StringWriter sw = new StringWriter();
        xmlTransformer.transform(new DOMSource(node), new StreamResult(sw));
        return sw.getBuffer().toString().substring(38);
    }
}
