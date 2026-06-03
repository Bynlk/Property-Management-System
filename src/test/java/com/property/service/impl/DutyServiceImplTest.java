package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.Duty;
import com.property.enums.DutyShift;
import com.property.mapper.DutyMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DutyServiceImpl 单元测试")
class DutyServiceImplTest {

    @Mock
    private DutyMapper dutyMapper;

    @InjectMocks
    private DutyServiceImpl dutyService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("存在时返回实体")
        void returnsEntity_whenExists() {
            Duty duty = Duty.builder().id(1).employeeId(1).dutyDate(LocalDate.now()).shift(DutyShift.MORNING).build();
            when(dutyMapper.selectById(1)).thenReturn(duty);

            Duty result = dutyService.getById(1);

            assertNotNull(result);
            assertEquals(1, result.getEmployeeId());
            assertEquals(DutyShift.MORNING, result.getShift());
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(dutyMapper.selectById(999)).thenReturn(null);

            assertNull(dutyService.getById(999));
        }
    }

    @Nested
    @DisplayName("add")
    class AddTest {

        @Test
        @DisplayName("新增成功返回1（早班）")
        void returnsOne_whenAddSucceeds_morning() {
            Duty duty = Duty.builder().employeeId(1).dutyDate(LocalDate.now()).shift(DutyShift.MORNING).build();
            when(dutyMapper.insert(any(Duty.class))).thenReturn(1);

            int result = dutyService.add(duty);

            assertEquals(1, result);
            verify(dutyMapper).insert(duty);
        }

        @Test
        @DisplayName("新增成功返回1（中班）")
        void returnsOne_whenAddSucceeds_afternoon() {
            Duty duty = Duty.builder().employeeId(1).dutyDate(LocalDate.now()).shift(DutyShift.AFTERNOON).build();
            when(dutyMapper.insert(any(Duty.class))).thenReturn(1);

            int result = dutyService.add(duty);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("新增成功返回1（晚班）")
        void returnsOne_whenAddSucceeds_night() {
            Duty duty = Duty.builder().employeeId(1).dutyDate(LocalDate.now()).shift(DutyShift.NIGHT).build();
            when(dutyMapper.insert(any(Duty.class))).thenReturn(1);

            int result = dutyService.add(duty);

            assertEquals(1, result);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("更新成功返回1")
        void returnsOne_whenUpdateSucceeds() {
            Duty existing = Duty.builder().id(1).employeeId(1).dutyDate(LocalDate.now()).shift(DutyShift.MORNING).build();
            Duty update = Duty.builder().id(1).employeeId(1).dutyDate(LocalDate.now()).shift(DutyShift.AFTERNOON).build();
            when(dutyMapper.selectById(1)).thenReturn(existing);
            when(dutyMapper.update(any(Duty.class))).thenReturn(1);

            int result = dutyService.update(update);

            assertEquals(1, result);
            verify(dutyMapper).update(update);
        }

        @Test
        @DisplayName("更新不存在的值班记录抛出异常")
        void throws_whenNotExists() {
            Duty update = Duty.builder().id(999).employeeId(1).dutyDate(LocalDate.now()).shift(DutyShift.MORNING).build();
            when(dutyMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> dutyService.update(update));
        }
    }

    @Nested
    @DisplayName("getByPage")
    class GetByPageTest {

        @Test
        @DisplayName("分页查询返回正确结果")
        void returnsPageResult() {
            when(dutyMapper.selectByPage(any(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
            when(dutyMapper.selectCount(any(), anyString())).thenReturn(0);

            var result = dutyService.getByPage(null, "", 1, 10);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
        }
    }

    @Nested
    @DisplayName("getByCondition")
    class GetByConditionTest {

        @Test
        @DisplayName("条件查询返回列表")
        void returnsFilteredList() {
            List<Duty> list = List.of(
                    Duty.builder().id(1).employeeId(1).shift(DutyShift.MORNING).build()
            );
            when(dutyMapper.selectByCondition(1, "早班")).thenReturn(list);

            List<Duty> result = dutyService.getByCondition(1, "早班");

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("返回全部值班记录")
        void returnsAll() {
            List<Duty> list = List.of(
                    Duty.builder().id(1).employeeId(1).shift(DutyShift.MORNING).build(),
                    Duty.builder().id(2).employeeId(2).shift(DutyShift.NIGHT).build()
            );
            when(dutyMapper.selectAll()).thenReturn(list);

            List<Duty> result = dutyService.getAll();

            assertEquals(2, result.size());
        }
    }
}
