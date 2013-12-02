package org.ejmc.android.simplechat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Vibrator;
import org.ejmc.android.simplechat.model.*;

import android.app.ListActivity;
import android.os.Bundle;
//import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.ejmc.android.simplechat.net.JSONparser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Chat activity.
 *
 * @author startic
 */
public class ChatActivity extends ListActivity {
    /**
     * Called when the activity is first created.
     */

    private EditText editor;
    private ImageButton sendButton;
    private TextView name;
    private String nick;
    private ArrayList<Message> chatList;
    private CustomAdapter adapter;
    private ListView lv;
    private int seqNumber;
    private String msg;
    private SharedPreferences prefs;
    private View row;
    private Context here;
    private int pos;


    //URL to get JSON Array
    private static String url = "http://172.16.100.73:8080/chat-kata/api/chat?seq=";
    //private static String url = "http: 172.16.100.73:8080/chat-kata/api/chat?seq=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        here = this;
        nick = getIntent().getExtras().getString("nick");
        prefs = getSharedPreferences("Chat", Context.MODE_PRIVATE);



        seqNumber = prefs.getInt("SEQ_" + nick, 0);


        name = (TextView) findViewById(R.id.nick);
        name.setText("Welcome " + nick + "!!!");
        editor = (EditText) findViewById(R.id.editTextMensaje);
        sendButton = (ImageButton) findViewById(R.id.sendButton);

        chatList =
                new ArrayList<Message>();

       /* chatList.add(
                new Message("Hey " + nick + ",", "Look the message that you haven't read yet!!"));*/


        adapter = new CustomAdapter(ChatActivity.this, chatList);
        refresh();
        lv = (ListView) this.getListView();


        url += seqNumber;


        Timer T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new getChat().execute();

                    }
                });
            }
        }, 1000, 1000);  //Timer execute GET per second


        //Event with the listView
            /*  lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                      chatList.remove(lv.getItemAtPosition(position));
                      refresh();
                         */
                     /*
                     Message message=(Message) lv.getItemAtPosition(position);
                      Toast.makeText(getApplicationContext(), message.getMensaje(), Toast.LENGTH_LONG).show();
                     */
        //Change background on click
                      /*
                      if (row != null) {
                          row.setBackgroundResource(R.color.Gris);
                      }
                      row = view;
                      view.setBackgroundResource(R.color.Rojo);




                  }
              });  */

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                pos=position;

                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        ChatActivity.this);

// Setting Dialog Title
                alertDialog2.setTitle("Confirm Delete...");

// Setting Dialog Message
                alertDialog2.setMessage("Are you sure you want delete this message?");

// Setting Icon to Dialog
                alertDialog2.setIcon(R.drawable.delete);

// Setting Positive "Yes" Btn
                alertDialog2.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chatList.remove(lv.getItemAtPosition(pos));
                                refresh();
                                Toast.makeText(getApplicationContext(),
                                        "You have deleted the message", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
// Setting Negative "NO" Btn
                alertDialog2.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getApplicationContext(),
                                        "You didn't delete anything", Toast.LENGTH_SHORT)
                                        .show();
                                dialog.cancel();
                            }
                        });

// Showing Alert Dialog
                alertDialog2.show();



                return true;
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = editor.getText().toString();
                if (isNotEmpty(msg)) {//lo envio si no no
                    //Message m = chatList.get(chatList.size() - 1);


                    new postChat().execute();


                    refresh();
                    // lv.setSelection(lv.getAdapter().getCount() - 1);
                }
                editor.setText("");
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        prefs.edit().putInt("SEQ_" + nick, seqNumber).commit();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Read values from the "savedInstanceState"-object and put them in your textview

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the values you need from your textview into "outState"-object
        super.onSaveInstanceState(outState);
    }

    public boolean isNotEmpty(String text) {
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) != ' ' && text.charAt(i) != '\n' && text.charAt(i) != '\t')
                return true;
        }
        return false;
    }

    public void refresh() {

        checkNick();//Made group of message of the same user

        lv = (ListView) this.getListView();
        setListAdapter((ListAdapter) adapter);
        lv.setSelection(lv.getAdapter().getCount() - 1);
    }

    private void checkNick() {

        int lenght = chatList.size();
        for (int i = 0; i < lenght - 1; i++) {
            if (lenght > 1 && chatList.get(i).getNombre().equals(chatList.get(i + 1).getNombre())) {
                String lastmessage = chatList.get(i + 1).getMensaje();
                chatList.get(i).setMensaje(chatList.get(i).getMensaje() + "\n" + lastmessage);
                chatList.remove(i + 1);
                //Recalculate the dimension of the array and the variable i for the next iteration
                i--;
                lenght = chatList.size();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
    }

    private class getChat extends AsyncTask<Void, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            boolean result = false;

            // Creating new JSON Parser
            JSONparser jParser = new JSONparser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                // Getting JSON Array(Messages) and Sequence number
                if (json == null)
                    return false;
                if (seqNumber != json.getInt("nextSeq")) {
                    result = true;
                } else
                    result = false;


                JSONArray arrayJS = json.getJSONArray("messages");

                if (arrayJS.length() != 0) {
                    for (int i = seqNumber; i < arrayJS.length(); i++) {

                        JSONObject c = arrayJS.getJSONObject(i);
                        int scr;
                        if (c.getString("nick").equals(nick))
                            scr = R.drawable.green_speech;
                        else
                            scr = R.drawable.transparente;


                        chatList.add(new Message(c.getString("nick"), c.getString("message"), scr));

                    }
                }
                seqNumber = json.getInt("nextSeq");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return result;
        }


        @Override
        protected void onPostExecute(Boolean result) {



            //If someone send a new message...
            if (result) {
                refresh();

                Vibrator v = (Vibrator) getSystemService(here.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                v.vibrate(500);
            }
        }

    }

    private class postChat extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Sending:" + msg, Toast.LENGTH_SHORT).show();

        }


        @Override
        protected Boolean doInBackground(Void... params) {

            boolean result = false;

            String status = JSONparser.sendJSON("http://172.16.100.73:8080/chat-kata/api/chat", new Message(nick, msg, R.drawable.flecha));
            //String status = JSONparser.sendJSON("http://172.16.100.73:8080/chat-kata/api/chat", new Message(nick, msg, R.drawable.flecha));

            if (status.equals("OK"))
                result = true;

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //Show status to the user

            if (result)
                Toast.makeText(getApplicationContext(), "Message has been sent", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Message couldn't been sent. Try again", Toast.LENGTH_SHORT).show();

        }
    }


}
