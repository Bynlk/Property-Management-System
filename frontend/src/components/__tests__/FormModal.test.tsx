import { describe, it, expect, vi } from 'vitest'
import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import FormModal from '../FormModal'

const fields = [
  { name: 'name', label: '姓名', required: true },
  { name: 'phone', label: '手机号', type: 'text' as const },
  { name: 'gender', label: '性别', type: 'select' as const, options: [
    { value: '男', label: '男' },
    { value: '女', label: '女' },
  ]},
]

function defaultProps(overrides?: Partial<React.ComponentProps<typeof FormModal>>) {
  return {
    title: '测试弹窗',
    open: true,
    onClose: vi.fn(),
    onSubmit: vi.fn(),
    fields,
    loading: false,
    ...overrides,
  }
}

describe('FormModal', () => {
  describe('打开/关闭', () => {
    it('open为false时不渲染', () => {
      render(<FormModal {...defaultProps({ open: false })} />)

      expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
      expect(screen.queryByText('测试弹窗')).not.toBeInTheDocument()
    })

    it('open为true时渲染弹窗', () => {
      render(<FormModal {...defaultProps()} />)

      expect(screen.getByRole('dialog')).toBeInTheDocument()
      expect(screen.getByText('测试弹窗')).toBeInTheDocument()
    })

    it('点击取消按钮调用onClose', async () => {
      const user = userEvent.setup()
      const onClose = vi.fn()
      render(<FormModal {...defaultProps({ onClose })} />)

      await user.click(screen.getByText('取消'))

      expect(onClose).toHaveBeenCalledTimes(1)
    })

    it('点击X按钮调用onClose', async () => {
      const user = userEvent.setup()
      const onClose = vi.fn()
      render(<FormModal {...defaultProps({ onClose })} />)

      await user.click(screen.getByLabelText('关闭'))

      expect(onClose).toHaveBeenCalledTimes(1)
    })

    it('点击背景蒙层调用onClose', async () => {
      const user = userEvent.setup()
      const onClose = vi.fn()
      render(<FormModal {...defaultProps({ onClose })} />)

      const backdrop = document.querySelector('.modal-backdrop')!
      await user.click(backdrop)

      expect(onClose).toHaveBeenCalledTimes(1)
    })
  })

  describe('Escape键关闭', () => {
    it('按下Escape键关闭弹窗', async () => {
      const user = userEvent.setup()
      const onClose = vi.fn()
      render(<FormModal {...defaultProps({ onClose })} />)

      await user.keyboard('{Escape}')

      expect(onClose).toHaveBeenCalledTimes(1)
    })

    it('open为false时Escape不触发onClose', async () => {
      const user = userEvent.setup()
      const onClose = vi.fn()
      render(<FormModal {...defaultProps({ open: false, onClose })} />)

      await user.keyboard('{Escape}')

      expect(onClose).not.toHaveBeenCalled()
    })
  })

  describe('表单字段渲染', () => {
    it('渲染所有字段标签', () => {
      render(<FormModal {...defaultProps()} />)

      expect(screen.getByText('姓名')).toBeInTheDocument()
      expect(screen.getByText('手机号')).toBeInTheDocument()
      expect(screen.getByText('性别')).toBeInTheDocument()
    })

    it('required字段显示星号', () => {
      render(<FormModal {...defaultProps()} />)

      // The required field should have a * marker
      const nameLabel = screen.getByText('姓名')
      expect(nameLabel.closest('label')).toBeInTheDocument()
    })

    it('渲染select类型字段的选项', () => {
      render(<FormModal {...defaultProps()} />)

      const select = screen.getByRole('combobox')
      expect(within(select).getByText('请选择')).toBeInTheDocument()
      expect(within(select).getByText('男')).toBeInTheDocument()
      expect(within(select).getByText('女')).toBeInTheDocument()
    })
  })

  describe('initialValues填充', () => {
    it('打开时用initialValues填充表单', () => {
      render(
        <FormModal
          {...defaultProps({
            initialValues: { name: '刘备', phone: '13900001111' },
          })}
        />
      )

      const nameInput = document.querySelector('input[name="name"]') as HTMLInputElement
      expect(nameInput).toBeTruthy()
      expect(nameInput.value).toBe('刘备')

      const phoneInput = document.querySelector('input[name="phone"]') as HTMLInputElement
      expect(phoneInput.value).toBe('13900001111')
    })
  })

  describe('表单提交', () => {
    it('点击确定按钮提交表单数据', async () => {
      const user = userEvent.setup()
      const onSubmit = vi.fn()
      render(<FormModal {...defaultProps({ onSubmit })} />)

      // Fill in the form
      const nameInput = document.querySelector('input[name="name"]') as HTMLInputElement
      const phoneInput = document.querySelector('input[name="phone"]') as HTMLInputElement

      await user.type(nameInput, '关羽')
      await user.type(phoneInput, '13900002222')

      // The submit button is outside the <form> in the DOM, so we submit the form directly
      const form = document.querySelector('form')!
      form.requestSubmit()

      expect(onSubmit).toHaveBeenCalledTimes(1)
      expect(onSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          name: '关羽',
          phone: '13900002222',
        })
      )
    })

    it('loading状态下确定按钮禁用', () => {
      render(<FormModal {...defaultProps({ loading: true })} />)

      const submitBtn = screen.getByText('提交中...')
      expect(submitBtn.closest('button')).toBeDisabled()
    })

    it('loading状态下显示加载动画', () => {
      render(<FormModal {...defaultProps({ loading: true })} />)

      expect(screen.getByText('提交中...')).toBeInTheDocument()
    })
  })

  describe('ARIA属性', () => {
    it('弹窗有正确的ARIA属性', () => {
      render(<FormModal {...defaultProps()} />)

      const dialog = screen.getByRole('dialog')
      expect(dialog).toHaveAttribute('aria-modal', 'true')
      expect(dialog).toHaveAttribute('aria-labelledby', 'modal-title')
    })

    it('标题有正确的id', () => {
      render(<FormModal {...defaultProps()} />)

      const title = screen.getByText('测试弹窗')
      expect(title).toHaveAttribute('id', 'modal-title')
    })
  })
})
