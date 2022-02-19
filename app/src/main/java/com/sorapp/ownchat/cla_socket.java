package com.sorapp.ownchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class cla_socket
{

    //Global Variabls
    BluetoothAdapter blu_adaptor;
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static ArrayList<String> Messages=new ArrayList<>();
    public static String Device_Name;

    public static ArrayList<BluetoothSocket> Server_Connection_Sockets=new ArrayList<>();

    //Constractor Start
    public cla_socket(String Name)
    {
        try
        {
            init_Client(Name);
        }
        catch (Exception E)
        {
            Log.i("Err",E.getMessage());
        }

    }

    public cla_socket()
    {
        try
        {
            init_Server();
        }
        catch (Exception E)
        {
            Log.i("Err",E.getMessage());
        }

    }
    //Constractor End



    //Get Initialize Server Start
    private void init_Server() throws Exception
    {
        BluetoothServerSocket blu_server_socet=blu_adaptor.listenUsingRfcommWithServiceRecord("Rezafta",MY_UUID_SECURE);

        while(true)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Log.i("Errt","Lissen");
                        BluetoothSocket blu_socket = blu_server_socet.accept();
                        Server_Connection_Sockets.add(blu_socket);
                        Log.i("Errt","Connect");
                        DataInputStream data_input=new DataInputStream(blu_socket.getInputStream());
                        DataOutputStream data_output=new DataOutputStream(blu_socket.getOutputStream());

                        Timer t1=new Timer();
                        t1.schedule(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                for (int i = 0; i < Server_Connection_Sockets.size(); i++)
                                {
                                    try
                                    {
                                        DataOutputStream user_out_stream = new DataOutputStream(Server_Connection_Sockets.get(i).getOutputStream());
                                        user_out_stream.writeUTF(Get_Create_Json_From_ArrayList());
                                    }
                                    catch (Exception E)
                                    {

                                    }
                                }
                            }
                        },1000);

                        Timer t2=new Timer();
                        t2.schedule(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    String input = data_input.readUTF();
                                    JSONArray jarray = new JSONArray(input);

//                                    ac_main.Chats_lst.clear();
                                    for (int i = 0; i < jarray.length(); i++)
                                    {
                                        JSONObject jobjct = new JSONObject(jarray.get(i).toString());
                                        ArrayList<String> chat_data = new ArrayList<>();

                                        chat_data.add(jobjct.get("id").toString());
                                        chat_data.add(jobjct.get("content").toString());
                                        chat_data.add(jobjct.get("date").toString());
                                        chat_data.add(jobjct.get("owner").toString());

                                        ac_main.Chats_lst.add(chat_data);
                                    }

                                }
                                catch (Exception E)
                                {

                                }
                            }
                        },1000);

                    }
                    catch (Exception E)
                    {

                    }
                }
            });
        }
    }
    //Get Initialize Server End




    //Get Create Json From ArrayList Function Start
    public String Get_Create_Json_From_ArrayList() throws Exception
    {
        JSONArray jarray=new JSONArray();

        for(int i=0;i<ac_main.Chats_lst.size();i++)
        {
            ArrayList<String> data=ac_main.Chats_lst.get(i);
            JSONObject jObject=new JSONObject();
            jObject.put("id",data.get(0));
            jObject.put("content",data.get(1));
            jObject.put("date",data.get(2));
            jObject.put("owner",data.get(3));
            jarray.put(jObject);
        }

        return jarray.toString();
    }
    //Get Create Json From ArrayList Function End




    //Get Initialize Client Start
    private void init_Client(String Name) throws Exception
    {
        Device_Name=Name;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    BluetoothSocket blu_socket;
                    BluetoothDevice device = null;
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                    for (BluetoothDevice div : btAdapter.getBondedDevices())
                    {
                        if (div.getName().equals(Name))
                        {
                            device = div;
                            break;
                        }
                    }

                    blu_socket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                    DataInputStream data_input = new DataInputStream(blu_socket.getInputStream());
                    DataOutputStream data_output = new DataOutputStream(blu_socket.getOutputStream());


                    Timer t1=new Timer();
                    t1.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                data_output.writeUTF(Get_Create_Json_From_ArrayList());
                            }
                            catch (Exception E)
                            {

                            }
                        }
                    },1000);

                    Timer t2=new Timer();
                    t2.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                String input = data_input.readUTF();
                                JSONArray jarray = new JSONArray(input);

//                                ac_main.Chats_lst.clear();
                                for (int i = 0; i < jarray.length(); i++)
                                {
                                    JSONObject jobjct = new JSONObject(jarray.get(i).toString());
                                    ArrayList<String> chat_data = new ArrayList<>();

                                    chat_data.add(jobjct.get("id").toString());
                                    chat_data.add(jobjct.get("content").toString());
                                    chat_data.add(jobjct.get("data").toString());
                                    chat_data.add(jobjct.get("owner").toString());

                                    ac_main.Chats_lst.add(chat_data);
                                }
                            }
                            catch (Exception E)
                            {

                            }
                        }
                    },1000);

                }
                catch (Exception E)
                {

                }

            }
        }).start();


    }
    //Get Initialize Client End


}
