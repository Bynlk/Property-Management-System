import { createContext, useContext, useState, useCallback, useRef, useEffect } from 'react'
import { CheckCircle, XCircle, AlertTriangle, Info, X } from 'lucide-react'

export type ToastType = 'success' | 'error' | 'warning' | 'info'

interface ToastItem {
  id: number
  type: ToastType
  message: string
}

interface ToastContextType {
  showToast: (type: ToastType, message: string) => void
}

const ToastContext = createContext<ToastContextType | null>(null)

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast must be used within ToastProvider')
  return ctx
}

// 模块级 ref：支持在 Hook 外部（如 useCrudPage）调用 showToast
let externalShowToast: ((type: ToastType, message: string) => void) | null = null

/**
 * 在非 React 组件中调用 Toast（如 useCrudPage Hook 内部）
 * 必须在 ToastProvider 挂载后才可用
 */
export function showToast(type: ToastType, message: string) {
  if (externalShowToast) {
    externalShowToast(type, message)
  } else {
    // ToastProvider 尚未挂载，fallback 到 console
    console.warn(`[Toast] ${type}: ${message}`)
  }
}

let toastId = 0

const icons = {
  success: CheckCircle,
  error: XCircle,
  warning: AlertTriangle,
  info: Info,
}

const colors = {
  success: 'text-success border-success/20 bg-success/10',
  error: 'text-danger border-danger/20 bg-danger/10',
  warning: 'text-yellow-400 border-yellow-400/20 bg-yellow-400/10',
  info: 'text-blue-400 border-blue-400/20 bg-blue-400/10',
}

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([])
  const toastsRef = useRef(toasts)
  toastsRef.current = toasts

  const showToastInternal = useCallback((type: ToastType, message: string) => {
    const id = ++toastId
    setToasts((prev) => [...prev, { id, type, message }])
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id))
    }, 3000)
  }, [])

  // 注册到模块级 ref，使 showToast() 可在 Hook 外部调用
  useEffect(() => {
    externalShowToast = showToastInternal
    return () => { externalShowToast = null }
  }, [showToastInternal])

  return (
    <ToastContext.Provider value={{ showToast: showToastInternal }}>
      {children}
      {toasts.length > 0 && (
        <div className="fixed top-4 right-4 z-[9999] space-y-2 max-w-sm">
          {toasts.map((toast) => {
            const Icon = icons[toast.type]
            return (
              <div
                key={toast.id}
                className={`flex items-center gap-3 px-4 py-3 rounded-xl border backdrop-blur-xl shadow-lg animate-[slide-in-right_0.3s_ease] ${colors[toast.type]}`}
                style={{ background: 'rgba(16, 16, 24, 0.95)' }}
                role="alert"
              >
                <Icon size={18} className="flex-shrink-0" />
                <span className="text-sm flex-1">{toast.message}</span>
                <button
                  onClick={() => setToasts((prev) => prev.filter((t) => t.id !== toast.id))}
                  className="p-0.5 rounded hover:bg-white/10 transition-colors"
                  aria-label="关闭"
                >
                  <X size={14} />
                </button>
              </div>
            )
          })}
        </div>
      )}
    </ToastContext.Provider>
  )
}

// 默认导出兼容旧代码（但推荐使用 ToastProvider）
export default ToastProvider
