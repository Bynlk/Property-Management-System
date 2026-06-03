/**
 * 将表单数据转换为 API payload
 * - 将空字符串转换为 null（可选字段）
 * - 将数字字符串转换为数字
 * - 移除 undefined 值
 */
export function formDataToPayload<T extends Record<string, unknown>>(
  form: Record<string, string>,
  numericFields?: string[],
): Partial<T> {
  const payload: Record<string, unknown> = {}

  for (const [key, value] of Object.entries(form)) {
    if (value === undefined) continue

    // 空字符串转 null
    if (value === '') {
      payload[key] = null
      continue
    }

    // 数字字段转换
    if (numericFields?.includes(key)) {
      const num = Number(value)
      payload[key] = isNaN(num) ? null : num
      continue
    }

    payload[key] = value
  }

  return payload as Partial<T>
}
