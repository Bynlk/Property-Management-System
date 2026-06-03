package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.Fee;
import com.property.enums.FeeStatus;
import com.property.enums.FeeType;
import com.property.mapper.FeeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeeServiceImpl 单元测试")
class FeeServiceImplTest {

    @Mock
    private FeeMapper feeMapper;

    @InjectMocks
    private FeeServiceImpl feeService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("存在时返回实体")
        void returnsEntity_whenExists() {
            Fee fee = Fee.builder().id(1).ownerId(1).status(FeeStatus.UNPAID).build();
            when(feeMapper.selectById(1)).thenReturn(fee);

            Fee result = feeService.getById(1);

            assertNotNull(result);
            assertEquals(FeeStatus.UNPAID, result.getStatus());
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(feeMapper.selectById(999)).thenReturn(null);

            assertNull(feeService.getById(999));
        }
    }

    @Nested
    @DisplayName("add")
    class AddTest {

        @Test
        @DisplayName("新增成功返回1")
        void returnsOne_whenAddSucceeds() {
            Fee fee = Fee.builder().ownerId(1).feeType(FeeType.PROPERTY_FEE).amount(BigDecimal.valueOf(1200)).build();
            when(feeMapper.insert(any(Fee.class))).thenReturn(1);

            int result = feeService.add(fee);

            assertEquals(1, result);
            verify(feeMapper).insert(fee);
        }
    }

    @Nested
    @DisplayName("update 状态转换")
    class UpdateStatusTest {

        @Test
        @DisplayName("未缴 -> 已缴：合法转换，自动设置paidDate")
        void unpaidToPaid_setsPaidDate() {
            Fee existing = Fee.builder().id(1).status(FeeStatus.UNPAID).build();
            Fee update = Fee.builder().id(1).status(FeeStatus.PAID).build();
            when(feeMapper.selectById(1)).thenReturn(existing);
            when(feeMapper.update(any())).thenReturn(1);

            int result = feeService.update(update);

            assertEquals(1, result);
            assertNotNull(update.getPaidDate());
            assertEquals(LocalDate.now(), update.getPaidDate());
            verify(feeMapper).update(update);
        }

        @Test
        @DisplayName("已缴 -> 未缴：不可逆，抛出异常")
        void paidToUnpaid_throws() {
            Fee existing = Fee.builder().id(1).status(FeeStatus.PAID).build();
            Fee update = Fee.builder().id(1).status(FeeStatus.UNPAID).build();
            when(feeMapper.selectById(1)).thenReturn(existing);

            assertThrows(BusinessException.class, () -> feeService.update(update));
        }

        @Test
        @DisplayName("相同状态更新：允许（修改其他字段）")
        void sameStatusAllowed() {
            Fee existing = Fee.builder().id(1).status(FeeStatus.UNPAID).amount(BigDecimal.valueOf(100)).build();
            Fee update = Fee.builder().id(1).status(FeeStatus.UNPAID).amount(BigDecimal.valueOf(200)).build();
            when(feeMapper.selectById(1)).thenReturn(existing);
            when(feeMapper.update(any())).thenReturn(1);

            int result = feeService.update(update);

            assertEquals(1, result);
            verify(feeMapper).update(update);
        }

        @Test
        @DisplayName("更新不存在的费用抛出异常")
        void throws_whenNotExists() {
            Fee update = Fee.builder().id(999).status(FeeStatus.PAID).build();
            when(feeMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> feeService.update(update));
        }

        @Test
        @DisplayName("已缴状态下不重新设置paidDate")
        void paidToPaid_noPaidDateChange() {
            Fee existing = Fee.builder().id(1).status(FeeStatus.PAID).paidDate(LocalDate.of(2024, 1, 5)).build();
            Fee update = Fee.builder().id(1).status(FeeStatus.PAID).build();
            when(feeMapper.selectById(1)).thenReturn(existing);
            when(feeMapper.update(any())).thenReturn(1);

            feeService.update(update);

            // paidDate should not be changed since both are PAID
            assertNull(update.getPaidDate());
            verify(feeMapper).update(update);
        }
    }

    @Nested
    @DisplayName("getByOwnerId")
    class GetByOwnerIdTest {

        @Test
        @DisplayName("返回指定业主的费用列表")
        void returnsList() {
            List<Fee> list = List.of(Fee.builder().id(1).ownerId(1).build());
            when(feeMapper.selectByOwnerId(1)).thenReturn(list);

            List<Fee> result = feeService.getByOwnerId(1);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getByPage")
    class GetByPageTest {

        @Test
        @DisplayName("分页查询返回正确结果")
        void returnsPageResult() {
            when(feeMapper.selectByPage(any(), any(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
            when(feeMapper.selectCount(any(), any(), anyString())).thenReturn(0);

            var result = feeService.getByPage(null, null, "", 1, 10);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
        }
    }
}
