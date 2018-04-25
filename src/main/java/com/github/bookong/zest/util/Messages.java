package com.github.bookong.zest.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author jiangxu
 */
public class Messages {

    private static final String         BUNDLE_NAME     = "com.github.bookong.zest.util.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages(){
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
