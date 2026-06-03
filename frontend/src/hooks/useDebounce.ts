import { useState, useEffect } from 'react'

/**
 * 防抖 Hook
 * @param value 要防抖的值
 * @param delay 延迟毫秒数，默认 300ms
 */
export function useDebounce<T>(value: T, delay: number = 300): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value)

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedValue(value), delay)
    return () => clearTimeout(timer)
  }, [value, delay])

  return debouncedValue
}
