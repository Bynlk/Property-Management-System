import { useCallback, useEffect, useState } from 'react'
import { Plus, Pencil, Trash2, Wrench } from 'lucide-react'
import DataTable from '../components/DataTable'
import FormModal from '../components/FormModal'
import ConfirmDialog from '../components/ConfirmDialog'
import { repairApi } from '../api'
import type { Repair } from '../types'

const fields = [
  { name: 'ownerId', label: '业主ID', required: true, type: 'number' as const },
  { name: 'deviceName', label: '设备名称', required: true },
  { name: 'faultDescription', label: '故障描述', type: 'textarea' as const },
  { name: 'repairPerson', label: '维修人员' },
  { name: 'status', label: '状态', type: 'select' as const, options: [{ value: '待维修', label: '待维修' }, { value: '维修中', label: '维修中' }, { value: '已完成', label: '已完成' }] },
]

export default function RepairPage() {
  const [data, setData] = useState<Repair[]>([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(1)
  const [loading, setLoading] = useState(false)
  const [status, setStatus] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Repair | null>(null)
  const [deleteItem, setDeleteItem] = useState<Repair | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const fetchData = useCallback(async () => {
    setLoading(true)
    try {
      const { data: res } = await repairApi.page({ status, pageNum, pageSize: 10 })
      setData(res.list); setTotal(res.total)
    } catch { /* ignore */ }
    finally { setLoading(false) }
  }, [status, pageNum])

  useEffect(() => { fetchData() }, [fetchData])

  const handleSubmit = async (formData: Record<string, string>) => {
    setSubmitting(true)
    try {
      if (editItem) { await repairApi.update({ ...formData, id: editItem.id } as unknown as Partial<Repair>) }
      else { await repairApi.add(formData as unknown as Partial<Repair>) }
      setModalOpen(false); setEditItem(null); fetchData()
    } finally { setSubmitting(false) }
  }

  const handleDelete = async () => {
    if (!deleteItem) return
    setSubmitting(true)
    try { await repairApi.delete(deleteItem.id); setDeleteItem(null); fetchData() }
    finally { setSubmitting(false) }
  }

  const statusColor = (s: string) => s === '待维修' ? 'badge-warning' : s === '维修中' ? 'badge-info' : 'badge-success'

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl liquid-glass-icon flex items-center justify-center">
            <Wrench size={20} className="relative z-10 text-white/70" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">报修管理</h1>
            <p className="text-xs text-text-secondary mt-0.5">管理设备报修工单</p>
          </div>
        </div>
        <button onClick={() => { setEditItem(null); setModalOpen(true) }} className="btn-primary flex items-center gap-2 px-5 py-2.5 text-sm"><Plus size={16} /> 新增报修</button>
      </div>

      <div className="glass p-4 flex gap-3 flex-wrap items-center">
        <select value={status} onChange={(e) => { setStatus(e.target.value); setPageNum(1) }} className="px-4 py-2.5 input-glass text-sm appearance-none cursor-pointer min-w-[140px]" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%238E94B0' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`, backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center' }}>
          <option value="">全部状态</option><option value="待维修">待维修</option><option value="维修中">维修中</option><option value="已完成">已完成</option>
        </select>
      </div>

      <DataTable
        columns={[
          { key: 'id', title: 'ID', width: '60px' },
          { key: 'ownerName', title: '业主' },
          { key: 'deviceName', title: '设备名称' },
          { key: 'faultDescription', title: '故障描述' },
          { key: 'repairPerson', title: '维修人员' },
          { key: 'status', title: '状态', width: '90px', render: (item: Repair) => <span className={`badge ${statusColor(item.status)}`}>{item.status}</span> },
          { key: 'actions', title: '操作', width: '120px', render: (item: Repair) => (
            <div className="flex gap-1">
              <button onClick={() => { setEditItem(item); setModalOpen(true) }} className="p-1.5 rounded-lg hover:bg-white/8 text-white/60 transition-colors"><Pencil size={14} /></button>
              <button onClick={() => setDeleteItem(item)} className="p-1.5 rounded-lg hover:bg-danger/10 text-danger transition-colors"><Trash2 size={14} /></button>
            </div>
          )},
        ]}
        data={data as unknown as Record<string, unknown>[]}
        total={total} pageNum={pageNum} pageSize={10} onPageChange={setPageNum} loading={loading}
      />
      <FormModal title={editItem ? '编辑报修' : '新增报修'} open={modalOpen} onClose={() => { setModalOpen(false); setEditItem(null) }} onSubmit={handleSubmit} fields={fields} initialValues={editItem ? { ownerId: String(editItem.ownerId), deviceName: editItem.deviceName, faultDescription: editItem.faultDescription, repairPerson: editItem.repairPerson || '', status: editItem.status } : undefined} loading={submitting} />
      <ConfirmDialog open={!!deleteItem} message={`确定要删除这条报修记录吗？`} onConfirm={handleDelete} onCancel={() => setDeleteItem(null)} loading={submitting} />
    </div>
  )
}
