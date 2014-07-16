package stejasvin.eaindia.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import stejasvin.eaindia.Objects.SkillChart;
import stejasvin.eaindia.Objects.Student;
import stejasvin.eaindia.R;
import stejasvin.eaindia.Utils.Utilities;
import stejasvin.eaindia.databases.SkillChartDatabaseHandler;
import stejasvin.eaindia.databases.StudentDatabaseHandler;

/**
 * Displays list of contacts from phone and search enabled
 *
 * @author sachin Nayak
 * @since v1.0
 */

class MapComparator implements Comparator<String> {

    public int compare(String first, String second) {
        // TODO: Null checking, both for maps and values
        return first.compareTo(second);
    }
}

public class AddSkillChart extends Activity {

    ArrayList<Student> selectedList = new ArrayList<Student>();
    HashMap<String,Student> studMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sc);

        // List to display the contacts
        final ListView names = (ListView) this.findViewById(R.id.list_add_sc_list);
        final TextView search = (TextView) this.findViewById(R.id.et_add_sc_stud_name);
        final TextView center = (TextView) this.findViewById(R.id.et_add_sc_cname);
        final TextView tutor = (TextView) this.findViewById(R.id.et_add_sc_tutor);
        Button bAdd = (Button)findViewById(R.id.b_add_sc_addtolist);
        Button bCreateSc = (Button)findViewById(R.id.b_add_sc_create_sc);

        //Adding favourities
        StudentDatabaseHandler studentDatabaseHandler = new StudentDatabaseHandler(AddSkillChart.this);
        studMap = studentDatabaseHandler.getAllStudentsMap();
        final ArrayList<Student> studList = studentDatabaseHandler.getAllStudents();
        final ArrayList<String> studNameList = new ArrayList<String>();

        for(int i=0;i<studList.size();i++)
            studNameList.add(studList.get(i).getName());

        // Sort the List using Map comparator
        Collections.sort(studNameList, new MapComparator());

        // Binding the list to a simple adapter and setting it to the
        // ListView
        final ArrayAdapter namesadapter = new ArrayAdapter(this,
                R.layout.single_list_item_skill_chart,studNameList);
        names.setAdapter(namesadapter);

        // Filtering the contacts according to the text entered
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                // When user changed the Text
                namesadapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // Do nothing
            }
        });

        names.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView,
                                    int myItemInt, long mylng) {
                addTvToScrollView(namesadapter.getItem(myItemInt).toString());
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(studNameList.contains(search.getText().toString()))
                    addTvToScrollView(search.getText().toString());
                else{
                    Toast.makeText(AddSkillChart.this,"Student by this name not found",Toast.LENGTH_SHORT).show();
                }

            }
        });

        bCreateSc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkillChartDatabaseHandler skillChartDatabaseHandler = new SkillChartDatabaseHandler(AddSkillChart.this);
                SkillChart skillChart = new SkillChart();
                skillChart.setCentreName(center.getText().toString());
                skillChart.setTutorName(tutor.getText().toString());
                skillChart.setDateOfCreation(Utilities.getDate(Utilities.getCurrentTime()));
                String studentIds="";
                for(Student s:selectedList){
                    studentIds+=s.getRoll()+":";
                }
                skillChart.setStudents(studentIds);
                skillChartDatabaseHandler.addSkillChart(skillChart);
                finish();
            }
        });

    }

    void addTvToScrollView(String name){
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll_add_sc);
        TextView tv = new TextView(AddSkillChart.this);
        TextView tv1 = (TextView)findViewById(R.id.et_add_sc_stud_name);
        tv.setText(name);
        ll.addView(tv);
        tv1.setText("");
        selectedList.add(studMap.get(name));
    }
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */
}