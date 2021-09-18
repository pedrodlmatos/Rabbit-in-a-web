package com.ua.riaw.utils.error.exceptions;

import org.apache.commons.lang3.StringUtils;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(Class claSS, String username, Long id) {
        super(UnauthorizedAccessException.generateMessage(claSS.getSimpleName(), username, id));
    }

    private static String generateMessage(String entity, String username, Long id) {
        return String.format("%s doesn't have access to entity %s with id %s", username, StringUtils.capitalize(entity), id);

    }
}
