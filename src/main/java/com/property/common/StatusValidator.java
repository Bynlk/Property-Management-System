package com.property.common;

import com.property.enums.*;

import java.util.Map;
import java.util.Set;

/**
 * 状态转换验证器
 * 定义各业务实体的合法状态转换路径，使用枚举确保类型安全
 */
public class StatusValidator {

    /**
     * 投诉状态转换: 待处理 → 处理中 → 已处理
     */
    private static final Map<ComplaintStatus, Set<ComplaintStatus>> COMPLAINT_TRANSITIONS = Map.of(
            ComplaintStatus.PENDING, Set.of(ComplaintStatus.PROCESSING),
            ComplaintStatus.PROCESSING, Set.of(ComplaintStatus.RESOLVED),
            ComplaintStatus.RESOLVED, Set.of()  // 终态，不可转换
    );

    /**
     * 报修状态转换: 待维修 → 维修中 → 已完成
     */
    private static final Map<RepairStatus, Set<RepairStatus>> REPAIR_TRANSITIONS = Map.of(
            RepairStatus.PENDING, Set.of(RepairStatus.IN_PROGRESS),
            RepairStatus.IN_PROGRESS, Set.of(RepairStatus.COMPLETED),
            RepairStatus.COMPLETED, Set.of()  // 终态，不可转换
    );

    /**
     * 房屋状态转换: 空置 ↔ 装修中 ↔ 已入住
     */
    private static final Map<HouseStatus, Set<HouseStatus>> HOUSE_TRANSITIONS = Map.of(
            HouseStatus.VACANT, Set.of(HouseStatus.RENOVATING, HouseStatus.OCCUPIED),
            HouseStatus.RENOVATING, Set.of(HouseStatus.VACANT, HouseStatus.OCCUPIED),
            HouseStatus.OCCUPIED, Set.of(HouseStatus.VACANT)
    );

    /**
     * 停车位状态转换: 空闲 ↔ 使用中
     */
    private static final Map<ParkingStatus, Set<ParkingStatus>> PARKING_TRANSITIONS = Map.of(
            ParkingStatus.IDLE, Set.of(ParkingStatus.IN_USE),
            ParkingStatus.IN_USE, Set.of(ParkingStatus.IDLE)
    );

    /**
     * 费用状态转换: 未缴 → 已缴（不可逆）
     */
    private static final Map<FeeStatus, Set<FeeStatus>> FEE_TRANSITIONS = Map.of(
            FeeStatus.UNPAID, Set.of(FeeStatus.PAID),
            FeeStatus.PAID, Set.of()  // 终态，不可逆
    );

    /**
     * 验证投诉状态转换（枚举类型重载）
     */
    public static void validateComplaintTransition(ComplaintStatus current, ComplaintStatus next) {
        if (current == null) {
            throw new BusinessException("投诉当前状态不能为空");
        }
        if (next == null) {
            throw new BusinessException("投诉目标状态不能为空");
        }
        validateTransition(current, next, COMPLAINT_TRANSITIONS, "投诉");
    }

    /**
     * 验证投诉状态转换（字符串类型，向后兼容）
     */
    public static void validateComplaintTransition(String currentStatus, String newStatus) {
        ComplaintStatus current = ComplaintStatus.fromValue(currentStatus);
        ComplaintStatus next = ComplaintStatus.fromValue(newStatus);
        if (current == null) {
            throw new BusinessException("投诉当前状态「" + currentStatus + "」不合法");
        }
        if (next == null) {
            throw new BusinessException("投诉目标状态「" + newStatus + "」不合法");
        }
        validateTransition(current, next, COMPLAINT_TRANSITIONS, "投诉");
    }

    /**
     * 验证报修状态转换（枚举类型重载）
     */
    public static void validateRepairTransition(RepairStatus current, RepairStatus next) {
        if (current == null) {
            throw new BusinessException("报修当前状态不能为空");
        }
        if (next == null) {
            throw new BusinessException("报修目标状态不能为空");
        }
        validateTransition(current, next, REPAIR_TRANSITIONS, "报修");
    }

    /**
     * 验证报修状态转换（字符串类型，向后兼容）
     */
    public static void validateRepairTransition(String currentStatus, String newStatus) {
        RepairStatus current = RepairStatus.fromValue(currentStatus);
        RepairStatus next = RepairStatus.fromValue(newStatus);
        if (current == null) {
            throw new BusinessException("报修当前状态「" + currentStatus + "」不合法");
        }
        if (next == null) {
            throw new BusinessException("报修目标状态「" + newStatus + "」不合法");
        }
        validateTransition(current, next, REPAIR_TRANSITIONS, "报修");
    }

