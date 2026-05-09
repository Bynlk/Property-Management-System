import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from 'lucide-react'
import { useRef, useCallback } from 'react'

interface Column {
  key: string
  title: string
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  render?: (item: any) => React.ReactNode
  width?: string
}

interface Props {
  columns: Column[]
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  data: any[]
  total: number
  pageNum: number
  pageSize: number
  onPageChange: (page: number) => void
  loading?: boolean
}

function SkeletonRows({ columns, count = 5 }: { columns: Column[]; count?: number }) {
  return (
    <>
      {Array.from({ length: count }).map((_, row) => (
        <tr key={row} className="border-b border-white/[0.04]">
          {columns.map((col, colIdx) => (
            <td key={col.key} className="px-4 py-3.5">
              <div
                className="skeleton h-4"
                style={{
                  width: colIdx === 0 ? '32px' : colIdx === columns.length - 1 ? '64px' : `${50 + Math.random() * 40}%`,
                  animationDelay: `${row * 0.1 + colIdx * 0.05}s`,
                }}
              />
            </td>
          ))}
        </tr>
      ))}
    </>
  )
}

export default function DataTable({
  columns, data, total, pageNum, pageSize, onPageChange, loading,
}: Props) {
  const totalPages = Math.ceil(total / pageSize)
  const tableRef = useRef<HTMLDivElement>(null)

  const handleMouseMove = useCallback((e: React.MouseEvent<HTMLDivElement>) => {
    const rect = e.currentTarget.getBoundingClientRect()
    const x = e.clientX - rect.left
    const y = e.clientY - rect.top
    e.currentTarget.style.setProperty('--mouse-x', `${x}px`)
    e.currentTarget.style.setProperty('--mouse-y', `${y}px`)
  }, [])

  // Generate page numbers with ellipsis
  const getPageNumbers = () => {
    const pages: (number | '...')[] = []
    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) pages.push(i)
    } else {
      pages.push(1)
      if (pageNum > 3) pages.push('...')
      const start = Math.max(2, pageNum - 1)
      const end = Math.min(totalPages - 1, pageNum + 1)
      for (let i = start; i <= end; i++) pages.push(i)
      if (pageNum < totalPages - 2) pages.push('...')
      pages.push(totalPages)
    }
    return pages
  }

  return (
    <div
      ref={tableRef}
      className="glow-card glass overflow-hidden"
      onMouseMove={handleMouseMove}
    >
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-white/[0.06]">
              {columns.map((col) => (
                <th
                  key={col.key}
                  className="px-4 py-3.5 text-left text-text-secondary font-medium text-xs uppercase tracking-widest"
                  style={col.width ? { width: col.width } : undefined}
                >
                  {col.title}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <SkeletonRows columns={columns} />
            ) : data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-4 py-16 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <div className="w-12 h-12 rounded-full bg-white/[0.04] flex items-center justify-center">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="text-text-secondary/50">
                        <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                      </svg>
                    </div>
                    <span className="text-text-secondary text-sm">暂无数据</span>
                  </div>
                </td>
              </tr>
            ) : (
              data.map((item, i) => (
                <tr
                  key={item.id ?? i}
                  className="border-b border-white/[0.04] hover:bg-white/[0.02] transition-colors duration-150"
                  style={{ animation: `fade-in-up 0.3s ease ${i * 0.03}s both` }}
                >
                  {columns.map((col) => (
                    <td key={col.key} className="px-4 py-3.5">
                      {col.render ? col.render(item) : (item[col.key] ?? '-')}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between px-4 py-3 border-t border-white/[0.06]">
          <span className="text-xs text-text-secondary">
            共 <span className="text-text-primary font-medium">{total}</span> 条，第 <span className="text-text-primary font-medium">{pageNum}</span>/{totalPages} 页
          </span>
          <div className="flex items-center gap-1">
            <button
              onClick={() => onPageChange(1)}
              disabled={pageNum <= 1}
              className="p-1.5 rounded-lg hover:bg-white/[0.06] text-text-secondary disabled:opacity-20 disabled:cursor-not-allowed transition-colors"
              title="首页"
            >
              <ChevronsLeft size={14} />
            </button>
            <button
              onClick={() => onPageChange(pageNum - 1)}
              disabled={pageNum <= 1}
              className="p-1.5 rounded-lg hover:bg-white/[0.06] text-text-secondary disabled:opacity-20 disabled:cursor-not-allowed transition-colors"
              title="上一页"
            >
              <ChevronLeft size={14} />
            </button>

            {getPageNumbers().map((page, idx) =>
              page === '...' ? (
                <span key={`ellipsis-${idx}`} className="w-8 h-8 flex items-center justify-center text-xs text-text-secondary/50">
                  ...
                </span>
              ) : (
                <button
                  key={page}
                  onClick={() => onPageChange(page)}
                  className={`w-8 h-8 rounded-lg text-xs font-medium transition-all duration-200 ${
                    page === pageNum
                      ? 'bg-white/10 text-white border border-white/15'
                      : 'text-text-secondary hover:bg-white/6 hover:text-text-primary'
                  }`}
                >
                  {page}
                </button>
              )
            )}

            <button
              onClick={() => onPageChange(pageNum + 1)}
              disabled={pageNum >= totalPages}
              className="p-1.5 rounded-lg hover:bg-white/[0.06] text-text-secondary disabled:opacity-20 disabled:cursor-not-allowed transition-colors"
              title="下一页"
            >
              <ChevronRight size={14} />
            </button>
            <button
              onClick={() => onPageChange(totalPages)}
              disabled={pageNum >= totalPages}
              className="p-1.5 rounded-lg hover:bg-white/[0.06] text-text-secondary disabled:opacity-20 disabled:cursor-not-allowed transition-colors"
              title="末页"
            >
              <ChevronsRight size={14} />
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
