import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useCrudSearch } from '../useCrudSearch'

describe('useCrudSearch', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('初始值：所有字段为空字符串', () => {
    const { result } = renderHook(() =>
      useCrudSearch([
        { key: 'name', type: 'text' },
        { key: 'status', type: 'select' },
      ])
    )

    expect(result.current.values).toEqual({ name: '', status: '' })
  })

  it('searchParams初始值所有字段为空', () => {
    const { result } = renderHook(() =>
      useCrudSearch([
        { key: 'name', type: 'text' },
        { key: 'status', type: 'select' },
      ])
    )

    expect(result.current.searchParams.name).toBe('')
    expect(result.current.searchParams.status).toBe('')
  })

  it('文本字段debounce 300ms', () => {
    const { result } = renderHook(() =>
      useCrudSearch([{ key: 'name', type: 'text' }])
    )

    act(() => {
      result.current.setValue('name', '刘备')
    })

    // 300ms内值不应传递到searchParams
    expect(result.current.searchParams.name).toBe('')

    act(() => {
      vi.advanceTimersByTime(300)
    })

    // 300ms后值应传递到searchParams
    expect(result.current.searchParams.name).toBe('刘备')
  })

  it('快速连续输入只取最后一个值', () => {
    const { result } = renderHook(() =>
      useCrudSearch([{ key: 'name', type: 'text' }])
    )

    act(() => {
      result.current.setValue('name', '刘')
    })

    act(() => {
      vi.advanceTimersByTime(100)
    })

    act(() => {
      result.current.setValue('name', '刘备')
    })

    act(() => {
      vi.advanceTimersByTime(100)
    })

    act(() => {
      result.current.setValue('name', '刘备改')
    })

    act(() => {
      vi.advanceTimersByTime(300)
    })

    expect(result.current.searchParams.name).toBe('刘备改')
  })

  it('select字段立即生效，无debounce', () => {
    const { result } = renderHook(() =>
      useCrudSearch([{ key: 'status', type: 'select' }])
    )

    act(() => {
      result.current.setValue('status', '待处理')
    })

    // select字段应立即更新
    expect(result.current.searchParams.status).toBe('待处理')
  })

  it('混合text和select字段', () => {
    const { result } = renderHook(() =>
      useCrudSearch([
        { key: 'name', type: 'text' },
        { key: 'status', type: 'select' },
      ])
    )

    act(() => {
      result.current.setValue('name', '刘备')
      result.current.setValue('status', '待处理')
    })

    // select立即生效
    expect(result.current.searchParams.status).toBe('待处理')
    // text还在debounce中
    expect(result.current.searchParams.name).toBe('')

    act(() => {
      vi.advanceTimersByTime(300)
    })

    // text debounce完成
    expect(result.current.searchParams.name).toBe('刘备')
  })

  it('默认类型为text（不指定type时）', () => {
    const { result } = renderHook(() =>
      useCrudSearch([{ key: 'name' }])  // no type specified
    )

    act(() => {
      result.current.setValue('name', '刘备')
    })

    // 默认为text，需要debounce
    expect(result.current.searchParams.name).toBe('')

    act(() => {
      vi.advanceTimersByTime(300)
    })

    expect(result.current.searchParams.name).toBe('刘备')
  })

  it('重置值后debounce重新计算', () => {
    const { result } = renderHook(() =>
      useCrudSearch([{ key: 'name', type: 'text' }])
    )

    act(() => {
      result.current.setValue('name', '刘备')
    })

    act(() => {
      vi.advanceTimersByTime(300)
    })

    expect(result.current.searchParams.name).toBe('刘备')

    // Reset
    act(() => {
      result.current.setValue('name', '')
    })

    act(() => {
      vi.advanceTimersByTime(300)
    })

    expect(result.current.searchParams.name).toBe('')
  })

  it('多个文本字段各自独立debounce', () => {
    const { result } = renderHook(() =>
      useCrudSearch([
        { key: 'name', type: 'text' },
        { key: 'phone', type: 'text' },
      ])
    )

    act(() => {
      result.current.setValue('name', '刘备')
    })

    // 设置phone，这会重置整个debounce（因为values变化触发同一个useEffect）
    act(() => {
      result.current.setValue('phone', '139')
    })

    // debounce尚未完成
    expect(result.current.searchParams.name).toBe('')
    expect(result.current.searchParams.phone).toBe('')

    act(() => {
      vi.advanceTimersByTime(300)
    })

    // 300ms后两者都debounce完成
    expect(result.current.searchParams.name).toBe('刘备')
    expect(result.current.searchParams.phone).toBe('139')
  })
})
