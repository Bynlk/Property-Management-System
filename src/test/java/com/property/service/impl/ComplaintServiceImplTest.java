package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.Complaint;
import com.property.entity.StatusChangeLog;
import com.property.enums.ComplaintStatus;
import com.property.mapper.ComplaintMapper;
import com.property.mapper.StatusChangeLogMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComplaintServiceImpl 单元测试")
class ComplaintServiceImplTest {

    @Mock
    private ComplaintMapper complaintMapper;
    @Mock
    private StatusChangeLogMapper statusChangeLogMapper;

    @InjectMocks
    private ComplaintServiceImpl complaintService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("存在时返回实体")
        void returnsEntity_whenExists() {
            Complaint complaint = Complaint.builder().id(1).title("噪音").status(ComplaintStatus.PENDING).build();
            when(complaintMapper.selectById(1)).thenReturn(complaint);

            Complaint result = complaintService.getById(1);

            assertNotNull(result);
            assertEquals("噪音", result.getTitle());
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(complaintMapper.selectById(999)).thenReturn(null);

            assertNull(complaintService.getById(999));
        }
    }

    @Nested
    @DisplayName("add")
    class AddTest {

        @Test
        @DisplayName("新增成功返回1")
        void returnsOne_whenAddSucceeds() {
            Complaint complaint = Complaint.builder().ownerId(1).title("电梯故障").build();
            when(complaintMapper.insert(any(Complaint.class))).thenReturn(1);

            int result = complaintService.add(complaint);

            assertEquals(1, result);
            verify(complaintMapper).insert(complaint);
        }
    }

    @Nested
    @DisplayName("update 状态转换")
    class UpdateStatusTest {

        @Test
        @DisplayName("待处理 -> 处理中：合法转换")
        void pendingToProcessing() {
            Complaint existing = Complaint.builder().id(1).status(ComplaintStatus.PENDING).build();
            Complaint update = Complaint.builder().id(1).status(ComplaintStatus.PROCESSING).build();
            when(complaintMapper.selectById(1)).thenReturn(existing);
            when(complaintMapper.update(any())).thenReturn(1);
            when(statusChangeLogMapper.insert(any())).thenReturn(1);

            int result = complaintService.update(update);

            assertEquals(1, result);
            verify(complaintMapper).update(update);
            verify(statusChangeLogMapper).insert(any(StatusChangeLog.class));
        }

        @Test
        @DisplayName("处理中 -> 已处理：合法转换，自动设置resolvedAt")
        void processingToResolved_setsResolvedAt() {
            Complaint existing = Complaint.builder().id(1).status(ComplaintStatus.PROCESSING).build();
            Complaint update = Complaint.builder().id(1).status(ComplaintStatus.RESOLVED).build();
            when(complaintMapper.selectById(1)).thenReturn(existing);
            when(complaintMapper.update(any())).thenReturn(1);
            when(statusChangeLogMapper.insert(any())).thenReturn(1);

            complaintService.update(update);

            assertNotNull(update.getResolvedAt());
            verify(complaintMapper).update(update);
        }

        @Test
        @DisplayName("待处理 -> 已处理：跳过中间状态，非法")
        void pendingToResolved_throws() {
            Complaint existing = Complaint.builder().id(1).status(ComplaintStatus.PENDING).build();
            Complaint update = Complaint.builder().id(1).status(ComplaintStatus.RESOLVED).build();
            when(complaintMapper.selectById(1)).thenReturn(existing);

            assertThrows(BusinessException.class, () -> complaintService.update(update));
        }

        @Test
        @DisplayName("已处理 -> 处理中：终态不可逆")
        void resolvedToProcessing_throws() {
            Complaint existing = Complaint.builder().id(1).status(ComplaintStatus.RESOLVED).build();
            Complaint update = Complaint.builder().id(1).status(ComplaintStatus.PROCESSING).build();
            when(complaintMapper.selectById(1)).thenReturn(existing);

            assertThrows(BusinessException.class, () -> complaintService.update(update));
        }

        @Test
        @DisplayName("更新不存在的投诉抛出异常")
        void throws_whenNotExists() {
            Complaint update = Complaint.builder().id(999).status(ComplaintStatus.PROCESSING).build();
            when(complaintMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> complaintService.update(update));
        }

        @Test
        @DisplayName("审计日志记录了正确的变更信息")
        void auditLog_recordsCorrectInfo() {
            Complaint existing = Complaint.builder().id(1).status(ComplaintStatus.PENDING).build();
            Complaint update = Complaint.builder().id(1).status(ComplaintStatus.PROCESSING).build();
            when(complaintMapper.selectById(1)).thenReturn(existing);
            when(complaintMapper.update(any())).thenReturn(1);
            when(statusChangeLogMapper.insert(any())).thenReturn(1);

            complaintService.update(update);

            ArgumentCaptor<StatusChangeLog> logCaptor = ArgumentCaptor.forClass(StatusChangeLog.class);
            verify(statusChangeLogMapper).insert(logCaptor.capture());
            StatusChangeLog log = logCaptor.getValue();
            assertEquals("complaint", log.getEntityType());
            assertEquals(1, log.getEntityId());
            assertEquals("待处理", log.getOldStatus());
            assertEquals("处理中", log.getNewStatus());
            assertNotNull(log.getChangedAt());
        }

        @Test
        @DisplayName("状态未变化时允许（修改其他字段）")
        void sameStatusAllowed() {
            Complaint existing = Complaint.builder().id(1).status(ComplaintStatus.PENDING).build();
            Complaint update = Complaint.builder().id(1).status(ComplaintStatus.PENDING).content("补充说明").build();
            when(complaintMapper.selectById(1)).thenReturn(existing);
            when(complaintMapper.update(any())).thenReturn(1);

            int result = complaintService.update(update);

            assertEquals(1, result);
            // 状态相同时仍允许更新，审计日志仍会写入（状态非null）
            verify(complaintMapper).update(update);
        }
    }

    @Nested
    @DisplayName("getByOwnerId")
    class GetByOwnerIdTest {

        @Test
        @DisplayName("返回指定业主的投诉列表")
        void returnsList() {
            List<Complaint> list = List.of(
                    Complaint.builder().id(1).ownerId(1).build()
            );
            when(complaintMapper.selectByOwnerId(1)).thenReturn(list);

            List<Complaint> result = complaintService.getByOwnerId(1);

            assertEquals(1, result.size());
        }
    }
}
