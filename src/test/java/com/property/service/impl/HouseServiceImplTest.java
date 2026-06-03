package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.House;
import com.property.enums.HouseStatus;
import com.property.mapper.HouseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HouseServiceImpl 单元测试")
class HouseServiceImplTest {

    @Mock
    private HouseMapper houseMapper;

    @InjectMocks
    private HouseServiceImpl houseService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("存在时返回实体")
        void returnsEntity_whenExists() {
            House house = House.builder().id(1).building("A栋").roomNumber("101").status(HouseStatus.VACANT).build();
            when(houseMapper.selectById(1)).thenReturn(house);

            House result = houseService.getById(1);

            assertNotNull(result);
            assertEquals("A栋", result.getBuilding());
            assertEquals(HouseStatus.VACANT, result.getStatus());
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(houseMapper.selectById(999)).thenReturn(null);

            assertNull(houseService.getById(999));
        }
    }

    @Nested
    @DisplayName("add")
    class AddTest {

        @Test
        @DisplayName("新增成功返回1")
        void returnsOne_whenAddSucceeds() {
            House house = House.builder().building("B栋").roomNumber("201").area(BigDecimal.valueOf(89.5)).build();
            when(houseMapper.insert(any(House.class))).thenReturn(1);

            int result = houseService.add(house);

            assertEquals(1, result);
            verify(houseMapper).insert(house);
        }
    }

    @Nested
    @DisplayName("update 状态转换")
    class UpdateStatusTest {

        @Test
        @DisplayName("空置 -> 装修中：合法转换")
        void vacantToRenovating() {
            House existing = House.builder().id(1).status(HouseStatus.VACANT).build();
            House update = House.builder().id(1).status(HouseStatus.RENOVATING).build();
            when(houseMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.update(any())).thenReturn(1);

            int result = houseService.update(update);

            assertEquals(1, result);
            verify(houseMapper).update(update);
        }

        @Test
        @DisplayName("空置 -> 已入住：合法转换")
        void vacantToOccupied() {
            House existing = House.builder().id(1).status(HouseStatus.VACANT).build();
            House update = House.builder().id(1).status(HouseStatus.OCCUPIED).build();
            when(houseMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.update(any())).thenReturn(1);

            int result = houseService.update(update);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("装修中 -> 已入住：合法转换")
        void renovatingToOccupied() {
            House existing = House.builder().id(1).status(HouseStatus.RENOVATING).build();
            House update = House.builder().id(1).status(HouseStatus.OCCUPIED).build();
            when(houseMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.update(any())).thenReturn(1);

            int result = houseService.update(update);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("已入住 -> 装修中：非法转换，抛出异常")
        void occupiedToRenovating_throws() {
            House existing = House.builder().id(1).status(HouseStatus.OCCUPIED).build();
            House update = House.builder().id(1).status(HouseStatus.RENOVATING).build();
            when(houseMapper.selectById(1)).thenReturn(existing);

            assertThrows(BusinessException.class, () -> houseService.update(update));
        }

        @Test
        @DisplayName("已入住 -> 空置：合法转换")
        void occupiedToVacant() {
            House existing = House.builder().id(1).status(HouseStatus.OCCUPIED).build();
            House update = House.builder().id(1).status(HouseStatus.VACANT).build();
            when(houseMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.update(any())).thenReturn(1);

            int result = houseService.update(update);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("状态为null时不触发状态校验（修改其他字段）")
        void nullStatus_skipsValidation() {
            House existing = House.builder().id(1).status(HouseStatus.OCCUPIED).building("A栋").build();
            House update = House.builder().id(1).status(null).building("B栋").build();
            when(houseMapper.selectById(1)).thenReturn(existing);
            when(houseMapper.update(any())).thenReturn(1);

            int result = houseService.update(update);

            assertEquals(1, result);
            verify(houseMapper).update(update);
        }

        @Test
        @DisplayName("更新不存在的房屋抛出异常")
        void throws_whenNotExists() {
            House update = House.builder().id(999).status(HouseStatus.VACANT).build();
            when(houseMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> houseService.update(update));
        }
    }

    @Nested
    @DisplayName("count")
    class CountTest {

        @Test
        @DisplayName("返回总数")
        void returnsCount() {
            when(houseMapper.countAll()).thenReturn(10);

            int count = houseService.count();

            assertEquals(10, count);
        }
    }

    @Nested
    @DisplayName("getByPage")
    class GetByPageTest {

        @Test
        @DisplayName("分页查询返回正确结果")
        void returnsPageResult() {
            when(houseMapper.selectByPage(anyString(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
            when(houseMapper.selectCount(anyString(), anyString())).thenReturn(0);

            var result = houseService.getByPage("", "", 1, 10);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
        }
    }

    @Nested
    @DisplayName("getByOwnerId")
    class GetByOwnerIdTest {

        @Test
        @DisplayName("返回指定业主的房屋列表")
        void returnsList() {
            List<House> list = List.of(House.builder().id(1).ownerId(1).building("A栋").build());
            when(houseMapper.selectByOwnerId(1)).thenReturn(list);

            List<House> result = houseService.getByOwnerId(1);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("返回全部房屋")
        void returnsAll() {
            List<House> list = List.of(
                    House.builder().id(1).building("A栋").build(),
                    House.builder().id(2).building("B栋").build()
            );
            when(houseMapper.selectAll()).thenReturn(list);

            List<House> result = houseService.getAll();

            assertEquals(2, result.size());
        }
    }
}
