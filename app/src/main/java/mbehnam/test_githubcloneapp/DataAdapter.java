package mbehnam.test_githubcloneapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MBehnam on 4/24/2018.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private ArrayList<Repositpries> Repos;

    public DataAdapter(ArrayList<Repositpries> android) {
        Repos = android;
    }

    public DataAdapter(Repositories_Search android) {
        Repos = android.getItems();
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.tv_name.setText((i+1)+": "+Repos.get(i).getFull_name());
        viewHolder.tv_Description.setText(Repos.get(i).getDescription());
    }

    @Override
    public int getItemCount() {
        return Repos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name,tv_Description;
        public ViewHolder(View view) {
            super(view);

            tv_name = (TextView)view.findViewById(R.id.tv_name);
            tv_Description = (TextView)view.findViewById(R.id.tv_Description);


        }
    }

}