package com.sample.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelOperationsBQ {
    public static DataFormatter formatter = new DataFormatter();

    @DataProvider
    public  Object[][] ReadData() throws IOException
    {
        FileInputStream fileInputStream= new FileInputStream("src/main/resources/bqRequestResponse.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook (fileInputStream);
        XSSFSheet worksheet=workbook.getSheet("Sheet1");
        XSSFRow Row=worksheet.getRow(0);

        int RowNum = worksheet.getPhysicalNumberOfRows();
        int ColNum= Row.getLastCellNum();


        Object Data[][]= new Object[RowNum-1][ColNum];
        XSSFRow headerrow= worksheet.getRow(0);
        List<String> stepDataRows = new ArrayList<>();
        for(int i=0; i<RowNum-1; i++) {
            Map<String, Object> jsonMap = new HashMap<>();
            XSSFRow row= worksheet.getRow(i+1);

            for (int j=0; j<ColNum; j++)
            {
                if(row==null) {
                    // Data[i][j]= "";
                }
                else {
                    XSSFCell cell= row.getCell(j);
                    if(cell==null){

                        jsonMap.put(String.valueOf(headerrow.getCell(j)), " ");
                    }
                    else{
                        if (row.getCell(j).getCellType() == CellType.STRING) {
                            jsonMap.put(String.valueOf(headerrow.getCell(j)), cell.getStringCellValue());
                        }
                        else if (row.getCell(j).getCellType() == CellType.NUMERIC) {
                            jsonMap.put(String.valueOf(headerrow.getCell(j)), formatter.formatCellValue(cell));
                        } else if (row.getCell(j).getCellType() == CellType.BOOLEAN) {
                            jsonMap.put(String.valueOf(headerrow.getCell(j)), formatter.formatCellValue(cell));
                        } else if (row.getCell(j).getCellType() == CellType.BLANK) {
                            jsonMap.put(String.valueOf(headerrow.getCell(j)), "");
                        }
                    }
                }
            }
            Gson gson = new Gson();
            String json = gson.toJson(jsonMap);
            stepDataRows.add(json);
        }

        Object[][] dataobj = copyDataRowtoArray(stepDataRows);
        return dataobj;
    }

    private Object[][] copyDataRowtoArray(List<String> dataRows) {
        int rowCount = dataRows.size();
        Object[][] dataobj = null;
        if (rowCount > 0) {
            dataobj = new Object[rowCount][1];
            for (int i = 0; i < rowCount; i++) {
                dataobj[i][0] = dataRows.get(i);
            }
        } else {
            throw new RuntimeException("Data in data file is corrupted");
        }
        return dataobj;
    }

}
