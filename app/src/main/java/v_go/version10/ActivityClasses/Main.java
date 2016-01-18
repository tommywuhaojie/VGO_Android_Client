package v_go.version10.ActivityClasses;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import v_go.version10.FragmentClasses.TabA_1;
import v_go.version10.FragmentClasses.TabB_1;
import v_go.version10.FragmentClasses.TabC_1;
import v_go.version10.FragmentClasses.TabD_1;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;
import v_go.version10.HelperClasses.BackgroundService;

public class Main extends AppCompatActivity{

    /* Your Tab host */
    private TabHost mTabHost;
    /* A HashMap of stacks, where we use tab identifier as keys..*/
    private HashMap<String, Stack<Fragment>> mStacks;
    /*Save current tabs identifier in this..*/
    private String mCurrentTab;
    // boolean var for switching tab delay
    private boolean allow = true;
    // local request trip id array
    private ArrayList<Integer> requestTripIdList;
    // local request id array
    private ArrayList<Integer> requestIdList;
    // local senders name array
    private ArrayList<String> senderNameList;
    // local notification type & result list
    private ArrayList<Integer> notifTypeList;

    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // lists initialization
        requestTripIdList = new ArrayList<>();
        requestIdList = new ArrayList<>();
        senderNameList = new ArrayList<>();
        notifTypeList = new ArrayList<>();

