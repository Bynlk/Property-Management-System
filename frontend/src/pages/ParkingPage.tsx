import { useCallback, useEffect, useState } from 'react'
import { Plus, Search, Pencil, Trash2, CarFront } from 'lucide-react'
import DataTable from '../components/DataTable'
import FormModal from '../components/FormModal'
import ConfirmDialog from '../components/ConfirmDialog'
import { parkingApi } from '../api'
import type { Parking } from '../types'

const fields = [
  { name: 'spotNumber', label: '车位编号', required: true },
  { name: 'licensePlate', label: '车牌号' },
  { name: 'ownerId', label: '业主ID', type: 'number' as const },
  { name: 'status', label: '状态', type: 'select' as const, options: [{ value: '使用中', label: '使用中' }, { value: '空闲', label: '空闲' }] },
]

export default function ParkingPage() {
  const [data, setData] = useState<Parking[]>([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(1)
  const [loading, setLoading] = useState(false)
  const [searchSpot, setSearchSpot] = useState('')
  const [searchStatus, setSearchStatus] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Parking | null>(null)
  const [deleteItem, setDeleteItem] = useState<Parking | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const fetchData = useCallback(async () => {
    setLoading(true)
    try {
      const { data: res } = await parkingApi.page({ spotNumber: searchSpot, status: searchStatus, pageNum, pageSize: 10 })
      setData(res.list); setTotal(res.total)
    } catch { /* ignore */ }
    finally { setLoading(false) }
  }, [searchSpot, searchStatus, pageNum])

  useEffect(() => { fetchData() }, [fetchData])

  const handleSubmit = async (formData: Record<string, string>) => {
    setSubmitting(true)
    try {
      if (editItem) { await parkingApi.update({ ...formData, id: editItem.id } as unknown as Partial<Parking>) }
      else { await parkingApi.add(formData as unknown as Partial<Parking>) }
      setModalOpen(false); setEditItem(null); fetchData()
    } finally { setSubmitting(false) }
  }

  const handleDelete = async () => {
    if (!deleteItem) return
    setSubmitting(true)
    try { await parkingApi.delete(deleteItem.id); setDeleteItem(null); fetchData() }
    finally { setSubmitting(false) }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl liquid-glass-icon flex items-center justify-center">
            <CarFront size={20} className="relative z-10 text-white/70" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">停车位管理</h1>
            <p className="text-xs text-text-secondary mt-0.5">管理车位分配与状态</p>
          </div>
        </div>
        <button onClick={() => { setEditItem(null); setModalOpen(true) }} className="btn-primary flex items-center gap-2 px-5 py-2.5 text-sm"><Plus size={16} /> 新增车位</button>
      </div>

      <div className="glass p-4 flex gap-3 flex-wrap items-center">
        <div className="relative flex-1 min-w-[200px]">
          <Search size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-text-secondary" />
          <input placeholder="搜索车位编号" value={searchSpot} onChange={(e) => { setSearchSpot(e.target.value); setPageNum(1) }} className="w-full pl-10 pr-4 py-2.5 input-glass text-sm" />
        </div>
        <select value={searchStatus} onChange={(e) => { setSearchStatus(e.target.value); setPageNum(1) }} className="px-4 py-2.5 input-glass text-sm appearance-none cursor-pointer min-w-[140px]" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%238E94B0' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`, backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center' }}>
          <option value="">全部状态</option><option value="使用中">使用中</option><option value="空闲">空闲</option>
        </select>
      </div>

      <DataTable
        columns={[
          { key: 'id', title: 'ID', width: '60px' },
          { key: 'spotNumber', title: '车位编号' },
          { key: 'licensePlate', title: '车牌号' },
          { key: 'ownerName', title: '业主' },
          { key: 'status', title: '状态', width: '80px', render: (item: Parking) => (
            <span className={`badge ${item.status === '使用中' ? 'badge-info' : 'badge-success'}`}>{item.status}</span>
          )},
          { key: 'actions', title: '操作', width: '120px', render: (item: Parking) => (
            <div className="flex gap-1">
              <button onClick={() => { setEditItem(item); setModalOpen(true) }} className="p-1.5 rounded-lg hover:bg-white/8 text-white/60 transition-colors"><Pencil size={14} /></button>
              <button onClick={() => setDeleteItem(item)} className="p-1.5 rounded-lg hover:bg-danger/10 text-danger transition-colors"><Trash2 size={14} /></button>
            </div>
          )},
        ]}
        data={data as unknown as Record<string, unknown>[]}
        total={total} pageNum={pageNum} pageSize={10} onPageChange={setPageNum} loading={loading}
      />
      <FormModal title={editItem ? '编辑车位' : '新增车位'} open={modalOpen} onClose={() => { setModalOpen(false); setEditItem(null) }} onSubmit={handleSubmit} fields={fields} initialValues={editItem ? { spotNumber: editItem.spotNumber, licensePlate: editItem.licensePlate || '', ownerId: editItem.ownerId ? String(editItem.ownerId) : '', status: editItem.status } : undefined} loading={submitting} />
      <ConfirmDialog open={!!deleteItem} message={`确定要删除车位「${deleteItem?.spotNumber}」吗？`} onConfirm={handleDelete} onCancel={() => setDeleteItem(null)} loading={submitting} />
    </div>
  )
}
