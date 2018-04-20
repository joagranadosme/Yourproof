package com.ssm.usuario.yourproof;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class listaActivity extends Activity {

    private static final int REQUEST_PATH = 1;
    String curFileName;
    String curPathName;
    EditText edittext;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        edittext = (EditText)findViewById(R.id.editText);
    }

    public void getfile(View view){
        Intent intent1 = new Intent(this, itemLista.class);
        startActivityForResult(intent1,REQUEST_PATH);
    }
    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        if (requestCode == REQUEST_PATH){
            if (resultCode == RESULT_OK) {
                curPathName = data.getStringExtra("GetPath");

                Toast.makeText(getApplicationContext(),curPathName.toString(), Toast.LENGTH_LONG).show();

                curFileName = data.getStringExtra("GetFileName");
                edittext.setText(curFileName);

                imageView = (ImageView) findViewById(R.id.imageView);

                try{
                    Bitmap bitmap = BitmapFactory.decodeFile(curPathName);
                    imageView.setImageBitmap(bitmap);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
                }


            }
        }
    }
}

