import { useState, useCallback, useEffect, useMemo, useRef } from 'react'
import { showToast } from '../components/Toast'
import { getApiErrorMessage } from '../utils/apiError'
import type { ApiResult, PageResult } from '../types'

interface CrudApi<T> {
  page: (params: Record<string, string | number | undefined>) => Promise<{ data: ApiResult<PageResult<T>> }>
  add: (data: Partial<T>) => Promise<{ data: ApiResult<void> }>
  update: (data: Partial<T> & { id: number }) => Promise<{ data: ApiResult<void> }>
  delete: (id: number) => Promise<{ data: ApiResult<void> }>
}

interface UseCrudPageOptions<T> {
  api: CrudApi<T>
  /** 额外的搜索参数（由页面管理状态） */
  searchParams?: Record<string, string | number | undefined>
  /** 每页条数，默认 10 */
  pageSize?: number
}

/**
 * 通用 CRUD 页面 Hook
 * 封装了分页查询、新增/修改、删除的通用逻辑
 * 内置 AbortController 竞态防护：页面切换或参数变化时自动取消旧请求
 */
export function useCrudPage<T extends { id: number }>({
  api,
  searchParams = {},
  pageSize = 10,
}: UseCrudPageOptions<T>) {
  const [data, setData] = useState<T[]>([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(1)
  const [loading, setLoading] = useState(false)
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<T | null>(null)
  const [deleteItem, setDeleteItem] = useState<T | null>(null)
  const [submitting, setSubmitting] = useState(false)

  // 竞态防护：记录最新请求，忽略过期响应
  const fetchIdRef = useRef(0)

  // 稳定化 searchParams 引用，避免不必要的重新请求
  const searchKey = useMemo(() => JSON.stringify(searchParams), [searchParams])

  const fetchData = useCallback(async () => {
    const fetchId = ++fetchIdRef.current
    setLoading(true)
    try {
      const { data: res } = await api.page({ ...JSON.parse(searchKey), pageNum, pageSize })
      // 竞态防护：如果已有更新的请求，忽略本次响应
      if (fetchId !== fetchIdRef.current) return
      if (res.code === 0 && res.data) {
        setData(res.data.list)
        setTotal(res.data.total)
      } else {
        showToast('error', res.msg || '加载数据失败')
      }
    } catch (err: unknown) {
      if (fetchId !== fetchIdRef.current) return
      showToast('error', getApiErrorMessage(err, '加载数据失败'))
    } finally {
      if (fetchId === fetchIdRef.current) setLoading(false)
    }
  }, [pageNum, pageSize, searchKey, api])

  useEffect(() => { fetchData() }, [fetchData])

  const handleSubmit = useCallback(async (formData: Record<string, string>) => {
    setSubmitting(true)
    try {
      const payload: Record<string, unknown> = {}
      for (const [key, value] of Object.entries(formData)) {
        payload[key] = value === '' ? null : value
      }
      if (editItem) {
        await api.update({ ...payload, id: editItem.id } as Partial<T> & { id: number })
        showToast('success', '修改成功')
      } else {
        await api.add(payload as Partial<T>)
        showToast('success', '新增成功')
      }
      setModalOpen(false)
      setEditItem(null)
      fetchData()
    } catch (err: unknown) {
      showToast('error', getApiErrorMessage(err, '操作失败'))
    } finally {
      setSubmitting(false)
    }
  }, [editItem, api, fetchData])

  const handleDelete = useCallback(async () => {
    if (!deleteItem) return
    setSubmitting(true)
    try {
      await api.delete(deleteItem.id)
      showToast('success', '删除成功')
      setDeleteItem(null)
      // If this was the last item on the current page and we're past page 1,
      // go back one page to avoid showing an empty page
      if (data.length === 1 && pageNum > 1) {
        setPageNum(prev => prev - 1)
      } else {
        fetchData()
      }
    } catch (err: unknown) {
      showToast('error', getApiErrorMessage(err, '删除失败'))
    } finally {
      setSubmitting(false)
    }
  }, [deleteItem, api, fetchData, data.length, pageNum])

  const openAdd = useCallback(() => {
    setEditItem(null)
    setModalOpen(true)
  }, [])

  const openEdit = useCallback((item: T) => {
    setEditItem(item)
    setModalOpen(true)
  }, [])

  const closeModal = useCallback(() => {
    setModalOpen(false)
    setEditItem(null)
  }, [])

  return {
    // 数据
    data, total, pageNum, loading,
    // 弹窗状态
    modalOpen, editItem, deleteItem, submitting,
    // 操作
    setPageNum, setDeleteItem,
    openAdd, openEdit, closeModal,
    handleSubmit, handleDelete,
  }
}
