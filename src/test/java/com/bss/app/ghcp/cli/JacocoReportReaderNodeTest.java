package com.bss.app.ghcp.cli;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class JacocoReportReaderNodeTest {

    private Class<?> findNodeClass() throws Exception {
        Class<?> outer = JacocoReportReader.class;
        for (Class<?> c : outer.getDeclaredClasses()) {
            if ("Node".equals(c.getSimpleName())) return c;
        }
        throw new ClassNotFoundException("Inner class Node not found");
    }

    @Test
    void constructorStoresPathAndDepth() throws Exception {
        Class<?> nodeClass = findNodeClass();
        Constructor<?> ctor = nodeClass.getDeclaredConstructor(Path.class, int.class);
        ctor.setAccessible(true);

        Path p = Paths.get("/tmp/myfile.html");
        Object node = ctor.newInstance(p, 2);

        Field pathField = nodeClass.getDeclaredField("path");
        Field depthField = nodeClass.getDeclaredField("depth");
        pathField.setAccessible(true);
        depthField.setAccessible(true);

        Object storedPath = pathField.get(node);
        int storedDepth = depthField.getInt(node);

        assertEquals(p, storedPath, "path should be stored exactly as provided");
        assertEquals(2, storedDepth, "depth should be stored exactly as provided");

        // verify final modifiers
        assertTrue(Modifier.isFinal(pathField.getModifiers()), "path field should be final");
        assertTrue(Modifier.isFinal(depthField.getModifiers()), "depth field should be final");
    }

    @Test
    void constructorAcceptsNullPathAndNegativeDepth() throws Exception {
        Class<?> nodeClass = findNodeClass();
        Constructor<?> ctor = nodeClass.getDeclaredConstructor(Path.class, int.class);
        ctor.setAccessible(true);

        Object node = ctor.newInstance(null, -5);

        Field pathField = nodeClass.getDeclaredField("path");
        Field depthField = nodeClass.getDeclaredField("depth");
        pathField.setAccessible(true);
        depthField.setAccessible(true);

        Object storedPath = pathField.get(node);
        int storedDepth = depthField.getInt(node);

        assertNull(storedPath, "path should be null when constructed with null");
        assertEquals(-5, storedDepth, "negative depth values should be preserved");
    }

    @Test
    void distinctInstancesAreDifferentObjects() throws Exception {
        Class<?> nodeClass = findNodeClass();
        Constructor<?> ctor = nodeClass.getDeclaredConstructor(Path.class, int.class);
        ctor.setAccessible(true);

        Object a = ctor.newInstance(Paths.get("/a"), 1);
        Object b = ctor.newInstance(Paths.get("/a"), 1);

        assertNotSame(a, b, "Two constructor calls should produce distinct instances");
    }
}
