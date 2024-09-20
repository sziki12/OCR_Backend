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
@RequestMapping("/api")
@CrossOrigin
class ReceiptController(
    private val receiptService: ReceiptService,
    private val householdService: HouseholdService,
    private val chartService: ChartService,
    ) {

    val gson = Gson()
    @GetMapping("/household/{householdId}/receipt")
    fun getAllReceipts(@PathVariable householdId: UUID): List<Receipt> {
        val household = householdService.getHousehold(householdId)
        if(household.isPresent)
        {
            return  receiptService.getReceiptsByHousehold(household.get())
        }
        return listOf()
    }

    @GetMapping("receipt/{receiptId}")
    fun getReceiptById(@PathVariable receiptId: Long): Receipt = receiptService.getReceipt(receiptId).orElseThrow{
        ResponseStatusException(HttpStatus.NOT_FOUND,"Receipt with the $receiptId Id not exists")
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("receipt")
    fun createReceipt(@RequestBody receiptData: ReceiptDTO)//TODO Household
    {
        receiptService.saveReceipt(Receipt(receiptData))
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("receipt/{receiptId}")
    fun deleteReceipt(@PathVariable receiptId: Long)
    {
        receiptService.deleteReceipt(receiptId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("receipt/{receiptId}")
    fun updateReceipt(@PathVariable receiptId: Long, @RequestBody receiptData: ReceiptDTO)
    {
        receiptService.updateReceipt(Receipt(receiptId,receiptData))
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/household/{householdId}/chart")
    fun getReceiptsChartData(@RequestBody request: ChartRequestDTO, @PathVariable householdId: UUID): ResponseEntity<PieChartDTO> {
        val household = householdService.getHousehold(householdId)
        if(household.isPresent)
        {
            val chartData = chartService.getPieChartData(household.get(), request)
            return ResponseEntity.ok().body(chartData)
        }
        return ResponseEntity.notFound().build()
    }
}