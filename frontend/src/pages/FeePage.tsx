import CrudPage from '../components/CrudPage'
import { DollarSign } from 'lucide-react'
import { feeApi } from '../api'
import { FEE_TYPE_OPTIONS, FEE_STATUS_OPTIONS, getStatusBadgeClass } from '../constants'
import type { Fee } from '../types'

const columns = [
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'ownerName', title: '业主' },
  { key: 'feeType', title: '费用类型', width: '100px' },
  { key: 'amount', title: '金额', width: '100px', render: (item: Fee) => <span className="font-medium tabular-nums">¥{item.amount}</span> },
  { key: 'shouldPayDate', title: '应缴日期' },
  { key: 'status', title: '状态', width: '80px', render: (item: Fee) => (
    <span className={`badge ${getStatusBadgeClass(item.status)}`}>{item.status}</span>
  )},
]

const fields = [
  { name: 'ownerId', label: '业主ID', required: true, type: 'number' as const },
  { name: 'houseId', label: '房屋ID', type: 'number' as const },
  { name: 'feeType', label: '费用类型', type: 'select' as const, required: true, options: FEE_TYPE_OPTIONS },
  { name: 'amount', label: '金额', type: 'number' as const, required: true },
  { name: 'shouldPayDate', label: '应缴日期', type: 'date' as const, required: true },
  { name: 'status', label: '状态', type: 'select' as const, options: FEE_STATUS_OPTIONS },
]

export default function FeePage() {
  return (
    <CrudPage<Fee>
      title="费用管理" subtitle="管理物业费用收缴" icon={DollarSign} addLabel="新增费用"
      api={feeApi}
      searchFields={[
        { key: 'feeType', placeholder: '全部类型', type: 'select', options: FEE_TYPE_OPTIONS },
        { key: 'status', placeholder: '全部状态', type: 'select', options: FEE_STATUS_OPTIONS },
      ]}
      columns={columns} fields={fields}
      getInitialValues={(item) => ({
        ownerId: String(item.ownerId), houseId: String(item.houseId ?? ''),
        feeType: item.feeType ?? '', amount: String(item.amount),
        shouldPayDate: item.shouldPayDate, status: item.status ?? '',
      })}
      getDeleteMessage={() => '确定要删除这笔费用记录吗？'}
    />
  )
}
