import { Pencil, Trash2 } from 'lucide-react'

interface Props<T> {
  item: T
  onEdit: (item: T) => void
  onDelete: (item: T) => void
}

/**
 * 通用操作列 — 编辑 + 删除按钮
 */
export default function ActionColumn<T>({ item, onEdit, onDelete }: Props<T>) {
  return (
    <div className="flex gap-1">
      <button
        onClick={() => onEdit(item)}
        className="p-1.5 rounded-lg hover:bg-white/8 text-white/60 transition-colors"
        title="编辑"
        aria-label="编辑"
      >
        <Pencil size={14} />
      </button>
      <button
        onClick={() => onDelete(item)}
        className="p-1.5 rounded-lg hover:bg-danger/10 text-danger transition-colors"
        title="删除"
        aria-label="删除"
      >
        <Trash2 size={14} />
      </button>
    </div>
  )
}
