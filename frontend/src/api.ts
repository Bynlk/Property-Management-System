import axios from 'axios'
import type {
  Owner, Employee, House, Fee, Parking, Complaint, Repair, Duty,
  PageResult, ApiResult, User,
} from './types'

const http = axios.create({ baseURL: '/api' })

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

http.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  },
)

// Auth
export const authApi = {
  login: (data: { username: string; password: string }) =>
    http.post<ApiResult & { token: string; user: User }>('/auth/login', data),
  info: () => http.get<ApiResult & { user: User }>('/auth/info'),
}

// Generic CRUD factory
function crud<T>(base: string) {
  return {
    page: (params: Record<string, string | number | undefined>) =>
      http.get<PageResult<T>>(`/${base}/page`, { params }),
    get: (id: number) => http.get<T>(`/${base}/get/${id}`),
    add: (data: Partial<T>) => http.post<ApiResult>(`/${base}/add`, data),
    update: (data: Partial<T>) => http.post<ApiResult>(`/${base}/update`, data),
    delete: (id: number) => http.post<ApiResult>(`/${base}/delete/${id}`),
  }
}

export const ownerApi = crud<Owner>('owner')
export const employeeApi = crud<Employee>('employee')
export const houseApi = {
  ...crud<House>('house'),
  getByOwner: (ownerId: number) => http.get<House[]>(`/house/owner/${ownerId}`),
}
export const feeApi = crud<Fee>('fee')
export const parkingApi = {
  ...crud<Parking>('parking'),
  getByOwner: (ownerId: number) => http.get<Parking[]>(`/parking/owner/${ownerId}`),
}
export const complaintApi = crud<Complaint>('complaint')
export const repairApi = crud<Repair>('repair')
export const dutyApi = crud<Duty>('duty')
