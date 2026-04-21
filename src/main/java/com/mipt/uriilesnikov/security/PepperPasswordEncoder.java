package com.mipt.uriilesnikov.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PepperPasswordEncoder implements PasswordEncoder {

    private final PasswordEncoder delegate;
    private final String pepper;

    public PepperPasswordEncoder(PasswordEncoder delegate, String pepper) {
        this.delegate = delegate;
        this.pepper = pepper;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(withPepper(rawPassword));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return delegate.matches(withPepper(rawPassword), encodedPassword);
    }

    private String withPepper(CharSequence rawPassword) {
        return rawPassword + pepper;
    }
}
