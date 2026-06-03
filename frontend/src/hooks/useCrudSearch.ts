import { useState, useMemo } from 'react'
import { useDebounce } from './useDebounce'

interface SearchFieldDef {
  key: string
  type?: 'text' | 'select'
}

/**
 * CRUD 搜索状态管理 Hook
 * - 文本输入自动 debounce（300ms），复用 useDebounce
 * - 下拉筛选立即生效
 * - 返回稳定的 searchParams 对象
 */
export function useCrudSearch(searchFields: SearchFieldDef[]) {
  const textKeys = useMemo(
    () => searchFields.filter(f => (f.type ?? 'text') === 'text').map(f => f.key),
    [searchFields],
  )

  const [values, setValues] = useState<Record<string, string>>(() =>
    Object.fromEntries(searchFields.map(f => [f.key, '']))
  )

  // Extract text values for debouncing
  const textValues = useMemo(() => {
    const result: Record<string, string> = {}
    for (const key of textKeys) {
      result[key] = values[key] ?? ''
    }
    return result
  }, [values, textKeys])

  // Debounce the text values object using useDebounce
  const debouncedText = useDebounce(textValues, 300)

  // 合并：文本字段用 debounce 值，select 字段用原始值
  const searchParams = useMemo(() => {
    const params: Record<string, string | undefined> = {}
    for (const f of searchFields) {
      const isText = (f.type ?? 'text') === 'text'
      params[f.key] = isText ? debouncedText[f.key] : values[f.key]
    }
    return params
  }, [searchFields, values, debouncedText])

  const setValue = (key: string, value: string) => {
    setValues(prev => ({ ...prev, [key]: value }))
  }

  return { values, setValue, searchParams }
}
