package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.Repair;
import com.property.entity.StatusChangeLog;
import com.property.enums.RepairStatus;
import com.property.mapper.RepairMapper;
import com.property.mapper.StatusChangeLogMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RepairServiceImpl 单元测试")
class RepairServiceImplTest {

    @Mock
    private RepairMapper repairMapper;
    @Mock
    private StatusChangeLogMapper statusChangeLogMapper;

    @InjectMocks
    private RepairServiceImpl repairService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("存在时返回实体")
        void returnsEntity_whenExists() {
            Repair repair = Repair.builder().id(1).deviceName("水龙头").status(RepairStatus.COMPLETED).build();
            when(repairMapper.selectById(1)).thenReturn(repair);

            Repair result = repairService.getById(1);

            assertNotNull(result);
            assertEquals("水龙头", result.getDeviceName());
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(repairMapper.selectById(999)).thenReturn(null);

            assertNull(repairService.getById(999));
        }
    }

    @Nested
    @DisplayName("update 状态转换")
    class UpdateStatusTest {

        @Test
        @DisplayName("待维修 -> 维修中：合法转换")
        void pendingToInProgress() {
            Repair existing = Repair.builder().id(1).status(RepairStatus.PENDING).build();
            Repair update = Repair.builder().id(1).status(RepairStatus.IN_PROGRESS).build();
            when(repairMapper.selectById(1)).thenReturn(existing);
            when(repairMapper.update(any())).thenReturn(1);
            when(statusChangeLogMapper.insert(any())).thenReturn(1);

            int result = repairService.update(update);

            assertEquals(1, result);
            verify(repairMapper).update(update);
        }

        @Test
        @DisplayName("维修中 -> 已完成：合法转换，自动设置completedAt")
        void inProgressToCompleted_setsCompletedAt() {
            Repair existing = Repair.builder().id(1).status(RepairStatus.IN_PROGRESS).build();
            Repair update = Repair.builder().id(1).status(RepairStatus.COMPLETED).build();
            when(repairMapper.selectById(1)).thenReturn(existing);
            when(repairMapper.update(any())).thenReturn(1);
            when(statusChangeLogMapper.insert(any())).thenReturn(1);

            repairService.update(update);

            assertNotNull(update.getCompletedAt());
            verify(repairMapper).update(update);
        }

        @Test
        @DisplayName("待维修 -> 已完成：跳过中间状态，非法")
        void pendingToCompleted_throws() {
            Repair existing = Repair.builder().id(1).status(RepairStatus.PENDING).build();
            Repair update = Repair.builder().id(1).status(RepairStatus.COMPLETED).build();
            when(repairMapper.selectById(1)).thenReturn(existing);

            assertThrows(BusinessException.class, () -> repairService.update(update));
        }

        @Test
        @DisplayName("已完成 -> 维修中：终态不可逆")
        void completedToInProgress_throws() {
            Repair existing = Repair.builder().id(1).status(RepairStatus.COMPLETED).build();
            Repair update = Repair.builder().id(1).status(RepairStatus.IN_PROGRESS).build();
            when(repairMapper.selectById(1)).thenReturn(existing);

            assertThrows(BusinessException.class, () -> repairService.update(update));
        }

        @Test
        @DisplayName("更新不存在的报修抛出异常")
        void throws_whenNotExists() {
            Repair update = Repair.builder().id(999).status(RepairStatus.IN_PROGRESS).build();
            when(repairMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> repairService.update(update));
        }

        @Test
        @DisplayName("审计日志记录了正确的变更信息")
        void auditLog_recordsCorrectInfo() {
            Repair existing = Repair.builder().id(1).status(RepairStatus.PENDING).build();
            Repair update = Repair.builder().id(1).status(RepairStatus.IN_PROGRESS).build();
            when(repairMapper.selectById(1)).thenReturn(existing);
            when(repairMapper.update(any())).thenReturn(1);
            when(statusChangeLogMapper.insert(any())).thenReturn(1);

            repairService.update(update);

            ArgumentCaptor<StatusChangeLog> logCaptor = ArgumentCaptor.forClass(StatusChangeLog.class);
            verify(statusChangeLogMapper).insert(logCaptor.capture());
            StatusChangeLog log = logCaptor.getValue();
            assertEquals("repair", log.getEntityType());
            assertEquals(1, log.getEntityId());
            assertEquals("待维修", log.getOldStatus());
            assertEquals("维修中", log.getNewStatus());
            assertNotNull(log.getChangedAt());
        }

        @Test
        @DisplayName("状态未变化时允许")
        void sameStatusAllowed() {
            Repair existing = Repair.builder().id(1).status(RepairStatus.PENDING).build();
            Repair update = Repair.builder().id(1).status(RepairStatus.PENDING).faultDescription("补充描述").build();
            when(repairMapper.selectById(1)).thenReturn(existing);
            when(repairMapper.update(any())).thenReturn(1);

            int result = repairService.update(update);

            assertEquals(1, result);
            // 状态相同时仍允许更新
            verify(repairMapper).update(update);
        }
    }

    @Nested
    @DisplayName("getByOwnerId")
    class GetByOwnerIdTest {

        @Test
        @DisplayName("返回指定业主的报修列表")
        void returnsList() {
            List<Repair> list = List.of(Repair.builder().id(1).ownerId(1).build());
            when(repairMapper.selectByOwnerId(1)).thenReturn(list);

            List<Repair> result = repairService.getByOwnerId(1);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("count")
    class CountTest {

        @Test
        @DisplayName("返回总数")
        void returnsCount() {
            when(repairMapper.countAll()).thenReturn(3);

            assertEquals(3, repairService.count());
        }
    }
}
