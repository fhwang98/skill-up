import * as React from "react"
import { DayPicker, useDayPicker } from "react-day-picker"
import { cn } from "@/lib/utils"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { format } from "date-fns"
import { ko } from "date-fns/locale"

// Nav는 숨기고 MonthCaption 안에 화살표를 직접 포함
function CalendarHeader({ calendarMonth }) {
  const { nextMonth, previousMonth, goToMonth } = useDayPicker()

  return (
    <div className="flex items-center justify-between h-7 px-1">
      <button
        type="button"
        onClick={() => previousMonth && goToMonth(previousMonth)}
        disabled={!previousMonth}
        className={cn(
          "h-7 w-7 inline-flex items-center justify-center rounded-md border border-input",
          previousMonth ? "opacity-50 hover:opacity-100 hover:bg-accent cursor-pointer" : "opacity-20 cursor-not-allowed"
        )}
      >
        <ChevronLeft className="h-4 w-4" />
      </button>

      <span className="text-sm font-medium">
        {format(calendarMonth.date, "yyyy년 MM월", { locale: ko })}
      </span>

      <button
        type="button"
        onClick={() => nextMonth && goToMonth(nextMonth)}
        disabled={!nextMonth}
        className={cn(
          "h-7 w-7 inline-flex items-center justify-center rounded-md border border-input",
          nextMonth ? "opacity-50 hover:opacity-100 hover:bg-accent cursor-pointer" : "opacity-20 cursor-not-allowed"
        )}
      >
        <ChevronRight className="h-4 w-4" />
      </button>
    </div>
  )
}

function Calendar({ className, classNames, showOutsideDays = true, ...props }) {
  return (
    <DayPicker
      showOutsideDays={showOutsideDays}
      className={cn("p-3", className)}
      classNames={{
        months: "flex flex-col sm:flex-row gap-4",
        month: "flex flex-col gap-4",
        month_caption: "flex justify-center items-center",
        caption_label: "hidden", // CalendarHeader에서 직접 렌더링
        nav: "hidden",           // CalendarHeader에서 직접 렌더링
        month_grid: "w-full border-collapse space-y-1",
        weekdays: "flex",
        weekday: "text-muted-foreground rounded-md w-8 font-normal text-[0.8rem] text-center",
        week: "flex w-full mt-2",
        day: "relative p-0 text-center text-sm focus-within:relative focus-within:z-20",
        day_button: cn(
          "h-8 w-8 p-0 font-normal rounded-md",
          "hover:bg-accent hover:text-accent-foreground",
          "focus:outline-none focus:ring-2 focus:ring-ring"
        ),
        selected: "[&>button]:bg-primary [&>button]:text-primary-foreground [&>button]:hover:bg-primary [&>button]:hover:text-primary-foreground",
        today: "[&>button]:bg-accent [&>button]:text-accent-foreground",
        outside: "text-muted-foreground opacity-50",
        disabled: "text-muted-foreground opacity-50",
        range_middle: "[&>button]:bg-accent [&>button]:text-accent-foreground",
        hidden: "invisible",
        ...classNames,
      }}
      components={{
        MonthCaption: (props) => <CalendarHeader {...props} />,
      }}
      {...props}
    />
  )
}

export { Calendar }
