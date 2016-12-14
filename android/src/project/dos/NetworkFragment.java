package project.dos;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class NetworkFragment extends Fragment {

    private static TextView textView;

    EventsListener<Integer> fragmentChanger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_network, null);

        textView = (TextView) v.findViewById(R.id.textView);
        Button clientButton = (Button) v.findViewById(R.id.clientButton);
        Button hostButton= (Button) v.findViewById(R.id.hostButton);
        Button startButton = (Button) v.findViewById(R.id.startButton);

        clientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((GameActivity) getActivity()).networkController.startDiscovery();
            }
        });
        hostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((GameActivity) getActivity()).networkController.startAdvertising();
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((GameActivity) getActivity()).networkController.stopDiscovery();
                ((GameActivity) getActivity()).networkController.stopAdvertising();
                fragmentChanger.listenEvent(0, 1);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {//context is an instance of FragmentActivity
        super.onAttach(context);
        try {
            fragmentChanger = (EventsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EventsListener");
        }
    }

    public static void showText(String message) {
        if (textView != null) {
            textView.setText(message);
        }
    }
}
