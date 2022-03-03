package com.example.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.map.entity.User;
import com.example.map.offine.Offline;

import org.litepal.crud.DataSupport;

import java.util.List;


public class MyActivity extends AppCompatActivity implements View.OnClickListener {
    private Button offine; //离线地图
    private Button favorites;
    private Button updatePass; //修改密码
    private Button quit; //退出
    private Button del;//注销
    //private User user = new User();
    private TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        init();
    }

    public void init() {
        name = findViewById(R.id.name);
        List<User> users = DataSupport.select("name")
                .where("status == ?","1")
                .find(User.class);
        if(users != null){
            //user = users.get(0);
            name.setText(users.get(0).getName());
        }
        favorites = findViewById(R.id.favorites);
        offine = findViewById(R.id.offline);
        updatePass = findViewById(R.id.update_pass);
        quit = findViewById(R.id.quit);
        del = findViewById(R.id.del);
        favorites.setOnClickListener(this);
        offine.setOnClickListener(this);
        updatePass.setOnClickListener(this);
        quit.setOnClickListener(this);
        del.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        List<User> users = DataSupport.select("status")
                .where("status = ?", "1")
                .find(User.class);
//        for(User user0 : users){
//            if(user0 != null) {
//                name.setText(user0.getName());
//            }
//        }
        switch (view.getId()) {
            case R.id.update_pass:
                Intent intent = new Intent(MyActivity.this, Update.class);
                startActivity(intent);
                break;
            case R.id.quit:
                for(User user1 : users){
                        User user = new User();
                        user.setStatus("0");
                        user.updateAll("status = ?", "1");
                }
                   Intent toMain = new Intent(MyActivity.this,MainActivity.class);
                startActivity(toMain);
                break;
            case R.id.offline://离线下载
                Intent intent1 = new Intent(MyActivity.this, Offline.class);
                startActivity(intent1);
                break;
            case R.id.del:
                DataSupport.deleteAll(User.class, "status = ?", "1");
                break;
            case R.id.favorites:
                Intent favorite = new Intent(MyActivity.this, Favorite.class);
                startActivity(favorite);
                break;
        }
    }
}