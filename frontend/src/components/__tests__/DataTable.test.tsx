import { describe, it, expect, vi } from 'vitest'
import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import DataTable from '../DataTable'

interface TestItem {
  id: number
  name: string
  status: string
}

const columns = [
  { key: 'name', title: '姓名' },
  { key: 'status', title: '状态' },
]

function defaultProps(overrides?: Partial<React.ComponentProps<typeof DataTable<TestItem>>>) {
  return {
    columns,
    data: [] as TestItem[],
    total: 0,
    pageNum: 1,
    pageSize: 10,
    onPageChange: vi.fn(),
    loading: false,
    ...overrides,
  }
}

describe('DataTable', () => {
  describe('数据渲染', () => {
    it('正确渲染表格数据', () => {
      const data: TestItem[] = [
        { id: 1, name: '刘备', status: '已入住' },
        { id: 2, name: '关羽', status: '空置' },
      ]
      render(<DataTable {...defaultProps({ data, total: 2 })} />)

      expect(screen.getByText('刘备')).toBeInTheDocument()
      expect(screen.getByText('关羽')).toBeInTheDocument()
      expect(screen.getByText('已入住')).toBeInTheDocument()
      expect(screen.getByText('空置')).toBeInTheDocument()
    })

    it('渲染表头', () => {
      render(<DataTable {...defaultProps({ data: [{ id: 1, name: '刘备', status: '已入住' }], total: 1 })} />)

      expect(screen.getByText('姓名')).toBeInTheDocument()
      expect(screen.getByText('状态')).toBeInTheDocument()
    })

    it('支持自定义render', () => {
      const customColumns = [
        { key: 'name', title: '姓名', render: (item: TestItem) => <span data-testid={`name-${item.id}`}>{item.name}</span> },
      ]
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ columns: customColumns, data, total: 1 })} />)

      expect(screen.getByTestId('name-1')).toBeInTheDocument()
      expect(screen.getByTestId('name-1')).toHaveTextContent('刘备')
    })
  })

  describe('空状态', () => {
    it('数据为空时显示"暂无数据"', () => {
      render(<DataTable {...defaultProps()} />)

      expect(screen.getByText('暂无数据')).toBeInTheDocument()
    })

    it('data为空数组时显示空状态', () => {
      const { container } = render(<DataTable {...defaultProps({ data: [], total: 0 })} />)

      expect(screen.getByText('暂无数据')).toBeInTheDocument()
      // Should not have any data rows
      const rows = container.querySelectorAll('tbody tr')
      expect(rows.length).toBe(1) // only the empty state row
    })
  })

  describe('加载状态', () => {
    it('loading为true时显示骨架屏', () => {
      const { container } = render(<DataTable {...defaultProps({ loading: true })} />)

      // Skeleton elements should be present
      const skeletons = container.querySelectorAll('.skeleton')
      expect(skeletons.length).toBeGreaterThan(0)
    })

    it('loading为true时不显示空状态', () => {
      render(<DataTable {...defaultProps({ loading: true })} />)

      expect(screen.queryByText('暂无数据')).not.toBeInTheDocument()
    })

    it('loading为true时不显示数据', () => {
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 1, loading: true })} />)

      expect(screen.queryByText('刘备')).not.toBeInTheDocument()
    })
  })

  describe('分页', () => {
    it('单页时不显示分页控件', () => {
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 1, pageSize: 10 })} />)

      expect(screen.queryByLabelText('上一页')).not.toBeInTheDocument()
    })

    it('多页时显示分页控件', () => {
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 20, pageSize: 10, pageNum: 1 })} />)

      expect(screen.getByLabelText('上一页')).toBeInTheDocument()
      expect(screen.getByLabelText('下一页')).toBeInTheDocument()
    })

    it('显示正确的页码信息', () => {
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 25, pageSize: 10, pageNum: 1 })} />)

      // The pagination summary text contains total count and current page info
      const paginationText = screen.getByText(/共/)
      expect(paginationText).toHaveTextContent('25')
      expect(paginationText).toHaveTextContent('1/3')
    })

    it('点击下一页触发onPageChange', async () => {
      const user = userEvent.setup()
      const onPageChange = vi.fn()
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 20, pageSize: 10, pageNum: 1, onPageChange })} />)

      await user.click(screen.getByLabelText('下一页'))

      expect(onPageChange).toHaveBeenCalledWith(2)
    })

    it('点击上一页触发onPageChange', async () => {
      const user = userEvent.setup()
      const onPageChange = vi.fn()
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 20, pageSize: 10, pageNum: 2, onPageChange })} />)

      await user.click(screen.getByLabelText('上一页'))

      expect(onPageChange).toHaveBeenCalledWith(1)
    })

    it('第一页时上一页按钮禁用', () => {
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 20, pageSize: 10, pageNum: 1 })} />)

      expect(screen.getByLabelText('上一页')).toBeDisabled()
    })

    it('最后一页时下一页按钮禁用', () => {
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 20, pageSize: 10, pageNum: 2 })} />)

      expect(screen.getByLabelText('下一页')).toBeDisabled()
    })

    it('点击页码触发onPageChange', async () => {
      const user = userEvent.setup()
      const onPageChange = vi.fn()
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 50, pageSize: 10, pageNum: 1, onPageChange })} />)

      await user.click(screen.getByLabelText('第3页'))

      expect(onPageChange).toHaveBeenCalledWith(3)
    })

    it('当前页码按钮有aria-current', () => {
      const data: TestItem[] = [{ id: 1, name: '刘备', status: '已入住' }]
      render(<DataTable {...defaultProps({ data, total: 20, pageSize: 10, pageNum: 1 })} />)

      const currentPageBtn = screen.getByLabelText('第1页')
      expect(currentPageBtn).toHaveAttribute('aria-current', 'page')
    })
  })
})
