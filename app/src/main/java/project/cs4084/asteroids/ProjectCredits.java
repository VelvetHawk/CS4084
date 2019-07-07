package project.cs4084.asteroids;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProjectCredits extends AppCompatActivity {
    boolean status = false;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_credits);
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(!status) {
                    CreditsFragment c1 = new CreditsFragment();
                    fragmentTransaction.add(R.id.fragment_container, c1);
                    fragmentTransaction.commit();
                    button2.setText("Second member");
                    status = true;
                } else {
                    CreditsFragmentTwo c2 = new CreditsFragmentTwo();
                    fragmentTransaction.add(R.id.fragment_container, c2);
                    fragmentTransaction.commit();
                    button2.setText("First member");
                    status = false;
                }

            }
        });
    }
}
