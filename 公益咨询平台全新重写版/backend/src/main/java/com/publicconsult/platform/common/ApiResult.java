package com.publicconsult.platform.common;

public record ApiResult<T>(boolean success, String message, T data) {
    public static <T> ApiResult<T> ok(T data) { return new ApiResult<>(true, "操作成功", data); }
    public static <T> ApiResult<T> ok(String message, T data) { return new ApiResult<>(true, message, data); }
    public static <T> ApiResult<T> fail(String message) { return new ApiResult<>(false, message, null); }
}
