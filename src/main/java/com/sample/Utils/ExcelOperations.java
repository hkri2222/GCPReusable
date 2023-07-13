package com.sample.Utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelOperations {
    public static DataFormatter formatter = new DataFormatter();

    @DataProvider
    public static Object[][] ReadData() throws IOException
    {
        FileInputStream fileInputStream= new FileInputStream("src/main/resources/sample1.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook (fileInputStream);
        XSSFSheet worksheet=workbook.getSheet("Sheet1");
        XSSFRow Row=worksheet.getRow(0);

        int RowNum = worksheet.getPhysicalNumberOfRows();
        int ColNum= Row.getLastCellNum();

        Object Data[][]= new Object[RowNum-1][ColNum];

        for(int i=0; i<RowNum-1; i++)
        {
            XSSFRow row= worksheet.getRow(i+1);

            for (int j=0; j<ColNum; j++)
            {
                if(row==null)
                    Data[i][j]= "";
                else
                {
                    XSSFCell cell= row.getCell(j);
                    if(cell==null)
                        Data[i][j]= "";
                    else
                    {
                        String value=formatter.formatCellValue(cell);
                        Data[i][j]=value;
                    }
                }
            }
        }

        return Data;
    }

}
