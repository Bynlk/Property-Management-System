/**
 * 枚举常量集中管理
 * 将各页面中硬编码的 select options 统一定义在此处
 */

/** 状态徽章颜色映射 */
export const STATUS_BADGE_MAP: Record<string, string> = {
  '待处理': 'badge-warning',
  '处理中': 'badge-info',
  '已处理': 'badge-success',
  '待维修': 'badge-warning',
  '维修中': 'badge-info',
  '已完成': 'badge-success',
  '未缴': 'badge-danger',
  '已缴': 'badge-success',
  '已入住': 'badge-success',
  '空置': 'badge-info',
  '装修中': 'badge-warning',
  '使用中': 'badge-info',
  '空闲': 'badge-success',
  '早班': 'badge-warning',
  '中班': 'badge-info',
  '晚班': 'badge-purple',
}

/** 获取状态徽章 CSS 类名 */
export function getStatusBadgeClass(status: string | null | undefined): string {
  if (!status) return 'badge-info'
  return STATUS_BADGE_MAP[status] ?? 'badge-info'
}

/** 性别选项 */
export const GENDER_OPTIONS = [
  { value: '男', label: '男' },
  { value: '女', label: '女' },
]

/** 费用类型选项 */
export const FEE_TYPE_OPTIONS = [
  { value: '物业费', label: '物业费' },
  { value: '水费', label: '水费' },
  { value: '电费', label: '电费' },
  { value: '燃气费', label: '燃气费' },
]

/** 费用状态选项 */
export const FEE_STATUS_OPTIONS = [
  { value: '未缴', label: '未缴' },
  { value: '已缴', label: '已缴' },
]

/** 投诉状态选项 */
export const COMPLAINT_STATUS_OPTIONS = [
  { value: '待处理', label: '待处理' },
  { value: '处理中', label: '处理中' },
  { value: '已处理', label: '已处理' },
]

/** 报修状态选项 */
export const REPAIR_STATUS_OPTIONS = [
  { value: '待维修', label: '待维修' },
  { value: '维修中', label: '维修中' },
  { value: '已完成', label: '已完成' },
]

/** 房屋状态选项 */
export const HOUSE_STATUS_OPTIONS = [
  { value: '已入住', label: '已入住' },
  { value: '空置', label: '空置' },
  { value: '装修中', label: '装修中' },
]

/** 停车位状态选项 */
export const PARKING_STATUS_OPTIONS = [
  { value: '使用中', label: '使用中' },
  { value: '空闲', label: '空闲' },
]

/** 值班班次选项 */
export const DUTY_SHIFT_OPTIONS = [
  { value: '早班', label: '早班' },
  { value: '中班', label: '中班' },
  { value: '晚班', label: '晚班' },
]
