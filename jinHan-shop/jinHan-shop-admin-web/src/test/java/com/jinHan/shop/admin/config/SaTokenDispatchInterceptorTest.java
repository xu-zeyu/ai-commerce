package com.jinHan.shop.admin.config;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaTokenDispatchInterceptorTest {

    @Test
    void shouldSkipAsyncDispatch() throws Exception {
        assertInternalDispatchSkipped(DispatcherType.ASYNC);
    }

    @Test
    void shouldSkipErrorDispatch() throws Exception {
        assertInternalDispatchSkipped(DispatcherType.ERROR);
    }

    @Test
    void shouldAuthenticateRequestDispatch() throws Exception {
        AtomicBoolean authCalled = new AtomicBoolean(false);
        SaTokenDispatchInterceptor interceptor = new SaTokenDispatchInterceptor(
                handler -> authCalled.set(true));
        interceptor.isAnnotation(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setDispatcherType(DispatcherType.REQUEST);

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertTrue(authCalled.get());
    }

    private void assertInternalDispatchSkipped(DispatcherType dispatcherType) throws Exception {
        AtomicBoolean authCalled = new AtomicBoolean(false);
        SaTokenDispatchInterceptor interceptor = new SaTokenDispatchInterceptor(
                handler -> authCalled.set(true));
        interceptor.isAnnotation(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setDispatcherType(dispatcherType);

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertFalse(authCalled.get());
    }
}
