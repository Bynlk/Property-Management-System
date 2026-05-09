import { useCallback, useEffect, useState } from 'react'
import { Plus, Pencil, Trash2, DollarSign } from 'lucide-react'
import DataTable from '../components/DataTable'
import FormModal from '../components/FormModal'
import ConfirmDialog from '../components/ConfirmDialog'
import { feeApi } from '../api'
import type { Fee } from '../types'

const fields = [
  { name: 'ownerId', label: '业主ID', required: true, type: 'number' as const },
  { name: 'houseId', label: '房屋ID', type: 'number' as const },
  { name: 'feeType', label: '费用类型', type: 'select' as const, required: true, options: [{ value: '物业费', label: '物业费' }, { value: '水费', label: '水费' }, { value: '电费', label: '电费' }, { value: '燃气费', label: '燃气费' }] },
  { name: 'amount', label: '金额', type: 'number' as const, required: true },
  { name: 'shouldPayDate', label: '应缴日期', type: 'date' as const, required: true },
  { name: 'status', label: '状态', type: 'select' as const, options: [{ value: '未缴', label: '未缴' }, { value: '已缴', label: '已缴' }] },
]

export default function FeePage() {
  const [data, setData] = useState<Fee[]>([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(1)
  const [loading, setLoading] = useState(false)
  const [feeType, setFeeType] = useState('')
  const [status, setStatus] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Fee | null>(null)
  const [deleteItem, setDeleteItem] = useState<Fee | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const fetchData = useCallback(async () => {
    setLoading(true)
    try {
      const { data: res } = await feeApi.page({ feeType, status, pageNum, pageSize: 10 })
      setData(res.list); setTotal(res.total)
    } catch { /* ignore */ }
    finally { setLoading(false) }
  }, [feeType, status, pageNum])

  useEffect(() => { fetchData() }, [fetchData])

  const handleSubmit = async (formData: Record<string, string>) => {
    setSubmitting(true)
    try {
      if (editItem) { await feeApi.update({ ...formData, id: editItem.id } as unknown as Partial<Fee>) }
      else { await feeApi.add(formData as unknown as Partial<Fee>) }
      setModalOpen(false); setEditItem(null); fetchData()
    } finally { setSubmitting(false) }
  }

  const handleDelete = async () => {
    if (!deleteItem) return
    setSubmitting(true)
    try { await feeApi.delete(deleteItem.id); setDeleteItem(null); fetchData() }
    finally { setSubmitting(false) }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl liquid-glass-icon flex items-center justify-center">
            <DollarSign size={20} className="relative z-10 text-white/70" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">费用管理</h1>
            <p className="text-xs text-text-secondary mt-0.5">管理物业费用收缴</p>
          </div>
        </div>
        <button onClick={() => { setEditItem(null); setModalOpen(true) }} className="btn-primary flex items-center gap-2 px-5 py-2.5 text-sm"><Plus size={16} /> 新增费用</button>
      </div>

      <div className="glass p-4 flex gap-3 flex-wrap items-center">
        <select value={feeType} onChange={(e) => { setFeeType(e.target.value); setPageNum(1) }} className="px-4 py-2.5 input-glass text-sm appearance-none cursor-pointer min-w-[140px]" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%238E94B0' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`, backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center' }}>
          <option value="">全部类型</option><option value="物业费">物业费</option><option value="水费">水费</option><option value="电费">电费</option><option value="燃气费">燃气费</option>
        </select>
        <select value={status} onChange={(e) => { setStatus(e.target.value); setPageNum(1) }} className="px-4 py-2.5 input-glass text-sm appearance-none cursor-pointer min-w-[140px]" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%238E94B0' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`, backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center' }}>
          <option value="">全部状态</option><option value="未缴">未缴</option><option value="已缴">已缴</option>
        </select>
      </div>

      <DataTable
        columns={[
          { key: 'id', title: 'ID', width: '60px' },
          { key: 'ownerName', title: '业主' },
          { key: 'feeType', title: '费用类型', width: '100px' },
          { key: 'amount', title: '金额', width: '100px', render: (item: Fee) => <span className="font-medium tabular-nums">¥{item.amount}</span> },
          { key: 'shouldPayDate', title: '应缴日期' },
          { key: 'status', title: '状态', width: '80px', render: (item: Fee) => (
            <span className={`badge ${item.status === '已缴' ? 'badge-success' : 'badge-danger'}`}>{item.status}</span>
          )},
          { key: 'actions', title: '操作', width: '120px', render: (item: Fee) => (
            <div className="flex gap-1">
              <button onClick={() => { setEditItem(item); setModalOpen(true) }} className="p-1.5 rounded-lg hover:bg-white/8 text-white/60 transition-colors"><Pencil size={14} /></button>
              <button onClick={() => setDeleteItem(item)} className="p-1.5 rounded-lg hover:bg-danger/10 text-danger transition-colors"><Trash2 size={14} /></button>
            </div>
          )},
        ]}
        data={data as unknown as Record<string, unknown>[]}
        total={total} pageNum={pageNum} pageSize={10} onPageChange={setPageNum} loading={loading}
      />
      <FormModal title={editItem ? '编辑费用' : '新增费用'} open={modalOpen} onClose={() => { setModalOpen(false); setEditItem(null) }} onSubmit={handleSubmit} fields={fields} initialValues={editItem ? { ownerId: String(editItem.ownerId), houseId: String(editItem.houseId || ''), feeType: editItem.feeType, amount: String(editItem.amount), shouldPayDate: editItem.shouldPayDate, status: editItem.status } : undefined} loading={submitting} />
      <ConfirmDialog open={!!deleteItem} message={`确定要删除这笔费用记录吗？`} onConfirm={handleDelete} onCancel={() => setDeleteItem(null)} loading={submitting} />
    </div>
  )
}
