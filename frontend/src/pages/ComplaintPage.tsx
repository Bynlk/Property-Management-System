import CrudPage from '../components/CrudPage'
import { AlertCircle } from 'lucide-react'
import { complaintApi } from '../api'
import { COMPLAINT_STATUS_OPTIONS, getStatusBadgeClass } from '../constants'
import type { Complaint } from '../types'

const columns = [
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'ownerName', title: '业主' },
  { key: 'title', title: '标题' },
  { key: 'status', title: '状态', width: '90px', render: (item: Complaint) => (
    <span className={`badge ${getStatusBadgeClass(item.status)}`}>{item.status}</span>
  )},
]

const fields = [
  { name: 'ownerId', label: '业主ID', required: true, type: 'number' as const },
  { name: 'title', label: '投诉标题', required: true },
  { name: 'content', label: '投诉内容', type: 'textarea' as const },
  { name: 'status', label: '状态', type: 'select' as const, options: COMPLAINT_STATUS_OPTIONS },
]

export default function ComplaintPage() {
  return (
    <CrudPage<Complaint>
      title="投诉管理" subtitle="处理业主投诉工单" icon={AlertCircle} addLabel="新增投诉"
      api={complaintApi}
      searchFields={[
        { key: 'status', placeholder: '全部状态', type: 'select', options: COMPLAINT_STATUS_OPTIONS },
      ]}
      columns={columns} fields={fields}
      getInitialValues={(item) => ({
        ownerId: String(item.ownerId), title: item.title,
        content: item.content, status: item.status ?? '',
      })}
      getDeleteMessage={(item) => `确定要删除投诉「${item.title}」吗？`}
    />
  )
}
