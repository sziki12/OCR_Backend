package app.ocr_backend.statistic

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/household/{householdId}/statistic")
@CrossOrigin
class StatisticController(private val chartService: ChartService) {
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/chart")
    fun getReceiptsChartData(
        @RequestBody request: ChartRequestDTO,
        @PathVariable householdId: UUID
    ): ResponseEntity<PieChartDTO> {
        val chartData = chartService.getPieChartData(householdId, request)
        return ResponseEntity.ok().body(chartData)
    }
}