import axios from 'axios'
import type {
  Owner, Employee, House, Fee, Parking, Complaint, Repair, Duty,
  PageResult, ApiResult, User,
} from './types'

const http = axios.create({ baseURL: '/api', timeout: 15000 })

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

http.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      // 通知 AuthContext 清除状态，触发 React 路由跳转
      window.dispatchEvent(new Event('auth:unauthorized'))
    }
    return Promise.reject(err)
  },
)

// Auth
export const authApi = {
  login: (data: { username: string; password: string }) =>
    http.post<ApiResult<{ token: string; user: User }>>('/auth/login', data),
  info: () => http.get<ApiResult<{ user: User }>>('/auth/info'),
}

// Generic CRUD factory — 所有端点统一返回 ApiResult<T>
// RESTful 语义：GET / + params=分页，GET /:id + 查询，POST / + body=新增，PUT /:id + body=修改，DELETE /:id + 删除
function crud<T>(base: string) {
  return {
    page: (params: Record<string, string | number | undefined>) =>
      http.get<ApiResult<PageResult<T>>>(`/${base}`, { params }),
    get: (id: number) => http.get<ApiResult<T>>(`/${base}/${id}`),
    add: (data: Partial<T>) => http.post<ApiResult<void>>(`/${base}`, data),
    update: (data: Partial<T> & { id: number }) =>
      http.put<ApiResult<void>>(`/${base}/${data.id}`, data),
    delete: (id: number) => http.delete<ApiResult<void>>(`/${base}/${id}`),
  }
}

export const ownerApi = crud<Owner>('owner')
export const employeeApi = crud<Employee>('employee')
export const houseApi = {
  ...crud<House>('house'),
  getByOwner: (ownerId: number) => http.get<ApiResult<House[]>>(`/house/owner/${ownerId}`),
}
export const feeApi = crud<Fee>('fee')
export const parkingApi = {
  ...crud<Parking>('parking'),
  getByOwner: (ownerId: number) => http.get<ApiResult<Parking[]>>(`/parking/owner/${ownerId}`),
}
export const complaintApi = crud<Complaint>('complaint')
export const repairApi = crud<Repair>('repair')
export const dutyApi = crud<Duty>('duty')

export interface DashboardStats {
  owners: number
  houses: number
  fees: number
  complaints: number
  repairs: number
}

export const dashboardApi = {
  stats: () => http.get<ApiResult<DashboardStats>>('/dashboard/stats'),
}
