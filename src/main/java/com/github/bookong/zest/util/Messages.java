package com.github.bookong.zest.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Jiang Xu
 */
public class Messages {

    private static final String   BUNDLE_NAME     = "com.github.bookong.zest.util.messages";

    private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages(){
    }

    public static void reBundle() {
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    }

    public static String run(String desc) {
        return getString("run", desc);
    }

    public static String runFail() {
        return getString("run.fail");
    }

    public static String noData() {
        return getString("no.data");
    }

    public static String noAnnotationZest() {
        return getString("no.annotation.zest");
    }

    public static String fileNotFound(String filePath) {
        return getString("file.found", filePath);
    }

    public static String parse(String filePath) {
        return getString("parse", filePath);
    }

    public static String parseCommonAttr(String nodeName, String attrName) {
        return getString("parse.common.attr", nodeName, attrName);
    }

    public static String parseCommonAttrUnknown(String nodeName, String attrNames) {
        return getString("parse.common.attr.unknown", nodeName, attrNames);
    }

    public static String parseCommonAttrDuplicate(String attrName, String attrValue) {
        return getString("parse.common.attr.duplicate", attrName, attrValue);
    }

    public static String parseCommonAttrEmpty(String attrName) {
        return getString("parse.common.attr.empty", attrName);
    }

    public static String parseCommonClassFound(String className) {
        return getString("parse.common.class.found", className);
    }

    public static String parseCommonChildren(String nodeName) {
        return getString("parse.common.children", nodeName);
    }

    public static String parseCommonChildrenUnknown(String parentElement, String childrenElement) {
        return getString("parse.common.children.unknown", parentElement, childrenElement);
    }

    public static String parseCommonChildrenList(String parentElement, String childrenElement) {
        return getString("parse.common.children.list", parentElement, childrenElement);
    }

    public static String parseZest() {
        return getString("parse.zest");
    }

    public static String parseZestNecessary() {
        return getString("parse.zest.necessary");
    }

    public static String parseSourcesError() {
        return getString("parse.sources.error");
    }

    public static String parseSourceError(String sourceId) {
        return getString("parse.source.error", sourceId);
    }

    public static String parseSourceNecessary() {
        return getString("parse.source.necessary");
    }

    public static String parseSourceInitError() {
        return getString("parse.source.init.error");
    }

    public static String parseSourceVerifyError() {
        return getString("parse.source.verify.error");
    }

    public static String parseSourceOperationMatch(String operation, String nodeName, String subNodeName) {
        return getString("parse.source.operation.match", operation, nodeName, subNodeName);
    }

    public static String parseSourceOperationUnknown(String operation) {
        return getString("parse.source.operation.unknown", operation);
    }

    public static String parseTableError(String name) {
        return getString("parse.table.error", name);
    }

    public static String parseTableMeta() {
        return getString("parse.table.meta");
    }

    public static String parseTableSortExist(String fieldName) {
        return getString("parse.table.sort.exist", fieldName);
    }

    public static String parseTableRule(String path) {
        return getString("parse.table.rule", path);
    }

    public static String parseCollectionError(String name) {
        return getString("parse.collection.error", name);
    }

    public static String parseCollectionSortExits(String fieldName) {
        return getString("parse.collection.sort.exits", fieldName);
    }

    public static String parseCollectionRule(String path) {
        return getString("parse.collection.rule", path);
    }

    public static String parseSortsError() {
        return getString("parse.sorts.error");
    }

    public static String parseSortsPosition() {
        return getString("parse.sorts.position");
    }

    public static String parseSortsOrder() {
        return getString("parse.sorts.order");
    }

    public static String parseSortError(String fieldName) {
        return getString("parse.sort.error", fieldName);
    }

    public static String parseSortDirection() {
        return getString("parse.sort.direction");
    }

    public static String parseRulesPosition() {
        return getString("parse.rules.position");
    }

    public static String parseRuleError(String path) {
        return getString("parse.rule.error", path);
    }

    public static String parseRuleFromUnitUnknown(String unit) {
        return getString("parse.rule.from.unit.unknown", unit);
    }

