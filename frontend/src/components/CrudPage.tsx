import { Search, type LucideIcon } from 'lucide-react'
import DataTable from './DataTable'
import FormModal from './FormModal'
import ConfirmDialog from './ConfirmDialog'
import PageHeader from './PageHeader'
import ActionColumn from './ActionColumn'
import FilterSelect from './FilterSelect'
import { useCrudSearch } from '../hooks/useCrudSearch'
import { useCrudPage } from '../hooks/useCrudPage'

interface SearchField {
  key: string
  placeholder: string
  type?: 'text' | 'select'
  options?: { value: string; label: string }[]
}

interface ColumnDef<T> {
  key: string
  title: string
  width?: string
  render?: (item: T) => React.ReactNode
}

interface FieldDef {
  name: string
  label: string
  type?: 'text' | 'number' | 'date' | 'select' | 'textarea'
  options?: { value: string; label: string }[]
  required?: boolean
  pattern?: string
  patternMessage?: string
  min?: number
  max?: number
}

export interface CrudPageConfig<T extends { id: number }> {
  title: string
  subtitle: string
  icon: LucideIcon
  addLabel?: string
  api: {
    page: (params: Record<string, string | number | undefined>) => Promise<any>
    add: (data: Partial<T>) => Promise<any>
    update: (data: Partial<T> & { id: number }) => Promise<any>
    delete: (id: number) => Promise<any>
  }
  searchFields?: SearchField[]
  columns: ColumnDef<T>[]
  fields: FieldDef[]
  getInitialValues?: (item: T) => Record<string, string>
  getDeleteMessage?: (item: T) => string
}

/**
 * 配置驱动的通用 CRUD 页面组件
 * 将 8 个业务页面的重复逻辑抽象为纯配置
 */
export default function CrudPage<T extends { id: number }>(config: CrudPageConfig<T>) {
  const { title, subtitle, icon, addLabel, api, searchFields = [], columns, fields, getInitialValues, getDeleteMessage } = config

  const { values, setValue, searchParams } = useCrudSearch(searchFields)

  const {
    data, total, pageNum, loading,
    modalOpen, editItem, deleteItem, submitting,
    setPageNum, setDeleteItem,
    openAdd, openEdit, closeModal,
    handleSubmit, handleDelete,
  } = useCrudPage<T>({ api, searchParams })

  // 判断第一个搜索字段是否为文本类型（用于显示搜索图标）
  const firstIsText = searchFields.length > 0 && (searchFields[0].type ?? 'text') === 'text'

  return (
    <div className="space-y-6">
      <PageHeader icon={icon} title={title} subtitle={subtitle} addLabel={addLabel} onAdd={openAdd} />

      {searchFields.length > 0 && (
        <div className="glass p-4 flex gap-3 flex-wrap items-center">
          {searchFields.map((field, idx) => {
            const isText = (field.type ?? 'text') === 'text'
            if (isText) {
              return (
                <div key={field.key} className="relative flex-1 min-w-[200px]">
                  {firstIsText && idx === 0 && (
                    <Search size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-text-secondary" />
                  )}
                  <input
                    placeholder={field.placeholder}
                    value={values[field.key]}
                    onChange={(e) => { setValue(field.key, e.target.value); setPageNum(1) }}
                    className={`w-full py-2.5 input-glass text-sm ${firstIsText && idx === 0 ? 'pl-10 pr-4' : 'px-4'}`}
                    aria-label={field.placeholder}
                  />
                </div>
              )
            }
            return (
              <FilterSelect
                key={field.key}
                value={values[field.key]}
                onChange={(v) => { setValue(field.key, v); setPageNum(1) }}
                options={field.options ?? []}
                placeholder={field.placeholder}
                ariaLabel={field.placeholder}
              />
            )
          })}
        </div>
      )}

      <DataTable<T>
        columns={[
          ...columns,
          { key: 'actions', title: '操作', width: '120px', render: (item) => <ActionColumn item={item} onEdit={openEdit} onDelete={setDeleteItem} /> },
        ]}
        data={data}
        total={total} pageNum={pageNum} pageSize={10} onPageChange={setPageNum} loading={loading}
      />

      <FormModal
        title={editItem ? `编辑${title.replace(/管理$/, '')}` : `新增${title.replace(/管理$/, '')}`}
        open={modalOpen}
        onClose={closeModal}
        onSubmit={handleSubmit}
        fields={fields}
        initialValues={editItem && getInitialValues ? getInitialValues(editItem) : undefined}
        loading={submitting}
      />

      <ConfirmDialog
        open={!!deleteItem}
        message={deleteItem && getDeleteMessage ? getDeleteMessage(deleteItem) : '确定要删除这条记录吗？'}
        onConfirm={handleDelete}
        onCancel={() => setDeleteItem(null)}
        loading={submitting}
      />
    </div>
  )
}
