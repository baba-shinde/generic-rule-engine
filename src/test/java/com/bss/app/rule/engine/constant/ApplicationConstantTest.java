package com.bss.app.rule.engine.constant;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationConstantTest {

    @Test
    void constantsHaveExpectedValues() {
        assertEquals("suggestion", ApplicationConstant.SUGGESTION_COLUMN_NAME);
        assertEquals("result", ApplicationConstant.RESULT_COLUMN_NAME);
        assertNotNull(ApplicationConstant.SUGGESTION_COLUMN_NAME);
        assertNotNull(ApplicationConstant.RESULT_COLUMN_NAME);
        assertFalse(ApplicationConstant.SUGGESTION_COLUMN_NAME.isEmpty());
        assertFalse(ApplicationConstant.RESULT_COLUMN_NAME.isEmpty());
    }

    @Test
    void fieldsArePublicStaticFinal() throws NoSuchFieldException {
        Field f1 = ApplicationConstant.class.getField("SUGGESTION_COLUMN_NAME");
        Field f2 = ApplicationConstant.class.getField("RESULT_COLUMN_NAME");

        assertEquals(String.class, f1.getType());
        assertEquals(String.class, f2.getType());

        int m1 = f1.getModifiers();
        int m2 = f2.getModifiers();

        assertTrue(Modifier.isPublic(m1) && Modifier.isStatic(m1) && Modifier.isFinal(m1));
        assertTrue(Modifier.isPublic(m2) && Modifier.isStatic(m2) && Modifier.isFinal(m2));
    }

    @Test
    void instantiateClassCoversConstructor() {
        ApplicationConstant ac = new ApplicationConstant();
        assertNotNull(ac);
    }
}
