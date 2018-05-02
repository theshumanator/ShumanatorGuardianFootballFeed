package com.example.fatoumeh.shumanatorguardianfootballfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by fatoumeh on 01/03/2018.
 */

public class FootballFeedAdapter extends ArrayAdapter<FootballFeed> {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public FootballFeedAdapter(@NonNull Context context, @NonNull List<FootballFeed> footballNews) {
        super(context, 0, footballNews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View footballView = convertView;
        if (footballView==null) {
            footballView = LayoutInflater.from(getContext()).inflate(
                    R.layout.football_list_layout, parent, false);
        }
        FootballFeed currentFeed = getItem(position);

        TextView tvSection = footballView.findViewById(R.id.section_text_view);
        tvSection.setText(currentFeed.getSection());

        TextView tvHeadline = footballView.findViewById(R.id.headline_text_view);
        tvHeadline.setText(currentFeed.getHeadline());

        TextView tvAuthor = footballView.findViewById(R.id.author_text_view);
        tvAuthor.setText(currentFeed.getAuthor());

        //reformat the date to date and time and store them separately
        String [] formattedDateTime=getNewDate(currentFeed.getDate());
        String formattedDate=formattedDateTime[0];
        String formattedTime=formattedDateTime[1];

        TextView tvDate = footballView.findViewById(R.id.date_text_view);
        tvDate.setText(formattedDate);

        TextView tvTime = footballView.findViewById(R.id.time_text_view);
        tvTime.setText(formattedTime);
        return footballView;
    }

    private String[] getNewDate(String date) {
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-MMM-yyy HH:mm");
        Date dateObj = null;
        try {
            dateObj = currentDateFormat.parse(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing date " + date, e);
        }
        String newDateTime = newDateFormat.format(dateObj);
        String [] dateArray=newDateTime.split(" ");
        return dateArray;
    }
}

