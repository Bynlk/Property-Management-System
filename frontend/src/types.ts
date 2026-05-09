export interface Owner {
  id: number
  name: string
  gender: string
  phone: string
  idCard: string
  moveInDate: string
}

export interface Employee {
  id: number
  name: string
  gender: string
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
  status: string
  ownerName?: string
}

export interface Fee {
  id: number
  ownerId: number
  houseId: number | null
  feeType: string
  amount: number
  shouldPayDate: string
  status: string
  ownerName?: string
  houseInfo?: string
}

export interface Parking {
  id: number
  spotNumber: string
  licensePlate: string | null
  ownerId: number | null
  status: string
  ownerName?: string
}

export interface Complaint {
  id: number
  ownerId: number
  title: string
  content: string
  createTime: string
  status: string
  ownerName?: string
}

export interface Repair {
  id: number
  ownerId: number
  deviceName: string
  faultDescription: string
  repairPerson: string | null
  status: string
  ownerName?: string
}

export interface Duty {
  id: number
  employeeId: number
  dutyDate: string
  shift: string
  employeeName?: string
}

export interface User {
  id: number
  username: string
  realName: string
  role: string
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  totalPages: number
}

export interface ApiResult {
  code: number
  msg: string
  [key: string]: unknown
}
