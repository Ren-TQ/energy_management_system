package com.campus.energy.pattern.strategy;

import com.campus.energy.entity.Alert;
import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.enums.AlertType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ============================================
 * 设计模式：Strategy Pattern（策略模式）- 具体策略实现
 * ============================================
 * 
 * 角色：ConcreteStrategy（具体策略）
 * 
 * 职责：检测设备电压异常情况，当电压偏离标准电压220V超过±10%时触发告警
 * 
 * 策略模式中的位置：
 * - 实现Strategy接口：AlertStrategy
 * - 被Context使用：AlertService会遍历所有策略，调用此策略的checkAlert()方法
 * - Spring自动管理：使用@Component注解，Spring会自动创建单例并注入到AlertService
 * 
 * 判断逻辑：
 * 1. 电压正常范围：198V（220V的90%）- 242V（220V的110%）
 * 2. 检查电压过低：如果 voltage < 198V，触发电压过低告警
 * 3. 检查电压过高：如果 voltage > 242V，触发电压过高告警
 * 4. 如果电压在正常范围内，返回Optional.empty()
 * 
 * 配置说明：
 * - 电压下限通过application.yml配置：alert.voltage.min（默认198V）
 * - 电压上限通过application.yml配置：alert.voltage.max（默认242V）
 * - 可在配置文件中修改，无需修改代码
 * 
 * 告警类型：
 * - VOLTAGE_LOW：电压过低告警（< 198V）
 * - VOLTAGE_HIGH：电压过高告警（> 242V）
 * 
 * 使用场景：
 * - 监控电网电压稳定性
 * - 防止电压异常导致设备损坏
 * - 及时发现电力系统故障
 * 
 * 扩展说明：
 * 如果需要修改判断逻辑（如改为动态阈值、考虑不同设备类型等），只需修改此类的checkAlert()方法，
 * 不影响其他策略和AlertService的代码。
 * ============================================
 */
@Component  // Spring自动管理，单例模式，自动注入到AlertService的alertStrategies列表
public class VoltageAbnormalAlertStrategy implements AlertStrategy {
    
    /**
     * 标准电压值
     * 
     * 中国电网标准电压：220V
     * 用于计算正常电压范围（±10%）
     */
    private static final double STANDARD_VOLTAGE = 220.0;
    
    /**
     * 电压下限（最低允许电压）
     * 
     * 配置说明：
     * - 从application.yml读取：alert.voltage.min
     * - 默认值：198V（220V的90%）
     * - 含义：当电压低于此值时，触发电压过低告警
     */
    @Value("${alert.voltage.min:198}")
    private double minVoltage;
    
    /**
     * 电压上限（最高允许电压）
     * 
     * 配置说明：
     * - 从application.yml读取：alert.voltage.max
     * - 默认值：242V（220V的110%）
     * - 含义：当电压高于此值时，触发电压过高告警
     */
    @Value("${alert.voltage.max:242}")
    private double maxVoltage;
    
