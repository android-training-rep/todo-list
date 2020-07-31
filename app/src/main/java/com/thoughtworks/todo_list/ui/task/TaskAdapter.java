package com.thoughtworks.todo_list.ui.task;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ItemViewHolder> {
    private List<Task> tasks;
    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ItemViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.title.setText(currentTask.getTitle());
        holder.deadline.setText(currentTask.getDeadline());
        holder.check.setChecked(currentTask.isDeleted());
        if (currentTask.isDeleted()) {
            holder.title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.deadline.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
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
