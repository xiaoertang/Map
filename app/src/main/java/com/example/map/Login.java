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

public class Login extends AppCompatActivity implements View.OnClickListener{
    private Button btn_register;
    private Button btn_login;
    private EditText edit_login_phone;
    private EditText edit_login_password;
    private Button my_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

    }

    public void initView() {
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        edit_login_phone = findViewById(R.id.edit_login_phone);
        edit_login_password = findViewById(R.id.edit_login_password);
        my_login = findViewById(R.id.my_login);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if("".equals(edit_login_phone.getText().toString())){
                    Toast.makeText(Login.this, "手机号未输入", Toast.LENGTH_SHORT).show();
                }else{
                    //查询
                    List<User> users = DataSupport.select("name", "phone", "password")
                            .where("phone = ?", edit_login_phone.getText().toString())
                            .find(User.class);
                    if(users.size() == 0){
                        Toast.makeText(Login.this, "该手机号未注册", Toast.LENGTH_SHORT).show();
                    }else{
                        User user = new User();
                        user.setStatus("1");
                        user.updateAll("status = ?","0");
                        for (User user1 : users) {
                            if (user1.getPassword().equals(edit_login_password.getText().toString())) {
                                my_login.setText(user1.getName());
                                Toast.makeText(Login.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(Login.this, MainActivity.class);
                                startActivity(login);
                                break;
                            }else {
                                Toast.makeText(Login.this, "手机号或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                break;
            case R.id.btn_register:
                Intent register = new Intent(Login.this, Register.class);
                startActivity(register);
                break;
        }
    }

//    @Override
//    public void onFocusChange(View view, boolean b) {
//        switch (view.getId()){
//            case R.id.edit_login_phone:
//
//                break;
//        }
//    }
}