        /**-------- Background Services and UI Updating for message and request notifications -----------------**/

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("new_request")) {

                    // prepare new notification toast
                    int numOfNewReq = intent.getIntExtra("num_of_new_req", 0);
                    String notifToast = "You have " + numOfNewReq + " new notification";
                    if(numOfNewReq == 1){
                        notifToast += "!";
                    }else{
                        notifToast += "s!";
                    }
                    // make toast
                    Toast toast = Toast.makeText(getApplicationContext(), notifToast, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
                    toast.show();

                    // if not currently not in 3rd tab change third tab icon
                    if(mTabHost.getCurrentTab() != 2) {
                        ImageView mImageView = (ImageView) mTabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.icon);
                        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.tab3_b_no));
                        Global.TAB3_NOTIFICATION = true;
                    }

                    // set local request trip list & req id list
                    setLocalRequestTripIdList(intent.getIntArrayExtra("trip_id_array"));
                    setRequestIdList(intent.getIntArrayExtra("req_id_array"));

                    // if at calendar page update "bell icon"
                    if(getCurrentFragment() instanceof TabC_1) {
                        ((TabC_1)getCurrentFragment()).updateUi();
                    }

                    // store & setup the contents of all notifications
                    addSenderNameToList(intent.getStringArrayExtra("sender_name_array"));
                    addNotifTypeToList(intent.getIntArrayExtra("notif_type_array"));

                }


            }
        };


        /**---------------------------------------------------------------------------------------------------------------**/
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        /*
         *  Navigation stacks for each tab gets created..
         *  tab identifier is used as key to get respective stack for each tab
         */
        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put(Global.TAB_A, new Stack<Fragment>());
        mStacks.put(Global.TAB_B, new Stack<Fragment>());
        mStacks.put(Global.TAB_C, new Stack<Fragment>());
        mStacks.put(Global.TAB_D, new Stack<Fragment>());

        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setOnTabChangedListener(listener);
        mTabHost.setup();
        // remove the tab dividers
        mTabHost.getTabWidget().setDividerDrawable(null);

        // init tabs
        initializeTabs();

        // prevent dialogs from closing by outside click
        setFinishOnTouchOutside(false);
    }

    private void setLocalRequestTripIdList(int[] array){
        for(int i : array){
            requestTripIdList.add(i);
        }
    }
    private void setRequestIdList(int[] array){
        for(int i : array){
            requestIdList.add(i);
        }
    }
    public ArrayList<Integer> getAllReqList(){
        return requestIdList;
    }
    public boolean isMatched(int trip_id){
        for(int i : requestTripIdList){
            if(trip_id == i){
                return true;
            }
        }
        return  false;
    }

    public ArrayList<String> getAllSenderName(){
        return senderNameList;
    }
    private void addSenderNameToList(String[] nameList){
        Collections.addAll(senderNameList, nameList);
    }
    public ArrayList<Integer> getALLNotificationType(){
        return notifTypeList;
    }
    private void addNotifTypeToList(int[] typeArray){
        for(int i : typeArray){
            notifTypeList.add(i);
        }
    }

    public void initializeTabs(){
        /* Setup your tab icons and content views.. Nothing special in this..*/
        // tab1
        TabHost.TabSpec spec    =   mTabHost.newTabSpec(Global.TAB_A);
        mTabHost.setCurrentTab(0);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab1));
        mTabHost.addTab(spec);

        // tab2
        spec = mTabHost.newTabSpec(Global.TAB_B);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab2));
        mTabHost.addTab(spec);

        // tab3
        spec = mTabHost.newTabSpec(Global.TAB_C);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab3_b));
        mTabHost.addTab(spec);

        // tab4
        spec = mTabHost.newTabSpec(Global.TAB_D);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab4));
        mTabHost.addTab(spec);

        // color
        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#cccccc"));
        }
        mTabHost.getTabWidget().setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#a6a6a6"));
    }

    /*Comes here when user switch tab, or we do programmatically*/
    TabHost.OnTabChangeListener listener    =   new TabHost.OnTabChangeListener() {
        public void onTabChanged(String tabId) {

            if(!allow){
                mTabHost.setCurrentTabByTag(mCurrentTab);
                return;
            }
            allow = false;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 0.3 seconds
                    allow = true;
                }
            }, 300);

            /*Set current tab..*/
            mCurrentTab = tabId;

            if(mStacks.get(tabId).size() == 0){
          /*
           *    First time this tab is selected. So add first fragment of that tab.
           *    Dont need animation, so that argument is false.
           *    We are adding a new fragment which is not present in stack. So add to stack is true.
           */
                if(tabId.equals(Global.TAB_A)){
                    pushFragments(tabId, new TabA_1(), false,true);
                }else if(tabId.equals(Global.TAB_B)){
                    pushFragments(tabId, new TabB_1(), false,true);
                }else if(tabId.equals(Global.TAB_C)){
                    pushFragments(tabId, new TabC_1(), false,true);
                }else if(tabId.equals(Global.TAB_D)){
                    pushFragments(tabId, new TabD_1(), false,true);
                }

            }else {
          /*
           *    We are switching tabs, and target tab is already has at least one fragment.
           *    No need of animation, no need of stack pushing. Just show the target fragment
           */
                pushFragments(tabId, mStacks.get(tabId).lastElement(), false,false);
            }

            // change the color of tabs
            for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#cccccc"));
            }
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#a6a6a6"));

            // if there is a red dot, dismiss it after switch to 3rd tab
            if(tabId.equals(Global.TAB_C) && Global.TAB3_NOTIFICATION){
                if(Global.TAB3_NOTIFICATION){
                    ImageView mImageView = (ImageView) mTabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.icon);
                    mImageView.setImageDrawable(getResources().getDrawable(R.drawable.tab3_b));
                    Global.TAB3_NOTIFICATION = false;
                }
            }
        }
    };

    /*
     *      To add fragment to a tab.
     *  tag             ->  Tab identifier
     *  fragment        ->  Fragment to show, in tab identified by tag
     *  shouldAnimate   ->  should animate transaction. false when we switch tabs, or adding first fragment to a tab
     *                      true when when we are pushing more fragment into navigation stack.
     *  shouldAdd       ->  Should add to fragment navigation stack (mStacks.get(tag)). false when we are switching tabs (except for the first time)
     *                      true in all other cases.
     */
    public void pushFragments(String tag, Fragment fragment,boolean shouldAnimate, boolean shouldAdd){
        if(shouldAdd) {
            mStacks.get(tag).push(fragment);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if(shouldAnimate) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }

    public void pushFragmentsWithUpDownAnim(String tag, Fragment fragment, boolean shouldAdd){
        if(shouldAdd) {
            mStacks.get(tag).push(fragment);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);

        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }



    public void popFragments(){
      /*
       *    Select the second last fragment in current tab's stack..
       *    which will be shown after the fragment transaction given below
       */
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 2);

      /*pop current fragment from stack.. */
        mStacks.get(mCurrentTab).pop();
        //clear global variable
        cleanGlobal();

      /* We have the target fragment in hand.. Just show it.. Show a standard navigation animation*/
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        // different animation for different page
        if((mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 1)) instanceof TabC_1) {
            ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom);
        }else {
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }

    public void popFragments(int n){
      /*
       *    Select the second last fragment in current tab's stack..
       *    which will be shown after the fragment transaction given below
       */
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - (n+1));

        for(int i=0; i<n; i++) { // pop n times
            /*pop current fragment from stack.. */
            mStacks.get(mCurrentTab).pop();
        }
        //clear global variable
        cleanGlobal();

      /* We have the target fragment in hand.. Just show it.. Show a standard navigation animation*/
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }

    public Fragment getCurrentFragment(){
        Fragment currentFragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 1);
        return  currentFragment;
    }


    @Override
    public void onBackPressed() {
        // We are already showing first fragment of current tab, so when back pressed, we will finish this activity..
        if(mStacks.get(mCurrentTab).size() == 1){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            moveTaskToBack(true);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            dialog.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Back to home screen?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return;
        }
        /* Goto previous fragment in navigation stack of this tab */
        popFragments();
    }

    /* Might be useful if we want to switch tab programmatically, from inside any of the fragment.*/
    public void setCurrentTab(int val){
        mTabHost.setCurrentTab(val);
    }

    //enable/disable back button from fragment
    public void enableBackButton(Boolean b){
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);
        getSupportActionBar().setDisplayShowHomeEnabled(b);
    }
    // clean global variable for some fragments
    private void cleanGlobal(){
        if(mCurrentTab.matches(Global.TAB_A)){
            if(mStacks.get(mCurrentTab).size() == 1){
                Global.DATE_TIME = "";
                Global.ALLOW_MUL_PASSEN = 0;
                Global.SELECTED_TYPE = 0;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public TabWidget getTabWidget(){
        return mTabHost.getTabWidget();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(getBaseContext(), BackgroundService.class));

        Log.d("DEBUG", "pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(getBaseContext(), BackgroundService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction("new_request");
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, filter);

        Log.d("DEBUG", "resume");
    }

}
