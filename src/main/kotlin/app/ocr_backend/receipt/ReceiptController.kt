package app.ocr_backend.receipt

import app.ocr_backend.statistic.ChartRequestDTO
import app.ocr_backend.db_service.DBService
import app.ocr_backend.household.HouseholdService
import app.ocr_backend.statistic.ChartService
import app.ocr_backend.statistic.PieChartDTO
import com.google.gson.Gson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/household/{householdId}")
@CrossOrigin
class ReceiptController(
    private val receiptService: ReceiptService,
    private val householdService: HouseholdService,
    private val chartService: ChartService,
) {

    val gson = Gson()

    @GetMapping("/receipt")
    fun getAllReceipts(@PathVariable householdId: UUID): List<Receipt> {
        return receiptService.getReceiptsByHousehold(householdId)
    }

    @GetMapping("receipt/{receiptId}")
    fun getReceiptById(@PathVariable receiptId: Long, @PathVariable householdId: UUID): Receipt =
        receiptService.getReceipt(householdId, receiptId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Receipt with the $receiptId Id not exists")
        }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("receipt")
    fun createReceipt(@RequestBody receiptData: ReceiptDTO, @PathVariable householdId: UUID)//TODO Household
    {
        receiptService.saveReceipt(Receipt(receiptData))
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("receipt/{receiptId}")
    fun deleteReceipt(@PathVariable receiptId: Long, @PathVariable householdId: UUID) {
        receiptService.deleteReceipt(householdId, receiptId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("receipt/{receiptId}")
    fun updateReceipt(
        @PathVariable receiptId: Long, @RequestBody receiptData: ReceiptDTO,
        @PathVariable householdId: UUID
    ) {
        receiptService.updateReceipt(householdId, Receipt(receiptId, receiptData))
    }

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