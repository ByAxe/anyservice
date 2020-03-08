package com.anyservice.web.advices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Locale;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
    @Autowired
    private MessageSource messageSource;

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public VndErrors illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        return new VndErrors("error", ex.getMessage() == null ? messageSource.getMessage("global.controller.exception.handler.illegal.argument.exception", null, LocaleContextHolder.getLocale()) : messageSource.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(NullPointerException.class)
    public VndErrors notFoundExceptionHandler(NullPointerException ex) {
        return new VndErrors("error", messageSource.getMessage(ex.getLocalizedMessage() == null ? "global.controller.exception.handler.not.found.exception.handler" : ex.getMessage(), null, LocaleContextHolder.getLocale()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(UnsupportedOperationException.class)
    public VndErrors notFoundExceptionHandler(UnsupportedOperationException ex) {
        return new VndErrors("error", ex.getMessage() == null ? messageSource.getMessage("global.controller.exception.handler.not.found.exception.handler.not.supported", null, LocaleContextHolder.getLocale()) : ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public VndErrors httpMessageNotReadableExceptionHandler(HttpClientErrorException.Forbidden ex) {
        return new VndErrors("error", ex.getMessage() == null ? messageSource.getMessage("global.controller.exception.handler.unknown.exception.handler", null, LocaleContextHolder.getLocale()) : ex.getMessage());
    }


    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public VndErrors unknownExceptionHandler(Exception ex) {
        logger.error(messageSource.getMessage("global.controller.exception.handler.unknown.exception.handler", null, Locale.getDefault()), ex);
        return new VndErrors("error", ex.getMessage() == null ? messageSource.getMessage("global.controller.exception.handler.unknown.exception.handler", null, LocaleContextHolder.getLocale()) : ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public VndErrors httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex) {
        return new VndErrors("error", ex.getMessage());
    }

}