    /**
     * 策略模式核心方法：检查电压是否异常
     * 
     * 执行流程：
     * 1. 获取当前电压值
     * 2. 检查电压是否过低（< minVoltage）
     * 3. 检查电压是否过高（> maxVoltage）
     * 4. 如果异常，构建对应类型的告警对象并返回
     * 5. 如果正常，返回空
     * 
     * 策略模式体现：
     * - 此方法封装了电压异常的判断算法
     * - AlertService调用此方法时，不需要知道具体的判断逻辑
     * - 可以独立替换或扩展，不影响其他策略
     * 
     * @param device 设备信息，包含设备ID、名称等
     * @param energyData 能耗数据，包含实时电压、电流、功率等
     * @return Optional<Alert>
     *         - 如果电压异常，返回包含告警对象的Optional（类型为VOLTAGE_LOW或VOLTAGE_HIGH）
     *         - 如果电压正常，返回Optional.empty()
     */
    @Override
    public Optional<Alert> checkAlert(Device device, EnergyData energyData) {
        // ============================================
        // 步骤1：获取当前电压值
        // ============================================
        double voltage = energyData.getVoltage();
        
        // ============================================
        // 步骤2：检查电压过低
        // 策略模式：封装判断逻辑，独立于调用方
        // ============================================
        if (voltage < minVoltage) {
            // ============================================
            // 构建电压过低告警对象
            // 设计模式：Builder Pattern（建造者模式）
            // ============================================
            // 
            // 建造者模式：使用链式调用创建Alert对象
            // 
            // 建造者模式优势：
            // - 链式调用：代码可读性强，对象创建过程清晰
            // - 参数可选：可以只设置需要的属性
            // - 避免构造函数参数过多
            // ============================================
            Alert alert = Alert.builder()
                    .device(device)  // 关联设备对象
                    .alertType(AlertType.VOLTAGE_LOW)  // 告警类型：电压过低
                    .alertValue(voltage)  // 当前电压值
                    .thresholdValue(minVoltage)  // 告警阈值（电压下限）
                    .description(String.format(
                            // 告警描述：包含详细信息，便于排查问题
                            "设备[%s]电压过低告警：当前电压 %.2fV，低于正常范围下限 %.2fV（标准电压 %.0fV的90%%）",
                            device.getName(),      // 设备名称
                            voltage,               // 当前电压
                            minVoltage,            // 电压下限
                            STANDARD_VOLTAGE       // 标准电压
                    ))
                    .triggerTime(LocalDateTime.now())  // 触发时间（当前时间）
                    .isResolved(false)  // 初始状态：未处理
                    .build();  // 构建Alert对象
            
            // ============================================
            // 返回电压过低告警
            // 策略模式：返回Optional，由调用方决定如何处理
            // ============================================
            return Optional.of(alert);
        }
        
        // ============================================
        // 步骤3：检查电压过高
        // 策略模式：封装判断逻辑，独立于调用方
        // ============================================
        if (voltage > maxVoltage) {
            // ============================================
            // 构建电压过高告警对象
            // 设计模式：Builder Pattern（建造者模式）
            // ============================================
            // 
            // 建造者模式：使用链式调用创建Alert对象
            // 
            // 建造者模式优势：
            // - 链式调用：代码可读性强，对象创建过程清晰
            // - 参数可选：可以只设置需要的属性
            // - 避免构造函数参数过多
            // ============================================
            Alert alert = Alert.builder()
                    .device(device)  // 关联设备对象
                    .alertType(AlertType.VOLTAGE_HIGH)  // 告警类型：电压过高
                    .alertValue(voltage)  // 当前电压值
                    .thresholdValue(maxVoltage)  // 告警阈值（电压上限）
                    .description(String.format(
                            // 告警描述：包含详细信息，便于排查问题
                            "设备[%s]电压过高告警：当前电压 %.2fV，超过正常范围上限 %.2fV（标准电压 %.0fV的110%%）",
                            device.getName(),      // 设备名称
                            voltage,               // 当前电压
                            maxVoltage,            // 电压上限
                            STANDARD_VOLTAGE       // 标准电压
                    ))
                    .triggerTime(LocalDateTime.now())  // 触发时间（当前时间）
                    .isResolved(false)  // 初始状态：未处理
                    .build();  // 构建Alert对象
            
            // ============================================
            // 返回电压过高告警
            // 策略模式：返回Optional，由调用方决定如何处理
            // ============================================
            return Optional.of(alert);
        }
        
        // ============================================
        // 电压正常，不触发告警
        // 策略模式：返回空，表示此策略未检测到异常
        // ============================================
        return Optional.empty();
    }
    
    /**
     * 获取策略名称
     * 
     * 用于日志记录和调试，标识当前策略
     * 
     * @return "电压异常告警策略"
     */
    @Override
    public String getStrategyName() {
        return "电压异常告警策略";
    }
}

