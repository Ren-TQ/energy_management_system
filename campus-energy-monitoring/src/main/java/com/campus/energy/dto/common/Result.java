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
// ============================================
// 设计模式：Builder Pattern（建造者模式）
// ============================================
// 
// 模式类型：创建型设计模式
// 
// 模式说明：
// 将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。
// 在本项目中，Result对象用于统一API响应格式，使用建造者模式可以更清晰地创建响应对象。
// 
// 在此项目中的应用：
// - 使用Lombok的@Builder注解自动生成建造者
// - 通过Result.builder()创建Result对象
// - 主要用于Controller层返回统一响应格式
// 
// 使用场景：
// 1. 成功响应：Result.success(data) - 静态方法内部使用builder()
// 2. 错误响应：Result.error(code, message) - 静态方法内部使用builder()
// 3. 自定义响应：Result.builder().code(200).message("成功").data(data).build()
// 
// 使用示例：
// // 成功响应
// return Result.success(deviceList);
// 
// // 错误响应
// return Result.error(400, "参数错误");
// 
// // 自定义响应（使用建造者模式）
// return Result.<DeviceDTO>builder()
//         .code(200)
//         .message("操作成功")
//         .data(deviceDTO)
//         .timestamp(System.currentTimeMillis())
//         .build();
// 
// 优势：
// 1. 链式调用：代码可读性强，对象创建过程清晰
// 2. 参数可选：可以只设置需要的字段
// 3. 避免构造函数参数过多：Result有多个字段，构造函数会很复杂
// 4. 统一响应格式：所有API接口使用相同的响应结构
// 
// 代码位置：
// - 使用位置：所有Controller方法的返回值
// ============================================
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
     * 
     * ============================================
     * 设计模式：Builder Pattern（建造者模式）
     * ============================================
     * 
     * 建造者模式：使用链式调用创建Result对象
     * 
     * 使用示例：
     * return Result.success();  // 无数据成功响应
     * 
     * 内部实现：
     * Result.<T>builder()
     *     .code(200)
     *     .message("操作成功")
     *     .timestamp(System.currentTimeMillis())
     *     .build();
     * 
     * 建造者模式优势：
     * - 链式调用，代码简洁
     * - 统一响应格式
     * ============================================
     */
    public static <T> Result<T> success() {
        // ============================================
        // 建造者模式：使用链式调用创建Result对象
        // ============================================
        return Result.<T>builder()
                .code(200)  // 设置响应码：200表示成功
                .message("操作成功")  // 设置响应消息
                .timestamp(System.currentTimeMillis())  // 设置时间戳
                .build();  // 构建Result对象
    }
    
    /**
     * 成功响应（带数据）
     * 
     * ============================================
     * 设计模式：Builder Pattern（建造者模式）
     * ============================================
     * 
     * 建造者模式：使用链式调用创建Result对象
     * 
     * 使用示例：
     * return Result.success(deviceList);  // 返回设备列表
     * 
     * 内部实现：
     * Result.<T>builder()
     *     .code(200)
     *     .message("操作成功")
     *     .data(data)  // 设置响应数据
     *     .timestamp(System.currentTimeMillis())
     *     .build();
     * 
     * @param data 响应数据，可以是任何类型
     * @return Result对象，包含成功响应信息
     * ============================================
     */
    public static <T> Result<T> success(T data) {
        // ============================================
        // 建造者模式：使用链式调用创建Result对象
        // ============================================
        return Result.<T>builder()
                .code(200)  // 设置响应码：200表示成功
                .message("操作成功")  // 设置响应消息
                .data(data)  // 设置响应数据
                .timestamp(System.currentTimeMillis())  // 设置时间戳
                .build();  // 构建Result对象
    }
    
    /**
     * 成功响应（带消息和数据）
     * 
     * ============================================
     * 设计模式：Builder Pattern（建造者模式）
     * ============================================
     * 
     * 建造者模式：使用链式调用创建Result对象
     * 
     * 使用示例：
     * return Result.success("查询成功", deviceList);
     * 
     * @param message 自定义响应消息
     * @param data 响应数据
     * @return Result对象，包含成功响应信息
     * ============================================
     */
    public static <T> Result<T> success(String message, T data) {
        // ============================================
        // 建造者模式：使用链式调用创建Result对象
        // ============================================
        return Result.<T>builder()
                .code(200)  // 设置响应码：200表示成功
                .message(message)  // 设置自定义响应消息
                .data(data)  // 设置响应数据
                .timestamp(System.currentTimeMillis())  // 设置时间戳
                .build();  // 构建Result对象
    }
    
    /**
     * 失败响应
     * 
     * ============================================
     * 设计模式：Builder Pattern（建造者模式）
     * ============================================
     * 
     * 建造者模式：使用链式调用创建Result对象
     * 
     * 使用示例：
     * return Result.error(400, "参数错误");
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return Result对象，包含错误响应信息
     * ============================================
     */
    public static <T> Result<T> error(Integer code, String message) {
        // ============================================
        // 建造者模式：使用链式调用创建Result对象
        // ============================================
        return Result.<T>builder()
                .code(code)  // 设置错误码
                .message(message)  // 设置错误消息
                .timestamp(System.currentTimeMillis())  // 设置时间戳
                .build();  // 构建Result对象
    }
    
    /**
     * 失败响应（带数据）
     * 
     * ============================================
     * 设计模式：Builder Pattern（建造者模式）
     * ============================================
     * 
     * 建造者模式：使用链式调用创建Result对象
     * 
     * 使用示例：
     * return Result.error(400, "参数错误", errorList);
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param data 错误数据（如验证错误列表）
     * @return Result对象，包含错误响应信息
     * ============================================
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

