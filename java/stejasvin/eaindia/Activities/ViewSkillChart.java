package stejasvin.eaindia.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.ui.TwoWayGridView;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import stejasvin.eaindia.Adapters.CustomGridAdapter;
import stejasvin.eaindia.Adapters.StudentGridListAdapter;
import stejasvin.eaindia.Objects.Skill;
import stejasvin.eaindia.Objects.SkillChart;
import stejasvin.eaindia.Objects.Student;
import stejasvin.eaindia.R;
import stejasvin.eaindia.Utils.Constants;
import stejasvin.eaindia.Utils.Utilities;
import stejasvin.eaindia.databases.SkillChartDatabaseHandler;
import stejasvin.eaindia.databases.SkillDatabaseHandler;
import stejasvin.eaindia.databases.StudentDatabaseHandler;

class StComparator implements Comparator<Student> {

    public int compare(Student first, Student second) {
        // TODO: Null checking, both for maps and values
        return first.getName().compareTo(second.getName());
    }
}

class SkComparator implements Comparator<Skill> {

    public int compare(Skill first, Skill second) {
        // TODO: Null checking, both for maps and values
        return first.getName().compareTo(second.getName());
    }
}

public class ViewSkillChart extends ActionBarActivity {


    private static final int REQUEST_EX = 0;
    ArrayList<String> gridViewList = new ArrayList<String>();
    ArrayList<String> listViewList = new ArrayList<String>();
    StComparator stComparator = new StComparator();
    SkComparator skComparator = new SkComparator();
    int gridHeight = 0;
    ArrayList<Skill> skillset;
    ArrayList<Student> studentset = new ArrayList<Student>();
    ArrayList<HashMap<String, String>> mainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_skill_chart);

        Intent iThis = getIntent();
        //TODO connect this
        int scLid = iThis.getIntExtra("stejasvin.eaindia.LID", -1);
        if (scLid == -1) {
            finish();
            return;
        }

        SkillChartDatabaseHandler skillChartDatabaseHandler = new SkillChartDatabaseHandler(this);
        SkillDatabaseHandler skillDatabaseHandler = new SkillDatabaseHandler(this);
        StudentDatabaseHandler studentDatabaseHandler = new StudentDatabaseHandler(this);

        SkillChart skillChart = skillChartDatabaseHandler.getSkillChart(scLid);

        ((TextView) findViewById(R.id.tv_vs_centre_name)).setText("Centre - "+skillChart.getCentreName());
        ((TextView) findViewById(R.id.tv_vs_tutor)).setText("Tutor - "+skillChart.getTutorName());
        ((Button) findViewById(R.id.b_vs_export)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewSkillChart.this, ActivityImport.class);
                i.putExtra("stejasvin.CHOICE",1);
                startActivityForResult(i, REQUEST_EX);

            }
        });
        TwoWayGridView gridView = (TwoWayGridView) findViewById(R.id.tv_vs_gridview);
        ListView listView = (ListView) findViewById(R.id.list_vs);


        String[] studIds = skillChart.getStudents().split(":");
        if (studIds.length == 0) {
            finish();
            Toast.makeText(this, "No students found", Toast.LENGTH_LONG).show();
            return;
        }

        int ids[] = new int[studIds.length];

        for (int i = 0; i < studIds.length; i++) {
            try {
                ids[i] = Integer.decode(studIds[i]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (ids.length > 0) {
            skillset = skillDatabaseHandler.getAllSkill();
            //studentset = studentDatabaseHandler.getAllStudents();
            for (int i = 0; i < ids.length; i++) {
                studentset.add(studentDatabaseHandler.getStudent(ids[i]));
            }
            Collections.sort(studentset, stComparator);
            Collections.sort(skillset, skComparator);
            int noc = skillset.size();
            int nor = studentset.size() + 1;
            int x, y;
            JSONArray jsonArray;

            gridView.setNumColumns(noc);
            gridView.setNumRows(nor);

            //gridViewList.add("Names");
            listViewList.add("Names");

            mainList = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < studentset.size(); i++) {
                //gridViewList.add(studentset.get(i).getName());
                listViewList.add(studentset.get(i).getName());
                try {
                    mainList.add(Utilities.extractjson(new JSONArray(studentset.get(i).getSkills())));
                } catch (Exception e) {
                    e.printStackTrace();
                    HashMap<String, String> map = new HashMap<String, String>();
                    mainList.add(map);
                }
            }

//            ArrayList<ArrayList<SkillJson>> mainList = new ArrayList<ArrayList<SkillJson>>();
//            for (int i = 0; i < studentset.size(); i++) {
//                //gridViewList.add(studentset.get(i).getName());
//                listViewList.add(studentset.get(i).getName());
//                try {
//                    mainList.add(Utilities.extractjson(new JSONArray(studentset.get(i).getSkills())));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    ArrayList<SkillJson> arrayList = new ArrayList<SkillJson>();
//                    mainList.add(arrayList);
//                }
//            }


            int stpos, skpos, flag = 0;
//            for (x = 0; x < skillset.size(); x++) {
//                gridViewList.add(skillset.get(x).getName());
//                for (y = 0; y < studentset.size(); y++) {
//                    stpos = y % nor - 1;
//                    skpos = y / nor;
//
//                    flag = 0;
//                    for (SkillJson s : mainList.get(y)) {
//                        if (s.getId().equals(skillset.get(x).getLid())) {
//                            gridViewList.add(s.getDate());
//                            flag = 1;
//                            break;
//                        }
//                    }
//                    if (flag == 0) {
//                        gridViewList.add("-");
//                    }
//
//                }
//            }

            gridHeight = (studentset.size() + 1) * 100;
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_grid);
            LinearLayout.LayoutParams layoutParamsGrid = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, gridHeight);

            linearLayout.setLayoutParams(layoutParamsGrid);

//            TextView tvListGrid = (TextView)findViewById(R.id.single_list_item_std_grid_name_list);
//            tvListGrid.setLayoutParams(layoutParamsList);

            CustomGridAdapter customGridAdapter = new CustomGridAdapter(this, studentset, skillset, mainList);
            gridView.setAdapter(customGridAdapter);
            listView.setAdapter(new StudentGridListAdapter(this, listViewList));


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EX) {
                String s = data.getStringExtra(ActivityImport.returnDirectoryParameter);
                boolean suc = false;
                if (s != null && !s.equals("")) {
                    suc = saveSkillChartExcelFile(ViewSkillChart.this, s, studentset, skillset, mainList);
                }
                if (!suc) {
                    Toast.makeText(ViewSkillChart.this, "Error in exporting,invalid dir", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_skill_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            StudentDatabaseHandler studentDatabaseHandler = new StudentDatabaseHandler(this);
            for (int i = 0; i < studentset.size(); i++) {
                Student student = studentset.get(i);
                JSONArray jsonArray = new JSONArray();
                for (int j = 0; j < skillset.size(); j++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate(SkillDatabaseHandler.KEY_LOCAL_ID, skillset.get(j).getLid());
                    if (mainList.get(i) != null)
                        jsonObject.accumulate(SkillDatabaseHandler.KEY_CREATION_DATE, mainList.get(i).get(skillset.get(j).getLid() + ""));
                    else
                        jsonObject.accumulate(SkillDatabaseHandler.KEY_CREATION_DATE, "");
                    jsonArray.put(j, jsonObject);
                }
                student.setSkills(jsonArray.toString());
                studentDatabaseHandler.updateStudent(student);

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private static boolean saveSkillChartExcelFile(Context context, String filePath, ArrayList<Student> studentset, ArrayList<Skill> skillset, ArrayList<HashMap<String, String>> mainList) {

//        // check if available and not read only
//        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//            Log.e(TAG, "Storage not available or read only");
//            return false;
//        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet(Utilities.getDate(Utilities.getCurrentTime()).replace("/", "_"));

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue(Constants.NAME);
        c.setCellStyle(cs);

        int rowCounter = 1, cellCounter = 0;
        c.setCellValue("Names");
        cellCounter++;

        for (Skill s : skillset) {
            c = row.createCell(cellCounter++);
            c.setCellValue(s.getName());
        }

        for (int i = 0; i < studentset.size(); i++) {
            cellCounter = 0;
            row = sheet1.createRow(rowCounter);
            c = row.createCell(cellCounter++);
            c.setCellValue(studentset.get(i).getName());

            for (Skill s : skillset) {
                c = row.createCell(cellCounter++);
                String s1 = mainList.get(i).get(s.getLid() + "");
                if (s1 != null)
                    c.setCellValue(s1);
                else
                    c.setCellValue("");
            }
            rowCounter++;
        }

//        sheet1.setColumnWidth(0, (15 * 500));
//        sheet1.setColumnWidth(1, (15 * 500));
//        sheet1.setColumnWidth(2, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File file = new File(filePath, "SkillChart" + Utilities.getDate(Utilities.getCurrentTime()).replace("/", "_") + ".xls");
        //File file1 = new File("/storage/sdcard1", "test.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
            Toast.makeText(context, "Data successfully exported to " + file, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e("TestPOI", Environment.getExternalStorageState());
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();

            } catch (Exception ex) {
            }
        }
        return success;
    }

}
