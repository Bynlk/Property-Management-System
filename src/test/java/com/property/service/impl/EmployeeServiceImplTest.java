package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.Employee;
import com.property.mapper.DutyMapper;
import com.property.mapper.EmployeeMapper;
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
@DisplayName("EmployeeServiceImpl 单元测试")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private DutyMapper dutyMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("存在时返回实体")
        void returnsEntity_whenExists() {
            Employee employee = Employee.builder().id(1).name("张三").build();
            when(employeeMapper.selectById(1)).thenReturn(employee);

            Employee result = employeeService.getById(1);

            assertNotNull(result);
            assertEquals("张三", result.getName());
            verify(employeeMapper).selectById(1);
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(employeeMapper.selectById(999)).thenReturn(null);

            Employee result = employeeService.getById(999);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("add")
    class AddTest {

        @Test
        @DisplayName("新增成功返回1")
        void returnsOne_whenAddSucceeds() {
            Employee employee = Employee.builder().name("李四").gender("男").phone("13800001111").position("保安").build();
            when(employeeMapper.insert(any(Employee.class))).thenReturn(1);

            int result = employeeService.add(employee);

            assertEquals(1, result);
            verify(employeeMapper).insert(employee);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("更新成功返回1")
        void returnsOne_whenUpdateSucceeds() {
            Employee existing = Employee.builder().id(1).name("张三").build();
            Employee update = Employee.builder().id(1).name("张三改").build();
            when(employeeMapper.selectById(1)).thenReturn(existing);
            when(employeeMapper.update(any(Employee.class))).thenReturn(1);

            int result = employeeService.update(update);

            assertEquals(1, result);
            verify(employeeMapper).update(update);
        }

        @Test
        @DisplayName("更新不存在的员工抛出异常")
        void throws_whenNotExists() {
            Employee update = Employee.builder().id(999).name("不存在").build();
            when(employeeMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> employeeService.update(update));
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("删除成功（无关联值班）")
        void succeeds_whenNoAssociatedDuties() {
            Employee existing = Employee.builder().id(1).name("张三").build();
            when(employeeMapper.selectById(1)).thenReturn(existing);
            when(dutyMapper.countByEmployeeId(1)).thenReturn(0);
            when(employeeMapper.deleteById(1)).thenReturn(1);

            int result = employeeService.delete(1);

            assertEquals(1, result);
            verify(employeeMapper).deleteById(1);
        }

        @Test
        @DisplayName("有关联值班时拒绝删除")
        void throws_whenHasDuties() {
            Employee existing = Employee.builder().id(1).name("张三").build();
            when(employeeMapper.selectById(1)).thenReturn(existing);
            when(dutyMapper.countByEmployeeId(1)).thenReturn(3);

            BusinessException ex = assertThrows(BusinessException.class, () -> employeeService.delete(1));
            assertTrue(ex.getMessage().contains("值班"));
            verify(employeeMapper, never()).deleteById(anyInt());
        }

        @Test
        @DisplayName("删除不存在的员工抛出异常")
        void throws_whenNotExists() {
            when(employeeMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> employeeService.delete(999));
        }
    }

    @Nested
    @DisplayName("getByPage")
    class GetByPageTest {

        @Test
        @DisplayName("分页查询返回正确结果")
        void returnsPageResult() {
            List<Employee> list = List.of(Employee.builder().id(1).name("张三").build());
            when(employeeMapper.selectByPage(anyString(), anyString(), anyInt(), anyInt())).thenReturn(list);
            when(employeeMapper.selectCount(anyString(), anyString())).thenReturn(1);

            var result = employeeService.getByPage("", "", 1, 10);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
        }
    }

    @Nested
    @DisplayName("getByCondition")
    class GetByConditionTest {

        @Test
        @DisplayName("条件查询返回列表")
        void returnsFilteredList() {
            List<Employee> list = List.of(Employee.builder().id(1).name("张三").position("保安").build());
            when(employeeMapper.selectByCondition("张三", "")).thenReturn(list);

            List<Employee> result = employeeService.getByCondition("张三", "");

            assertEquals(1, result.size());
            assertEquals("张三", result.get(0).getName());
        }
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("返回全部员工")
        void returnsAll() {
            List<Employee> list = List.of(
                    Employee.builder().id(1).name("张三").build(),
                    Employee.builder().id(2).name("李四").build()
            );
            when(employeeMapper.selectAll()).thenReturn(list);

            List<Employee> result = employeeService.getAll();

            assertEquals(2, result.size());
        }
    }
}
