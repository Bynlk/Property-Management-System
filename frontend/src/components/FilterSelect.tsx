import { DROPDOWN_ARROW_SVG } from '../utils/dropdownArrow'

interface Option {
  value: string
  label: string
}

interface Props {
  value: string
  onChange: (value: string) => void
  options: Option[]
  placeholder?: string
  ariaLabel?: string
}

/**
 * 通用筛选下拉框 — 统一带自定义箭头样式
 */
export default function FilterSelect({ value, onChange, options, placeholder, ariaLabel }: Props) {
  return (
    <select
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="px-4 py-2.5 input-glass text-sm appearance-none cursor-pointer min-w-[140px]"
      style={{
        backgroundImage: DROPDOWN_ARROW_SVG,
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'right 12px center',
      }}
      aria-label={ariaLabel}
    >
      {placeholder && <option value="">{placeholder}</option>}
      {options.map((opt) => (
        <option key={opt.value} value={opt.value}>{opt.label}</option>
      ))}
    </select>
  )
}
