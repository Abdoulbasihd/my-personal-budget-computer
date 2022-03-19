package cm.abimmobiledev.mybudgetizer.ui.expense.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<Expense> expenses;

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View expenseElementView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);

        return new ExpenseViewHolder(expenseElementView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        holder.expenseReason.setText(expense.getReason());
        holder.expenseTitle.setText(expense.getEntitled());
        holder.expenseAmount.setText(String.valueOf(expense.getAmount()));
        holder.expenseDate.setText(expense.getDateTimeOfExpense());

        holder.expenseCard.setOnClickListener(v -> Toast.makeText(holder.expenseCard.getContext(), "Nothing implemented there for the moment.... E-mail us for suggestions @ abdulbasihd@gmail.com. Thanks", Toast.LENGTH_LONG).show());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        final TextView expenseTitle;
        final TextView expenseAmount;
        final TextView expenseDate;
        final TextView expenseReason;
        final CardView expenseCard;

        public ExpenseViewHolder(@NonNull View expenseItemView) {
            super(expenseItemView);

            expenseTitle = expenseItemView.findViewById(R.id.expense_title);
            expenseAmount = expenseItemView.findViewById(R.id.amount_expended);
            expenseDate = expenseItemView.findViewById(R.id.expense_date);
            expenseReason = expenseItemView.findViewById(R.id.expense_reason);

            expenseCard = expenseItemView.findViewById(R.id.expense_card);

        }

    }
}
