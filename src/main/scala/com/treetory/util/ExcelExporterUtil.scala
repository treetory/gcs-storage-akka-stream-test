package com.treetory.util

import com.treetory.actor.Faker
import org.apache.poi.xssf.streaming.{SXSSFSheet, SXSSFWorkbook}
import org.slf4j.LoggerFactory

import java.io.{File, FileOutputStream}

object ExcelExporterUtil {

  def logger = LoggerFactory.getLogger(this.getClass)

  def export(sample: Seq[Faker]): Unit = {

//    val tempFile = File.createTempFile("fakerExport", ".xlsx")
    val tempFile = new File("./fakerExport.xlsx")
    val fileOut  = new FileOutputStream(tempFile)

    val workbook = new SXSSFWorkbook
    workbook.setCompressTempFiles(true)

    val style = workbook.createCellStyle()
    val font  = workbook.createFont()
    font.setBold(true)
    style.setFont(font)

    val sheet: SXSSFSheet = workbook.createSheet("faker")
    sheet.setRandomAccessWindowSize(100) // 메모리 행 100개로 제한, 초과 시 Disk로 flush

    val headerRow = sheet.createRow(0)

    var rowIndex: Int = 1

    val columns = List(
      "id",
      "title",
      "client",
      "area",
      "country",
      "contact",
      "assignee",
      "progress",
      "startTimestamp",
      "endTimestamp",
      "budget",
      "transaction",
      "account",
      "version",
      "available"
    )

    // 셀 칼럼 크기 설정
    sample.map(f => {
      if (rowIndex == 1) {
        for ((columnName, index) <- columns.zipWithIndex) {
          val headerCell = headerRow.createCell(index)
          headerCell.setCellValue(columnName)
          headerCell.setCellStyle(style)
        }
      }
      val row   = sheet.createRow(rowIndex)
      val cell0 = row.createCell(0)
      cell0.setCellValue(f.id)
      val cell1 = row.createCell(1)
      cell1.setCellValue(f.title)
      val cell2 = row.createCell(2)
      cell2.setCellValue(f.client)
      val cell3 = row.createCell(3)
      cell3.setCellValue(f.area)
      val cell4 = row.createCell(4)
      cell4.setCellValue(f.country)
      val cell5 = row.createCell(5)
      cell5.setCellValue(f.contact)
      val cell6 = row.createCell(6)
      cell6.setCellValue(f.assignee)
      val cell7 = row.createCell(7)
      cell7.setCellValue(f.progress)
      val cell8 = row.createCell(8)
      cell8.setCellValue(f.startTimestamp)
      val cell9 = row.createCell(9)
      cell9.setCellValue(f.endTimestamp)
      val cell10 = row.createCell(10)
      cell10.setCellValue(f.budget)
      val cell11 = row.createCell(11)
      cell11.setCellValue(f.transaction)
      val cell12 = row.createCell(12)
      cell12.setCellValue(f.account)
      val cell13 = row.createCell(13)
      cell13.setCellValue(f.version)
      val cell14 = row.createCell(14)
      cell14.setCellValue(f.available)
      val cell15 = row.createCell(15)
      cell15.setCellStyle(style)

      rowIndex = rowIndex + 1
    })
    workbook.write(fileOut)
    workbook.close()
  }

  def getExcel(): File = {
    new File("./fakerExport.xlsx")
  }
}
