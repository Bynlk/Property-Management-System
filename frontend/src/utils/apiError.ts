/**
 * 从 Axios 错误响应中提取用户友好的错误消息
 */
export function getApiErrorMessage(err: unknown, fallback = '操作失败'): string {
  if (
    err &&
    typeof err === 'object' &&
    'response' in err
  ) {
    const resp = (err as { response?: { data?: { msg?: string } } }).response
    if (resp?.data?.msg) {
      return resp.data.msg
    }
  }
  return fallback
}
