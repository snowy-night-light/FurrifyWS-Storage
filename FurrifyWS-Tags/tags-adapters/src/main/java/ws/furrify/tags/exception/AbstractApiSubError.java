/*
  MIT License
  <p>
  Copyright (c) 2019 Bruno Leite
 */
package ws.furrify.tags.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

abstract class AbstractApiSubError {
}

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
class AbstractApiValidationError extends AbstractApiSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    AbstractApiValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}