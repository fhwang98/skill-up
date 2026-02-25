import * as React from "react"
import { format, parseISO } from "date-fns"
import { ko } from "date-fns/locale"
import { CalendarIcon } from "lucide-react"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

/**
 * DatePicker 공통 컴포넌트
 *
 * @param {string}   value      - "YYYY-MM-DD" 형식 문자열
 * @param {function} onChange   - 선택 시 "YYYY-MM-DD" 문자열로 콜백
 * @param {string}   placeholder
 * @param {Date}     minDate    - 선택 불가 최소 날짜
 * @param {string}   className
 */
export function DatePicker({ value, onChange, placeholder = "날짜 선택", minDate, className }) {
  const selected = value ? parseISO(value) : undefined

  const handleSelect = (date) => {
    if (!date) return
    onChange(format(date, "yyyy-MM-dd"))
  }

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className={cn(
            "w-full justify-start text-left font-normal h-9",
            !selected && "text-muted-foreground",
            className
          )}
        >
          <CalendarIcon className="mr-2 h-4 w-4 shrink-0" />
          {selected ? format(selected, "yyyy년 MM월 dd일", { locale: ko }) : placeholder}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0" align="start">
        <Calendar
          mode="single"
          selected={selected}
          onSelect={handleSelect}
          disabled={minDate ? (date) => date < minDate : undefined}
          initialFocus
        />
      </PopoverContent>
    </Popover>
  )
}
