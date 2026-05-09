import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Users, Home, Receipt, MessageSquare, Wrench, ArrowRight,
  DollarSign, AlertCircle, CarFront,
} from 'lucide-react'
import { ownerApi, houseApi, feeApi, complaintApi, repairApi } from '../api'

interface StatCard {
  label: string
  value: number
  icon: React.ElementType
  to: string
}

function StatCardSkeleton() {
  return (
    <div className="h-40 rounded-2xl glass p-5 space-y-4">
      <div className="flex justify-between">
        <div className="skeleton w-11 h-11 rounded-xl" />
        <div className="skeleton w-4 h-4 rounded" />
      </div>
      <div className="skeleton w-16 h-7 rounded" />
      <div className="skeleton w-20 h-4 rounded" />
    </div>
  )
}

export default function DashboardPage() {
  const navigate = useNavigate()
  const [stats, setStats] = useState<StatCard[]>([])
  const [loading, setLoading] = useState(true)

  const handleCardMouseMove = useCallback((e: React.MouseEvent<HTMLButtonElement>) => {
    const rect = e.currentTarget.getBoundingClientRect()
    e.currentTarget.style.setProperty('--mouse-x', `${e.clientX - rect.left}px`)
    e.currentTarget.style.setProperty('--mouse-y', `${e.clientY - rect.top}px`)
  }, [])

  useEffect(() => {
    const load = async () => {
      try {
        const [owners, houses, fees, complaints, repairs] = await Promise.all([
          ownerApi.page({ pageNum: 1, pageSize: 1 }),
          houseApi.page({ pageNum: 1, pageSize: 1 }),
          feeApi.page({ pageNum: 1, pageSize: 1 }),
          complaintApi.page({ pageNum: 1, pageSize: 1 }),
          repairApi.page({ pageNum: 1, pageSize: 1 }),
        ])
        setStats([
          { label: '业主总数', value: owners.data.total, icon: Users, to: '/owner' },
          { label: '房屋总数', value: houses.data.total, icon: Home, to: '/house' },
          { label: '费用记录', value: fees.data.total, icon: DollarSign, to: '/fee' },
          { label: '投诉工单', value: complaints.data.total, icon: AlertCircle, to: '/complaint' },
          { label: '报修工单', value: repairs.data.total, icon: Wrench, to: '/repair' },
        ])
      } catch {
        // ignore
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const quickActions = [
    { label: '新增业主', icon: Users, to: '/owner' },
    { label: '新增费用', icon: Receipt, to: '/fee' },
    { label: '处理投诉', icon: MessageSquare, to: '/complaint' },
    { label: '报修管理', icon: Wrench, to: '/repair' },
    { label: '车位管理', icon: CarFront, to: '/parking' },
  ]

  return (
    <div className="space-y-8">
      {/* Header */}
      <div style={{ animation: 'fade-in-up 0.5s ease both' }}>
        <h1 className="text-3xl font-bold tracking-tight text-white/90">工作台</h1>
        <p className="text-text-secondary text-sm mt-2">小区物业管理系统概览</p>
      </div>

      {/* Stats Grid */}
      <div style={{ animation: 'fade-in-up 0.5s ease 0.1s both' }}>
        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4">
            {Array.from({ length: 5 }).map((_, i) => (
              <StatCardSkeleton key={i} />
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4">
            {stats.map((stat, idx) => (
              <button
                key={stat.label}
                onClick={() => navigate(stat.to)}
                onMouseMove={handleCardMouseMove}
                className="glow-card group text-left p-5 rounded-2xl border border-white/[0.06] hover:border-white/[0.12] transition-all duration-300"
                style={{
                  background: 'rgba(8, 8, 8, 0.55)',
                  backdropFilter: 'blur(16px)',
                  animation: `fade-in-up 0.5s ease ${0.15 + idx * 0.08}s both`,
                }}
              >
                <div className="flex items-center justify-between mb-5">
                  <div className="w-11 h-11 rounded-xl liquid-glass-icon flex items-center justify-center">
                    <stat.icon size={20} className="relative z-10 text-white/70" />
                  </div>
                  <ArrowRight size={16} className="text-text-secondary/40 group-hover:text-white/50 group-hover:translate-x-0.5 transition-all duration-200" />
                </div>
                <div className="text-3xl font-bold tracking-tight tabular-nums">
                  {stat.value.toLocaleString()}
                </div>
                <div className="text-sm text-text-secondary mt-1.5">{stat.label}</div>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Quick Actions */}
      <div className="glass p-6 sm:p-8" style={{ animation: 'fade-in-up 0.5s ease 0.4s both' }}>
        <h2 className="text-lg font-semibold mb-5 tracking-tight">快捷操作</h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3">
          {quickActions.map((action, idx) => (
            <button
              key={action.label}
              onClick={() => navigate(action.to)}
              className="group flex items-center gap-3 px-4 py-3.5 rounded-xl border border-white/6 hover:border-white/12 hover:bg-white/3 transition-all duration-200"
              style={{ animationDelay: `${idx * 0.05}s` }}
            >
              <action.icon size={18} className="text-text-secondary group-hover:text-white/60 transition-colors" />
              <span className="text-sm text-text-secondary group-hover:text-text-primary transition-colors">
                {action.label}
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* System Info */}
      <div className="glass p-6 sm:p-8" style={{ animation: 'fade-in-up 0.5s ease 0.5s both' }}>
        <h2 className="text-lg font-semibold mb-5 tracking-tight">系统信息</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
          {[
            { label: '系统版本', value: 'v1.0.0' },
            { label: '技术栈', value: 'Spring Boot + React' },
            { label: '数据库', value: 'MySQL 8.0' },
          ].map((item) => (
            <div key={item.label}>
              <div className="text-xs text-text-secondary uppercase tracking-widest mb-1.5">{item.label}</div>
              <div className="text-sm font-medium">{item.value}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
