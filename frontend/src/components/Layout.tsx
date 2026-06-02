import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { useEffect, useState, useRef } from 'react'
import {
  Users, Building2, Home, Receipt, Car, MessageSquare, Wrench, CalendarClock,
  LayoutDashboard, LogOut, Menu, X,
} from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import type { User } from '../types'

const navItems = [
  { to: '/', icon: LayoutDashboard, label: '工作台', roles: ['admin', 'user'] },
  { to: '/owner', icon: Users, label: '业主管理', roles: ['admin', 'user'] },
  { to: '/employee', icon: Building2, label: '员工管理', roles: ['admin', 'user'] },
  { to: '/house', icon: Home, label: '房屋管理', roles: ['admin', 'user'] },
  { to: '/fee', icon: Receipt, label: '费用管理', roles: ['admin', 'user'] },
  { to: '/parking', icon: Car, label: '停车位管理', roles: ['admin', 'user'] },
  { to: '/complaint', icon: MessageSquare, label: '投诉管理', roles: ['admin', 'user'] },
  { to: '/repair', icon: Wrench, label: '报修管理', roles: ['admin', 'user'] },
  { to: '/duty', icon: CalendarClock, label: '值班管理', roles: ['admin'] },
]

function SidebarContent({
  sidebarOpen, user, onToggle, onLogout, isMobile = false,
}: {
  sidebarOpen: boolean
  user: User | null
  onToggle?: () => void
  onLogout: () => void
  isMobile?: boolean
}) {
  return (
    <>
      {/* Logo */}
      <div className="h-16 flex items-center px-5 border-b border-white/[0.06]">
        {sidebarOpen && (
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg liquid-glass-icon flex items-center justify-center">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" className="relative z-10 opacity-70">
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                <polyline points="9 22 9 12 15 12 15 22" />
              </svg>
            </div>
            <span className="text-base font-semibold text-white/80">
              物业管理
            </span>
          </div>
        )}
        {onToggle && (
          <button
            onClick={onToggle}
            className="ml-auto p-1.5 rounded-lg hover:bg-white/[0.06] text-text-secondary transition-colors hidden lg:flex"
            aria-label={sidebarOpen ? '收起侧边栏' : '展开侧边栏'}
          >
            {sidebarOpen ? <X size={16} /> : <Menu size={16} />}
          </button>
        )}
        {isMobile && (
          <button
            onClick={onToggle}
            className="ml-auto p-1.5 rounded-lg hover:bg-white/[0.06] text-text-secondary"
            aria-label="关闭菜单"
          >
            <X size={18} />
          </button>
        )}
      </div>

      {/* Nav */}
      <nav className="flex-1 py-3 overflow-y-auto px-2 space-y-0.5">
        {navItems.filter(item => !user || item.roles.includes(user.role)).map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            className={({ isActive }) =>
              isMobile
                ? `relative flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm transition-all duration-200 ${
                    isActive
                      ? 'text-white bg-white/6 border border-white/10'
                      : 'text-text-secondary hover:text-text-primary hover:bg-white/[0.04]'
                  }`
                : `relative flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm transition-all duration-200 group ${
                    isActive
                      ? 'text-white'
                      : 'text-text-secondary hover:text-text-primary hover:bg-white/[0.04]'
                  }`
            }
          >
            {({ isActive }) => (
              <>
                {isActive && !isMobile && (
                  <div className="absolute inset-0 rounded-xl bg-white/6 border border-white/10" />
                )}
                <Icon size={18} className="relative z-10 flex-shrink-0" />
                {sidebarOpen && <span className="relative z-10 truncate">{label}</span>}
                {!sidebarOpen && !isMobile && (
                  <div className="absolute left-full ml-3 px-2.5 py-1.5 rounded-lg text-xs font-medium bg-bg-secondary border border-border text-text-primary opacity-0 group-hover:opacity-100 pointer-events-none transition-opacity whitespace-nowrap z-50 shadow-xl">
                    {label}
                  </div>
                )}
              </>
            )}
          </NavLink>
        ))}
      </nav>

      {/* User */}
      <div className="p-3 border-t border-white/[0.06]">
        <div className="flex items-center gap-3 px-2 py-2">
          <div className="relative">
            <div className="w-9 h-9 rounded-full liquid-glass-icon flex items-center justify-center text-xs font-bold text-white/70">
              {user?.realName?.[0] || 'A'}
            </div>
            <div className="absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full bg-success border-2 border-bg-primary" />
          </div>
          {sidebarOpen && (
            <div className="flex-1 min-w-0">
              <div className="text-sm font-medium truncate">{user?.realName || '管理员'}</div>
              <div className="text-xs text-text-secondary truncate">{user?.role || 'admin'}</div>
            </div>
          )}
          <button
            onClick={onLogout}
            className="p-1.5 rounded-lg hover:bg-danger/10 text-text-secondary hover:text-danger transition-colors"
            title="退出登录"
            aria-label="退出登录"
          >
            <LogOut size={15} />
          </button>
        </div>
      </div>
    </>
  )
}

