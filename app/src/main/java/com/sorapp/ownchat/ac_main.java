package com.sorapp.ownchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class ac_main extends AppCompatActivity
{

    //Global Variabls Start
    public static ArrayList<ArrayList<String>> Chats_lst=new ArrayList<>();

    cla_socket cls_main;
    String Bluetoothname="";

    Chat_Adaptor chat_adaptor;





    //Main Function Start
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        try
        {
            Bluetoothname=getLocalBluetoothName();
            GetDisplayStatus();
            GetComponents();
        }
        catch (Exception E)
        {
            Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    //Main Function End




    //Get List Of Bluetooth And Status Start
    private void GetDisplayStatus() throws Exception
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        View dialog_layout=getLayoutInflater().inflate(R.layout.ly_options,null);
        dialog.setView(dialog_layout);

        ArrayList<String> Device=new ArrayList<>();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        for (BluetoothDevice bt_dev:btAdapter.getBondedDevices())
        {
            Device.add(bt_dev.getName());
        }

        Spinner divices=(Spinner) dialog_layout.findViewById(R.id.ly_options_spiner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Device);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divices.setAdapter(adapter);

        dialog.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                if(((RadioButton)dialog_layout.findViewById(R.id.server_radio)).isChecked())
                {
                    cls_main=new cla_socket();
                }
                else
                {
                    cls_main=new cla_socket(divices.getSelectedItem().toString());
                }

            }
        });

        dialog.show();

    }
    //Get List Of Bluetooth And Status End






    //Get Components Start
    private void GetComponents() throws Exception
    {
        ListView chat_listview=(ListView) findViewById(R.id.chat_listview);
        chat_listview.setDividerHeight(0);
        chat_listview.setDivider(null);
        chat_adaptor =new Chat_Adaptor();

        chat_listview.setAdapter(chat_adaptor);

        Timer t=new Timer();
        t.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                chat_adaptor.notifyDataSetChanged();
            }
        },1000);
    }
    //Get Components End





    //On Click Send Chat Button Start
    public void GetOnClick_Send_Message(View v)
    {
        EditText chat=(EditText) findViewById(R.id.ac_main_edit_text_chat_message);

        Date currentTime = Calendar.getInstance().getTime();

        ArrayList<String> chatMessage=new ArrayList<>();
        chatMessage.add("0");
        chatMessage.add(chat.getText().toString());
        chatMessage.add(currentTime.toString());
        chatMessage.add(Bluetoothname);
        Chats_lst.add(chatMessage);

//        Toast.makeText(getApplicationContext(), Chats_lst.size()+"", Toast.LENGTH_SHORT).show();

        chat.setText("");

    }
    //On Click Send Chat Button End




    //Get Phone Bluetooth name Start
    public String getLocalBluetoothName()
    {
        BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        String name = mBluetoothAdapter.getName();
        if(name == null)
        {
            System.out.println("Name is null!");
            name = mBluetoothAdapter.getAddress();
        }
        return name;
    }
    //Get Phone Bluetooth name End




    //On Back Press Event STart
    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent();
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }
    //On Back Press Event End





    //Chat List Adaptor Start
    class Chat_Adaptor extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return Chats_lst.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public boolean isEnabled(int position)
        {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ArrayList<String> chat=Chats_lst.get(position);
            View view;

            Toast.makeText(getApplicationContext(), Bluetoothname.toUpperCase(), Toast.LENGTH_SHORT).show();

            if(chat.get(3).toUpperCase().equals(Bluetoothname.toUpperCase()))
            {
                view = getLayoutInflater().inflate(R.layout.ly_chat_my_user, null);
                TextView text = (TextView) view.findViewById(R.id.chat_text);
                text.setText(chat.get(1));
            }
            else
            {
                view = getLayoutInflater().inflate(R.layout.ly_chat_user_user, null);
                TextView text = (TextView) view.findViewById(R.id.chat_text);
                text.setText(chat.get(1));
            }


            return view;
        }
    }
    //Chat List Adaptor End


}