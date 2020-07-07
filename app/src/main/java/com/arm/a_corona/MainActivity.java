package com.arm.a_corona;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arm.a_corona.Pojo.MultipleResource;
import com.arm.a_corona.Pojo.Statewise;
import com.arm.a_corona.Pojo.Total;

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
    Button Refresh;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int size;
    private ArrayList<String> state;
    private ArrayList<String> count;
    private ArrayList<String> recover;
    private ArrayList<String> death;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Refresh = findViewById(R.id.Refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                work();
                //Do something after delayed time
            }
        }, 1000 * 10 * 60);


    }

    private void work() {

        Call<MultipleResource> call = apiInterface.doGetListResources();
        call.enqueue(new Callback<MultipleResource>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                Log.d("TAG", response.code() + "");
                assert response.body() != null;
                Total resource = response.body().getData().getTotal();


                totalactive.setText(String.valueOf(resource.getActive()));
                totalconfirmed.setText(String.valueOf(resource.getConfirmed()));
                totaldeath.setText(String.valueOf(resource.getDeaths()));
                totalrecovered.setText(String.valueOf(resource.getRecovered()));


                List<Statewise> resources = response.body().getData().getStatewise();
                state = new ArrayList<String>(resources.size());
                count = new ArrayList<String>(resources.size());
                recover = new ArrayList<String>(resources.size());
                death = new ArrayList<String>(resources.size());
                size = resources.size();
                for (int i = 0; i < resources.size(); i++) {
                    state.add(String.valueOf(resources.get(i).getState()));
                    count.add(String.valueOf(resources.get(i).getActive()));
                    recover.add(String.valueOf(resources.get(i).getRecovered()));
                    death.add(String.valueOf(resources.get(i).getDeaths()));
                }

                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);

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

                SimpleDateFormat df = new SimpleDateFormat(" HH:mm:ss a dd-MM-yyyy");
                String formattedDate = df.format(date1);

                lastUpdate.setText("Last Update-" + formattedDate);
                // System.out.println("Format dateTime => " + formattedDate);

                Toast.makeText(getApplicationContext(), " Data Refreshed ", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Turn On your Internet and click on refresh button", Toast.LENGTH_LONG).show();
                call.cancel();

            }
        });


    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        DataObject obja = new DataObject("Location", "Active", "Recv", "Dead");
        results.add(0, obja);
        for (int index = 0; index < size; index++) {
            DataObject obj = new DataObject(state.get(index), count.get(index), recover.get(index), death.get(index));
            results.add(index + 1, obj);
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