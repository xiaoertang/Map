package com.example.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class register extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener{

    private Button register;
    private EditText userName;
    private EditText phone;
    private EditText password;
    private EditText password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Connector.getDatabase();//创建数据数据库
        initView();
    }

    public void initView() {
        register = findViewById(R.id.regist);
        userName = findViewById(R.id.edit_userName);
        phone = findViewById(R.id.edit_phone);
        password = findViewById(R.id.edit_password);
        password2 = findViewById(R.id.edit_password2);

        //点击监听事件
        register.setOnClickListener(this);
        //失去焦点
        userName.setOnFocusChangeListener(this);
        phone.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        password2.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.regist:
                User user = new User();
                user.setName(userName.getText().toString());
                user.setPhone(phone.getText().toString());
                user.setPassword(password.getText().toString());
                user.setPassword2(password2.getText().toString());
                user.save();
                Intent toCustomerMenu = new Intent(register.this,CustomerMenu.class);
                startActivity(toCustomerMenu);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()){
            case R.id.edit_userName:
                if(!b){
                    //用户名长度不能为空且不大于9
                    if(userName.getText().length() < 9 || "".equals(userName.getText().toString())){
                        Toast.makeText(register.this,"用户名不能为空且长度小于9！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    userName.setText("");
                }
                break;
            case R.id.edit_phone:
                if(!b){
                    //手机号不能为空且长度小于12
                    if(phone.getText().length()!=11 || "".equals(phone.getText().toString())){
                        Toast.makeText(register.this,"手机号不能为空且长度小于12！",Toast.LENGTH_SHORT).show();
                    }
                    //如果该手机号被注册，则不能注册
                    List<User> users = DataSupport.select("phone")
                            .where("phone == ?",phone.getText().toString())
                            .find(User.class);
                    if(users != null){
                        Toast.makeText(register.this,"该手机号已被注册！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    phone.setText("");
                }
                break;
            case R.id.edit_password:
                if(!b){
                    //密码不能为空且长度小于16
                    if(password.getText().length()>10 || "".equals(password.getText().toString())){
                        Toast.makeText(register.this,"密码不能为空且长度小于10！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    password.setText("");
                }
                break;
            case R.id.edit_password2:
                if(!b){
                    if(!(password.getText().toString()).equals(password2.getText().toString()) ||"".equals(password2.getText().toString())){
                        Toast.makeText(register.this,"两次输入的密码不同，请重新输！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    password2.setText("");
                }
                break;
            default:
                break;
        }
    }

}