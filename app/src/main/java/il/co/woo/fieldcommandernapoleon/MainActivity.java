package il.co.woo.fieldcommandernapoleon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //the battle rounds are hard coded by the game rules
    private static final int[] mBattleRounds = {2,3,4,3,2,4,5,3,3,5,2,2,4,4};
    private static final String TAG = "MainActivity";
    //media player for dice roll
    private MediaPlayer mDiceRollMediaPlayer;
    //media player for sword fight
    private MediaPlayer mSwordClingMediaPlayer;
    private int mLastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Enter");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton calculateButton = findViewById(R.id.d10_button);
        ImageButton fightButton = findViewById(R.id.fight_button);
        ImageButton ccButton = findViewById(R.id.cc_button);
        mDiceRollMediaPlayer = MediaPlayer.create(this,R.raw.dice_roll);
        mSwordClingMediaPlayer = MediaPlayer.create(this,R.raw.swords_collide);

        final ImageButton resInfoImageButton = findViewById(R.id.res_info_button);
        final FontTextView rollResultTextView = findViewById(R.id.roll_result);
        final FontTextView battleRoundsTextView = findViewById(R.id.battle_rounds);
        final FontTextView newSuppPointsTextView = findViewById(R.id.new_enemy_supply_points);

        //the more info button should only appear after the first dice roll
        resInfoImageButton.setVisibility(View.INVISIBLE);

        //zero all values as this is the first time
        rollResultTextView.setText("0");
        battleRoundsTextView.setText("0");
        newSuppPointsTextView.setText("0");

        //the result more info button was clicked
        resInfoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: more info button was clicked");
                //all the strings in the resource are numbered according to the roll result
                //so generate the right resource name and then load it
                StringBuilder sb = new StringBuilder();
                Formatter fmt = new Formatter(sb);
                fmt.format(getResources().getString(R.string.event_more), mLastResult);
                //the string resource name
                String eventResName = sb.toString();
                //get the resource id
                int resID = getResources().getIdentifier(eventResName,"string",getApplicationContext().getPackageName());

                //create an OK dialog box with the string
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,R.style.AlertDialogTheme));
                builder.setMessage(getResources().getString(resID))
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .show();

            }
        });

        //the roll button was clicked
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: roll dice button was cliked");
                //get and validate the supply points
                EditText supplyPointsEditText = findViewById(R.id.enemy_supply_pt);
                String supplyPointsStr = supplyPointsEditText.getText().toString();
                if (supplyPointsStr.length() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.enter_supply_points), Toast.LENGTH_SHORT).show();
                    return;
                }

                //play a dice roll
                mDiceRollMediaPlayer.start();

                int originalSupplyPoints = Integer.parseInt(supplyPointsStr);
                //roll a dice
                final int random = new Random().nextInt(10) + 1; // [0, 9] + 1 => [2, 10]
                Log.d(TAG, "onClick: Roll result is: " + random);
                int modifier = 0;
                //this is according to the game rules
                if ((originalSupplyPoints >= 4) && (originalSupplyPoints <= 6)) {
                    modifier = 2;
                } else if (originalSupplyPoints >= 7) {
                    modifier = 4;
                }

                Log.d(TAG, "onClick: Roll modifier is" + modifier);

                //update the result with the modifier
                mLastResult = random + modifier;

                //update the UI
                rollResultTextView.setText(Integer.toString(mLastResult));
                battleRoundsTextView.setText(Integer.toString(mBattleRounds[mLastResult-1]));
                newSuppPointsTextView.setText(Integer.toString(originalSupplyPoints - modifier));

                //all the strings in the resource are numbered according to the roll result
                //so generate the right resource name and then load it
                StringBuilder sb = new StringBuilder();
                Formatter fmt = new Formatter(sb);
                fmt.format(getResources().getString(R.string.event), mLastResult);
                String eventResName = sb.toString();
                int resID = getResources().getIdentifier(eventResName,"string",getApplicationContext().getPackageName());

                //update the view with the new string
                FontTextView resEventTextView = findViewById(R.id.res_details);
                resEventTextView.setText(resID);
                //show the more info button
                resInfoImageButton.setVisibility(View.VISIBLE);
            }
        });


        //the envelopment test was clicked
        fightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Envelopment check button was clicked");

                EditText frenchForceEditText = findViewById(R.id.french_force);
                EditText enemyForceEditText = findViewById(R.id.enemy_force);
                String frenchForceStr = frenchForceEditText.getText().toString();
                String enemyForceStr = enemyForceEditText.getText().toString();

                //check for valid values
                if ((frenchForceStr.length() == 0) || (enemyForceStr.length() == 0)){
                    Log.d(TAG, "onClick: text was not entered correctly");
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.enter_force_size), Toast.LENGTH_SHORT).show();
                    return;
                }
                //check again for no 0 values
                int frenchForce = Integer.parseInt(frenchForceStr);
                int enemyForce = Integer.parseInt(enemyForceStr);
                if ((frenchForce == 0) || (enemyForce == 0)){
                    Log.d(TAG, "onClick: 0 was entered as one of the fighting forces");
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.force_size_0), Toast.LENGTH_SHORT).show();
                    return;
                }

                //play a sword fight sound
                mSwordClingMediaPlayer.start();

                //prepare the background so the image will be seen but still the text will be readable
                ImageView bg = findViewById(R.id.envelop_bg);
                bg.setAlpha(0.3f);

                FontTextView envelopmentTextView = findViewById(R.id.env_result);

                //update the text view in accordance of the rules
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

        ccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String credits = getResources().getString(R.string.no_affiliation)
                        + "\n\n"
                        + getResources().getString(R.string.dice_icon_credit)
                        + "\n\n"
                        + getResources().getString(R.string.sound_credit);


                //create an OK dialog box with the string
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,R.style.AlertDialogTheme));
                builder.setMessage(credits)
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDiceRollMediaPlayer.release();
        mSwordClingMediaPlayer.release();
        super.onDestroy();
    }
}
