package mx.univa.wear;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.widget.Button;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.wear.activity.ConfirmationActivity;
import android.widget.TextView;
import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Node;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends ConfirmationActivity{

    private TextView textView;
    Button talkButton;
    int receivedMessageNumber = 1;
    int sentMessageNumber = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView =  findViewById(R.id.text);
        talkButton =  findViewById(R.id.talkClick);


        talkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onClickMessage = "Enviar mensaje al dispositivo" + sentMessageNumber++;
                textView.setText(onClickMessage);

                String datapath = "/my_path";
                new SendMessage(datapath, onClickMessage).start();

            }
        });

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);

    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String onMessageReceived = "hola, recibí el mensaje" + receivedMessageNumber++;
            textView.setText(onMessageReceived);

        }
    }

    class SendMessage extends Thread {
        String path;
        String message;

        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

                List<Node> nodes = Tasks.await(nodeListTask);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {

                        Integer result = Tasks.await(sendMessageTask);

                    } catch (ExecutionException exception) {

                    } catch (InterruptedException exception) {

                    }

                }

            } catch (ExecutionException exception) {

//TO DO//

            } catch (InterruptedException exception) {

//TO DO//

            }
        }
    }
}