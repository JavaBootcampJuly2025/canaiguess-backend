package com.canaiguess.api.config;

import com.canaiguess.api.model.User;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MockUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final User mockUser;

    public MockUserArgumentResolver(User mockUser) {
        this.mockUser = mockUser;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
        return mockUser;
    }
}
