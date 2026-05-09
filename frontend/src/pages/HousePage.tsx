import { useCallback, useEffect, useState } from 'react'
import { Plus, Search, Pencil, Trash2, Home } from 'lucide-react'
import DataTable from '../components/DataTable'
import FormModal from '../components/FormModal'
import ConfirmDialog from '../components/ConfirmDialog'
import { houseApi } from '../api'
import type { House } from '../types'

const fields = [
  { name: 'building', label: '楼栋号', required: true },
  { name: 'unit', label: '单元号' },
  { name: 'roomNumber', label: '房间号', required: true },
  { name: 'area', label: '面积(㎡)', type: 'number' as const },
  { name: 'houseType', label: '户型' },
  { name: 'status', label: '状态', type: 'select' as const, options: [{ value: '已入住', label: '已入住' }, { value: '空置', label: '空置' }, { value: '装修中', label: '装修中' }] },
]

export default function HousePage() {
  const [data, setData] = useState<House[]>([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(1)
  const [loading, setLoading] = useState(false)
  const [searchBuilding, setSearchBuilding] = useState('')
  const [searchStatus, setSearchStatus] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<House | null>(null)
  const [deleteItem, setDeleteItem] = useState<House | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const fetchData = useCallback(async () => {
    setLoading(true)
    try {
      const { data: res } = await houseApi.page({ building: searchBuilding, status: searchStatus, pageNum, pageSize: 10 })
      setData(res.list); setTotal(res.total)
    } catch { /* ignore */ }
    finally { setLoading(false) }
  }, [searchBuilding, searchStatus, pageNum])

  useEffect(() => { fetchData() }, [fetchData])

  const handleSubmit = async (formData: Record<string, string>) => {
    setSubmitting(true)
    try {
      if (editItem) { await houseApi.update({ ...formData, id: editItem.id } as unknown as Partial<House>) }
      else { await houseApi.add(formData as unknown as Partial<House>) }
      setModalOpen(false); setEditItem(null); fetchData()
    } finally { setSubmitting(false) }
  }

  const handleDelete = async () => {
    if (!deleteItem) return
    setSubmitting(true)
    try { await houseApi.delete(deleteItem.id); setDeleteItem(null); fetchData() }
    finally { setSubmitting(false) }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl liquid-glass-icon flex items-center justify-center">
            <Home size={20} className="relative z-10 text-white/70" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">房屋管理</h1>
            <p className="text-xs text-text-secondary mt-0.5">管理房屋信息与状态</p>
          </div>
        </div>
        <button onClick={() => { setEditItem(null); setModalOpen(true) }} className="btn-primary flex items-center gap-2 px-5 py-2.5 text-sm"><Plus size={16} /> 新增房屋</button>
      </div>

      <div className="glass p-4 flex gap-3 flex-wrap items-center">
        <div className="relative flex-1 min-w-[200px]">
          <Search size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-text-secondary" />
          <input placeholder="搜索楼栋" value={searchBuilding} onChange={(e) => { setSearchBuilding(e.target.value); setPageNum(1) }} className="w-full pl-10 pr-4 py-2.5 input-glass text-sm" />
        </div>
        <select value={searchStatus} onChange={(e) => { setSearchStatus(e.target.value); setPageNum(1) }} className="px-4 py-2.5 input-glass text-sm appearance-none cursor-pointer min-w-[140px]" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%238E94B0' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E")`, backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center' }}>
          <option value="">全部状态</option><option value="已入住">已入住</option><option value="空置">空置</option><option value="装修中">装修中</option>
        </select>
      </div>

      <DataTable
        columns={[
          { key: 'id', title: 'ID', width: '60px' },
          { key: 'building', title: '楼栋' },
          { key: 'unit', title: '单元', width: '80px' },
          { key: 'roomNumber', title: '房间号' },
          { key: 'area', title: '面积(㎡)', width: '100px' },
          { key: 'houseType', title: '户型' },
          { key: 'status', title: '状态', width: '90px', render: (item: House) => (
            <span className={`badge ${item.status === '已入住' ? 'badge-success' : item.status === '空置' ? 'badge-info' : 'badge-warning'}`}>{item.status}</span>
          )},
          { key: 'actions', title: '操作', width: '120px', render: (item: House) => (
            <div className="flex gap-1">
              <button onClick={() => { setEditItem(item); setModalOpen(true) }} className="p-1.5 rounded-lg hover:bg-white/8 text-white/60 transition-colors"><Pencil size={14} /></button>
              <button onClick={() => setDeleteItem(item)} className="p-1.5 rounded-lg hover:bg-danger/10 text-danger transition-colors"><Trash2 size={14} /></button>
            </div>
          )},
        ]}
        data={data as unknown as Record<string, unknown>[]}
        total={total} pageNum={pageNum} pageSize={10} onPageChange={setPageNum} loading={loading}
      />
      <FormModal title={editItem ? '编辑房屋' : '新增房屋'} open={modalOpen} onClose={() => { setModalOpen(false); setEditItem(null) }} onSubmit={handleSubmit} fields={fields} initialValues={editItem ? { building: editItem.building, unit: editItem.unit, roomNumber: editItem.roomNumber, area: String(editItem.area || ''), houseType: editItem.houseType, status: editItem.status } : undefined} loading={submitting} />
      <ConfirmDialog open={!!deleteItem} message={`确定要删除「${deleteItem?.building} ${deleteItem?.roomNumber}」吗？`} onConfirm={handleDelete} onCancel={() => setDeleteItem(null)} loading={submitting} />
    </div>
  )
}
