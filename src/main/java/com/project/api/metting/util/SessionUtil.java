package com.project.api.metting.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

public class SessionUtil {

    public static void addAttribute(String name, Object value) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            attributes.setAttribute(name, value, RequestAttributes.SCOPE_SESSION);
        }
    }

    public static String getStringAttributeValue(String name) {
        Object value = getAttribute(name);
        return value != null ? value.toString() : null;
    }

    public static Object getAttribute(String name) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getAttribute(name, RequestAttributes.SCOPE_SESSION) : null;
    }
}
