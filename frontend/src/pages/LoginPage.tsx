import { useState, useEffect, useRef, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { authApi } from '../api'
import { Lock, User, Building2 } from 'lucide-react'

// Simple noise function for flow field
function noise(x: number, y: number, t: number): number {
  return (
    Math.sin(x * 0.008 + t * 0.3) *
    Math.cos(y * 0.006 - t * 0.2) *
    Math.PI * 2 +
    Math.sin(x * 0.005 - y * 0.007 + t * 0.15) * 1.5
  )
}

// Infinite flow particle system
function FlowParticles() {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const mouseRef = useRef({ x: -9999, y: -9999 })
  const clickRef = useRef(false)
  const animRef = useRef<number>(0)
  const timeRef = useRef(0)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')!

    const resize = () => {
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
    }
    resize()
    window.addEventListener('resize', resize)

    const handleMouse = (e: MouseEvent) => {
      mouseRef.current = { x: e.clientX, y: e.clientY }
    }
    const handleMouseDown = (e: MouseEvent) => {
      if (e.button === 0) clickRef.current = true
    }
    const handleMouseUp = () => { clickRef.current = false }
    window.addEventListener('mousemove', handleMouse)
    window.addEventListener('mousedown', handleMouseDown)
    window.addEventListener('mouseup', handleMouseUp)

    // Particles
    const count = Math.min(120, Math.floor(window.innerWidth * window.innerHeight / 8000))
    const trailLength = 18

    interface Particle {
      x: number
      y: number
      trail: { x: number; y: number }[]
      speed: number
      alpha: number
      width: number
      orbitAngle: number
      orbitRadius: number
      orbitSpeed: number
    }

    const particles: Particle[] = Array.from({ length: count }, () => ({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      trail: [],
      speed: 0.6 + Math.random() * 1.2,
      alpha: 0.08 + Math.random() * 0.2,
      width: 0.4 + Math.random() * 0.8,
      orbitAngle: Math.random() * Math.PI * 2,
      orbitRadius: 20 + Math.random() * 80,
      orbitSpeed: 0.02 + Math.random() * 0.06,
    }))

    const animate = () => {
      // Fade previous frame (creates trail effect)
      ctx.fillStyle = 'rgba(5, 5, 5, 0.08)'
      ctx.fillRect(0, 0, canvas.width, canvas.height)

      timeRef.current += 0.003
      const t = timeRef.current
      const mx = mouseRef.current.x
      const my = mouseRef.current.y
      const mouseRadius = 250

      const isClicking = clickRef.current

      for (const p of particles) {
        // Flow field direction
        const angle = noise(p.x, p.y, t)
        let vx = Math.cos(angle) * p.speed
        let vy = Math.sin(angle) * p.speed

        // Mouse attraction
        const dx = mx - p.x
        const dy = my - p.y
        const dist = Math.sqrt(dx * dx + dy * dy)

        if (isClicking) {
          // Click mode: attraction + orbital motion
          const attractRadius = 350
          if (dist > 1) {
            // Radial pull toward cursor (gentle)
            const pullForce = Math.min(2.5, dist * 0.015)
            vx += (dx / dist) * pullForce
            vy += (dy / dist) * pullForce

            // Tangential velocity for orbital effect
            const tangentForce = 1.2
            vx += (-dy / dist) * tangentForce
            vy += (dx / dist) * tangentForce

            // Once close enough, settle into a stable orbit
            if (dist < p.orbitRadius + 30) {
              p.orbitAngle += p.orbitSpeed
              const targetX = mx + Math.cos(p.orbitAngle) * p.orbitRadius
              const targetY = my + Math.sin(p.orbitAngle) * p.orbitRadius
              const tdx = targetX - p.x
              const tdy = targetY - p.y
              vx += tdx * 0.04
              vy += tdy * 0.04
            }
          }
        } else {
          // Normal hover attraction
          if (dist < mouseRadius && dist > 1) {
            const force = (1 - dist / mouseRadius) * 0.6
            vx += (dx / dist) * force
            vy += (dy / dist) * force
          }
          // Dampen residual orbital velocity so particles don't fling on release
          vx *= 0.92
          vy *= 0.92
        }

        // Update position
        p.x += vx
        p.y += vy

        // Store trail
        p.trail.push({ x: p.x, y: p.y })
        if (p.trail.length > trailLength) p.trail.shift()

        // Wrap around
        if (p.x < -20) { p.x = canvas.width + 20; p.trail = [] }
        if (p.x > canvas.width + 20) { p.x = -20; p.trail = [] }
        if (p.y < -20) { p.y = canvas.height + 20; p.trail = [] }
        if (p.y > canvas.height + 20) { p.y = -20; p.trail = [] }

        // Dynamic alpha near cursor
        let drawAlpha = p.alpha
        if (isClicking) {
          // Brighter orbiting particles
          const attractRadius = 350
          if (dist < attractRadius) {
            drawAlpha = Math.min(0.85, p.alpha + (1 - dist / attractRadius) * 0.7)
          }
        } else if (dist < mouseRadius) {
          drawAlpha = Math.min(0.6, p.alpha + (1 - dist / mouseRadius) * 0.4)
        }

        // Draw flow line
        if (p.trail.length > 2) {
          ctx.beginPath()
          ctx.moveTo(p.trail[0].x, p.trail[0].y)
          for (let i = 1; i < p.trail.length; i++) {
            const prev = p.trail[i - 1]
            const curr = p.trail[i]
            const cx = (prev.x + curr.x) / 2
            const cy = (prev.y + curr.y) / 2
            ctx.quadraticCurveTo(prev.x, prev.y, cx, cy)
          }
          ctx.strokeStyle = `rgba(255, 255, 255, ${drawAlpha})`
          ctx.lineWidth = p.width
          ctx.lineCap = 'round'
          ctx.stroke()
        }

        // Bright dot at head
        ctx.beginPath()
        ctx.arc(p.x, p.y, p.width * 0.8, 0, Math.PI * 2)
        ctx.fillStyle = `rgba(255, 255, 255, ${drawAlpha * 1.5})`
        ctx.fill()
      }

      animRef.current = requestAnimationFrame(animate)
    }

    // Initial clear
    ctx.fillStyle = '#050505'
    ctx.fillRect(0, 0, canvas.width, canvas.height)
    animate()

    return () => {
      cancelAnimationFrame(animRef.current)
      window.removeEventListener('resize', resize)
      window.removeEventListener('mousemove', handleMouse)
      window.removeEventListener('mousedown', handleMouseDown)
      window.removeEventListener('mouseup', handleMouseUp)
    }
  }, [])

  return (
    <canvas
      ref={canvasRef}
      className="fixed inset-0 pointer-events-none"
      style={{ zIndex: 0 }}
    />
  )
}

export default function LoginPage() {
  const navigate = useNavigate()
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
      const { data } = await authApi.login({ username, password })
      if (data.code === 0) {
        localStorage.setItem('token', data.token)
        localStorage.setItem('user', JSON.stringify(data.user))
        navigate('/')
      } else {
        setError(data.msg)
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
          默认账号: admin / admin123
        </p>
      </div>
    </div>
  )
}
