import CrudPage from '../components/CrudPage'
import { Building2 } from 'lucide-react'
import { employeeApi } from '../api'
import { GENDER_OPTIONS } from '../constants'
import type { Employee } from '../types'

const columns = [
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'name', title: '姓名' },
  { key: 'gender', title: '性别', width: '80px' },
  { key: 'phone', title: '手机号' },
  { key: 'position', title: '岗位' },
  { key: 'hireDate', title: '入职日期' },
]

const fields = [
  { name: 'name', label: '姓名', required: true },
  { name: 'gender', label: '性别', type: 'select' as const, options: GENDER_OPTIONS },
  { name: 'phone', label: '手机号', pattern: '^1[3-9]\\d{9}$', patternMessage: '请输入正确的11位手机号' },
  { name: 'position', label: '岗位' },
  { name: 'hireDate', label: '入职日期', type: 'date' as const },
]

export default function EmployeePage() {
  return (
    <CrudPage<Employee>
      title="员工管理" subtitle="管理员工信息与岗位" icon={Building2} addLabel="新增员工"
      api={employeeApi}
      searchFields={[
        { key: 'name', placeholder: '搜索姓名' },
        { key: 'position', placeholder: '搜索岗位' },
      ]}
      columns={columns} fields={fields}
      getInitialValues={(item) => ({
        name: item.name, gender: item.gender ?? '', phone: item.phone,
        position: item.position, hireDate: item.hireDate,
      })}
      getDeleteMessage={(item) => `确定要删除员工「${item.name}」吗？`}
    />
  )
}
