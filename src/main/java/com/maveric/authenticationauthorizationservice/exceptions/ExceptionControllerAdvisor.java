package com.maveric.authenticationauthorizationservice.exceptions;
import com.maveric.authenticationauthorizationservice.dto.ErrorDto;
import feign.FeignException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static com.maveric.authenticationauthorizationservice.constants.Constants.*;

@RestControllerAdvice
public class ExceptionControllerAdvisor {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ExceptionControllerAdvisor.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(BAD_REQUEST_CODE);
        errorDto.setMessage(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        log.error("{}->{}->{}",BAD_REQUEST_CODE,ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),ex.getMessage());
        return errorDto;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDto handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(METHOD_NOT_ALLOWED_CODE);
        errorDto.setMessage(METHOD_NOT_ALLOWED_MESSAGE);
        log.error("{}->{}",METHOD_NOT_ALLOWED_CODE,METHOD_NOT_ALLOWED_MESSAGE);
        return errorDto;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto handleHttpRequestInvalidCredentialsException(
            InvalidCredentialsException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(NOT_AUTHORIZED_CODE);
        errorDto.setMessage(NOT_AUTHORIZED_MESSAGE);
        log.error("{} -> {}",NOT_AUTHORIZED_CODE,NOT_AUTHORIZED_MESSAGE);
        return errorDto;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleHttpRequestUserNotFoundException(
            UserNotFoundException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(USER_NOT_FOUND_CODE);
        errorDto.setMessage(USER_NOT_FOUND_MESSAGE);
        log.error("{} ->{}",USER_NOT_FOUND_CODE,USER_NOT_FOUND_MESSAGE);
        return errorDto;
    }

    @ExceptionHandler(AccountCreationFailedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDto handleHttpRequestAccountCreationFailedException(
            AccountCreationFailedException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(ACCOUNT_CREATION_FAILED_CODE);
        errorDto.setMessage(ACCOUNT_CREATION_FAILED_MESSAGE);
        log.error("{}-> {}",ACCOUNT_CREATION_FAILED_CODE,ACCOUNT_CREATION_FAILED_MESSAGE);
        return errorDto;
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDto handleHttpFeignException(
            FeignException ex) {
        ErrorDto errorDto = new ErrorDto();
        if(ex.status()==503) {
            errorDto.setCode(SERVICE_UNAVAILABLE_CODE);
            errorDto.setMessage(SERVICE_UNAVAILABLE_MESSAGE);
            log.error("{}->{} ->{}",SERVICE_UNAVAILABLE_CODE,SERVICE_UNAVAILABLE_MESSAGE,ex.getMessage());
        }
        else if(ex.status()==400)
        {
            errorDto.setCode(BAD_REQUEST_CODE);
            String message = ex.getLocalizedMessage();
            JSONObject jsonObject = new JSONObject (message.substring(message.indexOf("{"),message.indexOf("}")+1));
            errorDto.setMessage(jsonObject.get("message").toString());
            log.error("{}- >{}",BAD_REQUEST_CODE,ex.getMessage());
        }
        else {
            errorDto.setCode(INTERNAL_SERVER_ERROR_CODE);
            errorDto.setMessage(INTERNAL_SERVER_ERROR_MESSAGE);
            log.error("{}-> {}->{}",INTERNAL_SERVER_ERROR_CODE,INTERNAL_SERVER_ERROR_MESSAGE,ex.getMessage());
        }

        return errorDto;
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDto handleHttpInternalAuthenticationServiceException(
            InternalAuthenticationServiceException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(SERVICE_UNAVAILABLE_CODE);
        errorDto.setMessage(SERVICE_UNAVAILABLE_MESSAGE);
        log.error("{}-{}-{}",SERVICE_UNAVAILABLE_CODE,SERVICE_UNAVAILABLE_MESSAGE,ex.getMessage());
        return errorDto;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ErrorDto handleOtherHttpException(Exception exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(INTERNAL_SERVER_ERROR_CODE);
        errorDto.setMessage(INTERNAL_SERVER_ERROR_MESSAGE);
        log.error("{}--{}--{}",INTERNAL_SERVER_ERROR_CODE,INTERNAL_SERVER_ERROR_MESSAGE,exception.getMessage());
        return errorDto;
    }




}
