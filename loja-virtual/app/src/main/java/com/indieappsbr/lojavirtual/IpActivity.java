package com.indieappsbr.lojavirtual;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_ip)
public class IpActivity extends AppCompatActivity
{
    @ViewById(R.id.edtIp)
    EditText edtIp;

    @ViewById(R.id.btOk)
    Button btOk;

    @Click(R.id.btOk)
    void clicou()
    {
        if(edtIp.getText().toString().isEmpty())
            return;

        MyApplication.pegarCliente().setarIp(edtIp.getText().toString());

        //Inicia a thread
        if(MyApplication.pegarCliente() != null)
            MyApplication.pegarCliente().start();

        LoginActivity_.intent(this).start();
        finish();
    }
}
