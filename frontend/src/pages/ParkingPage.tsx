import CrudPage from '../components/CrudPage'
import { CarFront } from 'lucide-react'
import { parkingApi } from '../api'
import { PARKING_STATUS_OPTIONS, getStatusBadgeClass } from '../constants'
import type { Parking } from '../types'

const columns = [
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'spotNumber', title: '车位编号' },
  { key: 'licensePlate', title: '车牌号' },
  { key: 'ownerName', title: '业主' },
  { key: 'status', title: '状态', width: '80px', render: (item: Parking) => (
    <span className={`badge ${getStatusBadgeClass(item.status)}`}>{item.status}</span>
  )},
]

const fields = [
  { name: 'spotNumber', label: '车位编号', required: true },
  { name: 'licensePlate', label: '车牌号', pattern: '^$|^[一-龥][A-Z][A-Z0-9]{5,6}$', patternMessage: '请输入正确的车牌号，如京A12345' },
  { name: 'ownerId', label: '业主ID', type: 'number' as const },
  { name: 'status', label: '状态', type: 'select' as const, options: PARKING_STATUS_OPTIONS },
]

export default function ParkingPage() {
  return (
    <CrudPage<Parking>
      title="停车位管理" subtitle="管理车位分配与状态" icon={CarFront} addLabel="新增车位"
      api={parkingApi}
      searchFields={[
        { key: 'spotNumber', placeholder: '搜索车位编号' },
        { key: 'status', placeholder: '全部状态', type: 'select', options: PARKING_STATUS_OPTIONS },
      ]}
      columns={columns} fields={fields}
      getInitialValues={(item) => ({
        spotNumber: item.spotNumber, licensePlate: item.licensePlate ?? '',
        ownerId: item.ownerId != null ? String(item.ownerId) : '', status: item.status ?? '',
      })}
      getDeleteMessage={(item) => `确定要删除车位「${item.spotNumber}」吗？`}
    />
  )
}
