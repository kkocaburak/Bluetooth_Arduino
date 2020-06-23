package com.burakkarakoca.bluetooth_arduino;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class RobotControl extends AppCompatActivity {

    Button btnIleri, btnGeri, btnSol, btnSag;
    TextView lumn;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_control);

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);

        btnIleri = findViewById(R.id.robot_control_buton_ileri);
        btnSol = findViewById(R.id.robot_control_buton_sol);
        btnSag = findViewById(R.id.robot_control_buton_sag);
        btnGeri = findViewById(R.id.robot_control_buton_geri);

        new ConnectBT().execute();

    }

    public void butonClick(View v){

        if(v == findViewById(R.id.robot_control_buton_ileri)){
            // ileri
            try
            {
                btSocket.getOutputStream().write("A".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }

        } else if(v == findViewById(R.id.robot_control_buton_sol)){
            // sol
            try
            {
                btSocket.getOutputStream().write("D".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }

        } else if(v == findViewById(R.id.robot_control_buton_sag)){
            // sag
            try
            {
                btSocket.getOutputStream().write("S".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }

        } else if(v == findViewById(R.id.robot_control_buton_geri)){
            // geri
            try
            {
                btSocket.getOutputStream().write("G".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }

        }

    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            {
                System.out.println("Error");}
        }

        finish(); //return to the first layout

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(RobotControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }




}