    /**
     * 验证费用状态转换（枚举类型重载）
     */
    public static void validateFeeTransition(FeeStatus current, FeeStatus next) {
        if (current == null) {
            throw new BusinessException("费用当前状态不能为空");
        }
        if (next == null) {
            throw new BusinessException("费用目标状态不能为空");
        }
        validateTransition(current, next, FEE_TRANSITIONS, "费用");
    }

    /**
     * 验证费用状态转换（字符串类型，向后兼容）
     */
    public static void validateFeeTransition(String currentStatus, String newStatus) {
        FeeStatus current = FeeStatus.fromValue(currentStatus);
        FeeStatus next = FeeStatus.fromValue(newStatus);
        if (current == null) {
            throw new BusinessException("费用当前状态「" + currentStatus + "」不合法");
        }
        if (next == null) {
            throw new BusinessException("费用目标状态「" + newStatus + "」不合法");
        }
        validateTransition(current, next, FEE_TRANSITIONS, "费用");
    }

    /**
     * 验证房屋状态转换（枚举类型重载）
     */
    public static void validateHouseTransition(HouseStatus current, HouseStatus next) {
        if (current == null) {
            throw new BusinessException("房屋当前状态不能为空");
        }
        if (next == null) {
            throw new BusinessException("房屋目标状态不能为空");
        }
        validateTransition(current, next, HOUSE_TRANSITIONS, "房屋");
    }

    /**
     * 验证房屋状态转换（字符串类型，向后兼容）
     */
    public static void validateHouseTransition(String currentStatus, String newStatus) {
        HouseStatus current = HouseStatus.fromValue(currentStatus);
        HouseStatus next = HouseStatus.fromValue(newStatus);
        if (current == null) {
            throw new BusinessException("房屋当前状态「" + currentStatus + "」不合法");
        }
        if (next == null) {
            throw new BusinessException("房屋目标状态「" + newStatus + "」不合法");
        }
        validateTransition(current, next, HOUSE_TRANSITIONS, "房屋");
    }

    /**
     * 验证停车位状态转换（枚举类型重载）
     */
    public static void validateParkingTransition(ParkingStatus current, ParkingStatus next) {
        if (current == null) {
            throw new BusinessException("停车位当前状态不能为空");
        }
        if (next == null) {
            throw new BusinessException("停车位目标状态不能为空");
        }
        validateTransition(current, next, PARKING_TRANSITIONS, "停车位");
    }

    /**
     * 验证停车位状态转换（字符串类型，向后兼容）
     */
    public static void validateParkingTransition(String currentStatus, String newStatus) {
        ParkingStatus current = ParkingStatus.fromValue(currentStatus);
        ParkingStatus next = ParkingStatus.fromValue(newStatus);
        if (current == null) {
            throw new BusinessException("停车位当前状态「" + currentStatus + "」不合法");
        }
        if (next == null) {
            throw new BusinessException("停车位目标状态「" + newStatus + "」不合法");
        }
        validateTransition(current, next, PARKING_TRANSITIONS, "停车位");
    }

    /**
     * 验证房屋状态值是否合法（仅验证值，不做转换检查）
     */
    public static void validateHouseStatus(String status) {
        if (status == null || status.isEmpty()) {
            return; // null/空 表示不修改状态，允许
        }
        if (HouseStatus.fromValue(status) == null) {
            throw new BusinessException("房屋状态「" + status + "」不合法，允许值：已入住/空置/装修中");
        }
    }

    /**
     * 验证停车位状态值是否合法（仅验证值，不做转换检查）
     */
    public static void validateParkingStatus(String status) {
        if (status == null || status.isEmpty()) {
            return; // null/空 表示不修改状态，允许
        }
        if (ParkingStatus.fromValue(status) == null) {
            throw new BusinessException("停车位状态「" + status + "」不合法，允许值：使用中/空闲");
        }
    }

    /**
     * 验证值班班次值是否合法（枚举类型重载）
     */
    public static void validateDutyShift(DutyShift shift) {
        // 枚举类型本身已保证合法性，null 表示不修改班次
    }

    /**
     * 验证值班班次值是否合法（字符串类型，向后兼容）
     */
    public static void validateDutyShift(String shift) {
        if (shift != null && !shift.isEmpty() && DutyShift.fromValue(shift) == null) {
            throw new BusinessException("值班班次「" + shift + "」不合法，允许值：早班/中班/晚班");
        }
    }

    /**
     * 通用状态转换验证
     */
    private static <E extends Enum<E>> void validateTransition(E current, E next,
                                                                Map<E, Set<E>> transitions, String entityName) {
        // 状态未变化，允许（用于更新其他字段）
        if (current.equals(next)) {
            return;
        }

        Set<E> allowedTargets = transitions.get(current);
        if (allowedTargets == null) {
            throw new BusinessException(entityName + "当前状态「" + current + "」不合法");
        }
        if (!allowedTargets.contains(next)) {
            throw new BusinessException(
                    entityName + "不允许从「" + current + "」转换为「" + next + "」");
        }
    }
}
