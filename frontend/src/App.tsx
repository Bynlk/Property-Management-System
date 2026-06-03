import { lazy, Suspense } from 'react'
import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import Layout from './components/Layout'
import { ToastProvider } from './components/Toast'
import ErrorBoundary from './components/ErrorBoundary'
import LoginPage from './pages/LoginPage'

// 代码分割：业务页面懒加载
const DashboardPage = lazy(() => import('./pages/DashboardPage'))
const OwnerPage = lazy(() => import('./pages/OwnerPage'))
const EmployeePage = lazy(() => import('./pages/EmployeePage'))
const HousePage = lazy(() => import('./pages/HousePage'))
const FeePage = lazy(() => import('./pages/FeePage'))
const ParkingPage = lazy(() => import('./pages/ParkingPage'))
const ComplaintPage = lazy(() => import('./pages/ComplaintPage'))
const RepairPage = lazy(() => import('./pages/RepairPage'))
const DutyPage = lazy(() => import('./pages/DutyPage'))

function PageLoading() {
  return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <div className="flex flex-col items-center gap-3">
        <div className="w-8 h-8 border-2 border-white/20 border-t-white/80 rounded-full animate-spin" />
        <span className="text-sm text-text-secondary">加载中...</span>
      </div>
    </div>
  )
}

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />
}

function RoleRoute({ children, roles }: { children: React.ReactNode; roles: string[] }) {
  const { user } = useAuth()
  if (!user || !roles.includes(user.role)) {
    return <Navigate to="/" replace />
  }
  return <>{children}</>
}

function NotFound() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] gap-4">
      <div className="text-6xl font-bold text-white/10">404</div>
      <p className="text-text-secondary">页面不存在</p>
      <Link to="/" className="btn-primary px-5 py-2 text-sm">返回首页</Link>
    </div>
  )
}

export default function App() {
  return (
    <ErrorBoundary>
      <BrowserRouter>
        <AuthProvider>
          <ToastProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/"
              element={
                <PrivateRoute>
                  <Layout />
                </PrivateRoute>
              }
            >
              <Route index element={<Suspense fallback={<PageLoading />}><DashboardPage /></Suspense>} />
              <Route path="owner" element={<Suspense fallback={<PageLoading />}><OwnerPage /></Suspense>} />
              <Route path="employee" element={<Suspense fallback={<PageLoading />}><EmployeePage /></Suspense>} />
              <Route path="house" element={<Suspense fallback={<PageLoading />}><HousePage /></Suspense>} />
              <Route path="fee" element={<Suspense fallback={<PageLoading />}><FeePage /></Suspense>} />
              <Route path="parking" element={<Suspense fallback={<PageLoading />}><ParkingPage /></Suspense>} />
              <Route path="complaint" element={<Suspense fallback={<PageLoading />}><ComplaintPage /></Suspense>} />
              <Route path="repair" element={<Suspense fallback={<PageLoading />}><RepairPage /></Suspense>} />
              <Route path="duty" element={<RoleRoute roles={['admin']}><Suspense fallback={<PageLoading />}><DutyPage /></Suspense></RoleRoute>} />
              <Route path="*" element={<NotFound />} />
            </Route>
          </Routes>
          </ToastProvider>
        </AuthProvider>
      </BrowserRouter>
    </ErrorBoundary>
  )
}
