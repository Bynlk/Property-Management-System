import { useEffect, useRef, useCallback } from 'react'
import { X } from 'lucide-react'
import { DROPDOWN_ARROW_SVG } from '../utils/dropdownArrow'

interface Field {
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

interface Props {
  title: string
  open: boolean
  onClose: () => void
  onSubmit: (data: Record<string, string>) => void
  fields: Field[]
  initialValues?: Record<string, string>
  loading?: boolean
}

export default function FormModal({
  title, open, onClose, onSubmit, fields, initialValues, loading,
}: Props) {
  const formRef = useRef<HTMLFormElement>(null)
  const modalRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (open && formRef.current && initialValues) {
      const form = formRef.current
      Object.entries(initialValues).forEach(([key, value]) => {
        const input = form.elements.namedItem(key) as HTMLInputElement | null
        if (input) input.value = value ?? ''
      })
    }
  }, [open, initialValues])

  // Focus first input when modal opens
  useEffect(() => {
    if (open) {
      requestAnimationFrame(() => {
        if (formRef.current) {
          const firstInput = formRef.current.querySelector<HTMLElement>(
            'input, select, textarea'
          )
          if (firstInput) firstInput.focus()
        }
      })
    }
  }, [open])

  // Focus trap: cycle Tab within the modal
  const handleTrapKeyDown = useCallback((e: KeyboardEvent) => {
    if (e.key !== 'Tab' || !open || !modalRef.current) return
    const focusable = modalRef.current.querySelectorAll<HTMLElement>(
      'input, select, textarea, button, [tabindex]:not([tabindex="-1"])'
    )
    if (focusable.length === 0) return
    const first = focusable[0]
    const last = focusable[focusable.length - 1]
    if (e.shiftKey) {
      if (document.activeElement === first) {
        e.preventDefault()
        last.focus()
      }
    } else {
      if (document.activeElement === last) {
        e.preventDefault()
        first.focus()
      }
    }
  }, [open])

  // Escape key to close
  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    if (e.key === 'Escape' && open) onClose()
  }, [open, onClose])

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [handleKeyDown])

  // Focus trap listener
  useEffect(() => {
    document.addEventListener('keydown', handleTrapKeyDown)
    return () => document.removeEventListener('keydown', handleTrapKeyDown)
  }, [handleTrapKeyDown])

  if (!open) return null

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const form = formRef.current!
    if (!form.reportValidity()) return
    const data: Record<string, string> = {}
    fields.forEach(({ name }) => {
      const input = form.elements.namedItem(name) as HTMLInputElement
      data[name] = input?.value ?? ''
    })
    onSubmit(data)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4" role="dialog" aria-modal="true" aria-labelledby="modal-title">
      {/* Backdrop */}
      <div className="modal-backdrop absolute inset-0 bg-black/60 backdrop-blur-md" onClick={onClose} />

      {/* Modal */}
      <div
        ref={modalRef}
        className="modal-content relative w-full max-w-lg rounded-2xl border border-white/[0.08] overflow-hidden shadow-2xl"
        style={{ background: 'rgba(16, 16, 24, 0.97)', backdropFilter: 'blur(32px)' }}
      >
        {/* Top thin line */}
        <div className="absolute top-0 left-[15%] right-[15%] h-px bg-white/8" />

        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-white/[0.06]">
          <h3 id="modal-title" className="text-lg font-semibold tracking-tight">{title}</h3>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-white/[0.06] text-text-secondary hover:text-text-primary transition-colors"
            aria-label="关闭"
          >
            <X size={18} />
          </button>
        </div>

        {/* Form */}
        <form ref={formRef} onSubmit={handleSubmit} className="px-6 py-5 space-y-4 max-h-[60vh] overflow-y-auto">
          {fields.map((field) => (
            <div key={field.name}>
              <label className="block text-sm text-text-secondary mb-2 font-medium">
                {field.label}
                {field.required && <span className="text-danger ml-1">*</span>}
              </label>
              {field.type === 'select' ? (
                <select
                  name={field.name}
                  required={field.required}
                  className="w-full px-3.5 py-2.5 input-glass text-sm appearance-none cursor-pointer"
                  style={{ backgroundImage: DROPDOWN_ARROW_SVG, backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center' }}
                >
                  <option value="">请选择</option>
                  {field.options?.map((opt) => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
              ) : field.type === 'textarea' ? (
                <textarea
                  name={field.name}
                  required={field.required}
                  rows={3}
                  className="w-full px-3.5 py-2.5 input-glass text-sm resize-none"
                />
              ) : (
                <input
                  type={field.type || 'text'}
                  name={field.name}
                  required={field.required}
                  pattern={field.pattern}
                  title={field.patternMessage}
                  min={field.min}
                  max={field.max}
                  className="w-full px-3.5 py-2.5 input-glass text-sm"
                />
              )}
            </div>
          ))}
        </form>

        {/* Footer */}
        <div className="flex justify-end gap-3 px-6 py-4 border-t border-white/[0.06]">
          <button
            type="button"
            onClick={onClose}
            className="px-5 py-2.5 rounded-xl text-sm text-text-secondary hover:bg-white/[0.06] hover:text-text-primary transition-all"
          >
            取消
          </button>
          <button
            type="submit"
            disabled={loading}
            className="btn-primary px-6 py-2.5 text-sm disabled:opacity-40 disabled:cursor-not-allowed"
          >
            {loading ? (
              <span className="flex items-center gap-2">
                <span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                提交中...
              </span>
            ) : '确定'}
          </button>
        </div>
      </div>
    </div>
  )
}
