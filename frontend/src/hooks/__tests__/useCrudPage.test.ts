import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { renderHook, act, waitFor } from '@testing-library/react'
import { useCrudPage } from '../useCrudPage'

// Mock showToast and getApiErrorMessage
vi.mock('../../components/Toast', () => ({
  showToast: vi.fn(),
}))
vi.mock('../../utils/apiError', () => ({
  getApiErrorMessage: vi.fn(() => '加载数据失败'),
}))

import { showToast } from '../../components/Toast'
import { getApiErrorMessage } from '../../utils/apiError'

interface TestItem {
  id: number
  name: string
}

function createMockApi(overrides?: Record<string, unknown>) {
  return {
    page: vi.fn().mockResolvedValue({
      data: {
        code: 0,
        data: { list: [], total: 0, pageNum: 1, pageSize: 10, totalPages: 0 },
      },
    }),
    add: vi.fn().mockResolvedValue({ data: { code: 0 } }),
    update: vi.fn().mockResolvedValue({ data: { code: 0 } }),
    delete: vi.fn().mockResolvedValue({ data: { code: 0 } }),
    ...overrides,
  }
}

describe('useCrudPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('初始状态：data为空数组，loading为false', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    // After initial fetch
    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })
    expect(result.current.data).toEqual([])
    expect(result.current.total).toBe(0)
    expect(result.current.pageNum).toBe(1)
  })

  it('分页查询-切换页码触发请求', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    await waitFor(() => {
      expect(api.page).toHaveBeenCalledTimes(1)
    })

    // Change page
    act(() => {
      result.current.setPageNum(2)
    })

    await waitFor(() => {
      expect(api.page).toHaveBeenCalledTimes(2)
    })

    // Verify page 2 was requested
    const secondCall = api.page.mock.calls[1][0]
    expect(secondCall.pageNum).toBe(2)
  })

  it('搜索参数变化触发重新请求', async () => {
    const api = createMockApi()
    const { rerender } = renderHook(
      ({ searchParams }) => useCrudPage<TestItem>({ api, searchParams }),
      { initialProps: { searchParams: {} as Record<string, string | number | undefined> } }
    )

    await waitFor(() => {
      expect(api.page).toHaveBeenCalledTimes(1)
    })

    // Update search params
    rerender({ searchParams: { name: '刘备' } })

    await waitFor(() => {
      expect(api.page).toHaveBeenCalledTimes(2)
    })
  })

  it('openAdd打开新增弹窗', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    act(() => {
      result.current.openAdd()
    })

    expect(result.current.modalOpen).toBe(true)
    expect(result.current.editItem).toBeNull()
  })

  it('openEdit打开编辑弹窗并设置editItem', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    const item = { id: 1, name: '刘备' }
    act(() => {
      result.current.openEdit(item)
    })

    expect(result.current.modalOpen).toBe(true)
    expect(result.current.editItem).toEqual(item)
  })

  it('closeModal关闭弹窗并清空editItem', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    act(() => {
      result.current.openEdit({ id: 1, name: '刘备' })
    })
    act(() => {
      result.current.closeModal()
    })

    expect(result.current.modalOpen).toBe(false)
    expect(result.current.editItem).toBeNull()
  })

  it('handleSubmit新增模式调用api.add并刷新数据', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    // Open add modal
    act(() => {
      result.current.openAdd()
    })

    // Submit
    await act(async () => {
      await result.current.handleSubmit({ name: '关羽' })
    })

    expect(api.add).toHaveBeenCalledWith({ name: '关羽' })
    expect(showToast).toHaveBeenCalledWith('success', '新增成功')
    expect(result.current.modalOpen).toBe(false)
  })

  it('handleSubmit编辑模式调用api.update', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    // Open edit modal
    act(() => {
      result.current.openEdit({ id: 1, name: '刘备' })
    })

    // Submit
    await act(async () => {
      await result.current.handleSubmit({ name: '刘备改' })
    })

    expect(api.update).toHaveBeenCalledWith({ name: '刘备改', id: 1 })
    expect(showToast).toHaveBeenCalledWith('success', '修改成功')
  })

  it('handleDelete调用api.delete并刷新数据', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    // Set delete item
    act(() => {
      result.current.setDeleteItem({ id: 1, name: '刘备' })
    })

    // Confirm delete
    await act(async () => {
      await result.current.handleDelete()
    })

    expect(api.delete).toHaveBeenCalledWith(1)
    expect(showToast).toHaveBeenCalledWith('success', '删除成功')
    expect(result.current.deleteItem).toBeNull()
  })

  it('handleDelete无deleteItem时不调用api', async () => {
    const api = createMockApi()
    const { result } = renderHook(() => useCrudPage<TestItem>({ api }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    // Delete without setting deleteItem
    await act(async () => {
      await result.current.handleDelete()
    })

    expect(api.delete).not.toHaveBeenCalled()
  })

  it('API错误时显示错误提示', async () => {
    const errorApi = createMockApi({
      page: vi.fn().mockRejectedValue(new Error('Network error')),
    })
    const { result } = renderHook(() => useCrudPage<TestItem>({ api: errorApi }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    expect(getApiErrorMessage).toHaveBeenCalled()
  })

  it('提交失败时显示错误提示', async () => {
    const errorApi = createMockApi({
      add: vi.fn().mockRejectedValue(new Error('Submit error')),
    })
    const { result } = renderHook(() => useCrudPage<TestItem>({ api: errorApi }))

    await waitFor(() => {
      expect(result.current.loading).toBe(false)
    })

    act(() => {
      result.current.openAdd()
    })

    await act(async () => {
      await result.current.handleSubmit({ name: 'test' })
    })

    expect(showToast).toHaveBeenCalledWith('error', expect.any(String))
  })
})
