package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
    public static String filePath = "src/test/resources/Team3_Restful_Ninjas_TestDataSheet.xlsx";

    public static List<Map<String, String>> getAllSheetData(String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheet(sheetName);
        DataFormatter formatter = new DataFormatter();
        List<Map<String, String>> listData = new ArrayList<>();

        Row header = sheet.getRow(0);
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            Map<String, String> rowData = new HashMap<>();
            for (int c = 0; c <= row.getLastCellNum(); c++) {
                String key = formatter.formatCellValue(header.getCell(c)).trim();
                String value = formatter.formatCellValue(row.getCell(c)).trim();
                rowData.put(key, value);
            }
            listData.add(rowData);
        }
        wb.close();
        return listData;
    }

    public static Map<String, String> readExcelData(String sheetName, String requestType) throws IOException {
        List<Map<String, String>> data = getAllSheetData(sheetName);
        for (Map<String, String> row : data) {
            if (row.get("ScenarioName").equalsIgnoreCase(requestType)) {
                return row;
            }
        }
        return null;
    }

}