    public static String parseOperator(String value) {
        return getString("parse.operator", value);
    }

    public static String parseOperatorDuplicate(String value) {
        return getString("parse.operator.duplicate", value);
    }

    public static String parseExecutor(String value, String className) {
        return getString("parse.executor", value, className);
    }

    public static String parseParamError() {
        return getString("parse.param.error");
    }

    public static String parseRulesError() {
        return getString("parse.rules.error");
    }

    public static String parseRuleChoice() {
        return getString("parse.rule.choice");
    }

    public static String parseParamNone(String fieldName) {
        return getString("parse.param.none", fieldName);
    }

    public static String parseParamNonsupportMap() {
        return getString("parse.param.nonsupport.map");
    }

    public static String parseParamInit(String methodName) {
        return getString("parse.param.init", methodName);
    }

    public static String parseDataError(int dataIdx) {
        return getString("parse.data.error", dataIdx);
    }

    public static String parseDataTableRowExist(String fieldName, String tableName) {
        return getString("parse.data.table.row.exist", fieldName, tableName);
    }

    public static String parseDataDate(String value) {
        return getString("parse.data.date", value);
    }

    public static String parseDataDatePattern() {
        return getString("parse.data.date.pattern");
    }

    public static String operatorUnbound(String value) {
        return getString("operator.unbound", value);
    }

    public static String operatorCast(String value, String fromClass, String toClass) {
        return getString("operator.cast", value, fromClass, toClass);
    }

    public static String verifyIgnore(String sourceId) {
        return getString("verify.ignore", sourceId);
    }

    public static String verifyTableIgnore(String sourceId, String tableName) {
        return getString("verify.table.ignore", sourceId, tableName);
    }

    public static String verifyTableStart(String sourceId, String tableName) {
        return getString("verify.table.start", sourceId, tableName);
    }

    public static String verifyTableExecutor(String executorClass) {
        return getString("verify.table.executor", executorClass);
    }

    public static String verifyTableSize(String sourceId, String tableName) {
        return getString("verify.table.size", sourceId, tableName);
    }

    public static String verifyRowError(String sourceId, String tableName, int rowIdx) {
        return getString("verify.row.error", sourceId, tableName, rowIdx);
    }

    public static String verifyRowRuleNonuse(String rulePath, int rowIdx) {
        return getString("verify.row.rule.nonuse", rulePath, rowIdx);
    }

    public static String verifyRowDataNull(String columnName) {
        return getString("verify.row.data.null", columnName);
    }

    public static String verifyRowDataDate(String columnName) {
        return getString("verify.row.data.date", columnName);
    }

    public static String verifyRowData(String columnName, String value) {
        return getString("verify.row.data", columnName, value);
    }

    public static String verifyCollectionIgnore(String sourceId, String collectionName) {
        return getString("verify.collection.ignore", sourceId, collectionName);
    }

    public static String verifyCollectionStart(String sourceId, String collectionName) {
        return getString("verify.collection.start", sourceId, collectionName);
    }

    public static String verifyCollectionExecutor(String executorClass) {
        return getString("verify.collection.executor", executorClass);
    }

    public static String verifyCollectionSize(String sourceId, String collectionId) {
        return getString("verify.collection.size", sourceId, collectionId);
    }

    public static String verifyDocumentError(String sourceId, String collectionId, int rowIdx) {
        return getString("verify.document.error", sourceId, collectionId, rowIdx);
    }

    public static String parseParamObjLoad(String fieldName) {
        return getString("parse.param.obj.load", fieldName);
    }

    public static String checkTableColDate(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateCurrent(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.current", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateFrom(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.from", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColRegexp(String sourceId, String tableName, int rowIdx, String columnName,
                                             String regExp) {
        return getString("check.table.col.regexp", sourceId, tableName, rowIdx, columnName, regExp);
    }

    private static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    private static String getString(String key, Object... args) {
        Object[] objs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                objs[i] = "";
            } else {
                objs[i] = arg;
            }
        }
        return String.format(getString(key), objs);
    }
}
