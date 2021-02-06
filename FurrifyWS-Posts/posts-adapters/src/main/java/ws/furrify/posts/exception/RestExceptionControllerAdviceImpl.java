/*
 * MIT License
 * <p>
 * Copyright (c) 2019 Bruno Leite
 */
package ws.furrify.posts.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ws.furrify.shared.exception.RestExceptionControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionControllerAdviceImpl extends RestExceptionControllerAdvice {
}