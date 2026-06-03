import { Plus, type LucideIcon } from 'lucide-react'

interface Props {
  icon: LucideIcon
  title: string
  subtitle: string
  addLabel?: string
  onAdd?: () => void
}

/**
 * 通用页面头部 — 图标 + 标题 + 副标题 + 新增按钮
 */
export default function PageHeader({ icon: Icon, title, subtitle, addLabel, onAdd }: Props) {
  return (
    <div className="flex items-center justify-between flex-wrap gap-4">
      <div className="flex items-center gap-3">
        <div className="w-10 h-10 rounded-xl liquid-glass-icon flex items-center justify-center">
          <Icon size={20} className="relative z-10 text-white/70" />
        </div>
        <div>
          <h1 className="text-2xl font-bold tracking-tight">{title}</h1>
          <p className="text-xs text-text-secondary mt-0.5">{subtitle}</p>
        </div>
      </div>
      {onAdd && (
        <button onClick={onAdd} className="btn-primary flex items-center gap-2 px-5 py-2.5 text-sm">
          <Plus size={16} /> {addLabel || '新增'}
        </button>
      )}
    </div>
  )
}
