package com.github.bookong.zest.runner.junit4.statement;

import java.io.File;

import org.junit.runners.model.FrameworkMethod;

/**
 * @author Jiang Xu
 */
public class ZestFrameworkMethod extends FrameworkMethod {

    private String testCasePath;
    private String testCaseFileName;

    public ZestFrameworkMethod(FrameworkMethod method, String testCasePath){
        super(method.getMethod());
        this.testCasePath = testCasePath;
        this.testCaseFileName = testCasePath.substring(testCasePath.lastIndexOf(File.separator) + 1);
    }

    @Override
    public String getName() {
        return String.format("%s [%s] ", super.getName(), testCaseFileName);
    }

    public String getTestCasePath() {
        return testCasePath;
    }
}
