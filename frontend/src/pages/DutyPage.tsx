import CrudPage from '../components/CrudPage'
import { CalendarClock } from 'lucide-react'
import { dutyApi } from '../api'
import { DUTY_SHIFT_OPTIONS, getStatusBadgeClass } from '../constants'
import type { Duty } from '../types'

const columns = [
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'employeeName', title: '员工' },
  { key: 'dutyDate', title: '值班日期' },
  { key: 'shift', title: '班次', width: '80px', render: (item: Duty) => (
    <span className={`badge ${getStatusBadgeClass(item.shift)}`}>{item.shift}</span>
  )},
]

const fields = [
  { name: 'employeeId', label: '员工ID', required: true, type: 'number' as const },
  { name: 'dutyDate', label: '值班日期', type: 'date' as const, required: true },
  { name: 'shift', label: '班次', type: 'select' as const, required: true, options: DUTY_SHIFT_OPTIONS },
]

export default function DutyPage() {
  return (
    <CrudPage<Duty>
      title="值班管理" subtitle="管理员工值班排班" icon={CalendarClock} addLabel="新增值班"
      api={dutyApi}
      searchFields={[
        { key: 'shift', placeholder: '全部班次', type: 'select', options: DUTY_SHIFT_OPTIONS },
      ]}
      columns={columns} fields={fields}
      getInitialValues={(item) => ({
        employeeId: String(item.employeeId), dutyDate: item.dutyDate, shift: item.shift ?? '',
      })}
      getDeleteMessage={() => '确定要删除这条值班记录吗？'}
    />
  )
}
