package com.main.personalfinances.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.personalfinances.R;
import com.main.personalfinances.data.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }


    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.editCategory.setText(expense.getCategory().toString());
        holder.editPrice.setText(String.valueOf(expense.getPrice()));
        holder.editDescription.setText(expense.getDescription());
        holder.editDueDate.setText(expense.getDueDate().toString());
        holder.editPurchaseDate.setText(expense.getDateAdded().toString());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {

        EditText editCategory;
        EditText editPrice;
        EditText editDescription;
        EditText editDueDate;
        EditText editPurchaseDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            editCategory = itemView.findViewById(R.id.editCategory);
            editPrice = itemView.findViewById(R.id.editPrice);
            editDescription = itemView.findViewById(R.id.editDescription);
            editDueDate = itemView.findViewById(R.id.editDueDate);
            editPurchaseDate = itemView.findViewById(R.id.editPurchaseDate);
        }
    }
}
