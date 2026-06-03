package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.Parking;
import com.property.enums.ParkingStatus;
import com.property.mapper.ParkingMapper;
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
@DisplayName("ParkingServiceImpl 单元测试")
class ParkingServiceImplTest {

    @Mock
    private ParkingMapper parkingMapper;

    @InjectMocks
    private ParkingServiceImpl parkingService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("存在时返回实体")
        void returnsEntity_whenExists() {
            Parking parking = Parking.builder().id(1).spotNumber("A-001").status(ParkingStatus.IDLE).build();
            when(parkingMapper.selectById(1)).thenReturn(parking);

            Parking result = parkingService.getById(1);

            assertNotNull(result);
            assertEquals("A-001", result.getSpotNumber());
            assertEquals(ParkingStatus.IDLE, result.getStatus());
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(parkingMapper.selectById(999)).thenReturn(null);

            assertNull(parkingService.getById(999));
        }
    }

    @Nested
    @DisplayName("add")
    class AddTest {

        @Test
        @DisplayName("新增成功返回1")
        void returnsOne_whenAddSucceeds() {
            Parking parking = Parking.builder().spotNumber("B-001").status(ParkingStatus.IDLE).build();
            when(parkingMapper.insert(any(Parking.class))).thenReturn(1);

            int result = parkingService.add(parking);

            assertEquals(1, result);
            verify(parkingMapper).insert(parking);
        }
    }

    @Nested
    @DisplayName("update 状态转换")
    class UpdateStatusTest {

        @Test
        @DisplayName("空闲 -> 使用中：合法转换")
        void idleToInUse() {
            Parking existing = Parking.builder().id(1).status(ParkingStatus.IDLE).build();
            Parking update = Parking.builder().id(1).status(ParkingStatus.IN_USE).build();
            when(parkingMapper.selectById(1)).thenReturn(existing);
            when(parkingMapper.update(any())).thenReturn(1);

            int result = parkingService.update(update);

            assertEquals(1, result);
            verify(parkingMapper).update(update);
        }

        @Test
        @DisplayName("使用中 -> 空闲：合法转换")
        void inUseToIdle() {
            Parking existing = Parking.builder().id(1).status(ParkingStatus.IN_USE).build();
            Parking update = Parking.builder().id(1).status(ParkingStatus.IDLE).build();
            when(parkingMapper.selectById(1)).thenReturn(existing);
            when(parkingMapper.update(any())).thenReturn(1);

            int result = parkingService.update(update);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("状态为null时不触发状态校验（修改其他字段）")
        void nullStatus_skipsValidation() {
            Parking existing = Parking.builder().id(1).status(ParkingStatus.IN_USE).licensePlate("京A12345").build();
            Parking update = Parking.builder().id(1).status(null).licensePlate("京B67890").build();
            when(parkingMapper.selectById(1)).thenReturn(existing);
            when(parkingMapper.update(any())).thenReturn(1);

            int result = parkingService.update(update);

            assertEquals(1, result);
            verify(parkingMapper).update(update);
        }

        @Test
        @DisplayName("更新不存在的停车位抛出异常")
        void throws_whenNotExists() {
            Parking update = Parking.builder().id(999).status(ParkingStatus.IDLE).build();
            when(parkingMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> parkingService.update(update));
        }
    }

    @Nested
    @DisplayName("getByPage")
    class GetByPageTest {

        @Test
        @DisplayName("分页查询返回正确结果")
        void returnsPageResult() {
            when(parkingMapper.selectByPage(anyString(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
            when(parkingMapper.selectCount(anyString(), anyString())).thenReturn(0);

            var result = parkingService.getByPage("", "", 1, 10);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
        }
    }

    @Nested
    @DisplayName("getByOwnerId")
    class GetByOwnerIdTest {

        @Test
        @DisplayName("返回指定业主的车位列表")
        void returnsList() {
            List<Parking> list = List.of(Parking.builder().id(1).ownerId(1).spotNumber("A-001").build());
            when(parkingMapper.selectByOwnerId(1)).thenReturn(list);

            List<Parking> result = parkingService.getByOwnerId(1);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("返回全部车位")
        void returnsAll() {
            List<Parking> list = List.of(
                    Parking.builder().id(1).spotNumber("A-001").build(),
                    Parking.builder().id(2).spotNumber("A-002").build()
            );
            when(parkingMapper.selectAll()).thenReturn(list);

            List<Parking> result = parkingService.getAll();

            assertEquals(2, result.size());
        }
    }
}
