import { useCallback, useEffect, useState } from 'react'
import { Plus, Search, Pencil, Trash2, Building2 } from 'lucide-react'
import DataTable from '../components/DataTable'
import FormModal from '../components/FormModal'
import ConfirmDialog from '../components/ConfirmDialog'
import { employeeApi } from '../api'
import type { Employee } from '../types'

const fields = [
  { name: 'name', label: '姓名', required: true },
  { name: 'gender', label: '性别', type: 'select' as const, options: [{ value: '男', label: '男' }, { value: '女', label: '女' }] },
  { name: 'phone', label: '手机号' },
  { name: 'position', label: '岗位' },
  { name: 'hireDate', label: '入职日期', type: 'date' as const },
]

export default function EmployeePage() {
  const [data, setData] = useState<Employee[]>([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(1)
  const [loading, setLoading] = useState(false)
  const [searchName, setSearchName] = useState('')
  const [searchPosition, setSearchPosition] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Employee | null>(null)
  const [deleteItem, setDeleteItem] = useState<Employee | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const fetchData = useCallback(async () => {
    setLoading(true)
    try {
      const { data: res } = await employeeApi.page({ name: searchName, position: searchPosition, pageNum, pageSize: 10 })
      setData(res.list)
      setTotal(res.total)
    } catch { /* ignore */ }
    finally { setLoading(false) }
  }, [searchName, searchPosition, pageNum])

  useEffect(() => { fetchData() }, [fetchData])

  const handleSubmit = async (formData: Record<string, string>) => {
    setSubmitting(true)
    try {
      if (editItem) {
        await employeeApi.update({ ...formData, id: editItem.id } as unknown as Partial<Employee>)
      } else {
        await employeeApi.add(formData as unknown as Partial<Employee>)
      }
      setModalOpen(false); setEditItem(null); fetchData()
    } finally { setSubmitting(false) }
  }

  const handleDelete = async () => {
    if (!deleteItem) return
    setSubmitting(true)
    try { await employeeApi.delete(deleteItem.id); setDeleteItem(null); fetchData() }
    finally { setSubmitting(false) }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl liquid-glass-icon flex items-center justify-center">
            <Building2 size={20} className="relative z-10 text-white/70" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">员工管理</h1>
            <p className="text-xs text-text-secondary mt-0.5">管理员工信息与岗位</p>
          </div>
        </div>
        <button onClick={() => { setEditItem(null); setModalOpen(true) }} className="btn-primary flex items-center gap-2 px-5 py-2.5 text-sm">
          <Plus size={16} /> 新增员工
        </button>
      </div>

      <div className="glass p-4 flex gap-3 flex-wrap items-center">
        <div className="relative flex-1 min-w-[200px]">
          <Search size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-text-secondary" />
          <input placeholder="搜索姓名" value={searchName} onChange={(e) => { setSearchName(e.target.value); setPageNum(1) }} className="w-full pl-10 pr-4 py-2.5 input-glass text-sm" />
        </div>
        <input placeholder="搜索岗位" value={searchPosition} onChange={(e) => { setSearchPosition(e.target.value); setPageNum(1) }} className="flex-1 min-w-[200px] px-4 py-2.5 input-glass text-sm" />
      </div>

      <DataTable
        columns={[
          { key: 'id', title: 'ID', width: '60px' },
          { key: 'name', title: '姓名' },
          { key: 'gender', title: '性别', width: '80px' },
          { key: 'phone', title: '手机号' },
          { key: 'position', title: '岗位' },
          { key: 'hireDate', title: '入职日期' },
          { key: 'actions', title: '操作', width: '120px', render: (item: Employee) => (
            <div className="flex gap-1">
              <button onClick={() => { setEditItem(item); setModalOpen(true) }} className="p-1.5 rounded-lg hover:bg-white/8 text-white/60 transition-colors"><Pencil size={14} /></button>
              <button onClick={() => setDeleteItem(item)} className="p-1.5 rounded-lg hover:bg-danger/10 text-danger transition-colors"><Trash2 size={14} /></button>
            </div>
          )},
        ]}
        data={data as unknown as Record<string, unknown>[]}
        total={total} pageNum={pageNum} pageSize={10} onPageChange={setPageNum} loading={loading}
      />
      <FormModal title={editItem ? '编辑员工' : '新增员工'} open={modalOpen} onClose={() => { setModalOpen(false); setEditItem(null) }} onSubmit={handleSubmit} fields={fields} initialValues={editItem ? { name: editItem.name, gender: editItem.gender, phone: editItem.phone, position: editItem.position, hireDate: editItem.hireDate } : undefined} loading={submitting} />
      <ConfirmDialog open={!!deleteItem} message={`确定要删除员工「${deleteItem?.name}」吗？`} onConfirm={handleDelete} onCancel={() => setDeleteItem(null)} loading={submitting} />
    </div>
  )
}
