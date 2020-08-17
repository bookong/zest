package com.github.bookong.zest.runner.junit4.statement;

import java.io.File;

import org.junit.runners.model.FrameworkMethod;

/**
 * @author Jiang Xu
 */
public class ZestFrameworkMethod extends FrameworkMethod {

    private String testCaseFilePath;
    private String testCaseFileName;

    public ZestFrameworkMethod(FrameworkMethod method, String testCaseFilePath){
        super(method.getMethod());
        this.testCaseFilePath = testCaseFilePath;
        this.testCaseFileName = testCaseFilePath.substring(testCaseFilePath.lastIndexOf(File.separator) + 1);
    }

    @Override
    public String getName() {
        return String.format("%s [%s] ", super.getName(), testCaseFileName);
    }

    public String getTestCaseFilePath() {
        return testCaseFilePath;
    }
}
