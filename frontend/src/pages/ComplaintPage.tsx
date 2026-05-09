import { useCallback, useEffect, useState } from 'react'
import { Plus, Pencil, Trash2, AlertCircle } from 'lucide-react'
import DataTable from '../components/DataTable'
import FormModal from '../components/FormModal'
import ConfirmDialog from '../components/ConfirmDialog'
import { complaintApi } from '../api'
import type { Complaint } from '../types'

const fields = [
  { name: 'ownerId', label: '业主ID', required: true, type: 'number' as const },
  { name: 'title', label: '投诉标题', required: true },
  { name: 'content', label: '投诉内容', type: 'textarea' as const },
  { name: 'status', label: '状态', type: 'select' as const, options: [{ value: '待处理', label: '待处理' }, { value: '处理中', label: '处理中' }, { value: '已处理', label: '已处理' }] },
]

export default function ComplaintPage() {
  const [data, setData] = useState<Complaint[]>([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(1)
  const [loading, setLoading] = useState(false)
  const [status, setStatus] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Complaint | null>(null)
  const [deleteItem, setDeleteItem] = useState<Complaint | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const fetchData = useCallback(async () => {
    setLoading(true)
    try {
      const { data: res } = await complaintApi.page({ status, pageNum, pageSize: 10 })
      setData(res.list); setTotal(res.total)
    } catch { /* ignore */ }
    finally { setLoading(false) }
  }, [status, pageNum])

  useEffect(() => { fetchData() }, [fetchData])

  const handleSubmit = async (formData: Record<string, string>) => {
    setSubmitting(true)
    try {
      if (editItem) { await complaintApi.update({ ...formData, id: editItem.id } as unknown as Partial<Complaint>) }
      else { await complaintApi.add(formData as unknown as Partial<Complaint>) }
      setModalOpen(false); setEditItem(null); fetchData()
    } finally { setSubmitting(false) }
  }

  const handleDelete = async () => {
    if (!deleteItem) return
    setSubmitting(true)
    try { await complaintApi.delete(deleteItem.id); setDeleteItem(null); fetchData() }
    finally { setSubmitting(false) }
  }

  const statusColor = (s: string) => s === '待处理' ? 'badge-warning' : s === '处理中' ? 'badge-info' : 'badge-success'

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl liquid-glass-icon flex items-center justify-center">
            <AlertCircle size={20} className="relative z-10 text-white/70" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">投诉管理</h1>
            <p className="text-xs text-text-secondary mt-0.5">处理业主投诉工单</p>
          </div>
        </div>
        <button onClick={() => { setEditItem(null); setModalOpen(true) }} className="btn-primary flex items-center gap-2 px-5 py-2.5 text-sm"><Plus size={16} /> 新增投诉</button>
      </div>

      <div className="glass p-4 flex gap-3 flex-wrap items-center">
        <select value={status} onChange={(e) => { setStatus(e.target.value); setPageNum(1) }} className="px-4 py-2.5 input-glass text-sm appearance-none cursor-pointer min-w-[140px]" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%238E94B0' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`, backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center' }}>
          <option value="">全部状态</option><option value="待处理">待处理</option><option value="处理中">处理中</option><option value="已处理">已处理</option>
        </select>
      </div>

      <DataTable
        columns={[
          { key: 'id', title: 'ID', width: '60px' },
          { key: 'ownerName', title: '业主' },
          { key: 'title', title: '标题' },
          { key: 'createTime', title: '投诉时间' },
          { key: 'status', title: '状态', width: '90px', render: (item: Complaint) => <span className={`badge ${statusColor(item.status)}`}>{item.status}</span> },
          { key: 'actions', title: '操作', width: '120px', render: (item: Complaint) => (
            <div className="flex gap-1">
              <button onClick={() => { setEditItem(item); setModalOpen(true) }} className="p-1.5 rounded-lg hover:bg-white/8 text-white/60 transition-colors"><Pencil size={14} /></button>
              <button onClick={() => setDeleteItem(item)} className="p-1.5 rounded-lg hover:bg-danger/10 text-danger transition-colors"><Trash2 size={14} /></button>
            </div>
          )},
        ]}
        data={data as unknown as Record<string, unknown>[]}
        total={total} pageNum={pageNum} pageSize={10} onPageChange={setPageNum} loading={loading}
      />
      <FormModal title={editItem ? '编辑投诉' : '新增投诉'} open={modalOpen} onClose={() => { setModalOpen(false); setEditItem(null) }} onSubmit={handleSubmit} fields={fields} initialValues={editItem ? { ownerId: String(editItem.ownerId), title: editItem.title, content: editItem.content, status: editItem.status } : undefined} loading={submitting} />
      <ConfirmDialog open={!!deleteItem} message={`确定要删除投诉「${deleteItem?.title}」吗？`} onConfirm={handleDelete} onCancel={() => setDeleteItem(null)} loading={submitting} />
    </div>
  )
}
