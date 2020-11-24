package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class gdp_rank_class extends AppCompatActivity {
    ListView recyclerView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    ArrayList<data_organize> uploadData;


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recyclerView);
        getData gt = new getData();
        gt.execute();
    }

    public void parseStringBuilder(StringBuilder mStringBuilder){
        Log.d(" Info ", "parseStringBuilder: Started parsing.");
        ArrayList<String> gdpData = new ArrayList<>();
        // splits the sb into rows.
        String[] rows = mStringBuilder.toString().split(":");

        //Add to the ArrayList
        for(int i=0; i<rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split(",");

            //use try catch to make sure there are no "" that try to parse into doubles.
            try{
                String x = columns[0];
                String y = columns[1];

                String cell = i + " " + x + " " + y;
                String cellInfo = "(x,y): ( "+ x +  "," + y + ")";

                //add the the uploadData ArrayList
                gdpData.add(cell);
                //uploadData.add(new data_organize(x, y));

            }catch (NumberFormatException e){

                Log.e("  eeeee ", "parseStringBuilder: NumberFormatException: " + e.getMessage());

            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gdpData);
        recyclerView.setAdapter(adapter);
    }

////////////////////////////////////////////////////////////////////////////////////
    private void readExcelData(){
        try {
            InputStream in = getAssets().open("gdp_excel.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            for(int r = 0; r < rowCount ; r++){
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();

                for (int c = 0; c < cellsCount; c++){
                    if(c > 2){
                        Log.i("Excel ", "Error");
                        break;
                    }else{
                        String value = getCellAsString(row, c, formulaEvaluator);
                        sb.append(value + ",");
                        //Log.i("Value ", value);
                    }
                }
                sb.append(":");
            }
            Log.i("Data : ", sb.toString());

            parseStringBuilder(sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        ////////////////////////////////////////////////////////////////////////////////////////
    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {

        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    //Log.i(" Boolean", value);
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("MM/dd/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                        //Log.i(" Numeric ", value);
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    arrayList.add(cellValue.getStringValue());
                    //Log.i(" String ", value);
                    break;
                default:
            }
            //recyclerView.setAdapter(adapter);
        } catch (NullPointerException e) {

            Log.i(" Info ", "getCellAsString: NullPointerException: " + e.getMessage() );
        }
        return value;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class getData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            readExcelData();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}