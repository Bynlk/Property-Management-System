import CrudPage from '../components/CrudPage'
import { Wrench } from 'lucide-react'
import { repairApi } from '../api'
import { REPAIR_STATUS_OPTIONS, getStatusBadgeClass } from '../constants'
import type { Repair } from '../types'

const columns = [
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'ownerName', title: '业主' },
  { key: 'deviceName', title: '设备名称' },
  { key: 'faultDescription', title: '故障描述' },
  { key: 'repairEmployeeName', title: '维修人员' },
  { key: 'status', title: '状态', width: '90px', render: (item: Repair) => (
    <span className={`badge ${getStatusBadgeClass(item.status)}`}>{item.status}</span>
  )},
]

const fields = [
  { name: 'ownerId', label: '业主ID', required: true, type: 'number' as const },
  { name: 'deviceName', label: '设备名称', required: true },
  { name: 'faultDescription', label: '故障描述', type: 'textarea' as const },
  { name: 'repairEmployeeId', label: '维修人员ID', type: 'number' as const },
  { name: 'status', label: '状态', type: 'select' as const, options: REPAIR_STATUS_OPTIONS },
]

export default function RepairPage() {
  return (
    <CrudPage<Repair>
      title="报修管理" subtitle="管理设备报修工单" icon={Wrench} addLabel="新增报修"
      api={repairApi}
      searchFields={[
        { key: 'status', placeholder: '全部状态', type: 'select', options: REPAIR_STATUS_OPTIONS },
      ]}
      columns={columns} fields={fields}
      getInitialValues={(item) => ({
        ownerId: String(item.ownerId), deviceName: item.deviceName,
        faultDescription: item.faultDescription,
        repairEmployeeId: item.repairEmployeeId != null ? String(item.repairEmployeeId) : '',
        status: item.status ?? '',
      })}
      getDeleteMessage={() => '确定要删除这条报修记录吗？'}
    />
  )
}