export default function Layout() {
  const location = useLocation()
  const { user, logout } = useAuth()
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [mobileOpen, setMobileOpen] = useState(false)
  const mainRef = useRef<HTMLDivElement>(null)

  // Close mobile sidebar on route change
  useEffect(() => {
    setMobileOpen(false)
  }, [location.pathname])

  // Scroll to top on route change
  useEffect(() => {
    mainRef.current?.scrollTo({ top: 0, behavior: 'smooth' })
  }, [location.pathname])

  const handleLogout = () => {
    logout()
  }

  return (
    <div className="flex h-screen overflow-hidden bg-bg-primary">
      <div className="noise-overlay" />

      {/* Mobile Overlay */}
      {mobileOpen && (
        <div
          className="fixed inset-0 bg-black/50 backdrop-blur-sm z-40 lg:hidden"
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* Sidebar — Desktop */}
      <aside
        className={`hidden lg:flex flex-shrink-0 flex-col transition-all duration-300 ease-out border-r border-white/[0.06] z-30 ${
          sidebarOpen ? 'w-60' : 'w-[72px]'
        }`}
        style={{ background: 'rgba(5, 5, 5, 0.92)', backdropFilter: 'blur(20px)' }}
      >
        <SidebarContent
          sidebarOpen={sidebarOpen}
          user={user}
          onToggle={() => setSidebarOpen(!sidebarOpen)}
          onLogout={handleLogout}
        />
      </aside>

      {/* Sidebar — Mobile */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 w-64 flex flex-col transition-transform duration-300 ease-out lg:hidden border-r border-white/[0.06] ${
          mobileOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
        style={{ background: 'rgba(5, 5, 5, 0.96)', backdropFilter: 'blur(24px)' }}
      >
        <SidebarContent
          sidebarOpen={true}
          user={user}
          onToggle={() => setMobileOpen(false)}
          onLogout={handleLogout}
          isMobile
        />
      </aside>

      {/* Main */}
      <main ref={mainRef} className="flex-1 overflow-y-auto relative z-10">
        {/* Mobile Header */}
        <div className="lg:hidden sticky top-0 z-20 flex items-center h-14 px-4 border-b border-white/[0.06]" style={{ background: 'rgba(10, 10, 15, 0.85)', backdropFilter: 'blur(12px)' }}>
          <button onClick={() => setMobileOpen(true)} className="p-1.5 rounded-lg hover:bg-white/[0.06] text-text-secondary" aria-label="打开菜单">
            <Menu size={20} />
          </button>
          <span className="ml-3 text-sm font-semibold text-white/80">
            物业管理
          </span>
        </div>

        <div className="p-4 sm:p-6 lg:p-8 max-w-[1440px] mx-auto">
          <div className="page-enter" key={location.pathname}>
            <Outlet />
          </div>
        </div>
      </main>
    </div>
  )
}
