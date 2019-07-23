package il.co.woo.fieldcommandernapoleon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int[] mBattleRounds = {2,3,4,3,2,4,5,3,3,5,2,2,4,4};
    private MediaPlayer mMediaPlayer;
    private int mLastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton calculateButton = findViewById(R.id.d10);
        mMediaPlayer = MediaPlayer.create(this,R.raw.dice_roll);

        final ImageButton resInfoImageButton = findViewById(R.id.res_info);
        resInfoImageButton.setVisibility(View.INVISIBLE);

        resInfoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                Formatter fmt = new Formatter(sb);
                fmt.format(getResources().getString(R.string.event_more), mLastResult);
                String eventResName = sb.toString();
                int res = getResources().getIdentifier(eventResName,"string",getApplicationContext().getPackageName());



                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,R.style.AlertDialogTheme));
                builder.setMessage(getResources().getString(res))
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .show();

            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText supplyPointsEditText = findViewById(R.id.enemy_supply_pt);
                String supplyPointsStr = supplyPointsEditText.getText().toString();
                if (supplyPointsStr.length() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.enter_supply_points), Toast.LENGTH_SHORT).show();
                    return;
                }

                mMediaPlayer.start();
                int originalSupplyPoints = Integer.parseInt(supplyPointsStr);
                //roll a dice
                final int random = new Random().nextInt(10) + 1; // [0, 9] + 1 => [2, 10]
                int modifier = 0;
                if ((originalSupplyPoints >= 4) && (originalSupplyPoints <= 6))
                    modifier = 2;
                else if (originalSupplyPoints >= 7)
                    modifier = 4;

                mLastResult = random + modifier;

                StringBuilder sb = new StringBuilder();
                Formatter fmt = new Formatter(sb);
                String result = getResources().getString(R.string.result);
                fmt.format(result, mLastResult,mBattleRounds[mLastResult-1]);
                String resString = sb.toString();

                FontTextView resultTextView = findViewById(R.id.result);
                resultTextView.setText(resString);

                sb.setLength(0);
                fmt.format(getResources().getString(R.string.event), mLastResult);
                String eventresName = sb.toString();
                int res = getResources().getIdentifier(eventresName,"string",getApplicationContext().getPackageName());

                FontTextView resEventTextView = findViewById(R.id.res_details);
                resEventTextView.setText(res);
                resInfoImageButton.setVisibility(View.VISIBLE);

                FontTextView newSupplyPointsTextView = findViewById(R.id.new_supply_points);

                if (modifier > 0) {
                    sb.setLength(0);
                    fmt.format(getResources().getString(R.string.new_supp_points), originalSupplyPoints - modifier);
                    String newSupplyPointsStr = sb.toString();
                    newSupplyPointsTextView.setText(newSupplyPointsStr);
                } else {
                    newSupplyPointsTextView.setText(R.string.supply_points_unchanged);
                }



            }
        });
    }

    @Override
    protected void onDestroy() {
        mMediaPlayer.release();
        super.onDestroy();
    }
}
