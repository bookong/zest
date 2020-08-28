package com.github.bookong.zest.testcase.mongo;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.executor.MongoExecutor;
import com.github.bookong.zest.rule.AbstractRule;
import com.github.bookong.zest.testcase.Source;
import com.github.bookong.zest.testcase.ZestData;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestJsonUtil;
import com.github.bookong.zest.util.ZestUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Jiang Xu
 */
public class Document {

    protected Logger    logger        = LoggerFactory.getLogger(getClass());

    private Object      data;

    private Set<String> expectedPaths = Collections.emptySet();

    public Document(MongoExecutor mongoExecutor, Class<?> entityClass, String collectionName, String xmlContent,
                    boolean isVerifyElement){
        try {
            this.data = mongoExecutor.createDocumentData(entityClass, collectionName, xmlContent, isVerifyElement);
        } catch (UnsupportedOperationException e) {
            this.data = ZestJsonUtil.fromJson(xmlContent, entityClass);
        }

        if (isVerifyElement) {
            this.expectedPaths = ZestUtil.parsePathsFromJson(xmlContent);
        }
    }

    public Object getData() {
        return data;
    }

    public Set<String> getExpectedPaths() {
        return expectedPaths;
    }

    public void verify(MongoExecutor executor, MongoOperations operator, ZestData zestData, Source source,
                       Collection collection, int rowIdx, Object actualData) {
        try {
            try {
                executor.verifyDocument(operator, zestData, source, collection, rowIdx, this, actualData);
            } catch (UnsupportedOperationException e) {
                verify(zestData, collection, rowIdx, actualData);
            }
        } catch (Exception e) {
            throw new ZestException(Messages.verifyDocumentError(source.getId(), collection.getName(), rowIdx), e);
        }
    }

    private void verify(ZestData zestData, Collection collection, int rowIdx, Object actualData) throws Exception {
        if (!collection.getEntityClass().isAssignableFrom(actualData.getClass())) {
            throw new ZestException(Messages.verifyDocumentType(collection.getEntityClass().getName(),
                                                                actualData.getClass().getName()));
        }

        for (Field f : collection.getEntityClass().getDeclaredFields()) {
            if (!f.isAccessible()) {
                continue;
            }

            Object expected = f.get(getData());
            Object actual = f.get(actualData);
            verify(zestData, collection, rowIdx, expected, actual, StringUtils.EMPTY, f.getName());
        }
    }

    private void verify(ZestData zestData, Collection collection, int rowIdx, Object expected, Object actual,
                        String parentPath, String subPath) {
        String path = ZestUtil.getPath(parentPath, subPath);

        if (!getExpectedPaths().contains(path)) {
            return;
        }

        AbstractRule rule = collection.getRuleMap().get(path);

        if (expected != null) {
            if (rule != null) {
                logger.info(Messages.verifyRuleIgnore(rule.getPath(), rowIdx));
            }

            if (expected instanceof Date) {
                Assert.assertTrue(Messages.verifyDocumentDataDate(path), actual instanceof Date);
                Date expectedDateInZest = ZestDateUtil.getDateInZest(zestData, (Date) expected);
                String expectedValue = ZestDateUtil.formatDateNormal(expectedDateInZest);
                String actualValue = ZestDateUtil.formatDateNormal((Date) actual);
                Assert.assertEquals(Messages.verifyDocumentData(path, expectedValue), expectedValue, actualValue);

            } else if (expected instanceof Map) {
                Map map = (Map) expected;
                for (Object key : map.keySet()) {
                    verify(zestData, collection, rowIdx, map.get(key), actual, path, String.valueOf(key));
                }

            } else if (expected instanceof List) {
                for (Object item : (List) expected) {
                    verify(zestData, collection, rowIdx, item, actual, path, StringUtils.EMPTY);
                }

            } else {
                String expectedValue = String.valueOf(expected);
                String actualValue = String.valueOf(actual);
                Assert.assertEquals(Messages.verifyDocumentData(path, expectedValue), expectedValue, actualValue);
            }

        } else {
            // expected == null
            if (rule != null) {
                rule.verify(zestData, path, actual);
            } else {
                Assert.assertNull(Messages.verifyDocumentDataNull(path), actual);
            }
        }
    }
}
