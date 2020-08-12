package com.github.bookong.zest.core;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.github.bookong.zest.core.testcase.AbstractDataConverter;
import com.github.bookong.zest.runner.junit4.statement.ZestFrameworkMethod;
import com.github.bookong.zest.runner.junit4.statement.ZestStatement;
import com.github.bookong.zest.util.ZestSqlHelper;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bookong.zest.runner.junit4.annotation.ZestDataSource;
import com.github.bookong.zest.runner.junit4.annotation.ZestTest;
import com.github.bookong.zest.core.executer.AbstractExcuter;
import com.github.bookong.zest.core.executer.SqlExcuter;
import com.github.bookong.zest.core.testcase.TestCaseData;
import com.github.bookong.zest.core.testcase.TestCaseDataSource;
import com.github.bookong.zest.core.testcase.ZestTestParam;
import com.github.bookong.zest.runner.ZestClassRunner;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;

/**
 * @author jiangxu
 */
@Deprecated
public class Launcher {

    private static Logger                            logger           = LoggerFactory.getLogger(Launcher.class);

    /** 被测试的对象 */
    private TestClass                                testClass;
    /** 当前要处理的 test case 文件路径 */
    private String                                   currTestCaseFilePath;
    /** 从当前要处理的 xml 中读取的测试用例 */




    private ZestClassRunner                          zestClassRunner;

    public Launcher(){
    }





























}
