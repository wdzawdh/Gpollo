package com.halfish.core.annotations.utils;

import java.security.MessageDigest;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * @author cw
 * @date 2017/12/21
 */
public class GpollpUtil {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String split(List<String> list, String separator) {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuffer.append("\"").append(list.get(i)).append("\"");
            if (i != list.size() - 1) {
                stringBuffer.append(separator);
            }
        }
        return stringBuffer.toString();
    }

    public static Class<?> parseVariableClass(TypeMirror typeMirror) {
        String type = parseVariableType(typeMirror);
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Gpollp报错:" + e.toString());
        }
    }

    /**
     * 返回TypeMirror的类型
     *
     * @param typeMirror VariableElement
     * @return String
     */
    public static String parseVariableType(TypeMirror typeMirror) {
        TypeKind typeKind = typeMirror.getKind();
        switch (typeKind) {
            case BOOLEAN:
                return "java.lang.Boolean";
            case BYTE:
                return "java.lang.Byte";
            case SHORT:
                return "java.lang.Short";
            case INT:
                return "java.lang.Integer";
            case LONG:
                return "java.lang.Long";
            case CHAR:
                return "java.lang.Character";
            case FLOAT:
                return "java.lang.Float";
            case DOUBLE:
                return "java.lang.Double";
            default:
                if (typeMirror instanceof DeclaredType) {
                    return handleGenericTypeVariable(typeMirror);
                }
                return typeMirror.toString();
        }
    }

    /**
     * List<User> return java.util.List.class ,rather than java.util.List<User>.class
     *
     * @return String
     */
    public static String handleGenericTypeVariable(TypeMirror typeMirror) {
        DeclaredType declaredType = (DeclaredType) typeMirror;
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        return typeElement.getQualifiedName().toString();
    }

    /**
     * encode By MD5
     *
     * @param str
     * @return String
     */
    public static String md5(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            return new String(encodeHex(messageDigest.digest()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data a byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     */
    public static char[] encodeHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }
}
