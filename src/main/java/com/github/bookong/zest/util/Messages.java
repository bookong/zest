package com.github.bookong.zest.util;

import com.github.bookong.zest.support.xml.data.ParamField;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Jiang Xu
 */
public class Messages {

    private static final String   BUNDLE_NAME     = "com.github.bookong.zest.util.messages"; //$NON-NLS-1$

    private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages(){
    }

    public static void reBundle() {
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    }

    public static String failRun() {
        return getString("fail.run");
    }

    public static String noAnnotationZest() {
        return getString("no.annotation.zest");
    }

    public static String fileNotFound(String filePath) {
        return getString("file.found", filePath);
    }

    public static String noData() {
        return getString("no.data");
    }

    public static String parseFile(String filePath) {
        return getString("parse.file", filePath);
    }

    public static String parseDate(String value) {
        return getString("parse.date", value);
    }

    public static String parseParamObj(ParamField xmlParamField) {
        return getString("parse.param.obj", xmlParamField.getName());
    }

    public static String parseDataSqlType(String tableName, String fieldName) {
        return getString("parse.data.sql.type", tableName, fieldName);
    }

    public static String parseDataSqlTypeUnsupported(String tableName, String fieldName, Integer colSqlType) {
        return getString("parse.data.sql.type.unsupported", tableName, fieldName, colSqlType);
    }

    public static String parseDataFieldDuplicate(String tableName, String fieldName) {
        return getString("parse.data.field.duplicate", tableName, fieldName);
    }

    public static String parseDataFieldUnder(String tableName, String fieldName) {
        return getString("parse.data.field.under", tableName, fieldName);
    }

    public static String parseDataFieldNone(String tableName, String fieldName) {
        return getString("parse.data.field.none", tableName, fieldName);
    }

    public static String parseDataTableSort() {
        return getString("parse.data.table.sort");
    }

    public static String parseDocObj(String sourceId, String collectionId, int rowIdx) {
        return getString("parse.doc.obj", sourceId, collectionId, rowIdx);
    }

    public static String parseDbMeta() {
        return getString("parse.db.meta");
    }

    public static String parseOperation() {
        return getString("parse.operation");
    }

    public static String duplicateOperation(String value) {
        return getString("duplicate.operation", value);
    }

    public static String operationNull(String sourceId) {
        return getString("operation.null", sourceId);
    }

    public static String operationUnsupported(String sourceId, String operationClass) {
        return getString("operation.unsupported", sourceId, operationClass);
    }

    public static String operationMismatching(String sourceId, String nodeName) {
        return getString("operation.mismatching", sourceId, nodeName);
    }

    public static String initExecutor(String className) {
        return getString("init.executor", className);
    }

    public static String initParam() {
        return getString("init.param");
    }

    public static String executorMatchSql() {
        return getString("executor.match.sql");
    }

    public static String statementEvaluate(String testCaseFilePath) {
        return getString("statement.evaluate", testCaseFilePath);
    }

    public static String statementRun(String desc) {
        return getString("statement.run", desc);
    }

    public static String ignoreTargetData(String id) {
        return getString("ignore.target.data", id);
    }

    public static String ignoreTargetTable(String sourceId, String tableName) {
        return getString("ignore.target.table", sourceId, tableName);
    }

    public static String ignoreTargetColUnspecified(String sourceId, String tableName) {
        return getString("ignore.target.col.unspecified", sourceId, tableName);
    }

    public static String startCheckTable(String sourceId, String tableName) {
        return getString("start.check.table", sourceId, tableName);
    }

    public static String checkDs(String value) {
        return getString("check.ds", value);
    }

    public static String checkTableSize(String sourceId, String tableName) {
        return getString("check.table.size", sourceId, tableName);
    }

    public static String checkTableColDate(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateCurrent(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.current", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateFromUnit(String sourceId, String tableName, int rowIdx, String columnName,
                                                   String unit) {
        return getString("check.table.col.date.from.unit", sourceId, tableName, rowIdx, columnName, unit);
    }

    public static String checkTableColDateFrom(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.from", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColRegexp(String sourceId, String tableName, int rowIdx, String columnName,
                                             String regExp) {
        return getString("check.table.col.regexp", sourceId, tableName, rowIdx, columnName, regExp);
    }

    public static String checkTableColNullable(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.nullable", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColNull(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.null", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateType(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.type", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableCol(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col", sourceId, tableName, rowIdx, columnName);
    }

    private static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!'; // $NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private static String getString(String key, Object... args) {
        return String.format(getString(key), args);
    }
}
