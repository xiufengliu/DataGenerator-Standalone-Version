package org.iss4e.datagen.common;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * The class is used to get the StackTrace of an Exception in String for
 * Printing and (or) Logging purpose.<br>The idea behind developing this class
 * is that Java's native
 * <code>printStackTrace</code> method is being discouraged in NetBeans.<br>So
 * i decided to write my own StackTracer Class!
 *
 * @author S!LENT W@RRIOR
 */
public final class StackTracer {

    private StackTracer() {
    }

    /**
     * Gets a Throwable object (Usually an Exception or child of an Exception
     * Class) and returns back the stack trace of an Exception as a String
     *
     * @param throwable The Throwable or Exception object. It is advised to give
     * the Exception object as Throwable input parameter
     * @return A String containing the StackTrace
     */
    public static String getStackTrace(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }

    /**
     * Gets a Throwable object (Usually an Exception or child of an Exception
     * Class) and returns back the stack trace of an Exception as a String in a
     * more Formatted manner
     *
     * @param throwable The Throwable or Exception object. It is advised to give
     * the Exception object as Throwable input parameter
     * @return A String containing the StackTrace in a more Formatted way!
     */
    public static String getFormattedStackTrace(Throwable throwable) {
        StringBuilder stringedStackTrace = new StringBuilder();
        stringedStackTrace.append("Root Cause of this Exception is:\n\t");
        stringedStackTrace.append(throwable.toString());
        stringedStackTrace.append("\n--------------------------------------------------------------------------------------\n");
        stringedStackTrace.append("\t\t\tStack Trace of this Exception is as follows...\n");
        int exceptionNumber;
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            exceptionNumber = i + 1;
            stringedStackTrace.append("Exception ").append(exceptionNumber).append(" occurred at:\n\t");
            stringedStackTrace.append("Class: ");
            stringedStackTrace.append(stackTrace[i].getClassName());
            stringedStackTrace.append("\n\tMethod: ");
            stringedStackTrace.append(stackTrace[i].getMethodName());
            stringedStackTrace.append("\n\tLine # ");
            stringedStackTrace.append(stackTrace[i].getLineNumber());
            stringedStackTrace.append("\n");
        }
        return stringedStackTrace.toString();
    }

    /**
     * Gets a Throwable object (Usually an Exception or child of an Exception
     * Class) and prints the Stack Trace of the given Throwable
     *
     * @param throwable The Throwable or Exception object. It is advised to give
     * the Exception object as Throwable input parameter
     */
    public static void printStackTrace(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        System.err.println(writer.toString());
    }

    /**
     * Gets a Throwable object (Usually an Exception or child of an Exception
     * Class) and prints the Stack Trace of the given Throwable in a more
     * formatted way!
     *
     * @param throwable The Throwable or Exception object. It is advised to give
     * the Exception object as Throwable input parameter
     */
    public static void printFormattedStackTrace(Throwable throwable) {
        System.err.print("Root Cause of this Exception is:\n\t");
        System.err.print(throwable.toString());
        System.err.print("\n--------------------------------------------------------------------------------------\n");
        System.err.print("\t\t\tStack Trace of this Exception is as follows...\n");
        int exceptionNumber;
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            exceptionNumber = i + 1;
            System.err.print("Exception ");
            System.err.print(exceptionNumber);
            System.err.print(" occurred at:\n\t");
            System.err.print("Class: ");
            System.err.print(stackTrace[i].getClassName());
            System.err.print("\n\tMethod: ");
            System.err.print(stackTrace[i].getMethodName());
            System.err.print("\n\tLine # ");
            System.err.print(stackTrace[i].getLineNumber());
            System.err.println();
        }
    }
}