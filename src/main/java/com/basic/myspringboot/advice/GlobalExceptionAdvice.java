package com.basic.myspringboot.advice;

import com.basic.myspringboot.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

//@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    // 공통 타임스탬프 포맷터
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss E a", Locale.KOREA);

    /**
     * ProblemDetail에 공통 속성(Timestamp)을 추가하는 편의 메서드
     */
    private void addCommonAttributes(ProblemDetail pd) {
        pd.setProperty("timestamp", formatter.format(LocalDateTime.now()));
    }

    // 400 - 요청 파라미터 누락
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(status, "필수 파라미터가 누락되었습니다: " + ex.getParameterName());
        pd.setTitle("Missing Parameter");
        addCommonAttributes(pd);

        log.error(ex.getMessage(), ex);
        return createResponseEntity(pd, headers, status, request);
    }

    // 400 - 요청 바디 파싱 실패
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(status, "요청 데이터 형식이 올바르지 않습니다.");
        pd.setTitle("Message Not Readable");
        addCommonAttributes(pd);

        log.error(ex.getMessage(), ex);
        return createResponseEntity(pd, headers, status, request);
    }

    // 400 - 유효성 검사 실패 (@Valid)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, "입력값 유효성 검사에 실패했습니다.");
        pd.setTitle("Validation Failed");
        pd.setProperty("errors", errors); // 상세 에러 목록 추가
        addCommonAttributes(pd);

        log.error(ex.getMessage(), ex);
        return createResponseEntity(pd, headers, status, request);
    }

    // 404 - 리소스 없음
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(status, "요청한 경로를 찾을 수 없습니다: " + ex.getResourcePath());
        pd.setTitle("Resource Not Found");
        addCommonAttributes(pd);

        return createResponseEntity(pd, headers, status, request);
    }

    // 405 - 지원하지 않는 Method
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(status, "지원하지 않는 HTTP Method입니다.");
        pd.setTitle("Method Not Supported");
        pd.setProperty("supportedMethods", ex.getSupportedHttpMethods());
        addCommonAttributes(pd);

        return createResponseEntity(pd, headers, status, request);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageConversionException.class)
    protected ResponseEntity<Object> handleHttpMessageConversionException(
            org.springframework.http.converter.HttpMessageConversionException ex,
            WebRequest request) {

        log.error("메시지 변환 오류 발생: {}", ex.getMessage());

        // 400 Bad Request로 응답할지, 500으로 할지 결정 (보통 400 권장)
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, "서버에서 데이터를 처리하는 중 타입 정의 오류가 발생했습니다.");
        pd.setTitle("Message Conversion Error");
        pd.setDetail(ex.getMostSpecificCause().getMessage()); // 구체적인 원인(HttpMethod 관련 메시지) 출력
        addCommonAttributes(pd);

        return createResponseEntity(pd, new HttpHeaders(), status, request);
    }

    // 비즈니스 예외 처리 (커스텀)
    @ExceptionHandler(BusinessException.class)
    protected ProblemDetail handleBusinessException(BusinessException e) {
        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(e.getHttpStatus(), e.getMessage());
        pd.setTitle("Business Logic Error");
        pd.setType(URI.create("/docs/errors/business-exception")); // 예시 URI
        addCommonAttributes(pd);

        log.error("Business Exception: {}", e.getMessage());
        return pd;
    }

    // 500 - 최상위 예외 처리
    @ExceptionHandler(Exception.class)
    protected ProblemDetail handleGeneralException(Exception e) {
        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
        pd.setTitle("Internal Server Error");
        addCommonAttributes(pd);

        log.error("Unhandled Exception: ", e);
        return pd;
    }
}