package com.intern.ankan.mqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    //Connect Button
    private Button connect;
    //Disonnect Button
    private Button disconnect;
    //UI Status
    private TextView messages;
    private String clientId;
    private MqttAndroidClient client;
    private MqttConnectOptions options;
    //Subscribe to a particular event
    private Button subscribe;
    private EditText publish;
    private Button publishBtn;
    private EditText topic;
    private static final String TAG =" MQTT_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = (Button)findViewById(R.id.connect);
        disconnect = (Button)findViewById(R.id.disconnect);
        topic = (EditText)findViewById(R.id.subEdit);
        subscribe = (Button)findViewById(R.id.sub_button);
        publish = (EditText)findViewById(R.id.pub);
        publishBtn = (Button)findViewById(R.id.pub_button);


        messages = (TextView)findViewById(R.id.status);
       // clientId = MqttClient.generateClientId();
        clientId = "AnkanHalder";

       // options = new MqttConnectOptions();
       // options.setUserName("gvhwtipw");
      //  options.setPassword("bxg0FMlYpSgO".toCharArray());




        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectBroker();
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectBroker();
            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String choice = topic.getText().toString();

                if(TextUtils.isEmpty(choice.trim()))
                {
                    Toast.makeText(MainActivity.this, "Topic can't be Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    subscribe(choice.trim());
                }
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = publish.getText().toString();
                if(TextUtils.isEmpty(type.trim()))
                {
                    Toast.makeText(MainActivity.this, "Publish Topic can't be Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    publish(type.trim());
                }

            }
        });


    }


    //Function Connect
   private void connectBroker(){

        try {
            client = new MqttAndroidClient(this.getApplicationContext(), "tcp://test.mosquitto.org:1883",
                    clientId);
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    messages.setText("Connected");

                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) {
                            messages.setText(new String(message.getPayload()));
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "Failed "+ exception.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG,exception.getMessage());
                    exception.printStackTrace();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }



    }

    private void publish(String topic){

        String message = topic+":-> New Message";
        try {
            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    //Function Disconnect
    private void disconnectBroker(){

        if(client != null)
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    messages.setText("Disconnected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Function Subscribe
    private void subscribe(final String topic){

        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Toast.makeText(MainActivity.this, "Subscribed to "+topic, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
