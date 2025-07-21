package com.canaiguess.api.annotation;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') or @gameSecurity.isOwner(#gameId, authentication)")
public @interface CanAccessGame { }

