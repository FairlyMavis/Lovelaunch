package mavis.demos.simplecastdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import mavis.demos.simplecastdemo.client.ClientActivity;
import mavis.demos.simplecastdemo.server.ServerActivity;
import mavis.demos.simplecastdemo.wifidirect.WiFiDirectSetupActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // 通过Intent跳转到ServerActivity
    public void mainOnClick(View view) {

        if (view.getId() == R.id.btn_server) {
            Intent intent = new Intent(this, ServerActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_client) {
            EditText editText = findViewById(R.id.et_ws_socket);
            String trim = editText.getText().toString().trim();
            if (trim.isEmpty()) {
                Toast.makeText(this, "需要输入链接", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ClientActivity.class);
            intent.putExtra("ws_address",trim);
            startActivity(intent);
        }
        if (view.getId() == R.id.btn_wifi_direct) {
            Intent intent = new Intent(this, WiFiDirectSetupActivity.class);
            startActivityForResult(intent,1001);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (data != null) {
                String address = data.getStringExtra("address");
                if (TextUtils.isEmpty(address)) {
                    return;
                }
                EditText editText = findViewById(R.id.et_ws_socket);
                editText.setText("ws://" + address + ":50000");
            }
        }
    }

}