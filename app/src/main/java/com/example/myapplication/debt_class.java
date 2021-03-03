package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class debt_class extends AppCompatActivity {

    ArrayList<String> arrayList = new ArrayList<>();
    String[] rows;
    ListView listViewDebt;
    TextView name_of_country;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<example_item> debt_data;

    ////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_1:
                startActivity(new Intent(this, demography.class));
                break;
            case R.id.item_2:
                startActivity(new Intent(this, gdp_rank_class.class));
                break;
            case  R.id.item_3:
                startActivity(new Intent(this, main_menu.class));
                break;
            case R.id.item_4:
                startActivity(new Intent(this, notes.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater2 = getMenuInflater();
        inflater2.inflate(R.menu.menu, menu);

        MenuItem item_population = menu.findItem(R.id.item_1);
        MenuItem item_gdp = menu.findItem(R.id.item_2);
        MenuItem item_menu = menu.findItem(R.id.item_3);
        MenuItem item_notes =menu.findItem(R.id.item_4);

        item_population.setTitle("Population of Countries");
        item_gdp.setTitle("Debt of Countries");
        item_menu.setTitle("Main menu");
        item_notes.setTitle("Notes");

        return true;
    }

    /////////////////////////////////////////////

    void setArrayListElements(){
        String top_5_countries[] = {"France","Germany","Italy","Japan","Untied Kingdom","United States"};

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1 , top_5_countries);
        listViewDebt.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, main_menu.class));
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_class);
        listViewDebt = findViewById(R.id.list_view_debt);
        name_of_country = findViewById(R.id.nameOfCountry);

        debt_data = new ArrayList<>();
        mRecyclerView =(RecyclerView) findViewById(R.id.recyclerViewDebt);
        mAdapter = new recycler_adapter(debt_data);
        mLayoutManager = new LinearLayoutManager(this);


        setArrayListElements();

        new getDebtData().execute();

        listViewDebt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Split the columns

                    String[] columns = rows[i].split(",");
                    String[] columns_data = new String[columns.length];
                    for(int a= 0; a < columns.length; a++){
                    columns_data[a] = columns[a];

                        Log.i("Columns  ", columns_data[a]);
                        if(a == 0){
                            name_of_country.setText(columns_data[0]);
                        }else{
                            String date = String.valueOf(1949 + a);
                            debt_data.add(new example_item(columns_data[a], date));
                        }
                    }

                mRecyclerView.setLayoutManager(mLayoutManager);
                if (debt_data.size() > 0) {
                    mRecyclerView.setAdapter(mAdapter);
                }else{
                    Log.i("ArrayList ", " is empty");
                    Toast.makeText(debt_class.this, "No data", Toast.LENGTH_SHORT).show();
                }
                listViewDebt.setAlpha(0);
            }
        });

    }

    private void readExcelData(){
        try {
            InputStream in = getAssets().open("top_5_countries_debt.xls");
            HSSFWorkbook workbook = new HSSFWorkbook(in);
            HSSFSheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            for(int r = 0; r < rowCount ; r++){
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();

                for (int c = 0; c < cellsCount; c++){
                        String value = getCellAsString(row, c, formulaEvaluator);
                        sb.append(value + ",");
                        Log.i("Data : ", value);
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

    ////////////////////////////////////////////////////////////////////////////
    public void parseStringBuilder(StringBuilder mStringBuilder) {
        Log.d(" Info ", "parseStringBuilder: Started parsing.");
        this.rows = mStringBuilder.toString().split(":");

        //Add to the ArrayList
        for (int i = 0; i < rows.length; i++) {
            //Split the columns
            String[] columns = rows[i].split(",");
            String[] columns_data = new String[columns.length];

            try {
                for(int j = 0; j < columns.length; j++){
                    columns_data[i] = columns[i];
                }
            } catch (NumberFormatException e) {
                Log.e("  eeeee ", "parseStringBuilder: NumberFormatException: " + e.getMessage());
            }

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////
    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {

        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();

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

                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    arrayList.add(cellValue.getStringValue());
                    break;
                default:
            }

        } catch (NullPointerException e) {

            Log.i(" Info ", "getCellAsString: NullPointerException: " + e.getMessage() );
        }
        return value;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class getDebtData extends AsyncTask<String, String, String>{
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... strings) {
            readExcelData();

            return null;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(debt_class.this, "Please wait...", "Load data", true);
            progressDialog.setCancelable(true);
        }
        @Override
        protected void onPostExecute(String s) {
            progressDialog.cancel();
            super.onPostExecute(s);
        }
    }

}