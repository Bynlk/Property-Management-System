import { describe, it, expect } from 'vitest'
import { getStatusBadgeClass, STATUS_BADGE_MAP } from '../constants'

describe('getStatusBadgeClass', () => {
  it('返回正确的徽章类名', () => {
    expect(getStatusBadgeClass('待处理')).toBe('badge-warning')
    expect(getStatusBadgeClass('处理中')).toBe('badge-info')
    expect(getStatusBadgeClass('已处理')).toBe('badge-success')
    expect(getStatusBadgeClass('未缴')).toBe('badge-danger')
    expect(getStatusBadgeClass('已缴')).toBe('badge-success')
  })

  it('null 返回默认 badge-info', () => {
    expect(getStatusBadgeClass(null)).toBe('badge-info')
  })

  it('undefined 返回默认 badge-info', () => {
    expect(getStatusBadgeClass(undefined)).toBe('badge-info')
  })

  it('未知状态返回默认 badge-info', () => {
    expect(getStatusBadgeClass('未知状态')).toBe('badge-info')
  })

  it('所有枚举状态都有映射', () => {
    const allStatuses = [
      '待处理', '处理中', '已处理',
      '待维修', '维修中', '已完成',
      '未缴', '已缴',
      '已入住', '空置', '装修中',
      '使用中', '空闲',
      '早班', '中班', '晚班',
    ]
    for (const status of allStatuses) {
      expect(STATUS_BADGE_MAP[status]).toBeDefined()
      expect(getStatusBadgeClass(status)).toBe(STATUS_BADGE_MAP[status])
    }
  })
})
