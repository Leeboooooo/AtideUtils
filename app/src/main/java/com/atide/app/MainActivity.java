package com.atide.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.atide.utils.ObjectUtils;

import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_main);

        main();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init(){
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToActity();
            }
        });
    }

    public void intentToActity(){
        Intent intent = new Intent();

        ComponentName cmp = new ComponentName("com.sina.weibo","com.sina.weibo.EditActivity");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        startActivityForResult(intent, 0);
    }

    public void intent2(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName comp = new ComponentName("com.mishangwo.activity",
                "com.mishangwo.activity.WelcomeActivity");
        intent.setComponent(comp);

        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

        intent.setFlags(launchFlags);

        intent.setAction("android.intent.action.VIEW");

        Bundle bundle = new Bundle();
        bundle.putString("from", "来自测试应用");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void main(){
        TestItem item = new TestItem("名称", "16", "college");

        ObjectUtils utils = new ObjectUtils();
        String[] filedNames = utils.getFiledName(item);
        for(String name:filedNames){
            System.out.println("属性名称---"+name);
        }

        Object[] values = utils.getFiledValues(item);
        for(Object value:values){
            System.out.println("属性值---"+value);
        }

        List<Map<String, String>> mapList = utils.getFiledsInfo(item);
        for(Map<String, String> map:mapList){
            for(String key:map.keySet()){
                System.out.println("key:"+key+", value:"+map.get(key));
            }
        }
    }

    private static class TestItem extends Object{
        public String name;
        public String year;
        public String school;
        public TestItem(String name, String year, String school){
            this.name = name;
            this.year = year;
            this.school = school;
        }
        public String getName() {
            return name;
        }
        public String getYear() {
            return year;
        }
        public String getSchool(){
            return school;
        }
    }
}
