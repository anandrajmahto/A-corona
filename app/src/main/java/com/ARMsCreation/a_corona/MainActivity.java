package com.ARMsCreation.a_corona;

import android.annotation.SuppressLint;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ARMsCreation.a_corona.Pojo.Regional;
import com.ARMsCreation.a_corona.Pojo.Statewise;
import com.ARMsCreation.a_corona.Pojo.Summary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    APIInterface apiInterface;
    TextView totalactive, totalrecovered, totaldeath, totalconfirmed, lastUpdate;
    Button Refresh, location, recv, active, dead;
    boolean locationchk, recvchk, activechk, deadchk;
    ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int size;
    private ArrayList<String> state;
    private ArrayList<String> count;
    private ArrayList<String> recover;
    private ArrayList<String> death;
    private String[][] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Refresh = findViewById(R.id.Refresh);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        location = findViewById(R.id.location);
        active = findViewById(R.id.active);
        recv = findViewById(R.id.recv);
        dead = findViewById(R.id.dead);
        locationchk = false;
        recvchk = false;
        activechk = false;
        deadchk = false;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        totalactive = findViewById(R.id.totalactive);
        totalconfirmed = findViewById(R.id.totalconfirmed);
        totaldeath = findViewById(R.id.totaldeath);
        totalrecovered = findViewById(R.id.totalrecovered);
        lastUpdate = findViewById(R.id.lastUpdate);

        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                work();
            }
        });
        work();

    }

    private void work() {
        Call<Statewise> cal = apiInterface.datatwise();
        cal.enqueue(new Callback<Statewise>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Statewise> call, Response<Statewise> response) {
                //   Toast.makeText(MainActivity.this, "cal done pass", Toast.LENGTH_SHORT).show();
                //assert response.body() != null;
                Summary resource = response.body().getData().getSummary();
                //  Toast.makeText(MainActivity.this,data.getTotal().toString(), Toast.LENGTH_SHORT).show();
                String sDate1 = response.body().getLastRefreshed();
                Date date1 = null;
                String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
                try {
                    date1 = new SimpleDateFormat(pattern).parse(sDate1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //   lastUpdate.setText("Last Update-" + String.valueOf(date1));

                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat(" hh:mm a dd-MM-yyyy");
                String formattedDate = df.format(date1);

                lastUpdate.setText("Last Update-" + formattedDate);
                totalactive.setText(new DecimalFormat("##,##,###").format((resource.getTotal() - (resource.getDeaths() + resource.getDischarged()))));
                totalconfirmed.setText(new DecimalFormat("##,##,###").format(resource.getTotal()));
                totaldeath.setText(new DecimalFormat("##,##,###").format(resource.getDeaths()));
                totalrecovered.setText(new DecimalFormat("##,##,###").format(resource.getDischarged()));


                progressBar.setVisibility(View.INVISIBLE);


                List<Regional> resources = response.body().getData().getRegional();

                state = new ArrayList<String>(resources.size());
                count = new ArrayList<String>(resources.size());
                recover = new ArrayList<String>(resources.size());
                death = new ArrayList<String>(resources.size());
                size = resources.size();
                data = new String[size][4];
                int i = 0;
                for (Regional resourcess : resources) {
                    data[i][0] = String.valueOf(resourcess.getLoc());
                    data[i][1] = String.valueOf((resourcess.getTotalConfirmed() - (resourcess.getDeaths() + resourcess.getDischarged())));
                    data[i][2] = String.valueOf(resourcess.getDischarged());
                    data[i][3] = String.valueOf(resourcess.getDeaths());

                    i++;

                }
                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);

                check();
                Toast.makeText(getApplicationContext(), " Data Refreshed ", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<Statewise> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Turn On your Internet and click on refresh button", Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });
        /**


         Call<Data> call = apiInterface.doGetListResources();
         call.enqueue(new Callback<Data>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
         @Override public void onResponse(Call<Data> call, Response<Data> response) {
         Log.d("TAG", response.code() + "");
         assert response.body() != null;
         Summary resource = response.body().getSummary();

         //  totalactive.setText(new DecimalFormat("##,##,###").format((resource.getTotal() - (resource.getDeaths() + resource.getDischarged()))));
         totalconfirmed.setText(new DecimalFormat("##,##,###").format(resource.getTotal()));
         totaldeath.setText(new DecimalFormat("##,##,###").format(resource.getDeaths()));
         totalrecovered.setText(new DecimalFormat("##,##,###").format(resource.getDischarged()));


         List<Regional> resources = response.body().getRegional();

         state = new ArrayList<String>(resources.size());
         count = new ArrayList<String>(resources.size());
         recover = new ArrayList<String>(resources.size());
         death = new ArrayList<String>(resources.size());
         size = resources.size();
         data = new String[size][4];
         int i = 0;
         for (Regional resourcess : resources) {
         data[i][0] = String.valueOf(resourcess.getLoc());
         data[i][1] = String.valueOf((resourcess.getTotalConfirmed() - (resourcess.getDeaths()) + resourcess.getDischarged()));
         data[i][2] = String.valueOf(resourcess.getDischarged());
         data[i][3] = String.valueOf(resourcess.getDeaths());
         i++;
         }


         /**  for (int i = 0; i < size; i++) {
         // state.add(String.valueOf(resources.get(i).getState()));
         // count.add(String.valueOf(resources.get(i).getActive()));
         //  recover.add(String.valueOf(resources.get(i).getRecovered()));
         //  death.add(String.valueOf(resources.get(i).getDeaths()));
         data[i][0] = String.valueOf(resources.get(i).getState());
         data[i][1] = String.valueOf(resources.get(i).getActive());
         data[i][2] = String.valueOf(resources.get(i).getRecovered());
         data[i][3] = String.valueOf(resources.get(i).getDeaths());
         }

         progressBar.setVisibility(View.INVISIBLE);
         mAdapter = new MyRecyclerViewAdapter(getDataSet());
         mRecyclerView.setAdapter(mAdapter);

         check();

         // System.out.println("Format dateTime => " + formattedDate);

         Toast.makeText(getApplicationContext(), " Data Refreshed ", Toast.LENGTH_LONG).show();

         }

         @Override public void onFailure(Call<Data> call, Throwable t) {
         Toast.makeText(getApplicationContext(), "Turn On your Internet and click on refresh button", Toast.LENGTH_LONG).show();
         call.cancel();

         }
         });
         **/
    }

    private void check() {
        location.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String temp;
                for (int i = 0; i < size; i++) {
                    for (int j = i + 1; j < size; j++) {

                        if (locationchk) {


                            if (data[j][0].compareTo(data[i][0]) > 0) {
                                temp = data[i][1];
                                data[i][1] = data[j][1];
                                data[j][1] = temp;

                                temp = data[i][0];
                                data[i][0] = data[j][0];
                                data[j][0] = temp;

                                temp = data[i][2];
                                data[i][2] = data[j][2];
                                data[j][2] = temp;

                                temp = data[i][3];
                                data[i][3] = data[j][3];
                                data[j][3] = temp;
                            }
                        } else {
                            if (data[i][0].compareTo(data[j][0]) > 0) {
                                temp = data[i][1];
                                data[i][1] = data[j][1];
                                data[j][1] = temp;

                                temp = data[i][0];
                                data[i][0] = data[j][0];
                                data[j][0] = temp;

                                temp = data[i][2];
                                data[i][2] = data[j][2];
                                data[j][2] = temp;

                                temp = data[i][3];
                                data[i][3] = data[j][3];
                                data[j][3] = temp;
                            }
                        }

                    }
                }

                locationchk = !locationchk;
                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);
            }

        });


        active.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String temp;
                for (int i = 0; i < size; i++) {
                    for (int j = i + 1; j < size; j++) {

                        if (activechk) {
                            if (Integer.parseInt(data[i][1]) > Integer.parseInt(data[j][1])) {
                                temp = data[i][1];
                                data[i][1] = data[j][1];
                                data[j][1] = temp;

                                temp = data[i][0];
                                data[i][0] = data[j][0];
                                data[j][0] = temp;

                                temp = data[i][2];
                                data[i][2] = data[j][2];
                                data[j][2] = temp;

                                temp = data[i][3];
                                data[i][3] = data[j][3];
                                data[j][3] = temp;
                            }
                        } else {
                            if (Integer.parseInt(data[i][1]) < Integer.parseInt(data[j][1])) {
                                temp = data[i][1];
                                data[i][1] = data[j][1];
                                data[j][1] = temp;

                                temp = data[i][0];
                                data[i][0] = data[j][0];
                                data[j][0] = temp;

                                temp = data[i][2];
                                data[i][2] = data[j][2];
                                data[j][2] = temp;

                                temp = data[i][3];
                                data[i][3] = data[j][3];
                                data[j][3] = temp;
                            }
                        }

                    }
                }
                activechk = !activechk;
                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);
            }

        });
        recv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String temp;
                for (int i = 0; i < size; i++) {
                    for (int j = i + 1; j < size; j++) {

                        if (recvchk) {
                            if (Integer.parseInt(data[i][2]) > Integer.parseInt(data[j][2])) {
                                temp = data[i][1];
                                data[i][1] = data[j][1];
                                data[j][1] = temp;

                                temp = data[i][0];
                                data[i][0] = data[j][0];
                                data[j][0] = temp;

                                temp = data[i][2];
                                data[i][2] = data[j][2];
                                data[j][2] = temp;

                                temp = data[i][3];
                                data[i][3] = data[j][3];
                                data[j][3] = temp;
                            }
                        } else {
                            if (Integer.parseInt(data[i][2]) < Integer.parseInt(data[j][2])) {
                                temp = data[i][1];
                                data[i][1] = data[j][1];
                                data[j][1] = temp;

                                temp = data[i][0];
                                data[i][0] = data[j][0];
                                data[j][0] = temp;

                                temp = data[i][2];
                                data[i][2] = data[j][2];
                                data[j][2] = temp;

                                temp = data[i][3];
                                data[i][3] = data[j][3];
                                data[j][3] = temp;
                            }
                        }

                    }
                }
                recvchk = !recvchk;
                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);
            }

        });
        dead.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String temp;
                for (int i = 0; i < size; i++) {
                    for (int j = i + 1; j < size; j++) {

                        if (deadchk) {
                            if (Integer.parseInt(data[i][3]) > Integer.parseInt(data[j][3])) {
                                temp = data[i][1];
                                data[i][1] = data[j][1];
                                data[j][1] = temp;

                                temp = data[i][0];
                                data[i][0] = data[j][0];
                                data[j][0] = temp;

                                temp = data[i][2];
                                data[i][2] = data[j][2];
                                data[j][2] = temp;

                                temp = data[i][3];
                                data[i][3] = data[j][3];
                                data[j][3] = temp;
                            }
                        } else {
                            if (Integer.parseInt(data[i][3]) < Integer.parseInt(data[j][3])) {
                                temp = data[i][1];
                                data[i][1] = data[j][1];
                                data[j][1] = temp;

                                temp = data[i][0];
                                data[i][0] = data[j][0];
                                data[j][0] = temp;

                                temp = data[i][2];
                                data[i][2] = data[j][2];
                                data[j][2] = temp;

                                temp = data[i][3];
                                data[i][3] = data[j][3];
                                data[j][3] = temp;
                            }
                        }

                    }
                }
                deadchk = !deadchk;
                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);
            }

        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<DataObject> getDataSet() {
        ArrayList<DataObject> results = new ArrayList<DataObject>();
        // DataObject obja = new DataObject("Location", "Active", "Recv", "Dead");
        // results.add(0, obja);
        for (int index = 0; index < size; index++) {
            //results.clear();
            //   DataObject obj = new DataObject(state.get(index), count.get(index), recover.get(index), death.get(index));
            //new DecimalFormat("##,##,###").format(amount);
            DataObject obj = new DataObject(data[index][0], new DecimalFormat("##,##,###").format(Integer.parseInt(data[index][1])), new DecimalFormat("##,##,###").format(Integer.parseInt(data[index][2])), new DecimalFormat("##,##,###").format(Integer.parseInt(data[index][3])));
            results.add(index, obj);
        }
        return results;
    }

}

/**
 * Log.d("TAG", response.code() + "");
 * String displayResponse = "";
 * assert response.body() != null;
 * Summary resource = response.body().getData().getSummary();
 * totalactive.setText(String.valueOf(resource.getTotal()-(resource.getDischarged()+resource.getDeaths())));
 * totalconfirmed.setText(String.valueOf(resource.getTotal()));
 * totaldeath.setText(String.valueOf(resource.getDeaths()));
 * totalrecovered.setText(String.valueOf(resource.getDischarged()));
 **/