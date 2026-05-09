import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import OwnerPage from './pages/OwnerPage'
import EmployeePage from './pages/EmployeePage'
import HousePage from './pages/HousePage'
import FeePage from './pages/FeePage'
import ParkingPage from './pages/ParkingPage'
import ComplaintPage from './pages/ComplaintPage'
import RepairPage from './pages/RepairPage'
import DutyPage from './pages/DutyPage'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token')
  return token ? <>{children}</> : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <BrowserRouter>
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
          <Route index element={<DashboardPage />} />
          <Route path="owner" element={<OwnerPage />} />
          <Route path="employee" element={<EmployeePage />} />
          <Route path="house" element={<HousePage />} />
          <Route path="fee" element={<FeePage />} />
          <Route path="parking" element={<ParkingPage />} />
          <Route path="complaint" element={<ComplaintPage />} />
          <Route path="repair" element={<RepairPage />} />
          <Route path="duty" element={<DutyPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
