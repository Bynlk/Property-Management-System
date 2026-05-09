import { useCallback, useEffect, useState } from 'react'
import { Plus, Pencil, Trash2, CalendarClock } from 'lucide-react'
import DataTable from '../components/DataTable'
import FormModal from '../components/FormModal'
import ConfirmDialog from '../components/ConfirmDialog'
import { dutyApi } from '../api'
import type { Duty } from '../types'

const fields = [
  { name: 'employeeId', label: '员工ID', required: true, type: 'number' as const },
  { name: 'dutyDate', label: '值班日期', type: 'date' as const, required: true },
  { name: 'shift', label: '班次', type: 'select' as const, required: true, options: [{ value: '早班', label: '早班' }, { value: '中班', label: '中班' }, { value: '晚班', label: '晚班' }] },
]

export default function DutyPage() {
  const [data, setData] = useState<Duty[]>([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(1)
  const [loading, setLoading] = useState(false)
  const [shift, setShift] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Duty | null>(null)
  const [deleteItem, setDeleteItem] = useState<Duty | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const fetchData = useCallback(async () => {
    setLoading(true)
    try {
      const { data: res } = await dutyApi.page({ shift, pageNum, pageSize: 10 })
      setData(res.list); setTotal(res.total)
    } catch { /* ignore */ }
    finally { setLoading(false) }
  }, [shift, pageNum])

  useEffect(() => { fetchData() }, [fetchData])

  const handleSubmit = async (formData: Record<string, string>) => {
    setSubmitting(true)
    try {
      if (editItem) { await dutyApi.update({ ...formData, id: editItem.id } as unknown as Partial<Duty>) }
      else { await dutyApi.add(formData as unknown as Partial<Duty>) }
      setModalOpen(false); setEditItem(null); fetchData()
    } finally { setSubmitting(false) }
  }

  const handleDelete = async () => {
    if (!deleteItem) return
    setSubmitting(true)
    try { await dutyApi.delete(deleteItem.id); setDeleteItem(null); fetchData() }
    finally { setSubmitting(false) }
  }

  const shiftColor = (s: string) => s === '早班' ? 'badge-warning' : s === '中班' ? 'badge-info' : 'badge-purple'

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl liquid-glass-icon flex items-center justify-center">
            <CalendarClock size={20} className="relative z-10 text-white/70" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">值班管理</h1>
            <p className="text-xs text-text-secondary mt-0.5">管理员工值班排班</p>
          </div>
        </div>
        <button onClick={() => { setEditItem(null); setModalOpen(true) }} className="btn-primary flex items-center gap-2 px-5 py-2.5 text-sm"><Plus size={16} /> 新增值班</button>
      </div>

      <div className="glass p-4 flex gap-3 flex-wrap items-center">
        <select value={shift} onChange={(e) => { setShift(e.target.value); setPageNum(1) }} className="px-4 py-2.5 input-glass text-sm appearance-none cursor-pointer min-w-[140px]" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%238E94B0' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`, backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center' }}>
          <option value="">全部班次</option><option value="早班">早班</option><option value="中班">中班</option><option value="晚班">晚班</option>
        </select>
      </div>

      <DataTable
        columns={[
          { key: 'id', title: 'ID', width: '60px' },
          { key: 'employeeName', title: '员工' },
          { key: 'dutyDate', title: '值班日期' },
          { key: 'shift', title: '班次', width: '80px', render: (item: Duty) => <span className={`badge ${shiftColor(item.shift)}`}>{item.shift}</span> },
          { key: 'actions', title: '操作', width: '120px', render: (item: Duty) => (
            <div className="flex gap-1">
              <button onClick={() => { setEditItem(item); setModalOpen(true) }} className="p-1.5 rounded-lg hover:bg-white/8 text-white/60 transition-colors"><Pencil size={14} /></button>
              <button onClick={() => setDeleteItem(item)} className="p-1.5 rounded-lg hover:bg-danger/10 text-danger transition-colors"><Trash2 size={14} /></button>
            </div>
          )},
        ]}
        data={data as unknown as Record<string, unknown>[]}
        total={total} pageNum={pageNum} pageSize={10} onPageChange={setPageNum} loading={loading}
      />
      <FormModal title={editItem ? '编辑值班' : '新增值班'} open={modalOpen} onClose={() => { setModalOpen(false); setEditItem(null) }} onSubmit={handleSubmit} fields={fields} initialValues={editItem ? { employeeId: String(editItem.employeeId), dutyDate: editItem.dutyDate, shift: editItem.shift } : undefined} loading={submitting} />
      <ConfirmDialog open={!!deleteItem} message={`确定要删除这条值班记录吗？`} onConfirm={handleDelete} onCancel={() => setDeleteItem(null)} loading={submitting} />
    </div>
  )
}
