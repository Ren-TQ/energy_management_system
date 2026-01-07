package com.campus.energy.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================
 * MVC架构 - View层（视图层）
 * ============================================
 * 
 * 架构说明：
 * 本类属于MVC架构中的View（V）层，负责：
 * 1. 统一API响应格式
 * 2. 封装返回给客户端的数据
 * 3. 在RESTful API中，View层是JSON响应格式
 * 
 * MVC职责划分：
 * - Controller (C): DeviceController等 - 接收请求
 * - Model (M): Service、Repository、Entity - 处理业务和数据
 * - View (V): 本类（Result<T>）- JSON响应格式
 * 
 * 数据流转：
 * Controller → 调用Service → 获取DTO数据 → 封装为Result<T> → JSON响应（View层）
 * 
 * 说明：
 * 在传统MVC中，View是HTML页面；在RESTful API中，View是JSON格式的响应数据
 * 本类统一封装了所有API的响应格式，确保前端接收到的数据结构一致
 * ============================================
 * 
 * 统一API响应结果封装
 * 
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一响应结果")
public class Result<T> {
    
    @Schema(description = "响应码", example = "200")
    private Integer code;
    
    @Schema(description = "响应消息", example = "操作成功")
    private String message;
    
    @Schema(description = "响应数据")
    private T data;
    
    @Schema(description = "时间戳")
    private Long timestamp;
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(200)
                .message("操作成功")
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return Result.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 失败响应（带数据）
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 参数错误
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }
    
    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }
    
    /**
     * 禁止访问
     */
    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }
    
    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }
    
    /**
     * 服务器内部错误
     */
    public static <T> Result<T> serverError(String message) {
        return error(500, message);
    }
}

