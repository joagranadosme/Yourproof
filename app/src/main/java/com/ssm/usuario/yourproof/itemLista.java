package com.ssm.usuario.yourproof;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class itemLista extends ListActivity {

    private String parentDir;
    private File currentDir;
    private FileArrayAdapter adapter;
    private String[] exten = {"jpg", "png", "gif","jpeg"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //File root = Environment.getRootDirectory();
        File root = Environment.getExternalStorageDirectory();
        //currentDir = new File("/sdcard/");
        currentDir = root;
        parentDir=currentDir.getParent();
        fill(currentDir);
    }

    private void fill(File f){
        File[]dirs = f.listFiles();
        this.setTitle("Current Dir: "+f.getName());
        List<Item> dir = new ArrayList<Item>();
        List<Item> fls = new ArrayList<Item>();
        try{
            for(File ff: dirs){
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);

                if(ff.isDirectory()){
                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formated = lastModDate.toString();
                    dir.add(new Item(ff.getName(),num_item,date_modify, ff.getAbsolutePath(),"directory_icon"));
                }
                else{
                    fls.add(new Item(ff.getName(),ff.length() + " Bytes", date_modify, ff.getAbsolutePath(),"file_icon"));
                }
            }
        }catch(Exception e){

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0,new Item("..",parentDir,"",f.getParent(),"directory_up"));
        adapter = new FileArrayAdapter(itemLista.this,R.layout.activity_item,dir);
        this.setListAdapter((ListAdapter) adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Item o = adapter.getItem(position);
        if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
            currentDir = new File(o.getPath());
            parentDir = o.getPath();
            fill(currentDir);
        }
        else{
            onFileClick(o);
        }
    }

    private void onFileClick(Item o){
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("GetPath",o.getPath());
        intent.putExtra("GetFileName",o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}