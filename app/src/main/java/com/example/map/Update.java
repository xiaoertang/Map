package com.example.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.map.entity.User;

import org.litepal.crud.DataSupport;

import java.util.List;

public class Update extends AppCompatActivity implements View.OnClickListener{
    private EditText oldPass;
    private EditText newPass;
    private Button ok;
    private Button cancel;
   // private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        init();
    }
    public void init(){

        oldPass = findViewById(R.id.old_pass);
        newPass = findViewById(R.id.new_pass);
        ok = findViewById(R.id.ok);
        cancel= findViewById(R.id.cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ok:
                List<User> users = DataSupport.select("password")
                        .where("status = ?","1")
                        .find(User.class);
                if(users == null){
                    Toast.makeText(Update.this,"未登录！",Toast.LENGTH_SHORT).show();
                }else{
                    for(User user1 : users){
                        if(user1.getPassword().equals(oldPass.getText().toString())){
                            //密码不能为空且长度小于16
                            if(newPass.getText().length()>10 || "".equals(newPass.getText().toString())){
                                Toast.makeText(Update.this,"密码不能为空且长度小于10！",Toast.LENGTH_SHORT).show();
                            }else{
                                User user = new User();
                                user.setPassword(newPass.getText().toString());
                                user.updateAll("status=?","1");
                                Intent intent1 = new Intent(Update.this,MyActivity.class);
                                startActivity(intent1);
                            }
                        }else{
                            Toast.makeText(Update.this,"原密码输入错误，请重新输入！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.cancel:
                Intent intent2 = new Intent(Update.this,MyActivity.class);
                startActivity(intent2);
                break;
        }
    }
}