package dulop.tappydefender;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);

        buttonPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Create a new Intent object
        Intent i = new Intent(this, GameActivity.class);
        // Start GameActivity class vie the Intent
        startActivity(i);
        // Shut activity down
        finish();
    }
}
