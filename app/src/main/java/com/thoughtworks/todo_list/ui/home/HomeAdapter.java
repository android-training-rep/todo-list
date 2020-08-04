package com.thoughtworks.todo_list.ui.home;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;
import java.util.Objects;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemViewHolder> {
    private static final String TAG = "TaskAdapter";
    private List<Task> tasks;
    private HomeViewModel viewModel;

    public HomeAdapter(HomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public HomeAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ItemViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.title.setText(currentTask.getTitle());
        holder.deadline.setText(currentTask.getDeadline());
        holder.check.setChecked(currentTask.isCompleted());
        Log.d(TAG, ":" + currentTask.getTitle() + currentTask.isCompleted());

        if (currentTask.isCompleted()) {
            holder.title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.deadline.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                currentTask.setCompleted(!currentTask.isCompleted());
                viewModel.setUpdateTask(currentTask);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setToDetail(currentTask);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (Objects.nonNull(tasks)) return tasks.size();
        return 0;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView title, deadline;
        public CheckBox check;
        public ItemViewHolder(@NonNull View v) {
            super(v);
            itemView = v;
            title = itemView.findViewById(R.id.item_title);
            deadline = itemView.findViewById(R.id.item_deadline);
            check = itemView.findViewById(R.id.item_check);
        }
    }
}
