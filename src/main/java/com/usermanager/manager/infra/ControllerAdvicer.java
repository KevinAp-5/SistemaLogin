package com.usermanager.manager.infra;


import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.usermanager.manager.dto.ResponseMessage;
import com.usermanager.manager.exception.UserDoesNotExistException;
import com.usermanager.manager.exception.UserExistsException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerAdvicer {

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ResponseMessage> handleUserExistsException(UserExistsException ex) {
        return ResponseEntity.status(409).body(new ResponseMessage("User already exists: " + ex.getMessage()));
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<ResponseMessage> handleUserDoesNotExistsException(UserDoesNotExistException ex) {
        return ResponseEntity.status(404).body(new ResponseMessage("User does not exists " + ex.getMessage()));
    }

}
