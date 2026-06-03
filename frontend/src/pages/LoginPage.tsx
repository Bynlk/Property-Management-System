import { useState, useEffect, useRef, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { authApi } from '../api'
import { useAuth } from '../contexts/AuthContext'
import { Lock, User, Building2 } from 'lucide-react'
import FlowParticles from '../components/FlowParticles'

export default function LoginPage() {
  const navigate = useNavigate()
  const auth = useAuth()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [mounted, setMounted] = useState(false)
  const cardRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    setMounted(true)
  }, [])

  const handleMouseMove = useCallback((e: React.MouseEvent<HTMLDivElement>) => {
    if (!cardRef.current) return
    const rect = cardRef.current.getBoundingClientRect()
    cardRef.current.style.setProperty('--mouse-x', `${e.clientX - rect.left}px`)
    cardRef.current.style.setProperty('--mouse-y', `${e.clientY - rect.top}px`)
  }, [])

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data: res } = await authApi.login({ username, password })
      if (res.code === 0 && res.data) {
        auth.login(res.data.token, res.data.user)
        navigate('/')
      } else {
        setError(res.msg)
      }
    } catch {
      setError('网络错误，请稍后重试')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
      <FlowParticles />
      <div className="noise-overlay" />

      {/* Login Card */}
      <div
        ref={cardRef}
        onMouseMove={handleMouseMove}
        className={`glow-card relative w-full max-w-md rounded-2xl border border-white/[0.08] p-8 sm:p-10 transition-all duration-700 ${
          mounted ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-8'
        }`}
        style={{
          background: 'rgba(8, 8, 8, 0.85)',
          backdropFilter: 'blur(24px)',
          boxShadow: '0 25px 60px rgba(0, 0, 0, 0.5)',
          zIndex: 10,
        }}
      >
        {/* Top thin line */}
        <div className="absolute top-0 left-[15%] right-[15%] h-px bg-white/[0.08]" />

        {/* Logo & Title */}
        <div className={`text-center mb-10 transition-all duration-700 delay-100 ${mounted ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'}`}>
          <div className="mx-auto w-16 h-16 rounded-2xl liquid-glass-icon flex items-center justify-center mb-5">
            <Building2 size={28} className="relative z-10 text-white/70" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-white/90">
            物业管理系统
          </h1>
          <p className="text-sm text-text-secondary mt-2.5 tracking-wide">Property Management System</p>
        </div>

        {/* Form */}
        <form onSubmit={handleLogin} className="space-y-5">
          <div className={`transition-all duration-700 delay-200 ${mounted ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'}`}>
            <label className="block text-sm text-text-secondary mb-2 font-medium">用户名</label>
            <div className="relative group">
              <User size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-white/50 transition-colors" />
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                className="w-full pl-11 pr-4 py-3 input-glass text-sm"
                placeholder="请输入用户名"
              />
            </div>
          </div>

          <div className={`transition-all duration-700 delay-300 ${mounted ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'}`}>
            <label className="block text-sm text-text-secondary mb-2 font-medium">密码</label>
            <div className="relative group">
              <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-white/50 transition-colors" />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full pl-11 pr-4 py-3 input-glass text-sm"
                placeholder="请输入密码"
              />
            </div>
          </div>

          {error && (
            <div className="text-sm text-danger text-center py-2.5 rounded-lg bg-danger/8 border border-danger/15 animate-[fade-in-up_0.3s_ease]">
              {error}
            </div>
          )}

          <div className={`transition-all duration-700 delay-[400ms] ${mounted ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'}`}>
            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full py-3 text-sm tracking-wide"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <span className="w-4 h-4 border-2 border-black/20 border-t-black rounded-full animate-spin" />
                  登录中...
                </span>
              ) : '登 录'}
            </button>
          </div>
        </form>

        <p className={`text-xs text-text-secondary/60 text-center mt-8 transition-all duration-700 delay-500 ${mounted ? 'opacity-100' : 'opacity-0'}`}>
          Property Management System v2.0
        </p>
      </div>
    </div>
  )
}
