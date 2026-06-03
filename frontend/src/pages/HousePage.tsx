import CrudPage from '../components/CrudPage'
import { Home } from 'lucide-react'
import { houseApi } from '../api'
import { HOUSE_STATUS_OPTIONS, getStatusBadgeClass } from '../constants'
import type { House } from '../types'

const columns = [
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'building', title: '楼栋' },
  { key: 'unit', title: '单元', width: '80px' },
  { key: 'roomNumber', title: '房间号' },
  { key: 'area', title: '面积(㎡)', width: '100px' },
  { key: 'houseType', title: '户型' },
  { key: 'status', title: '状态', width: '90px', render: (item: House) => (
    <span className={`badge ${getStatusBadgeClass(item.status)}`}>{item.status}</span>
  )},
]

const fields = [
  { name: 'building', label: '楼栋号', required: true },
  { name: 'unit', label: '单元号' },
  { name: 'roomNumber', label: '房间号', required: true },
  { name: 'area', label: '面积(㎡)', type: 'number' as const, min: 1, max: 9999 },
  { name: 'houseType', label: '户型' },
  { name: 'status', label: '状态', type: 'select' as const, options: HOUSE_STATUS_OPTIONS },
]

export default function HousePage() {
  return (
    <CrudPage<House>
      title="房屋管理" subtitle="管理房屋信息与状态" icon={Home} addLabel="新增房屋"
      api={houseApi}
      searchFields={[
        { key: 'building', placeholder: '搜索楼栋' },
        { key: 'status', placeholder: '全部状态', type: 'select', options: HOUSE_STATUS_OPTIONS },
      ]}
      columns={columns} fields={fields}
      getInitialValues={(item) => ({
        building: item.building, unit: item.unit, roomNumber: item.roomNumber,
        area: String(item.area ?? ''), houseType: item.houseType, status: item.status ?? '',
      })}
      getDeleteMessage={(item) => `确定要删除房屋「${item.building}-${item.unit}-${item.roomNumber}」吗？`}
    />
  )
}
