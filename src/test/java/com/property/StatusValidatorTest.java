package com.property;

import com.property.common.BusinessException;
import com.property.common.StatusValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StatusValidator 状态机单元测试
 * 覆盖所有合法转换、非法转换、终态锁定、相同状态更新等场景
 */
@DisplayName("状态转换验证器测试")
class StatusValidatorTest {

    // ==================== 投诉状态机 ====================
    @Nested
    @DisplayName("投诉状态转换")
    class ComplaintTransitionTest {

        @Test
        @DisplayName("待处理 → 处理中：合法转换")
        void pendingToProcessing() {
            assertDoesNotThrow(() ->
                    StatusValidator.validateComplaintTransition("待处理", "处理中"));
        }

        @Test
        @DisplayName("处理中 → 已处理：合法转换")
        void processingToResolved() {
            assertDoesNotThrow(() ->
                    StatusValidator.validateComplaintTransition("处理中", "已处理"));
        }

        @Test
        @DisplayName("已处理 → 处理中：终态不可逆")
        void resolvedCannotGoBack() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> StatusValidator.validateComplaintTransition("已处理", "处理中"));
            assertTrue(ex.getMessage().contains("不允许"));
        }

        @Test
        @DisplayName("待处理 → 已处理：不可跳过中间状态")
        void pendingCannotSkipToResolved() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateComplaintTransition("待处理", "已处理"));
        }

        @Test
        @DisplayName("相同状态更新：允许（用于修改其他字段）")
        void sameStatusAllowed() {
            assertDoesNotThrow(() ->
                    StatusValidator.validateComplaintTransition("待处理", "待处理"));
        }

        @Test
        @DisplayName("非法当前状态：抛出异常")
        void invalidCurrentStatus() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateComplaintTransition("未知状态", "处理中"));
        }

        @Test
        @DisplayName("非法目标状态：抛出异常")
        void invalidTargetStatus() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateComplaintTransition("待处理", "不存在"));
        }
    }

    // ==================== 报修状态机 ====================
    @Nested
    @DisplayName("报修状态转换")
    class RepairTransitionTest {

        @Test
        @DisplayName("待维修 → 维修中：合法转换")
        void pendingToInProgress() {
            assertDoesNotThrow(() ->
                    StatusValidator.validateRepairTransition("待维修", "维修中"));
        }

        @Test
        @DisplayName("维修中 → 已完成：合法转换")
        void inProgressToCompleted() {
            assertDoesNotThrow(() ->
                    StatusValidator.validateRepairTransition("维修中", "已完成"));
        }

        @Test
        @DisplayName("已完成：终态不可逆")
        void completedIsFinal() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateRepairTransition("已完成", "维修中"));
        }

        @Test
        @DisplayName("待维修 → 已完成：不可跳过")
        void cannotSkip() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateRepairTransition("待维修", "已完成"));
        }

        @Test
        @DisplayName("相同状态更新：允许")
        void sameStatusAllowed() {
            assertDoesNotThrow(() ->
                    StatusValidator.validateRepairTransition("待维修", "待维修"));
        }
    }

    // ==================== 费用状态机 ====================
    @Nested
    @DisplayName("费用状态转换")
    class FeeTransitionTest {

        @Test
        @DisplayName("未缴 → 已缴：合法转换")
        void unpaidToPaid() {
            assertDoesNotThrow(() ->
                    StatusValidator.validateFeeTransition("未缴", "已缴"));
        }

        @Test
        @DisplayName("已缴 → 未缴：不可逆")
        void paidCannotRevert() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateFeeTransition("已缴", "未缴"));
        }

        @Test
        @DisplayName("相同状态更新：允许")
        void sameStatusAllowed() {
            assertDoesNotThrow(() ->
                    StatusValidator.validateFeeTransition("未缴", "未缴"));
        }
    }

    // ==================== 房屋状态验证 ====================
    @Nested
    @DisplayName("房屋状态验证")
    class HouseStatusTest {

        @Test
        @DisplayName("合法状态值：已入住")
        void validOccupied() {
            assertDoesNotThrow(() -> StatusValidator.validateHouseStatus("已入住"));
        }

        @Test
        @DisplayName("合法状态值：空置")
        void validVacant() {
            assertDoesNotThrow(() -> StatusValidator.validateHouseStatus("空置"));
        }

        @Test
        @DisplayName("合法状态值：装修中")
        void validRenovating() {
            assertDoesNotThrow(() -> StatusValidator.validateHouseStatus("装修中"));
        }

        @Test
        @DisplayName("空值：允许（不修改状态）")
        void nullAllowed() {
            assertDoesNotThrow(() -> StatusValidator.validateHouseStatus(null));
        }

        @Test
        @DisplayName("空字符串：允许")
        void emptyAllowed() {
            assertDoesNotThrow(() -> StatusValidator.validateHouseStatus(""));
        }

        @Test
        @DisplayName("非法状态值：抛出异常")
        void invalidStatus() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateHouseStatus("已售出"));
        }
    }

    // ==================== 停车位状态验证 ====================
    @Nested
    @DisplayName("停车位状态验证")
    class ParkingStatusTest {

        @Test
        @DisplayName("合法状态值：使用中")
        void validInUse() {
            assertDoesNotThrow(() -> StatusValidator.validateParkingStatus("使用中"));
        }

        @Test
        @DisplayName("合法状态值：空闲")
        void validIdle() {
            assertDoesNotThrow(() -> StatusValidator.validateParkingStatus("空闲"));
        }

        @Test
        @DisplayName("非法状态值：抛出异常")
        void invalidStatus() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateParkingStatus("维修中"));
        }
    }

    // ==================== 值班班次验证 ====================
    @Nested
    @DisplayName("值班班次验证")
    class DutyShiftTest {

        @Test
        @DisplayName("合法班次：早班")
        void validMorning() {
            assertDoesNotThrow(() -> StatusValidator.validateDutyShift("早班"));
        }

        @Test
        @DisplayName("合法班次：中班")
        void validAfternoon() {
            assertDoesNotThrow(() -> StatusValidator.validateDutyShift("中班"));
        }

        @Test
        @DisplayName("合法班次：晚班")
        void validNight() {
            assertDoesNotThrow(() -> StatusValidator.validateDutyShift("晚班"));
        }

        @Test
        @DisplayName("非法班次：抛出异常")
        void invalidShift() {
            assertThrows(BusinessException.class,
                    () -> StatusValidator.validateDutyShift("夜班"));
        }
    }
}
