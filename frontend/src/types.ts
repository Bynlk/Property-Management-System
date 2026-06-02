export interface Owner {
  id: number
  name: string
  gender: '男' | '女' | ''
  phone: string
  idCard: string
  moveInDate: string
}

export interface Employee {
  id: number
  name: string
  gender: '男' | '女' | ''
  phone: string
  position: string
  hireDate: string
}

export interface House {
  id: number
  building: string
  unit: string
  roomNumber: string
  area: number
  houseType: string
  ownerId: number | null
  status: '已入住' | '空置' | '装修中' | ''
  ownerName?: string
}

export interface Fee {
  id: number
  ownerId: number
  houseId: number | null
  feeType: '物业费' | '水费' | '电费' | '燃气费' | ''
  amount: number
  shouldPayDate: string
  paidDate: string | null
  status: '未缴' | '已缴' | ''
  ownerName?: string
  houseInfo?: string
}

export interface Parking {
  id: number
  spotNumber: string
  licensePlate: string | null
  ownerId: number | null
  status: '使用中' | '空闲' | ''
  ownerName?: string
}

export interface Complaint {
  id: number
  ownerId: number
  title: string
  content: string
  createTime: string
  status: '待处理' | '处理中' | '已处理' | ''
  ownerName?: string
}

export interface Repair {
  id: number
  ownerId: number
  deviceName: string
  faultDescription: string
  repairPerson: string | null
  status: '待维修' | '维修中' | '已完成' | ''
  ownerName?: string
}

export interface Duty {
  id: number
  employeeId: number
  dutyDate: string
  shift: '早班' | '中班' | '晚班' | ''
  employeeName?: string
}

export interface User {
  id: number
  username: string
  realName: string
  role: 'admin' | 'user'
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  totalPages: number
}

/** 统一 API 响应封装 */
export interface ApiResult<T = unknown> {
  code: number
  msg: string
  data: T
}
