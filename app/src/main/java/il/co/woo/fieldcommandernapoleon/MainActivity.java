package il.co.woo.fieldcommandernapoleon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int[] mBattleRounds = {2,3,4,3,2,4,5,3,3,5,2,2,4,4};
    private MediaPlayer mDiceRollMediaPlayer;
    private MediaPlayer mSowrdClingMediaPlayer;
    private int mLastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton calculateButton = findViewById(R.id.d10);
        ImageButton fightButton = findViewById(R.id.fight);
        mDiceRollMediaPlayer = MediaPlayer.create(this,R.raw.dice_roll);
        mSowrdClingMediaPlayer = MediaPlayer.create(this,R.raw.swords_collide);

        final ImageButton resInfoImageButton = findViewById(R.id.res_info);
        final FontTextView rollResultTextView = findViewById(R.id.roll_result);
        final FontTextView battleRoundsTextView = findViewById(R.id.battle_rounds);
        final FontTextView newSuppPointsTextView = findViewById(R.id.new_enemy_supply_points);


        resInfoImageButton.setVisibility(View.INVISIBLE);

        rollResultTextView.setText("0");
        battleRoundsTextView.setText("0");
        newSuppPointsTextView.setText("0");

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

                mDiceRollMediaPlayer.start();
                int originalSupplyPoints = Integer.parseInt(supplyPointsStr);
                //roll a dice
                final int random = new Random().nextInt(10) + 1; // [0, 9] + 1 => [2, 10]
                int modifier = 0;
                if ((originalSupplyPoints >= 4) && (originalSupplyPoints <= 6))
                    modifier = 2;
                else if (originalSupplyPoints >= 7)
                    modifier = 4;

                mLastResult = random + modifier;

                rollResultTextView.setText(Integer.toString(mLastResult));
                battleRoundsTextView.setText(Integer.toString(mBattleRounds[mLastResult-1]));

                StringBuilder sb = new StringBuilder();
                Formatter fmt = new Formatter(sb);
                fmt.format(getResources().getString(R.string.event), mLastResult);
                String eventresName = sb.toString();
                int res = getResources().getIdentifier(eventresName,"string",getApplicationContext().getPackageName());

                FontTextView resEventTextView = findViewById(R.id.res_details);
                resEventTextView.setText(res);
                resInfoImageButton.setVisibility(View.VISIBLE);

                newSuppPointsTextView.setText(Integer.toString(originalSupplyPoints - modifier));
            }
        });


        fightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText frenchForceEditText = findViewById(R.id.french_force);
                EditText enemyForceEditText = findViewById(R.id.enemy_force);
                String frenchForceStr = frenchForceEditText.getText().toString();
                String enemyForceStr = enemyForceEditText.getText().toString();

                if ((frenchForceStr.length() == 0) || (enemyForceStr.length() == 0)){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.enter_force_size), Toast.LENGTH_SHORT).show();
                    return;
                }

                mSowrdClingMediaPlayer.start();
                int frenchForce = Integer.parseInt(frenchForceStr);
                int enemyForce = Integer.parseInt(enemyForceStr);
                if ((frenchForce == 0) || (enemyForce == 0)){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.force_size_0), Toast.LENGTH_SHORT).show();
                    return;
                }

                ImageView bg = findViewById(R.id.envelop_bg);
                bg.setAlpha(0.3f);

                FontTextView envelopmentTextView = findViewById(R.id.env_result);

                if (frenchForce >= enemyForce*3) {
                    envelopmentTextView.setText(R.string.french_envelop);
                    bg.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.french_flag));
                } else if (enemyForce >= frenchForce*3) {
                    envelopmentTextView.setText(R.string.enemy_envelop);
                    bg.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.brit_flag));
                } else {
                    envelopmentTextView.setText(R.string.no_envelop);
                    bg.setImageDrawable(null);
                }


            }
        });
    }

    @Override
    protected void onDestroy() {
        mDiceRollMediaPlayer.release();
        mSowrdClingMediaPlayer.release();
        super.onDestroy();
    }
}
