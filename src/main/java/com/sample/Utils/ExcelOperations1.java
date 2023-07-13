package com.sample.Utils;

import com.sample.steps.CustomBaseStep;
import com.sample.steps.GCSfileUploadSteps;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelOperations1 extends CustomUtilStep {
    static String excelfilepath= filepath;
    public static DataFormatter formatter = new DataFormatter();

    @DataProvider
    public Object[][] ReadData() throws IOException
    {
        FileInputStream fileInputStream= new FileInputStream(excelfilepath);
        XSSFWorkbook workbook = new XSSFWorkbook (fileInputStream);
        XSSFSheet worksheet=workbook.getSheet("Sheet1");
        XSSFRow Row=worksheet.getRow(0);

        int RowNum = worksheet.getPhysicalNumberOfRows();
        int ColNum= Row.getLastCellNum();

        Object Data[][]= new Object[RowNum-1][ColNum];

        List<TestCase> stepDataRows = new ArrayList<>();
        for(int i=0; i<RowNum-1; i++) {
            TestCase testCase = new TestCase();
            XSSFRow row= worksheet.getRow(i+1);
            XSSFRow headerrow= worksheet.getRow(0);

            for (int j=0; j<ColNum; j++) {
                if (row == null){
                    testCase.setTestId(" ");
                    testCase.setMessage(" ");
                    testCase.setTestCaseName(" ");
            }
                else
                {
                    if(formatter.formatCellValue(headerrow.getCell(j)).equalsIgnoreCase("TCID")) {
                        XSSFCell cell = row.getCell(j);
                        if (cell == null)
                            testCase.setTestId(" ");
                        else {
                            String value = formatter.formatCellValue(cell);
                            testCase.setTestId(value);
                        }
                    }
                    if(formatter.formatCellValue(headerrow.getCell(j)).equalsIgnoreCase("MESSAGE")) {
                        XSSFCell cell = row.getCell(j);
                        if (cell == null)
                            testCase.setMessage(" ");
                        else {
                            String value = formatter.formatCellValue(cell);
                            testCase.setMessage(value);
                        }
                    }
                    if(formatter.formatCellValue(headerrow.getCell(j)).equalsIgnoreCase("TESTCASENAME")) {
                        XSSFCell cell = row.getCell(j);
                        if (cell == null)
                            testCase.setTestCaseName(" ");
                        else {
                            String value = formatter.formatCellValue(cell);
                            testCase.setTestCaseName(value);
                        }
                    }
                }
            }
            stepDataRows.add(testCase);
        }

        Object[][] dataobj = copyDataRowtoArray(stepDataRows);
        return dataobj;
    }

    private Object[][] copyDataRowtoArray(List<TestCase> dataRows) {
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
