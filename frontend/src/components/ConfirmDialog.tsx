import { AlertTriangle } from 'lucide-react'

interface Props {
  open: boolean
  title?: string
  message: string
  onConfirm: () => void
  onCancel: () => void
  loading?: boolean
}

export default function ConfirmDialog({ open, title = '确认操作', message, onConfirm, onCancel, loading }: Props) {
  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="modal-backdrop absolute inset-0 bg-black/60 backdrop-blur-md" onClick={onCancel} />
      <div
        className="modal-content relative w-full max-w-sm rounded-2xl border border-white/[0.08] overflow-hidden shadow-2xl"
        style={{ background: 'rgba(16, 16, 24, 0.97)', backdropFilter: 'blur(32px)' }}
      >
        {/* Top thin line */}
        <div className="absolute top-0 left-[15%] right-[15%] h-px bg-white/8" />

        <div className="p-6 text-center">
          <div className="mx-auto w-14 h-14 rounded-2xl bg-danger/10 flex items-center justify-center mb-5 border border-danger/20">
            <AlertTriangle className="text-danger" size={26} />
          </div>
          <h3 className="text-lg font-semibold mb-2">{title}</h3>
          <p className="text-sm text-text-secondary leading-relaxed">{message}</p>
        </div>
        <div className="flex gap-3 px-6 py-4 border-t border-white/[0.06]">
          <button
            onClick={onCancel}
            className="flex-1 px-4 py-2.5 rounded-xl text-sm text-text-secondary hover:bg-white/[0.06] hover:text-text-primary transition-all"
          >
            取消
          </button>
          <button
            onClick={onConfirm}
            disabled={loading}
            className="btn-danger flex-1 px-4 py-2.5 text-sm disabled:opacity-40 disabled:cursor-not-allowed"
          >
            {loading ? (
              <span className="flex items-center justify-center gap-2">
                <span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                处理中...
              </span>
            ) : '确认'}
          </button>
        </div>
      </div>
    </div>
  )
}
