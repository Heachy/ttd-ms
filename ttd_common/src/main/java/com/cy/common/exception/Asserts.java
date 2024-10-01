package com.cy.common.exception;

import com.cy.common.api.IStatusCode;

/**
 * @author Haechi
 */
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IStatusCode statusCode) {
        throw new ApiException(statusCode);
    }
}
