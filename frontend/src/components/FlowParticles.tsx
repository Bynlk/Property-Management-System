import { useEffect, useRef } from 'react'

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
export default function FlowParticles() {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const mouseRef = useRef({ x: -9999, y: -9999 })
  const clickRef = useRef(false)
  const animRef = useRef<number>(0)
  const timeRef = useRef(0)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')!

    const isMobile = window.innerWidth < 768

    const resize = () => {
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
    }
    resize()
    window.addEventListener('resize', resize)

    // 页面不可见时暂停动画，节省性能
    let paused = false
    const handleVisibility = () => {
      paused = document.hidden
      if (!paused) animRef.current = requestAnimationFrame(animate)
    }
    document.addEventListener('visibilitychange', handleVisibility)

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

    // Particles — 移动端减少粒子数量以提升性能
    const count = isMobile
      ? Math.min(40, Math.floor(window.innerWidth * window.innerHeight / 20000))
      : Math.min(120, Math.floor(window.innerWidth * window.innerHeight / 8000))
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
      if (paused) return
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
          if (dist < 350) {
            drawAlpha = Math.min(0.85, p.alpha + (1 - dist / 350) * 0.7)
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
      document.removeEventListener('visibilitychange', handleVisibility)
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
