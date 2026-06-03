package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.Owner;
import com.property.mapper.ComplaintMapper;
import com.property.mapper.FeeMapper;
import com.property.mapper.HouseMapper;
import com.property.mapper.OwnerMapper;
import com.property.mapper.RepairMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerServiceImpl 单元测试")
class OwnerServiceImplTest {

    @Mock
    private OwnerMapper ownerMapper;
    @Mock
    private HouseMapper houseMapper;
    @Mock
    private FeeMapper feeMapper;
    @Mock
    private ComplaintMapper complaintMapper;
    @Mock
    private RepairMapper repairMapper;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("存在时返回实体")
        void returnsEntity_whenExists() {
            Owner owner = Owner.builder().id(1).name("刘备").build();
            when(ownerMapper.selectById(1)).thenReturn(owner);

            Owner result = ownerService.getById(1);

            assertNotNull(result);
            assertEquals("刘备", result.getName());
            verify(ownerMapper).selectById(1);
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(ownerMapper.selectById(999)).thenReturn(null);

            Owner result = ownerService.getById(999);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("add")
    class AddTest {

        @Test
        @DisplayName("新增成功返回1")
        void returnsOne_whenAddSucceeds() {
            Owner owner = Owner.builder().name("张飞").build();
            when(ownerMapper.insert(any(Owner.class))).thenReturn(1);

            int result = ownerService.add(owner);

            assertEquals(1, result);
            verify(ownerMapper).insert(owner);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("更新成功返回1")
        void returnsOne_whenUpdateSucceeds() {
            Owner existing = Owner.builder().id(1).name("刘备").build();
            Owner update = Owner.builder().id(1).name("刘备改").build();
            when(ownerMapper.selectById(1)).thenReturn(existing);
            when(ownerMapper.update(any(Owner.class))).thenReturn(1);

            int result = ownerService.update(update);

            assertEquals(1, result);
            verify(ownerMapper).update(update);
        }

        @Test
        @DisplayName("更新不存在的业主抛出异常")
        void throws_whenNotExists() {
            Owner update = Owner.builder().id(999).name("不存在").build();
            when(ownerMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> ownerService.update(update));
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("删除成功（无关联数据）")
        void succeeds_whenNoAssociatedData() {
            Owner existing = Owner.builder().id(1).name("刘备").build();
            when(ownerMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.countByOwnerId(1)).thenReturn(0);
            when(feeMapper.countByOwnerId(1)).thenReturn(0);
            when(complaintMapper.countByOwnerId(1)).thenReturn(0);
            when(repairMapper.countByOwnerId(1)).thenReturn(0);
            when(ownerMapper.deleteById(1)).thenReturn(1);

            int result = ownerService.delete(1);

            assertEquals(1, result);
            verify(ownerMapper).deleteById(1);
        }

        @Test
        @DisplayName("删除不存在的业主抛出异常")
        void throws_whenNotExists() {
            when(ownerMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> ownerService.delete(999));
        }

        @Test
        @DisplayName("有关联房屋时拒绝删除")
        void throws_whenHasHouses() {
            Owner existing = Owner.builder().id(1).name("刘备").build();
            when(ownerMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.countByOwnerId(1)).thenReturn(2);

            BusinessException ex = assertThrows(BusinessException.class, () -> ownerService.delete(1));
            assertTrue(ex.getMessage().contains("房屋"));
            verify(ownerMapper, never()).deleteById(anyInt());
        }

        @Test
        @DisplayName("有关联费用时拒绝删除")
        void throws_whenHasFees() {
            Owner existing = Owner.builder().id(1).name("刘备").build();
            when(ownerMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.countByOwnerId(1)).thenReturn(0);
            when(feeMapper.countByOwnerId(1)).thenReturn(3);

            BusinessException ex = assertThrows(BusinessException.class, () -> ownerService.delete(1));
            assertTrue(ex.getMessage().contains("费用"));
            verify(ownerMapper, never()).deleteById(anyInt());
        }

        @Test
        @DisplayName("有关联投诉时拒绝删除")
        void throws_whenHasComplaints() {
            Owner existing = Owner.builder().id(1).name("刘备").build();
            when(ownerMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.countByOwnerId(1)).thenReturn(0);
            when(feeMapper.countByOwnerId(1)).thenReturn(0);
            when(complaintMapper.countByOwnerId(1)).thenReturn(1);

            BusinessException ex = assertThrows(BusinessException.class, () -> ownerService.delete(1));
            assertTrue(ex.getMessage().contains("投诉"));
            verify(ownerMapper, never()).deleteById(anyInt());
        }

        @Test
        @DisplayName("有关联报修时拒绝删除")
        void throws_whenHasRepairs() {
            Owner existing = Owner.builder().id(1).name("刘备").build();
            when(ownerMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.countByOwnerId(1)).thenReturn(0);
            when(feeMapper.countByOwnerId(1)).thenReturn(0);
            when(complaintMapper.countByOwnerId(1)).thenReturn(0);
            when(repairMapper.countByOwnerId(1)).thenReturn(1);

            BusinessException ex = assertThrows(BusinessException.class, () -> ownerService.delete(1));
            assertTrue(ex.getMessage().contains("报修"));
            verify(ownerMapper, never()).deleteById(anyInt());
        }
    }

    @Nested
    @DisplayName("getByPage")
    class GetByPageTest {

        @Test
        @DisplayName("分页查询返回正确结果")
        void returnsPageResult() {
            List<Owner> list = List.of(Owner.builder().id(1).name("刘备").build());
            when(ownerMapper.selectByPage(anyString(), anyString(), anyInt(), anyInt())).thenReturn(list);
            when(ownerMapper.selectCount(anyString(), anyString())).thenReturn(1);

            var result = ownerService.getByPage("", "", 1, 10);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
        }
    }

    @Nested
    @DisplayName("count")
    class CountTest {

        @Test
        @DisplayName("返回总数")
        void returnsCount() {
            when(ownerMapper.countAll()).thenReturn(5);

            int count = ownerService.count();

            assertEquals(5, count);
        }
    }
}
