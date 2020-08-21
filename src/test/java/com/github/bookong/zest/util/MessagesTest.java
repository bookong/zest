package com.github.bookong.zest.util;

import com.github.bookong.zest.support.xml.data.ParamField;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;

/**
 * @author Jiang Xu
 * @see Messages
 */
public class MessagesTest {

    @Test
    public void testAll() throws Exception {
        System.out.println("zh.CN:");
        Locale.setDefault(new Locale("zh", "CN"));
        test();
        System.out.println("=====================================================================");
        System.out.println("en.US:");
        Locale.setDefault(new Locale("en", "US"));
        Messages.reBundle();
        test();
    }


    private void test() throws Exception {
        for (Method m : Messages.class.getMethods()) {
            if (Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers())
                    && !"getString".equals(m.getName()) && !"reBundle".equals(m.getName())) {

                System.out.println(m.getName().concat("()"));

                Object[] params = new Object[m.getParameterTypes().length];
                int idx = 0;
                for (Class<?> c : m.getParameterTypes()) {
                    if (ParamField.class.isAssignableFrom(c)) {
                        ParamField obj = new ParamField();
                        obj.setName("name");
                        params[idx++] = obj;

                    } else if (Class.class.isAssignableFrom(c)) {
                        params[idx++] = String.class;

                    } else if (Integer.class.isAssignableFrom(c)) {
                        params[idx++] = 6;

                    } else if (String.class.isAssignableFrom(c)) {
                        params[idx++] = "X";

                    } else if ("int".equals(c.getName())) {
                        params[idx++] = 6;

                    } else {
                        throw new Exception("unknown ".concat(c.getName()));

                    }
                }

                String str = String.valueOf(m.invoke(null, params));
                System.out.println("\t".concat(str));
                Assert.assertFalse(m.getName().concat("() There is no corresponding content in the configuration file"),
                        str.startsWith("!") && str.endsWith("!"));
            }
        }
    }

}
