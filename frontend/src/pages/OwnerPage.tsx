import CrudPage from '../components/CrudPage'
import { Users } from 'lucide-react'
import { ownerApi } from '../api'
import { GENDER_OPTIONS } from '../constants'
import type { Owner } from '../types'

const columns = [
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'name', title: '姓名' },
  { key: 'gender', title: '性别', width: '80px' },
  { key: 'phone', title: '手机号' },
  { key: 'idCard', title: '身份证号' },
  { key: 'moveInDate', title: '入住日期' },
]

const fields = [
  { name: 'name', label: '姓名', required: true },
  { name: 'gender', label: '性别', type: 'select' as const, options: GENDER_OPTIONS },
  { name: 'phone', label: '手机号', pattern: '^1[3-9]\\d{9}$', patternMessage: '请输入正确的11位手机号' },
  { name: 'idCard', label: '身份证号', pattern: '^$|^\\d{17}[\\dXx]$', patternMessage: '请输入正确的18位身份证号' },
  { name: 'moveInDate', label: '入住日期', type: 'date' as const },
]

export default function OwnerPage() {
  return (
    <CrudPage<Owner>
      title="业主管理" subtitle="管理小区业主信息" icon={Users} addLabel="新增业主"
      api={ownerApi}
      searchFields={[
        { key: 'name', placeholder: '搜索姓名' },
        { key: 'phone', placeholder: '搜索手机号' },
      ]}
      columns={columns} fields={fields}
      getInitialValues={(item) => ({
        name: item.name, gender: item.gender ?? '', phone: item.phone,
        idCard: item.idCard, moveInDate: item.moveInDate,
      })}
      getDeleteMessage={(item) => `确定要删除业主「${item.name}」吗？`}
    />
  )
}
