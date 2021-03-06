/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private final String TAG="a";
    private Stack<LetterTile> placedTiles = new Stack<LetterTile>();
    //private Button undo;
    //testing


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                if (word.length()==WORD_LENGTH) {
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        //word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
       // word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
        /*undo = (Button) findViewById(R.id.button);
        undo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG,"click undo");
                onUndo(view);
            }
        });*/
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(TAG,"in onTouch");
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                //placedTiles.push(tile);
                //Log.d(TAG,"pushed to placedTiles");
                //return tile.onTouchEvent(event);
                return true;
            }


            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }
                    LinearLayout parent = (LinearLayout) tile.getParent();
                    LinearLayout newParent = (LinearLayout) v;
                    parent.removeView(tile);
                    newParent.addView(tile);
                    placedTiles.push(tile);
                    Log.d(TAG,"pushed to placedTiles");
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");
        word1 = words.get(random.nextInt(words.size()));
        word2 = words.get(random.nextInt(words.size()));
        Log.d(TAG,"word1: "+word1);
        Log.d(TAG,"word2: "+word2);
        char[] word1Array = word1.toCharArray();
        char[] word2Array = word2.toCharArray();
        int counter1 = 0;
        int counter2=0;
        int pickWord = random.nextInt(2);
        String scrambled="";
        Log.d(TAG,"scrambled: "+scrambled);
        while (counter1<word1Array.length ||counter2<word2Array.length){
            Log.d(TAG,"pickWord: "+pickWord);
            if (pickWord==0){
                if (counter1<word1Array.length){
                    Log.d(TAG,"counter1: "+counter1);
                    scrambled += word1Array[counter1];
                    counter1++;
                }

            } else {
               if (counter2<word2Array.length) {
                    Log.d(TAG,"counter2: "+counter2);
                    scrambled += word2Array[counter2];
                    counter2++;
                }
            }


            pickWord = random.nextInt(2);

        }


        Log.d(TAG,"scrambled: "+scrambled);
        //messageBox.setText(scrambled);
        char[] scrambledArray = scrambled.toCharArray();
        Context context = getApplicationContext();
        for (int i=scrambledArray.length-1;i>=0;i--){
           stackedLayout.push(new LetterTile(context, scrambledArray[i]));
        }

        return true;
    }

    public boolean onUndo(View view) {
        Log.d(TAG,"in onUndo");
        if (!placedTiles.isEmpty()) {
            LetterTile recent = placedTiles.pop();
            recent.moveToViewGroup(stackedLayout);
            Log.d(TAG,"move to view group");
        }
        return true;
    }
}
