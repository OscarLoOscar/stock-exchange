package com.example.coin_exchange.exception;

import java.util.Objects;
import org.springframework.validation.BindingResult;
import com.example.coin_exchange.exception.exceptionEnum.Syscode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResp<T> {
  private String syscode;
  private String message;
  private T data;
  private ErrorDetails error;

  public static <T> ApiResponseBuilder<T> builder() {
    return new ApiResponseBuilder<>();
  }

  @Getter
  public static class ApiResponseBuilder<T> {
    private String syscode;
    private String message;
    private T data;
    private ErrorDetails error;

    public ApiResponseBuilder<T> status(Syscode syscode) {
      this.syscode = syscode.getSyscode();
      this.message = syscode.getMessage();
      return this;
    }

    public ApiResponseBuilder<T> concatMessageIfPresent(String str) {
      if (this.message != null && str != null)
        this.message += " " + str;
      return this;
    }

    public ApiResponseBuilder<T> ok() {
      this.syscode = Syscode.OK.getSyscode();
      this.message = Syscode.OK.getMessage();
      return this;
    }

    public ApiResponseBuilder<T> error() {
      this.syscode = Syscode.INVALID_INPUT.getSyscode();
      this.message = Syscode.INVALID_INPUT.getMessage();
      return this;
    }

    public ApiResponseBuilder<T> error(Syscode syscode) {
      this.syscode = syscode.getSyscode();
      this.message = syscode.getMessage();
      return this;
    }

    public ApiResponseBuilder<T> error(Syscode syscode,
        BindingResult bindingResult) {
      this.syscode = syscode.getSyscode();
      String fieldError =
          Objects.requireNonNull(bindingResult.getFieldError()).getField();
      String errorCode = bindingResult.getFieldError().getCode();
      this.error = new ErrorDetails(fieldError, errorCode);
      this.message = String.format("Validation error for field: %s,error: %s",
          fieldError, errorCode);
      return this;
    }

    public ApiResponseBuilder<T> syscode(String statusCode) {
      this.syscode = statusCode;
      return this;
    }

    public ApiResponseBuilder<T> data(T data) {
      this.data = data;
      return this;
    }

    public ApiResponseBuilder<T> message(String message) {
      this.message = message;
      return this;
    }

    public ApiResp<T> build() {
      if (this.syscode == null || this.syscode.isEmpty()
          || this.message == null) {
        throw new IllegalArgumentException(
            "Status code and message must be provided");
      }
      return new ApiResp<>(this.syscode, this.message, this.data, this.error);
    }
  }
  @Getter
  @ToString
  public static class ErrorDetails {
    private String field;
    private String errorCode;

    public ErrorDetails(String field, String errorCode) {
      this.field = field;
      this.errorCode = errorCode;
    }
  }

}
